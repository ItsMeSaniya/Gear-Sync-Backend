package com.gearsync.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceSummaryDTO {
    private Long id;
    private String serviceName;
    private String category;
    private BigDecimal basePrice;
    private Integer estimatedDurationMinutes;
}
