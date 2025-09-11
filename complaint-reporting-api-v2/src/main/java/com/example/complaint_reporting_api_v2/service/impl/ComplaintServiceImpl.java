package com.example.complaint_reporting_api_v2.service.impl;

import com.example.complaint_reporting_api_v2.dto.complaint.*;
import com.example.complaint_reporting_api_v2.entity.ComplaintEntity;
import com.example.complaint_reporting_api_v2.entity.ComplaintStatusEnum;
import com.example.complaint_reporting_api_v2.entity.UserEntity;
import com.example.complaint_reporting_api_v2.repository.ComplaintRepository;
import com.example.complaint_reporting_api_v2.repository.UserRepository;
import com.example.complaint_reporting_api_v2.service.ComplaintService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class ComplaintServiceImpl implements ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final UserRepository userRepository;

    public ComplaintServiceImpl(
            ComplaintRepository complaintRepository,
            UserRepository userRepository
    ){
        this.complaintRepository = complaintRepository;
        this.userRepository = userRepository;
    }

    // Create Complaint
    @Override
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
                .updatedAt(LocalDateTime.now())
                .build();
        ComplaintEntity saved = complaintRepository.save(complaint);
        // Return DTO
        return CreateComplaintResponse.builder()
                .complaintId(saved.getComplaintId())
                .email(user.getEmail())
                .description(saved.getDescription())
                .status(saved.getStatus())
                .createdAt(saved.getCreatedAt())
                .build();
    }

    // Delete complaint
    @Override
    public ResponseEntity<String> deleteComplaint(Long id){
        ComplaintEntity c = complaintRepository.findById(id).orElse(null);

        if(c == null) return ResponseEntity.notFound().build();

        c.setUpdatedAt(LocalDateTime.now());
        c.setDeletedAt(LocalDateTime.now());
        
        complaintRepository.save(c);

        return ResponseEntity.ok("Complaint number " + c.getComplaintId() + " from user " + c.getUser().getEmail() + " has been deleted!");
    }

    // Update complaint
    @Override
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
                .complaintId(updated.getComplaintId())
                .email(updated.getUser().getEmail())
                .description(updated.getDescription())
                .status(updated.getStatus())
                .createdAt(updated.getCreatedAt())
                .updatedAt(updated.getUpdatedAt())
                .build();
    }

    // Get all complaint
    @Override
    public List<GetAllComplaintResponse> getAllComplaint(String status){
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
                .map(c -> GetAllComplaintResponse.builder()
                        .complaintId(c.getComplaintId())
                        .email(c.getUser().getEmail())
                        .description(c.getDescription())
                        .status(c.getStatus())
                        .createdAt(c.getCreatedAt())
                        .updatedAt(c.getUpdatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    // Get complaint detail
    @Override
    public GetComplaintResponse getComplaintDetail(Long id){
        ComplaintEntity c = complaintRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Complaint not found"));
        return GetComplaintResponse.builder()
                .complaintId(c.getComplaintId())
                .userEmail(c.getUser().getEmail())
                .description(c.getDescription())
                .status(c.getStatus())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .build();
    }

    // Get complaint statistic
    @Override
    public ComplaintStatisticsResponse getComplaintStatistic(){
        List<ComplaintEntity> complainList = complaintRepository.findAll();
        Map<String, Long> statusCounts = complainList.stream()
                .collect(Collectors.groupingBy(ComplaintEntity::getStatus, Collectors.counting()));

        return ComplaintStatisticsResponse.builder()
                .open(statusCounts.getOrDefault("OPEN", 0L))
                .inProgress(statusCounts.getOrDefault("IN_PROGRESS", 0L))
                .resolved(statusCounts.getOrDefault("RESOLVED", 0L))
                .build();
    }
}
