package com.urbanevents.events;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;

public record EventMetadata(
        @NotBlank
        @Size(max = 100)
        String eventId,

        @NotBlank
        @Size(max = 100)
        String eventType,

        @NotNull
        Instant timestamp,

        @NotBlank
        @Size(max = 100)
        String sourceService,

        @NotBlank
        @Size(max = 50)
        String schemaVersion,

        @Size(max = 50) //estado de la incidencia, opcional para algunos eventos
        String estado
        

) {
        public EventMetadata(String eventId,
                                                 String eventType,
                                                 Instant timestamp,
                                                 String sourceService,
                                                 String schemaVersion) {
                this(eventId, eventType, timestamp, sourceService, schemaVersion, null);
        }
}
