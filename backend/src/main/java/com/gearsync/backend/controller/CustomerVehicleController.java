package com.gearsync.backend.controller;
import com.gearsync.backend.dto.VehicleRequestDTO;
import com.gearsync.backend.dto.VehicleResponseDTO;
import com.gearsync.backend.model.Vehicle;
import com.gearsync.backend.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer/vehicles")
@RequiredArgsConstructor
public class CustomerVehicleController {

    private final VehicleService vehicleService;

    @GetMapping
    public ResponseEntity<List<Vehicle>> list(Authentication authentication) {
        return ResponseEntity.ok(vehicleService.listMyVehicles(authentication.getName()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vehicle> get(Authentication authentication, @PathVariable Long id) {
        return ResponseEntity.ok(vehicleService.getMyVehicle(authentication.getName(), id));
    }

    @PostMapping
    public ResponseEntity<VehicleResponseDTO> addMyVehicle(
            Authentication authentication,
            @RequestBody VehicleRequestDTO payload) {

        VehicleResponseDTO response =
                vehicleService.addMyVehicle(authentication.getName(), payload);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
        public ResponseEntity<Vehicle> update(Authentication authentication, @PathVariable Long id, @RequestBody VehicleRequestDTO payload) {
            return ResponseEntity.ok(vehicleService.updateMyVehicle(authentication.getName(), id, payload));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Vehicle> patchUpdate(
            Authentication authentication,
            @PathVariable Long id,
            @RequestBody VehicleRequestDTO payload) {
        return ResponseEntity.ok(vehicleService.updateMyVehicle(authentication.getName(), id, payload));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(Authentication authentication, @PathVariable Long id) {
        vehicleService.deleteMyVehicle(authentication.getName(), id);
        return ResponseEntity.noContent().build();
    }
}


