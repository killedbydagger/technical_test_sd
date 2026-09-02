package com.temp.demo.service;

import com.temp.demo.dto.request.RequestCreateProductDTO;
import com.temp.demo.dto.request.RequestDeleteProductDTO;
import com.temp.demo.dto.request.RequestUpdateProductDTO;
import com.temp.demo.dto.response.ResponseCustomPaging;
import com.temp.demo.dto.response.ResponseProductDTO;
import com.temp.demo.entity.Product;
import com.temp.demo.exception.DataNotFoundException;
import com.temp.demo.repository.ProductRepository;
import com.temp.demo.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    // test
    public ResponseCustomPaging<ResponseProductDTO> getProduct(String name, BigDecimal minPrice, BigDecimal maxPrice,
                                                                   String sortBy, String sortOrder, int pageNumber, int pageSize) {

        Sort sort = Sort.by(Sort.Direction.fromString(sortOrder.toUpperCase()), sortBy);
        PageRequest pageRequest = PageRequest.of(pageNumber-1, pageSize, sort);
        Page<Product> pages = productRepository.findProductPaging(name, minPrice, maxPrice, pageRequest);
        return new ResponseCustomPaging<>(pages.map(this::convert));
    }

    public ResponseProductDTO createProduct(RequestCreateProductDTO createProductDTO) {
        Product product = new Product();
        product.setId(0);
        product.setName(createProductDTO.getName());
        product.setPrice(createProductDTO.getPrice());
        product.setDescription(createProductDTO.getDescription());
        product.setCreatedAt(Constants.getTimestamp(Boolean.TRUE));
        product.setDeleted(Boolean.FALSE);
        return convert(productRepository.save(product));
    }

    public ResponseProductDTO updateProduct(RequestUpdateProductDTO updateProductDTO) {
        Optional<Product> findById = productRepository.findById(updateProductDTO.getId());
        if(!findById.isPresent())
            throw new DataNotFoundException("Product not found");

        Product product = findById.get();
        product.setName(updateProductDTO.getName());
        product.setPrice(updateProductDTO.getPrice());
        product.setDescription(updateProductDTO.getDescription());
        product.setUpdatedAt(Constants.getTimestamp(Boolean.TRUE));
        return convert(productRepository.save(product));
    }

    public void deleteProduct(RequestDeleteProductDTO deleteProductDTO) {
        Optional<Product> findById = productRepository.findById(deleteProductDTO.getId());
        if(!findById.isPresent())
            throw new DataNotFoundException("Product not found");

        Product product = findById.get();
        product.setDeleted(Boolean.TRUE);
        product.setUpdatedAt(Constants.getTimestamp(Boolean.TRUE));
        productRepository.save(product);
    }

    private ResponseProductDTO convert(Product product) {
        ResponseProductDTO dto = new ResponseProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setPrice(product.getPrice());
        dto.setDescription(product.getDescription());
        dto.setCreatedAt(product.getCreatedAt());
        dto.setUpdatedAt(product.getUpdatedAt());
        return dto;
    }
}
