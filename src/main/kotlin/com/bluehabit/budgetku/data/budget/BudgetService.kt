/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.budgetku.data.budget

import com.bluehabit.budgetku.common.Constants.ErrorCode
import com.bluehabit.budgetku.common.Constants.Permission.WRITE_CATEGORY
import com.bluehabit.budgetku.common.exception.DataNotFoundException
import com.bluehabit.budgetku.common.model.BaseResponse
import com.bluehabit.budgetku.common.model.PagingDataResponse
import com.bluehabit.budgetku.common.model.baseResponse
import com.bluehabit.budgetku.common.utils.ValidationUtil
import com.bluehabit.budgetku.common.utils.allowTo
import com.bluehabit.budgetku.common.utils.getTodayDateTimeOffset
import com.bluehabit.budgetku.common.utils.slugify
import com.bluehabit.budgetku.data.BaseService
import com.bluehabit.budgetku.data.budget.budgetCategory.BudgetCategory
import com.bluehabit.budgetku.data.budget.budgetCategory.BudgetCategoryRepository
import com.bluehabit.budgetku.data.budget.budgetCategory.BudgetCategoryRequest
import com.bluehabit.budgetku.data.budget.budgetCategory.BudgetCategoryUpdateRequest
import com.bluehabit.budgetku.data.budget.budgetCategory.toResponse
import com.bluehabit.budgetku.data.user.userCredential.UserCredentialRepository
import org.springframework.context.support.ResourceBundleMessageSource
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service


@Service
class BudgetService(
    private val budgetCategoryRepository: BudgetCategoryRepository,
    private val validationUtil: ValidationUtil,
    override val userCredentialRepository: UserCredentialRepository,
    override val i18n: ResourceBundleMessageSource,
    override val errorCode: Int = ErrorCode.CODE_BUDGET
) : BaseService() {
    suspend fun getListBudgetCategory(
        pageable: Pageable
    ): BaseResponse<PagingDataResponse<BudgetCategory>> = buildResponse {

        val findAll = budgetCategoryRepository.findAll(pageable)

        baseResponse {
            code = HttpStatus.OK.value()
            data = findAll.toResponse()
            message = ""

        }
    }


    //region admin
    suspend fun createBudgetCategory(
        request: BudgetCategoryRequest
    ): BaseResponse<BudgetCategory> = buildResponse {
        validationUtil.validate(request)

        val createSlug = request.categoryName.orEmpty().slugify()
        val date = getTodayDateTimeOffset()

        val category = BudgetCategory(
            categoryId = null,
            categoryName = request.categoryName!!,
            categorySlug = createSlug,
            createdAt = date,
            updatedAt = date
        )

        val savedData = budgetCategoryRepository.save(category)

        baseResponse {
            code = HttpStatus.OK.value()
            data = savedData
            message = ""
        }
    }

    suspend fun updateBudgetCategory(
        categoryId: String,
        request: BudgetCategoryUpdateRequest
    ): BaseResponse<BudgetCategory> = buildResponse {
        validationUtil.validate(request)

        val findBudgetCategory = budgetCategoryRepository.findByIdOrNull(categoryId)
            ?: throw DataNotFoundException("")

        val createSlug = request.categoryName.orEmpty().slugify()
        val updatedData = budgetCategoryRepository.save(
            findBudgetCategory.copy(
                categorySlug = createSlug,
                categoryName = request.categoryName!!,
                updatedAt = getTodayDateTimeOffset()
            )
        )
        baseResponse {
            code = HttpStatus.OK.value()
            data = updatedData
            message = ""
        }
    }

    suspend fun deleteCategory(
        categoryId: String
    ): BaseResponse<BudgetCategory> = buildResponse(
        checkAccess = { it.allowTo(WRITE_CATEGORY) }
    ) {
        val findCategory = budgetCategoryRepository.findByIdOrNull(categoryId)
            ?: throw DataNotFoundException("Not found")
        budgetCategoryRepository.deleteById(categoryId)

        baseResponse {

            code = HttpStatus.OK.value()
            data = findCategory
            message = "Sukses"

        }
    }
    //end region

}