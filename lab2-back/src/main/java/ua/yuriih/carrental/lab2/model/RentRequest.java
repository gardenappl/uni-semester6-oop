package ua.yuriih.carrental.lab2.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "requests")
public class RentRequest {
    public static final int STATUS_PENDING = 0;
    public static final int STATUS_DENIED = 1;
    public static final int STATUS_ACTIVE = 3;
    public static final int STATUS_REPAIR_NEEDED = 4;
    public static final int STATUS_ENDED = 5;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private int status;

    @Column(name = "message")
    private String statusMessage;

    @Column(name = "user_id")
    private final Long userId;

    @Column
    private final int days;

    @ManyToOne
    @JoinColumn(name = "car_id")
    private final Car car;

    @Column(name = "start_date", nullable = false)
    private final LocalDate startDate;

    @Column(name = "payment_cost", nullable = false)
    private BigDecimal repairCost;

    public LocalDate getEndDate() {
        return startDate.plusDays(days);
    }
}