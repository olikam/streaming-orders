package tech.company.assignment.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

final class OrderStreamProcessorImplTest {

  @Test
  void processMethodShouldNotWaitForTimeout_IfNoMessageReceived()
    throws IOException {
    try (InputStream source = new ByteArrayInputStream("".getBytes(StandardCharsets.UTF_8));
      ByteArrayOutputStream sink = new ByteArrayOutputStream()) {
      // given
      Duration maxTime = Duration.ofMillis(100);

      // when
      Instant start = Instant.now();
      new OrderStreamProcessorImpl(5, maxTime).process(source, sink);
      Instant end = Instant.now();
      long actualElapsedTime = Duration.between(start, end).toMillis();

      // then
      Assertions.assertTrue(actualElapsedTime < maxTime.toMillis());
    }
  }

  @Test
  void processMethodShouldWaitForTimeout_AsLongAsItReceivesKeepAliveMessage()
    throws IOException {
    try (InputStream source = new ByteArrayInputStream(
      "\n".repeat(1000000).getBytes(StandardCharsets.UTF_8));
      ByteArrayOutputStream sink = new ByteArrayOutputStream()) {
      // given
      Duration maxTime = Duration.ofMillis(100);

      // when
      Instant start = Instant.now();
      new OrderStreamProcessorImpl(5, maxTime).process(source, sink);
      Instant end = Instant.now();
      long actualElapsedTime = Duration.between(start, end).toMillis();
      System.out.println("actualElapsedTime: " + actualElapsedTime);
      // then
      Assertions.assertTrue(actualElapsedTime >= 2 * maxTime.toMillis());
    }
  }
}