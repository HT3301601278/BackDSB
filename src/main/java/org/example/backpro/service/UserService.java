package org.example.backpro.service;

import org.example.backpro.entity.User;
import org.example.backpro.exception.ResourceNotFoundException;
import org.example.backpro.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public User register(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new IllegalArgumentException("用户名已存在");
        }
        return userRepository.save(user);
    }

    public User login(String username, String password) {
        return userRepository.findByUsername(username)
                .filter(user -> user.getPassword().equals(password))
                .orElse(null);
    }

    public User changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));

        if (!user.getPassword().equals(oldPassword)) {
            throw new IllegalArgumentException("旧密码不正确");
        }

        user.setPassword(newPassword);
        return userRepository.save(user);
    }
}
