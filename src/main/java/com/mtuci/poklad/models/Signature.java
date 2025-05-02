package com.mtuci.poklad.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "signatures") // лучше использовать множественное число
public class Signature {
    @Id
    @GeneratedValue
    private UUID id;

    private String threatName;

    private Long user_id;

    private String firstBytes; // храним в Base64

    private String remainderHash;

    private int remainderLength;

    private String fileType;

    private int offsetStart;

    private int offsetEnd;

    @Column(length = 512)
    private String digitalSignature;

    private LocalDateTime updatedAt;

    private String status;

    @Version
    private Long version;
}
