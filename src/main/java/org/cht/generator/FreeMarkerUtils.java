// Copyright (C) 2016 Meituan
// All rights reserved

package org.cht.generator;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Freemarker 操作帮助类
 *
 * @author chenhetong
 * @version 1.0
 * @created 16/4/1 下午10:38
 **/
public class FreeMarkerUtils {

    /**
     * 从 classpath 读取模板
     *
     * @param dir  classpath dir  eg /drools/template
     * @param name 模板名称
     * @return 模板类
     */
    public static Template getTemplateFromClasspath(String dir, String name) throws IOException {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_23);
        //设定去哪里读取相应的ftl模板文件
        cfg.setClassForTemplateLoading(FreeMarkerUtils.class, dir);
        //在模板文件目录中找到名称为name的文件
        return cfg.getTemplate(name);
    }

    /**
     * 获取 drools 模板文件
     *
     * @param name 模板文件名称
     * @return 模板对象
     * @throws IOException
     */
    public static Template getTemplate(String name) throws IOException {
        return getTemplateFromClasspath("/template/", name);
    }

    public static void main(String[] args) throws IOException, TemplateException {

    }

}
