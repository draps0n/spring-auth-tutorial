package com.drapson.springauthtutorial.adapters.in.api;

import com.drapson.springauthtutorial.adapters.in.api.response.CurrentUserInfoResponse;
import com.drapson.springauthtutorial.adapters.in.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SecurityRequirement(name = "accessToken")
@RestController
@RequestMapping("/api/v1/me")
public class CurrentUserController {
    @GetMapping
    public ResponseEntity<CurrentUserInfoResponse> getAllUsers() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return ResponseEntity.ok(new CurrentUserInfoResponse(
                userDetails.getUsername()
        ));
    }
}
