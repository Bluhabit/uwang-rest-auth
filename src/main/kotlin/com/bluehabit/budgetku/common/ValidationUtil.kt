package com.bluehabit.budgetku.common

import com.bluehabit.budgetku.common.exception.BadRequestException
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit
import javax.validation.ConstraintViolationException
import javax.validation.Validator

@Component
class ValidationUtil(
    val validator: Validator
) {
    fun validate(any: Any) {
        val result = validator.validate(any)

        if (result.size != 0) {
            throw ConstraintViolationException(result)
        }
    }

    fun validateDate(start:Long,end:Long){
        if(start>end){
            throw BadRequestException("not allowed start date more than end date")
        }

        val differenceInTime: Long = end - start
        val differenceInDays = (TimeUnit.MILLISECONDS
            .toDays(differenceInTime)
                % 365)

        if(differenceInDays > 3){
            throw BadRequestException("Maks date range only allow for 3 days")
        }

    }

    fun isValid(any: Any):Boolean{
        val result = validator.validate(any)
        return result.size == 0
    }

    fun isValidWithMessage(any:Any):Pair<Boolean,String>{
        return try {
            val result = validator.validate(any)

            Pair(
                result.size == 0,
                result.map {  "${it.propertyPath.lastOrNull()?.name} ${it.message}" }.toString()
            )
        }catch (e:Exception){

            Pair(
                false,
                e.message.toString()
            )
        }

    }


}