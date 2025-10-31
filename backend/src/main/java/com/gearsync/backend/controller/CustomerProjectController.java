package com.gearsync.backend.controller;

import com.gearsync.backend.dto.ProjectRequestDTO;
import com.gearsync.backend.dto.ProjectResponseDTO;
import com.gearsync.backend.dto.ProjectUpdateRequestDTO;
import com.gearsync.backend.exception.ResourceNotFoundException;
import com.gearsync.backend.exception.UnauthorizedException;
import com.gearsync.backend.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/customer/projects")
@RequiredArgsConstructor
public class CustomerProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<?> createProject(
            Authentication authentication,
            @Valid @RequestBody ProjectRequestDTO request) {

        try {
            ProjectResponseDTO response = projectService.createProject(
                    authentication.getName(),
                    request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (UnauthorizedException | ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }


    }


    @GetMapping
    public ResponseEntity<List<?>> getMyProjects(Authentication authentication) {

        try {
            List<ProjectResponseDTO> projects = projectService.getMyProjects(
                    authentication.getName()
            );
            return ResponseEntity.ok(projects);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonList(e.getMessage()));
        }
    }


    @GetMapping("/active")
    public ResponseEntity<List<ProjectResponseDTO>> getMyActiveProjects(Authentication authentication) {
        List<ProjectResponseDTO> projects = projectService.getMyActiveProjects(
                authentication.getName()
        );
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProjectById(
            Authentication authentication,
            @PathVariable Long id) {

        try {
            ProjectResponseDTO project = projectService.getProjectById(
                    authentication.getName(),
                    id
            );
            return ResponseEntity.ok(project);
        } catch (ResourceNotFoundException | UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> updateProject(
            Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody ProjectUpdateRequestDTO request) {


        try {
            ProjectResponseDTO response = projectService.updateProject(
                    authentication.getName(),
                    id,
                    request
            );
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException | UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProject(
            Authentication authentication,
            @PathVariable Long id) {

        try {
            projectService.deleteProject(authentication.getName(), id);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException | UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }
}