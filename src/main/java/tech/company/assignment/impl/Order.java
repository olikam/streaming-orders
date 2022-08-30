package tech.company.assignment.impl;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.Instant;

public record Order(
  String orderId,
  String orderStatus,
  Delivery delivery,
  Integer amount) {

  public record Delivery(String deliveryId,
                         @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", timezone = "UTC")
                         Instant deliveryTime) {

  }
}
