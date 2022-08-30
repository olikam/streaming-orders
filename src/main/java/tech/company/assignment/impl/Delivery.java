package tech.company.assignment.impl;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import java.time.Instant;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Delivery {

  private String deliveryId;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", timezone = "UTC")
  private Instant deliveryTime;
  @Setter(AccessLevel.NONE)
  private DeliveryStatus deliveryStatus;
  @Setter(AccessLevel.NONE)
  private SortedSet<Order> orders;
  private Integer totalAmount;

  public void addOrder(Order order) {
    if (this.orders == null) {
      this.orders = new TreeSet<>();
    }
    this.orders.add(order);
    calculateTotalAmount(order);
    calculateStatus();
  }

  private void calculateTotalAmount(Order order) {
    if ("delivered".equals(order.getOrderStatus())) {
      if (this.totalAmount == null) {
        this.totalAmount = order.getAmount();
      } else {
        this.totalAmount += order.getAmount();
      }
    }
  }

  private void calculateStatus() {
    if (this.orders.stream().anyMatch(o -> "delivered".equals(o.getOrderStatus()))) {
      this.deliveryStatus = DeliveryStatus.DELIVERED;
    } else {
      this.deliveryStatus = DeliveryStatus.CANCELLED;
    }
  }

  public enum DeliveryStatus {
    CANCELLED, DELIVERED;

    @JsonValue
    public String toLowerCase() {
      return toString().toLowerCase();
    }
  }

  @Data
  @AllArgsConstructor
  public static class Order implements Comparable<Order> {

    private String orderId;
    private Integer amount;
    @JsonIgnore
    private String orderStatus;

    // Descending order
    @Override
    public int compareTo(Order o) {
      return o.getOrderId().compareTo(this.orderId);
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      Order order = (Order) o;
      return orderId.equals(order.orderId);
    }

    @Override
    public int hashCode() {
      return Objects.hash(orderId);
    }
  }
}
