package com.project.razorpay.merchant.entity;

import com.project.razorpay.common.entity.BaseEntity;
import com.project.razorpay.common.enums.EventAggregateType;
import com.project.razorpay.common.enums.OutboxStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OutboxEvent extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventAggregateType aggregateType;

    @Column(nullable = false)
    private UUID aggregateId;

    @Column(nullable = false, length = 50)
    private String eventType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    private Map<String, Object> payload;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OutboxStatus status = OutboxStatus.PENDING;

    @Builder.Default
    @Column(nullable = false)
    private Integer attempts = 0;

    @Column(length = 1000)
    private String lastError;

    private LocalDateTime publishedAt;
}
