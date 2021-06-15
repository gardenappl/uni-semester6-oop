package ua.yuriih.carrental.lab2.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "cars")
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private final String model;

    @Column(nullable = false)
    private final String manufacturer;

    @Column(name = "uah_per_day", nullable = false)
    private final BigDecimal uahPerDay;

    @Column(name = "thumbnail_url", nullable = false)
    private final String thumbnailUrl;

    @ManyToOne
    @JoinColumn(name = "current_user_id")
    private User user;

    @Column(nullable = false)
    private final String description;

    @Column(name = "uah_purchase", nullable = false)
    private final BigDecimal uahPurchase;
}
