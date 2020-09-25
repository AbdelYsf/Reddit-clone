package com.abedlysf.reddit.clone.controller;

import com.abedlysf.reddit.clone.dto.AuthenticationResponse;
import com.abedlysf.reddit.clone.dto.LoginRequest;
import com.abedlysf.reddit.clone.dto.RegisterRequest;
import com.abedlysf.reddit.clone.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@AllArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    public final AuthService authService;
    @PostMapping("/signup")
    public ResponseEntity signup(@RequestBody RegisterRequest registerRequest){
            authService.signup(registerRequest);
            return new ResponseEntity<>("User Registration successful ", HttpStatus.OK);
    }

    @GetMapping("accountVerification/{token}")
    public ResponseEntity<String>  verifyAccount(@PathVariable String token){
        authService.verifyAccount(token);
        return new ResponseEntity<String>("Account Acitivated successfully ",HttpStatus.OK);
    }

    @PostMapping("/login")
    public AuthenticationResponse login(@RequestBody LoginRequest loginRequest){
         return authService.login(loginRequest);

    }
}
