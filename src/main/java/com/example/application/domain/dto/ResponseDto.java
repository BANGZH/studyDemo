package com.example.application.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @description:
 * @author: zhao.zhijian
 * @create: 2019-09-09 17:10
 **/

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDto implements Serializable {
    @Builder.Default
    private String code = "0";
    @Builder.Default
    private String message = "success";
    private Object data;
}
