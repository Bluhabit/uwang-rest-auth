/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.budgetku.data.post

import org.springframework.stereotype.Repository
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

@Repository
class InMemorySseEmitterRepository {
    private val userEmitterMap:ConcurrentHashMap<String,SseEmitter> = ConcurrentHashMap()

    fun addEmitter(memberId:String,emitter: SseEmitter){
        userEmitterMap[memberId] = emitter
    }

    fun removeEmitter(memberId: String){
        if(!userEmitterMap.isNullOrEmpty() && userEmitterMap.containsKey(memberId)){
            userEmitterMap.remove(memberId)
        }
    }

    fun getEmitter(memberId: String): SseEmitter? {
        return userEmitterMap.get(memberId)
    }

    fun getAllEmitter():ConcurrentHashMap<String,SseEmitter>{
        return userEmitterMap
    }
}