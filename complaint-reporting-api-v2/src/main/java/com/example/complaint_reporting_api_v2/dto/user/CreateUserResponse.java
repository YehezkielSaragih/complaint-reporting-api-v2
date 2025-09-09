package com.example.complaint_reporting_api_v2.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateUserResponse {

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("email")
    private String email;
}
