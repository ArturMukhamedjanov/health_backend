package health.controllers;

import health.auth.RegisterRequest;
import health.auth.services.AuthenticationService;
import health.models.Timetable;
import health.models.dto.*;
import health.models.mapper.*;
import health.services.*;
import health.utils.EntityMergeUtil;
import health.utils.TimetableUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/clinic")
public class ClinicController extends BaseController {

    private final UserService userService;
    private final ClinicService clinicService;
    private final DoctorService doctorService;
    private final TimetableService timetableService;
    private final AppointmentService appointmentService;
    private final ChatService chatService;
    private final MessageService messageService;

    private final ClinicMapper clinicMapper;
    private final DoctorMapper doctorMapper;
    private final TimetableMapper timetableMapper;
    private final AppointmentMapper appointmentMapper;
    private final ChatMapper chatMapper;
    private final MessageMapper messageMapper;

    public ClinicController(
            AuthenticationService authenticationService,
            UserService userService,
            ClinicService clinicService,
            DoctorService doctorService,
            TimetableService timetableService,
            AppointmentService appointmentService,
            ChatService chatService,
            MessageService messageService,
            ClinicMapper clinicMapper,
            DoctorMapper doctorMapper,
            TimetableMapper timetableMapper,
            AppointmentMapper appointmentMapper,
            ChatMapper chatMapper,
            MessageMapper messageMapper) {
        super(authenticationService);
        this.userService = userService;
        this.clinicService = clinicService;
        this.doctorService = doctorService;
        this.timetableService = timetableService;
        this.appointmentService = appointmentService;
        this.chatService = chatService;
        this.messageService = messageService;
        this.clinicMapper = clinicMapper;
        this.doctorMapper = doctorMapper;
        this.timetableMapper = timetableMapper;
        this.appointmentMapper = appointmentMapper;
        this.chatMapper = chatMapper;
        this.messageMapper = messageMapper;
    }

    @GetMapping()
    public ResponseEntity<ClinicDto> getClinic() {
        return withUserEntity(
                clinicService::getClinicByUser,
                clinic -> {
                    var clinicDto = clinicMapper.mapToDto(clinic);
                    clinicDto.toBuilder().email(getCurrentUser().getEmail());
                    return ResponseEntity.ok(clinicDto);
                }
        );
    }

    @PostMapping
    public ResponseEntity<ClinicDto> updateClinicInfo(@Valid @RequestBody ClinicDto clinicDto) {
        return withUserEntity(
                clinicService::getClinicByUser,
                clinic -> {
                    var updatedClinic = EntityMergeUtil.mergeClinic(clinic, clinicDto);
                    updatedClinic = clinicService.updateClinic(updatedClinic);
                    var res = clinicMapper.mapToDto(updatedClinic);
                    res.toBuilder().email(getCurrentUser().getEmail());
                    return ResponseEntity.ok(res);
                }
        );
    }

    @PostMapping("/doctor")
    public ResponseEntity<Void> registerDoctor(@Valid @RequestBody DoctorDto doctorDto) {
        return withUserEntity(
                clinicService::getClinicByUser,
                clinic -> {
                    if (userService.getUserByEmail(doctorDto.email()).isPresent()) {
                        return badRequest();
                    }
                    var doctor = doctorMapper.mapFromDto(doctorDto);
                    doctor.setClinic(clinic);
                    authenticationService.registerDoctor(RegisterRequest.builder()
                            .email(doctorDto.email())
                            .password(doctorDto.password())
                            .build(), doctor);
                    return ResponseEntity.ok().build();
                }
        );
    }

