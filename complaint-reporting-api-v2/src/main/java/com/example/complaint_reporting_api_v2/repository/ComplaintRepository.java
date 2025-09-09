package com.example.complaint_reporting_api_v2.repository;

import com.example.complaint_reporting_api_v2.entity.ComplaintEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComplaintRepository extends JpaRepository<ComplaintEntity, Long> {
}
