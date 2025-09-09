package com.example.complaint_reporting_api_v2.dto.complaint;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateComplaintStatusRequest {

    @NotNull
    @JsonProperty("complaint_id")
    private Long id;

    @NotBlank
    @JsonProperty("status")
    private String status;
}
