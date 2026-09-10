package com.temp.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.temp.demo.bean.ControllerAdviceConfig;
import com.temp.demo.dto.request.RequestAuthenticateDTO;
import com.temp.demo.dto.request.RequestForgetPasswordDTO;
import com.temp.demo.dto.request.RequestResetPasswordDTO;
import com.temp.demo.dto.request.RequestStaffRegisterDTO;
import com.temp.demo.dto.response.ResponseAuthenticateDTO;
import com.temp.demo.service.DocumentService;
import com.temp.demo.service.StaffService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class PublicControllerTest {

    private MockMvc mockMvc;

    @Mock
    private StaffService staffService;

    @Mock
    private DocumentService documentService;

    @InjectMocks
    private PublicController publicController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(publicController)
                .setControllerAdvice(new ControllerAdviceConfig())
                .build();
    }

    // ==========================================
    // 1. Staff Register (/api/public/staff/register)
    // ==========================================

    @Test
    @DisplayName("staffRegister - Success returns 200 OK")
    void staffRegister_success() throws Exception {
        RequestStaffRegisterDTO requestDTO = new RequestStaffRegisterDTO();
        requestDTO.setFirstName("John");
        requestDTO.setLastName("Doe");
        requestDTO.setEmail("john.doe@example.com");
        requestDTO.setUsername("johndoe123");
        requestDTO.setPassword("Password123!");
        requestDTO.setConfirmPassword("Password123!");

        doNothing().when(staffService).staffRegister(any(RequestStaffRegisterDTO.class));

        mockMvc.perform(post("/api/public/staff/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.content").value("success"));

        verify(staffService).staffRegister(any(RequestStaffRegisterDTO.class));
    }

    @Test
    @DisplayName("staffRegister - Missing required field returns 400 Bad Request")
    void staffRegister_missingFields_returns400() throws Exception {
        RequestStaffRegisterDTO requestDTO = new RequestStaffRegisterDTO();
        // firstName is null (required)
        requestDTO.setEmail("john.doe@example.com");
        requestDTO.setUsername("johndoe123");
        requestDTO.setPassword("Password123!");
        requestDTO.setConfirmPassword("Password123!");

        mockMvc.perform(post("/api/public/staff/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));

        verify(staffService, never()).staffRegister(any(RequestStaffRegisterDTO.class));
    }

    @Test
    @DisplayName("staffRegister - field too short returns 400 Bad Request")
    void staffRegister_fieldTooShort_returns400() throws Exception {
        RequestStaffRegisterDTO requestDTO = new RequestStaffRegisterDTO();
        // firstName is too short
        requestDTO.setFirstName("Al");
        requestDTO.setEmail("john.doe@example.com");
        requestDTO.setUsername("johndoe123");
        requestDTO.setPassword("Password123!");
        requestDTO.setConfirmPassword("Password123!");

        mockMvc.perform(post("/api/public/staff/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));

        verify(staffService, never()).staffRegister(any(RequestStaffRegisterDTO.class));
    }

    @Test
    @DisplayName("staffRegister - Password mismatch returns 400 Bad Request")
    void staffRegister_passwordMismatch_returns400() throws Exception {
        RequestStaffRegisterDTO requestDTO = new RequestStaffRegisterDTO();
        requestDTO.setFirstName("John");
        requestDTO.setLastName("Doe");
        requestDTO.setEmail("john.doe@example.com");
        requestDTO.setUsername("johndoe123");
        requestDTO.setPassword("Password123!");
        requestDTO.setConfirmPassword("DifferentPassword123!");

        mockMvc.perform(post("/api/public/staff/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));

        verify(staffService, never()).staffRegister(any(RequestStaffRegisterDTO.class));
    }

    // ==========================================
    // 2. Authenticate (/api/public/staff/authenticate)
    // ==========================================

    @Test
    @DisplayName("authenticate - Success returns 200 OK with token")
    void authenticate_success() throws Exception {
        RequestAuthenticateDTO requestDTO = new RequestAuthenticateDTO();
        requestDTO.setUsername("johndoe123");
        requestDTO.setPassword("secretPassword");

        ResponseAuthenticateDTO responseDTO = new ResponseAuthenticateDTO("sample-jwt-token");
        when(staffService.authenticate(any(RequestAuthenticateDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/public/staff/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.content.token").value("sample-jwt-token"));

        verify(staffService).authenticate(any(RequestAuthenticateDTO.class));
    }

    @Test
    @DisplayName("authenticate - Missing password returns 400 Bad Request")
    void authenticate_missingPassword_returns400() throws Exception {
        RequestAuthenticateDTO requestDTO = new RequestAuthenticateDTO();
        requestDTO.setUsername("johndoe123");
        // password is null

        mockMvc.perform(post("/api/public/staff/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));

        verify(staffService, never()).authenticate(any(RequestAuthenticateDTO.class));
    }

    // ==========================================
    // 3. Forget Password (/api/public/staff/forget_password)
    // ==========================================

    @Test
    @DisplayName("forgetPassword - Success returns 200 OK")
    void forgetPassword_success() throws Exception {
        RequestForgetPasswordDTO requestDTO = new RequestForgetPasswordDTO();
        requestDTO.setUsername("johndoe123");
        requestDTO.setEmail("john.doe@example.com");

        doNothing().when(staffService).forgetPassword(any(RequestForgetPasswordDTO.class));

        mockMvc.perform(post("/api/public/staff/forget_password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.content").value("success"));

        verify(staffService).forgetPassword(any(RequestForgetPasswordDTO.class));
    }

    @Test
    @DisplayName("forgetPassword - Missing email returns 400 Bad Request")
    void forgetPassword_missingEmail_returns400() throws Exception {
        RequestForgetPasswordDTO requestDTO = new RequestForgetPasswordDTO();
        requestDTO.setUsername("johndoe123");
        // email is null

        mockMvc.perform(post("/api/public/staff/forget_password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));

        verify(staffService, never()).forgetPassword(any(RequestForgetPasswordDTO.class));
    }

    // ==========================================
    // 4. Validate Forget Password Session (/api/public/staff/forget_password/redirect/{session_token})
    // ==========================================

    @Test
    @DisplayName("validateForgetPasswordSession - Success returns 200 OK with boolean result")
    void validateForgetPasswordSession_success() throws Exception {
        String sessionToken = "valid-session-token-123";
        when(staffService.validateForgetPasswordSession(sessionToken)).thenReturn(true);

        mockMvc.perform(get("/api/public/staff/forget_password/redirect/{session_token}", sessionToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.content").value(true));

        verify(staffService).validateForgetPasswordSession(sessionToken);
    }

    // ==========================================
    // 5. Reset Password (/api/public/staff/reset_password)
    // ==========================================

    @Test
    @DisplayName("resetPassword - Success returns 200 OK")
    void resetPassword_success() throws Exception {
        RequestResetPasswordDTO requestDTO = new RequestResetPasswordDTO();
        requestDTO.setSessionToken("session-token-xyz");
        requestDTO.setNewPassword("NewSecurePassword123!");
        requestDTO.setConfirmPassword("NewSecurePassword123!");

        doNothing().when(staffService).resetPassword(any(RequestResetPasswordDTO.class));

        mockMvc.perform(post("/api/public/staff/reset_password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.content").value("success"));

        verify(staffService).resetPassword(any(RequestResetPasswordDTO.class));
    }

    @Test
    @DisplayName("resetPassword - Password mismatch returns 400 Bad Request")
    void resetPassword_passwordMismatch_returns400() throws Exception {
        RequestResetPasswordDTO requestDTO = new RequestResetPasswordDTO();
        requestDTO.setSessionToken("session-token-xyz");
        requestDTO.setNewPassword("NewSecurePassword123!");
        requestDTO.setConfirmPassword("DifferentPassword123!");

        mockMvc.perform(post("/api/public/staff/reset_password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));

        verify(staffService, never()).resetPassword(any(RequestResetPasswordDTO.class));
    }

    // ==========================================
    // 6. Get Image (/api/public/file/image/{image})
    // ==========================================

    @Test
    @DisplayName("getImage - Returns image byte array and 200 OK")
    void getImage_success() throws Exception {
        String imageName = "avatar.png";
        byte[] imageBytes = new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47};

        when(documentService.getUserProfileImage(eq(imageName))).thenReturn(imageBytes);

        mockMvc.perform(get("/api/public/file/image/{image}", imageName)
                        .accept(MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().bytes(imageBytes));

        verify(documentService).getUserProfileImage(eq(imageName));
    }
}
