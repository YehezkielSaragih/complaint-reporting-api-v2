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
public class ComplaintStatisticsResponse {
    @JsonProperty("open")
    private Long open;

    @JsonProperty("resolved")
    private Long resolved;

    @JsonProperty("in_progress")
    private Long inProgress;
}