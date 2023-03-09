/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.budgetku.common.utils

import com.bluehabit.budgetku.data.user.userCredential.UserCredential
import org.apache.tika.detect.DefaultDetector
import org.apache.tika.metadata.Metadata
import org.apache.tika.mime.MediaType
import org.springframework.web.multipart.MultipartFile

fun MultipartFile?.getMimeTypes(): String {
    return try {
        val detector = DefaultDetector()
        val metadata = Metadata()

        val mediaType = detector.detect(this?.inputStream, metadata)

        mediaType.toString()
    } catch (e: Exception) {
        MediaType.OCTET_STREAM.toString()
    }
}

fun UserCredential.createFileName(mimeType: String): String {
    return this.userId?.plus(".").plus(mimeType)
}