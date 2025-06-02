package id.co.bcaf.solvr.security;

import id.co.bcaf.solvr.model.account.Feature;
import id.co.bcaf.solvr.model.account.Role;
import id.co.bcaf.solvr.model.account.RoleToFeature;
import id.co.bcaf.solvr.services.BlacklistTokenService;
import id.co.bcaf.solvr.services.FeatureService;
import id.co.bcaf.solvr.services.RoleService;
import id.co.bcaf.solvr.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends GenericFilterBean {
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Autowired
    private BlacklistTokenService blacklistTokenService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private FeatureService featureService;



    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestURI = httpRequest.getRequestURI();
        String authHeader = httpRequest.getHeader("Authorization");

        logger.info("Processing request: {} with Authorization header: {}", requestURI, authHeader);

        // Check for Authorization header
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            String username = jwtUtil.extractusername(token);
            UUID userId = jwtUtil.extractId(token);
            request.setAttribute("userId", userId);
            String role = jwtUtil.extractRole(token);

            logger.info("Extracted username from token: {}", username);

            if (blacklistTokenService.isTokenBlacklisted(token)) {
                ((HttpServletResponse) response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token Blacklisted");
                return;
            }

            if (jwtUtil.validateToken(token, username)) {
                logger.info("Role Name : {}", role);
//                List<String> features = featureService.getFeatureByRole(role);
                String roleWithPrefix = role.startsWith("ROLE_") ? role : "ROLE_" + role;
                Role roleModel = roleService.getRoleByName(role);

                List<RoleToFeature> features = featureService.getRoleToFeatureByRole(roleModel);
                List<String> featuresList = features.stream().map(RoleToFeature::getFeature).map(Feature::getName).toList();

                List<GrantedAuthority> authorities =featuresList.stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
                authorities.add(new SimpleGrantedAuthority(roleWithPrefix));

                logger.debug("Token validated for user: {}", username);
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(username, null, authorities);

                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpRequest));

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            } else {
                logger.warn("Invalid token for user: {}", username);
                ((HttpServletResponse) response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
                return;
            }
        } catch (Exception e) {
            logger.error("Error processing JWT token", e);
            ((HttpServletResponse) response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token processing error: " + e.getMessage());
            return;
        }

        chain.doFilter(request, response);
    }
}
