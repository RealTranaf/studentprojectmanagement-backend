package com.ld.springsecurity.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @GetMapping()
    public String helloWorld1Admin(){
        return "Hello World Admin GET";
    }
    @PostMapping()
    public String helloWorld2Admin(){
        return "Hello World Admin POST";
    }
    @PutMapping()
    public String helloWorld3Admin(){
        return "Hello World Admin PUT";
    }
    @DeleteMapping()
    public String helloWorld4Admin(){
        return "Hello World Admin DELETE";
    }
}
