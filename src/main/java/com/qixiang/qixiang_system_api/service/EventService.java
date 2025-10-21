package com.qixiang.qixiang_system_api.service;

import com.qixiang.qixiang_system_api.entity.Event;
import com.qixiang.qixiang_system_api.repository.EventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 比赛项目服务类
 * 提供比赛项目相关的业务逻辑
 */
@Service
public class EventService {
    
    private static final Logger logger = LoggerFactory.getLogger(EventService.class);
    
    @Autowired
    private EventRepository eventRepository;
    
    /**
     * 根据组别ID获取可参加的比赛项目
     * @param groupId 竞赛组别ID
     * @return 可参加的比赛项目列表
     */
    public List<Event> getEventsByGroupId(Long groupId) {
        logger.info("根据组别ID {} 获取可参加的项目", groupId);
        
        try {
            List<Event> events = eventRepository.findEventsByGroupId(groupId);
            logger.info("成功获取组别 {} 的 {} 个可参加项目", groupId, events.size());
            return events;
            
        } catch (Exception e) {
            logger.error("根据组别ID {} 获取可参加项目失败: {}", groupId, e.getMessage(), e);
            throw new RuntimeException("获取可参加项目失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 获取所有比赛项目
     * @return 所有比赛项目列表
     */
    public List<Event> getAllEvents() {
        logger.info("获取所有比赛项目");
        
        try {
            List<Event> events = eventRepository.findAll();
            logger.info("成功获取所有 {} 个比赛项目", events.size());
            return events;
            
        } catch (Exception e) {
            logger.error("获取所有比赛项目失败: {}", e.getMessage(), e);
            throw new RuntimeException("获取所有比赛项目失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 根据项目类型获取比赛项目
     * @param eventType 项目类型
     * @return 指定类型的比赛项目列表
     */
    public List<Event> getEventsByEventType(String eventType) {
        logger.info("根据项目类型 {} 获取比赛项目", eventType);
        
        try {
            List<Event> events = eventRepository.findByEventType(eventType);
            logger.info("成功获取 {} 类型的 {} 个比赛项目", eventType, events.size());
            return events;
            
        } catch (Exception e) {
            logger.error("根据项目类型 {} 获取比赛项目失败: {}", eventType, e.getMessage(), e);
            throw new RuntimeException("获取比赛项目失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 根据组别ID获取必报项目
     * @param groupId 竞赛组别ID
     * @return 必报项目列表
     */
    public List<Event> getMandatoryEventsByGroupId(Long groupId) {
        logger.info("根据组别ID {} 获取必报项目", groupId);
        
        try {
            List<Event> events = eventRepository.findMandatoryEventsByGroupId(groupId);
            logger.info("成功获取组别 {} 的 {} 个必报项目", groupId, events.size());
            return events;
            
        } catch (Exception e) {
            logger.error("根据组别ID {} 获取必报项目失败: {}", groupId, e.getMessage(), e);
            throw new RuntimeException("获取必报项目失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 根据组别ID获取选报项目
     * @param groupId 竞赛组别ID
     * @return 选报项目列表
     */
    public List<Event> getOptionalEventsByGroupId(Long groupId) {
        logger.info("根据组别ID {} 获取选报项目", groupId);
        
        try {
            List<Event> events = eventRepository.findOptionalEventsByGroupId(groupId);
            logger.info("成功获取组别 {} 的 {} 个选报项目", groupId, events.size());
            return events;
            
        } catch (Exception e) {
            logger.error("根据组别ID {} 获取选报项目失败: {}", groupId, e.getMessage(), e);
            throw new RuntimeException("获取选报项目失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 根据组别ID和项目类型获取可参加的比赛项目
     * @param groupId 竞赛组别ID
     * @param eventType 项目类型
     * @return 可参加的比赛项目列表
     */
    public List<Event> getEventsByGroupIdAndEventType(Long groupId, String eventType) {
        logger.info("根据组别ID {} 和项目类型 {} 获取可参加的项目", groupId, eventType);
        
        try {
            List<Event> events = eventRepository.findEventsByGroupIdAndEventType(groupId, eventType);
            logger.info("成功获取组别 {} {} 类型的 {} 个可参加项目", groupId, eventType, events.size());
            return events;
            
        } catch (Exception e) {
            logger.error("根据组别ID {} 和项目类型 {} 获取可参加项目失败: {}", groupId, eventType, e.getMessage(), e);
            throw new RuntimeException("获取可参加项目失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 检查项目是否可以被指定组别参加
     * @param eventId 项目ID
     * @param groupId 组别ID
     * @return 是否可以参加
     */
    public boolean isEventAvailableForGroup(Long eventId, Long groupId) {
        logger.info("检查项目 {} 是否可以被组别 {} 参加", eventId, groupId);
        
        try {
            boolean isAvailable = eventRepository.isEventAvailableForGroup(eventId, groupId);
            logger.info("项目 {} 是否可以被组别 {} 参加: {}", eventId, groupId, isAvailable);
            return isAvailable;
            
        } catch (Exception e) {
            logger.error("检查项目 {} 是否可以被组别 {} 参加失败: {}", eventId, groupId, e.getMessage(), e);
            throw new RuntimeException("检查项目可参加性失败: " + e.getMessage(), e);
        }
    }
}
