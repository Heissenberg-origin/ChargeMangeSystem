package utils;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.baomidou.mybatisplus.generator.fill.Property;

import java.sql.Types;
import java.util.Collections;

/**
 * TODO
 *
 * @Description
 * @Author MF
 * @Date 2024/8/14 16:55
 **/
public class CodeGenerator {
    public static void main(String[] args) {
        // 使用 FastAutoGenerator 快速配置代码生成器
        FastAutoGenerator.create("jdbc:mysql://192.168.6.129:3306/al-test?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai", "root", "123456")
                .globalConfig(builder -> {
                    builder.disableOpenDir() // 不允许自动打开输出目录
                            .author("MF") // 设置作者
                            .enableSpringdoc()//设置接口文档springdoc
                            //.enableSwagger()//设置接口文档工具swagger
                            .outputDir(System.getProperty("user.dir") + "/src/main/java"); // 指定输出目录
                })
                .dataSourceConfig(builder ->
                        builder.typeConvertHandler((globalConfig, typeRegistry, metaInfo) -> {
                            int typeCode = metaInfo.getJdbcType().TYPE_CODE;
                            if (typeCode == Types.SMALLINT) {
                                // 自定义类型转换
                                return DbColumnType.INTEGER;
                            }
                            return typeRegistry.getColumnType(metaInfo);
                        })
                )
                .packageConfig(builder -> {
                    builder.parent("org.example") // 设置父包名
                            //.moduleName("system2") //功能模块包名设置
                            .controller("controller") // 设置 Controller 包名
                            .entity("entity") // 设置实体类包名
                            .mapper("mapper") // 设置 Mapper 接口包名
                            .service("service") // 设置 Service 接口包名
                            .serviceImpl("service.impl") // 设置 Service 实现类包名
                            .xml("mapper") // 设置 Mapper XML 文件包名
                            .pathInfo(Collections.singletonMap(OutputFile.xml, System.getProperty("user.dir") + "/src/main/resources/mapper")); // 设置 Mapper XML 文件的输出路径
                })
                .strategyConfig(builder -> {
                    builder.addInclude("user","sys_dept","sys_dic", "sys_dic_item", "sys_login_log").addTablePrefix("t_", "c_", "sys_") // 设置需要生成的表名
                            .controllerBuilder().enableFileOverride().enableRestStyle().entityBuilder().fileOverride().enableLombok()
                            .enableTableFieldAnnotation().logicDeleteColumnName("isDelete")
                            .addTableFills(new Property("createTime", FieldFill.INSERT))
                            .addTableFills(new Property("updateTime", FieldFill.INSERT_UPDATE))
                            .mapperBuilder().fileOverride().formatMapperFileName("I%sDao").formatXmlFileName("%sMapper")
                            .serviceBuilder().fileOverride().formatServiceFileName("I%sService")
                            .formatServiceImplFileName("%sServiceImp");
                })
                .templateEngine(new FreemarkerTemplateEngine()) // 使用 Freemarker 模板引擎
                .execute(); // 执行生成
    }
}