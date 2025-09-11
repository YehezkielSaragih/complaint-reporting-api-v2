package com.example.complaint_reporting_api_v2.service;

import com.example.complaint_reporting_api_v2.dto.user.CreateUserRequest;
import com.example.complaint_reporting_api_v2.dto.user.CreateUserResponse;
import com.example.complaint_reporting_api_v2.dto.user.GetUserComplaintResponse;

import java.util.List;

public interface UserService {
    CreateUserResponse createUser(CreateUserRequest req);
    List<GetUserComplaintResponse> getUserComplaints(Long userId);
}
