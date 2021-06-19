package network.security.jwt;

public interface JwtParam {
    String AUTHORIZATION_HEADER_STRING = "Authorization";
    String TOKEN_BEARER_PREFIX = "Bearer ";
    Integer ACCESS_TOKEN_EXPIRATION = 1; //часов
}
