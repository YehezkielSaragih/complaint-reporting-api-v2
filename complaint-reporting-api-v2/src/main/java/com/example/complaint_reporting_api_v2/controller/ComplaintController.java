package com.example.complaint_reporting_api_v2.controller;

import com.example.complaint_reporting_api_v2.dto.complaint.CreateComplaintRequest;
import com.example.complaint_reporting_api_v2.dto.complaint.CreateComplaintResponse;
import com.example.complaint_reporting_api_v2.dto.complaint.FindAllComplaintResponse;
import com.example.complaint_reporting_api_v2.entity.ComplaintEntity;
import com.example.complaint_reporting_api_v2.service.ComplaintService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/complaints")
public class ComplaintController {

    @Autowired
    ComplaintService complaintService;

    // Create complaint
    @PostMapping
    public CreateComplaintResponse createComplaint(@Valid @RequestBody CreateComplaintRequest request){
        return complaintService.createComplaint(request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteComplaint (@PathVariable Long id){
        return complaintService.deleteComplaint(id);
    }
    // Find all complaint
    @GetMapping
    public List<FindAllComplaintResponse> getAllComplaint(@RequestParam(required = false) String status){
        return complaintService.findAllComplaint(status);
    }
}
