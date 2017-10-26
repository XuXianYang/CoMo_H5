package com.dianxian.session.facade;

import com.dianxian.session.domain.SessionInfo;
import com.dianxian.session.dto.SessionDTO;
import com.dianxian.session.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Created by y on 2016/6/21.
 */
@Component
public class SessionFacade {
    @Autowired
    private SessionService sessionService;
    public SessionInfo createSession(Long userId, int userType){

        String sid = sessionService.createSession(userId, userType);
        SessionInfo sessionInfo = new SessionInfo();
        try{
            sid = URLEncoder.encode(sid, "UTF-8");
        }catch (Exception e){
            // nothing
        }
        sessionInfo.setSid(sid);
        return sessionInfo;
    }

    public SessionDTO loadSession(String sessionId){
        try{
            sessionId = URLDecoder.decode(sessionId, "UTF-8");
        }catch (Exception e){
            // nothing
        }
        return sessionService.loadSession(sessionId);
    }

    public void keepSession(String sessionId) {
        try{
            sessionId = URLDecoder.decode(sessionId, "UTF-8");
        }catch (Exception e){
            // nothing
        }
        sessionService.keepSession(sessionId);
    }
}
