package com.epam.travel_agency_final_project.security;
import com.epam.travel_agency_final_project.dto.UserSecurityDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class JwtProvider {

    @Value("${jwt.secret}")
    private String secret;

    private SecretKey key;

    // Метод @PostConstruct гарантує, що ключ буде створено
    // відразу після ініціалізації біна, використовуючи завантажений secret
    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // Тепер ви можете використовувати цей 'key' у своїх методах
    public SecretKey getKey() {
        return this.key;
    }
    // Секретний ключ повинен бути довжиною не менше 32 байт (256 біт) для алгоритму HS256.
    // У реальному проєкті ми ховаємо його в application.properties
  //  private final String SECRET_STRING = "super_secret_key_for_warehouse_system_1234567890_security";

    // Перетворюємо наш рядок у безпечний криптографічний ключ
  //  private final SecretKey key = Keys.hmacShaKeyFor(SECRET_STRING.getBytes(StandardCharsets.UTF_8));

    // Час життя Access-токена — 15 хвилин (у мілісекундах)
    private final long ACCESS_TOKEN_EXPIRATION_MS = 15 * 60 * 1000;

    /**
     * ГЕНЕРАЦІЯ ACCESS-ТОКЕНА
     * Зашиваємо всередину JWT логін користувача та його ролі.
     */
    public String generateAccessToken(UserSecurityDTO userDto) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + ACCESS_TOKEN_EXPIRATION_MS);

        // Пакуємо корисне навантаження (payload) з безпечного DTO
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", userDto.getRoles());
        claims.put("userId", userDto.getId());

        return Jwts.builder()
                .subject(userDto.getLogin()) // Наш унікальний логін
                .claims(claims)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }
    /**
     * ВАЛІДАЦІЯ ТОКЕНА
     * Перевіряє, чи токен не підроблений і чи не закінчився його час життя.
     * Якщо щось не так — викидає виняток (Exception).
     */
    public boolean validateAccessToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key) // Перевіряємо цифровий підпис
                    .build()
                    .parseSignedClaims(token); // Парсимо токен
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // Сюди під капотом влітають ExpiredJwtException, MalformedJwtException тощо.
            // Наш фільтр зможе перехопити ці помилки
            return false;
        }
    }

    /**
     * ВИДОБУТОК ЛОГІНА
     * Дістає значення поля 'subject' (username) з токена.
     */
    public String getLoginFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }

    public boolean isTokenExpired(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key) // Новий метод встановлення ключа
                    .build()
                    .parseSignedClaims(token); // Новий метод парсингу підписаного токена

            return false; // Помилки немає -> токен ще свіжий і валідний
        } catch (ExpiredJwtException e) {
            // Зловили саме прострочення!
            // Це зелене світло для нашого фільтра, щоб запустити оновлення через Refresh-токен
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // Будь-яка інша помилка (невірний підпис, зламаний формат) — токен невалідний
            return false;
        }
    }

    /**
     * ВИДОБУТОК РОЛЕЙ
     * Дістає кастомний список ролей, який ми туди поклали при генерації.
     */
  //  @SuppressWarnings("unchecked")
    public java.util.List<String> getRolesFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.get("roles", java.util.List.class);
    }

    public UUID getUserIdFromToken(String token) {
        // 1. Парсимо токен та отримуємо claims (тіло токена)
        Claims claims = Jwts.parser()
                .verifyWith(key) // Ваша секретна клавіша (SecretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        // 2. Дістаємо об'єкт userId з claims
        Object userIdObj = claims.get("userId");

        // 3. Перетворюємо на UUID
        // В JWT claims часто повертають String, тому спочатку перетворюємо на рядок
        if (userIdObj != null) {
            return UUID.fromString(userIdObj.toString());
        }

        return null;
    }

}
