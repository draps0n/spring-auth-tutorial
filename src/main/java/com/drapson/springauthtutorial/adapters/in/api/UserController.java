package com.drapson.springauthtutorial.adapters.in.api;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SecurityRequirement(name = "accessToken")
@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    @GetMapping
    public String getAllUsers() {
        return "Secured list of users";
    }
}
