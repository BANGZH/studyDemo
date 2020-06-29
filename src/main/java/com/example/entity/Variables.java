package com.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @ClassName Variables
 * @Description: Variables 类（或接口）是
 * @Author: bang
 * @Date: 2020/2/2011:40
 */
@Data
@TableName("variable_info")
public class Variables {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer envId;
    private String variableName;
    private String variableValue;
    private Integer variableType;
}