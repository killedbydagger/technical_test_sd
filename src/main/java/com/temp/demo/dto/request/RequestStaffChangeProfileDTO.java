package com.temp.demo.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
public class RequestStaffChangeProfileDTO {

    @NotNull(message = "Field 'firstName' is required")
    @ApiModelProperty(value = "The first name of the staff", required = true, example = "Mewing")
    private String firstName;

    @ApiModelProperty(value = "The last name of the staff", example = "Mewing")
    private String lastName;

    @ApiModelProperty(value = "The image request object of the staff")
    private @Valid RequestUploadFileDTO image;
}
