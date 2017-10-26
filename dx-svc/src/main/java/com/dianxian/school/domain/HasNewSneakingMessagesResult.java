package com.dianxian.school.domain;

import java.util.Date;

/**
 * Created by XuWenHao on 10/11/2016.
 */
public class HasNewSneakingMessagesResult {
    private boolean hasNewSneakingMessages;
    private Date lastViewTime;

    public boolean isHasNewSneakingMessages() {
        return hasNewSneakingMessages;
    }

    public void setHasNewSneakingMessages(boolean hasNewSneakingMessages) {
        this.hasNewSneakingMessages = hasNewSneakingMessages;
    }

    public Date getLastViewTime() {
        return lastViewTime;
    }

    public void setLastViewTime(Date lastViewTime) {
        this.lastViewTime = lastViewTime;
    }
}
