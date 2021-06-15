package com.jiava.wiki.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jiava.wiki.config.WikiApplication;
import com.jiava.wiki.domain.Ebook;
import com.jiava.wiki.domain.EbookExample;
import com.jiava.wiki.mapper.EbookMapper;
import com.jiava.wiki.req.EbookQueryReq;
import com.jiava.wiki.req.EbookSaveReq;
import com.jiava.wiki.resp.EbookQueryResp;
import com.jiava.wiki.resp.PageResp;
import com.jiava.wiki.util.CopyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;


import javax.annotation.Resource;
import java.util.List;

@Service
public class EbookService {
    private static final Logger LOG = LoggerFactory.getLogger(WikiApplication.class);

    @Resource
    private EbookMapper ebookMapper;

    public PageResp<EbookQueryResp> list(EbookQueryReq req) {

        EbookExample ebookExample = new EbookExample();
        EbookExample.Criteria criteria = ebookExample.createCriteria();
        if(!ObjectUtils.isEmpty(req.getName())) {
            criteria.andNameLike("%" + req.getName() + "%");
        }
        //PageHelper只会对之后第一条查询数据生效,所以和要分页的sql放在一起
        PageHelper.startPage(req.getPage(),req.getSize());
        List<Ebook> ebookList = ebookMapper.selectByExample(ebookExample);

        PageInfo<Ebook> pageInfo=new PageInfo<>(ebookList);
        LOG.info("总行数:{}", pageInfo.getTotal());
        LOG.info("总页数:{}",pageInfo.getPages());
        List<EbookQueryResp> respList = CopyUtil.copyList(ebookList, EbookQueryResp.class);
        PageResp<EbookQueryResp> pageResp=new PageResp<>();
        pageResp.setList(respList);
        pageResp.setTotal(pageInfo.getTotal());
        return pageResp;
    }
//    保存
    public void save(EbookSaveReq req){
        Ebook ebook=CopyUtil.copy(req,Ebook.class);
        if(ObjectUtils.isEmpty(req.getId())){
            //新增
            ebookMapper.insert(ebook);
        }
        else{
            //更新
            ebookMapper.updateByPrimaryKey(ebook);
        }
    }
}
