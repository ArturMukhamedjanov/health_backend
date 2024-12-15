package health.controllers;

import health.auth.services.AuthenticationService;
import health.models.*;
import health.models.auth.Role;
import health.models.dto.*;
import health.models.mapper.*;
import health.services.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final AuthenticationService authenticationService;
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


    @GetMapping()
    public ResponseEntity<CustomerDto> getCustomer() {
        var currentUser = authenticationService.getCurrentUser();
        var customer = customerService.getCustomerByUser(currentUser);
        if (customer.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var customerDto = customerMapper.mapToDto(customer.get());
        customerDto.toBuilder().email(currentUser.getEmail());
        return ResponseEntity.ok(customerDto);
    }

    @PostMapping
    public ResponseEntity<CustomerDto> updateCustomerInfo(@Valid @RequestBody CustomerDto customerDto) {
        var currentUser = authenticationService.getCurrentUser();
        var currentCustomer = customerService.getCustomerByUser(currentUser);
        if (currentCustomer.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var updatedCustomer = mergeCustomers(currentCustomer.get(), customerDto);
        updatedCustomer = customerService.updateCustomer(updatedCustomer);
        var res = customerMapper.mapToDto(updatedCustomer);
        res.toBuilder().email(currentUser.getEmail());
        return ResponseEntity.ok(res);
    }

    @GetMapping("/analysis")
    public ResponseEntity<List<Map<String, Object>>> getCustomerAnalysis() {
        var currentUser = authenticationService.getCurrentUser();
        var customer = customerService.getCustomerByUser(currentUser);
        if (customer.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var analysis = analysisService.getAnalysysesByCustomer(customer.get());
        var response = analysis.stream()
                .map(analysisMapper::mapToDto)
                .collect(Collectors.groupingBy(
                        AnalysisDto::name,
                        Collectors.toList()
                ))
                .entrySet().stream()
                .map(entry -> {
                    String name = entry.getKey();

                    List<AnalysisDto> group = entry.getValue();

                    String unit = group.get(0).unit();

                    List<Map<String, Object>> values = group.stream()
                            .map(dto -> {
                                Map<String, Object> map = new HashMap<>();
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

    @PostMapping("/analysis")
    public ResponseEntity<List<AnalysisDto>> addCustomerAnalysis(@Valid @RequestBody List<AnalysisDto> analysisDtos) {
        var currentUser = authenticationService.getCurrentUser();
        var customer = customerService.getCustomerByUser(currentUser);
        for(AnalysisDto analysisDto : analysisDtos){
            if(!validateAnalysis(analysisDto)){
                return ResponseEntity.badRequest().build();
            }
        }
        if (customer.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var analyzes = analysisDtos.stream()
                .map(analysisMapper::mapFromDto)
                .peek(item -> item.setCustomer(customer.get()))
                .toList();
        List<AnalysisDto> res = analysisService.addAnalysis(analyzes).stream().map(analysisMapper::mapToDto).toList();
        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/analysis/{id}")
    public ResponseEntity<Void> deleteCustomerAnalysis(@PathVariable Long id) {
        var currentUser = authenticationService.getCurrentUser();
        var customer = customerService.getCustomerByUser(currentUser);
        if (customer.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var analysis = analysisService.getAnalysisById(id);
        if (analysis.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        if (analysis.get().getCustomer().getId() != customer.get().getId()) {
            return ResponseEntity.status(403).build();
        }
        analysisService.deleteAnalysis(analysis.get());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/appointment")
    public ResponseEntity<List<AppointmentDto>> getAppointments() {
        var currentUser = authenticationService.getCurrentUser();
        var customer = customerService.getCustomerByUser(currentUser);
        if (customer.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        List<Appointment> appointments = appointmentService.getAppointmentsByCustomer(customer.get());
        return ResponseEntity.ok(appointments.stream().map(appointmentMapper::mapToDto).toList());
    }

    @PostMapping("/appointment/{timetableId}")
    public ResponseEntity<AppointmentDto> addAppointment(@PathVariable Long timetableId) {
        var currentUser = authenticationService.getCurrentUser();
        var customer = customerService.getCustomerByUser(currentUser);
        if (customer.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var timetableOpt = timetableService.getTimetableById(timetableId);
        if (timetableOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        if(timetableOpt.get().isReserved()){
            return ResponseEntity.badRequest().build();
        }
        var appointment = Appointment.builder()
                .clinic(timetableOpt.get().getDoctor().getClinic())
                .doctor(timetableOpt.get().getDoctor())
                .customer(customer.get())
                .timetable(timetableOpt.get())
                .build();
        appointment = appointmentService.saveOrUpdateAppointment(appointment);
        timetableService.reserveTimetable(timetableOpt.get());
        return ResponseEntity.ok(appointmentMapper.mapToDto(appointment));
    }

    @GetMapping("/chat")
    public ResponseEntity<List<ChatDto>> getChats() {
        var currentUser = authenticationService.getCurrentUser();
        var customer = customerService.getCustomerByUser(currentUser);
        if (customer.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        List<Chat> chats = chatService.getChatsByCustomer(customer.get());
        return ResponseEntity.ok(chats.stream().map(chatMapper::mapToDto).toList());
    }

    @GetMapping("/chat/{chatId}/message")
    public ResponseEntity<List<MessageDto>> getChatMessages(@PathVariable Long chatId) {
        var currentUser = authenticationService.getCurrentUser();
        var customer = customerService.getCustomerByUser(currentUser);
        if (customer.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var chatOpt = chatService.getChatById(chatId);
        if(chatOpt.isEmpty() || chatOpt.get().getCustomer().getId() != customer.get().getId()){
            return ResponseEntity.notFound().build();
        }
        List<Message> messages = messageService.getMessagesByChat(chatOpt.get());
        return ResponseEntity.ok(messages.stream().map(messageMapper::mapToDto).toList());
    }

    @PostMapping("/chat/{doctorId}")
    public ResponseEntity<ChatDto> createChat(@PathVariable Long doctorId) {
        var currentUser = authenticationService.getCurrentUser();
        var customer = customerService.getCustomerByUser(currentUser);
        if (customer.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var doctorOpt = doctorService.getDoctorById(doctorId);
        if(doctorOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var appointmentOpt = appointmentService.getAppointmentsByDoctorAndCustomer(doctorOpt.get(), customer.get());
        if(appointmentOpt.isEmpty()){
            return ResponseEntity.badRequest().build();
        }
        var chatOpt = chatService.getChatByDoctorAndCustomer(doctorOpt.get(), customer.get());
        if(chatOpt.isPresent()){
            return ResponseEntity.ok(chatMapper.mapToDto(chatOpt.get()));
        }
        var chat = Chat.builder()
                .customer(customer.get())
                .doctor(doctorOpt.get())
                .clinic(doctorOpt.get().getClinic())
                .build();
        chat = chatService.saveOrUpdateChat(chat);
        return ResponseEntity.ok(chatMapper.mapToDto(chat));
    }

    @PostMapping("/chat/{chatId}/message")
    public ResponseEntity<List<MessageDto>> createMessage(@PathVariable Long chatId, @RequestBody MessageDto messageDto) {
        var currentUser = authenticationService.getCurrentUser();
        var customer = customerService.getCustomerByUser(currentUser);
        if (customer.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var chatOpt = chatService.getChatById(chatId);
        if(chatOpt.isEmpty() || chatOpt.get().getCustomer().getId() != customer.get().getId()){
            return ResponseEntity.notFound().build();
        }
        if(messageDto.text() == null || messageDto.text().isEmpty()){
            return ResponseEntity.badRequest().build();
        }
        var message = Message.builder()
                .chat(chatOpt.get())
                .role(Role.CUSTOMER)
                .text(messageDto.text())
                .sendTime(Instant.now())
                .build();
        message = messageService.saveOrUpdateMessage(message);
        var messages = messageService.getMessagesByChat(chatOpt.get());
        return ResponseEntity.ok(messages.stream().map(messageMapper::mapToDto).toList());
    }

    public Customer mergeCustomers(Customer oldCustomer, CustomerDto newCustomer){
        if(newCustomer.firstName() != null){
            oldCustomer.setFirstName(newCustomer.firstName());
        }
        if(newCustomer.lastName() != null){
            oldCustomer.setLastName(newCustomer.lastName());
        }
        if(newCustomer.age() != null){
            oldCustomer.setAge(newCustomer.age());
        }
        if(newCustomer.weight() != null){
            oldCustomer.setWeight(newCustomer.weight());
        }
        if(newCustomer.height() != null){
            oldCustomer.setHeight(newCustomer.height());
        }
        if(newCustomer.gender() != null){
            oldCustomer.setGender(newCustomer.gender());
        }
        return oldCustomer;
    }

    public boolean validateAnalysis(AnalysisDto analysisDto){
        return analysisDto.name() != null &&
                analysisDto.value() != null &&
                analysisDto.unit() != null &&
                analysisDto.date() != null;
    }
}
