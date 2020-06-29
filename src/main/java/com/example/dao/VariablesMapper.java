package com.example.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.Variables;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface VariablesMapper extends BaseMapper<Variables> {
    List<Variables> selectByEnvId(@Param("envId") Integer envId);

    /**
     * 同步时其他环境的变量
     * 删除变量
     */
    Integer deleteByNames( @Param("set") Set<String> variablesNames);

    Integer updateByName(Variables variables);

}
