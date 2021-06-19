package network.security.jwt;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Getter;
import network.model.entity.Person;
import network.security.userdetails.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Getter
    private String secretKey;

    @Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl;

    @PostConstruct
    //При запуске приложения создает и кодирует секретный код
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(generateSecretKey(64));
    }

    public byte[] generateSecretKey(int length) {
        SecureRandom random = new SecureRandom();
        return new BigInteger(length, random).toString(32).getBytes(StandardCharsets.UTF_8);
    }

    public String createToken(Person person) {

        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();
        //Действие токена
        calendar.add(Calendar.HOUR, JwtParam.ACCESS_TOKEN_EXPIRATION);
        Date validity = calendar.getTime();

        String token = Jwts.builder()
                .claim("email", person.getEmail())
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();

        return JwtParam.TOKEN_BEARER_PREFIX + token;
    }

    public Authentication getAuthentication(String token) {

        UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(getUserEmailFromToken(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getUserEmailFromToken(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().get("email").toString();
    }

    public String checkFormatToken(HttpServletRequest req) {
        String bearerToken = req.getHeader(JwtParam.AUTHORIZATION_HEADER_STRING);
        if (bearerToken != null && bearerToken.startsWith(JwtParam.TOKEN_BEARER_PREFIX)) {
            //В запросе к токену добавляеться в начале префикс "Bearer " это стандарт, поэтому уберем префикс
            return bearerToken.replace(JwtParam.TOKEN_BEARER_PREFIX, "");
        }
        return null;
    }

    public boolean checkTokenIsExist(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

}