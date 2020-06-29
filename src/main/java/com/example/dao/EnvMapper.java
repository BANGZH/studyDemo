package com.example.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.Environment;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnvMapper extends BaseMapper<Environment> {
    Integer selectExistEnvId();

    List<Integer> selectEnvIds();
}
