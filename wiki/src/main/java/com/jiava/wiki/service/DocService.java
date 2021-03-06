package com.jiava.wiki.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jiava.wiki.config.WikiApplication;
import com.jiava.wiki.domain.Content;
import com.jiava.wiki.domain.Doc;
import com.jiava.wiki.domain.DocExample;
import com.jiava.wiki.exception.BusinessException;
import com.jiava.wiki.exception.BusinessExceptionCode;
import com.jiava.wiki.mapper.ContentMapper;
import com.jiava.wiki.mapper.DocMapper;
import com.jiava.wiki.mapper.DocMapperCust;
import com.jiava.wiki.req.DocQueryReq;
import com.jiava.wiki.req.DocSaveReq;
import com.jiava.wiki.resp.DocQueryResp;
import com.jiava.wiki.resp.PageResp;
import com.jiava.wiki.util.CopyUtil;
import com.jiava.wiki.util.RedisUtil;
import com.jiava.wiki.util.RequestContext;
import com.jiava.wiki.util.SnowFlake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class DocService {
    private static final Logger LOG = LoggerFactory.getLogger(WikiApplication.class);
    @Resource
    private ContentMapper contentMapper;
    @Resource
    private DocMapper docMapper;
    @Resource
    private SnowFlake snowFlake;
    @Resource
    private DocMapperCust docMapperCust;
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private WsService wsService;
//    @Resource
//    private RocketMQTemplate rocketMQTemplate;

    public PageResp<DocQueryResp> list(DocQueryReq req) {

        DocExample docExample = new DocExample();
        DocExample.Criteria criteria = docExample.createCriteria();
        docExample.setOrderByClause("sort asc");
        //PageHelper只会对之后第一条查询数据生效,所以和要分页的sql放在一起
        PageHelper.startPage(req.getPage(), req.getSize());
        List<Doc> docList = docMapper.selectByExample(docExample);

        PageInfo<Doc> pageInfo = new PageInfo<>(docList);
        LOG.info("总行数:{}", pageInfo.getTotal());
        LOG.info("总页数:{}", pageInfo.getPages());
        List<DocQueryResp> respList = CopyUtil.copyList(docList, DocQueryResp.class);
        PageResp<DocQueryResp> pageResp = new PageResp<>();
        pageResp.setList(respList);
        pageResp.setTotal(pageInfo.getTotal());
        return pageResp;
    }

    public List<DocQueryResp> all(DocQueryReq req) {
        DocExample docExample = new DocExample();
        DocExample.Criteria criteria = docExample.createCriteria();
        if (!ObjectUtils.isEmpty(req.getEbookId())) {
            criteria.andEbookIdEqualTo(req.getEbookId());
        }
        docExample.setOrderByClause("sort asc");
        List<Doc> docList = docMapper.selectByExample(docExample);

        List<DocQueryResp> respList = CopyUtil.copyList(docList, DocQueryResp.class);

        return respList;
    }

    //    保存
    //同样这个注解和Asnc一样，要在不同类中调用这个方法注解才会生效
    @Transactional
    public void save(DocSaveReq req) {
        Doc doc = CopyUtil.copy(req, Doc.class);
        Content content = CopyUtil.copy(req, Content.class);
        if (ObjectUtils.isEmpty(req.getId())) {
            //新增
            doc.setId(snowFlake.nextId());
            doc.setViewCount(0);
            doc.setVoteCount(0);
            docMapper.insert(doc);
            content.setId(doc.getId());
            contentMapper.insert(content);
        } else {
            //更新
            //不包含大字段的更新
            docMapper.updateByPrimaryKey(doc);
            //包含大字段的更新
            int count = contentMapper.updateByPrimaryKeyWithBLOBs(content);
            if (count == 0)
                contentMapper.insert(content);
        }
    }

    public String findContent(Long id) {
        //这里这个select可以找到全部的大小字段
        Content content = contentMapper.selectByPrimaryKey(id);
        docMapperCust.increaseViewCount(id);
        if (ObjectUtils.isEmpty(content)) return "";
        return content.getContent();
    }

    //删除
    public void delete(Long id) {
        docMapper.deleteByPrimaryKey(id);
    }

    //点赞
    public void vote(Long id) {
        String key = RequestContext.getRemoteAddr();
        if (redisUtil.validateRepeat("DOC_VOTE_" + id + "_" + key, 5)) {
            docMapperCust.increaseVoteCount(id);
        } else {
            throw new BusinessException(BusinessExceptionCode.VOTE_REPEAT);
        }
        Doc doc = docMapper.selectByPrimaryKey(id);
         wsService.sendInfo("【" + doc.getName() + "】被点赞", MDC.get("LOG_ID"));
      //  rocketMQTemplate.convertAndSend("VOTE_TOPIC","【" + doc.getName() + "】被点赞");
    }

    public void delete(List<String> ids) {
        DocExample docExample = new DocExample();
        DocExample.Criteria criteria = docExample.createCriteria();
        criteria.andIdIn(ids);
        docMapper.deleteByExample(docExample);
    }

    public void updateEbookInfo() {
        docMapperCust.updateCount();
    }



}
