/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.budgetku.common.utils

import com.bluehabit.budgetku.common.exception.BadRequestException
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class FileStorageUtils(
    private val root:Path = Paths.get("uploads")
)  {

    init {
        try {
            Files.createDirectories(root)
        }catch (e:Exception){
            throw BadRequestException(e.message)
        }
    }

    fun save(file:MultipartFile,fileName:String){
        try {
            Files.copy(
                file.inputStream,
                root.resolve(
                    fileName
                )
            )
        }catch (e:Exception){
            if (e is FileAlreadyExistsException) {
                throw  RuntimeException("A file of that name already exists.");
            }

            throw  RuntimeException(e.message);
        }
    }
}