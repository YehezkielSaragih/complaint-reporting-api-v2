package com.example.complaint_reporting_api_v2.repository;

import com.example.complaint_reporting_api_v2.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
}
