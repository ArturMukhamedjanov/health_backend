package health.controllers;

import health.auth.services.AuthenticationService;
import health.models.Chat;
import health.models.Message;
import health.models.auth.Role;
import health.models.dto.*;
import health.models.mapper.*;
import health.services.*;
import health.utils.ChatMessageUtil;
import health.utils.EntityMergeUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/doctor")
public class DoctorController extends BaseController {

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

    public DoctorController(
            AuthenticationService authenticationService,
            AppointmentService appointmentService,
            AppointmentMapper appointmentMapper,
            TimetableService timetableService,
            TimetableMapper timetableMapper,
            DoctorService doctorService,
            DoctorMapper doctorMapper,
            ChatService chatService,
            ChatMapper chatMapper,
            MessageService messageService,
            MessageMapper messageMapper,
            CustomerService customerService) {
        super(authenticationService);
        this.appointmentService = appointmentService;
        this.appointmentMapper = appointmentMapper;
        this.timetableService = timetableService;
        this.timetableMapper = timetableMapper;
        this.doctorService = doctorService;
        this.doctorMapper = doctorMapper;
        this.chatService = chatService;
        this.chatMapper = chatMapper;
        this.messageService = messageService;
        this.messageMapper = messageMapper;
        this.customerService = customerService;
    }

    @GetMapping()
    public ResponseEntity<DoctorDto> getDoctor() {
        return withUserEntity(
                doctorService::getDoctorByUser,
                doctor -> {
                    var doctorDto = doctorMapper.mapToDto(doctor);
                    doctorDto.toBuilder().email(getCurrentUser().getEmail());
                    return ResponseEntity.ok(doctorDto);
                }
        );
    }

    @PostMapping
    public ResponseEntity<DoctorDto> updateDoctorInfo(@Valid @RequestBody DoctorDto doctorDto) {
        return withUserEntity(
                doctorService::getDoctorByUser,
                doctor -> {
                    var updatedDoctor = EntityMergeUtil.mergeDoctor(doctor, doctorDto);
                    updatedDoctor = doctorService.updateDoctor(updatedDoctor);
                    var res = doctorMapper.mapToDto(updatedDoctor);
                    res.toBuilder().email(getCurrentUser().getEmail());
                    return ResponseEntity.ok(res);
                }
        );
    }

    @GetMapping("/appointment")
    public ResponseEntity<List<AppointmentDto>> getAppointments() {
        return withUserEntity(
                doctorService::getDoctorByUser,
                doctor -> {
                    var appointments = appointmentService.getAppointmentsByDoctor(doctor);
                    var appointmentDtos = appointments.stream().map(appointmentMapper::mapToDto).collect(Collectors.toList());
                    return ResponseEntity.ok(appointmentDtos);
                }
        );
    }

    @GetMapping("/timetable")
    public ResponseEntity<List<TimetableDto>> getTimetable() {
        return withUserEntity(
                doctorService::getDoctorByUser,
                doctor -> {
                    var timetable = timetableService.getTimetablesByDoctor(doctor);
                    var timetableDtos = timetable.stream().map(timetableMapper::mapToDto).collect(Collectors.toList());
                    return ResponseEntity.ok(timetableDtos);
                }
        );
    }

    @GetMapping("/chat")
    public ResponseEntity<List<ChatDto>> getChats() {
        return withUserEntity(
                doctorService::getDoctorByUser,
                doctor -> {
                    List<Chat> chats = chatService.getChatsByDoctor(doctor);
                    return ResponseEntity.ok(chats.stream().map(chatMapper::mapToDto).toList());
                }
        );
    }

    @GetMapping("/chat/{chatId}/message")
    public ResponseEntity<List<MessageDto>> getChatMessages(@PathVariable Long chatId) {
        return withUserEntity(
                doctorService::getDoctorByUser,
                doctor -> withEntity(
                        chatService::getChatById,
                        chatId,
                        chat -> {
                            if (!belongsTo(chat.getDoctor().getId(), doctor.getId())) {
                                return notFound();
                            }
                            List<Message> messages = messageService.getMessagesByChat(chat);
                            return ResponseEntity.ok(messages.stream().map(messageMapper::mapToDto).toList());
                        }
                )
        );
    }

    @PostMapping("/chat/{customerId}")
    public ResponseEntity<ChatDto> createChat(@PathVariable Long customerId) {
        return withUserEntity(
                doctorService::getDoctorByUser,
                doctor -> withEntity(
                        customerService::getCustomerById,
                        customerId,
                        customer -> {
                            var appointmentOpt = appointmentService.getAppointmentsByDoctorAndCustomer(doctor, customer);
                            if (appointmentOpt.isEmpty()) {
                                return badRequest();
                            }
                            var chatOpt = chatService.getChatByDoctorAndCustomer(doctor, customer);
                            if (chatOpt.isPresent()) {
                                return ResponseEntity.ok(chatMapper.mapToDto(chatOpt.get()));
                            }
                            var chat = Chat.builder()
                                    .customer(customer)
                                    .doctor(doctor)
                                    .clinic(doctor.getClinic())
                                    .build();
                            chat = chatService.saveOrUpdateChat(chat);
                            return ResponseEntity.ok(chatMapper.mapToDto(chat));
                        }
                )
        );
    }

    @PostMapping("/chat/{chatId}/message")
    public ResponseEntity<List<MessageDto>> createMessage(@PathVariable Long chatId, @Valid @RequestBody MessageDto messageDto) {
        return withUserEntity(
                doctorService::getDoctorByUser,
                doctor -> withEntity(
                        chatService::getChatById,
                        chatId,
                        chat -> {
                            if (!belongsTo(chat.getDoctor().getId(), doctor.getId())) {
                                return notFound();
                            }
                            var message = ChatMessageUtil.createMessage(chat, messageDto.text(), Role.DOCTOR);
                            message = messageService.saveOrUpdateMessage(message);
                            var messages = messageService.getMessagesByChat(chat);
                            return ResponseEntity.ok(messages.stream().map(messageMapper::mapToDto).toList());
                        }
                )
        );
    }
}
