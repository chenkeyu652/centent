package com.centent.core.handler;

import com.centent.core.bean.Result;
import com.centent.core.exception.AuthorizedException;
import com.centent.core.exception.BusinessException;
import com.centent.core.exception.IllegalArgumentException;
import com.centent.core.exception.NotFoundException;
import com.centent.core.exception.UnimportantException;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 统一异常处理器
 *
 * @since 0.0.1
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Cache<String, Boolean> INTERFACE_CACHE = CacheBuilder.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .maximumSize(500)
            .build();

    @RequestMapping("/error")
    public Result<Void> error() {
        return Result.error();
    }

    @ResponseBody
    @ExceptionHandler(value = BusinessException.class)
    public Result<Object> businessException(HttpServletRequest request, BusinessException e) {
        this.commonHandle(request, e);
        return Result.error(e.getCode(), e.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(value = UnimportantException.class)
    public Result<Object> unimportantException(HttpServletRequest request, UnimportantException e) {
        return Result.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(value = {NoResourceFoundException.class, NoHandlerFoundException.class})
    public Object noHandlerFoundException(HttpServletRequest request, Exception e) throws ExecutionException {
        this.commonHandle(request, e);
        if (this.isInterface(request) || Objects.equals("/404", request.getRequestURI())) {
            return this.jsonResult(NotFoundException.CODE, NotFoundException.MESSAGE);
        }
        return "redirect:/404";
    }

    @ExceptionHandler(value = AuthorizedException.class)
    public Object authorizedException(HttpServletRequest request, AuthorizedException e) throws ExecutionException {
        this.commonHandle(request, e);
        // 判断accept是否是json请求
        if (this.isInterface(request)) {
            return this.jsonResult(AuthorizedException.CODE, AuthorizedException.MESSAGE);
        }
        return "redirect:/login";
    }

    @ResponseBody
    @ExceptionHandler(value = MethodArgumentTypeMismatchException.class)
    public Result<Object> methodArgumentTypeMismatchException(HttpServletRequest request, MethodArgumentTypeMismatchException e) {
        this.commonHandle(request, e);
        return Result.error("非法参数！");
    }

    @ResponseBody
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public Result<Object> methodArgumentNotValidException(HttpServletRequest request, MethodArgumentNotValidException e) {
        this.commonHandle(request, e);
        String msg = Optional.ofNullable(e.getBindingResult().getFieldError())
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .orElse(IllegalArgumentException.MESSAGE);
        return Result.error(msg);
    }

    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public Result<Object> exception(HttpServletRequest request, Exception e) {
        this.commonHandle(request, e);
        log.error("", e);
        return Result.error(e.getMessage());
    }

    private void commonHandle(HttpServletRequest request, Exception e) {
        log.error(String.format("[%s] %s ---> %s: %s",
                request.getMethod(), request.getRequestURI(), e.getClass().getSimpleName(), e.getMessage()));
    }

    private ModelAndView jsonResult(int code, String msg) {
        ModelAndView modelAndView = new ModelAndView(new MappingJackson2JsonView());
        modelAndView.addObject("code", code);
        modelAndView.addObject("msg", msg);
        return modelAndView;
    }

    private boolean isInterface(HttpServletRequest request) throws ExecutionException {
        return INTERFACE_CACHE.get(request.getRequestURI(), () -> {
            // 通过debug调试可以看出request中含有该对象。
            Object attribute = request.getAttribute("org.springframework.web.servlet.HandlerMapping.bestMatchingHandler");
            if (attribute instanceof HandlerMethod handler) {
                // getBeanType()拿到发送请求得类模板（Class），getDeclaredAnnotationsByType(指定注解类模板)通过指定得注解，得到一个数组。
                // 判断当类上含有@RestController或是@ResponseBody或是方法上有@ResponseBody时，则表明该异常是一个接口请求发生的
                RestController[] annotations1 = handler.getBeanType().getDeclaredAnnotationsByType(RestController.class);
                if (annotations1.length > 0) {
                    return true;
                }
                ResponseBody[] annotations2 = handler.getBeanType().getDeclaredAnnotationsByType(ResponseBody.class);
                if (annotations2.length > 0) {
                    return true;
                }
                ResponseBody[] annotations3 = handler.getMethod().getAnnotationsByType(ResponseBody.class);
                return annotations3.length > 0;
            }
            return false;
        });
    }
}
