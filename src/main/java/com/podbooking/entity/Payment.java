package com.podbooking.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

@Getter
@Setter
@Entity
@Table(name = "payments", schema = "pod_booking_system")
public class Payment {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_package_id")
    private UserPackage userPackage;

    @Size(max = 50)
    @NotNull
    @Column(name = "payment_reference", nullable = false, length = 50)
    private String paymentReference;

    @NotNull
    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Size(max = 3)
    @ColumnDefault("'VND'")
    @Column(name = "currency", length = 3)
    private String currency;

    @NotNull
    @Lob
    @Column(name = "method", nullable = false)
    private String method;

    @ColumnDefault("'pending'")
    @Lob
    @Column(name = "status")
    private String status;

    @Lob
    @Column(name = "gateway")
    private String gateway;

    @Size(max = 255)
    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "gateway_response")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> gatewayResponse;

    @Column(name = "processed_at")
    private Instant processedAt;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant createdAt;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at")
    private Instant updatedAt;

}