package com.example.complaint_reporting_api_v2.service;

import com.example.complaint_reporting_api_v2.dto.complaint.*;
import com.example.complaint_reporting_api_v2.entity.ComplaintEntity;
import com.example.complaint_reporting_api_v2.entity.ComplaintStatusEnum;
import com.example.complaint_reporting_api_v2.entity.UserEntity;
import com.example.complaint_reporting_api_v2.repository.ComplaintRepository;
import com.example.complaint_reporting_api_v2.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class ComplaintService {

    @Autowired
    private ComplaintRepository complaintRepository;
    @Autowired
    private UserRepository userRepository;

    // Create Complaint
    public CreateComplaintResponse createComplaint(CreateComplaintRequest request) {
        // Find user by email
        UserEntity user = userRepository.findByEmail(request.getEmail())
                .filter(u -> u.getDeletedAt() == null)
                .orElseThrow(() -> new IllegalArgumentException("Email not registered as user!"));
        // Logic
        ComplaintEntity complaint = ComplaintEntity.builder()
                .description(request.getDescription())
                .status(ComplaintStatusEnum.OPEN.toString())
                .user(user)
                .createdAt(LocalDateTime.now())
                .build();
        ComplaintEntity saved = complaintRepository.save(complaint);
        // Add complaint to user complaint list
        if (user.getComplaints() == null) user.setComplaints(new ArrayList<>());
        user.getComplaints().add(saved);
        // Return DTO
        return CreateComplaintResponse.builder()
                .email(user.getEmail())
                .description(saved.getDescription())
                .status(saved.getStatus())
                .createdAt(saved.getCreatedAt())
                .build();
    }


    public ResponseEntity<String> deleteComplaint(Long id){
        ComplaintEntity c = complaintRepository.findById(id).orElse(null);

        if(c == null) return ResponseEntity.notFound().build();

        c.setDeletedAt(LocalDateTime.now());
        
        complaintRepository.save(c);

        return ResponseEntity.ok("Complaint number " + c.getComplaintId() + " from user " + c.getUser().getEmail() + " has been deleted!");
    }
  
    // Find all
    public List<FindAllComplaintResponse> findAllComplaint(String status){
        // Find data
        List<ComplaintEntity> listData = complaintRepository.findAll().stream()
                .filter(c -> c.getDeletedAt() == null)
                .collect(Collectors.toList());
        // Filter by status
        if (status != null && !status.isBlank()) {
            try {
                ComplaintStatusEnum stats = ComplaintStatusEnum.valueOf(status.toUpperCase());
                listData = listData.stream()
                        .filter(c -> c.getStatus().equals(stats.name()))
                        .toList();
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid status");
            }
        }
        // Return DTO
        return listData.stream()
                .map(c -> FindAllComplaintResponse.builder()
                        .email(c.getUser().getEmail())
                        .description(c.getDescription())
                        .status(c.getStatus())
                        .createdAt(c.getCreatedAt())
                        .updatedAt(c.getUpdatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    public UpdateComplaintStatusResponse updateComplaintStatus(Long id, UpdateComplaintStatusRequest request) {
        // Find Complaint
        ComplaintEntity complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Complaint id not found"));
        // Validation
        ComplaintStatusEnum statusEnum;
        try {
            statusEnum = ComplaintStatusEnum.valueOf(request.getStatus().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status value: " + request.getStatus());
        }
        // Update status & timestamps
        complaint.setStatus(request.getStatus());
        complaint.setUpdatedAt(LocalDateTime.now());
        ComplaintEntity updated = complaintRepository.save(complaint);
        // Mapping ke DTO
        return UpdateComplaintStatusResponse.builder()
                .email(updated.getUser().getEmail())
                .description(updated.getDescription())
                .status(updated.getStatus())
                .createdAt(updated.getCreatedAt())
                .updatedAt(updated.getUpdatedAt())
                .build();
    }

    public ResponseEntity<GetComplaintResponse> getComplaintDetail(Long id){
        ComplaintEntity c = complaintRepository.findById(id).orElse(null);

        if(c == null) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(
                GetComplaintResponse.builder()
                        .complaintId(c.getComplaintId())
                        .userEmail(c.getUser().getEmail())
                        .description(c.getDescription())
                        .status(c.getStatus())
                        .build()
        );
    }

    public ResponseEntity<ComplaintStatisticsResponse> getComplaintStatistic(){
        List<ComplaintEntity> complainList = complaintRepository.findAll();
        Map<String, Long> statusCounts = complainList.stream()
                .collect(Collectors.groupingBy(ComplaintEntity::getStatus, Collectors.counting()));

        ComplaintStatisticsResponse c = ComplaintStatisticsResponse.builder()
                .open(statusCounts.getOrDefault("OPEN", 0L))
                .inProgress(statusCounts.getOrDefault("IN_PROGRESS", 0L))
                .resolved(statusCounts.getOrDefault("RESOLVED", 0L))
                .build();

        return ResponseEntity.ok(c);
    }
}
