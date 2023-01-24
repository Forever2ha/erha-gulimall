package com.atguigu.gulimall.product.exceptionHandler;


import com.atguigu.common.exception.BizCodeEnume;
import com.atguigu.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;

@Slf4j
@RestControllerAdvice(basePackages = "com.atguigu.gulimall.product" )
public class ProductExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R handleValidException(MethodArgumentNotValidException e){
        HashMap<String, String> map = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach((err) ->{
            map.put(err.getField(),err.getDefaultMessage());
        } );
        return R.error(BizCodeEnume.VALID_EXCEPTION.getCode(),BizCodeEnume.VALID_EXCEPTION.getMsg()).put("data",map);
    }

    @ExceptionHandler(Throwable.class)
    public R handleThrowable(Throwable t){
        return R.error(BizCodeEnume.UNKNOW_EXCEPTION.getCode(),
                BizCodeEnume.UNKNOW_EXCEPTION.getMsg())
                .setData(t.getMessage());
    }
}
