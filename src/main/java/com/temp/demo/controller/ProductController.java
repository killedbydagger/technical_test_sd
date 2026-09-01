package com.temp.demo.controller;

import com.temp.demo.dto.request.RequestCreateProductDTO;
import com.temp.demo.dto.request.RequestDeleteProductDTO;
import com.temp.demo.dto.request.RequestUpdateProductDTO;
import com.temp.demo.dto.response.BasicResponse;
import com.temp.demo.dto.response.ResponseCustomPaging;
import com.temp.demo.dto.response.ResponseProductDTO;
import com.temp.demo.service.ProductService;
import com.temp.demo.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.math.BigDecimal;

@RestController
@RequestMapping(value = Constants.API_PATH + Constants.PRODUCT_PATH)
@Validated
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping(value = Constants.GET_PATH)
    @PreAuthorize(value = "hasAuthority('PRODUCT_READ')")
    public ResponseEntity<BasicResponse<ResponseCustomPaging<ResponseProductDTO>>> getProduct(@RequestParam(value = "name", required = false, defaultValue = "") String name,
                                                                                              @RequestParam(value = "min_price", required = false) BigDecimal minPrice,
                                                                                              @RequestParam(value = "max_price", required = false) BigDecimal maxPrice,
                                                                                              @RequestParam(value = "sort_by", required = false, defaultValue = "price") String sortBy,
                                                                                              @RequestParam(value = "sort_order", required = false, defaultValue = "asc") String sortOrder,
                                                                                              @RequestParam(value = "page") int pageNumber,
                                                                                              @RequestParam(value = "size") int pageSize) {
        BasicResponse<ResponseCustomPaging<ResponseProductDTO>> response = new BasicResponse<>();
        response.setSuccess(productService.getProduct(name, minPrice, maxPrice, sortBy, sortOrder, pageNumber, pageSize), "success");
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = Constants.CREATE_PATH)
    @PreAuthorize(value = "hasAuthority('PRODUCT_CREATE')")
    public ResponseEntity<BasicResponse<ResponseProductDTO>> createProduct(@Valid @RequestBody RequestCreateProductDTO createProductDTO) {
        BasicResponse<ResponseProductDTO> response = new BasicResponse<>();
        response.setSuccess(productService.createProduct(createProductDTO), "success");
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = Constants.UPDATE_PATH)
    @PreAuthorize(value = "hasAuthority('PRODUCT_UPDATE')")
    public ResponseEntity<BasicResponse<ResponseProductDTO>> updateProduct(@Valid @RequestBody RequestUpdateProductDTO updateProductDTO) {
        BasicResponse<ResponseProductDTO> response = new BasicResponse<>();
        response.setSuccess(productService.updateProduct(updateProductDTO), "success");
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = Constants.DELETE_PATH)
    @PreAuthorize(value = "hasAuthority('PRODUCT_DELETE')")
    public ResponseEntity<BasicResponse<String>> deleteProduct(@Valid @RequestBody RequestDeleteProductDTO deleteProductDTO) {
        BasicResponse<String> response = new BasicResponse<>();
        productService.deleteProduct(deleteProductDTO);
        response.setSuccess("success", "success");
        return ResponseEntity.ok(response);
    }
}
