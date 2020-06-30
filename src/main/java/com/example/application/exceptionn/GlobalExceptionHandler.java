package com.example.application.exceptionn;

import com.example.application.domain.dto.ResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private String errorMessageTemplate = "\n%s";
    private String errorStackTraceTemplate = "\n\t\tat %s";
    private int maxTraceSize = 3;

    /**
     * 应用到所有@RequestMapping注解方法，在其执行之前初始化数据绑定器
     * @param binder
     */
/*    @InitBinder
    public void initWebBinder(WebDataBinder binder){
        //对日期的统一处理
        binder.addCustomFormatter(new DateFormatter("yyyy-MM-dd"));
        //添加对数据的校验
        //binder.setValidator();
    }*/

    /**
     * 把值绑定到Model中，使全局@RequestMapping可以获取到该值
     * @param model
     */
/*    @ModelAttribute
    public void addAttribute(Model model) {
        model.addAttribute("attribute",  "The Attribute");
    }*/

    /**
     * 捕获CustomException
     * @param e
     * @return json格式类型
     */
    @ResponseBody
    @ExceptionHandler({Exception.class}) //指定拦截异常的类型
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) //自定义浏览器返回状态码
    public ResponseDto handleException(Exception e) {
        log.error("", e);
        ResponseDto result = ResponseDto.builder().code("-1").message("系统异常，请联系管理员").build();
        return result;
    }

    @ResponseBody
    @ExceptionHandler({BasicException.class}) //指定拦截异常的类型
    public ResponseDto handleBasicException(BasicException e, HttpServletRequest request, HttpServletResponse response) {
        StringBuilder sb = new StringBuilder(String.format(errorMessageTemplate, e.toString()));
        StackTraceElement[] stackTraceElements = e.getStackTrace();
        int traceSize = stackTraceElements.length < maxTraceSize ? stackTraceElements.length : maxTraceSize;
        for (int i = 0; i < traceSize; i++) {
            sb.append(String.format(errorStackTraceTemplate, stackTraceElements[i]));
        }
        log.error(sb.toString());
        ResponseDto result = ResponseDto.builder().code(e.getErrorCode()).message(e.getMessage()).build();
        response.setStatus(e.getHttpStatus().value());
        return result;
    }


    @ResponseBody
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseDto handleMethodArgumentNotValidException(MethodArgumentNotValidException e ,HttpServletRequest request,HttpServletResponse response){
        log.error(e.getBindingResult().getFieldError().getDefaultMessage());
        ResponseDto result=ResponseDto.builder().code("-1").message(e.getBindingResult().getFieldError().getDefaultMessage()).build();
        response.setStatus(HttpStatus.OK.value());
        return result;
    }

    /**
     * 拦截参数检验异常
     */
    @ResponseBody
    @ExceptionHandler({BindException.class})
    public ResponseDto handleBindException(BindException e , HttpServletRequest request, HttpServletResponse response){
        log.error(e.getBindingResult().getFieldError().getDefaultMessage());
        ResponseDto result=ResponseDto.builder().code("-1").message(e.getBindingResult().getFieldError().getDefaultMessage()).build();
        response.setStatus(HttpStatus.OK.value());
        return result;
    }

}
