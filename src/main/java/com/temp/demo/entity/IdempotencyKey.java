package com.temp.demo.entity;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "idempotency_keys")
public class IdempotencyKey {

        @Id
        @GeneratedValue(generator = "uuid-hibernate-generator")
        @GenericGenerator(name = "uuid-hibernate-generator", strategy = "org.hibernate.id.UUIDGenerator")
        private String id;

        private String idempotencyKey;
        private String hashRequest;
        private String response;
        private Long createdAt;
}
