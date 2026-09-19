package com.temp.demo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.temp.demo.dto.request.RequestCreateProductDTO;
import com.temp.demo.dto.request.RequestDeleteProductDTO;
import com.temp.demo.dto.request.RequestUpdateProductDTO;
import com.temp.demo.dto.response.ResponseCustomPaging;
import com.temp.demo.dto.response.ResponseProductDTO;
import com.temp.demo.entity.IdempotencyKey;
import com.temp.demo.entity.Product;
import com.temp.demo.exception.DataErrorException;
import com.temp.demo.exception.DataNotFoundException;
import com.temp.demo.exception.TransactionConflictException;
import com.temp.demo.repository.ProductRepository;
import com.temp.demo.util.Constants;
import com.temp.demo.util.Hashing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private IdempotencyKeyService idempotencyKeyService;

    @Autowired
    private RedisManagementService redisManagementService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public ResponseCustomPaging<ResponseProductDTO> getProduct(String name, BigDecimal minPrice, BigDecimal maxPrice,
            String sortBy, String sortOrder, int pageNumber, int pageSize) {

        Sort sort = Sort.by(Sort.Direction.fromString(sortOrder.toUpperCase()), sortBy);
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, pageSize, sort);
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

    public ResponseProductDTO updateProduct(String idempotencyKey, RequestUpdateProductDTO updateProductDTO) throws JsonProcessingException {
        // check cached first
        String cachedResponse = redisManagementService.getValueFromRedis(idempotencyKey);
        if(!Objects.isNull(cachedResponse))
            return objectMapper.readValue(cachedResponse, ResponseProductDTO.class);

        // check database
        String hashedRequest = Hashing.sha256(objectMapper.writeValueAsString(updateProductDTO));
        Optional<IdempotencyKey> find = idempotencyKeyService.findByIdempotencyKey(idempotencyKey);
        if(find.isPresent()) {
            IdempotencyKey idempotencyKeyObj = find.get();
            if(!idempotencyKeyObj.getHashRequest().equals(hashedRequest))
                throw new TransactionConflictException("Idempotency-Key was already used with a different request");

            String response = idempotencyKeyObj.getResponse();
            redisManagementService.setValueToRedis(idempotencyKey, response, 10, TimeUnit.MINUTES);
            return objectMapper.readValue(response, ResponseProductDTO.class);
        }

        Optional<Product> findById = productRepository.findById(updateProductDTO.getId());
        if (!findById.isPresent())
            throw new DataNotFoundException("Product not found");

        Product product = findById.get();
        product.setName(updateProductDTO.getName());
        product.setPrice(updateProductDTO.getPrice());
        product.setDescription(updateProductDTO.getDescription());
        product.setUpdatedAt(Constants.getTimestamp(Boolean.TRUE));
        Product saved = productRepository.saveAndFlush(product);
        ResponseProductDTO convert = convert(saved);

        String response = objectMapper.writeValueAsString(convert);
        redisManagementService.setValueToRedis(idempotencyKey, response, 10, TimeUnit.MINUTES);
        idempotencyKeyService.save(idempotencyKey, hashedRequest, response);
        return convert;
    }

    public void deleteProduct(RequestDeleteProductDTO deleteProductDTO) {
        Optional<Product> findById = productRepository.findById(deleteProductDTO.getId());
        if (!findById.isPresent())
            throw new DataNotFoundException("Product not found");

        Product product = findById.get();
        if (product.isDeleted())
            throw new DataErrorException("Product is deleted");

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
