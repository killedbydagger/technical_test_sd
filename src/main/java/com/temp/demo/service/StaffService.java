package com.temp.demo.service;

import com.temp.demo.bean.BCryptPasswordEncoding;
import com.temp.demo.dto.EmailVerificationRegisterDTO;
import com.temp.demo.dto.request.RequestAuthenticateDTO;
import com.temp.demo.dto.request.RequestForgetPasswordDTO;
import com.temp.demo.dto.request.RequestResetPasswordDTO;
import com.temp.demo.dto.request.RequestStaffChangePasswordDTO;
import com.temp.demo.dto.request.RequestStaffChangeProfileDTO;
import com.temp.demo.dto.request.RequestStaffRegisterDTO;
import com.temp.demo.dto.request.RequestUploadFileDTO;
import com.temp.demo.dto.response.ResponseAuthenticateDTO;
import com.temp.demo.entity.Authority;
import com.temp.demo.entity.Staff;
import com.temp.demo.exception.AuthenticationException;
import com.temp.demo.exception.DataErrorException;
import com.temp.demo.exception.DataNotFoundException;
import com.temp.demo.repository.StaffRepository;
import com.temp.demo.security.JwtTokenUtil;
import com.temp.demo.util.Constants;
import com.temp.demo.util.Encryption;
import com.temp.demo.util.FileExtension;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class StaffService implements UserDetailsService {

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private BCryptPasswordEncoding passwordEncoding;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private EmailService emailService;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private RedisManagementService redisManagementService;

    @Value("${credential.secret.key}")
    private String credentialSecretKey;

    @Value("${front.end.url}")
    private String frontEndUrl;

    private final Logger logger = LogManager.getLogger(this);

    @Override
    public UserDetails loadUserByUsername(String username) {
        Optional<Staff> findByUsername = staffRepository.findByUsername(username);
        if(!findByUsername.isPresent())
            throw new DataNotFoundException("Staff not found");
        return findByUsername.get();
    }

    public List<SimpleGrantedAuthority> getStaffAuthority(Staff staff) {
        String key = "user|authority";
        String value = redisManagementService.getValueFromRedis(key, staff.getId());
        if(!Objects.isNull(value)) {
            return Arrays.stream(value.split(","))
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        }

        Set<String> authorities = staffRepository.getAuthorities(staff.getId());
        List<SimpleGrantedAuthority> result = new ArrayList<>();
        StringJoiner newValue = new StringJoiner(",");
        for(String authority : authorities) {
            result.add(new SimpleGrantedAuthority(authority));
            newValue.add(authority);
        }
        CompletableFuture.runAsync(() -> redisManagementService.setValueToRedis(key, staff.getId(), newValue.toString()));
        return result;
    }

    public Staff getByUsername(String username) {
        return (Staff) loadUserByUsername(username);
    }

    public void staffRegister(RequestStaffRegisterDTO staffRegisterDTO) {
        Staff staff = new Staff();
        staff.setId(0);
        staff.setFirstName(staffRegisterDTO.getFirstName());
        staff.setLastName(staffRegisterDTO.getLastName());
        staff.setEmail(staffRegisterDTO.getEmail());
        staff.setUsername(staffRegisterDTO.getUsername());
        staff.setPassword(passwordEncoding.passwordEncoder().encode(staffRegisterDTO.getPassword()));
        staff.setActive(Boolean.TRUE);
        staff.setEnabled(Boolean.TRUE);
        staffRepository.save(staff);
    }

    public ResponseAuthenticateDTO authenticate(RequestAuthenticateDTO authenticateDTO) {
        String usernameStr = authenticateDTO.getUsername();
        UserDetails userDetails = loadUserByUsername(usernameStr);
        Staff staff = (Staff) userDetails;
        checkStaffCredentials(staff, authenticateDTO.getPassword());
        return new ResponseAuthenticateDTO(jwtTokenUtil.generateToken(staff));
    }

    public void staffChangePassword(Staff staff, RequestStaffChangePasswordDTO changePasswordDTO) {
        String oldPassword = changePasswordDTO.getOldPassword();
        checkStaffCredentials(staff, oldPassword);

        staff.setPassword(passwordEncoding.passwordEncoder().encode(changePasswordDTO.getNewPassword()));
        staff.setLastUpdate(Constants.getCurrentTimestamp(Constants.FORMAT_1));
        staffRepository.save(staff);
    }

    public void forgetPassword(RequestForgetPasswordDTO forgetPasswordDTO) {
        String username = forgetPasswordDTO.getUsername();
        UserDetails userDetails = loadUserByUsername(username);
        Staff staff = (Staff) userDetails;
        if(!staff.getEmail().equals(forgetPasswordDTO.getEmail()))
            throw new DataErrorException("Email is invalid");

        String token = createForgetPasswordSession(staff, Constants.getTimestamp(Boolean.FALSE));
        EmailVerificationRegisterDTO verificationRegisterDTO = new EmailVerificationRegisterDTO();
        verificationRegisterDTO.setVerificationUrl(String.format("%s/user/reset_password/%s", frontEndUrl, token));

        CompletableFuture.runAsync(() -> emailService.sendEmailVerificationRegister(verificationRegisterDTO));
    }

    public boolean validateForgetPasswordSession(String token) {
        String[] split = decryptSessionToken(token);
        return validateSession(split);
    }

    private boolean validateSession(String[] split) {
        String timestampStr = split[3];
        if(!StringUtils.hasLength(timestampStr))
            throw new AuthenticationException("Missing timestamp on session token");
        long timestamp = Long.parseLong(timestampStr);
        return Constants.getTimestamp(Boolean.FALSE) < timestamp;
    }

    private void checkStaffCredentials(Staff staff, String password) {
        BCryptPasswordEncoder bCryptPasswordEncoder = passwordEncoding.passwordEncoder();
        if(!bCryptPasswordEncoder.matches(password, staff.getPassword()))
            throw new AuthenticationException("Staff credentials is invalid");
        if(!staff.isActive())
            throw new DataErrorException("Staff is inactive");
        if(!staff.isEnabled())
            throw new DataErrorException("Staff is disabled");
    }

    private String createForgetPasswordSession(Staff staff, Long timestamp) {
        long expDuration = 300;
        String raw = String.format("%s:%s:%s:%s", getStaffCompleteName(staff).replaceAll("\\s",""), staff.getEmail(), staff.getUsername(), timestamp + expDuration);
        return Encryption.getEncryptedString(credentialSecretKey, raw, Encryption.SecretKeyType.HEX);
    }

    private String getStaffCompleteName(Staff staff) {
        return String.format("%s %s", staff.getFirstName(), StringUtils.hasLength(staff.getLastName()) ? staff.getLastName() : "");
    }

    private String[] decryptSessionToken(String token) {
        String decryptedString = Encryption.getDecryptedString(credentialSecretKey, token, Encryption.SecretKeyType.HEX);
        String[] split = decryptedString.split(":");
        if(split.length != 4)
            throw new AuthenticationException("Invalid session token");
        return split;
    }

    public void resetPassword(RequestResetPasswordDTO resetPasswordDTO) {
        String sessionToken = resetPasswordDTO.getSessionToken();
        String[] split = decryptSessionToken(sessionToken);
        if(!validateSession(split))
            throw new AuthenticationException("Session token is expired");

        String username = split[2];
        UserDetails userDetails = loadUserByUsername(username);
        Staff staff = (Staff) userDetails;
        staff.setPassword(passwordEncoding.passwordEncoder().encode(resetPasswordDTO.getNewPassword()));
        staff.setLastUpdate(Constants.getCurrentTimestamp(Constants.FORMAT_1));
        staffRepository.save(staff);
    }

    public void staffChangeProfile(Staff staff, RequestStaffChangeProfileDTO changeProfileDTO) {
        String newImagePath = null;
        if(!Objects.isNull(changeProfileDTO.getImage())) {
            RequestUploadFileDTO uploadFileDTO = changeProfileDTO.getImage();
            try {
                String name = getStaffCompleteName(staff).replaceAll("\\s", "_").toUpperCase();
                Long timestamp = Constants.getTimestamp(Boolean.TRUE);
                FileExtension fileExtension = FileExtension.valueOf(uploadFileDTO.getExtension());
                String filename = String.format("%s_%s.%s", name, timestamp, fileExtension.getAlias());
                newImagePath = documentService.saveImage(uploadFileDTO.getSrc(), "user_profile", filename);
            } catch (Exception e) {
                logger.warn(String.format("Failed to save image, reason : %s", e.getMessage()), e);
            }
        }

        staff.setFirstName(changeProfileDTO.getFirstName());
        staff.setLastName(changeProfileDTO.getLastName());
        if(StringUtils.hasLength(newImagePath))
            staff.setPicture(newImagePath);
        staff.setLastUpdate(Constants.getCurrentTimestamp(Constants.FORMAT_1));
        staffRepository.save(staff);
    }


}
