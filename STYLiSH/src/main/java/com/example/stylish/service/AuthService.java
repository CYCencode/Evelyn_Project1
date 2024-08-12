package com.example.stylish.service;

import com.example.stylish.dao.UserDao;
import com.example.stylish.dto.SigninRequest;
import com.example.stylish.dto.SignupRequest;
import com.example.stylish.exception.EmailAlreadyExistsException;
import com.example.stylish.exception.InvalidUserException;
import com.example.stylish.exception.ServerException;
import com.example.stylish.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;
    private final FacebookService facebookService;

    public AuthService(UserDao userDao, PasswordEncoder passwordEncoder, FacebookService facebookService) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
        this.facebookService = facebookService;
    }

    public User createUser(SignupRequest signupRequest) {
        log.info("收到註冊請求: {}", signupRequest.getEmail());
        try {
            // first,check if email already exist
            if (existsByEmail(signupRequest.getEmail())) {
                log.warn("註冊失敗 : 該信箱已被註冊: {}", signupRequest.getEmail());
                throw new EmailAlreadyExistsException("email already exists");
            }
            // if lack of parameter
            if (signupRequest.getEmail() == null || signupRequest.getPassword() == null || signupRequest.getName() == null) {
                log.error("註冊失敗 : 參數不完整，無法創建用戶: {}", signupRequest.getEmail());
                throw new IllegalArgumentException("lack of parameters");
            }
            return registerUser(signupRequest);
        } catch (Exception e) {
            log.info("登入失敗 : 伺服器錯誤: {}", signupRequest, e);
            throw new ServerException("Server error occurred");
        }
    }

    public User registerUser(SignupRequest signupRequest) {
        log.debug("嘗試註冊新用戶，信箱: {}", signupRequest.getEmail());
        if (existsByEmail(signupRequest.getEmail())) {
            log.warn("註冊失敗，該信箱已被註冊: {}", signupRequest.getEmail());
            return null;
        }

        User user = new User();
        user.setName(signupRequest.getName());
        user.setEmail(signupRequest.getEmail());
        user.setProvider("native");
        user.setPicture("");
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user = userDao.save(user);

        log.info("用戶註冊成功，user ID: {}", user.getId());
        return user;
    }

    public boolean existsByEmail(String email) {
        return userDao.existsByEmail(email);
    }

    public User validateUser(SigninRequest signinRequest) {
        log.info("收到登入請求: {}", signinRequest);
        try {
            if (signinRequest.getProvider() == null) {
                log.warn("登入失敗 : 參數格式不符合規範: {}", signinRequest);
                throw new IllegalArgumentException("lack of parameters");
            }
            if ("native".equals(signinRequest.getProvider())) {
                if (signinRequest.getEmail() == null || signinRequest.getPassword() == null) {
                    log.warn("登入失敗 : 參數格式不符合規範: {}", signinRequest);
                    throw new IllegalArgumentException("lack of parameters");
                }
                User user = validatePassword(signinRequest.getEmail(), signinRequest.getPassword());

                if (user == null) {
                    log.warn("登入失敗 : 信箱或密碼錯誤: {}", signinRequest.getEmail());
                    throw new InvalidUserException("Invalid email or password!");
                }
                return user;
            } else if ("facebook".equals(signinRequest.getProvider())) {
                if (signinRequest.getAccessToken() == null) {
                    log.warn("登入失敗 : 缺少 Facebook access token: {}", signinRequest);
                    throw new IllegalArgumentException("lack of parameters");
                }
                User user = facebookService.validateFacebookUser(signinRequest.getAccessToken());
                if (user == null) {
                    log.warn("登入失敗 : Facebook 驗證失敗: {}", signinRequest);
                    throw new InvalidUserException("Invalid access token!");
                }
                return user;
            } else {
                log.warn("登入失敗 : 未知的 provider: {}", signinRequest.getProvider());
                throw new IllegalArgumentException("invalid provider");
            }
        } catch (Exception e) {
            log.info("登入失敗 : 伺服器錯誤: {}", signinRequest, e);
            throw new ServerException("Server error occurred");
        }
    }

    public User validatePassword(String email, String password) {
        User user = userDao.findByEmail(email).orElse(null);
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            log.warn("驗證失敗，信箱或密碼錯誤: {}", email);
            return null;
        }
        return user;
    }
}
