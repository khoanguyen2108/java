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
@Table(name = "calendar_sync", schema = "pod_booking_system")
public class CalendarSync {
    @Id
    @Column(name = "user_id", nullable = false)
    private Integer id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User users;

    @NotNull
    @Lob
    @Column(name = "provider", nullable = false)
    private String provider;

    @Lob
    @Column(name = "access_token")
    private String accessToken;

    @Lob
    @Column(name = "refresh_token")
    private String refreshToken;

    @ColumnDefault("'inactive'")
    @Lob
    @Column(name = "sync_status")
    private String syncStatus;

    @Column(name = "last_sync_at")
    private Instant lastSyncAt;

    @ColumnDefault("'hourly'")
    @Lob
    @Column(name = "sync_frequency")
    private String syncFrequency;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant createdAt;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at")
    private Instant updatedAt;

}