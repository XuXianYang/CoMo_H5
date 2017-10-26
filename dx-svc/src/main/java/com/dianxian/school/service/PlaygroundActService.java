package com.dianxian.school.service;

import com.dianxian.core.exception.BizLogicException;
import com.dianxian.core.resource.PagingInfo;
import com.dianxian.core.resource.QueryPaging;
import com.dianxian.school.consts.ServiceCodes;
import com.dianxian.school.dao.DiscussMapper;
import com.dianxian.school.dao.PlaygroundActMapper;
import com.dianxian.school.dao.ZanMapper;
import com.dianxian.school.domain.*;
import com.dianxian.school.dto.PlaygroundActDto;
import com.dianxian.storage.dto.ResourceFileCategory;
import com.dianxian.storage.dto.ResourceFileDto;
import com.dianxian.storage.service.StorageService;
import com.dianxian.user.domain.User;
import com.dianxian.user.dto.StudentDto;
import com.dianxian.user.service.UserService;
import com.dianxian.web.utils.DisplayUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.ws.rs.core.UriBuilder;
import java.io.InputStream;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by chenzhong on 2016/11/7.
 */
@Service
public class PlaygroundActService {
    @Autowired
    private StorageService storageService;
    @Autowired
    private PlaygroundActMapper playgroundActMapper;
    @Autowired
    private ZanMapper zanMapper;
    @Autowired
    private DiscussMapper discussMapper;
    @Autowired
    private UserService userService;

    /**
     * 获取操场活动列表
     * chenzhong
     *
     * @param studentDto
     * @param pageInfo
     * @return
     */
    public PagingInfo<PlaygroundActDto> listPgAct(StudentDto studentDto, QueryPaging pageInfo) {
        Page<PlaygroundAct> page = PageHelper.startPage(pageInfo.getPageNum(), pageInfo.getPageSize());

        PlaygroundActExample ex = new PlaygroundActExample();
        ex.setOrderByClause("create_at desc");
        ex.createCriteria().andSchoolIdEqualTo(studentDto.getSchoolId());
        List<PlaygroundAct> playgroundActs = playgroundActMapper.selectByExample(ex);
        List<PlaygroundActDto> dtos = handleDetailByPlaygroundAct(playgroundActs);

        return new PagingInfo<PlaygroundActDto>(page, dtos);
    }

    /**
     * 用户操场活动图片文本保存7牛保存数据表
     * chenzhong
     *
     * @param userId
     * @param content
     * @param ins
     */
    public void sendPgAct(Long userId, String content, Map<String, InputStream> ins) {
        StudentDto studentDto = userService.getStudentByUserId(userId);

        List<String> imgUrls = Lists.newArrayList();
        if (ins != null && ins.size() > 0) {
            for (Map.Entry<String, InputStream> entry : ins.entrySet()) {
                String originFileName = entry.getKey();
                String destFileName = String.format("school/%s/pg_act/%s/%s", studentDto.getSchoolId(), UUID.randomUUID(), originFileName);
                InputStream in = entry.getValue();

                //保存并拼接url 保存再字段中
                ResourceFileDto dto = storageService.save(userId, in, originFileName, destFileName, ResourceFileCategory.PLAYGROUND_ACT_IMG);
                UriBuilder uriBuilder = UriBuilder.fromUri(dto.getUrlPrefix());
                uriBuilder.path(dto.getRelativePath());
                imgUrls.add(uriBuilder.build().toString());
            }
        }

        String strImg = null;
        if (imgUrls.size() > 0) {
            strImg = StringUtils.join(imgUrls, ",");
        }

        PlaygroundAct playgroundAct = new PlaygroundAct();
        playgroundAct.setContent(StringUtils.isNotEmpty(content) ? content : null);
        playgroundAct.setCreateAt(Calendar.getInstance().getTime());
        playgroundAct.setSchoolId(studentDto.getSchoolId());
        playgroundAct.setUserId(userId);
        playgroundAct.setImgUrl(strImg);

        playgroundActMapper.insert(playgroundAct);
    }

    /**
     * 删除活动
     * chenzhong
     *
     * @param actId
     */
    public void deletePgAct(Long actId) {
        //删除活动表
        playgroundActMapper.deleteByPrimaryKey(actId);

        //删除评论表
        DiscussExample ex = new DiscussExample();
        ex.createCriteria().andActIdEqualTo(actId);
        discussMapper.deleteByExample(ex);

        //删除zan表
        ZanExample example = new ZanExample();
        example.createCriteria().andActIdEqualTo(actId);
        zanMapper.deleteByExample(example);

    }

