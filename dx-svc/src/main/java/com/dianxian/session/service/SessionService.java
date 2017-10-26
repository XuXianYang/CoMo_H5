package com.dianxian.session.service;

import com.dianxian.core.utils.time.DateUtils;
import com.dianxian.session.definition.InvalidReason;
import com.dianxian.session.definition.ValidStatus;
import com.dianxian.session.dto.SessionDTO;
import com.dianxian.session.exception.ErrorType;
import com.dianxian.session.exception.SessionAppRuntimeException;
import com.dianxian.session.utils.EmptyChecker;
import com.dianxian.session.utils.HmacUtils;
import com.dianxian.session.utils.MapUtils;
import com.dianxian.session.utils.SessionIdGenerator;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Date;
import java.util.Map;

@Service
public class SessionService {

    @Autowired
    private SessionAppProperties sessionAppProperties;

    @Autowired
    private RedisCacheService redisCacheService;

    private static SecureRandom random = new SecureRandom();

    private final static String STORE_ACCESS_TIME = "store-access-time";


    //HMAC key like salt
    public static final String HMAC_KEY = "vfq3qjBIqbizbQIbWtcG7gE4WwPh8C0ElitJ2ARWX7GaxioyKJp63uLpbYSaZwZwv54J7YVHG/4LWMSA1vhDBg==";




    /**
     * 创建会话, 之前需要判断该用户所有非失效的Session并且设置为失效
     *
     * @param userId         用户ID
     * @return json object
     */
    public String createSession(Long userId, int userType) {
        if (EmptyChecker.isEmpty(userId)) {
            throw new SessionAppRuntimeException(ErrorType.USER_ID_CAN_NOT_BE_NULL);
        }
        String sessionId = SessionIdGenerator.generate();
        SessionDTO sessionDTO = new SessionDTO(sessionId, userId);
        sessionDTO.setUserType(userType);

        // set createdAt and partitionKey
        Date createdAt = new Date();
        sessionDTO.setCreatedAt(createdAt);
        sessionDTO.setAccessTime(createdAt);
        sessionDTO.setStartTime(createdAt);


        //add redis cache
        syncDbSessionDtoToCache(sessionDTO);
        //add session store
//        if(sessionDTO!=null) {
//            updateSessionStoreAccessTime(sessionId, createdAt);
//        }
        return generateSelfCheckSessionId(sessionId);
    }

    /**
     * 检查Session有效性
     * (1)首先自校验
     * (2)判断是否过期
     * (3)判断是否有效，若失效，给出原因
     * (4)判断企业个人标志
     */
    public SessionDTO checkSession(String selfCheckSessionId) {
        SessionDTO sessionDTO = innerCheckSession(selfCheckSessionId);
        return sessionDTO;
    }

    /**
     * load sessionDTO by selfCheckSessionId
     *
     * @param selfCheckSessionId
     * @return
     */
    public SessionDTO loadSession(String selfCheckSessionId) {
        SessionDTO sessionDTO = innerCheckSession(selfCheckSessionId);

        return sessionDTO;
    }


    /**
     * 保持Session有效性
     * (1)首先自校验
     * (2)判断是否过期
     * (3)判断是否有效，若失效，给出原因
     * 若有效，更新最后访问时间,
//     * 同时更新sesstion store的最后访问时间
     */
    public Map keepSession(String selfCheckSessionId) {
        SessionDTO sessionDTO = innerCheckSession(selfCheckSessionId);

        Date accessTime = sessionDTO.getAccessTime();
        Date now = new Date();
        if (DateUtils.diffSecond(accessTime, now) > sessionAppProperties.getSessionKeepIntervalSecondInteger()) {
            updateSessionAccessTime(sessionDTO, now);
//            updateSessionStoreAccessTime(sessionDTO.getSessionId(), now);
        }
        return MapUtils.buildKeyValueMap("sessionId", selfCheckSessionId);
    }


    /**
     * 使Session失效（根据sessionId）
     * (1)根据sessionId检索session，设置为失效
     * 调用接口需要给出失效原因：0正常退出 1超时 2IP地址变更 3单终端登录控制 4疑似攻击 5黑名单 9其他原因
     */
    public int invalidateSessionById(String selfCheckSessionId, String invalidReason) {
        if (!checkSessionId(selfCheckSessionId)) {
            throw new SessionAppRuntimeException(ErrorType.INVALID_SESSION_ID);
        }

        String sessionId = drawSessionIdFromSelfCheckSessionId(selfCheckSessionId);
        SessionDTO sessionDTO = redisCacheService.getCachedSession(sessionId);
        if (sessionDTO == null) {
            return 0;
        }

        updateSessionToInvalid(sessionDTO, invalidReason);
        return 1;
    }

