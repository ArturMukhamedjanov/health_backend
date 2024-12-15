package health.models.mapper;

import health.models.Appointment;
import health.models.dto.AppointmentDto;
import org.springframework.stereotype.Component;

@Component
public class AppointmentMapper {


    public AppointmentDto mapToDto(Appointment appointment) {
        return AppointmentDto.builder()
                .id(appointment.getId())
                .clinicId(appointment.getClinic().getId())
                .doctorId(appointment.getDoctor().getId())
                .customerId(appointment.getCustomer().getId())
                .timetableId(appointment.getTimetable().getId())
                .start(appointment.getTimetable().getStart())
                .build();
    }
}