    /**
     * 查询单个活动详情
     * chenzhong
     *
     * @param actId
     * @return
     */
    public PlaygroundActDto listDetailPgAct(Long actId) {
        PlaygroundAct act = playgroundActMapper.selectByPrimaryKey(actId);
        if (act == null)
            throw new BizLogicException(ServiceCodes.PLAYGROUND_ACT_DATA_IS_EXISTS, "PLAYGROUND_ACT_DATA_IS_EXISTS");

        PagingInfo<Discuss> discuss = getPageDiscuss(actId, new QueryPaging());/*评论第一次取10*/
        PagingInfo<Zan> zan = getPageZan(actId, new QueryPaging());/*赞第一次取10*/

        PlaygroundActDto dto = new PlaygroundActDto();
        BeanUtils.copyProperties(act, dto);
        //处理评论信息
        dto.setDiscussCount(discuss == null ? 0 : Long.valueOf(discuss.getTotal()).intValue());
        dto.setDiscusses(discuss == null ? null : discuss.getList());
        //处理赞信息
        dto.setZanCount(zan == null ? 0 : Long.valueOf(zan.getTotal()).intValue());
        dto.setZans(zan == null ? null : zan.getList());

        if (StringUtils.isNotEmpty(dto.getImgUrl())) {
            dto.setImgs(CollectionUtils.arrayToList(dto.getImgUrl().split(",")));
            dto.setImgUrl(null);
        }
        dto.setStrCreateAt(DisplayUtils.formatDateTime(dto.getCreateAt()));

        //封装用户信息
        List<Long> userid = Lists.newLinkedList();
        userid.add(dto.getUserId());
        Map<Long, User> users = userService.getUsersByIdsToMap(userid);
        dto.setUserDto(users.get(dto.getUserId()));

        return dto;
    }

    /**
     * 获取分页赞
     * chenzhong
     *
     * @param actId
     * @param pageInfo
     * @return
     */
    public PagingInfo<Zan> getPageZan(Long actId, QueryPaging pageInfo) {
        if (actId == null) return null;
        Page<Zan> page = PageHelper.startPage(pageInfo.getPageNum(), pageInfo.getPageSize());

        ZanExample ex2 = new ZanExample();
        ex2.setOrderByClause("id desc");
        ex2.createCriteria().andActIdEqualTo(actId);
        List<Zan> zen = zanMapper.selectByExample(ex2);

        if (CollectionUtils.isEmpty(zen))
            throw new BizLogicException(ServiceCodes.PLAYGROUND_ACT_DATA_ZAN_IS_EXISTS, "PLAYGROUND_ACT_DATA_ZAN_IS_EXISTS");

        List<Long> userIds = Lists.newLinkedList();
        for (Zan zan : zen) {
            userIds.add(zan.getUserId());
        }

        Map<Long, User> userDtoMap = userService.getUsersByIdsToMap(userIds);

        for (Zan zan : zen) {
            zan.setUserDto(userDtoMap.get(zan.getUserId()));
        }

        return new PagingInfo<Zan>(page, zen);
    }



    /**
     * 获取分页评论
     * chenzhong
     *
     * @param actId
     * @param pageInfo
     * @return
     */
    public PagingInfo<Discuss> getPageDiscuss(Long actId, QueryPaging pageInfo) {
        if (actId == null) return null;
        Page<Discuss> page = PageHelper.startPage(pageInfo.getPageNum(), pageInfo.getPageSize());

        DiscussExample ex = new DiscussExample();
        ex.setOrderByClause("created_at desc");
        ex.createCriteria().andActIdEqualTo(actId);
        List<Discuss> discusses = discussMapper.selectByExample(ex);

        if (CollectionUtils.isEmpty(discusses))
            throw new BizLogicException(ServiceCodes.PLAYGROUND_ACT_DATA_DISCUSS_IS_EXISTS, "PLAYGROUND_ACT_DATA_DISCUSS_IS_EXISTS");

        List<Long> userIds = Lists.newLinkedList();
        for (Discuss discuss : discusses) {
            userIds.add(discuss.getSrcId());
            userIds.add(discuss.getTarId());
        }

        Map<Long, User> userMap = userService.getUsersByIdsToMap(userIds);

        for (Discuss discuss : discusses) {
            discuss.setSrcDto(userMap.get(discuss.getSrcId()));
            discuss.setTarDto(userMap.get(discuss.getTarId()));
            discuss.setStrCreatedAt(DisplayUtils.formatDateTime(discuss.getCreatedAt()));
        }
        return new PagingInfo<Discuss>(page, discusses);
    }

