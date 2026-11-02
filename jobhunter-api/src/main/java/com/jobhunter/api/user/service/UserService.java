package com.jobhunter.api.user.service;


import com.jobhunter.api.user.entity.User;
import com.jobhunter.api.user.exception.EmailAlreadyExistsException;
import com.jobhunter.api.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(User user) {

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new EmailAlreadyExistsException(user.getEmail());
        }

        try {
            return userRepository.saveAndFlush(user);
        } catch (DataIntegrityViolationException exception) {
            if (isDuplicateEmail(exception)) {
                throw new EmailAlreadyExistsException(user.getEmail());
            }

            throw exception;
        }
    }

    private boolean isDuplicateEmail(DataIntegrityViolationException exception) {
        Throwable cause = exception;

        while (cause != null) {
            if (cause.getMessage() != null
                    && cause.getMessage().contains("uk_users_email")) {
                return true;
            }

            cause = cause.getCause();
        }

        return false;
    }
}
