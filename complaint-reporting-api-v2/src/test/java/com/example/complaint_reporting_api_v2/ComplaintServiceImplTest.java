package com.example.complaint_reporting_api_v2;

import com.example.complaint_reporting_api_v2.dto.complaint.*;
import com.example.complaint_reporting_api_v2.entity.ComplaintEntity;
import com.example.complaint_reporting_api_v2.entity.ComplaintStatusEnum;
import com.example.complaint_reporting_api_v2.entity.UserEntity;
import com.example.complaint_reporting_api_v2.repository.ComplaintRepository;
import com.example.complaint_reporting_api_v2.repository.UserRepository;
import com.example.complaint_reporting_api_v2.service.impl.ComplaintServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ComplaintServiceImplTest {

    @Mock
    private ComplaintRepository complaintRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ComplaintServiceImpl complaintService;

    private UserEntity mockUser;
    private ComplaintEntity mockComplaint;

    @BeforeEach
    void setUp() {
        mockUser = UserEntity.builder()
                .userId(1L)
                .email("test@mail.com")
                .deletedAt(null)
                .build();

        mockComplaint = ComplaintEntity.builder()
                .complaintId(100L)
                .description("Test complaint")
                .status("OPEN")
                .user(mockUser)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // --------- CREATE COMPLAINT ---------
    @Test
    void createComplaint_ShouldSaveAndReturnResponse() {
        CreateComplaintRequest request = CreateComplaintRequest.builder()
                .email("test@mail.com")
                .description("Test complaint")
                .build();

        when(userRepository.findByEmail("test@mail.com")).thenReturn(Optional.of(mockUser));
        when(complaintRepository.save(any(ComplaintEntity.class))).thenReturn(mockComplaint);

        CreateComplaintResponse response = complaintService.createComplaint(request);

        assertNotNull(response);
        assertEquals("test@mail.com", response.getEmail());
        assertEquals("OPEN", response.getStatus());
        verify(complaintRepository).save(any(ComplaintEntity.class));
    }

    @Test
    void createComplaint_ShouldThrowIfUserNotFound() {
        CreateComplaintRequest request = CreateComplaintRequest.builder()
                .email("unknown@mail.com")
                .description("Test complaint")
                .build();

        when(userRepository.findByEmail("unknown@mail.com")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> complaintService.createComplaint(request));
    }

    // --------- DELETE COMPLAINT ---------
    @Test
    void deleteComplaint_ShouldDeleteAndReturnMessage() {
        when(complaintRepository.findById(100L)).thenReturn(Optional.of(mockComplaint));
        when(complaintRepository.save(any(ComplaintEntity.class))).thenReturn(mockComplaint);

        ResponseEntity<String> response = complaintService.deleteComplaint(100L);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().contains("deleted"));
        verify(complaintRepository).save(mockComplaint);
    }

    @Test
    void deleteComplaint_ShouldReturnNotFoundIfNotExists() {
        when(complaintRepository.findById(200L)).thenReturn(Optional.empty());

        ResponseEntity<String> response = complaintService.deleteComplaint(200L);

        assertEquals(404, response.getStatusCodeValue());
    }

    // --------- UPDATE STATUS ---------
    @Test
    void updateComplaintStatus_ShouldUpdateAndReturnResponse() {
        UpdateComplaintStatusRequest request = UpdateComplaintStatusRequest.builder()
                .status("RESOLVED")
                .build();

        when(complaintRepository.findById(100L)).thenReturn(Optional.of(mockComplaint));
        when(complaintRepository.save(any(ComplaintEntity.class))).thenReturn(mockComplaint);

        UpdateComplaintStatusResponse response = complaintService.updateComplaintStatus(100L, request);

        assertEquals("RESOLVED", response.getStatus());
        verify(complaintRepository).save(mockComplaint);
    }

    @Test
    void updateComplaintStatus_ShouldThrowIfInvalidStatus() {
        UpdateComplaintStatusRequest request = UpdateComplaintStatusRequest.builder()
                .status("INVALID")
                .build();

        when(complaintRepository.findById(100L)).thenReturn(Optional.of(mockComplaint));

        assertThrows(IllegalArgumentException.class,
                () -> complaintService.updateComplaintStatus(100L, request));
    }

    @Test
    void updateComplaintStatus_ShouldThrowIfComplaintNotFound() {
        when(complaintRepository.findById(200L)).thenReturn(Optional.empty());

        UpdateComplaintStatusRequest request = UpdateComplaintStatusRequest.builder()
                .status("OPEN")
                .build();

        assertThrows(NoSuchElementException.class,
                () -> complaintService.updateComplaintStatus(200L, request));
    }

    // --------- GET ALL COMPLAINT ---------
    @Test
    void getAllComplaint_ShouldReturnList() {
        when(complaintRepository.findAll()).thenReturn(List.of(mockComplaint));

        List<GetAllComplaintResponse> responses = complaintService.getAllComplaint(null);

        assertEquals(1, responses.size());
        assertEquals("OPEN", responses.get(0).getStatus());
    }

    @Test
    void getAllComplaint_ShouldFilterByStatus() {
        when(complaintRepository.findAll()).thenReturn(List.of(mockComplaint));

        List<GetAllComplaintResponse> responses = complaintService.getAllComplaint("OPEN");

        assertEquals(1, responses.size());
        assertEquals("OPEN", responses.get(0).getStatus());
    }

    @Test
    void getAllComplaint_ShouldThrowIfStatusInvalid() {
        when(complaintRepository.findAll()).thenReturn(List.of(mockComplaint));

        assertThrows(IllegalArgumentException.class,
                () -> complaintService.getAllComplaint("INVALID"));
    }

    // --------- GET COMPLAINT DETAIL ---------
    @Test
    void getComplaintDetail_ShouldReturnResponse() {
        when(complaintRepository.findById(100L)).thenReturn(Optional.of(mockComplaint));

        GetComplaintResponse response = complaintService.getComplaintDetail(100L);

        assertEquals("Test complaint", response.getDescription());
        assertEquals("test@mail.com", response.getUserEmail());
    }

    @Test
    void getComplaintDetail_ShouldThrowIfNotFound() {
        when(complaintRepository.findById(200L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class,
                () -> complaintService.getComplaintDetail(200L));
    }

    // --------- GET STATISTICS ---------
    @Test
    void getComplaintStatistic_ShouldReturnCounts() {
        ComplaintEntity c1 = ComplaintEntity.builder().status("OPEN").build();
        ComplaintEntity c2 = ComplaintEntity.builder().status("IN_PROGRESS").build();
        ComplaintEntity c3 = ComplaintEntity.builder().status("RESOLVED").build();

        when(complaintRepository.findAll()).thenReturn(List.of(c1, c2, c3));

        ComplaintStatisticsResponse stats = complaintService.getComplaintStatistic();

        assertEquals(1L, stats.getOpen());
        assertEquals(1L, stats.getInProgress());
        assertEquals(1L, stats.getResolved());
    }
}