    /**
     * 赞一个
     * chenzhong
     *
     * @param userId
     * @param actId
     */
    public void saveZan(Long userId, Long actId) {
        //赞过就抛异常
        ZanExample example = new ZanExample();
        example.createCriteria().andUserIdEqualTo(userId).andActIdEqualTo(actId);

        if (!CollectionUtils.isEmpty(zanMapper.selectByExample(example)))
            throw new BizLogicException(ServiceCodes.THIS_USER_ZANED, "THIS_USER_ZANED");

        //没赞过，就赞拉
        Zan zan = new Zan();
        zan.setUserId(userId);
        zan.setActId(actId);
        zanMapper.insert(zan);

    }

    /**
     * 取消赞
     * chenzhong
     *
     * @param userId
     * @param actId
     */
    public void removeZan(Long userId, Long actId) {
        ZanExample example = new ZanExample();
        example.createCriteria().andUserIdEqualTo(userId).andActIdEqualTo(actId);
        zanMapper.deleteByExample(example);
    }

    /**
     * 保存评论信息
     * chenzhong
     *
     * @param discuss
     */
    public void saveDiscuss(Discuss discuss) {
        discuss.setCreatedAt(Calendar.getInstance().getTime());
        discussMapper.insert(discuss);
    }

    /**
     * 查看我评论过的活动
     * chenzhong
     *
     * @param studentDto
     * @param pageInfo
     * @return
     */
    public PagingInfo<PlaygroundActDto> discussPgAct(StudentDto studentDto, QueryPaging pageInfo) {
        Page<Discuss> page = PageHelper.startPage(pageInfo.getPageNum(), pageInfo.getPageSize());

        //查询用户的所有评论过的帖子
        List<Long> actIds = discussMapper.findBySrcId(studentDto.getUserId());

        if (CollectionUtils.isEmpty(actIds))
            throw new BizLogicException(ServiceCodes.PLAYGROUND_ACT_DATA_IS_EXISTS, "PLAYGROUND_ACT_DATA_IS_EXISTS");

        List<PlaygroundAct> acts = getPgActById(actIds);

        List<PlaygroundActDto> dtos = handleDetailByPlaygroundAct(acts);

        return new PagingInfo<PlaygroundActDto>(page, dtos);
    }

    private List<PlaygroundAct> getPgActById(List<Long> actIds) {
        PlaygroundActExample ex = new PlaygroundActExample();
        ex.setOrderByClause("create_at desc");
        ex.createCriteria().andIdIn(actIds);
        return playgroundActMapper.selectByExample(ex);
    }

    /**
     * 我赞的的帖子
     * chenzhong
     *
     * @param studentDto
     * @param pageInfo
     * @return
     */
    public PagingInfo<PlaygroundActDto> zanPgAct2me(StudentDto studentDto, QueryPaging pageInfo) {
        Page<Zan> page = PageHelper.startPage(pageInfo.getPageNum(), pageInfo.getPageSize());

        ZanExample ex = new ZanExample();
        ex.createCriteria().andUserIdEqualTo(studentDto.getUserId());
        List<Zan> list = zanMapper.selectByExample(ex);

        if (CollectionUtils.isEmpty(list))
            throw new BizLogicException(ServiceCodes.PLAYGROUND_ACT_DATA_IS_EXISTS, "PLAYGROUND_ACT_DATA_IS_EXISTS");

        List<Long> actIds = Lists.newLinkedList();
        for (Zan zan : list) {
            actIds.add(zan.getActId());
        }

        List<PlaygroundAct> acts = getPgActById(actIds);

        List<PlaygroundActDto> dtos = handleDetailByPlaygroundAct(acts);

        return new PagingInfo<PlaygroundActDto>(page, dtos);

    }

