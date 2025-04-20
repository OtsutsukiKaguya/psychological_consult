package com.example.demo.controller;

import com.example.demo.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/db/test")
@RequiredArgsConstructor
public class DBTestController {

    private final UserRepository userRepository;

    @GetMapping("/count")
    public String countUsers() {
        long count = userRepository.count();
        return "✅ Total users in DB: " + count;
    }
}
