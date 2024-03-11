package com.balievent.telegrambot.model.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Accessors(chain = true)
@Getter
@Setter
@ToString

@Entity
@Table(name = "event")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id")
    private String externalId;

    @Column(name = "event_name")
    private String eventName;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "location_id", foreignKey = @ForeignKey(name = "fk_event_location"))
    @ToString.Exclude
    private Location location;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "event_url")
    private String eventUrl;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "service_name")
    private String serviceName;

    @Column(name = "coordinates")
    private String coordinates;
}
