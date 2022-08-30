package tech.company.assignment.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.Collection;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@UtilityClass
@Slf4j
public class JsonParser {

  private final ObjectMapper OBJECT_MAPPER = ObjectMapperFactory.createObjectMapper();

  static {
    OBJECT_MAPPER.enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL)
      .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
      .registerModule(new JavaTimeModule())
      .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
  }

  /**
   * Converts JSON String to Order.
   *
   * @param orderJson JSON String representation of the order.
   * @return Order object
   * @throws MessageParseException If any error occurs during JSON parsing.
   */
  public Order convert(String orderJson) throws MessageParseException {
    try {
      return OBJECT_MAPPER.readValue(orderJson, Order.class);
    } catch (JsonProcessingException e) {
      log.error("Json parse error: " + e.getMessage());
      throw new MessageParseException(e.getMessage());
    }
  }

  /**
   * Converts any collection to a JSON String.
   *
   * @param collection To be converted.
   * @return String JSON
   * @throws MessageParseException If any error occurs during JSON parsing.
   */
  public String convert(Collection<?> collection) throws MessageParseException {
    try {
      return OBJECT_MAPPER.writeValueAsString(collection);
    } catch (JsonProcessingException e) {
      log.error("Json parse error: " + e.getMessage());
      throw new MessageParseException(e.getMessage());
    }
  }
}
