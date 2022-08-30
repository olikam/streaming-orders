package tech.company.assignment.impl;

import com.google.auto.service.AutoService;
import java.time.Duration;
import tech.company.assignment.api.OrderProcessorFactory;
import tech.company.assignment.api.OrderStreamProcessor;

@AutoService(OrderProcessorFactory.class)
public final class OrderProcessorFactoryImpl implements OrderProcessorFactory {

  @Override
  public OrderStreamProcessor createProcessor(int maxOrders, Duration maxTime) {
    return new OrderStreamProcessorImpl(maxOrders, maxTime);
  }
}
