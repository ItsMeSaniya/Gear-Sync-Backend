package com.gearsync.backend.repository;

import com.gearsync.backend.model.TimeLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TimeLogRepository extends JpaRepository<TimeLog, Long> {

    // Find all time logs by employee
    List<TimeLog> findByEmployeeId(Long employeeId);

    // Find time logs by employee and date range
    List<TimeLog> findByEmployeeIdAndStartTimeBetween(
            Long employeeId,
            LocalDateTime start,
            LocalDateTime end
    );

    // Find all time logs for an appointment
    List<TimeLog> findByAppointmentId(Long appointmentId);

    // Find all time logs for a project
    List<TimeLog> findByProjectId(Long projectId);

    // Find time logs by employee for a specific appointment
    List<TimeLog> findByEmployeeIdAndAppointmentId(Long employeeId, Long appointmentId);

    // Find time logs by employee for a specific project
    List<TimeLog> findByEmployeeIdAndProjectId(Long employeeId, Long projectId);

    // Calculate total hours worked by employee
    @Query("SELECT COALESCE(SUM(t.durationMinutes), 0) FROM TimeLog t WHERE t.employee.id = :employeeId")
    Long getTotalMinutesWorkedByEmployee(@Param("employeeId") Long employeeId);

    // Calculate total hours worked by employee in date range
    @Query("SELECT COALESCE(SUM(t.durationMinutes), 0) FROM TimeLog t " +
            "WHERE t.employee.id = :employeeId " +
            "AND t.startTime >= :startDate AND t.endTime <= :endDate")
    Long getTotalMinutesWorkedByEmployeeInRange(
            @Param("employeeId") Long employeeId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    // Calculate total hours for an appointment
    @Query("SELECT COALESCE(SUM(t.durationMinutes), 0) FROM TimeLog t WHERE t.appointment.id = :appointmentId")
    Long getTotalMinutesForAppointment(@Param("appointmentId") Long appointmentId);

    // Calculate total hours for a project
    @Query("SELECT COALESCE(SUM(t.durationMinutes), 0) FROM TimeLog t WHERE t.project.id = :projectId")
    Long getTotalMinutesForProject(@Param("projectId") Long projectId);

    // Count time logs by employee
    long countByEmployeeId(Long employeeId);

    // Find recent time logs (last 7 days)
    @Query("SELECT t FROM TimeLog t WHERE t.employee.id = :employeeId " +
            "AND t.startTime >= :startDate ORDER BY t.startTime DESC")
    List<TimeLog> findRecentTimeLogsByEmployee(
            @Param("employeeId") Long employeeId,
            @Param("startDate") LocalDateTime startDate
    );
}