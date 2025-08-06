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
@Table(name = "waitlists", schema = "pod_booking_system")
public class Waitlist {
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

    @NotNull
    @Column(name = "preferred_start_time", nullable = false)
    private Instant preferredStartTime;

    @NotNull
    @Column(name = "preferred_end_time", nullable = false)
    private Instant preferredEndTime;

    @ColumnDefault("0")
    @Column(name = "priority_score")
    private Integer priorityScore;

    @ColumnDefault("24")
    @Column(name = "max_wait_hours")
    private Integer maxWaitHours;

    @ColumnDefault("'waiting'")
    @Lob
    @Column(name = "status")
    private String status;

    @Column(name = "notified_at")
    private Instant notifiedAt;

    @NotNull
    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant createdAt;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at")
    private Instant updatedAt;

}