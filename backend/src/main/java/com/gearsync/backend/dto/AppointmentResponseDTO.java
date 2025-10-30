package com.gearsync.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentResponseDTO {
    private Long id;
    private LocalDateTime scheduledDateTime;
    private String status;
    private String customerNotes;
    private String employeeNotes;
    private BigDecimal estimatedCost;
    private BigDecimal finalCost;
    private Integer progressPercentage;

    private Long customerId;
    private String customerName;
    private String customerEmail;
    private String customerPhone;

    private Long vehicleId;
    private String vehicleRegistrationNumber;
    private String vehicleMake;
    private String vehicleModel;
    private String vehicleYear;

    private List<ServiceSummaryDTO> services;

    private Long assignedEmployeeId;
    private String assignedEmployeeName;
    private String assignedEmployeeEmail;

    private LocalDateTime actualStartTime;
    private LocalDateTime actualEndTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}