package tech.company.assignment.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import tech.company.assignment.api.OrderStreamProcessor;

@Slf4j
public class OrderStreamProcessorImpl implements OrderStreamProcessor {

  private final int maxOrders;
  private final Duration maxTime;
  private final Map<String, Delivery> deliveryMap = new HashMap<>();
  private static final Set<String> ORDER_STATUSES_TO_BE_PROCESSED = Set.of(
    "delivered",
    "cancelled");

  public OrderStreamProcessorImpl(int maxOrders, Duration maxTime) {
    this.maxOrders = maxOrders;
    this.maxTime = maxTime;
  }

  @Override
  public void process(InputStream source, OutputStream sink) throws IOException {
    log.info("New OrderStreamProcessor starts with the params: maxOrders={}, maxTime={}ms",
      maxOrders, maxTime.toMillis());
    writeOutput(sink, processInput(source));
  }

  private List<Delivery> processInput(InputStream source) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(source));
    int tempMaxOrder = maxOrders;
    Instant start = Instant.now();
    while (tempMaxOrder > 0 && Duration.between(start, Instant.now()).compareTo(maxTime) < 0) {
      String line = reader.readLine();
      // If the source is empty, stop the process. (An assumption due to uncertainty in readme file)
      if (line == null) {
        break;
      }
      // Wait till the timeout expires as long as keep-alive message is received.
      // (An assumption due to uncertainty in readme file)
      if (line.isEmpty()) {
        continue;
      }
      Order order;
      try {
        order = JsonParser.convert(line);
      } catch (MessageParseException e) {
        // If an error occurs while parsing, some measures can be taken not to lose the order.
        // The options can be to store that line and/or to raise an alert.
        // Since I think it's out of the scope of this assignment, I skip it.
        log.error("Order cannot be parsed: {}", line);
        continue;
      }
      if (checkStatus(order.orderStatus())) {
        transformAndGroup(order);
      }
      --tempMaxOrder;
    }
    return sortDeliveries();
  }

  private boolean checkStatus(String orderStatus) {
    return ORDER_STATUSES_TO_BE_PROCESSED.contains(orderStatus);
  }

  private void transformAndGroup(Order order) {
    Delivery delivery = deliveryMap.computeIfAbsent(order.delivery().deliveryId(), (k) -> {
      Delivery tmpDelivery = new Delivery();
      tmpDelivery.setDeliveryId(order.delivery().deliveryId());
      tmpDelivery.setDeliveryTime(order.delivery().deliveryTime());
      return tmpDelivery;
    });
    delivery.addOrder(new Delivery.Order(order.orderId(), order.amount(), order.orderStatus()));
  }

  private List<Delivery> sortDeliveries() {
    return deliveryMap.values().stream()
      .sorted(Comparator.comparingLong(delivery -> delivery.getDeliveryTime().toEpochMilli()))
      .toList();
  }

  private void writeOutput(OutputStream sink, List<Delivery> deliveries) throws IOException {
    String output;
    try {
      output = JsonParser.convert(deliveries);
    } catch (MessageParseException e) {
      log.error("The result cannot be parsed: " + e.getMessage());
      throw new RuntimeException(e);
    }
    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(sink));
    writer.write(output);
    writer.flush();
  }
}
