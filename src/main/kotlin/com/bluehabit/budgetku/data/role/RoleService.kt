/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.budgetku.data.role

import com.bluehabit.budgetku.common.Constants.ErrorCode
import com.bluehabit.budgetku.common.Constants.Permission.READ_ROLE
import com.bluehabit.budgetku.common.Constants.Permission.WRITE_ROLE
import com.bluehabit.budgetku.common.exception.DataNotFoundException
import com.bluehabit.budgetku.common.model.BaseResponse
import com.bluehabit.budgetku.common.model.PagingDataResponse
import com.bluehabit.budgetku.common.model.baseResponse
import com.bluehabit.budgetku.common.utils.ValidationUtil
import com.bluehabit.budgetku.common.utils.allowTo
import com.bluehabit.budgetku.common.utils.getTodayDateTimeOffset
import com.bluehabit.budgetku.data.BaseService
import com.bluehabit.budgetku.data.role.permission.PermissionRepository
import com.bluehabit.budgetku.data.role.roleGroup.RoleGroup
import com.bluehabit.budgetku.data.role.roleGroup.RoleGroupRepository
import com.bluehabit.budgetku.data.user.userCredential.UserCredentialRepository
import jakarta.transaction.Transactional
import org.springframework.context.support.ResourceBundleMessageSource
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class RoleService(
    override val i18n: ResourceBundleMessageSource,
    override val userCredentialRepository: UserCredentialRepository,
    override val errorCode: Int = ErrorCode.CODE_ROLE,
    private val roleGroupRepository: RoleGroupRepository,
    private val permissionRepository: PermissionRepository,
    private val validationUtil: ValidationUtil
) : BaseService() {
    @Transactional
    suspend fun getListRoleGroup(
        pageable: Pageable
    ): BaseResponse<PagingDataResponse<RoleGroup>> = buildResponse(
        checkAccess = { it.allowTo(READ_ROLE) }
    ) {

        val findAll = roleGroupRepository.findAll(pageable)
        baseResponse {
            code = HttpStatus.OK.value()
            data = findAll.toResponse()
            message = "Sukses"
        }
    }

    @Transactional
    suspend fun createNewRoleGroup(
        request: RoleRequest
    ): BaseResponse<RoleGroup> = buildResponse(
        checkAccess = { it.allowTo(WRITE_ROLE) }
    ) {
        validationUtil.validate(request)

        val findPermission = permissionRepository.findAllById(request.permissions.orEmpty())

        val date = getTodayDateTimeOffset()


        val roleGroup = RoleGroup(
            roleId = null,
            roleName = request.roleName!!,
            roleDescription = request.roleDescription!!,
            rolePermissions = findPermission.toList(),
            createdAt = date,
            updatedAt = date
        )
        val saveRole = roleGroupRepository.save(roleGroup)

        baseResponse {
            code = HttpStatus.OK.value()
            data = saveRole
            message = "Success"
        }
    }

    @Transactional
    suspend fun updateRoleGroup(
        roleId:String,
        request: RoleUpdateRequest
    ):BaseResponse<RoleGroup> = buildResponse(
        checkAccess = {it.allowTo(WRITE_ROLE)}
    ) {
        validationUtil.validate(request)

        val findRoleById = roleGroupRepository.findByIdOrNull(roleId)
            ?: throw DataNotFoundException(
                translate(""),
                errorDataNotFound
            )
        val findPermission = permissionRepository.findAllById(request.permissions.orEmpty())

        val updateRoleGroup = roleGroupRepository.save(
            findRoleById.copy(
                roleName = request.roleName!!,
                roleDescription = request.roleDescription!!,
                rolePermissions = findPermission.toList(),
                updatedAt = getTodayDateTimeOffset()
            )
        )

        baseResponse {
            code = HttpStatus.OK.value()
            data = updateRoleGroup
            message = "Success"
        }
    }

    @Transactional
    suspend fun deleteRole(
        roleId: String
    ):BaseResponse<RoleGroup> = buildResponse(
        checkAccess = {it.allowTo(WRITE_ROLE)}
    ) {
        val findRole = roleGroupRepository.findByIdOrNull(roleId)
            ?: throw DataNotFoundException(
                translate(""),
                errorDataNotFound
            )
        roleGroupRepository.deleteById(roleId)
        baseResponse {
            code = HttpStatus.OK.value()
            data = findRole
            message = "Success"
        }
    }
}