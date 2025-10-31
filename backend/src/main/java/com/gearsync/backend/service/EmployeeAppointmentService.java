package com.gearsync.backend.service;

import com.gearsync.backend.dto.*;
import com.gearsync.backend.exception.*;
import com.gearsync.backend.model.*;
import com.gearsync.backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeAppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;

    private static final Set<String> ALLOWED_EMPLOYEE_STATUSES = new HashSet<>(
            Arrays.asList("IN_PROGRESS", "COMPLETED", "ON_HOLD")
    );


    @Transactional(readOnly = true)
    public List<AppointmentResponseDTO> getMyAssignedAppointments(String employeeEmail) {
        User employee = userRepository.findByEmail(employeeEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        if (employee.getRole() != Role.EMPLOYEE && employee.getRole() != Role.ADMIN) {
            throw new UnauthorizedException("Only employees can access this endpoint");
        }

        List<Appointment> appointments = appointmentRepository.findByAssignedEmployeeId(employee.getId());

        return appointments.stream()
                .map(appointment -> {
                    List<Services> services = new ArrayList<>(appointment.getAppointmentServices());
                    return convertToResponseDTO(appointment, services);
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AppointmentResponseDTO getAppointmentDetails(String employeeEmail, Long appointmentId) {

        User employee = userRepository.findByEmail(employeeEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with ID: " + appointmentId));


        if (appointment.getAssignedEmployee() == null ||
                !appointment.getAssignedEmployee().getId().equals(employee.getId())) {
            throw new UnauthorizedException("This appointment is not assigned to you");
        }

        List<Services> services = new ArrayList<>(appointment.getAppointmentServices());
        return convertToResponseDTO(appointment, services);
    }

    @Transactional
    public AppointmentResponseDTO updateAppointmentStatus(
            String employeeEmail,
            Long appointmentId,
            EmployeeStatusUpdateDTO request) {

        User employee = userRepository.findByEmail(employeeEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with ID: " + appointmentId));

        if (appointment.getAssignedEmployee() == null ||
                !appointment.getAssignedEmployee().getId().equals(employee.getId())) {
            throw new UnauthorizedException("This appointment is not assigned to you");
        }

        String newStatus = request.getStatus().toUpperCase();
        if (!ALLOWED_EMPLOYEE_STATUSES.contains(newStatus)) {
            throw new IllegalArgumentException(
                    "Employees can only set status to: IN_PROGRESS, COMPLETED, or ON_HOLD. " +
                            "Current attempt: " + newStatus
            );
        }

        AppointmentStatus currentStatus = appointment.getStatus();
        AppointmentStatus targetStatus;

        try {
            targetStatus = AppointmentStatus.valueOf(newStatus);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status: " + newStatus);
        }

        if (currentStatus == AppointmentStatus.COMPLETED) {
            throw new IllegalStateException("Cannot change status of a completed appointment");
        }

        if (currentStatus == AppointmentStatus.CANCELLED) {
            throw new IllegalStateException("Cannot change status of a cancelled appointment");
        }

        appointment.setStatus(targetStatus);

        if (targetStatus == AppointmentStatus.IN_PROGRESS &&
                appointment.getActualStartTime() == null) {
            appointment.setActualStartTime(LocalDateTime.now());
        }

        if (targetStatus == AppointmentStatus.COMPLETED) {
            if (appointment.getActualEndTime() == null) {
                appointment.setActualEndTime(LocalDateTime.now());
            }
            appointment.setProgressPercentage(100);
        }

        if (request.getProgressPercentage() != null) {
            appointment.setProgressPercentage(request.getProgressPercentage());
        }

        if (request.getNotes() != null && !request.getNotes().isEmpty()) {
            String existingNotes = appointment.getEmployeeNotes() != null ?
                    appointment.getEmployeeNotes() : "";
            String timestamp = LocalDateTime.now().toString();
            String newNote = String.format("[%s] %s: %s", timestamp, employee.getFirstName(), request.getNotes());

            appointment.setEmployeeNotes(
                    existingNotes.isEmpty() ? newNote : existingNotes + "\n" + newNote
            );
        }

        Appointment updated = appointmentRepository.save(appointment);

        List<Services> services = new ArrayList<>(appointment.getAppointmentServices());
        return convertToResponseDTO(updated, services);
    }


    private AppointmentResponseDTO convertToResponseDTO(
            Appointment appointment,
            List<Services> services) {

        AppointmentResponseDTO dto = new AppointmentResponseDTO();
        dto.setId(appointment.getId());
        dto.setScheduledDateTime(appointment.getScheduledDateTime());
        dto.setStatus(appointment.getStatus().name());
        dto.setCustomerNotes(appointment.getCustomerNotes());
        dto.setEmployeeNotes(appointment.getEmployeeNotes());
        dto.setFinalCost(appointment.getFinalCost());
        dto.setProgressPercentage(appointment.getProgressPercentage());

        User customer = appointment.getCustomer();
        dto.setCustomerId(customer.getId());
        dto.setCustomerName(customer.getFirstName() + " " + customer.getLastName());
        dto.setCustomerEmail(customer.getEmail());
        dto.setCustomerPhone(customer.getPhoneNumber());

        Vehicle vehicle = appointment.getVehicle();
        dto.setVehicleId(vehicle.getId());
        dto.setVehicleRegistrationNumber(vehicle.getRegistrationNumber());
        dto.setVehicleMake(vehicle.getMake());
        dto.setVehicleModel(vehicle.getModel());
        dto.setVehicleYear(String.valueOf(vehicle.getYear()));

        List<ServiceSummaryDTO> serviceSummaries = services.stream()
                .map(service -> {
                    ServiceSummaryDTO summary = new ServiceSummaryDTO();
                    summary.setId(service.getId());
                    summary.setServiceName(service.getServiceName());
                    summary.setCategory(service.getCategory().name());
                    summary.setBasePrice(service.getBasePrice());
                    summary.setEstimatedDurationMinutes(service.getEstimatedDurationMinutes());
                    return summary;
                })
                .collect(Collectors.toList());
        dto.setServices(serviceSummaries);

        BigDecimal estimatedCost = services.stream()
                .map(Services::getBasePrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        dto.setEstimatedCost(estimatedCost);

        if (appointment.getAssignedEmployee() != null) {
            User employee = appointment.getAssignedEmployee();
            dto.setAssignedEmployeeId(employee.getId());
            dto.setAssignedEmployeeName(employee.getFirstName() + " " + employee.getLastName());
            dto.setAssignedEmployeeEmail(employee.getEmail());
        }

        dto.setActualStartTime(appointment.getActualStartTime());
        dto.setActualEndTime(appointment.getActualEndTime());
        dto.setCreatedAt(appointment.getCreatedAt());
        dto.setUpdatedAt(appointment.getUpdatedAt());
        return dto;
    }
}