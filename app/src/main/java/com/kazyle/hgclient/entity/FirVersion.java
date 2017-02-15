package com.kazyle.hgclient.entity;

/**
 * Created by Kazyle on 2016/9/5.
 */
public class FirVersion {

    private String name;

    private String version;

    private String build;

    private String versionShort;

    private String changelog;

    private long updated_at;

    private String installUrl;

    private String install_url;

    private String direct_install_url;

    private String update_url;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getBuild() {
        return build;
    }

    public void setBuild(String build) {
        this.build = build;
    }

    public String getVersionShort() {
        return versionShort;
    }

    public void setVersionShort(String versionShort) {
        this.versionShort = versionShort;
    }

    public String getChangelog() {
        return changelog;
    }

    public void setChangelog(String changelog) {
        this.changelog = changelog;
    }

    public long getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(long updated_at) {
        this.updated_at = updated_at;
    }

    public String getInstallUrl() {
        return installUrl;
    }

    public void setInstallUrl(String installUrl) {
        this.installUrl = installUrl;
    }

    public String getInstall_url() {
        return install_url;
    }

    public void setInstall_url(String install_url) {
        this.install_url = install_url;
    }

    public String getDirect_install_url() {
        return direct_install_url;
    }

    public void setDirect_install_url(String direct_install_url) {
        this.direct_install_url = direct_install_url;
    }

    public String getUpdate_url() {
        return update_url;
    }

    public void setUpdate_url(String update_url) {
        this.update_url = update_url;
    }

    public boolean checkVersionCode(int versionCode) {
        int _version = Integer.valueOf(this.getVersion());
        return _version > versionCode;
    }

    public boolean checkVersionName(String versionName) {
        float _versionName = Float.valueOf(this.getVersionShort());
        float _versionName2 = Float.valueOf(versionName);
        return _versionName > _versionName2;
    }
}
