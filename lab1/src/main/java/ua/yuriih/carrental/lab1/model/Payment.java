package ua.yuriih.carrental.lab1.model;

import java.math.BigDecimal;
import java.util.Objects;

public class Payment {
    private final int id;
    private final BigDecimal hrnAmount;
    private final int rentRequestId;
    private final int type;

    public static final int TYPE_REVENUE = 0;
    public static final int TYPE_REPAIR_COST = 1;
    public static final int TYPE_MAINTENANCE = 2;
    public static final int TYPE_REFUND = 3;
    public static final int TYPE_REPAIR_PAID_BY_CUSTOMER = 4;

    public Payment(Integer id, BigDecimal hrnAmount, int rentRequestId, int type) {
        this.id = id;
        this.hrnAmount = hrnAmount;
        this.rentRequestId = rentRequestId;
        this.type = type;
    }

    public BigDecimal getHrnAmount() {
        return hrnAmount;
    }

    public int getRentRequestId() {
        return rentRequestId;
    }

    public int getId() {
        return id;
    }

    public int getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payment payment = (Payment) o;
        return hrnAmount.equals(payment.hrnAmount) && rentRequestId == payment.rentRequestId && id == payment.id && type == payment.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(hrnAmount, rentRequestId, id, type);
    }

    @Override
    public String toString() {
        return "Payment{" +
                "id=" + id +
                ", hrnAmount=" + hrnAmount +
                ", rentRequestId=" + rentRequestId +
                ", type=" + type +
                '}';
    }
}
