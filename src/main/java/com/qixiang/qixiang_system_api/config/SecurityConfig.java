package com.qixiang.qixiang_system_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security配置类
 * 配置安全策略和访问权限
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    /**
     * 密码编码器Bean
     * @return BCryptPasswordEncoder实例
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    /**
     * 安全过滤器链配置
     * @param http HttpSecurity对象
     * @return SecurityFilterChain实例
     * @throws Exception 配置异常
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 禁用CSRF保护（因为使用JWT）
            .csrf(csrf -> csrf.disable())
            
            // 配置会话管理为无状态
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // 配置请求授权规则
            .authorizeHttpRequests(authz -> authz
                // 允许认证相关的接口无需认证
                .requestMatchers("/api/auth/**").permitAll()
                // 允许数据管理接口（用于测试和调试）
                .requestMatchers("/api/data/**").permitAll()
                // 允许H2控制台访问
                .requestMatchers("/h2-console/**").permitAll()
                // 允许健康检查接口
                .requestMatchers("/actuator/**").permitAll()
                // 允许静态资源访问
                .requestMatchers("/static/**", "/css/**", "/js/**", "/images/**").permitAll()
                // 其他所有请求都需要认证
                .anyRequest().authenticated()
            )
            
            // 禁用frame options以允许H2控制台
            .headers(headers -> headers.frameOptions().disable());
        
        return http.build();
    }
}
