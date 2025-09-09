package com.example.complaint_reporting_api_v2.service;

import com.example.complaint_reporting_api_v2.dto.user.CreateUserRequest;
import com.example.complaint_reporting_api_v2.dto.user.CreateUserResponse;
import com.example.complaint_reporting_api_v2.entity.UserEntity;
import com.example.complaint_reporting_api_v2.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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
}
