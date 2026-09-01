package com.temp.demo.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;

@Data
public class RequestChangePasswordDTO {
    @NotNull(message = "Field 'newPassword' is required")
    @ApiModelProperty(value = "The new password of the staff", required = true, example = "newPass123")
    private String newPassword;

    @NotNull(message = "Field 'confirmPassword' is required")
    @ApiModelProperty(value = "The confirmation of the new password", required = true, example = "newPass123")
    private String confirmPassword;

    @AssertTrue(message = "Field 'newPassword' and 'confirmPassword' must be same")
    @ApiModelProperty(hidden = true)
    public boolean getValidateConfirmPassword() {
        return this.newPassword.equals(confirmPassword);
    }
}
