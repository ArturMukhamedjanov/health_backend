package health.utils;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

/**
 * Utility class for timetable operations.
 * Centralizes timetable validation logic.
 */
public class TimetableUtil {

    private TimetableUtil() {
        // Utility class, prevent instantiation
    }

    /**
     * Checks if working hours have overlapping time slots.
     * Two time slots overlap if they are less than 60 minutes apart.
     *
     * @param workingHours List of working hour time slots
     * @return true if there are overlapping time slots, false otherwise
     */
    public static boolean hasOverlappingWorkingHours(List<Instant> workingHours) {
        if (workingHours == null || workingHours.size() <= 1) {
            return false;
        }
        workingHours.sort(Instant::compareTo);
        for (int i = 0; i < workingHours.size() - 1; i++) {
            Instant current = workingHours.get(i);
            Instant next = workingHours.get(i + 1);
            if (Duration.between(current, next).toMinutes() < 60) {
                return true;
            }
        }
        return false;
    }
}