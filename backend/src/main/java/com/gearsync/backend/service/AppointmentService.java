package com.gearsync.backend.service;

import com.gearsync.backend.dto.AppointmentRequestDTO;
import com.gearsync.backend.dto.AppointmentResponseDTO;
import com.gearsync.backend.dto.MyAppointmentDTO;
import com.gearsync.backend.dto.ServiceSummaryDTO;
import com.gearsync.backend.exception.*;
import com.gearsync.backend.model.*;
import com.gearsync.backend.repository.AppointmentRepository;
import com.gearsync.backend.repository.ServiceRepository;
import com.gearsync.backend.repository.UserRepository;
import com.gearsync.backend.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final ServiceRepository serviceRepository;
    private final ModelMapper modelMapper;


    @Transactional
    public AppointmentResponseDTO bookAppointment(String customerEmail, AppointmentRequestDTO request) {

        User customer = userRepository.findByEmail(customerEmail)
                .orElseThrow(() -> new UserNotFoundException("Customer not found"));

        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new VehicleNotFoundException("Vehicle not found with ID: " + request.getVehicleId()));

        if (!vehicle.getOwner().getId().equals(customer.getId())) {
            throw new UnauthorizedException("You can only book appointments for your own vehicles");
        }

        if (request.getScheduledDateTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Cannot schedule appointment in the past");
        }

        if (appointmentRepository.existsByCustomerAndScheduledDateTime(
                customer, request.getScheduledDateTime())) {
            throw new DuplicateResourceException(
                    "You already have an appointment scheduled at " + request.getScheduledDateTime()
            );
        }

        if (request.getServiceIds().isEmpty()) {
            throw new IllegalArgumentException("At least one service must be selected");
        }

        List<Services> services = serviceRepository.findAllById(request.getServiceIds());

        if (services.size() != request.getServiceIds().size()) {
            throw new ResourceNotFoundException("One or more services not found");
        }

        for (Services service : services) {
            if (!service.getIsActive()) {
                throw new IllegalArgumentException("Services '" + service.getServiceName() + "' is not available");
            }
        }

        BigDecimal estimatedCost = services.stream()
                .map(Services::getBasePrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Integer totalDuration = services.stream()
                .mapToInt(Services::getEstimatedDurationMinutes)
                .sum();


        Appointment appointment = new Appointment();
        appointment.setCustomer(customer);
        appointment.setVehicle(vehicle);
        appointment.setScheduledDateTime(request.getScheduledDateTime());
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        appointment.setCustomerNotes(request.getCustomerNotes());
        appointment.setProgressPercentage(0);
        appointment.setFinalCost(estimatedCost);

        appointment.setAppointmentServices(new HashSet<>(services));

        Appointment savedAppointment = appointmentRepository.save(appointment);

        return convertToResponseDTO(savedAppointment, services);
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

    @Transactional
    public List<MyAppointmentDTO> getMyAppointments(String customerEmail) {
        User customer = userRepository.findByEmail(customerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        List<Appointment> appointments = appointmentRepository.findAllByCustomerId(customer.getId());

        return appointments.stream().map(appointment -> {
            MyAppointmentDTO dto = new MyAppointmentDTO();
            dto.setId(appointment.getId());
            dto.setScheduledDateTime(appointment.getScheduledDateTime());
            dto.setStatus(appointment.getStatus().name());
            dto.setCustomerNotes(appointment.getCustomerNotes());
            dto.setFinalCost(appointment.getFinalCost());
            dto.setServices(
                    appointment.getAppointmentServices().stream().map(service -> {
                        ServiceSummaryDTO sDto = new ServiceSummaryDTO();
                        sDto.setId(service.getId());
                        sDto.setServiceName(service.getServiceName());
                        sDto.setBasePrice(service.getBasePrice());
                        return sDto;
                    }).collect(Collectors.toSet())
            );

            return dto;
        }).collect(Collectors.toList());
    }

    @Transactional
    public MyAppointmentDTO getAppointmentById(String customerEmail, Long appointmentId) {
        User customer = userRepository.findByEmail(customerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

        if (!appointment.getCustomer().getId().equals(customer.getId())) {
            throw new UnauthorizedException("You can only view your own appointments");
        }

        MyAppointmentDTO response = modelMapper.map(appointment, MyAppointmentDTO.class);
        return response;
    }
}