package com.example.complaint_reporting_api_v2.service;

import com.example.complaint_reporting_api_v2.dto.complaint.CreateComplaintRequest;
import com.example.complaint_reporting_api_v2.dto.complaint.CreateComplaintResponse;
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
}
