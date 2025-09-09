package com.example.complaint_reporting_api_v2.dto.complaint;

import com.example.complaint_reporting_api_v2.entity.ComplaintStatusEnum;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FindAllComplaintResponse {

    @NotBlank
    @Email
    @JsonProperty("email")
    private String email;

    @NotBlank
    @JsonProperty("description")
    private String description;

    @NotNull
    @JsonProperty("status")
    private String status;

    @NotNull
    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @NotNull
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
}
