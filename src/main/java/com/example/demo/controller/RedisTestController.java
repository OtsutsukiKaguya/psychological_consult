package com.example.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/redis/test")
@RequiredArgsConstructor
public class RedisTestController {

    private final StringRedisTemplate redis;

    @GetMapping("/set")
    public String set(@RequestParam String key, @RequestParam String value) {
        redis.opsForValue().set(key, value);
        return "✅ Set key: " + key + ", value: " + value;
    }

    @GetMapping("/get")
    public String get(@RequestParam String key) {
        String value = redis.opsForValue().get(key);
        return "🔍 Get key: " + key + ", value: " + value;
    }
}
