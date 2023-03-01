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
import org.springframework.web.servlet.i18n.SessionLocaleResolver
import java.util.*

@SpringBootApplication
class BudgetkuApplication{
	@Bean
	fun localResolver(): SessionLocaleResolver {
		val resolver = SessionLocaleResolver()
		resolver.setDefaultLocale(Locale.US)
		return resolver
	}

	@Bean
	fun bundleMessageSource(): ResourceBundleMessageSource {
		val base = ResourceBundleMessageSource()
		base.setBasename("message")

		return base
	}

	@Bean
	fun bcrypt():BCryptPasswordEncoder = BCryptPasswordEncoder(STRENGTH)

	@Bean
	fun scrypt():SCryptPasswordEncoder = SCryptPasswordEncoder(
		2,
		1,
		1,
		10,
		10
	)

}

fun main(args: Array<String>) {
	runApplication<BudgetkuApplication>(*args)
}
