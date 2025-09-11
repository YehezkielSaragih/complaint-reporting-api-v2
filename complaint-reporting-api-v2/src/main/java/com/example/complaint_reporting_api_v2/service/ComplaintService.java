package com.example.complaint_reporting_api_v2.service;

import com.example.complaint_reporting_api_v2.dto.complaint.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ComplaintService {
    CreateComplaintResponse createComplaint(CreateComplaintRequest request);
    ResponseEntity<String> deleteComplaint(Long id);
    UpdateComplaintStatusResponse updateComplaintStatus(Long id, UpdateComplaintStatusRequest request);
    List<GetAllComplaintResponse> getAllComplaint(String status);
    GetComplaintResponse getComplaintDetail(Long id);
    ComplaintStatisticsResponse getComplaintStatistic();


}
