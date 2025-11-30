package health.controllers;

import health.auth.services.AuthenticationService;
import health.models.*;
import health.models.auth.Role;
import health.models.dto.*;
import health.models.mapper.*;
import health.services.*;
import health.utils.ChatMessageUtil;
import health.utils.EntityMergeUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/customer")
public class CustomerController extends BaseController {

    private final CustomerService customerService;
    private final AnalysisService analysisService;
    private final TimetableService timetableService;
    private final AppointmentService appointmentService;
    private final ChatService chatService;
    private final MessageService messageService;
    private final DoctorService doctorService;

    private final AnalysisMapper analysisMapper;
    private final CustomerMapper customerMapper;
    private final AppointmentMapper appointmentMapper;
    private final ChatMapper chatMapper;
    private final MessageMapper messageMapper;

    public CustomerController(
            AuthenticationService authenticationService,
            CustomerService customerService,
            AnalysisService analysisService,
            TimetableService timetableService,
            AppointmentService appointmentService,
            ChatService chatService,
            MessageService messageService,
            DoctorService doctorService,
            AnalysisMapper analysisMapper,
            CustomerMapper customerMapper,
            AppointmentMapper appointmentMapper,
            ChatMapper chatMapper,
            MessageMapper messageMapper) {
        super(authenticationService);
        this.customerService = customerService;
        this.analysisService = analysisService;
        this.timetableService = timetableService;
        this.appointmentService = appointmentService;
        this.chatService = chatService;
        this.messageService = messageService;
        this.doctorService = doctorService;
        this.analysisMapper = analysisMapper;
        this.customerMapper = customerMapper;
        this.appointmentMapper = appointmentMapper;
        this.chatMapper = chatMapper;
        this.messageMapper = messageMapper;
    }

    @GetMapping()
    public ResponseEntity<CustomerDto> getCustomer() {
        return withUserEntity(
                customerService::getCustomerByUser,
                customer -> {
                    var customerDto = customerMapper.mapToDto(customer);
                    customerDto.toBuilder().email(getCurrentUser().getEmail());
                    return ResponseEntity.ok(customerDto);
                }
        );
    }

    @PostMapping
    public ResponseEntity<CustomerDto> updateCustomerInfo(@Valid @RequestBody CustomerDto customerDto) {
        return withUserEntity(
                customerService::getCustomerByUser,
                customer -> {
                    var updatedCustomer = EntityMergeUtil.mergeCustomer(customer, customerDto);
                    updatedCustomer = customerService.updateCustomer(updatedCustomer);
                    var res = customerMapper.mapToDto(updatedCustomer);
                    res.toBuilder().email(getCurrentUser().getEmail());
                    return ResponseEntity.ok(res);
                }
        );
    }

    @GetMapping("/analysis")
    public ResponseEntity<List<Map<String, Object>>> getCustomerAnalysis() {
        return withUserEntity(
                customerService::getCustomerByUser,
                customer -> {
                    var analysis = analysisService.getAnalysysesByCustomer(customer);
        var response = analysis.stream()
                .map(analysisMapper::mapToDto)
                .collect(Collectors.groupingBy(
                        AnalysisDto::name,
                        Collectors.toList()
                ))
                .entrySet().stream()
                .map(entry -> {
                    var name = entry.getKey();

                    var group = entry.getValue();

                    var unit = group.get(0).unit();

                    var values = group.stream()
                            .map(dto -> {
                                Map<String, Object> map = new HashMap<>();
                                map.put("id", dto.id());
                                map.put("value", dto.value()); // Преобразуем при необходимости
                                map.put("date", dto.date());  // Преобразуем `Instant` в строку или используйте как есть
                                return map;
                            })
                            .toList();
                    return Map.of(
                            "name", name,
                            "unit", unit,
                            "values", values
                    );
                    })
                    .toList();
                    return ResponseEntity.ok(response);
                }
        );
    }

    @PostMapping("/analysis")
    public ResponseEntity<List<AnalysisDto>> addCustomerAnalysis(@Valid @RequestBody List<AnalysisDto> analysisDtos) {
        return withUserEntity(
                customerService::getCustomerByUser,
                customer -> {
                    var analyzes = analysisDtos.stream()
                            .map(analysisMapper::mapFromDto)
                            .peek(item -> item.setCustomer(customer))
                            .toList();
                    List<AnalysisDto> res = analysisService.addAnalysis(analyzes).stream()
                            .map(analysisMapper::mapToDto)
                            .toList();
                    return ResponseEntity.ok(res);
                }
        );
    }

