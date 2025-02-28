package com.myzhihu.config;


import com.myzhihu.filter.JwtAuthenticateFilter;
import com.myzhihu.handler.AccessDeniedHandlerImpl;
import com.myzhihu.handler.AuthenticationExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SpringSecurityConfig {
    @Autowired
    private AuthenticationConfiguration authenticationConfiguration;

    @Autowired
    private JwtAuthenticateFilter jwtAuthenticateFilter;

    @Autowired
    AuthenticationExceptionHandler authenticationExceptionHandler;

    @Autowired
    AccessDeniedHandlerImpl accessDeniedHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .exceptionHandling()
                .authenticationEntryPoint(authenticationExceptionHandler)
                .accessDeniedHandler(accessDeniedHandler);
        http
                .csrf().disable()
                .addFilterBefore(jwtAuthenticateFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll();
                    auth.requestMatchers( "/users/checkToken").permitAll();
                    auth.requestMatchers( "/users/*").permitAll();
                    auth.requestMatchers( "/questions/getByPage2/*").permitAll();
                    auth.requestMatchers("/answers/getAllByQuestionId/*/*").permitAll();
                    auth.requestMatchers("/answers/getAnswersByUserId/*/*").permitAll();
                    auth.requestMatchers( "/questions/getById/**").permitAll();
                    auth.requestMatchers( "/questions/search").permitAll();
                    auth.requestMatchers( "/questions/getUserQuestionByPage").permitAll();
                    auth.requestMatchers( "/users/search").permitAll();
                    auth.requestMatchers( "/users/getUserInfoById/*").permitAll();
                    auth.requestMatchers( "/answers/getQuestionAndAnswer").permitAll();
                    auth.requestMatchers( "/users/register").permitAll();
                    auth.requestMatchers("/users/login").permitAll();
                    auth.requestMatchers("/images/**").permitAll();
                    auth.requestMatchers("/sys_images/**").permitAll();
                    auth.requestMatchers("/subscribe/getFansByUid/*").permitAll();
                    auth.requestMatchers("/subscribe/getFollowedByUid/*").permitAll();
                    auth.requestMatchers("/questions/getHomePageQuestions").permitAll();
                    auth.requestMatchers("/comments/getComments").permitAll();
                    auth.requestMatchers( "/favorites/getFolders").permitAll();
                    auth.requestMatchers("/users/getVerify").anonymous();
                    auth.anyRequest().authenticated(); // 其他请求需要认证
                });
        return http.build();

    }
}
