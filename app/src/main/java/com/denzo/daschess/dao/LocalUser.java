package com.denzo.daschess.dao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;

@Entity
public class LocalUser {

    @Id
    @NotNull
    private String login;
    private String name;
    private String avatarUrl;
    private Integer followers;
    private Integer following;

    @Generated
    public LocalUser() {
    }

    public LocalUser(String login) {
        this.login = login;
    }

    @Generated
    public LocalUser(String login, String name, String avatarUrl, Integer followers, Integer following) {
        this.login = login;
        this.name = name;
        this.avatarUrl = avatarUrl;
        this.followers = followers;
        this.following = following;
    }

    @NotNull
    public String getLogin() {
        return login;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setLogin(@NotNull String login) {
        this.login = login;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public Integer getFollowers() {
        return followers;
    }

    public void setFollowers(Integer followers) {
        this.followers = followers;
    }

    public Integer getFollowing() {
        return following;
    }

    public void setFollowing(Integer following) {
        this.following = following;
    }

}