    @DeleteMapping("/analysis/{id}")
    public ResponseEntity<Void> deleteCustomerAnalysis(@PathVariable Long id) {
        return withUserEntity(
                customerService::getCustomerByUser,
                customer -> withEntity(
                        analysisService::getAnalysisById,
                        id,
                        analysis -> {
                            if (!belongsTo(analysis.getCustomer().getId(), customer.getId())) {
                                return forbidden();
                            }
                            analysisService.deleteAnalysis(analysis);
                            return ResponseEntity.ok().build();
                        }
                )
        );
    }

    @GetMapping("/appointment")
    public ResponseEntity<List<AppointmentDto>> getAppointments() {
        return withUserEntity(
                customerService::getCustomerByUser,
                customer -> {
                    List<Appointment> appointments = appointmentService.getAppointmentsByCustomer(customer);
                    return ResponseEntity.ok(appointments.stream().map(appointmentMapper::mapToDto).toList());
                }
        );
    }

    @DeleteMapping("/appointment/{appointmentId}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable Long appointmentId) {
        return withUserEntity(
                customerService::getCustomerByUser,
                customer -> withEntity(
                        appointmentService::getAppointmentById,
                        appointmentId,
                        appointment -> {
                            if (!belongsTo(appointment.getCustomer().getId(), customer.getId())) {
                                return notFound();
                            }
                            timetableService.freeTimetable(appointment.getTimetable());
                            appointmentService.deleteAppointment(appointment);
                            return ResponseEntity.ok().build();
                        }
                )
        );
    }

    @PostMapping("/appointment/{timetableId}")
    public ResponseEntity<AppointmentDto> addAppointment(@PathVariable Long timetableId) {
        return withUserEntity(
                customerService::getCustomerByUser,
                customer -> withEntity(
                        timetableService::getTimetableById,
                        timetableId,
                        timetable -> {
                            if (timetable.isReserved()) {
                                return badRequest();
                            }
                            var appointment = Appointment.builder()
                                    .clinic(timetable.getDoctor().getClinic())
                                    .doctor(timetable.getDoctor())
                                    .customer(customer)
                                    .timetable(timetable)
                                    .build();
                            appointment = appointmentService.saveOrUpdateAppointment(appointment);
                            timetableService.reserveTimetable(timetable);
                            return ResponseEntity.ok(appointmentMapper.mapToDto(appointment));
                        }
                )
        );
    }

    @GetMapping("/chat")
    public ResponseEntity<List<ChatDto>> getChats() {
        return withUserEntity(
                customerService::getCustomerByUser,
                customer -> {
                    List<Chat> chats = chatService.getChatsByCustomer(customer);
                    return ResponseEntity.ok(chats.stream().map(chatMapper::mapToDto).toList());
                }
        );
    }

    @GetMapping("/chat/{chatId}/message")
    public ResponseEntity<List<MessageDto>> getChatMessages(@PathVariable Long chatId) {
        return withUserEntity(
                customerService::getCustomerByUser,
                customer -> withEntity(
                        chatService::getChatById,
                        chatId,
                        chat -> {
                            if (!belongsTo(chat.getCustomer().getId(), customer.getId())) {
                                return notFound();
                            }
                            List<Message> messages = messageService.getMessagesByChat(chat);
                            return ResponseEntity.ok(messages.stream().map(messageMapper::mapToDto).toList());
                        }
                )
        );
    }

    @PostMapping("/chat/{doctorId}")
    public ResponseEntity<ChatDto> createChat(@PathVariable Long doctorId) {
        return withUserEntity(
                customerService::getCustomerByUser,
                customer -> withEntity(
                        doctorService::getDoctorById,
                        doctorId,
                        doctor -> {
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
                customerService::getCustomerByUser,
                customer -> withEntity(
                        chatService::getChatById,
                        chatId,
                        chat -> {
                            if (!belongsTo(chat.getCustomer().getId(), customer.getId())) {
                                return notFound();
                            }
                            var message = ChatMessageUtil.createMessage(chat, messageDto.text(), Role.CUSTOMER);
                            message = messageService.saveOrUpdateMessage(message);
                            var messages = messageService.getMessagesByChat(chat);
                            return ResponseEntity.ok(messages.stream().map(messageMapper::mapToDto).toList());
                        }
                )
        );
    }
}
