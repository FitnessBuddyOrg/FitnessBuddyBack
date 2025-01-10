package api.fitnessbuddyback.exeption;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

public class RequestLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        System.out.println("Request URI: " + request.getRequestURI());
        System.out.println("Query Parameters: " + request.getQueryString());
        System.out.println("Headers: " + request.getHeaderNames());
        filterChain.doFilter(request, response);
    }
}
