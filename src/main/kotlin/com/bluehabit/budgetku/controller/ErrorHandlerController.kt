package com.bluehabit.budgetku.controller


import com.bluehabit.budgetku.common.exception.BadRequestException
import com.bluehabit.budgetku.common.exception.DataNotFoundException
import com.bluehabit.budgetku.common.exception.DuplicateException
import com.bluehabit.budgetku.common.exception.UnAuthorizedException
import com.bluehabit.budgetku.common.model.BaseResponse
import org.hibernate.exception.DataException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.METHOD_NOT_ALLOWED
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.multipart.MaxUploadSizeExceededException
import java.text.ParseException
import javax.persistence.NonUniqueResultException
import javax.validation.ConstraintViolationException

/**
 * Error handler controller
 * this controller for catch every error throw by defaul or custom
 * */
@RestControllerAdvice
class ErrorHandlerController {
    @ExceptionHandler(
        value = [ConstraintViolationException::class]
    )
    @ResponseStatus(
        BAD_REQUEST,
    )
    fun validationError(
        error: ConstraintViolationException
    ) = BaseResponse<List<Any>>(
        code = BAD_REQUEST.value(),
        data = listOf(),
        message = error.message.toString(),
    )

    @ExceptionHandler(
        value = [BadRequestException::class]
    )
    @ResponseStatus(
        BAD_REQUEST
    )
    fun badRequest(
        error: BadRequestException
    ) = BaseResponse<List<Any>>(
        code = BAD_REQUEST.value(),
        data = listOf(),
        message = error.message.toString()
    )

    @ExceptionHandler(
        value = [DataNotFoundException::class]
    )
    @ResponseStatus(
        HttpStatus.NOT_FOUND
    )
    fun dataNotFound(
        error: DataNotFoundException
    ) = BaseResponse<List<Any>>(
        code = NOT_FOUND.value(),
        data = listOf(),
        message = error.message.toString()
    )

    @ExceptionHandler(
        value = [UnAuthorizedException::class]
    )
    @ResponseStatus(
        HttpStatus.UNAUTHORIZED
    )
    fun unAuthorized(
        error: UnAuthorizedException
    ) = BaseResponse<List<Any>>(
        code = UNAUTHORIZED.value(),
        data = listOf(),
        message = error.message.toString(),
    )

    @ExceptionHandler(
        value = [HttpMediaTypeNotSupportedException::class]
    )
    @ResponseStatus(
        BAD_REQUEST
    )
    fun mediaTypeNotSupported(
        error: HttpMediaTypeNotSupportedException
    ) = BaseResponse<List<Any>>(
        code = BAD_REQUEST.value(),
        data = listOf(),
        message = "${error.message}, Supported type = ${error.supportedMediaTypes}",
    )

    @ExceptionHandler(
        value = [HttpMessageNotReadableException::class]
    )
    @ResponseStatus(
        BAD_REQUEST
    )
    fun mediaTypeJsonInvalid(
        error: HttpMessageNotReadableException
    ) = BaseResponse<List<Any>>(
        code = BAD_REQUEST.value(),
        data = listOf(),
        message = "Data given not valid",
    )

    @ExceptionHandler(
        value = [HttpRequestMethodNotSupportedException::class]
    )
    @ResponseStatus(
        HttpStatus.METHOD_NOT_ALLOWED
    )
    fun methodNotAllowed(
        error: HttpRequestMethodNotSupportedException
    ) = BaseResponse<List<Any>>(
        code = METHOD_NOT_ALLOWED.value(),
        data = listOf(),
        message = "${error.message}",
    )

    @ExceptionHandler(
        value = [MaxUploadSizeExceededException::class]
    )
    @ResponseStatus(
        BAD_REQUEST
    )
    fun maximumFileUpload(
        error: MaxUploadSizeExceededException
    ) = BaseResponse<List<Any>>(
        code = BAD_REQUEST.value(),
        data = listOf(),
        message = error.message.toString()
    )

    @ExceptionHandler(
        value = [DataIntegrityViolationException::class]
    )
    @ResponseStatus(
        BAD_REQUEST
    )
    fun sqlError(
        error: DataIntegrityViolationException
    ) = BaseResponse<List<Any>>(
        code = BAD_REQUEST.value(),
        data = listOf(),
        message = "${error.mostSpecificCause.message}"
    )

    @ExceptionHandler(
        value = [DataException::class]
    )
    @ResponseStatus(
        BAD_REQUEST
    )
    fun sqlError(
        error: DataException
    ) = BaseResponse<List<Any>>(
        code = BAD_REQUEST.value(),
        data = listOf(),
        message = error.message.toString(),
    )

    @ExceptionHandler(
        value = [org.hibernate.exception.ConstraintViolationException::class],
    )
    @ResponseStatus(
        BAD_REQUEST
    )
    fun sqlError(
        error: org.hibernate.exception.ConstraintViolationException
    ) = BaseResponse<List<Any>>(
        code = BAD_REQUEST.value(),
        data = listOf(),
        message = error.message.toString(),
    )

    @ExceptionHandler(
        value = [DuplicateException::class],
    )
    @ResponseStatus(
        BAD_REQUEST
    )
    fun sqlError(
        error: DuplicateException
    ) = BaseResponse<List<Any>>(
        code = BAD_REQUEST.value(),
        data = listOf(),
        message = error.message.toString(),
    )

    @ExceptionHandler(
        value = [NullPointerException::class]
    )
    @ResponseStatus(
        BAD_REQUEST
    )
    fun nullPointer(e: NullPointerException) =
        BaseResponse<List<Any>>(
            code = BAD_REQUEST.value(),
            data = listOf(),
            message = e.message.toString()
        )

    @ExceptionHandler(
        value = [ParseException::class]
    )
    @ResponseStatus(
        BAD_REQUEST
    )
    fun formatException(
        e: ParseException
    ) = BaseResponse<List<Any>>(
        code = BAD_REQUEST.value(),
        data = listOf(),
        message = e.message.toString(),
    )

    @ExceptionHandler(
        value = [NonUniqueResultException::class]
    )
    @ResponseStatus(
        BAD_REQUEST
    )
    fun NonUniqueResult(
        e: NonUniqueResultException
    ) = BaseResponse<List<Any>>(
        code = BAD_REQUEST.value(),
        data = listOf(),
        message = e.message.toString(),
    )
}