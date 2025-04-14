package com.ld.springsecurity.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/manager")

public class ManagerController {
    @GetMapping()
    public String helloWorld1Manager(){
        return "Hello World Manager GET";
    }
    @PostMapping()
    public String helloWorld2Manager(){
        return "Hello World Manager POST";
    }
    @PutMapping()
    public String helloWorld3Manager(){
        return "Hello World Manager PUT";
    }
    @DeleteMapping()
    public String helloWorld4Manager(){
        return "Hello World Manager DELETE";
    }
}
