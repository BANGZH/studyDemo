package com.example.application.exceptionn;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * @ClassName BasicException
 * @Description: BasicException 类（或接口）是
 * @Author: zhonghanbang
 * @Date: 2020/6/3014:21
 */
@Getter
public class BasicException extends RuntimeException {
    private HttpStatus httpStatus = HttpStatus.OK;
    private String errorCode = "-1";
    private String errorMsg = "系统内部异常";

    public BasicException() {
        super();
    }

    public BasicException(String errorMsg) {
        super(errorMsg);
        this.errorMsg = errorMsg;
    }

    public BasicException(String errorCode, String errorMsg) {
        super(errorMsg);
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public BasicException(HttpStatus httpStatus, String errorCode, String errorMsg) {
        super(errorMsg);
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }
}
