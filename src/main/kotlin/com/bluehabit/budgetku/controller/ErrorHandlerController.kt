package com.bluehabit.budgetku.controller


import com.bluehabit.budgetku.common.exception.BadRequestException
import com.bluehabit.budgetku.common.exception.DataNotFoundException
import com.bluehabit.budgetku.common.exception.DuplicateException
import com.bluehabit.budgetku.common.exception.UnAuthorizedException
import com.bluehabit.budgetku.common.model.BaseResponse
import com.bluehabit.budgetku.common.model.baseResponse
import org.hibernate.exception.DataException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.CONFLICT
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
        value = [IllegalArgumentException::class]
    )
    @ResponseStatus(
        BAD_REQUEST
    )
    fun illegalArgument(
        e:IllegalArgumentException
    ): BaseResponse<List<Any>> {
        e.printStackTrace()
        return baseResponse<List<Any>> {
            code = BAD_REQUEST.value()
            data = listOf()
            message = e.message.orEmpty()
        }
    }

    @ExceptionHandler(
        value = [ConstraintViolationException::class]
    )
    @ResponseStatus(
        BAD_REQUEST,
    )
    fun validationError(
        error: ConstraintViolationException
    ) = baseResponse<List<Any>> {
        code = BAD_REQUEST.value()
        data = listOf()
        message = error.message.orEmpty()
    }

    @ExceptionHandler(
        value = [BadRequestException::class]
    )
    @ResponseStatus(
        BAD_REQUEST
    )
    fun badRequest(
        error: BadRequestException
    ) = baseResponse<List<Any>> {
        code = BAD_REQUEST.value()
        data = listOf()
        message = error.message.orEmpty()
    }

    @ExceptionHandler(
        value = [DataNotFoundException::class]
    )
    @ResponseStatus(
        CONFLICT
    )
    fun dataNotFound(
        error: DataNotFoundException
    ) = baseResponse<List<Any>> {
        code = CONFLICT.value()
        data = listOf()
        message = error.message.orEmpty()
    }

    @ExceptionHandler(
        value = [UnAuthorizedException::class]
    )
    @ResponseStatus(
        UNAUTHORIZED
    )
    fun unAuthorized(
        error: UnAuthorizedException
    ) = baseResponse<List<Any>> {
        code = BAD_REQUEST.value()
        data = listOf()
        message = error.message.orEmpty()
    }

    @ExceptionHandler(
        value = [HttpMediaTypeNotSupportedException::class]
    )
    @ResponseStatus(
        BAD_REQUEST
    )
    fun mediaTypeNotSupported(
        error: HttpMediaTypeNotSupportedException
    ) = baseResponse<List<Any>> {
        code = BAD_REQUEST.value()
        data = listOf()
        message = error.message.orEmpty()
    }

    @ExceptionHandler(
        value = [HttpMessageNotReadableException::class]
    )
    @ResponseStatus(
        BAD_REQUEST
    )
    fun mediaTypeJsonInvalid(
        error: HttpMessageNotReadableException
    ) = baseResponse<List<Any>> {
        code = BAD_REQUEST.value()
        data = listOf()
        message = error.message.orEmpty()
    }

    @ExceptionHandler(
        value = [HttpRequestMethodNotSupportedException::class]
    )
    @ResponseStatus(
        METHOD_NOT_ALLOWED
    )
    fun methodNotAllowed(
        error: HttpRequestMethodNotSupportedException
    ) = baseResponse<List<Any>> {
        code = BAD_REQUEST.value()
        data = listOf()
        message = error.message.orEmpty()
    }

    @ExceptionHandler(
        value = [MaxUploadSizeExceededException::class]
    )
    @ResponseStatus(
        BAD_REQUEST
    )
    fun maximumFileUpload(
        error: MaxUploadSizeExceededException
    ) = baseResponse<List<Any>> {
        code = BAD_REQUEST.value()
        data = listOf()
        message = error.message.orEmpty()
    }

    @ExceptionHandler(
        value = [DataIntegrityViolationException::class]
    )
    @ResponseStatus(
        BAD_REQUEST
    )
    fun sqlError(
        error: DataIntegrityViolationException
    ) = baseResponse<List<Any>> {
        code = BAD_REQUEST.value()
        data = listOf()
        message = error.message.orEmpty()
    }

    @ExceptionHandler(
        value = [DataException::class]
    )
    @ResponseStatus(
        BAD_REQUEST
    )
    fun sqlError(
        error: DataException
    ) = baseResponse<List<Any>> {
        code = BAD_REQUEST.value()
        data = listOf()
        message = error.message.orEmpty()
    }

    @ExceptionHandler(
        value = [org.hibernate.exception.ConstraintViolationException::class],
    )
    @ResponseStatus(
        BAD_REQUEST
    )
    fun sqlError(
        error: org.hibernate.exception.ConstraintViolationException
    ) = baseResponse<List<Any>> {
        code = BAD_REQUEST.value()
        data = listOf()
        message = error.message.orEmpty()
    }

    @ExceptionHandler(
        value = [DuplicateException::class],
    )
    @ResponseStatus(
        BAD_REQUEST
    )
    fun sqlError(
        error: DuplicateException
    ) = baseResponse<List<Any>> {
        code = BAD_REQUEST.value()
        data = listOf()
        message = error.message.orEmpty()
    }

    @ExceptionHandler(
        value = [NullPointerException::class]
    )
    @ResponseStatus(
        BAD_REQUEST
    )
    fun nullPointer(error: NullPointerException) =
        baseResponse<List<Any>> {
            code = BAD_REQUEST.value()
            data = listOf()
            message = error.message.orEmpty()
        }

    @ExceptionHandler(
        value = [ParseException::class]
    )
    @ResponseStatus(
        BAD_REQUEST
    )
    fun formatException(
        error: ParseException
    ) = baseResponse<List<Any>> {
        code = BAD_REQUEST.value()
        data = listOf()
        message = error.message.orEmpty()
    }

    @ExceptionHandler(
        value = [NonUniqueResultException::class]
    )
    @ResponseStatus(
        BAD_REQUEST
    )
    fun nonUniqueResult(
        error: NonUniqueResultException
    ) = baseResponse<List<Any>> {
        code = BAD_REQUEST.value()
        data = listOf()
        message = error.message.orEmpty()
    }
}