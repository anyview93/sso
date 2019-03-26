package com.example.demo.entity;

import java.io.Serializable;

/**
 * @author shizhiguo
 * @ClassName User
 * @date 2019-03-24
 */
public class User implements Serializable {
    private String id;
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
