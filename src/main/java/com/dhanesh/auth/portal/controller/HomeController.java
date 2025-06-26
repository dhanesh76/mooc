package com.dhanesh.auth.portal.controller;

import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequiredArgsConstructor
public class HomeController {
    
    @GetMapping("/")
    public String home() {
        return "home";
    }
}
