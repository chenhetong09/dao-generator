// Copyright (C) 2016 XueQiu
// All rights reserved

package org.cht.generator;

import com.google.common.base.CaseFormat;

/**
 * @author chenhetong
 * @version 1.0
 * @created 16/9/8 下午6:34
 **/
public class Field {
    private String comment;
    private String type;
    private String name;
    private String camelName;

    public Field(String comment, String type, String name) {
        this.comment = comment;
        this.type = type;
        this.name = name;
        this.camelName = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, name);
    }

    public String getCamelName() {
        return camelName;
    }

    public Field setCamelName(String camelName) {
        this.camelName = camelName == null ? null : camelName.trim();
        return this;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment == null ? null : comment.trim();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    @Override
    public String toString() {
        return "Field{" +
                "comment='" + comment + '\'' +
                ", type='" + type + '\'' +
                ", name='" + name + '\'' +
                ", camelName='" + camelName + '\'' +
                '}';
    }
}
