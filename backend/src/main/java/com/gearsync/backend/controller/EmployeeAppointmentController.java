package com.gearsync.backend.controller;

import com.gearsync.backend.dto.AppointmentResponseDTO;
import com.gearsync.backend.dto.EmployeeStatusUpdateDTO;
import com.gearsync.backend.dto.TimeLogResponseDTO;
import com.gearsync.backend.exception.ResourceNotFoundException;
import com.gearsync.backend.exception.UnauthorizedException;
import com.gearsync.backend.service.EmployeeAppointmentService;
import com.gearsync.backend.service.EmployeeTimeLogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/employee/appointments")
@RequiredArgsConstructor
public class EmployeeAppointmentController {
    private final EmployeeAppointmentService appointmentService;
    private final EmployeeTimeLogService timeLogService;

    @GetMapping
    public ResponseEntity<List<?>> getMyAssignedAppointments(Authentication authentication) {
        try {
            List<AppointmentResponseDTO> appointments = appointmentService.getMyAssignedAppointments(
                    authentication.getName()
            );
            return ResponseEntity.ok(appointments);
        } catch (UnauthorizedException | ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Collections.singletonList(e.getMessage()));
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getAppointmentDetails(
            Authentication authentication,
            @PathVariable Long id) {
        try {
            AppointmentResponseDTO appointment = appointmentService.getAppointmentDetails(
                    authentication.getName(),
                    id
            );
            return ResponseEntity.ok(appointment);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }

    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateAppointmentStatus(
            Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody EmployeeStatusUpdateDTO request) {

        try {
            AppointmentResponseDTO response = appointmentService.updateAppointmentStatus(
                    authentication.getName(),
                    id,
                    request
            );
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/{id}/timelogs")
    public ResponseEntity<List<?>> getAppointmentTimeLogs(
            Authentication authentication,
            @PathVariable Long id) {

        try {
            List<TimeLogResponseDTO> timeLogs = timeLogService.getTimeLogsForAppointment(
                    authentication.getName(),
                    id
            );
            return ResponseEntity.ok(timeLogs);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonList(e.getMessage()));
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Collections.singletonList(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonList(e.getMessage()));
        }
    }
}
