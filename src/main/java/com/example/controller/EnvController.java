package com.example.controller;

import com.example.entity.Environment;
import com.example.entity.Variables;
import com.example.service.EnvService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @ClassName EnvController
 * @Description: EnvController 类（或接口）是
 * @Author: bang
 * @Date: 2020/2/2012:02
 */
@RestController
@RequestMapping("/api/v1/env")
public class EnvController {
    private final EnvService envService;
    public EnvController(EnvService envService) {
        this.envService = envService;
    }

    @PostMapping("/insert")
    public void insertEnv(@RequestBody(required = false) Environment environment){
        envService.insertEnv(environment);
    }

    @GetMapping("/test")
    public List<Variables> getTest(){
        return envService.selectVariables();
    }
}