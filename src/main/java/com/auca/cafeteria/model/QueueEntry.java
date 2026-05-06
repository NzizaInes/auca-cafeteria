package com.auca.cafeteria.model;

import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "queue_entries")
public class QueueEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(nullable = false)
    private Integer queueNumber;

    private Integer estimatedWaitMinutes;

    @Column(nullable = false)
    private LocalDateTime joinedAt = LocalDateTime.now();
}