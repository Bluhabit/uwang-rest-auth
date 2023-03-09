/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.budgetku.common.utils

import java.text.Normalizer
import java.util.*


fun String.slugify(replacement: String = "-") = Normalizer
    .normalize(this, Normalizer.Form.NFD)
    .replace("[^\\p{ASCII}]".toRegex(), "")
    .replace("[^a-zA-Z0-9\\s]+".toRegex(), "").trim()
    .replace("\\s+".toRegex(), replacement)
    .lowercase(Locale.getDefault())

