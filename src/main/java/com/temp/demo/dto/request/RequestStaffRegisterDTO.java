package com.temp.demo.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class RequestStaffRegisterDTO {

    @NotNull(message = "Field 'firstName' is required")
    @ApiModelProperty(value = "The first name of the staff", required = true, example = "Mewing")
    @Size(min = 3, max = 100, message = "Field `firstName` has 3 min characters and 255 max characters")
    private String firstName;

    @Size(min = 3, max = 100, message = "Field `lastName` has 3 min characters and 255 max characters")
    @ApiModelProperty(value = "The last name of the staff", example = "Mewing")
    private String lastName;

    @NotNull(message = "Field 'email' is required")
    @ApiModelProperty(value = "The email of the staff", required = true, example = "example@example.com")
    private String email;

    @NotNull(message = "Field 'username' is required")
    @Size(min = 3, max = 100, message = "Field `username` has 3 min characters and 255 max characters")
    @ApiModelProperty(value = "The username of the staff", required = true, example = "__username001")
    private String username;

    @NotNull(message = "Field 'password' is required")
    @Size(min = 10, max = 100, message = "Field `password` has 10 min characters and 255 max characters")
    @ApiModelProperty(value = "The new password of the staff", required = true, example = "newPass123")
    private String password;

    @NotNull(message = "Field 'confirmPassword' is required")
    @ApiModelProperty(value = "The confirmation of the new password", required = true, example = "newPass123")
    private String confirmPassword;

    @AssertTrue(message = "Field 'password' and 'confirmPassword' must be same")
    @ApiModelProperty(hidden = true)
    public boolean getValidateConfirmPassword() {
        return this.password.equals(confirmPassword);
    }
}
