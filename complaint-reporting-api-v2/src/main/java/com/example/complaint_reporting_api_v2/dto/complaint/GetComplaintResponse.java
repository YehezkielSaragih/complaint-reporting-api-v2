package com.example.complaint_reporting_api_v2.dto.complaint;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetComplaintResponse {

    @JsonProperty("complaint_id")
    private Long complaintId;

    @JsonProperty("user_email")
    private String userEmail;

    @JsonProperty("description")
    private String description;

    @JsonProperty("status")
    private String status;
}
