package com.temp.demo.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class RequestResetPasswordDTO extends RequestChangePasswordDTO {

    @NotNull(message = "Field 'sessionToken' is required")
    @ApiModelProperty(value = "The session token created when clicking requesting forget password", required = true, example = "$e$S1oNt0k3N")
    private String sessionToken;
}
