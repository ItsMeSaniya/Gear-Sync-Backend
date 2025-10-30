package com.gearsync.backend.repository;

import com.gearsync.backend.model.Services;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ServiceRepository extends JpaRepository<Services, Long> {
}
