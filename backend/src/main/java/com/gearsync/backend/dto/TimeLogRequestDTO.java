package com.gearsync.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimeLogRequestDTO {
    private Long appointmentId;
    private Long projectId;

    @NotNull(message = "Start time is required")
    @PastOrPresent(message = "Start time cannot be in the future")
    private LocalDateTime startTime;

    @NotNull(message = "End time is required")
    private LocalDateTime endTime;

    @NotBlank(message = "Work description is required")
    @Size(min = 10, max = 1000, message = "Work description must be between 10-1000 characters")
    private String workDescription;

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;
}
