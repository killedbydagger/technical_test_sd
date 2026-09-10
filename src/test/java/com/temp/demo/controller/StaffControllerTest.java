package com.temp.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.temp.demo.bean.ControllerAdviceConfig;
import com.temp.demo.dto.request.RequestStaffChangePasswordDTO;
import com.temp.demo.dto.request.RequestStaffChangeProfileDTO;
import com.temp.demo.dto.request.RequestUploadFileDTO;
import com.temp.demo.entity.Staff;
import com.temp.demo.service.StaffService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class StaffControllerTest {

    private MockMvc mockMvc;

    @Mock
    private StaffService staffService;

    @InjectMocks
    private StaffController staffController;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private Staff mockStaff;

    @BeforeEach
    void setUp() {
        mockStaff = new Staff();
        mockStaff.setId(1);
        mockStaff.setUsername("testuser");
        mockStaff.setEmail("test@example.com");
        mockStaff.setFirstName("John");
        mockStaff.setLastName("Doe");

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(mockStaff, null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        mockMvc = MockMvcBuilders.standaloneSetup(staffController)
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                .setControllerAdvice(new ControllerAdviceConfig())
                .build();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    // ==========================================
    // 1. Staff Change Password (/api/staff/change_password)
    // ==========================================

    @Test
    @DisplayName("staffChangePassword - Success returns 200 OK")
    void staffChangePassword_success() throws Exception {
        RequestStaffChangePasswordDTO requestDTO = new RequestStaffChangePasswordDTO();
        requestDTO.setOldPassword("OldPassword123!");
        requestDTO.setNewPassword("NewPassword123!");
        requestDTO.setConfirmPassword("NewPassword123!");

        doNothing().when(staffService).staffChangePassword(eq(mockStaff), any(RequestStaffChangePasswordDTO.class));

        mockMvc.perform(post("/api/staff/change_password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.content").value("success"));

        verify(staffService).staffChangePassword(eq(mockStaff), any(RequestStaffChangePasswordDTO.class));
    }

    @Test
    @DisplayName("staffChangePassword - Missing oldPassword returns 400 Bad Request")
    void staffChangePassword_missingOldPassword_returns400() throws Exception {
        RequestStaffChangePasswordDTO requestDTO = new RequestStaffChangePasswordDTO();
        // oldPassword is null
        requestDTO.setNewPassword("NewPassword123!");
        requestDTO.setConfirmPassword("NewPassword123!");

        mockMvc.perform(post("/api/staff/change_password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));

        verify(staffService, never()).staffChangePassword(any(), any());
    }

    @Test
    @DisplayName("staffChangePassword - Password mismatch returns 400 Bad Request")
    void staffChangePassword_passwordMismatch_returns400() throws Exception {
        RequestStaffChangePasswordDTO requestDTO = new RequestStaffChangePasswordDTO();
        requestDTO.setOldPassword("OldPassword123!");
        requestDTO.setNewPassword("NewPassword123!");
        requestDTO.setConfirmPassword("DifferentPassword123!");

        mockMvc.perform(post("/api/staff/change_password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));

        verify(staffService, never()).staffChangePassword(any(), any());
    }

    // ==========================================
    // 2. Staff Change Profile (/api/staff/change_profile)
    // ==========================================

    @Test
    @DisplayName("staffChangeProfile - Success without image returns 200 OK")
    void staffChangeProfile_withoutImage_success() throws Exception {
        RequestStaffChangeProfileDTO requestDTO = new RequestStaffChangeProfileDTO();
        requestDTO.setFirstName("Alice");
        requestDTO.setLastName("Smith");

        doNothing().when(staffService).staffChangeProfile(eq(mockStaff), any(RequestStaffChangeProfileDTO.class));

        mockMvc.perform(post("/api/staff/change_profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.content").value("success"));

        verify(staffService).staffChangeProfile(eq(mockStaff), any(RequestStaffChangeProfileDTO.class));
    }

    @Test
    @DisplayName("staffChangeProfile - Success with image returns 200 OK")
    void staffChangeProfile_withImage_success() throws Exception {
        RequestUploadFileDTO uploadFileDTO = new RequestUploadFileDTO();
        uploadFileDTO.setSrc("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg==");
        uploadFileDTO.setExtension("PNG");

        RequestStaffChangeProfileDTO requestDTO = new RequestStaffChangeProfileDTO();
        requestDTO.setFirstName("Alice");
        requestDTO.setLastName("Smith");
        requestDTO.setImage(uploadFileDTO);

        doNothing().when(staffService).staffChangeProfile(eq(mockStaff), any(RequestStaffChangeProfileDTO.class));

        mockMvc.perform(post("/api/staff/change_profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.content").value("success"));

        verify(staffService).staffChangeProfile(eq(mockStaff), any(RequestStaffChangeProfileDTO.class));
    }

    @Test
    @DisplayName("staffChangeProfile - Missing firstName returns 400 Bad Request")
    void staffChangeProfile_missingFirstName_returns400() throws Exception {
        RequestStaffChangeProfileDTO requestDTO = new RequestStaffChangeProfileDTO();
        // firstName is null
        requestDTO.setLastName("Smith");

        mockMvc.perform(post("/api/staff/change_profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));

        verify(staffService, never()).staffChangeProfile(any(), any());
    }

    @Test
    @DisplayName("staffChangeProfile - firstName is too short returns 400 Bad Request")
    void staffChangeProfile_firstNameTooShort_returns400() throws Exception {
        RequestStaffChangeProfileDTO requestDTO = new RequestStaffChangeProfileDTO();
        // firstName is too short
        requestDTO.setFirstName("Al");
        requestDTO.setLastName("Smith");

        mockMvc.perform(post("/api/staff/change_profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));

        verify(staffService, never()).staffChangeProfile(any(), any());
    }

    @Test
    @DisplayName("staffChangeProfile - Invalid image extension returns 400 Bad Request")
    void staffChangeProfile_invalidImageExtension_returns400() throws Exception {
        RequestUploadFileDTO uploadFileDTO = new RequestUploadFileDTO();
        uploadFileDTO.setSrc("base64data");
        uploadFileDTO.setExtension("GIF"); // Not supported (only PNG and JPEG)

        RequestStaffChangeProfileDTO requestDTO = new RequestStaffChangeProfileDTO();
        requestDTO.setFirstName("Alice");
        requestDTO.setImage(uploadFileDTO);

        mockMvc.perform(post("/api/staff/change_profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));

        verify(staffService, never()).staffChangeProfile(any(), any());
    }
}
