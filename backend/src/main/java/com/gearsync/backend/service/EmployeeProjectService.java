package com.gearsync.backend.service;

import com.gearsync.backend.dto.*;
import com.gearsync.backend.exception.*;
import com.gearsync.backend.model.*;
import com.gearsync.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    private static final Set<String> ALLOWED_EMPLOYEE_STATUSES = new HashSet<>(
            Arrays.asList("IN_PROGRESS", "COMPLETED", "ON_HOLD")
    );

    @Transactional(readOnly = true)
    public List<ProjectResponseDTO> getMyAssignedProjects(String employeeEmail) {

        User employee = userRepository.findByEmail(employeeEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        if (employee.getRole() != Role.EMPLOYEE && employee.getRole() != Role.ADMIN) {
            throw new UnauthorizedException("Only employees can access this endpoint");
        }

        List<Project> projects = projectRepository.findByAssignedEmployeeId(employee.getId());

        return projects.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public ProjectResponseDTO getProjectDetails(String employeeEmail, Long projectId) {

        User employee = userRepository.findByEmail(employeeEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + projectId));

        if (project.getAssignedEmployee() == null ||
                !project.getAssignedEmployee().getId().equals(employee.getId())) {
            throw new UnauthorizedException("This project is not assigned to you");
        }

        return convertToResponseDTO(project);
    }


    @Transactional
    public ProjectResponseDTO updateProjectStatus(
            String employeeEmail,
            Long projectId,
            EmployeeStatusUpdateDTO request) {


        User employee = userRepository.findByEmail(employeeEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + projectId));

        if (project.getAssignedEmployee() == null ||
                !project.getAssignedEmployee().getId().equals(employee.getId())) {
            throw new UnauthorizedException("This project is not assigned to you");
        }

        String newStatus = request.getStatus().toUpperCase();
        if (!ALLOWED_EMPLOYEE_STATUSES.contains(newStatus)) {
            throw new IllegalArgumentException(
                    "Employees can only set status to: IN_PROGRESS, COMPLETED, or ON_HOLD. " +
                            "Current attempt: " + newStatus
            );
        }

        ProjectStatus currentStatus = project.getStatus();
        ProjectStatus targetStatus;

        try {
            targetStatus = ProjectStatus.valueOf(newStatus);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status: " + newStatus);
        }

        if (currentStatus == ProjectStatus.COMPLETED) {
            throw new IllegalStateException("Cannot change status of a completed project");
        }

        if (currentStatus == ProjectStatus.CANCELLED) {
            throw new IllegalStateException("Cannot change status of a cancelled project");
        }

        if (currentStatus == ProjectStatus.REJECTED) {
            throw new IllegalStateException("Cannot change status of a rejected project");
        }

        if (currentStatus == ProjectStatus.PENDING) {
            throw new IllegalStateException("Cannot change status of a pending project. Wait for admin approval.");
        }

        project.setStatus(targetStatus);

        if (targetStatus == ProjectStatus.IN_PROGRESS &&
                project.getStartDate() == null) {
            project.setStartDate(LocalDateTime.now());
        }

        if (targetStatus == ProjectStatus.COMPLETED) {
            if (project.getCompletionDate() == null) {
                project.setCompletionDate(LocalDateTime.now());
            }
            project.setProgressPercentage(100);
        }

        if (request.getProgressPercentage() != null) {
            project.setProgressPercentage(request.getProgressPercentage());
        }

        if (request.getNotes() != null && !request.getNotes().isEmpty()) {
            String existingDescription = project.getDescription() != null ?
                    project.getDescription() : "";
            String timestamp = LocalDateTime.now().toString();
            String newNote = String.format("\n\n[%s] Employee Update - %s: %s",
                    timestamp, employee.getFirstName(), request.getNotes());

            project.setDescription(existingDescription + newNote);
        }

        Project updated = projectRepository.save(project);
        return convertToResponseDTO(updated);
    }


    private ProjectResponseDTO convertToResponseDTO(Project project) {
        ProjectResponseDTO dto = new ProjectResponseDTO();
        dto.setId(project.getId());
        dto.setProjectName(project.getProjectName());
        dto.setDescription(project.getDescription());
        dto.setStatus(project.getStatus().name());

        dto.setEstimatedCost(project.getEstimatedCost());
        dto.setActualCost(project.getActualCost());
        dto.setEstimatedDurationHours(project.getEstimatedDurationHours());
        dto.setStartDate(project.getStartDate());
        dto.setCompletionDate(project.getCompletionDate());
        dto.setExpectedCompletionDate(project.getExpectedCompletionDate());
        dto.setProgressPercentage(project.getProgressPercentage());

        User customer = project.getCustomer();
        dto.setCustomerId(customer.getId());
        dto.setCustomerName(customer.getFirstName() + " " + customer.getLastName());
        dto.setCustomerEmail(customer.getEmail());
        dto.setCustomerPhone(customer.getPhoneNumber());

        Vehicle vehicle = project.getVehicle();
        dto.setVehicleId(vehicle.getId());
        dto.setVehicleRegistrationNumber(vehicle.getRegistrationNumber());
        dto.setVehicleMake(vehicle.getMake());
        dto.setVehicleModel(vehicle.getModel());
        dto.setVehicleYear(String.valueOf(vehicle.getYear()));

        if (project.getAssignedEmployee() != null) {
            User employee = project.getAssignedEmployee();
            dto.setAssignedEmployeeId(employee.getId());
            dto.setAssignedEmployeeName(employee.getFirstName() + " " + employee.getLastName());
            dto.setAssignedEmployeeEmail(employee.getEmail());
        }

        dto.setCreatedAt(project.getCreatedAt());
        dto.setUpdatedAt(project.getUpdatedAt());

        return dto;
    }
}