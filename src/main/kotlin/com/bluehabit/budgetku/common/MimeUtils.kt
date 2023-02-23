package com.bluehabit.budgetku.common
fun String?.isFileSupported():Boolean{
    if(this == null){
        return false
    }
    if(this.isBlank()){
        return false
    }
    return MedicalRecordMimeType.any {
        mimeType ->
        mimeType.name == this
    }
}

fun getExtension(mimeType:String):String{
    return MedicalRecordMimeType.first {
        it.name == mimeType
    }.extension
}

fun createFileName(className:String,userCode:String,createdAt:Long,extension:String):String{
    return "$className-$userCode-$createdAt$extension"
}


val MedicalRecordMimeType = listOf(
    MimeType("image/png",".png"),
    MimeType("image/jpeg",".jpg"),
    MimeType("image/jpg",".jpg"),
    MimeType("application/pdf",".pdf"),
    MimeType("application/json",".json"),
    MimeType("application/octet-stream",".pcm"),
)

data class MimeType(var name:String,var extension:String)