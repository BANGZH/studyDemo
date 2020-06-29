package com.example.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.efunds.util.CommonUtil;
import com.example.dao.EnvMapper;
import com.example.dao.VariablesMapper;
import com.example.entity.Environment;
import com.example.entity.Variables;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import lombok.extern.slf4j.Slf4j;
import org.omg.DynamicAny.DynAny;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName EnvService
 * @Description: EnvService 类（或接口）是
 * @Author: bang
 * @Date: 2020/2/2011:59
 */
@Slf4j
@Service
public class EnvService extends ServiceImpl<EnvMapper, Environment> {
    final EnvMapper envMapper;
    final VariablesMapper variablesMapper;

    public EnvService(EnvMapper envMapper, VariablesMapper variablesMapper) {
        this.envMapper = envMapper;
        this.variablesMapper = variablesMapper;
    }

    /**
     * 新增环境
     * @return
     */
    public int insertEnv(Environment environment){
        //新增前获取当前的所有环境的id
        List<Integer> envIds = envMapper.selectEnvIds();
        Environment insertEnv = new Environment();
        insertEnv.setEnvName(environment.getEnvName());
        int result = envMapper.insert(insertEnv);
        if (result < 0){
            return 0;
        }
        Integer envId = insertEnv.getId();
        //新增变量
        List<Variables> variables = environment.getVariablesList();
        for (Variables variable : variables) {
            variable.setEnvId(envId);
            variablesMapper.insert(variable);
        }

        //同步其他环境的变量
        List<Variables> existVariables = selectVariables();
        if (StringUtils.isEmpty(existVariables) || StringUtils.isEmpty(envIds)){
            return 1;
        }
        //更新的变量
        List<Variables> updateList = new ArrayList<>();
        //删除的变量
        for (Variables variable : variables) {
            for (Variables existVariable : existVariables) {
                if (variable.getVariableName().equals(existVariable.getVariableName())){
                    if (!variable.getVariableType().equals(existVariable.getVariableType())){
                        updateList.add(variable);
                    }
                }

            }
        }

        for (Variables variables1 : updateList) {
            variablesMapper.updateByName(variables1);
        }
        //同步删除的变量
        Set<String> deleteList = new HashSet<>();
        String[] newName = variables.stream().map(Variables::getVariableName).toArray(String[]::new);
        Set<String> check = new HashSet<>(Arrays.asList(newName));
        String[] oldName = existVariables.stream().map(Variables::getVariableName).toArray(String[]::new);
        Set<String> old = new HashSet<>(Arrays.asList(oldName));
        for (String s : oldName) {
            if (!check.contains(s)){
                deleteList.add(s);
            }
        }
        if (!deleteList.isEmpty()){
            variablesMapper.deleteByNames(deleteList);
        }
        //同步新增的变量
        Set<String> insertVariName = new HashSet<>();
        for (String s : newName) {
            if (!old.contains(s)){
                insertVariName.add(s);
            }
        }
        List<Variables> insertList = variables.stream()
                .filter((Variables v) -> insertVariName.contains(v.getVariableName()))
                .collect(Collectors.toList());
        for (Integer id : envIds) {
            for (Variables variables1 : insertList) {
                    variables1.setEnvId(id);
                    variablesMapper.insert(variables1);
            }
        }
        return 1;
    }

    /**
     * 点击新增环境
     * 获取当前的环境信息
     * @return
     */
    public List<Variables> selectVariables(){
        //检查当前是否有环境
        Integer envId = envMapper.selectExistEnvId();
        if (!StringUtils.isEmpty(envId)){
            //有->获取已有环境的所有变量返回
            return variablesMapper.selectByEnvId(envId);
        }
        return Collections.emptyList();
    }
}
