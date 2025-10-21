package com.qixiang.qixiang_system_api.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Spring Security配置类
 * 配置安全策略和访问权限
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    
    /**
     * 密码编码器Bean
     * @return BCryptPasswordEncoder实例
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    /**
     * CORS配置Bean
     * @return CorsConfigurationSource实例
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true);
        configuration.addAllowedOriginPattern("*"); // 允许所有域名
        configuration.addAllowedHeader("*"); // 允许所有头
        configuration.addAllowedMethod("*"); // 允许所有HTTP方法
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
    
    /**
     * 认证管理器Bean
     * @param config 认证配置
     * @return AuthenticationManager实例
     * @throws Exception 配置异常
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
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
            // 启用CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
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
                // 允许参赛单位查询接口（用于调试）
                .requestMatchers("/api/teams/**").permitAll()
                // 允许H2控制台访问
                .requestMatchers("/h2-console/**").permitAll()
                // 允许健康检查接口
                .requestMatchers("/actuator/**").permitAll()
                // 允许报名数据汇总健康检查接口
                .requestMatchers("/api/registrations/health").permitAll()
                // 允许运动员管理健康检查接口
                .requestMatchers("/api/athletes/health").permitAll()
                // 允许组别管理健康检查接口
                .requestMatchers("/api/groups/health").permitAll()
                // 允许静态资源访问
                .requestMatchers("/static/**", "/css/**", "/js/**", "/images/**").permitAll()
                // 其他所有请求都需要认证
                .anyRequest().authenticated()
            )
            
            // 禁用frame options以允许H2控制台
            .headers(headers -> headers.frameOptions().disable())
            
            // 添加JWT认证过滤器，在UsernamePasswordAuthenticationFilter之前执行
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}
