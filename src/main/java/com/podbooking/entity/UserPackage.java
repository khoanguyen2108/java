package com.podbooking.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "user_packages", schema = "pod_booking_system")
public class UserPackage {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "package_id", nullable = false)
    private Package packageField;

    @Column(name = "remaining_hours")
    private Integer remainingHours;

    @Column(name = "remaining_days")
    private Integer remainingDays;

    @NotNull
    @Column(name = "expiry_date", nullable = false)
    private Instant expiryDate;

    @ColumnDefault("1")
    @Column(name = "is_active")
    private Boolean isActive;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "purchased_at")
    private Instant purchasedAt;

}