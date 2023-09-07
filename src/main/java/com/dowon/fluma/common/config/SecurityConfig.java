package com.dowon.fluma.common.config;

import com.dowon.fluma.common.filter.CustomAuthenticationFilter;
import com.dowon.fluma.common.filter.CustomAuthorizationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final UserDetailsService userDetailsService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    @Value("${SECRET_KEY}")
    private String SECRET_KEY;

    /**
     * CustomFilter 에 사용할 AuthenticationManager 를 제공하기 위해 빈 생성
     * @return
     * @throws Exception
     */
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     * authenticationManager 에서 사용할 UserDetailsService 객체를 인자로 받아
     * DAO authenticationConfigurer 를 반환 받으면 그 안에 passwordEncoder 를 지정해줌
     * @param auth the {@link AuthenticationManagerBuilder} to use
     * @throws Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(authenticationManagerBean(), SECRET_KEY);
        // CustomAuthenticationFilter 가 상속받는 UsernamePasswordAuthenticationFilter 는
        // 기존에 로그인 처리 url 이 기본으로 잡혀있다
        // 위와 같은 이유로 아래 코드를 통해 내가 원하는 로그인 엔드포인트를 지정해줄 수 있다
        customAuthenticationFilter.setFilterProcessesUrl("/users/login");
        // csrf 금지
        http.csrf().disable();
        // http 프로토콜의 조건 중 하나인 STATELESS 무상태 구조
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        // ExpressionInterceptUrlRegistry 를 통해 각각 url 로의 접근 권한을 설정할 수 있게 해줌
        // 핵심은 권한에 따른 엔드포인트 분류할 때 더 강한 권한일수록 antMatchers 를 나중에 선언해야한다
        // Ex) permitAll() -> hasAuthority("ROLE_USER") -> hasAuthority("ROLE_ADMIN")
        http.authorizeRequests()
                // 권한이나 인증 없이 permit 해줌
                .antMatchers("/users/login/**",
                        "/users/token/refresh/**",
                        "/users/join/**").permitAll()
//                .antMatchers(HttpMethod.GET,"/novels",
//                        "/novels/drawing/**").permitAll()
//                .antMatchers("/novels/**").hasAuthority("ROLE_USER")
//                .antMatchers("/chapters/**").hasAuthority("ROLE_USER")
                // 인증(authenticated)되야만 접근 가능 antMatchers 로 지정한 엔드포인트 외에
                .anyRequest().authenticated();
        // filter 로 dispatcher servlet 에 접근하기 전에 먼저 authenticationManager 객체로 검사
        http.addFilter(customAuthenticationFilter);
        // 다른 필터보다 요청을 먼저 가로챔
        http.addFilterBefore(new CustomAuthorizationFilter(SECRET_KEY), UsernamePasswordAuthenticationFilter.class);
    }

}