    /**
     * 更新数据库中的Session为无效，
     *
     * @param selfCheckSessionId 自校验的SessionID
     * @param errorType          出错原因，目前支持失效和超出最大有效时间
     * @return 更新数据的数量
     */
    public int updateSessionToInvalidWithError(String selfCheckSessionId, ErrorType errorType) {
        if (errorType != ErrorType.SESSION_EXPIRED && errorType != ErrorType.SESSION_EXCEED_MAX_VALID_HOUR) {
            return 0;
        }

        String sessionId = drawSessionIdFromSelfCheckSessionId(selfCheckSessionId);
        SessionDTO sessionDTO = redisCacheService.getCachedSession(sessionId);
        if (sessionDTO == null) {
            return 0;
        }

        if (errorType == ErrorType.SESSION_EXPIRED) {
            return updateSessionToInvalid(sessionDTO, InvalidReason.TIMEOUT.key);
        } else if (errorType == ErrorType.SESSION_EXCEED_MAX_VALID_HOUR) {
            return updateSessionToInvalid(sessionDTO, InvalidReason.EXCEED_MAX_VALID_HOUR.key);
        }
        return 0;
    }

    /**
     * put session and session store into redis cache
//     * @param sessionId
     */


//    private SessionDTO findSessionDtoBySessionId(String sessionId) {
//        SessionDTO sessionDTO = redisCacheService.getCachedSession(sessionId);
//        if (sessionDTO == null) {
//            List<SessionDTO> list = sessionDAO.findSessionList(MapUtils.buildKeyValueMap("sessionId", sessionId));
//            if (list == null || list.size() == 0) {
//                return null;
//            }
//            sessionDTO = list.get(0);
//            redisCacheService.putSessionToCache(sessionDTO);
//        }
//        return sessionDTO;
//    }
    public void removeSession(String sid){
        redisCacheService.removeCachedSession(sid);
    }

    private SessionDTO innerCheckSession(String selfCheckSessionId) {
        if (!checkSessionId(selfCheckSessionId)) {
            throw new SessionAppRuntimeException(ErrorType.INVALID_SESSION_ID);
        }
        String sessionId = drawSessionIdFromSelfCheckSessionId(selfCheckSessionId);
        SessionDTO sessionDTO = redisCacheService.getCachedSession(sessionId);
        if (sessionDTO == null) {
            throw new SessionAppRuntimeException(ErrorType.SESSION_NOT_EXIST);
        }

        Date accessTime = sessionDTO.getAccessTime();
        if (isSessionTimeout(accessTime)) {
            throw new SessionAppRuntimeException(ErrorType.SESSION_EXPIRED);
        }

        Date createTime = sessionDTO.getStartTime();
        if (!isSessionInMaxValidHour(createTime)) {
            throw new SessionAppRuntimeException(ErrorType.SESSION_EXCEED_MAX_VALID_HOUR);
        }

        String isValid = sessionDTO.getIsValid();
        if (!"1".equals(isValid)) {
            throwExceptionWithInvalidReason(sessionDTO);
        }

        return sessionDTO;
    }


    private void throwExceptionWithInvalidReason(SessionDTO sessionDTO) {
        //失效原因 0正常退出 1超时 2IP地址变更 3单终端登录控制 4疑似攻击 5黑名单 9其他原因
        String invalidReason = sessionDTO.getInvalidReason();
        if (InvalidReason.LOGOUT.key.equals(invalidReason)||InvalidReason.TIMEOUT.key.equals(invalidReason)) {
            throw new SessionAppRuntimeException(ErrorType.SESSION_HAS_LOGOUT);
        } else if (InvalidReason.MULTIPLE_CONSOLE.key.equals(invalidReason)) {
            throw new SessionAppRuntimeException(ErrorType.LOGIN_ON_ANOTHER_CONSOLE);
        } else {
            throw new SessionAppRuntimeException(ErrorType.UNKNOWN_ERROR);
        }
    }

    private int updateSessionToInvalid(SessionDTO sessionDTO, String invalidReason) {
        sessionDTO.setIsValid(ValidStatus.INVALID.key);
        sessionDTO.setInvalidReason(invalidReason);
        //add redis cache
        SessionDTO sessionDTO1 = syncDbSessionDtoToCache(sessionDTO);
        if (sessionDTO1 == null){
            return 0;
        }
        return 1;
    }
    /**
     * put session and session store into redis cache
     */
    public SessionDTO syncDbSessionDtoToCache(SessionDTO sessionDTO) {

        if (redisCacheService.isRedisSwitchOn()) {
            redisCacheService.putSessionToCache(sessionDTO);
            return sessionDTO;
            }
        return null;
    }

    private void updateSessionAccessTime(SessionDTO sessionDTO, Date accessTime) {

        sessionDTO.setAccessTime(accessTime);
        //add redis cache
        syncDbSessionDtoToCache(sessionDTO);
    }

