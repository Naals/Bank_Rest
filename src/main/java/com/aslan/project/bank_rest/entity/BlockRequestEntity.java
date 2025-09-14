package com.aslan.project.bank_rest.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "block_requests")
@Getter
@Setter @NoArgsConstructor
public class BlockRequestEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Card card;

    private Long userId;
    private String status;

    private Instant createdAt;
}
