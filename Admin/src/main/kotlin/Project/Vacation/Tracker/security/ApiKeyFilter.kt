package Project.Vacation.Tracker.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class ApiKeyFilter(
    @Value("\${api.key}") private val apiKey: String
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {

        val requestUri = request.requestURI
        if (requestUri.startsWith("/swagger-ui") ||
            requestUri.startsWith("/v3/api-docs")) {
            filterChain.doFilter(request, response)
            return
        }

        val requestApiKey = request.getHeader("x-api-key")
        if (requestApiKey == null || requestApiKey != apiKey) {
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            response.writer.write("Unauthorized: Invalid API Key")
            return
        }

        filterChain.doFilter(request, response)
    }
}

