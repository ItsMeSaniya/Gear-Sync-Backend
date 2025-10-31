package com.gearsync.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectRequestDTO {
    @NotNull(message = "Vehicle ID is required")
    private Long vehicleId;

    @NotBlank(message = "Project name is required")
    @Size(min = 3, max = 100, message = "Project name must be between 3-100 characters")
    private String projectName;

    @NotBlank(message = "Description is required")
    @Size(min = 10, max = 2000, message = "Description must be between 10-2000 characters")
    private String description;

    @Size(max = 500, message = "Additional notes cannot exceed 500 characters")
    private String additionalNotes;
}
