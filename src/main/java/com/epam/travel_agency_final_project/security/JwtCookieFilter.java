package com.epam.travel_agency_final_project.security;


        import com.epam.travel_agency_final_project.dto.RefreshTokenDTO;
        import com.epam.travel_agency_final_project.dto.UserSecurityDTO;
        import com.epam.travel_agency_final_project.entity.User;
        import com.epam.travel_agency_final_project.mapper.UserSecurityMapper;

        import com.epam.travel_agency_final_project.service.RefreshTokenService;
        import com.epam.travel_agency_final_project.service.UserService;
        import jakarta.servlet.FilterChain;
        import jakarta.servlet.ServletException;
        import jakarta.servlet.http.Cookie;
        import jakarta.servlet.http.HttpServletRequest;
        import jakarta.servlet.http.HttpServletResponse;
        import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
        import org.springframework.security.core.authority.SimpleGrantedAuthority;
        import org.springframework.security.core.context.SecurityContextHolder;
        import org.springframework.stereotype.Component;
        import org.springframework.web.filter.OncePerRequestFilter;

        import java.io.IOException;
        import java.util.List;
        import java.util.UUID;
        import java.util.Arrays;
@Component
public class JwtCookieFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;
    private final UserSecurityMapper userSecurityMapper;

    public JwtCookieFilter(JwtProvider jwtProvider,
                           RefreshTokenService refreshTokenService,
                           UserService userService,
                           UserSecurityMapper userSecurityMapper) {
        this.jwtProvider = jwtProvider;
        this.refreshTokenService = refreshTokenService;
        this.userService = userService;
        this.userSecurityMapper = userSecurityMapper;
    }

    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
//        return path.equals("/")
//                || path.equals("/index")
//                || path.equals("/login")
//                || path.equals("/register")
//                || path.startsWith("/tours")
//                || path.startsWith("/cart")
//                || path.startsWith("/css/")
//                || path.startsWith("/blocked/")
//                || path.startsWith("/js/")
//                || path.startsWith("/profile")
//                || path.startsWith("/uploads/");

    return path.equals("/")
            || path.equals("/index")
            || path.equals("/login")
            || path.equals("/register")
            || path.startsWith("/tours")
            || path.startsWith("/cart")
            || path.startsWith("/css/")
            || path.startsWith("/blocked/")
            || path.startsWith("/js/")
            || path.startsWith("/uploads/");
}

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {


        String accessToken = extractCookie(request, "access_token");
        String refreshUUID = extractCookie(request, "refresh_token");

        System.out.println("*********************************");


        System.out.println("DEBUG: Request to " + request.getRequestURI());

        System.out.println("DEBUG: REfresh token found: " + (refreshUUID != null));

        System.out.println("DEBUG: Access token found: " + (accessToken != null));

        System.out.println("*********************************");

      //  String accessToken = extractCookie(request, "access_token");

        RefreshTokenDTO refreshCurrentToken=null;
        UserSecurityDTO userSecurityDTO=null;
        // UserSecurityDTO userSecurityDTO = userService.findById(refreshCurrentToken.getUserId());
        if (accessToken != null && jwtProvider.validateAccessToken(accessToken)&& refreshUUID != null) {
            refreshCurrentToken = refreshTokenService.getRefreshToken(refreshUUID);
            userSecurityDTO = userService.findById(refreshCurrentToken.getUserId());
            System.out.println("****************************************");

            System.out.println(userSecurityDTO);

            System.out.println("****************************************");

            authenticateUserInContext(accessToken,userSecurityDTO);

        }

        // 1. Валідний Access Token - пріоритетний сценарій
        if (accessToken != null && jwtProvider.validateAccessToken(accessToken)&&refreshUUID != null) {
            refreshCurrentToken = refreshTokenService.getRefreshToken(refreshUUID);
            userSecurityDTO = userService.findById(refreshCurrentToken.getUserId());
            authenticateUserInContext(accessToken,userSecurityDTO);

            filterChain.doFilter(request, response);
            return;
        }

        // 2. Спроба оновлення (Access прострочений, але є Refresh UUID в куках)
        if (refreshUUID != null && jwtProvider.isTokenExpired(accessToken)) {
             refreshCurrentToken = refreshTokenService.getRefreshToken(refreshUUID);

            if (refreshCurrentToken != null) {
                // Отримуємо DTO користувача через наш сервіс та мапер
                userSecurityDTO = userService.findById(refreshCurrentToken.getUserId());

                // БЕЗПЕЧНА ЛОГІКА ПЕРЕВІРОК:

                // А. Користувача не знайдено в БД (наприклад, видалений)
                if (userSecurityDTO == null) {
                    response.sendRedirect("/register");
                    return;
                }

                // Б. Користувач заблокований
                if (userSecurityDTO.isLocked()) {
                    response.sendRedirect("/blok");
                    return;
                }

                // В. Ротація токенів (оновлюємо дані в базі та куки)
               String rotatedToken = refreshTokenService.rotateRefreshToken(refreshUUID);

                if (rotatedToken != null) {
                    // Генеруємо новий Access Token для існуючого користувача
                    String newAccessToken = jwtProvider.generateAccessToken(userSecurityDTO);

                    // Оновлюємо куки у браузері
                    updateAuthCookies(response, newAccessToken, rotatedToken);

                    // Авторизуємо поточний запит
                    authenticateUserInContext(newAccessToken, userSecurityDTO);

                    filterChain.doFilter(request, response);
                    return;
                }
            }
        }

        // 3. Якщо нічого не підійшло, передаємо запит далі (фільтр Spring Security заблокує, якщо потрібно)
        filterChain.doFilter(request, response);
    }

//    private void authenticateUserInContext(String token) {
//        String login = jwtProvider.getLoginFromToken(token);
//        List<String> roles = jwtProvider.getRolesFromToken(token);
//
//        var authorities = roles.stream().map(SimpleGrantedAuthority::new).toList();
//        SecurityContextHolder.getContext().setAuthentication(
//                new UsernamePasswordAuthenticationToken(login, null, authorities)
//        );
//    }

    private void authenticateUserInContext(String token, UserSecurityDTO userDTO) {
        String login = jwtProvider.getLoginFromToken(token);

        // Беремо ролі з DTO користувача, яке ви отримали через userService
        List<String> roles = userDTO.getRoles(); // Припустимо, у вас є метод getRoles()

        var authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.startsWith("ROLE_") ? role : "ROLE_" + role))
                .toList();

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(login, null, authorities)
        );
    }

    private String extractCookie(HttpServletRequest request, String name) {
        if (request.getCookies() == null) return null;
        return Arrays.stream(request.getCookies())
                .filter(c -> name.equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

    private void updateAuthCookies(HttpServletResponse response, String accessToken, String refreshUUID) {
        Cookie accessCookie = new Cookie("access_token", accessToken);
        accessCookie.setHttpOnly(true);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(15 * 60);
        response.addCookie(accessCookie);

        Cookie refreshCookie = new Cookie("refresh_token", refreshUUID);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(30 * 24 * 60 * 60);
        response.addCookie(refreshCookie);
    }
}
