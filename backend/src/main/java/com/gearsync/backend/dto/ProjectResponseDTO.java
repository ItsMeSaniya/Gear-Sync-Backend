package com.gearsync.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectResponseDTO {
    private Long id;
    private String projectName;
    private String description;
    private String status;

    // Cost information
    private BigDecimal estimatedCost;
    private BigDecimal actualCost;

    // Duration information
    private Integer estimatedDurationHours;
    private LocalDateTime startDate;
    private LocalDateTime completionDate;
    private LocalDateTime expectedCompletionDate;

    // Progress
    private Integer progressPercentage;

    // Customer info
    private Long customerId;
    private String customerName;
    private String customerEmail;
    private String customerPhone;

    // Vehicle info
    private Long vehicleId;
    private String vehicleRegistrationNumber;
    private String vehicleMake;
    private String vehicleModel;
    private String vehicleYear;

    // Assigned employee info (nullable)
    private Long assignedEmployeeId;
    private String assignedEmployeeName;
    private String assignedEmployeeEmail;

    // Additional notes
    private String additionalNotes;

    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
