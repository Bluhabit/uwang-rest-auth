/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.eureka.services.user;

import com.bluehabit.eureka.common.BaseResponse;
import com.bluehabit.eureka.common.AbstractBaseService;
import com.bluehabit.eureka.common.Constant;
import com.bluehabit.eureka.common.PermissionUtils;
import com.bluehabit.eureka.component.role.Permission;
import com.bluehabit.eureka.component.role.PermissionRepository;
import com.bluehabit.eureka.component.role.Role;
import com.bluehabit.eureka.component.role.RoleRepository;
import com.bluehabit.eureka.component.role.model.CreateRoleRequest;
import com.bluehabit.eureka.component.role.model.DeleteRolesRequest;
import com.bluehabit.eureka.exception.GeneralErrorException;
import com.bluehabit.eureka.exception.UnAuthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RoleServiceAbstract extends AbstractBaseService {
    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private RoleRepository roleRepository;

    public ResponseEntity<BaseResponse<Page<Permission>>> getPermissions(Pageable pageable) {
        return PermissionUtils.hasAccess(Constant.READ_ROLE)
                .map(granted -> {
                    final Page<Permission> findAllPermission = permissionRepository.findAll(pageable);

                    return BaseResponse.success(translate("auth.success"), findAllPermission);
                })
                .orElseThrow(() -> new UnAuthorizedException(Constant.BKA_1008, "User does not have access!"));
    }

    public ResponseEntity<BaseResponse<Page<Role>>> getRole(Pageable pageable) {
        return PermissionUtils.hasAccess(Constant.READ_ROLE)
                .map(granted -> {
                    final Page<Role> findAllRole = roleRepository.findAll(pageable);

                    return BaseResponse.success(translate("auth.success"), findAllRole);
                })
                .orElseThrow(() -> new UnAuthorizedException(Constant.BKA_1008, "User does not have access!"));
    }

    public ResponseEntity<BaseResponse<List<Role>>> searchRole(String query) {
        return PermissionUtils.hasAccess(Constant.READ_ROLE)
                .map(granted -> {
                    final List<Role> findAllRole = roleRepository.searchByName(query);

                    return BaseResponse.success(translate("auth.success"), findAllRole);
                })
                .orElseThrow(() -> new UnAuthorizedException(Constant.BKA_1008, "User does not have access!"));
    }

    public ResponseEntity<BaseResponse<Role>> createNewRole(CreateRoleRequest createRoleRequest) {
        return PermissionUtils.hasAccess(Constant.READ_ROLE)
                .map(granted -> {
                    final List<Permission> permissions = new ArrayList<>();
                    final OffsetDateTime date = OffsetDateTime.now();
                    permissionRepository.findAllById(createRoleRequest.permission()).forEach(permissions::add);
                    final Role permissionGroup = new Role(
                            UUID.randomUUID().toString(),
                            createRoleRequest.roleName(),
                            createRoleRequest.roleDescription(),
                            permissions,
                            date,
                            date
                    );

                    final Role savedData = roleRepository.save(permissionGroup);
                    return BaseResponse.success(translate("auth.success"), savedData);
                })
                .orElseThrow(() -> new UnAuthorizedException(Constant.BKA_1008, "User does not have access!"));
    }

    public ResponseEntity<BaseResponse<Role>> updateNewRole(String roleId, CreateRoleRequest request) {
        return PermissionUtils.hasAccess(Constant.READ_ROLE)
                .map(granted -> {

                    final Optional<Role> findRole = roleRepository.findById(roleId);
                    if (findRole.isEmpty()) {
                        throw new GeneralErrorException(HttpStatus.BAD_REQUEST.value(), "Cannot find role!");
                    }

                    final List<Permission> permissions = new ArrayList<>();
                    final OffsetDateTime date = OffsetDateTime.now();
                    permissionRepository.findAllById(request.permission()).forEach(permissions::add);

                    final Role role = findRole.get();
                    role.setUpdatedAt(date);
                    role.setRoleName(request.roleName());
                    role.setRoleDescription(request.roleDescription());
                    role.setRolePermission(permissions);

                    final Role savedData = roleRepository.save(role);
                    return BaseResponse.success(translate("auth.success"), savedData);
                })
                .orElseThrow(() -> new UnAuthorizedException(Constant.BKA_1008, "User does not have access!"));
    }

    public ResponseEntity<BaseResponse<String>> deleteRole(String roleId) {
        return PermissionUtils.hasAccess(Constant.WRITE_ROLE)
                .map(granted -> {
                    final boolean findPermissionGroup = roleRepository.existsByIdIgnoreCase(roleId);
                    if (findPermissionGroup) {
                        throw new GeneralErrorException(HttpStatus.FORBIDDEN.value(), "");
                    }
                    roleRepository.deleteById(roleId);
                    return BaseResponse.success("", "");
                })
                .orElseThrow(() -> new UnAuthorizedException(Constant.BKA_1008, ""));
    }

    public ResponseEntity<BaseResponse<List<String>>> deleteRoles(DeleteRolesRequest request) {
        return PermissionUtils.hasAccess(Constant.WRITE_ROLE).map(granted -> {
            roleRepository.deleteAllById(request.ids());
            return BaseResponse.success(translate("auth.success"), request.ids());
        }).orElseThrow(() -> new UnAuthorizedException(Constant.BKA_1008, "User does not have access!"));
    }

}
