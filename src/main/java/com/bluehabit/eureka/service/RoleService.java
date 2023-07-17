/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.eureka.service;

import com.bluehabit.eureka.common.BaseResponse;
import com.bluehabit.eureka.common.Constant;
import com.bluehabit.eureka.common.PermissionUtils;
import com.bluehabit.eureka.component.role.Permission;
import com.bluehabit.eureka.component.role.Role;
import com.bluehabit.eureka.component.role.model.DeleteRolesRequest;
import com.bluehabit.eureka.exception.GeneralErrorException;
import com.bluehabit.eureka.exception.UnAuthorizedException;
import com.bluehabit.eureka.component.role.model.CreateRoleRequest;
import com.bluehabit.eureka.component.role.RoleRepository;
import com.bluehabit.eureka.component.role.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class RoleService {
    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private RoleRepository roleRepository;

    public ResponseEntity<BaseResponse<Page<Permission>>> getPermissions(Pageable pageable) {
        return PermissionUtils.hasAccess(Constant.READ_ROLE)
                .map((granted) -> {
                    var findAllPermission = permissionRepository.findAll(pageable);

                    return BaseResponse.success("Success", findAllPermission);
                })
                .orElseThrow(() -> new UnAuthorizedException(1008, "User does not have access!"));


    }

    public ResponseEntity<BaseResponse<Page<Role>>> getRole(Pageable pageable) {
        return PermissionUtils.hasAccess(Constant.READ_ROLE)
                .map((granted) -> {
                    var findAllRole = roleRepository.findAll(pageable);

                    return BaseResponse.success("Success", findAllRole);
                })
                .orElseThrow(() -> new UnAuthorizedException(1008, "User does not have access!"));
    }

    public ResponseEntity<BaseResponse<List<Role>>> searchRole(String query) {
        return PermissionUtils.hasAccess(Constant.READ_ROLE)
                .map((granted) -> {
                    var findAllRole = roleRepository.searchByName(query);

                    return BaseResponse.success("Success", findAllRole);
                })
                .orElseThrow(() -> new UnAuthorizedException(1008, "User does not have access!"));
    }

    public ResponseEntity<BaseResponse<Role>> createNewRole(CreateRoleRequest createRoleRequest) {
        return PermissionUtils.hasAccess(Constant.READ_ROLE)
                .map((granted) -> {
                    List<Permission> permissions = new ArrayList<>();
                    var date = OffsetDateTime.now();
                    permissionRepository.findAllById(createRoleRequest.permission()).forEach(permissions::add);
                    var permissionGroup = new Role(
                            UUID.randomUUID().toString(),
                            createRoleRequest.roleName(),
                            createRoleRequest.roleDescription(),
                            permissions,
                            date,
                            date
                    );

                    var savedData = roleRepository.save(permissionGroup);
                    return BaseResponse.success("Success create role", savedData);
                })
                .orElseThrow(() -> new UnAuthorizedException(1008, "User does not have access!"));

    }

    public ResponseEntity<BaseResponse<Role>> updateNewRole(String roleId, CreateRoleRequest request) {
        return PermissionUtils.hasAccess(Constant.READ_ROLE)
                .map((granted) -> {

                    var findRole = roleRepository.findById(roleId);
                    if (findRole.isEmpty()) {
                        throw new GeneralErrorException(400, "Cannot find role!");
                    }

                    List<Permission> permissions = new ArrayList<>();
                    var date = OffsetDateTime.now();
                    permissionRepository.findAllById(request.permission()).forEach(permissions::add);

                    var role = findRole.get();
                    role.setUpdatedAt(date);
                    role.setRoleName(request.roleName());
                    role.setRoleDescription(request.roleDescription());
                    role.setRolePermission(permissions);

                    var savedData = roleRepository.save(role);
                    return BaseResponse.success("Success create role", savedData);
                })
                .orElseThrow(() -> new UnAuthorizedException(1008, "User does not have access!"));
    }

    public ResponseEntity<BaseResponse<String>> deleteRole(String roleId) {
        return PermissionUtils.hasAccess(Constant.WRITE_ROLE)
                .map((granted)->{
                    var findPermissionGroup = roleRepository.existsByIdIgnoreCase(roleId);
                    if (findPermissionGroup) {
                        throw new GeneralErrorException(100,"");
                    }
                    roleRepository.deleteById(roleId);
                    return BaseResponse.success("","");
                })
                .orElseThrow(()->new UnAuthorizedException(1008,""));
    }

    public ResponseEntity<BaseResponse<List<String>>> deleteRoles(DeleteRolesRequest request) {
        return PermissionUtils.hasAccess(Constant.WRITE_ROLE).map((granted)->{
            roleRepository.deleteAllById(request.ids());

            return BaseResponse.success("Success delete role", request.ids());
        }).orElseThrow(()->new UnAuthorizedException(1008, "User does not have access!"));

    }

}