    /**
     * 我发布的的帖子
     * chenzhong
     *
     * @param studentDto
     * @param pageInfo
     * @return
     */
    public PagingInfo<PlaygroundActDto> sendPgAct2me(StudentDto studentDto, QueryPaging pageInfo) {
        Page<PlaygroundAct> page = PageHelper.startPage(pageInfo.getPageNum(), pageInfo.getPageSize());

        PlaygroundActExample ex = new PlaygroundActExample();
        ex.setOrderByClause("create_at desc");
        ex.createCriteria().andUserIdEqualTo(studentDto.getUserId());
        List<PlaygroundAct> acts = playgroundActMapper.selectByExample(ex);

        if (CollectionUtils.isEmpty(acts))
            throw new BizLogicException(ServiceCodes.PLAYGROUND_ACT_DATA_IS_EXISTS, "PLAYGROUND_ACT_DATA_IS_EXISTS");

        List<PlaygroundActDto> dtos = handleDetailByPlaygroundAct(acts);

        return new PagingInfo<PlaygroundActDto>(page, dtos);
    }

    /**
     * 发现的帖子 -- 评论排序
     * chenzhong
     *
     * @param studentDto
     * @param pageInfo
     * @return
     */
    public PagingInfo<PlaygroundActDto> discoverPdAct(StudentDto studentDto, QueryPaging pageInfo) {
        Page page = PageHelper.startPage(pageInfo.getPageNum(), pageInfo.getPageSize());
        //查询条件
        Map<String, Object> conditions = Maps.newHashMap();
        conditions.put("schoolId", studentDto.getSchoolId());
//        conditions.put("pageNum",pageInfo.getPageNum());
//        conditions.put("pageSize",pageInfo.getPageSize());

        //获取10评论最多的
        List<PlaygroundActDto> dtos = playgroundActMapper.findActIdByCountDesc(conditions);

        if (CollectionUtils.isEmpty(dtos))
            throw new BizLogicException(ServiceCodes.PLAYGROUND_ACT_DATA_IS_EXISTS, "PLAYGROUND_ACT_DATA_IS_EXISTS");

        List<PlaygroundAct> acts = Lists.newArrayList();
        for (PlaygroundActDto dto : dtos) {
            PlaygroundAct act = new PlaygroundAct();
            BeanUtils.copyProperties(dto, act);
            acts.add(act);
        }

        List<PlaygroundActDto> dtos1 = handleDetailByPlaygroundAct(acts);
        return new PagingInfo<PlaygroundActDto>(page, dtos1);
    }


    /**
     * 活动详情 -- 用户的查询出活动集合的时候给予我进行数据封装
     * chenzhong
     *
     * @param acts 集合的大小就是分页的大小
     * @return
     */
    private List<PlaygroundActDto> handleDetailByPlaygroundAct(List<PlaygroundAct> acts) {
        if (CollectionUtils.isEmpty(acts))
            throw new BizLogicException(ServiceCodes.PLAYGROUND_ACT_DATA_IS_EXISTS, "PLAYGROUND_ACT_DATA_IS_EXISTS");

        List<PlaygroundActDto> dtos = Lists.newArrayList();/*返回对象*/
        for (PlaygroundAct act : acts) {
            PlaygroundActDto dto = new PlaygroundActDto();
            BeanUtils.copyProperties(act, dto);

            //活动主数据
            if (StringUtils.isNotEmpty(dto.getImgUrl())) {
                dto.setImgs(CollectionUtils.arrayToList(dto.getImgUrl().split(",")));/*图片*/
                dto.setImgUrl(null);
            }
            List<Long> userid = Lists.newLinkedList();
            userid.add(dto.getUserId());
            Map<Long, User> users = userService.getUsersByIdsToMap(userid);
            dto.setUserDto(users.get(dto.getUserId()));
            dto.setStrCreateAt(DisplayUtils.formatDateTime(dto.getCreateAt()));/*时间*/

            /*集合最大10，不考虑循环查询效率*/
//            PagingInfo<Zan> pageZan = getPageZan(dto.getId(), new QueryPaging());/*主要获取总数量，其他不关心*/
//            PagingInfo<Discuss> discuss = getPageDiscuss(dto.getId(), new QueryPaging());/*主要获取总数量，其他不关心*/

            //评论总数量 -- 不考虑具体评论的人
            DiscussExample ex = new DiscussExample();
            ex.createCriteria().andActIdEqualTo(dto.getId());
            int i = discussMapper.countByExample(ex);
            dto.setDiscussCount(i);

            //赞总数量 -- 不考虑具体赞的人
            ZanExample ex1 = new ZanExample();
            ex1.createCriteria().andActIdEqualTo(dto.getId());
            i = zanMapper.countByExample(ex1);
            dto.setZanCount(i);

            dtos.add(dto);
        }
        return dtos;
    }


}
