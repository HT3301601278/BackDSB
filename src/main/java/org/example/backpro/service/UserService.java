package org.example.backpro.service;

import org.example.backpro.entity.User;
import org.example.backpro.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public User register(User user) {
        // 实现注册逻辑
        return userRepository.save(user);
    }

    public User login(String username, String password) {
        // 实现登录逻辑
        User user = userRepository.findByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }
}