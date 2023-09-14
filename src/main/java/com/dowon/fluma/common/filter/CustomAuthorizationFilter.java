package com.dowon.fluma.common.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@Log4j2
public class CustomAuthorizationFilter extends OncePerRequestFilter {
    private String SECRET_KEY;
    public CustomAuthorizationFilter(String SECRET_KEY) {
        this.SECRET_KEY = SECRET_KEY;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request.getServletPath().equals("/users/login") || request.getServletPath().equals("/users/token/refresh")) {
            filterChain.doFilter(request, response);
        } else {
            String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                try {
                    // local 에서 사용할 GrantType 으로 가정하여 Bearer 만 받음
                    String token = authorizationHeader.substring("Bearer ".length());
                    // token 을 생성할 때 사용한 알고리즘과 일치
                    Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY.getBytes());
                    // 토큰 검증 객체를 생성한 후 알고리즘 주기
                    JWTVerifier verifier = JWT.require(algorithm).build();
                    // 토큰 검증 객체에서 검증된 복호화된 토큰 변수 지정
                    DecodedJWT decodedJWT = verifier.verify(token);
                    // 토큰에서 정보 빼내기
                    String username = decodedJWT.getSubject();
                    // String[] 로 받은 authority 들을 Collection<SimpleGrantAuthority> 객체에 담기
                    String[] roles = decodedJWT.getClaim("roles").asArray(String.class);

                    Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
                    Arrays.stream(roles).forEach(role -> {
                        authorities.add(new SimpleGrantedAuthority(role));
                    });
                    // 가져온 정보들을 SecurityContext 에 넣을 authenticationToken 으로 만들기
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(username, null, authorities);
                    // authenticationToken 을 추가하면서 회원정보가 SecurityContext 에 저장함
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    // 다음 순서의 filter 로 넘어가기 위해 doFilter() 실행
                    filterChain.doFilter(request, response);
                }catch (TokenExpiredException e) {
                    log.error("Error logging in : {}", e.getMessage());
                    response.setHeader("error", "you're monster");
                    response.setStatus(HttpStatus.FORBIDDEN.value());
                    Map<String, String> error = new HashMap<>();
                    error.put("error", "토큰 기한 만료");
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    // fasterxml.json 기능 중 하나로 ServletOutputStream 에 직접 error(Map)를 집어넣을 수 있음
                    new ObjectMapper().writeValue(response.getOutputStream(), error);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                filterChain.doFilter(request, response);
            }
        }
    }
}
