package com.denzo.daschess.dao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;

@Entity
public class BookMarkRepo {

    @Id
    private long id;

    @NotNull
    private String name;
    private String description;
    private String language;
    private Integer stargazersCount;
    private Integer watchersCount;
    private Integer forksCount;
    private Boolean fork;
    private String ownerLogin;
    private String ownerAvatarUrl;
    private java.util.Date markTime;

    @Generated
    public BookMarkRepo() {
    }

    public BookMarkRepo(long id) {
        this.id = id;
    }

    @Generated
    public BookMarkRepo(long id, String name, String description, String language, Integer stargazersCount, Integer watchersCount, Integer forksCount, Boolean fork, String ownerLogin, String ownerAvatarUrl, java.util.Date markTime) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.language = language;
        this.stargazersCount = stargazersCount;
        this.watchersCount = watchersCount;
        this.forksCount = forksCount;
        this.fork = fork;
        this.ownerLogin = ownerLogin;
        this.ownerAvatarUrl = ownerAvatarUrl;
        this.markTime = markTime;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @NotNull
    public String getName() {
        return name;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setName(@NotNull String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Integer getStargazersCount() {
        return stargazersCount;
    }

    public void setStargazersCount(Integer stargazersCount) {
        this.stargazersCount = stargazersCount;
    }

    public Integer getWatchersCount() {
        return watchersCount;
    }

    public void setWatchersCount(Integer watchersCount) {
        this.watchersCount = watchersCount;
    }

    public Integer getForksCount() {
        return forksCount;
    }

    public void setForksCount(Integer forksCount) {
        this.forksCount = forksCount;
    }

    public Boolean getFork() {
        return fork;
    }

    public void setFork(Boolean fork) {
        this.fork = fork;
    }

    public String getOwnerLogin() {
        return ownerLogin;
    }

    public void setOwnerLogin(String ownerLogin) {
        this.ownerLogin = ownerLogin;
    }

    public String getOwnerAvatarUrl() {
        return ownerAvatarUrl;
    }

    public void setOwnerAvatarUrl(String ownerAvatarUrl) {
        this.ownerAvatarUrl = ownerAvatarUrl;
    }

    public java.util.Date getMarkTime() {
        return markTime;
    }

    public void setMarkTime(java.util.Date markTime) {
        this.markTime = markTime;
    }

}