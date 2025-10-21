package com.qixiang.qixiang_system_api.config;

import com.qixiang.qixiang_system_api.entity.Team;
import com.qixiang.qixiang_system_api.entity.User;
import com.qixiang.qixiang_system_api.repository.TeamRepository;
import com.qixiang.qixiang_system_api.repository.UserRepository;
import com.qixiang.qixiang_system_api.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JWT认证过滤器
 * 继承OncePerRequestFilter，确保每个请求只执行一次
 * 负责从请求头中提取JWT Token并验证用户身份
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TeamRepository teamRepository;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                  HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        try {
            // 1. 从请求头中提取JWT Token
            String jwt = getJwtFromRequest(request);
            
            // 2. 如果Token存在且有效，进行认证
            if (jwt != null && jwtUtil.isTokenFormatValid(jwt)) {
                // 验证Token并获取用户名
                String username = jwtUtil.extractUsername(jwt);
                
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    // 获取角色信息
                    String role = jwtUtil.extractRole(jwt);
                    String orgCode = jwtUtil.extractOrgCode(jwt);
                    
                    logger.debug("JWT Token解析成功 - 用户: {}, 角色: {}, 机构: {}", username, role, orgCode);
                    
                    // 根据角色加载用户详细信息
                    Object userDetails = loadUserByRoleAndUsername(role, username);
                    
                    if (userDetails != null) {
                        // 创建认证对象
                        UsernamePasswordAuthenticationToken authentication = 
                            new UsernamePasswordAuthenticationToken(
                                userDetails, 
                                null, 
                                getAuthorities(role)
                            );
                        
                        // 设置认证详情
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        
                        // 将认证对象设置到SecurityContext
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        
                        logger.info("用户认证成功: {} (角色: {})", username, role);
                    } else {
                        logger.warn("用户数据加载失败: {} (角色: {})", username, role);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("JWT认证过程中发生错误: {}", e.getMessage(), e);
            // 清除可能存在的认证信息
            SecurityContextHolder.clearContext();
        }
        
        // 继续过滤器链
        filterChain.doFilter(request, response);
    }
    
    /**
     * 从请求头中提取JWT Token
     * @param request HTTP请求
     * @return JWT Token字符串，如果不存在返回null
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        
        // 检查Bearer Token格式
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // 去掉"Bearer "前缀
        }
        
        return null;
    }
    
    /**
     * 根据角色和用户名加载用户详细信息
     * @param role 用户角色
     * @param username 用户名或ID
     * @return 用户详细信息对象
     */
    private Object loadUserByRoleAndUsername(String role, String username) {
        try {
            if ("ADMIN".equals(role)) {
                // 加载管理员用户
                List<User> users = userRepository.findByUsername(username);
                if (!users.isEmpty()) {
                    User user = users.get(0); // 取第一个匹配的用户
                    logger.debug("管理员用户加载成功: {}", user.getUsername());
                    return user;
                }
            } else if ("TEAM".equals(role)) {
                // 加载参赛单位（用户名可能是ID或name）
                try {
                    // 尝试按ID查找
                    Long teamId = Long.parseLong(username);
                    Optional<Team> teamOpt = teamRepository.findById(teamId);
                    if (teamOpt.isPresent()) {
                        Team team = teamOpt.get();
                        logger.debug("参赛单位加载成功(ID): {}", team.getName());
                        return team;
                    }
                } catch (NumberFormatException e) {
                    // 如果不是数字，尝试按name查找（需要在默认运动会中查找）
                    // 这里我们暂时使用teamCode查找，因为TeamRepository没有findByName方法
                    Team team = teamRepository.findByTeamCode(username);
                    if (team != null) {
                        logger.debug("参赛单位加载成功(Code): {}", team.getName());
                        return team;
                    }
                }
            }
            
            logger.warn("未找到用户数据: 角色={}, 用户名={}", role, username);
            return null;
            
        } catch (Exception e) {
            logger.error("加载用户数据时发生错误: 角色={}, 用户名={}, 错误={}", role, username, e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * 根据角色获取权限列表
     * @param role 用户角色
     * @return 权限列表
     */
    private List<SimpleGrantedAuthority> getAuthorities(String role) {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        
        if ("ADMIN".equals(role)) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        } else if ("TEAM".equals(role)) {
            authorities.add(new SimpleGrantedAuthority("ROLE_TEAM"));
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        } else {
            // 默认权限
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }
        
        return authorities;
    }
    
    /**
     * 判断是否应该跳过此过滤器
     * 对于公开的API端点，可以跳过JWT验证
     * @param request HTTP请求
     * @return 是否跳过
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        
        // 跳过公开的API端点
        return path.startsWith("/api/auth/login") ||
               path.startsWith("/api/teams/by-org-code/") ||
               path.startsWith("/h2-console") ||
               path.startsWith("/actuator") ||
               path.startsWith("/static") ||
               path.startsWith("/css") ||
               path.startsWith("/js") ||
               path.startsWith("/images");
    }
}
