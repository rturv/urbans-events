package com.urbanevents.events;

import java.time.Instant;

public record EventMetadata(
        String eventId,
        String eventType,
        Instant timestamp,
        String sourceService,
        String schemaVersion
) {
}
