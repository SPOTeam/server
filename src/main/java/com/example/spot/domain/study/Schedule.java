package com.example.spot.domain.study;
import com.example.spot.domain.common.BaseEntity;
import com.example.spot.domain.enums.Period;
import jakarta.persistence.*;

import java.time.LocalDateTime;

import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Getter
@Builder
@DynamicUpdate
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Schedule extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id", nullable = false)
    private Study study;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private LocalDateTime startedAt;

    @Column(nullable = false)
    private LocalDateTime finishedAt;

    @Column(nullable = false, columnDefinition = "BIT DEFAULT 0")
    private Boolean isAllDay;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Period period;

/* ----------------------------- 생성자 ------------------------------------- */

    @Builder
    public Schedule(String title, String location,
                    LocalDateTime staredAt, LocalDateTime finishedAt,
                    Boolean isAllDay, Period period) {
        this.title = title;
        this.location = location;
        this.startedAt = staredAt;
        this.finishedAt = finishedAt;
        this.isAllDay = isAllDay;
        this.period = period;
    }

}
