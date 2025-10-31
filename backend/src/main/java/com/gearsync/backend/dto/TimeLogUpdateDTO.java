package com.gearsync.backend.dto;

import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimeLogUpdateDTO {

    @PastOrPresent(message = "Start time cannot be in the future")
    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @Size(min = 10, max = 1000, message = "Work description must be between 10-1000 characters")
    private String workDescription;

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;
}