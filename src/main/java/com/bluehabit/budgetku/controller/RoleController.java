/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.budgetku.controller;

import com.bluehabit.budgetku.common.BaseResponse;
import com.bluehabit.budgetku.component.role.entity.Permission;
import com.bluehabit.budgetku.component.role.entity.PermissionGroup;
import com.bluehabit.budgetku.component.role.model.CreateRoleRequest;
import com.bluehabit.budgetku.component.role.model.DeleteRolesRequest;
import com.bluehabit.budgetku.component.role.service.RoleService;
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
    public ResponseEntity<BaseResponse<Page<PermissionGroup>>> getRoles(
            Pageable pageable
    ){
        return roleService.getRole(pageable);
    }

    @GetMapping(
            path = "/api/v1/role/search",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<BaseResponse<List<PermissionGroup>>> searchRole(
           @RequestParam("query") String query
    ){
        return roleService.searchRole(query);
    }

    @PutMapping(
            path = "/api/v1/role/{roleId}",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<BaseResponse<PermissionGroup>> updateRole(
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
    public ResponseEntity<BaseResponse<PermissionGroup>> createNewRole(
            @Valid @RequestBody CreateRoleRequest request
            ){
        return roleService.createNewRole(request);
    }

    @DeleteMapping(
            path = "/api/v1/role/{roleId}",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<BaseResponse<Object>> deleteRole(
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
