package com.fesa.sharetools.Service;

import com.fesa.sharetools.Model.User;

import java.util.Optional;

public interface UserService extends BaseService<User, Long> {
    User save(User user);
    User authenticate(String email, String password);
    Optional<User> findByEmail(String email);
}
