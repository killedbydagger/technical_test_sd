package com.temp.demo.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Data
public class RequestUpdateProductDTO {

    private int id;

    @NotNull(message = "Field 'name' is required")
    @Size(min = 3, max = 255, message = "Field `name` has 3 min characters and 255 max characters")
    @ApiModelProperty(value = "The name of the product", required = true, example = "Mewing")
    private String name;

    @NotNull(message = "Field 'price' is required")
    @PositiveOrZero(message = "Field 'price' must be zero or greater")
    @ApiModelProperty(value = "The price of the product", required = true, example = "25.55")
    private BigDecimal price;

    @NotNull(message = "Field 'description' is required")
    @ApiModelProperty(value = "The description of the product", required = true, example = "lorem ipsum dolor sit amet")
    private String description;
}
