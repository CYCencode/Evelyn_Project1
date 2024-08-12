package com.example.stylish.controller;

import com.example.stylish.dto.SigninRequest;
import com.example.stylish.dto.SignupRequest;
import com.example.stylish.exception.EmailAlreadyExistsException;
import com.example.stylish.exception.InvalidUserException;
import com.example.stylish.exception.ServerException;
import com.example.stylish.model.User;
import com.example.stylish.security.JwtTokenProvider;
import com.example.stylish.service.AuthService;
import com.example.stylish.service.ErrorWrapper;
import com.example.stylish.service.ResponseWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/1.0/user")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthController(AuthService authService, JwtTokenProvider jwtTokenProvider) {
        this.authService = authService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignupRequest signupRequest) {
        try {
            User user = authService.createUser(signupRequest);
            String jwt = jwtTokenProvider.createToken("native", user.getEmail(), user.getName(), "");
            return ResponseEntity.ok(ResponseWrapper.wrapSigninSignupResponse(jwt, 3600, user));
        } catch (EmailAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ErrorWrapper.wrapErrorMsg("Email already exists!"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ErrorWrapper.wrapErrorMsg("Could not register user"));
        } catch (ServerException e) {
            log.info("註冊失敗 : 伺服器錯誤: {}", signupRequest, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ErrorWrapper.wrapErrorMsg("Server error occurred while processing signup."));
        }
    }


    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody SigninRequest signinRequest) {
        try {
            User user = authService.validateUser(signinRequest);
            String jwt = jwtTokenProvider.createToken(user.getProvider(), user.getEmail(), user.getName(), user.getPicture());
            return ResponseEntity.ok(ResponseWrapper.wrapSigninSignupResponse(jwt, 3600, user));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ErrorWrapper.wrapErrorMsg("Please make sure all the parameter is correctly provided!"));
        } catch (InvalidUserException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ErrorWrapper.wrapErrorMsg("Invalid user!"));
        } catch (ServerException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ErrorWrapper.wrapErrorMsg("Server error occurred while processing sign in."));
        }
    }


}
