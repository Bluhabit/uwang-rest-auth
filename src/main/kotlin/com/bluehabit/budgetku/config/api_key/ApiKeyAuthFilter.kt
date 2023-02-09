package com.bluehabit.budgetku.config.api_key

import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter
import javax.servlet.http.HttpServletRequest

class ApiKeyAuthFilter(
    private val headerName:String
) : AbstractPreAuthenticatedProcessingFilter() {
    override fun getPreAuthenticatedPrincipal(request: HttpServletRequest?): Any {
        return request?.getHeader(headerName) ?: "NO-HEADER"
    }

    override fun getPreAuthenticatedCredentials(request: HttpServletRequest?): Any? {
       return null
    }


}