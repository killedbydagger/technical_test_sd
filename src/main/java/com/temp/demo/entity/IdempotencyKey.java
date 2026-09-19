package com.temp.demo.entity;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Data
@Entity
@Table(name = "idempotency_keys",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_idempotency_key", columnNames = "idempotency_key")
        }
)
public class IdempotencyKey {

        @Id
        @GeneratedValue(generator = "uuid-hibernate-generator")
        @GenericGenerator(name = "uuid-hibernate-generator", strategy = "org.hibernate.id.UUIDGenerator")
        private String id;

        @Column(name = "idempotency_key", nullable = false, unique = true)
        private String idempotencyKey;

        @Column(name = "hash_request", columnDefinition = "TEXT")
        private String hashRequest;

        @Column(name = "response", columnDefinition = "TEXT")
        private String response;

        @Column(name = "created_at", nullable = false)
        private Long createdAt;
}
