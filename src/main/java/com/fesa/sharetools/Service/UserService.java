package com.fesa.sharetools.Service;

import com.fesa.sharetools.Model.User;

public interface UserService extends BaseService<User, Long> {
    // métodos adicionais se necessário
    User save(User user);
    User authenticate(String email, String password);
}
