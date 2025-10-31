package com.gearsync.backend.service;

import com.gearsync.backend.dto.*;
import com.gearsync.backend.exception.*;
import com.gearsync.backend.model.*;
import com.gearsync.backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;


    @Transactional
    public ProjectResponseDTO createProject(String customerEmail, ProjectRequestDTO request) {

        User customer = userRepository.findByEmail(customerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        if (customer.getRole() != Role.CUSTOMER) {
            throw new UnauthorizedException("Only customers can create project requests");
        }

        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with ID: " + request.getVehicleId()));

        if (!vehicle.getOwner().getId().equals(customer.getId())) {
            throw new UnauthorizedException("You can only create projects for your own vehicles");
        }


        Project project = new Project();
        project.setCustomer(customer);
        project.setVehicle(vehicle);
        project.setProjectName(capitalizeWords(request.getProjectName().trim()));
        project.setDescription(request.getDescription().trim());
        project.setStatus(ProjectStatus.PENDING);
        project.setProgressPercentage(0);
        project.setEstimatedCost(java.math.BigDecimal.ZERO);
        project.setEstimatedDurationHours(0);

        if (request.getAdditionalNotes() != null && !request.getAdditionalNotes().isEmpty()) {
            project.setDescription(project.getDescription() + "\n\nAdditional Notes: " + request.getAdditionalNotes().trim());
        }

        Project savedProject = projectRepository.save(project);

        return convertToResponseDTO(savedProject);
    }

    @Transactional(readOnly = true)
    public List<ProjectResponseDTO> getMyProjects(String customerEmail) {

        User customer = userRepository.findByEmail(customerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        List<Project> projects = projectRepository.findByCustomerId(customer.getId());

        return projects.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProjectResponseDTO> getMyActiveProjects(String customerEmail) {

        User customer = userRepository.findByEmail(customerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        List<Project> projects = projectRepository.findActiveProjectsByCustomer(customer.getId());

        return projects.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProjectResponseDTO getProjectById(String userEmail, Long projectId) {

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + projectId));

        if (user.getRole() == Role.CUSTOMER) {
            if (!project.getCustomer().getId().equals(user.getId())) {
                throw new UnauthorizedException("You don't have permission to view this project");
            }
        }

        return convertToResponseDTO(project);
    }


    @Transactional
    public ProjectResponseDTO updateProject(String customerEmail, Long projectId, ProjectUpdateRequestDTO request) {

        User customer = userRepository.findByEmail(customerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + projectId));

        if (!project.getCustomer().getId().equals(customer.getId())) {
            throw new UnauthorizedException("You can only update your own projects");
        }

        if (project.getStatus() != ProjectStatus.PENDING &&
                project.getStatus() != ProjectStatus.REJECTED) {
            throw new IllegalStateException(
                    "You can only update projects that are PENDING or REJECTED. " +
                            "This project is currently " + project.getStatus()
            );
        }

        boolean isUpdated = false;

        if (request.getProjectName() != null && !request.getProjectName().isEmpty()) {
            project.setProjectName(capitalizeWords(request.getProjectName().trim()));
            isUpdated = true;
        }

        if (request.getDescription() != null && !request.getDescription().isEmpty()) {
            String newDescription = request.getDescription().trim();

            if (request.getAdditionalNotes() != null && !request.getAdditionalNotes().isEmpty()) {
                newDescription += "\n\nAdditional Notes: " + request.getAdditionalNotes().trim();
            }

            project.setDescription(newDescription);
            isUpdated = true;
        } else if (request.getAdditionalNotes() != null) {
            project.setDescription(project.getDescription() + "\n\nAdditional Notes: " + request.getAdditionalNotes().trim());
            isUpdated = true;
        }
        if (!isUpdated) {
            throw new IllegalArgumentException("No valid fields provided for update");
        }

        if (project.getStatus() == ProjectStatus.REJECTED) {
            project.setStatus(ProjectStatus.PENDING);
            log.info("Project {} status changed from REJECTED to PENDING", projectId);
        }

        Project updatedProject = projectRepository.save(project);
        return convertToResponseDTO(updatedProject);
    }

    @Transactional
    public void deleteProject(String customerEmail, Long projectId) {
        log.info("Deleting project {} for customer: {}", projectId, customerEmail);

        User customer = userRepository.findByEmail(customerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + projectId));

        if (!project.getCustomer().getId().equals(customer.getId())) {
            throw new UnauthorizedException("You can only delete your own projects");
        }

        if (project.getStatus() == ProjectStatus.IN_PROGRESS) {
            throw new IllegalStateException(
                    "Cannot delete a project that is currently in progress. " +
                            "Please contact support to cancel this project."
            );
        }

        if (project.getStatus() == ProjectStatus.COMPLETED) {
            throw new IllegalStateException("Cannot delete a completed project");
        }

        if (project.getStatus() != ProjectStatus.PENDING &&
                project.getStatus() != ProjectStatus.REJECTED &&
                project.getStatus() != ProjectStatus.CANCELLED) {
            throw new IllegalStateException(
                    "Can only delete projects that are PENDING, REJECTED, or CANCELLED. " +
                            "This project is currently " + project.getStatus()
            );
        }

        projectRepository.delete(project);
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

    private String capitalizeWords(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        String[] words = input.split("\\s+");
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                result.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1).toLowerCase())
                        .append(" ");
            }
        }

        return result.toString().trim();
    }
}