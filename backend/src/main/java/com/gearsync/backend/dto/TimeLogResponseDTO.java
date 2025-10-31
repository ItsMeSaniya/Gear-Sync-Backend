package com.gearsync.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TimeLogResponseDTO {
    private Long id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer durationMinutes;
    private String workDescription;
    private String notes;

    private Long employeeId;
    private String employeeName;
    private String employeeEmail;

    private Long appointmentId;
    private String appointmentDescription;

    private Long projectId;
    private String projectName;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
