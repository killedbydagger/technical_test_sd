package com.temp.demo.dto.request;

import com.temp.demo.util.EnumStringValue;
import com.temp.demo.util.FileExtension;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class RequestUploadFileDTO {

    @NotNull(message = "Field 'src' is required")
    private String src;

    @NotNull(message = "Field 'extension' is required")
    @ApiModelProperty(value = "The extension of file")
    @EnumStringValue(enumClass = FileExtension.class, message = "Field 'extension' is not supported")
    private String extension;
}
