package com.dowon.fluma.common.filter;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private static String SECRET_KEY;
    private final AuthenticationManager authenticationManager;

    // authentication 을 하기 위한 관리자객체 생성
    public CustomAuthenticationFilter(AuthenticationManager authenticationManager, String secret_key){
        this.authenticationManager = authenticationManager;
        SECRET_KEY = secret_key;
    }

    // authentication 에 사용하기 위해 username 과 password 를 이용하여
    // UsernamePasswordAuthenticationToken 객체 생성
    // 로그인 실패시 AuthenticationException 전달
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        log.info("Username : {} , Password : {}",username, password);
        UsernamePasswordAuthenticationToken authenticationToken
                = new UsernamePasswordAuthenticationToken(username, password);
        // 관리자 객체에 인증 토큰을 인자로 담아 반환
        return authenticationManager.authenticate(authenticationToken);
    }

    // authentication 성공 메소드
    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            // AuthenticationManager 가 인증한 로그인 결과
                                            // = attemptAuthentication() 의 반환 값
                                            Authentication authentication) throws IOException, ServletException {

        // 사용자 로그인 성공
        User user = (User)authentication.getPrincipal();
        // signature 에 담길 시크릿키와 알고리즘 설정
        Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY.getBytes());

        String access_token = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + 60*1000))
                .withIssuer(request.getRequestURL().toString())
                .withClaim("roles", user.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .sign(algorithm);

        String refresh_token = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + 3*60*60*1000))
                .withIssuer(request.getRequestURL().toString())
                .sign(algorithm);

        // response header 에 담으면 보안에 취약하므로 body에 담아서 보내기
        Map<String, String> tokens = new HashMap<>();
//        tokens.put("granty_type", "Bearer");
        tokens.put("access_token", access_token);
        tokens.put("refresh_token", refresh_token);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), tokens);
    }
}
