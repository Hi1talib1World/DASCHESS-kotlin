package com.denzo.daschess.dao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;

@Entity
public class MyTrendingLanguage {

    @Id
    @NotNull
    private String slug;

    @NotNull
    private String name;
    private int order;

    @Generated
    public MyTrendingLanguage() {
    }

    public MyTrendingLanguage(String slug) {
        this.slug = slug;
    }

    @Generated
    public MyTrendingLanguage(String slug, String name, int order) {
        this.slug = slug;
        this.name = name;
        this.order = order;
    }

    @NotNull
    public String getSlug() {
        return slug;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setSlug(@NotNull String slug) {
        this.slug = slug;
    }

    @NotNull
    public String getName() {
        return name;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setName(@NotNull String name) {
        this.name = name;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

}