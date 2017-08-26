package com.kazyle.hgclient.domain;

import java.util.List;

/**
 * Created by Kazyle on 2017/4/26.
 */
public class ArticleConfig {

    private Integer type;

    private List<String> platforms;

    private String wechat;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public List<String> getPlatforms() {
        return platforms;
    }

    public void setPlatforms(List<String> platforms) {
        this.platforms = platforms;
    }

    public String getWechat() {
        return wechat;
    }

    public void setWechat(String wechat) {
        this.wechat = wechat;
    }

    @Override
    public String toString() {
        return "ArticleConfig{" +
                "type=" + type +
                ", platforms=" + platforms +
                '}';
    }
}
