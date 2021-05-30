package ua.yuriih.carrental.lab1.model;

import java.math.BigDecimal;
import java.util.Objects;

public class Payment {
    private final int id;
    private final BigDecimal hrnAmount;
    private final int rentRequestId;
    private final boolean isRepair;

    public Payment(Integer id, BigDecimal hrnAmount, int rentRequestId, boolean isRepair) {
        this.id = id;
        this.hrnAmount = hrnAmount;
        this.rentRequestId = rentRequestId;
        this.isRepair = isRepair;
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

    public boolean isRepair() {
        return isRepair;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payment payment = (Payment) o;
        return hrnAmount.equals(payment.hrnAmount) && rentRequestId == payment.rentRequestId && id == payment.id && isRepair == payment.isRepair;
    }

    @Override
    public int hashCode() {
        return Objects.hash(hrnAmount, rentRequestId, id, isRepair);
    }

    @Override
    public String toString() {
        return "Payment{" +
                "id=" + id +
                "hrnAmount=" + hrnAmount +
                ", rentRequestId=" + rentRequestId +
                ", isRepair=" + isRepair +
                '}';
    }
}
