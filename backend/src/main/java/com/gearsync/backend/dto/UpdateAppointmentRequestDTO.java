package com.gearsync.backend.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAppointmentRequestDTO {
    private Long vehicleId;

    private List<Long> serviceIds;

    @Future(message = "Appointment must be scheduled for a future date")
    private LocalDateTime scheduledDateTime;

    @Size(max = 1000, message = "Customer notes cannot exceed 1000 characters")
    private String customerNotes;
}
