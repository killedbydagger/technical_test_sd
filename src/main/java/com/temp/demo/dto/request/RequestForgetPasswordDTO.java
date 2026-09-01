package com.temp.demo.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class RequestForgetPasswordDTO {

    @NotNull(message = "Field 'email' is required")
    @ApiModelProperty(value = "The email of the staff", required = true, example = "example@example.com")
    private String email;

    @NotNull(message = "Field 'username' is required")
    @ApiModelProperty(value = "The username of the staff", required = true, example = "__username001")
    private String username;
}