    @GetMapping("/doctor")
    public ResponseEntity<List<DoctorDto>> getDoctors() {
        return withUserEntity(
                clinicService::getClinicByUser,
                clinic -> {
                    var doctors = doctorService.getDoctorsByClinic(clinic);
                    var doctorDtos = doctors.stream().map(doctorMapper::mapToDto).toList();
                    return ResponseEntity.ok(doctorDtos);
                }
        );
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<DoctorDto> getDoctor(@PathVariable Long doctorId) {
        return withUserEntity(
                clinicService::getClinicByUser,
                clinic -> withEntity(
                        doctorService::getDoctorById,
                        doctorId,
                        doctor -> {
                            if (!belongsTo(doctor.getClinic().getId(), clinic.getId())) {
                                return notFound();
                            }
                            return ResponseEntity.ok(doctorMapper.mapToDto(doctor));
                        }
                )
        );
    }

    @GetMapping("/doctor/{doctorId}/timetable")
    public ResponseEntity<List<TimetableDto>> getDoctorTimetable(@PathVariable Long doctorId) {
        return withUserEntity(
                clinicService::getClinicByUser,
                clinic -> withEntity(
                        doctorService::getDoctorById,
                        doctorId,
                        doctor -> {
                            if (!belongsTo(doctor.getClinic().getId(), clinic.getId())) {
                                return notFound();
                            }
                            var timetables = timetableService.getTimetablesByDoctor(doctor);
                            var timetableDtos = timetables.stream().map(timetableMapper::mapToDto).toList();
                            return ResponseEntity.ok(timetableDtos);
                        }
                )
        );
    }

    @PostMapping("/doctor/{doctorId}/timetable")
    public ResponseEntity<List<TimetableDto>> setDoctorTimetable(@Valid @RequestBody List<Instant> workingHours, @PathVariable Long doctorId) {
        return withUserEntity(
                clinicService::getClinicByUser,
                clinic -> withEntity(
                        doctorService::getDoctorById,
                        doctorId,
                        doctor -> {
                            if (!belongsTo(doctor.getClinic().getId(), clinic.getId())) {
                                return notFound();
                            }
                            var reservedTimetables = timetableService.getReservedTimetablesByDoctor(doctor);
                            Set<Instant> workingHoursSet = new HashSet<>(workingHours);
                            reservedTimetables.stream()
                                    .map(Timetable::getStart)
                                    .filter(start -> !workingHoursSet.contains(start))
                                    .forEach(workingHours::add);
                            if (TimetableUtil.hasOverlappingWorkingHours(workingHours)) {
                                return badRequest();
                            }
                            timetableService.deleteFreeTimetables(doctor);
                            List<Timetable> timetables = timetableService.addOrUpdateFromRawTimetable(workingHours, doctor);
                            var timetableDtos = timetables.stream().map(timetableMapper::mapToDto).toList();
                            return ResponseEntity.ok(timetableDtos);
                        }
                )
        );
    }

    @GetMapping("/doctor/{doctorId}/appointment")
    public ResponseEntity<List<AppointmentDto>> getDoctorAppointments(@PathVariable Long doctorId) {
        return withUserEntity(
                clinicService::getClinicByUser,
                clinic -> withEntity(
                        doctorService::getDoctorById,
                        doctorId,
                        doctor -> {
                            if (!belongsTo(doctor.getClinic().getId(), clinic.getId())) {
                                return notFound();
                            }
                            var appointments = appointmentService.getAppointmentsByDoctor(doctor);
                            var appointmentDtos = appointments.stream().map(appointmentMapper::mapToDto).toList();
                            return ResponseEntity.ok(appointmentDtos);
                        }
                )
        );
    }

    @GetMapping("/appointment")
    public ResponseEntity<List<AppointmentDto>> getAppointments() {
        return withUserEntity(
                clinicService::getClinicByUser,
                clinic -> {
                    var appointments = appointmentService.getAppointmentsByClinic(clinic);
                    var appointmentDtos = appointments.stream().map(appointmentMapper::mapToDto).toList();
                    return ResponseEntity.ok(appointmentDtos);
                }
        );
    }

    @GetMapping("/doctor/{doctorId}/chat")
    public ResponseEntity<List<ChatDto>> getDoctorChats(@PathVariable Long doctorId) {
        return withUserEntity(
                clinicService::getClinicByUser,
                clinic -> withEntity(
                        doctorService::getDoctorById,
                        doctorId,
                        doctor -> {
                            if (!belongsTo(doctor.getClinic().getId(), clinic.getId())) {
                                return notFound();
                            }
                            var chats = chatService.getChatsByDoctor(doctor);
                            var chatDtos = chats.stream().map(chatMapper::mapToDto).toList();
                            return ResponseEntity.ok(chatDtos);
                        }
                )
        );
    }

    @GetMapping("/chat")
    public ResponseEntity<List<ChatDto>> getClinicChats() {
        return withUserEntity(
                clinicService::getClinicByUser,
                clinic -> {
                    var chats = chatService.getChatsByClinic(clinic);
                    var chatDtos = chats.stream().map(chatMapper::mapToDto).toList();
                    return ResponseEntity.ok(chatDtos);
                }
        );
    }

    @GetMapping("/chat/{chatId}/message")
    public ResponseEntity<List<MessageDto>> getChatMessages(@PathVariable Long chatId) {
        return withUserEntity(
                clinicService::getClinicByUser,
                clinic -> withEntity(
                        chatService::getChatById,
                        chatId,
                        chat -> {
                            if (!belongsTo(chat.getClinic().getId(), clinic.getId())) {
                                return notFound();
                            }
                            var messages = messageService.getMessagesByChat(chat);
                            var messageDtos = messages.stream().map(messageMapper::mapToDto).toList();
                            return ResponseEntity.ok(messageDtos);
                        }
                )
        );
    }
}
