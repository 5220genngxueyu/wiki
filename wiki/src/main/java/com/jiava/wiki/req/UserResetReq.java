package com.jiava.wiki.req;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.jiava.wiki.util.JsonLongSerializer;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class UserResetReq {
    @JsonSerialize(using = JsonLongSerializer.class )
    private Long id;
    @NotNull(message="【密码】不能为空")
    @Length(min=6,max=32,message="【密码】6~32位")
    @Pattern(regexp = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,32}$", message = "【密码】至少包含 数字和英文，长度6-32")
    private String password;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "UserResetReq{" +
                "id=" + id +
                ", password='" + password + '\'' +
                '}';
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}