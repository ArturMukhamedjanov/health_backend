package health.utils;

import health.models.Clinic;
import health.models.Customer;
import health.models.Doctor;
import health.models.dto.ClinicDto;
import health.models.dto.CustomerDto;
import health.models.dto.DoctorDto;

/**
 * Utility class for merging DTO data into existing entities.
 * Centralizes merge logic to avoid duplication across controllers.
 */
public class EntityMergeUtil {

    private EntityMergeUtil() {
        // Utility class, prevent instantiation
    }

    /**
     * Merges CustomerDto data into existing Customer entity.
     * Only updates non-null fields from the DTO.
     *
     * @param entity Existing customer entity
     * @param dto DTO with updated data
     * @return Updated customer entity
     */
    public static Customer mergeCustomer(Customer entity, CustomerDto dto) {
        if (dto.firstName() != null) {
            entity.setFirstName(dto.firstName());
        }
        if (dto.lastName() != null) {
            entity.setLastName(dto.lastName());
        }
        if (dto.age() != null) {
            entity.setAge(dto.age());
        }
        if (dto.weight() != null) {
            entity.setWeight(dto.weight());
        }
        if (dto.height() != null) {
            entity.setHeight(dto.height());
        }
        if (dto.gender() != null) {
            entity.setGender(dto.gender());
        }
        return entity;
    }

    /**
     * Merges DoctorDto data into existing Doctor entity.
     * Only updates non-null fields from the DTO.
     *
     * @param entity Existing doctor entity
     * @param dto DTO with updated data
     * @return Updated doctor entity
     */
    public static Doctor mergeDoctor(Doctor entity, DoctorDto dto) {
        if (dto.firstName() != null) {
            entity.setFirstName(dto.firstName());
        }
        if (dto.lastName() != null) {
            entity.setLastName(dto.lastName());
        }
        if (dto.speciality() != null) {
            entity.setSpeciality(dto.speciality());
        }
        return entity;
    }

    /**
     * Merges ClinicDto data into existing Clinic entity.
     * Only updates non-null fields from the DTO.
     *
     * @param entity Existing clinic entity
     * @param dto DTO with updated data
     * @return Updated clinic entity
     */
    public static Clinic mergeClinic(Clinic entity, ClinicDto dto) {
        if (dto.name() != null) {
            entity.setName(dto.name());
        }
        if (dto.description() != null) {
            entity.setDescription(dto.description());
        }
        return entity;
    }
}