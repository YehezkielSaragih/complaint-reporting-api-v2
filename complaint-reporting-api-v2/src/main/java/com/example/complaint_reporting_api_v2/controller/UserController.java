package com.example.complaint_reporting_api_v2.controller;

import com.example.complaint_reporting_api_v2.dto.user.CreateUserRequest;
import com.example.complaint_reporting_api_v2.dto.user.CreateUserResponse;
import com.example.complaint_reporting_api_v2.dto.user.GetUserComplaintResponse;
import com.example.complaint_reporting_api_v2.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreateUserResponse> createUser(@Valid @RequestBody CreateUserRequest req){
        return userService.createUser(req);
    }

    @GetMapping("/{id}/complaints")
    public ResponseEntity<List<GetUserComplaintResponse>> getUserComplaints(@PathVariable Long id){
        return userService.getUserComplaints(id);
    }
}
