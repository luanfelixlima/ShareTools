package com.fesa.sharetools.Controller;

import com.fesa.sharetools.Model.User;
import com.fesa.sharetools.Service.UserService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController extends BaseController<User, Long> {

    public UserController(UserService service) {
        super(service);
    }
}
