package com.temp.demo.service;

import com.temp.demo.entity.IdempotencyKey;
import com.temp.demo.repository.IdempotencyKeyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class IdempotencyKeyService {

    @Autowired
    private IdempotencyKeyRepository idempotencyKeyRepository;

    public Optional<IdempotencyKey> findByIdempotencyKey(String idempotencyKey) {
        return idempotencyKeyRepository.findByIdempotencyKey(idempotencyKey);
    }

    public void save(String key, String hashRequest, String response) {
        IdempotencyKey idempotencyKey = new IdempotencyKey();
        idempotencyKey.setIdempotencyKey(key);
        idempotencyKey.setHashRequest(hashRequest);
        idempotencyKey.setResponse(response);
        idempotencyKey.setCreatedAt(System.currentTimeMillis());
        idempotencyKeyRepository.save(idempotencyKey);
    }
}
