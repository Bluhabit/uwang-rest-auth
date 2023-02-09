package com.bluehabit.budgetku.config.admin

import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter
import javax.servlet.http.HttpServletRequest

class AdminAuthFilter (
    private val headerName:String
): AbstractPreAuthenticatedProcessingFilter(){
    override fun getPreAuthenticatedPrincipal(request: HttpServletRequest?): Any? {
        return request?.getHeader(headerName) ?: ""
    }

    override fun getPreAuthenticatedCredentials(request: HttpServletRequest?): Any? {
        return null
    }
}