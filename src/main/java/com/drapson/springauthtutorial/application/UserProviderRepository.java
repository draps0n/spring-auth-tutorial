package com.drapson.springauthtutorial.application;

import java.util.UUID;

public interface UserProviderRepository {
    boolean checkIfUserHasProvider(UUID id, String provider);
}
