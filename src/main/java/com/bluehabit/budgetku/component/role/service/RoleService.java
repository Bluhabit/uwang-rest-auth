/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.budgetku.component.role.service;

import com.bluehabit.budgetku.common.BaseResponse;
import com.bluehabit.budgetku.common.Constant;
import com.bluehabit.budgetku.common.PermissionUtils;
import com.bluehabit.budgetku.component.role.entity.Permission;
import com.bluehabit.budgetku.component.role.entity.PermissionGroup;
import com.bluehabit.budgetku.component.role.model.DeleteRolesRequest;
import com.bluehabit.budgetku.exception.GeneralErrorException;
import com.bluehabit.budgetku.exception.UnAuthorizedException;
import com.bluehabit.budgetku.component.role.model.CreateRoleRequest;
import com.bluehabit.budgetku.component.role.repo.PermissionGroupRepository;
import com.bluehabit.budgetku.component.role.repo.PermissionRepository;
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
    private PermissionGroupRepository permissionGroupRepository;

    public ResponseEntity<BaseResponse<Page<Permission>>> getPermissions(Pageable pageable){
        var hasAccess = PermissionUtils.hasAccess(Constant.READ_ROLE);
        if(!hasAccess){
           throw new UnAuthorizedException(1008,"User does not have access!");
        }

        var findAllPermission = permissionRepository.findAll(pageable);

        return BaseResponse.success("Success",findAllPermission);
    }

    public ResponseEntity<BaseResponse<Page<PermissionGroup>>> getRole(Pageable pageable){
        var hasAccess = PermissionUtils.hasAccess(Constant.READ_ROLE);
        if(!hasAccess){
            throw new UnAuthorizedException(1008,"User does not have access!");
        }
        var findAllRole = permissionGroupRepository.findAll(pageable);

        return BaseResponse.success("Success",findAllRole);
    }

    public ResponseEntity<BaseResponse<List<PermissionGroup>>> searchRole(String query){
        var hasAccess = PermissionUtils.hasAccess(Constant.READ_ROLE);
        if(!hasAccess){
            throw new UnAuthorizedException(1008,"User does not have access!");
        }
        var findAllRole = permissionGroupRepository.searchByName(query);

        return BaseResponse.success("Success",findAllRole);
    }

    public ResponseEntity<BaseResponse<PermissionGroup>> createNewRole(CreateRoleRequest createRoleRequest){
        var hasAccess = PermissionUtils.hasAccess(Constant.WRITE_ROLE);
        if(!hasAccess){
            throw  new UnAuthorizedException(1008,"User does not have access!");
        }

        List<Permission> permissions = new ArrayList<>();
        var date = OffsetDateTime.now();
        permissionRepository.findAllById(createRoleRequest.permission()).forEach(permissions::add);
        var permissionGroup = new PermissionGroup(
                UUID.randomUUID().toString(),
                createRoleRequest.roleName(),
                createRoleRequest.roleDescription(),
                permissions,
                date,
                date
        );

        var savedData = permissionGroupRepository.save(permissionGroup);
        return BaseResponse.success("Success create role",savedData);
    }

    public ResponseEntity<BaseResponse<PermissionGroup>> updateNewRole(String roleId,CreateRoleRequest request){
        var hasAccess = PermissionUtils.hasAccess(Constant.WRITE_ROLE);
        if(!hasAccess){
            throw  new UnAuthorizedException(1008,"User does not have access!");
        }

        var findRole = permissionGroupRepository.findById(roleId);
        if(findRole.isEmpty()){
            throw  new GeneralErrorException(400,"Cannot find role!");
        }

        List<Permission> permissions = new ArrayList<>();
        var date = OffsetDateTime.now();
        permissionRepository.findAllById(request.permission()).forEach(permissions::add);

        var role = findRole.get();
        role.setUpdatedAt(date);
        role.setRoleName(request.roleName());
        role.setRoleDescription(request.roleDescription());
        role.setRolePermission(permissions);

        var savedData = permissionGroupRepository.save(role);
        return BaseResponse.success("Success create role",savedData);
    }

    public ResponseEntity<BaseResponse<Object>> deleteRole(String roleId){
        var hasAccess = PermissionUtils.hasAccess(Constant.WRITE_ROLE);
        if(!hasAccess){
            throw new UnAuthorizedException(1008,"User does not have access!");
        }

        var findPermissionGroup = permissionGroupRepository.existsByRoleIdIgnoreCase(roleId);
        if(findPermissionGroup){
            throw new GeneralErrorException(1008,"Role doesn't exist");
        }

        permissionGroupRepository.deleteById(roleId);

        return BaseResponse.success("Success delete role","");
    }

    public  ResponseEntity<BaseResponse<List<String>>> deleteRoles(DeleteRolesRequest request){
        var hasAccess = PermissionUtils.hasAccess(Constant.WRITE_ROLE);
        if(!hasAccess){
            throw new UnAuthorizedException(1008,"User does not have access!");
        }

        permissionGroupRepository.deleteAllById(request.ids());

        return BaseResponse.success("Success delete role",request.ids());
    }

}
