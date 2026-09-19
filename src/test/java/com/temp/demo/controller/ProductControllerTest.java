package com.temp.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.temp.demo.bean.ControllerAdviceConfig;
import com.temp.demo.dto.request.RequestCreateProductDTO;
import com.temp.demo.dto.request.RequestDeleteProductDTO;
import com.temp.demo.dto.request.RequestUpdateProductDTO;
import com.temp.demo.dto.response.ResponseCustomPaging;
import com.temp.demo.dto.response.ResponseProductDTO;
import com.temp.demo.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ProductControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(productController)
                .setControllerAdvice(new ControllerAdviceConfig())
                .build();
    }

    // ==========================================
    // 1. Get Product (/api/product/get)
    // ==========================================

    @Test
    @DisplayName("getProduct - Success returns 200 OK")
    void getProduct_shouldReturn200() throws Exception {
        ResponseCustomPaging<ResponseProductDTO> paging = new ResponseCustomPaging<>();
        paging.setContent(Collections.emptyList());
        paging.setTotalElements(0);
        paging.setTotalPages(0);
        paging.setFirst(true);
        paging.setLast(true);

        when(productService.getProduct(
                "",
                null,
                null,
                "price",
                "asc",
                0,
                10
        )).thenReturn(paging);

        mockMvc.perform(
                        get("/api/product/get")
                                .param("page", "0")
                                .param("size", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.content").isMap())
                .andExpect(jsonPath("$.content.content").isArray())
                .andExpect(jsonPath("$.content.content").isEmpty())
                .andExpect(jsonPath("$.content.totalElements").value(0))
                .andExpect(jsonPath("$.content.totalPages").value(0))
                .andExpect(jsonPath("$.content.first").value(true))
                .andExpect(jsonPath("$.content.last").value(true));

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

    @Test
    @DisplayName("getProduct - Missing required page param returns 400 Bad Request")
    void getProduct_missingPage_returns400() throws Exception {
        mockMvc.perform(
                        get("/api/product/get")
                                .param("size", "10")
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));

        verify(productService, never()).getProduct(any(), any(), any(), any(), any(), any(int.class), any(int.class));
    }

    // ==========================================
    // 2. Create Product (/api/product/create)
    // ==========================================

    @Test
    @DisplayName("createProduct - Success returns 200 OK")
    void createProduct_success() throws Exception {
        RequestCreateProductDTO requestDTO = new RequestCreateProductDTO();
        requestDTO.setName("Sample Product");
        requestDTO.setPrice(new BigDecimal("19.99"));
        requestDTO.setDescription("Sample product description");

        ResponseProductDTO responseDTO = new ResponseProductDTO();
        responseDTO.setId(1);
        responseDTO.setName("Sample Product");
        responseDTO.setPrice(new BigDecimal("19.99"));
        responseDTO.setDescription("Sample product description");

        when(productService.createProduct(any(RequestCreateProductDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/product/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.content.name").value("Sample Product"));

        verify(productService).createProduct(any(RequestCreateProductDTO.class));
    }

    @Test
    @DisplayName("createProduct - Missing required name returns 400 Bad Request")
    void createProduct_missingName_returns400() throws Exception {
        RequestCreateProductDTO requestDTO = new RequestCreateProductDTO();
        // name is null
        requestDTO.setPrice(new BigDecimal("19.99"));
        requestDTO.setDescription("Sample product description");

        mockMvc.perform(post("/api/product/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));

        verify(productService, never()).createProduct(any(RequestCreateProductDTO.class));
    }

    // ==========================================
    // 3. Update Product (/api/product/update)
    // ==========================================

    @Test
    @DisplayName("updateProduct - Success returns 200 OK")
    void updateProduct_success() throws Exception {
        String idempotencyKey = "test-idempotency-key";
        RequestUpdateProductDTO requestDTO = new RequestUpdateProductDTO();
        requestDTO.setId(1);
        requestDTO.setName("Updated Product");
        requestDTO.setPrice(new BigDecimal("29.99"));
        requestDTO.setDescription("Updated product description");

        ResponseProductDTO responseDTO = new ResponseProductDTO();
        responseDTO.setId(1);
        responseDTO.setName("Updated Product");
        responseDTO.setPrice(new BigDecimal("29.99"));
        responseDTO.setDescription("Updated product description");

        when(productService.updateProduct(eq(idempotencyKey), any(RequestUpdateProductDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/product/update")
                        .header("Idempotency-Key", idempotencyKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.content.name").value("Updated Product"));

        verify(productService).updateProduct(eq(idempotencyKey), any(RequestUpdateProductDTO.class));
    }

    @Test
    @DisplayName("updateProduct - Missing Idempotency-Key header returns 400 Bad Request")
    void updateProduct_missingIdempotencyKey_returns400() throws Exception {
        RequestUpdateProductDTO requestDTO = new RequestUpdateProductDTO();
        requestDTO.setId(1);
        requestDTO.setName("Updated Product");
        requestDTO.setPrice(new BigDecimal("29.99"));
        requestDTO.setDescription("Updated product description");

        mockMvc.perform(post("/api/product/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Request header Idempotency-Key is required"));

        verify(productService, never()).updateProduct(any(), any());
    }

    @Test
    @DisplayName("updateProduct - Missing required name returns 400 Bad Request")
    void updateProduct_missingName_returns400() throws Exception {
        RequestUpdateProductDTO requestDTO = new RequestUpdateProductDTO();
        // name is null
        requestDTO.setId(1);
        requestDTO.setPrice(new BigDecimal("29.99"));
        requestDTO.setDescription("Updated product description");

        mockMvc.perform(post("/api/product/update")
                        .header("Idempotency-Key", "test-idempotency-key")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));

        verify(productService, never()).updateProduct(any(), any());
    }

    // ==========================================
    // 4. Delete Product (/api/product/delete)
    // ==========================================

    @Test
    @DisplayName("deleteProduct - Success returns 200 OK")
    void deleteProduct_success() throws Exception {
        RequestDeleteProductDTO requestDTO = new RequestDeleteProductDTO();
        requestDTO.setId(1);

        doNothing().when(productService).deleteProduct(any(RequestDeleteProductDTO.class));

        mockMvc.perform(post("/api/product/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.content").value("success"));

        verify(productService).deleteProduct(any(RequestDeleteProductDTO.class));
    }
}