    public boolean isSessionTimeout(Date accessTime) {
        return System.currentTimeMillis() - accessTime.getTime() > ((long) sessionAppProperties.getSessionTimeoutSecondInteger()) * 1000L;
    }

    public boolean isSessionInMaxValidHour(Date createTime) {
        return System.currentTimeMillis() - createTime.getTime() < ((long) sessionAppProperties.getSessionMaxValidHourInteger()) * 60 * 60 * 1000L;
    }

    public String generateSelfCheckSessionId(String sessionId) {
        return sessionId + "," + encryptSessionIdWithHmac(sessionId);
    }

    public String drawSessionIdFromSelfCheckSessionId(String selfCheckSessionId) {
        return selfCheckSessionId.substring(0, selfCheckSessionId.indexOf(","));
    }

    public String drawCheckCodeFromSelfCheckSessionId(String selfCheckSessionId) {
        return selfCheckSessionId.substring(selfCheckSessionId.indexOf(",") + 1);
    }

    public boolean checkSessionId(String selfCheckSessionId) {
        if (EmptyChecker.isEmpty(selfCheckSessionId) || !selfCheckSessionId.contains(",")) {
            return false;
        }
        String sessionId = drawSessionIdFromSelfCheckSessionId(selfCheckSessionId);
        String checkCode = drawCheckCodeFromSelfCheckSessionId(selfCheckSessionId);
        return encryptSessionIdWithHmac(sessionId).equals(checkCode);
    }

    private String encryptSessionIdWithHmac(String sessionId) {
        String securitySalt = sessionAppProperties.getSessionSecuritySalt();
        return HmacUtils.encryptAsString(sessionId + ":" + securitySalt, HMAC_KEY);
    }

    private void checkOldSessionsAndUpdateToInvalidForCreateNewSession(Long userId, String loginConsole) {
//        if (list != null && list.size() > 0) {
//            for (SessionDTO sessionDTO : list) {
//                String invalidReason = InvalidReason.MULTIPLE_CONSOLE.key;
//                if (isSessionTimeout(sessionDTO.getAccessTime())) {
//                    invalidReason = InvalidReason.TIMEOUT.key;
//                }
//                updateSessionToInvalid(sessionDTO, invalidReason);
//            }
//        }
    }


    /**
     * add a new session store into redis cache
     * @param sessionId
     */
//    private void updateSessionStoreAccessTime(String sessionId, Date accessTime){
//        if (redisCacheService.isRedisSwitchOn()) {
//            redisCacheService.putSessionStore(SessionStoreIdGenerator.generateSessionStoreId(sessionId), generateEmptySessionStoreValue(accessTime));
//        }
//    }
//
    /**
     * generate the empty session store which only store the access time to keep alive.
     * @param accessTime
     * @return
     */
    private Map<String, String> generateEmptySessionStoreValue(Date accessTime){
        Map<String, String> value = Maps.newHashMap();
        value.put(STORE_ACCESS_TIME, String.valueOf(accessTime.getTime()));
        return value;
    }

//    private String getSessionStoreId(String selfCheckSessionId){
//        return SessionStoreIdGenerator.generateSessionStoreId(drawSessionIdFromSelfCheckSessionId(selfCheckSessionId));
//    }
//
//
//    public void putSessionStore(String selfCheckSessionId, Map<String, String> values){
//        SessionDTO sessionDTO = innerCheckSession(selfCheckSessionId);
//        if(sessionDTO!=null){
//            redisCacheService.putSessionStore(getSessionStoreId(selfCheckSessionId), values);
//            keepSession(selfCheckSessionId);
//        }else{
//            Logger.warn(this, "Can't put session store since can't find the session by id " + selfCheckSessionId);
//        }
//    }
//
//    public List<String> getSessionStoreAllKeys(String selfCheckSessionId){
//        List<String> keys = Lists.newArrayList();
//        SessionDTO sessionDTO = innerCheckSession(selfCheckSessionId);
//        if(sessionDTO!=null){
//            keys = redisCacheService.getSessionStoreAllKeys(getSessionStoreId(selfCheckSessionId));
//            keepSession(selfCheckSessionId);
//        }else{
//            Logger.warn(this, "Can't get keys from session store since can't find the session by id " + selfCheckSessionId);
//        }
//        return keys;
//    }
//
//    public Map<String, String> getSessionStoreValuesByKeys(String selfCheckSessionId, List<String> keys){
//        Map<String, String> values = Maps.newHashMap();
//        SessionDTO sessionDTO = innerCheckSession(selfCheckSessionId);
//        if(sessionDTO!=null){
//            values = redisCacheService.getSessionStoreByKeys(getSessionStoreId(selfCheckSessionId), keys);
//            keepSession(selfCheckSessionId);
//        }else{
//            Logger.warn(this, "Can't get values from session store since can't find the session by id " + selfCheckSessionId);
//        }
//
//        return values;
//    }

}
