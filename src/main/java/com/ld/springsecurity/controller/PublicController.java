package com.ld.springsecurity.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
public class PublicController {

    @GetMapping("/public")
    public ResponseEntity<String> publicHello(){
        return ResponseEntity.ok("Hello World");
    }

}
