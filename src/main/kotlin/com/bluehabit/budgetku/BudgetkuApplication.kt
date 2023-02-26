/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.budgetku

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.support.ResourceBundleMessageSource
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
}

fun main(args: Array<String>) {
	runApplication<BudgetkuApplication>(*args)
}
