package ua.yuriih.carrental.lab1.model;

import java.time.LocalDate;
import java.util.Objects;

public final class RentRequest {
    private final int id;
    private final int status;
    private final String statusMessage;
    private final int passportId;
    private final int userId;
    private final int days;
    private final LocalDate startDate;
    private final LocalDate endDate;

    private static final int STATUS_PENDING = 0;
    private static final int STATUS_DENIED = 1;
    private static final int STATUS_ACTIVE = 2;
    private static final int STATUS_REPAIR_NEEDED = 3;

    public RentRequest(int id, int status, String statusMessage, int passportId, int userId, int days, LocalDate startDate) {
        this.id = id;
        this.status = status;
        this.statusMessage = statusMessage;
        this.passportId = passportId;
        this.userId = userId;
        this.days = days;
        this.startDate = startDate;
        this.endDate = startDate.plusDays(days);
    }

    public int getStatus() {
        return status;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public int getPassportId() {
        return passportId;
    }

    public int getUserId() {
        return userId;
    }

    public int getDays() {
        return days;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "RentRequest{" +
                "id=" + id +
                ", status=" + status +
                ", statusMessage='" + statusMessage + '\'' +
                ", passportId=" + passportId +
                ", userId=" + userId +
                ", days=" + days +
                ", startDate=" + startDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RentRequest that = (RentRequest) o;
        return id == that.id && status == that.status && passportId == that.passportId && userId == that.userId && days == that.days && Objects.equals(statusMessage, that.statusMessage) && startDate.equals(that.startDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, status, statusMessage, passportId, userId, days, startDate);
    }
}
