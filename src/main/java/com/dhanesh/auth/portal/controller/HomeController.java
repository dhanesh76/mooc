package com.dhanesh.auth.portal.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Hidden;

@Hidden
@RestController
public class HomeController {
    @GetMapping("/")
    public String home() {
        return "MOOC  Backend is Running";
    }
}
