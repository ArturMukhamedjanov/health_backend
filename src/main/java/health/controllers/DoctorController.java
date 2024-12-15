package health.controllers;

import health.auth.services.AuthenticationService;
import health.models.Chat;
import health.models.Doctor;
import health.models.Message;
import health.models.auth.Role;
import health.models.dto.*;
import health.models.mapper.*;
import health.services.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@RestController
@RequestMapping("/doctor")
public class DoctorController {

    private final AuthenticationService authenticationService;
    private final AppointmentService appointmentService;
    private final AppointmentMapper appointmentMapper;
    private final TimetableService timetableService;
    private final TimetableMapper timetableMapper;
    private final DoctorService doctorService;
    private final DoctorMapper doctorMapper;
    private final ChatService chatService;
    private final ChatMapper chatMapper;
    private final MessageService messageService;
    private final MessageMapper messageMapper;
    private final CustomerService customerService;

    // Получение информации о докторе для текущего пользователя
    @GetMapping()
    public ResponseEntity<DoctorDto> getDoctor() {
        var currentUser = authenticationService.getCurrentUser();
        var doctor = doctorService.getDoctorByUser(currentUser);
        if (doctor.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var doctorDto = doctorMapper.mapToDto(doctor.get());
        doctorDto.toBuilder().email(currentUser.getEmail());
        return ResponseEntity.ok(doctorDto);
    }

    // Обновление информации о докторе
    @PostMapping
    public ResponseEntity<DoctorDto> updateDoctorInfo(@Valid @RequestBody DoctorDto doctorDto) {
        var currentUser = authenticationService.getCurrentUser();
        var currentDoctor = doctorService.getDoctorByUser(currentUser);
        if (currentDoctor.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var updatedDoctor = mergeDoctors(currentDoctor.get(), doctorDto);
        updatedDoctor = doctorService.updateDoctor(updatedDoctor);
        var res = doctorMapper.mapToDto(updatedDoctor);
        res.toBuilder().email(currentUser.getEmail());
        return ResponseEntity.ok(res);
    }

    @GetMapping("/appointment")
    public ResponseEntity<List<AppointmentDto>> getAppointments() {
        var currentUser = authenticationService.getCurrentUser();
        var doctor = doctorService.getDoctorByUser(currentUser);
        if (doctor.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var appointments = appointmentService.getAppointmentsByDoctor(doctor.get());
        var appointmentDtos = appointments.stream().map(appointmentMapper::mapToDto).collect(Collectors.toList());
        return ResponseEntity.ok(appointmentDtos);
    }

    @GetMapping("/timetable")
    public ResponseEntity<List<TimetableDto>> getTimetable() {
        var currentUser = authenticationService.getCurrentUser();
        var doctor = doctorService.getDoctorByUser(currentUser);
        if (doctor.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var timetable = timetableService.getTimetablesByDoctor(doctor.get());
        var timetableDtos = timetable.stream().map(timetableMapper::mapToDto).collect(Collectors.toList());
        return ResponseEntity.ok(timetableDtos);
    }

    @GetMapping("/chat")
    public ResponseEntity<List<ChatDto>> getChats() {
        var currentUser = authenticationService.getCurrentUser();
        var doctor = doctorService.getDoctorByUser(currentUser);
        if (doctor.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        List<Chat> chats = chatService.getChatsByDoctor(doctor.get());
        return ResponseEntity.ok(chats.stream().map(chatMapper::mapToDto).toList());
    }

    @GetMapping("/chat/{chatId}/message")
    public ResponseEntity<List<MessageDto>> getChatMessages(@PathVariable Long chatId) {
        var currentUser = authenticationService.getCurrentUser();
        var doctor = doctorService.getDoctorByUser(currentUser);
        if (doctor.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var chatOpt = chatService.getChatById(chatId);
        if (chatOpt.isEmpty() || chatOpt.get().getDoctor().getId() != doctor.get().getId()) {
            return ResponseEntity.notFound().build();
        }
        List<Message> messages = messageService.getMessagesByChat(chatOpt.get());
        return ResponseEntity.ok(messages.stream().map(messageMapper::mapToDto).toList());
    }

    @PostMapping("/chat/{customerId}")
    public ResponseEntity<ChatDto> createChat(@PathVariable Long customerId) {
        var currentUser = authenticationService.getCurrentUser();
        var doctor = doctorService.getDoctorByUser(currentUser);
        if (doctor.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var customerOpt = customerService.getCustomerById(customerId);
        if (customerOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var appointmentOpt = appointmentService.getAppointmentsByDoctorAndCustomer(doctor.get(), customerOpt.get());
        if (appointmentOpt.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        var chatOpt = chatService.getChatByDoctorAndCustomer(doctor.get(), customerOpt.get());
        if (chatOpt.isPresent()) {
            return ResponseEntity.ok(chatMapper.mapToDto(chatOpt.get()));
        }
        var chat = Chat.builder()
                .customer(customerOpt.get())
                .doctor(doctor.get())
                .clinic(doctor.get().getClinic())
                .build();
        chat = chatService.saveOrUpdateChat(chat);
        return ResponseEntity.ok(chatMapper.mapToDto(chat));
    }

    @PostMapping("/chat/{chatId}/message")
    public ResponseEntity<List<MessageDto>> createMessage(@PathVariable Long chatId, @RequestBody MessageDto messageDto) {
        var currentUser = authenticationService.getCurrentUser();
        var doctor = doctorService.getDoctorByUser(currentUser);
        if (doctor.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var chatOpt = chatService.getChatById(chatId);
        if (chatOpt.isEmpty() || chatOpt.get().getDoctor().getId() != doctor.get().getId()) {
            return ResponseEntity.notFound().build();
        }
        if (messageDto.text() == null || messageDto.text().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        var message = Message.builder()
                .chat(chatOpt.get())
                .role(Role.DOCTOR)
                .text(messageDto.text())
                .sendTime(Instant.now())
                .build();
        message = messageService.saveOrUpdateMessage(message);
        var messages = messageService.getMessagesByChat(chatOpt.get());
        return ResponseEntity.ok(messages.stream().map(messageMapper::mapToDto).toList());
    }

    public Doctor mergeDoctors(Doctor oldDoctor, DoctorDto newDoctor) {
        if (newDoctor.firstName() != null) {
            oldDoctor.setFirstName(newDoctor.firstName());
        }
        if (newDoctor.lastName() != null) {
            oldDoctor.setLastName(newDoctor.lastName());
        }
        if (newDoctor.speciality() != null) {
            oldDoctor.setSpeciality(newDoctor.speciality());
        }
        return oldDoctor;
    }
}
