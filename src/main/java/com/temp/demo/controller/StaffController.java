package com.temp.demo.controller;

import com.temp.demo.dto.request.RequestStaffChangePasswordDTO;
import com.temp.demo.dto.request.RequestStaffChangeProfileDTO;
import com.temp.demo.dto.response.BasicResponse;
import com.temp.demo.entity.Staff;
import com.temp.demo.service.StaffService;
import com.temp.demo.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

@RestController
@RequestMapping(value = Constants.API_PATH + Constants.STAFF_PATH)
@Validated
public class StaffController {

    @Autowired
    private StaffService staffService;

    @PostMapping(value = Constants.CHANGE_PASSWORD_PATH)
    public ResponseEntity<BasicResponse<String>> staffChangePassword(@ApiIgnore @AuthenticationPrincipal Staff staff,
                                                                     @Valid @RequestBody RequestStaffChangePasswordDTO changePasswordDTO) {
        BasicResponse<String> response = new BasicResponse<>();
        staffService.staffChangePassword(staff, changePasswordDTO);
        response.setSuccess("success", "success");
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = Constants.CHANGE_PROFILE_PATH)
    public ResponseEntity<BasicResponse<String>> staffChangeProfile(@ApiIgnore @AuthenticationPrincipal Staff staff,
                                                                    @Valid @RequestBody RequestStaffChangeProfileDTO changeProfileDTO) {
        BasicResponse<String> response = new BasicResponse<>();
        staffService.staffChangeProfile(staff, changeProfileDTO);
        response.setSuccess("success", "success");
        return ResponseEntity.ok(response);
    }
}
