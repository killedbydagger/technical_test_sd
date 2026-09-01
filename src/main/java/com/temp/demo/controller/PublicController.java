package com.temp.demo.controller;

import com.temp.demo.dto.request.RequestAuthenticateDTO;
import com.temp.demo.dto.request.RequestForgetPasswordDTO;
import com.temp.demo.dto.request.RequestResetPasswordDTO;
import com.temp.demo.dto.request.RequestStaffRegisterDTO;
import com.temp.demo.dto.response.BasicResponse;
import com.temp.demo.dto.response.ResponseAuthenticateDTO;
import com.temp.demo.dto.response.ResponseCustomPaging;
import com.temp.demo.dto.response.ResponseProductDTO;
import com.temp.demo.entity.Staff;
import com.temp.demo.service.DocumentService;
import com.temp.demo.service.ProductService;
import com.temp.demo.service.StaffService;
import com.temp.demo.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.math.BigDecimal;

@RestController
@RequestMapping(value = Constants.API_PATH + Constants.PUBLIC_PATH)
@Validated
public class PublicController {

    @Autowired
    private StaffService staffService;

    @Autowired
    private ProductService productService;

    @Autowired
    private DocumentService documentService;

    @PostMapping(value = Constants.STAFF_PATH + Constants.REGISTER_PATH)
    public ResponseEntity<BasicResponse<String>> staffRegister(@Valid @RequestBody RequestStaffRegisterDTO staffRegisterDTO) {
        BasicResponse<String> response = new BasicResponse<>();
        staffService.staffRegister(staffRegisterDTO);
        response.setSuccess("success", "success");
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = Constants.STAFF_PATH + Constants.AUTHENTICATE_PATH)
    public ResponseEntity<BasicResponse<ResponseAuthenticateDTO>> authenticate(@Valid @RequestBody RequestAuthenticateDTO authenticateDTO) {
        BasicResponse<ResponseAuthenticateDTO> response = new BasicResponse<>();
        response.setSuccess(staffService.authenticate(authenticateDTO), "success");
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = Constants.STAFF_PATH + Constants.FORGET_PASSWORD_PATH)
    public ResponseEntity<BasicResponse<String>> forgetPassword(@Valid @RequestBody RequestForgetPasswordDTO forgetPasswordDTO) {
        BasicResponse<String> response = new BasicResponse<>();
        staffService.forgetPassword(forgetPasswordDTO);
        response.setSuccess("success", "success");
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = Constants.STAFF_PATH + Constants.FORGET_PASSWORD_PATH + Constants.REDIRECT_PATH + "/{session_token}")
    public ResponseEntity<BasicResponse<Boolean>> validateForgetPasswordSession(@PathVariable("session_token") String sessionToken) {
        BasicResponse<Boolean> response = new BasicResponse<>();
        response.setSuccess(staffService.validateForgetPasswordSession(sessionToken), "success");
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = Constants.STAFF_PATH + Constants.RESET_PASSWORD_PATH)
    public ResponseEntity<BasicResponse<String>> resetPassword(@Valid @RequestBody RequestResetPasswordDTO resetPasswordDTO) {
        BasicResponse<String> response = new BasicResponse<>();
        staffService.resetPassword(resetPasswordDTO);
        response.setSuccess("success", "success");
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = Constants.PRODUCT_PATH + Constants.GET_PATH)
    public ResponseEntity<BasicResponse<ResponseCustomPaging<ResponseProductDTO>>> getProduct(@RequestParam(value = "name", required = false, defaultValue = "") String name,
                                                                                              @RequestParam(value = "min_price", required = false) BigDecimal minPrice,
                                                                                              @RequestParam(value = "max_price", required = false) BigDecimal maxPrice,
                                                                                              @RequestParam(value = "sort_by", required = false, defaultValue = "price") String sortBy,
                                                                                              @RequestParam(value = "sort_order", required = false, defaultValue = "asc") String sortOrder,
                                                                                              @RequestParam(value = "page") int pageNumber,
                                                                                              @RequestParam(value = "size") int pageSize) {
        BasicResponse<ResponseCustomPaging<ResponseProductDTO>> response = new BasicResponse<>();
        response.setSuccess(productService.getProduct(name, minPrice, maxPrice, sortBy, sortOrder, pageNumber, pageSize), "success");
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = Constants.FILE + Constants.IMAGE + "/{image}", produces = {MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE})
    public ResponseEntity<Object> getImage(@PathVariable("image") String image) {
        return ResponseEntity.ok(documentService.getUserProfileImage(image));
    }
}
