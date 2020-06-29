package com.example.entity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.List;

/**
 * @ClassName Environment
 * @Description: Environment 类（或接口）是
 * @Author: bang
 * @Date: 2020/2/2011:36
 */
@Data
@TableName("env_info")
public class Environment {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String envName;
    List<Variables> variablesList;
}
