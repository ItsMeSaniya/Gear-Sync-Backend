package com.gearsync.backend.controller;

import com.gearsync.backend.dto.EmployeeStatusUpdateDTO;
import com.gearsync.backend.dto.ProjectResponseDTO;
import com.gearsync.backend.dto.TimeLogResponseDTO;
import com.gearsync.backend.exception.ResourceNotFoundException;
import com.gearsync.backend.exception.UnauthorizedException;
import com.gearsync.backend.service.EmployeeProjectService;
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
@RequestMapping("/api/employee/projects")
@RequiredArgsConstructor
class EmployeeProjectController {

    private final EmployeeProjectService projectService;
    private final EmployeeTimeLogService timeLogService;

    @GetMapping
    public ResponseEntity<List<?>> getMyAssignedProjects(Authentication authentication) {
        try {
            List<ProjectResponseDTO> projects = projectService.getMyAssignedProjects(
                    authentication.getName()
            );
            return ResponseEntity.ok(projects);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(404).body(Collections.singletonList(e.getMessage()));
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(403).body(Collections.singletonList(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Collections.singletonList(e.getMessage()));
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getProjectDetails(
            Authentication authentication,
            @PathVariable Long id) {

        try {
            ProjectResponseDTO project = projectService.getProjectDetails(
                    authentication.getName(),
                    id
            );
            return ResponseEntity.ok(project);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }


    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateProjectStatus(
            Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody EmployeeStatusUpdateDTO request) {


        try {
            ProjectResponseDTO response = projectService.updateProjectStatus(
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
        }catch(IllegalStateException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @GetMapping("/{id}/timelogs")
    public ResponseEntity<List<?>> getProjectTimeLogs(
            Authentication authentication,
            @PathVariable Long id) {


        try {
            List<TimeLogResponseDTO> timeLogs = timeLogService.getTimeLogsForProject(
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