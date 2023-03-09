/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.budgetku

import com.bluehabit.budgetku.common.Constants.BCrypt.STRENGTH
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.support.ResourceBundleMessageSource
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.i18n.SessionLocaleResolver
import java.util.*

@SpringBootApplication
@Controller
class BudgetkuApplication{
	@GetMapping
	fun index()= "index"
}

fun main(args: Array<String>) {
	runApplication<BudgetkuApplication>(*args)
}
