package com.ld.springsecurity.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/teacher")

public class ManagerController {
    @GetMapping()
    public String helloWorld1Teacher(){
        return "Hello World Teacher GET";
    }
    @PostMapping()
    public String helloWorld2Teacher(){
        return "Hello World Teacher POST";
    }
    @PutMapping()
    public String helloWorld3Teacher(){
        return "Hello World Teacher PUT";
    }
    @DeleteMapping()
    public String helloWorld4Teacher(){
        return "Hello World Teacher DELETE";
    }
}
