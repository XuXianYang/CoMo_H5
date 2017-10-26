package com.dianxian.school.service;

import com.dianxian.core.resource.QueryPaging;
import com.dianxian.core.utils.json.JsonUtils;
import com.dianxian.school.dto.SneakingMessageDto;
import com.dianxian.testframework.AbstractServiceTest;
import com.github.pagehelper.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by XuWenHao on 10/11/2016.
 */
public class MessageServiceTest extends AbstractServiceTest {
    @Autowired
    MessageService messageService;

    @Test
    public void test_hasNewSneakingMessages() throws ParseException {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = df.parse("2016-10-11 11:02:05");
        boolean result = messageService.hasNewSneakingMessages(1L, null);
        logger.info("result {}", result);
    }

    @Test
    public void test_getSneakingMessagesWithContent() {
        Page<SneakingMessageDto> result = messageService.getSneakingMessagesWithContent(2L, new QueryPaging());
        logger.info(JsonUtils.toJson(result));
    }
}
