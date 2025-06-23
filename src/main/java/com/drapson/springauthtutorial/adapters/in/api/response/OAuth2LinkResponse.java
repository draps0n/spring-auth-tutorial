package com.drapson.springauthtutorial.adapters.in.api.response;

import java.util.UUID;

public record OAuth2LinkResponse(
        String provider,
        String providerId,
        UUID userId
) {
}
