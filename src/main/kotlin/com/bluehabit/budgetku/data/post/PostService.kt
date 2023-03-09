/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.budgetku.data.post

import com.bluehabit.budgetku.common.Constants
import com.bluehabit.budgetku.data.BaseService
import com.bluehabit.budgetku.data.user.userCredential.UserCredentialRepository
import org.springframework.context.support.ResourceBundleMessageSource
import org.springframework.stereotype.Service
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

@Service
class PostService(
    override val userCredentialRepository: UserCredentialRepository,
    override val i18n: ResourceBundleMessageSource,
    override val errorCode: Int = Constants.ErrorCode.CODE_POST,
    override val inMemorySseEmitterRepository: InMemorySseEmitterRepository
):BaseService() {



}