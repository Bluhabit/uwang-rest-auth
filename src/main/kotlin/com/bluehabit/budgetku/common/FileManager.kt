package com.bluehabit.budgetku.common

import com.aliyun.oss.OSSClientBuilder
import org.springframework.core.env.Environment
import org.springframework.web.multipart.MultipartFile
import java.io.ByteArrayInputStream
/**
 * File Manager
 * @author trian damai
 *
 * */
class FileManager(
    private val env: Environment
) {
    private val endPoint = env.getProperty("endPoint")
    private val accessKeyId = env.getProperty("accessKeyId")
    private val accessKeySecret = env.getProperty("accessKeySecret")
    private val bucketName = env.getProperty("bucketName")
    private val folderName = env.getProperty("folderName")

    fun uploadFile(
        file: MultipartFile?,
        fileName: String
    ): FileResult {
        if (file == null) {
            return FileResult(
                false,
                "",
                "File cannot empty!"
            )
        } else {
            val ossClient = OSSClientBuilder()
                .build(endPoint, accessKeyId, accessKeySecret)
            val fileObject = ByteArrayInputStream(file.bytes)
            val fileUploaded = ossClient
                .putObject(
                    bucketName,
                    "$folderName/$fileName",
                    fileObject
                )
            ossClient.shutdown()
            return if (fileUploaded != null) {
                FileResult(
                    true,
                    "https://$bucketName.$endPoint/$folderName/$fileName",
                    "File has been upload"
                )

            } else {
                FileResult(
                    false,
                    "",
                    "Failed upload file"
                )
            }

        }
    }

    fun deleteFile(fileName: String):Boolean{
        val ossClient = OSSClientBuilder()
            .build(endPoint, accessKeyId, accessKeySecret)

        return try {
            val delete =  ossClient.deleteObject(bucketName,"$folderName/$fileName")
            if(delete.response.isSuccessful)return true
            false
        }catch (e:Exception){
            false
        }finally {
            ossClient.shutdown()
        }
    }
}

data class FileResult(
    val success:Boolean,
    val url:String,
    val message:String
)