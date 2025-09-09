package com.example.complaint_reporting_api_v2.service;

import com.example.complaint_reporting_api_v2.dto.user.CreateUserRequest;
import com.example.complaint_reporting_api_v2.dto.user.CreateUserResponse;
import com.example.complaint_reporting_api_v2.dto.user.GetUserComplaintResponse;
import com.example.complaint_reporting_api_v2.entity.ComplaintEntity;
import com.example.complaint_reporting_api_v2.entity.UserEntity;
import com.example.complaint_reporting_api_v2.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public ResponseEntity<CreateUserResponse> createUser(CreateUserRequest req){
        UserEntity user = UserEntity.builder()
                .email(req.getEmail())
                .createdAt(LocalDateTime.now())
                .build();

        UserEntity savedUser = userRepository.save(user);

        return ResponseEntity.ok(
                CreateUserResponse.builder()
                        .userId(savedUser.getUserId())
                        .email(savedUser.getEmail())
                        .build()
        );
    }

    public ResponseEntity<List<GetUserComplaintResponse>> getUserComplaints(Long userId){
        UserEntity user = userRepository.findById(userId).orElse(null);
        if(user == null) return ResponseEntity.notFound().build();

        List<ComplaintEntity> complaintList = user.getComplaints();

        List<GetUserComplaintResponse> complaintResponsesList = complaintList.stream().map(
                c -> GetUserComplaintResponse.builder()
                        .complaintId(c.getComplaintId())
                        .description(c.getDescription())
                        .status(c.getStatus())
                        .build()
        ).toList();

        return ResponseEntity.ok(complaintResponsesList);
    }
}
