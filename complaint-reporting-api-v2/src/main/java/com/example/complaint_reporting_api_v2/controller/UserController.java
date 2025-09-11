package com.example.complaint_reporting_api_v2.controller;

import com.example.complaint_reporting_api_v2.dto.user.CreateUserRequest;
import com.example.complaint_reporting_api_v2.dto.user.CreateUserResponse;
import com.example.complaint_reporting_api_v2.dto.user.GetUserComplaintResponse;
import com.example.complaint_reporting_api_v2.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService){
        this.userService=userService;
    }

    @PostMapping
    public ResponseEntity<CreateUserResponse> createUser(@Valid @RequestBody CreateUserRequest req){
        return ResponseEntity.ok(userService.createUser(req));
    }

    @GetMapping("/{id}/complaints")
    public ResponseEntity<List<GetUserComplaintResponse>> getUserComplaints(@PathVariable Long id){
        return ResponseEntity.ok(userService.getUserComplaints(id));
    }
}
