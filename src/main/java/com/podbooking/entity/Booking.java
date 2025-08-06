package com.podbooking.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "bookings", schema = "pod_booking_system")
public class Booking {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pod_id", nullable = false)
    private Pod pod;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_package_id")
    private UserPackage userPackage;

    @Size(max = 20)
    @NotNull
    @Column(name = "booking_code", nullable = false, length = 20)
    private String bookingCode;

    @NotNull
    @Column(name = "start_time", nullable = false)
    private Instant startTime;

    @NotNull
    @Column(name = "end_time", nullable = false)
    private Instant endTime;

    @NotNull
    @Column(name = "duration_hours", nullable = false, precision = 4, scale = 2)
    private BigDecimal durationHours;

    @NotNull
    @Column(name = "base_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal baseAmount;

    @ColumnDefault("0.00")
    @Column(name = "service_amount", precision = 10, scale = 2)
    private BigDecimal serviceAmount;

    @NotNull
    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @ColumnDefault("'pending'")
    @Lob
    @Column(name = "status")
    private String status;

    @ColumnDefault("'pending'")
    @Lob
    @Column(name = "payment_status")
    private String paymentStatus;

    @Lob
    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "check_in_time")
    private Instant checkInTime;

    @Column(name = "check_out_time")
    private Instant checkOutTime;

    @Lob
    @Column(name = "special_requests")
    private String specialRequests;

    @Lob
    @Column(name = "cancellation_reason")
    private String cancellationReason;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant createdAt;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at")
    private Instant updatedAt;

}