package com.example.project_gym.utilservices;

import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;
@Service
public class UserIdGenerator {
    private final AtomicLong idCounter = new AtomicLong(0L);

    public Long generateId(){
        return idCounter.incrementAndGet();
    }

    public void setInitialValue(long currentMaxId) {
        idCounter.updateAndGet(prev -> Math.max(prev, currentMaxId));
    }
}
