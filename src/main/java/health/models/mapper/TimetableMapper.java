package health.models.mapper;

import health.models.Timetable;
import health.models.dto.TimetableDto;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class TimetableMapper {

    public Timetable mapFromDto(TimetableDto dto) {
        var builder = Timetable.builder();

        if (dto.start() != null) {
            builder.start(Instant.parse(dto.start()));
        }

        return builder.build();
    }

    public TimetableDto mapToDto(Timetable timetable) {
        return TimetableDto.builder()
                .id(timetable.getId())
                .doctorId(timetable.getDoctor().getId())
                .start(timetable.getStart().toString())
                .reserved(timetable.isReserved())
                .build();
    }
}
