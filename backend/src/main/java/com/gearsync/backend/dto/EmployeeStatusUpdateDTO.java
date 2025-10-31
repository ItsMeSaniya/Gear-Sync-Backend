package com.gearsync.backend.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeStatusUpdateDTO {
    @NotBlank(message = "Status is required")
    @Pattern(regexp = "^(IN_PROGRESS|COMPLETED|ON_HOLD)$", message = "Status must be either 'IN_PROGRESS','COMPLETED' or ON_HOLD")
    private String status;

    @Size(max = 1000, message = "Notes cannot exceed 1000 characters")
    private String notes;

    @Min(value = 0, message = "Progress percentage must be between 0-100")
    @Max(value = 100, message = "Progress percentage must be between 0-100")
    private Integer progressPercentage;
}
