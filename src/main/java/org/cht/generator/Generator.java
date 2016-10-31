// Copyright (C) 2016 XueQiu
// All rights reserved

package org.cht.generator;

import com.google.common.base.CaseFormat;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @author chenhetong
 * @version 1.0
 * @created 16/9/8 下午6:06
 **/
public class Generator {

    private static Logger LOGGER = LoggerFactory.getLogger(Generator.class);

    static {
        try {
            new com.mysql.jdbc.Driver();
        } catch (SQLException e) {
            LOGGER.error("init jdbc driver failed", e);
        }
    }

    private static Map<String, String> CONVERT_TYPE_MAP;
    private static Map<String, String> BOXED_TYPE_CONVERTER;

    static {
        HashMap<String, String> tmp = Maps.newHashMap();
        tmp.put("CHAR", "String");
        tmp.put("CHARACTER", "String");
        tmp.put("LONG", "String");
        tmp.put("STRING", "String");
        tmp.put("VARCHAR", "String");
        tmp.put("VARCHAR2", "String");
        tmp.put("INT", "Integer");
        tmp.put("SMALLINT", "Integer");
        tmp.put("DECIMAL", "BigDecimal");
        tmp.put("NUMBER", "BigDecimal");
        tmp.put("NUMERIC", "BigDecimal");
        tmp.put("REAL", "Float");
        tmp.put("FLOAT", "Double");
        tmp.put("DOUBLE", "Double");
        tmp.put("DECIMAL", "BigDecimal");
        tmp.put("BIGINT", "Long");
        tmp.put("INT UNSIGNED", "Long");
        tmp.put("TINYINT", "Byte");
        tmp.put("TINYINT UNSIGNED", "Integer");
        tmp.put("DATETIME", "Date");
        CONVERT_TYPE_MAP = Collections.unmodifiableMap(tmp);
        tmp = Maps.newHashMap();
        tmp.put("Integer", "int");
        tmp.put("Long", "long");
        tmp.put("Float", "float");
        tmp.put("Double", "double");
        tmp.put("Byte", "byte");
        BOXED_TYPE_CONVERTER = Collections.unmodifiableMap(tmp);
    }

    private GeneratorSettings settings;

    public Generator(GeneratorSettings settings) {
        this.settings = settings;
    }

    public static class GeneratorSettings {
        private String jdbcUrl;
        private String passwd;
        private String user;

        public GeneratorSettings(String jdbcUrl, String user, String passwd) {
            this.jdbcUrl = jdbcUrl;
            this.passwd = passwd;
            this.user = user;
        }

        public String getJdbcUrl() {
            return jdbcUrl;
        }

        public GeneratorSettings setJdbcUrl(String jdbcUrl) {
            this.jdbcUrl = jdbcUrl == null ? null : jdbcUrl.trim();
            return this;
        }

        public String getPasswd() {
            return passwd;
        }

        public GeneratorSettings setPasswd(String passwd) {
            this.passwd = passwd == null ? null : passwd.trim();
            return this;
        }

        public String getUser() {
            return user;
        }

        public GeneratorSettings setUser(String user) {
            this.user = user == null ? null : user.trim();
            return this;
        }
    }


    List<Field> getTargetTableMetaField(String table) throws SQLException {
        if (settings == null) {
            throw new IllegalArgumentException("generator settings is invalid");
        }
        Connection conn = DriverManager.getConnection(settings.getJdbcUrl(), settings.getUser(), settings.getPasswd());
        DatabaseMetaData metaData = conn.getMetaData();
        List<Field> rtn = Lists.newLinkedList();
        ResultSet resultSet = metaData.getColumns(null, null, table, null);
        while (resultSet.next()) {
            String name = resultSet.getString("COLUMN_NAME");
            String typeName = resultSet.getString("TYPE_NAME");
            String remarks = resultSet.getString("REMARKS");
            int isNullable = resultSet.getInt("NULLABLE");
            String type = CONVERT_TYPE_MAP.get(typeName) == null ? "Object" : CONVERT_TYPE_MAP.get(typeName);
            if (isNullable == 0) {
                type = BOXED_TYPE_CONVERTER.get(type) == null ? type : BOXED_TYPE_CONVERTER.get(type);
            }
            Field field = new Field(remarks, type, name);
            rtn.add(field);
        }
        conn.close();
        return rtn;
    }


    public static void main(String[] args) throws SQLException, IOException, TemplateException {
        Properties properties = new Properties();
        properties.load(Generator.class.getResourceAsStream("/application.properties"));
        String url = properties.getProperty("jdbc.url");
        String user = properties.getProperty("jdbc.user");
        String passwd = properties.getProperty("jdbc.password");
        String targetPath = properties.getProperty("target.path");

        String tables = properties.getProperty("tables");

        for (String table : tables.split(",")) {
            GeneratorSettings settings = new GeneratorSettings(url, user, passwd);
            Generator generator = new Generator(settings);
            List<Field> rtn = generator.getTargetTableMetaField(table);
            String prefix = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, table);
            //generate bean file
            String beanName = prefix + "Entity";
            String beanTpl = "bean.ftl";
            Template droolsTemplate = FreeMarkerUtils.getTemplate(beanTpl);
            Map<String, Object> params = new HashMap<>();
            params.put("beanComment", String.format("%s bean", table));
            params.put("beanName", beanName);
            params.put("time", DateTimeFormatter.ofPattern("yy/MM/dd HH:mm").format(LocalDateTime.now()));
            params.put("beanFields", rtn);
            droolsTemplate.process(params, new BufferedWriter(new FileWriter(String.format("%s%sEntity.java", targetPath, prefix))));

            //generate dao file
            beanName = prefix + "DaoImpl";
            beanTpl = "dao.ftl";
            params.put("beanName", beanName);
            params.put("beanComment", String.format("%s dao implment", table));
            params.put("allFields", rtn.stream().map(Field::getName).reduce((a, b) -> a + ',' + b).get());
            params.put("allValues", ":" + rtn.stream().map(Field::getCamelName).reduce((a, b) -> a + ',' + ':' + b).get());
            params.put("tableName", table);
            droolsTemplate = FreeMarkerUtils.getTemplate(beanTpl);
            droolsTemplate.process(params, new BufferedWriter(new FileWriter(String.format("%s%sDaoImpl.java", targetPath, prefix))));
        }
    }
}
