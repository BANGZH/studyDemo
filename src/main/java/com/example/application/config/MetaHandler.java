package com.example.application.config;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;

import java.lang.reflect.Field;
import java.time.LocalDateTime;

/**
 * @ClassName MetaHandler
 * @Description: MetaHandler 类 使用mybatis-plus 的自动填充功能
 * @Author: zhonghanbang
 * @Date: 2020/7/2915:59
 */
public class MetaHandler implements MetaObjectHandler {

    /**
     * 新增数据执行
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        // String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        String userName ="currentUser";
        exeFill("createTime", LocalDateTime.now(), metaObject);
        exeFill("creator", userName, metaObject);
        exeFill("updateTime", LocalDateTime.now(), metaObject);
        exeFill("editor", userName, metaObject);
    }

    /**
     * 更新数据执行
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        // String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        String userName ="currentUser";
        exeFill("updateTime", LocalDateTime.now(), metaObject);
        exeFill("editor",userName, metaObject);
    }

    /**
     * 处理自动填充策略为默认不处理(fill = FieldFill.DEFAULT)时也会填充字段的问题
     * @param fieldName
     * @param fieldVal
     * @param metaObject
     */
    public void exeFill(String fieldName, Object fieldVal, MetaObject metaObject){
        if (metaObject.hasSetter(fieldName) && metaObject.hasGetter(fieldName)) {
            Object classObj = metaObject.getOriginalObject();
            Field[] declaredFields = classObj.getClass().getDeclaredFields();
            if(declaredFields!=null){
                for(Field field:declaredFields){
                    if(field.getName().equals(fieldName)){
                        TableField tableField = field.getAnnotation(TableField.class);
                        if(tableField!=null && tableField.fill()!= FieldFill.DEFAULT){
                            this.setFieldValByName(fieldName, fieldVal, metaObject);
                        }
                    }
                }
            }
        }
    }
}
