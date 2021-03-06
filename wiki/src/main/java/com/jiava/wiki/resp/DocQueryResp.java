package com.jiava.wiki.resp;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.jiava.wiki.req.PageReq;
import com.jiava.wiki.util.JsonLongSerializer;

public class DocQueryResp{
    @JsonSerialize(using = JsonLongSerializer.class )
    private Long id;
    @JsonSerialize(using = JsonLongSerializer.class )
    private Long parent;
    @JsonSerialize(using = JsonLongSerializer.class )
    private Long ebookId;

    private String name;

    private Integer sort;

    private Integer viewCount;

    private Integer voteCount;

    @Override
    public String toString() {
        return "DocQueryReq{" +
                "id=" + id +
                ", parent=" + parent +
                ", ebookId=" + ebookId +
                ", name='" + name + '\'' +
                ", sort=" + sort +
                ", viewCount=" + viewCount +
                ", voteCount=" + voteCount +
                '}'+super.toString();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getParent() {
        return parent;
    }

    public void setParent(Long parent) {
        this.parent = parent;
    }

    public Long getEbookId() {
        return ebookId;
    }

    public void setEbookId(Long ebookId) {
        this.ebookId = ebookId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }

    public Integer getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(Integer voteCount) {
        this.voteCount = voteCount;
    }
}
