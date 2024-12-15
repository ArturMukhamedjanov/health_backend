package health.services;

import health.models.Doctor;
import health.models.Timetable;
import health.repos.TimetableRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TimetableService {

    private final TimetableRepo timetableRepository;

    public List<Timetable> getTimetablesByDoctor(Doctor doctor) {
        return timetableRepository.getTimetablesByDoctor(doctor);
    }

    public List<Timetable> getFreeTimetablesByDoctor(Doctor doctor) {
        return timetableRepository.getTimetablesByDoctorAndReserved(doctor, false);
    }

    public List<Timetable> getReservedTimetablesByDoctor(Doctor doctor) {
        return timetableRepository.getTimetablesByDoctorAndReserved(doctor, true);
    }

    public Optional<Timetable> getTimetableByDoctorAndStart(Doctor doctor, Instant start) {
        return timetableRepository.findTimetableByDoctorAndStart(doctor, start);
    }

    public Optional<Timetable> getTimetableById(Long id) {
        return timetableRepository.findById(id);
    }

    public Timetable createTimetable(Timetable timetable) {
        return timetableRepository.save(timetable);
    }

    public Timetable updateTimetable(Timetable timetable) {
        return timetableRepository.save(timetable);
    }

    public void deleteTimetable(Timetable timetable) {
        timetableRepository.delete(timetable);
    }

    public void reserveTimetable(Timetable timetable) {
        timetable.setReserved(true);
        updateTimetable(timetable);
    }

    public void freeTimetable(Timetable timetable) {
        timetable.setReserved(false);
        updateTimetable(timetable);
    }

    public void deleteFreeTimetables(Doctor doctor){
        List<Timetable> freeTimetables = getFreeTimetablesByDoctor(doctor);
        timetableRepository.deleteAll(freeTimetables);
    }

    public List<Timetable> addOrUpdateFromRawTimetable(List<Instant> workingOurs, Doctor doctor) {
        List<Timetable> result = new ArrayList<>();
        for (Instant workingHour : workingOurs){
            Optional<Timetable> timetableOpt = getTimetableByDoctorAndStart(doctor, workingHour);
            if(timetableOpt.isPresent()){
                result.add(timetableOpt.get());
            }else{
                result.add(createTimetable(Timetable.builder()
                        .doctor(doctor)
                        .start(workingHour)
                        .reserved(false)
                        .build()));
            }
        }
        return result;
    }
}