package com.example.complaint_reporting_api_v2.dto.user;

import com.example.complaint_reporting_api_v2.entity.ComplaintStatusEnum;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetUserComplaintResponse {

    @JsonProperty("complaint_id")
    private Long complaintId;

    @JsonProperty("description")
    private String description;

    @JsonProperty("status")
    private ComplaintStatusEnum status;
}
