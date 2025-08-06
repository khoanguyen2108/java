package com.podbooking.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Map;

@Getter
@Setter
@Entity
@Table(name = "feedback", schema = "pod_booking_system")
public class Feedback {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pod_id", nullable = false)
    private Pod pod;

    @NotNull
    @Column(name = "overall_rating", nullable = false)
    private Integer overallRating;

    @Column(name = "cleanliness_rating")
    private Integer cleanlinessRating;

    @Column(name = "service_rating")
    private Integer serviceRating;

    @Column(name = "amenities_rating")
    private Integer amenitiesRating;

    @Lob
    @Column(name = "comment")
    private String comment;

    @Column(name = "photos")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> photos;

    @Lob
    @Column(name = "admin_response")
    private String adminResponse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responded_by")
    private User respondedBy;

    @Column(name = "responded_at")
    private Instant respondedAt;

    @ColumnDefault("1")
    @Column(name = "is_approved")
    private Boolean isApproved;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant createdAt;

}