package com.temp.demo.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class RequestAuthenticateDTO {

    @NotNull(message = "Field 'username' is required")
    @ApiModelProperty(value = "The username of the staff", required = true, example = "__username001")
    private String username;

    @NotNull(message = "Field 'password' is required")
    @ApiModelProperty(value = "The password of the staff", required = true, example = "pass123")
    private String password;
}
