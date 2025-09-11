package com.example.complaint_reporting_api_v2.service.impl;

import com.example.complaint_reporting_api_v2.dto.user.CreateUserRequest;
import com.example.complaint_reporting_api_v2.dto.user.CreateUserResponse;
import com.example.complaint_reporting_api_v2.dto.user.GetUserComplaintResponse;
import com.example.complaint_reporting_api_v2.entity.ComplaintEntity;
import com.example.complaint_reporting_api_v2.entity.UserEntity;
import com.example.complaint_reporting_api_v2.repository.UserRepository;
import com.example.complaint_reporting_api_v2.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository){
        this.userRepository=userRepository;
    }

    @Override
    public CreateUserResponse createUser(CreateUserRequest req){
        UserEntity user = UserEntity.builder()
                .email(req.getEmail())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        UserEntity savedUser = userRepository.save(user);

        return CreateUserResponse.builder()
                .userId(savedUser.getUserId())
                .email(savedUser.getEmail())
                .build();
    }

    @Override
    public List<GetUserComplaintResponse> getUserComplaints(Long userId){
        UserEntity user = userRepository.findById(userId).
                orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        List<ComplaintEntity> complaintList = user.getComplaints();

        return complaintList.stream().map(
                c -> GetUserComplaintResponse.builder()
                        .complaintId(c.getComplaintId())
                        .description(c.getDescription())
                        .status(c.getStatus())
                        .build()
        ).toList();
    }
}
