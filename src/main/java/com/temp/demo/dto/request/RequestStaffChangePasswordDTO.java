package com.temp.demo.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;


@Data
@EqualsAndHashCode(callSuper = true)
public class RequestStaffChangePasswordDTO extends RequestChangePasswordDTO {

    @NotNull(message = "Field 'oldPassword' is required")
    @ApiModelProperty(value = "The old password of the staff", required = true, example = "oldPass123")
    private String oldPassword;

}
