package com.podbooking.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "daily_analytics", schema = "pod_booking_system")
public class DailyAnalytic {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @Column(name = "date_recorded", nullable = false)
    private LocalDate dateRecorded;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pod_id")
    private Pod pod;

    @ColumnDefault("0")
    @Column(name = "total_bookings")
    private Integer totalBookings;

    @ColumnDefault("0.00")
    @Column(name = "total_revenue", precision = 12, scale = 2)
    private BigDecimal totalRevenue;

    @ColumnDefault("0.00")
    @Column(name = "total_hours_booked", precision = 8, scale = 2)
    private BigDecimal totalHoursBooked;

    @ColumnDefault("0")
    @Column(name = "unique_customers")
    private Integer uniqueCustomers;

    @ColumnDefault("0")
    @Column(name = "vip_customers")
    private Integer vipCustomers;

    @ColumnDefault("0.00")
    @Column(name = "occupancy_rate", precision = 5, scale = 2)
    private BigDecimal occupancyRate;

    @ColumnDefault("0.00")
    @Column(name = "average_duration", precision = 4, scale = 2)
    private BigDecimal averageDuration;

    @ColumnDefault("0.00")
    @Column(name = "cancellation_rate", precision = 5, scale = 2)
    private BigDecimal cancellationRate;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant createdAt;

}