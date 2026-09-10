package com.temp.demo.controller;

import com.temp.demo.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductControllerTest.class)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Test
    void getProduct_shouldReturn200() throws Exception {

        when(productService.getProduct(
                "",
                null,
                null,
                "price",
                "asc",
                0,
                10
        )).thenReturn(null);

        mockMvc.perform(
                        get("/api/product/get")
                                .param("page", "0")
                                .param("size", "10")
                )
                .andExpect(status().isOk());

        verify(productService).getProduct(
                "",
                null,
                null,
                "price",
                "asc",
                0,
                10
        );
    }
}
