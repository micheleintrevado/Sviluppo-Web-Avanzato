
package org.univaq.swa.framework.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author Giuseppe
 */
public class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {
 
  private final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
 
  @Override
  public LocalDateTime deserialize(JsonParser p, DeserializationContext context) throws IOException {
    return LocalDateTime.parse(p.getValueAsString(), formatter);
  }
}