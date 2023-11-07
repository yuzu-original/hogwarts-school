package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.stream.LongStream;

@Service
public class OtherService {
    private final Logger logger = LoggerFactory.getLogger(OtherService.class);

    public Long getSum() {
        // FIXME: bad practice
        return LongStream.iterate(1, a -> a + 1)
                .limit(1_000_000)
                .sum();
    }

    public Long getSumFast() {
        Long a = 1L;
        Long b = 1_000_000L;
        Long c = a + b;
        return (c - (1 - c % 2)) * (c / 2);
    }
}
