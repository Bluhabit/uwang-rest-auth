/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.budgetku.services;

import com.bluehabit.budgetku.common.BaseResponse;
import com.bluehabit.budgetku.common.Constant;
import com.bluehabit.budgetku.common.PermissionUtils;
import com.bluehabit.budgetku.entity.Permission;
import com.bluehabit.budgetku.repositories.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {
    @Autowired
    private PermissionRepository permissionRepository;

    public ResponseEntity<BaseResponse<List<Permission>>> getPermissions(Pageable pageable){
        var hasAccess = PermissionUtils.hasAccess(Constant.READ_ROLE);
        if(!hasAccess){
           return BaseResponse.unAuthorized(1008,"User does not have access!");
        }

        var findAllPermission = permissionRepository.findAll(pageable);

        return BaseResponse.success("Success",findAllPermission.getContent());
    }


}
