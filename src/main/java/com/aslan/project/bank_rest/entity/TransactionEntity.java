package com.aslan.project.bank_rest.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "transactions")
@Getter @Setter @NoArgsConstructor
public class TransactionEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Card fromCard;

    @ManyToOne
    private Card toCard;

    private Long amount;
    private Instant createdAt;
}
