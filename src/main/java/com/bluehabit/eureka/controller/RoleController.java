/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.eureka.controller;

import com.bluehabit.eureka.common.BaseResponse;
import com.bluehabit.eureka.component.role.Permission;
import com.bluehabit.eureka.component.role.Role;
import com.bluehabit.eureka.component.role.model.CreateRoleRequest;
import com.bluehabit.eureka.component.role.model.DeleteRolesRequest;
import com.bluehabit.eureka.service.RoleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class RoleController {
    @Autowired
    private RoleService roleService;

    @GetMapping(
            path = "/api/v1/role/permission",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<BaseResponse<Page<Permission>>> getPermissions(
            Pageable pageable
    ){
        return roleService.getPermissions(pageable);
    }


    @GetMapping(
            path = "/api/v1/role",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<BaseResponse<Page<Role>>> getRoles(
            Pageable pageable
    ){
        return roleService.getRole(pageable);
    }

    @GetMapping(
            path = "/api/v1/role/search",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<BaseResponse<List<Role>>> searchRole(
           @RequestParam("query") String query
    ){
        return roleService.searchRole(query);
    }

    @PutMapping(
            path = "/api/v1/role/{roleId}",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<BaseResponse<Role>> updateRole(
            @PathVariable("roleId") String roleId,
            @Valid @RequestBody CreateRoleRequest request
    ){
        return roleService.updateNewRole(roleId,request);
    }

    @PostMapping(
            path = "/api/v1/role",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<BaseResponse<Role>> createNewRole(
            @Valid @RequestBody CreateRoleRequest request
            ){
        return roleService.createNewRole(request);
    }

    @DeleteMapping(
            path = "/api/v1/role/{roleId}",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<BaseResponse<String>> deleteRole(
           @PathVariable(name = "roleId") String roleId
    ){
        return roleService.deleteRole(roleId);
    }

    @PostMapping(
            path = "/api/v1/roles",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<BaseResponse<List<String>>> deleteRoles(
            @Valid @RequestBody DeleteRolesRequest request
            ){
        return roleService.deleteRoles((request));
    }
}
