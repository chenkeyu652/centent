package com.centent.auth.crypt;

import com.centent.core.bean.RSAKeyPair;
import com.centent.core.bean.Result;
import com.centent.core.util.AESUtil;
import com.centent.core.util.RSAUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.Objects;

@Slf4j
@ControllerAdvice
@SuppressWarnings("NullableProblems")
public class EncryptResponse implements ResponseBodyAdvice<Result<Object>> {

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private RSAKeyPair rsaKeyPair;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        Crypt crypt = null;
        if (returnType.hasMethodAnnotation(Crypt.class)) {
            crypt = returnType.getMethodAnnotation(Crypt.class);
        }
        return !Objects.isNull(crypt) && crypt.response();
    }

    @Override
    public Result<Object> beforeBodyWrite(Result<Object> result,
                                          MethodParameter returnType,
                                          MediaType selectedContentType,
                                          Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                          ServerHttpRequest request,
                                          ServerHttpResponse response) {
        try {
            // 只需要加密请求成功的数据
            if (!Objects.equals(0, result.getCode())) {
                return result;
            }

            long start = System.currentTimeMillis();
            String key = request.getHeaders().get("X-Auth-Key").get(0);
            key = RSAUtil.decrypt(rsaKeyPair.getPrivateKey(), key);
            if (Objects.nonNull(result.getData())) {
                result.setData(AESUtil.encrypt(key, objectMapper.writeValueAsBytes(result.getData())));
            }
            log.info("响应数据加密完成，耗时{}ms", System.currentTimeMillis() - start);
            return result;
        } catch (Exception e) {
            throw new RuntimeException("响应数据加密失败", e);
        }
    }
}
