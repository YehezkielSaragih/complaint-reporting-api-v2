package com.example.complaint_reporting_api_v2.controller;

import com.example.complaint_reporting_api_v2.dto.complaint.*;
import com.example.complaint_reporting_api_v2.service.ComplaintService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/complaints")
public class ComplaintController {

    private final ComplaintService complaintService;

    public ComplaintController(ComplaintService complaintService){
        this.complaintService=complaintService;
    }

    // Create complaint
    @PostMapping
    public CreateComplaintResponse createComplaint(@Valid @RequestBody CreateComplaintRequest request){
        return complaintService.createComplaint(request);
    }

    // Delete complaint
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteComplaint (@PathVariable Long id){
        return complaintService.deleteComplaint(id);
    }

    // Update complaint status
    @PutMapping("/{id}")
    public UpdateComplaintStatusResponse updateComplaintStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateComplaintStatusRequest status
    ) {
        return complaintService.updateComplaintStatus(id, status);
    }

    // Get all complaint
    @GetMapping
    public List<GetAllComplaintResponse> getAllComplaint(
            @RequestParam(required = false)
            String status
    ){
        return complaintService.getAllComplaint(status);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetComplaintResponse> getComplaintDetail(@PathVariable Long id){
        return ResponseEntity.ok(complaintService.getComplaintDetail(id));

    }

    @GetMapping("/statistics")
    public ResponseEntity<ComplaintStatisticsResponse> getComplaintStatistic(){
        return ResponseEntity.ok(complaintService.getComplaintStatistic());
    }
}
