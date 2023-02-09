package com.bluehabit.budgetku.controller

import com.aliyun.oss.common.auth.InvalidCredentialsException
import com.bluehabit.budgetku.common.exception.BadRequestException
import com.bluehabit.budgetku.common.exception.DataNotFoundException
import com.bluehabit.budgetku.common.exception.DuplicateException
import com.bluehabit.budgetku.common.exception.UnAuthorizedException
import com.bluehabit.budgetku.model.BaseResponse
import org.hibernate.exception.DataException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.multipart.MaxUploadSizeExceededException
import java.text.ParseException
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
        HttpStatus.BAD_REQUEST,
    )
    fun validationError(
        error:ConstraintViolationException
    )= BaseResponse<List<Any>,Any,Any>(
        status = STATUS.BAD_REQUEST,
        code = BAD_REQUEST.value(),
        data = listOf(),
        message = error.message.toString(),
        errorCode = 1
    )

    @ExceptionHandler(
        value = [BadRequestException::class]
    )
    @ResponseStatus(
        HttpStatus.BAD_REQUEST
    )
    fun badRequest(
        error: BadRequestException
    )= BaseResponse<List<Any>,Any,Any>(
        status = STATUS.BAD_REQUEST,
        code = HttpStatus.BAD_REQUEST.value(),
        data = listOf(),
        message = error.message.toString(),
        errorCode = 2
    )

    @ExceptionHandler(
        value = [DataNotFoundException::class]
    )
    @ResponseStatus(
        HttpStatus.NOT_FOUND
    )
    fun dataNotFound(
        error: DataNotFoundException
    )= BaseResponse<List<Any>,Any,Any>(
        status = STATUS.NOT_FOUND,
        code = HttpStatus.NOT_FOUND.value(),
        data = listOf(),
        message = error.message.toString(),
        errorCode = 3
    )

    @ExceptionHandler(
        value = [UnAuthorizedException::class]
    )
    @ResponseStatus(
        HttpStatus.UNAUTHORIZED
    )
    fun unAuthorized(
        error: UnAuthorizedException
    )= BaseResponse<List<Any>,Any,Any>(
        status = STATUS.UNAUTHORIZED,
        code = HttpStatus.UNAUTHORIZED.value(),
        data = listOf(),
        message = error.message.toString(),
        errorCode = 4
    )

    @ExceptionHandler(
        value = [HttpMediaTypeNotSupportedException::class]
    )
    @ResponseStatus(
        HttpStatus.BAD_REQUEST
    )
    fun mediaTypeNotSupported(
        error:HttpMediaTypeNotSupportedException
    )= BaseResponse<List<Any>,Any,Any>(
        status = STATUS.BAD_REQUEST,
        code = HttpStatus.BAD_REQUEST.value(),
        data = listOf(),
        message = "${error.message}, Supported type = ${error.supportedMediaTypes}",
        errorCode = 5
    )

    @ExceptionHandler(
        value = [HttpMessageNotReadableException::class]
    )
    @ResponseStatus(
        HttpStatus.BAD_REQUEST
    )
    fun mediaTypeJsonInvalid(
        error:HttpMessageNotReadableException
    )= BaseResponse<List<Any>,Any,Any>(
        status = STATUS.BAD_REQUEST,
        code = HttpStatus.BAD_REQUEST.value(),
        data = listOf(),
        message = "Data given not valid",
        errorCode = 6
    )

    @ExceptionHandler(
        value = [HttpRequestMethodNotSupportedException::class]
    )
    @ResponseStatus(
        HttpStatus.METHOD_NOT_ALLOWED
    )
    fun methodNotAllowed(
        error:HttpRequestMethodNotSupportedException
    )= BaseResponse<List<Any>,Any,Any>(
        status = STATUS.NOT_FOUND,
        code = HttpStatus.METHOD_NOT_ALLOWED.value(),
        data = listOf(),
        message = "${error.message}",
        errorCode = 7
    )

    @ExceptionHandler(
        value = [MaxUploadSizeExceededException::class]
    )
    @ResponseStatus(
        HttpStatus.BAD_REQUEST
    )
    fun maximumFileUpload(
        error:MaxUploadSizeExceededException
    )= BaseResponse<List<Any>,Any,Any>(
        status = STATUS.BAD_REQUEST,
        code = HttpStatus.BAD_REQUEST.value(),
        data = listOf(),
        message = error.message.toString(),
        errorCode = 8
    )

    @ExceptionHandler(
        value = [InvalidCredentialsException::class]
    )
    @ResponseStatus(
        HttpStatus.BAD_REQUEST
    )
    fun ossUnAuthorized(
        error:InvalidCredentialsException
    )=BaseResponse<List<Any>,Any,Any>(
        status = STATUS.BAD_REQUEST,
        code = HttpStatus.BAD_REQUEST.value(),
        data = listOf(),
        message = error.message.toString(),
        errorCode = 9
    )

    @ExceptionHandler(
        value = [DataIntegrityViolationException::class]
    )
    @ResponseStatus(
        HttpStatus.BAD_REQUEST
    )
    fun sqlError(
        error:DataIntegrityViolationException
    )=BaseResponse<List<Any>,Any,Any>(
        status = STATUS.BAD_REQUEST,
        code = HttpStatus.BAD_REQUEST.value(),
        data = listOf(),
        message = "${error.mostSpecificCause.message}",
        errorCode = 10
    )

    @ExceptionHandler(
        value = [DataException::class]
    )
    @ResponseStatus(
        HttpStatus.BAD_REQUEST
    )
    fun sqlError(
        error:DataException
    )=BaseResponse<List<Any>,Any,Any>(
        status = STATUS.BAD_REQUEST,
        code = HttpStatus.BAD_REQUEST.value(),
        data = listOf(),
        message = error.message.toString(),
        errorCode = 11
    )

    @ExceptionHandler(
        value = [org.hibernate.exception.ConstraintViolationException::class],
    )
    @ResponseStatus(
        HttpStatus.BAD_REQUEST
    )
    fun sqlError(
        error:org.hibernate.exception.ConstraintViolationException
    )=BaseResponse<List<Any>,Any,Any>(
        status = STATUS.BAD_REQUEST,
        code = HttpStatus.BAD_REQUEST.value(),
        data = listOf(),
        message = error.message.toString(),
        errorCode = 12
    )

    @ExceptionHandler(
        value = [DuplicateException::class],
    )
    @ResponseStatus(
        HttpStatus.BAD_REQUEST
    )
    fun sqlError(
        error:DuplicateException
    )=BaseResponse<List<Any>,Any,Any>(
        status = STATUS.BAD_REQUEST,
        code = HttpStatus.BAD_REQUEST.value(),
        data = listOf(),
        message = error.message.toString(),
        errorCode = 13
    )

    @ExceptionHandler(
        value = [NullPointerException::class]
    )
    @ResponseStatus(
        HttpStatus.BAD_REQUEST
    )
    fun nullPointer(e:NullPointerException)=
        BaseResponse<List<Any>,Any,Any>(
            status = STATUS.BAD_REQUEST,
            code = HttpStatus.BAD_REQUEST.value(),
            data = listOf(),
            message = e.message.toString(),
            errorCode = 14
        )

    @ExceptionHandler(
        value = [ParseException::class]
    )
    @ResponseStatus(
        HttpStatus.BAD_REQUEST
    )
    fun formatException(
        e:ParseException
    )=BaseResponse<List<Any>,Any,Any>(
        status = STATUS.BAD_REQUEST,
        code = HttpStatus.BAD_REQUEST.value(),
        data = listOf(),
        message = e.message.toString(),
        errorCode = 15
    )
}