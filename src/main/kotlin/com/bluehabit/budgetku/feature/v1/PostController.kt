/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.budgetku.feature.v1

import com.bluehabit.budgetku.data.post.PostService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(
    value = ["/v1/post"]
)
class PostController(
    private val postService: PostService
){

    @GetMapping(
        value = ["/subscribe/{eventType}"],
    )
    suspend fun subscribeToEvent(
        @PathVariable("eventType") eventType:String,
        @RequestParam("entityId") entityId:String
    )= postService.subscribe(eventType,entityId)

    @GetMapping(
        value = ["/send/{memberId}"]
    )
    suspend fun sendEvent(
        @PathVariable("memberId") memberId:String
    ) = postService.sendBroadcast(mapOf(
        "name" to "Trian",
        "eventType" to "coba"
    ))
}