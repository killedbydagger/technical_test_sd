package com.temp.demo.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ResponseProductDTO {

    private int id;
    private String name;
    private BigDecimal price;
    private String description;
    private Long createdAt;
    private Long updatedAt;

}
