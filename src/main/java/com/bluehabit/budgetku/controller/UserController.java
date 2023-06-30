package com.bluehabit.budgetku.controller;

import com.bluehabit.budgetku.common.BaseResponse;
import com.bluehabit.budgetku.entity.UserProfile;
import com.bluehabit.budgetku.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController()
public class UserController {
    @Autowired
    private UserService userService;



    @GetMapping(
            path = "/api/v1/users",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<BaseResponse<List<UserProfile>>> getUsers(Pageable pageable) {
        return userService.getUsers(pageable);
    }
}
