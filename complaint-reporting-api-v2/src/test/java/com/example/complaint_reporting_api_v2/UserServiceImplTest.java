package com.example.complaint_reporting_api_v2;

import com.example.complaint_reporting_api_v2.dto.user.CreateUserRequest;
import com.example.complaint_reporting_api_v2.dto.user.CreateUserResponse;
import com.example.complaint_reporting_api_v2.dto.user.GetUserComplaintResponse;
import com.example.complaint_reporting_api_v2.entity.ComplaintEntity;
import com.example.complaint_reporting_api_v2.entity.UserEntity;
import com.example.complaint_reporting_api_v2.repository.UserRepository;
import com.example.complaint_reporting_api_v2.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private UserEntity mockUser;

    @BeforeEach
    void setUp() {
        mockUser = UserEntity.builder()
                .userId(1L)
                .email("test@mail.com")
                .complaints(List.of(
                        ComplaintEntity.builder()
                                .complaintId(10L)
                                .description("Complaint 1")
                                .status("OPEN")
                                .build(),
                        ComplaintEntity.builder()
                                .complaintId(11L)
                                .description("Complaint 2")
                                .status("RESOLVED")
                                .build()
                ))
                .build();
    }

    // --------- CREATE USER ---------
    @Test
    void createUser_ShouldSaveAndReturnResponse() {
        CreateUserRequest request = CreateUserRequest.builder()
                .email("new@mail.com")
                .build();

        UserEntity savedUser = UserEntity.builder()
                .userId(2L)
                .email("new@mail.com")
                .build();

        when(userRepository.save(any(UserEntity.class))).thenReturn(savedUser);

        CreateUserResponse response = userService.createUser(request);

        assertNotNull(response);
        assertEquals(2L, response.getUserId());
        assertEquals("new@mail.com", response.getEmail());
        verify(userRepository).save(any(UserEntity.class));
    }

    // --------- GET USER COMPLAINTS ---------
    @Test
    void getUserComplaints_ShouldReturnListOfComplaints() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        List<GetUserComplaintResponse> responses = userService.getUserComplaints(1L);

        assertEquals(2, responses.size());
        assertEquals("Complaint 1", responses.get(0).getDescription());
        assertEquals("RESOLVED", responses.get(1).getStatus());
    }

    @Test
    void getUserComplaints_ShouldThrowIfUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class,
                () -> userService.getUserComplaints(99L));
    }
}
