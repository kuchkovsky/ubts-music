package ua.org.ubts.songs.filter;

import org.springframework.web.filter.OncePerRequestFilter;
import ua.org.ubts.songs.service.TrackTokenService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class TrackFilesDownloadFilter extends OncePerRequestFilter {

    private TrackTokenService trackTokenService;

    public TrackFilesDownloadFilter(TrackTokenService trackTokenService) {
        this.trackTokenService = trackTokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String requestUri = request.getRequestURI();
        if (request.getMethod().equals("POST") || requestUri.contains("/sample/")) {
            chain.doFilter(request, response);
        } else {
            String token = request.getParameter("token");
            if (token == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            } else if (!trackTokenService.verifyToken(token)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            } else {
                chain.doFilter(request, response);
            }
        }
    }

}
