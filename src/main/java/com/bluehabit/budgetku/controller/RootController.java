package com.bluehabit.budgetku.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class RootController {

    @GetMapping
    public Map<String, String> index() {
        return Map.of("message", "Blue habit V1");
    }
}
