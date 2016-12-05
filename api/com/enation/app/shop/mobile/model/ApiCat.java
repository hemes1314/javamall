package com.enation.app.shop.mobile.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dawei on 4/1/15.
 */
public class ApiCat {
    protected String cat_id;
    protected String name;
    protected Integer parent_id;
    private String image;
    private int level = 1;
    private List<ApiCat> children = new ArrayList<ApiCat>();

    public String getCat_id() {
        return cat_id;
    }

    public void setCat_id(String cat_id) {
        this.cat_id = cat_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getParent_id() {
        return parent_id;
    }

    public void setParent_id(Integer parent_id) {
        this.parent_id = parent_id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public List<ApiCat> getChildren() {
        return children;
    }

    public void setChildren(List<ApiCat> children) {
        this.children = children;
    }
}
