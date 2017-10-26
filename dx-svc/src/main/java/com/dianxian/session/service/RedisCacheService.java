package com.dianxian.session.service;

import com.dianxian.redis.RedisCacheConfig;
import com.dianxian.redis.RedisCacheStore;
import com.dianxian.session.dto.SessionDTO;
import jersey.repackaged.com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.testng.collections.Lists;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;
import java.util.Map;
/**
 * Created by y on 2016/5/4.
 */
@Component
public class RedisCacheService {
    @Autowired
    private RedisCacheConfig redisCacheConfig;
    @Autowired
    private SessionAppProperties sessionAppProperties;

//    定义客户端对象
    private RedisCacheStore<SessionDTO> cache;

    private boolean isSwitchOn = true;

    private int redisCacheExpireSeconds = 0;

    public boolean isRedisSwitchOn() {
        return isSwitchOn;
    }

    private int getRedisCacheExpireSeconds() {
        return redisCacheExpireSeconds;
    }

    private int getSessionStoreExpireSeconds(){
        return redisCacheExpireSeconds*2;
    }

    //在bean初始化之后创建客户端
    @PostConstruct
    public void initRedisClient() {
        try {
//            String sessionRedisSwitch = sessionAppProperties.getSessionRedisSwitch();
            String sessionRedisSwitch = "on";
            isSwitchOn = (sessionRedisSwitch != null && sessionRedisSwitch.trim().equalsIgnoreCase("on"));
        } catch (Throwable e) {
//            Logger.error(this, "Error occurred when check redis switch on/off.", e);
        }

        try {
            redisCacheExpireSeconds = sessionAppProperties.getSessionRedisKeepHourInteger() * 60 * 60;
        } catch (Throwable e) {
//            Logger.error(this, "Error occurred when initialize redisCacheExpireSeconds.", e);
        }

        if (isRedisSwitchOn()) {
            try {
                cache = new RedisCacheStore<SessionDTO>(redisCacheConfig, SessionDTO.class, null, null);
            } catch (Throwable e) {
//                Logger.error(this, "Error occurred when create session cache store.", e);
            }
        }
    }

    public void putSessionToCache(SessionDTO dto) {
        if (isRedisSwitchOn()) {
            try {
                if (dto == null) {
                    return;
                }
                if (cache != null) {
                    cache.put(dto.getSessionId(), dto, getRedisCacheExpireSeconds());
                }
            } catch (Throwable e) {
//                Logger.warn(this, "Error occurred when add session to cache.", e);
            }
        }
    }

    public SessionDTO getCachedSession(String sessionId) {
        if (isRedisSwitchOn()) {
            try {
                if (sessionId == null || cache == null) {
                    return null;
                }
                return cache.get(sessionId);
            } catch (Throwable e) {
//                Logger.warn(this, "Error occurred when get cached session.", e);
            }
        }
        return null;
    }

    public void removeCachedSession(String sessionId) {
        if (isRedisSwitchOn()) {
            try {
                if (sessionId == null || cache == null) return;
                if(sessionId != null) cache.remove(sessionId);
            } catch (Throwable e) {
//                Logger.warn(this, "Error occurred when get cached session.", e);
            }
        }
    }


//    public void putSessionStore(String sessionId, Map<String, String> values){
//        if (isRedisSwitchOn()) {
//            try {
//                if (StringUtils.isBlank(sessionId) || values==null || values.isEmpty()) {
//                    return;
//                }
//                if (cache != null) {
////                    Logger.info(this, "Put in session store value is:" + values);
//                    cache.hmset(sessionId, values, getSessionStoreExpireSeconds());
//                }
//            } catch (Throwable e) {
////                Logger.warn(this, "Error occurred when add session store to cache.", e);
//            }
//        }
//    }

    public Map<String, String> getSessionStoreByKeys(String sessionId, List<String> keys){
        Map<String, String> result = Maps.newHashMap();
        if (isRedisSwitchOn()) {
            try {
//                if (StringUtils.isBlank(sessionId) || cache == null || keys == null || keys.isEmpty()) {
//                    return result;
//                }
//                result = cache.hmget(sessionId, keys.toArray(new String[keys.size()]));
            } catch (Throwable e) {
//                Logger.warn(this, "Error occurred when get cached session store.", e);
            }
        }
        return result;
    }

    public List<String> getSessionStoreAllKeys(String sessionId){
        List<String> keys = Lists.newArrayList();
        if (isRedisSwitchOn()) {
            try {
//                if (StringUtils.isBlank(sessionId) || cache == null) {
//                    return keys;
//                }
                //TODO call redis
            } catch (Throwable e) {
//                Logger.warn(this, "Error occurred when get cached session store all keys.", e);
            }
        }
        return keys;
    }

    // 在bean销毁时关闭客户端
    @PreDestroy
    public void destroyRedisClient() {
//        if (isRedisSwitchOn() && cache != null) {
//            try {
//                cache.shutdown();
//            } catch (Throwable e) {
//                Logger.error(this, "Error occurred when shutdown cache.", e);
//            }
//        }
    }
}
