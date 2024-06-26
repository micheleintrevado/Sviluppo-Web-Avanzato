package org.univaq.swa.framework.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author Giuseppe
 */
public class LocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {

    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

    @Override
    public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {

        gen.writeString(value.format(formatter));

    }
}
