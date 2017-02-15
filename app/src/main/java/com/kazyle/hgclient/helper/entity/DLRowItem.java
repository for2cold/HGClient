package com.kazyle.hgclient.helper.entity;

import java.io.File;
import java.io.Serializable;

/**
 * Created by Kazyle on 2016/10/19.
 */
public class DLRowItem implements Serializable {

    private String name;

    private String url;

    private String dest;

    private boolean success;

    public DLRowItem(String name, String url, String dest) {
        this.name = name;
        this.url = url;
        this.dest = dest;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDest() {
        return dest;
    }

    public void setDest(String dest) {
        this.dest = dest;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
