package com.drapson.springauthtutorial.application;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public interface TempUserDataPort {
    void save(String key, Object data, Duration timeout);
    Object get(String key);
    void delete(String key);
}
