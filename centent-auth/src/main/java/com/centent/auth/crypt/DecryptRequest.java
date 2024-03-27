package com.centent.auth.crypt;

import com.centent.core.bean.RSAKeyPair;
import com.centent.core.util.AESUtil;
import com.centent.core.util.RSAUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

@Slf4j
@ControllerAdvice
@SuppressWarnings("NullableProblems")
public class DecryptRequest extends RequestBodyAdviceAdapter {

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private HttpServletRequest request;

    @Resource
    private RSAKeyPair rsaKeyPair;

    @Override
    public boolean supports(MethodParameter methodParameter,
                            Type targetType,
                            Class<? extends HttpMessageConverter<?>> converterType) {
        Crypt crypt = null;
        if (methodParameter.hasMethodAnnotation(Crypt.class)) {
            crypt = methodParameter.getMethodAnnotation(Crypt.class);
        } else if (methodParameter.hasParameterAnnotation(Crypt.class)) {
            crypt = methodParameter.getParameterAnnotation(Crypt.class);
        }
        return !Objects.isNull(crypt) && crypt.request();
    }

    @Override
    public HttpInputMessage beforeBodyRead(final HttpInputMessage inputMessage,
                                           MethodParameter parameter,
                                           Type targetType,
                                           Class<? extends HttpMessageConverter<?>> converterType) throws IOException {
        try {
            long start = System.currentTimeMillis();
            // 将读取出来的数据转换成CryptEntity
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            inputMessage.getBody().transferTo(result);
            String bodyAsString = result.toString(StandardCharsets.UTF_8);
            log.info("CryptEntity: {}", bodyAsString);
            Map<String, String> map = objectMapper.readValue(bodyAsString, Map.class);
            String key = request.getHeader("X-Auth-Key");
            key = RSAUtil.decrypt(rsaKeyPair.getPrivateKey(), key);
            log.info("RSA解密后的key: {}", key);

            byte[] decrypt = AESUtil.decrypt(key, map.get("data"));
            final ByteArrayInputStream bias = new ByteArrayInputStream(decrypt);
            HttpInputMessage httpInputMessage = new HttpInputMessage() {
                @Override
                public InputStream getBody() {
                    return bias;
                }

                @Override
                public HttpHeaders getHeaders() {
                    return inputMessage.getHeaders();
                }
            };
            log.info("请求数据解密完成，耗时{}ms", System.currentTimeMillis() - start);
            return httpInputMessage;
        } catch (Exception e) {
            throw new RuntimeException("请求数据解密失败", e);
        }
        // return super.beforeBodyRead(inputMessage, parameter, targetType, converterType);
    }
}
