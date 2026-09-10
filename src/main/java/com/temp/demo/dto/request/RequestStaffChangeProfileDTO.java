package com.temp.demo.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class RequestStaffChangeProfileDTO {

    @NotBlank(message = "Field 'firstName' is required")
    @Size(min = 3, max = 255, message = "Field 'firstName' must be between 3 and 255 characters")
    @ApiModelProperty(value = "The first name of the staff", required = true, example = "Mewing")
    private String firstName;

    @ApiModelProperty(value = "The last name of the staff", example = "Mewing")
    private String lastName;

    @ApiModelProperty(value = "The image request object of the staff")
    private @Valid RequestUploadFileDTO image;
}
