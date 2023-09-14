package com.dowon.fluma.user.api;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.dowon.fluma.common.dto.StatusDTO;
import com.dowon.fluma.user.domain.Role;
import com.dowon.fluma.user.domain.User;
import com.dowon.fluma.user.dto.RoleToUserDTO;
import com.dowon.fluma.user.exception.SameNameException;
import com.dowon.fluma.user.exception.SameUsernameException;
import com.dowon.fluma.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Value("${SECRET_KEY}")
    private String SECRET_KEY;

    @GetMapping("/users")
    public ResponseEntity<List<User>> getUsers() {
        return ResponseEntity.ok().body(userService.getUsers());
    }

    @PostMapping(value = "/join", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity saveUser(@RequestBody User user) {
        try {
            return new ResponseEntity<>(userService.saveUser(user), HttpStatus.OK);
        } catch (SameUsernameException e) {
            return new ResponseEntity<>(StatusDTO.builder().status(e.getMessage()).build(), HttpStatus.OK);
        } catch (SameNameException e) {
            return new ResponseEntity<>(StatusDTO.builder().status(e.getMessage()).build(), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/info")
    public void getName(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // header 에 authorization 으로 Bearer {refresh token} 받기 ( refresh token 은 쿠키로 관리 )
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            try {
                // local 에서 사용할 GrantType 으로 가정하여 Bearer 만 받음
                // 토큰 값만 자르기
                String access_token = authorizationHeader.substring("Bearer ".length());
                // token 을 생성할 때 사용한 알고리즘과 일치
                Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY.getBytes());
                // 토큰 검증 객체를 생성한 후 알고리즘 주기
                JWTVerifier verifier = JWT.require(algorithm).build();
                // 토큰 검증 객체에서 검증된 복호화된 토큰 변수 지정
                DecodedJWT decodedJWT = verifier.verify(access_token);
                // 토큰에서 정보 빼내기
                String username = decodedJWT.getSubject();
                // 빼낸 정보로 DB 조회하기
                User user = userService.getUser(username);
                Map<String, String> map = new HashMap<>();
                map.put("userName", user.getName());
                map.put("userId", user.getId().toString());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), map);
            }catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            throw new RuntimeException("Refresh token is missing");
        }
    }
    @PostMapping("/role/save")
    public ResponseEntity<Role> saveUser(@RequestBody Role role) {
        return ResponseEntity.ok().body(userService.saveRole(role));
    }

    @PostMapping("/role/addtouser")
    public ResponseEntity<?> addRoleToUser(@RequestBody RoleToUserDTO form) {
        userService.addRoleToUser(form.getUsername(), form.getRoleName());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/token/refresh")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // header 에 authorization 으로 Bearer {refresh token} 받기 ( refresh token 은 쿠키로 관리 )
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            try {
                // local 에서 사용할 GrantType 으로 가정하여 Bearer 만 받음
                // 토큰 값만 자르기
                String refresh_token = authorizationHeader.substring("Bearer ".length());
                // token 을 생성할 때 사용한 알고리즘과 일치
                Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY.getBytes());
                // 토큰 검증 객체를 생성한 후 알고리즘 주기
                JWTVerifier verifier = JWT.require(algorithm).build();
                // 토큰 검증 객체에서 검증된 복호화된 토큰 변수 지정
                DecodedJWT decodedJWT = verifier.verify(refresh_token);
                // 토큰에서 정보 빼내기
                String username = decodedJWT.getSubject();
                // 빼낸 정보로 DB 조회하기
                User user = userService.getUser(username);
                String access_token = JWT.create()
                        .withSubject(user.getUsername())
                        .withExpiresAt(new Date(System.currentTimeMillis() + 60*1000))
                        .withIssuer(request.getRequestURL().toString())
                        .withClaim("roles", user.getRoles()
                                .stream()
                                .map(Role::getName).collect(Collectors.toList()))
                        .sign(algorithm);
                // response header 에 담으면 보안에 취약하므로 바디에 담아서 보내기
                Map<String, String> tokens = new HashMap<>();
                tokens.put("access_token", access_token);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), tokens);
            }catch (TokenExpiredException e) {
                log.error("Error logging in : {}", e.getMessage());
                response.setHeader("error", "you're monster");
                response.setStatus(HttpStatus.FORBIDDEN.value());
                Map<String, String> error = new HashMap<>();
                error.put("error", "재로그인");
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                // fasterxml.json 기능 중 하나로 ServletOutputStream 에 직접 error(Map)를 집어넣을 수 있음
                new ObjectMapper().writeValue(response.getOutputStream(), error);
            }catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            throw new RuntimeException("Refresh token is missing");
        }
    }
}
