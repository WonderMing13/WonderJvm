package org.wonderming.utils;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.QueryStringDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wangdeming
 **/
public class FullHttpRequestUtil {

    private final Logger logger = LoggerFactory.getLogger(FullHttpRequestUtil.class);

    public static Map<String,String> parseParam(FullHttpRequest fullHttpRequest){
        final HttpMethod httpMethod = fullHttpRequest.method();
        Map<String, String> paramMap = new HashMap<>(16);
        if (HttpMethod.GET.name().equals(httpMethod.name())){
            QueryStringDecoder decoder = new QueryStringDecoder(fullHttpRequest.uri());
            Map<String, List<String>> parameters = decoder.parameters();
            parameters.forEach((key, value) -> {
                paramMap.put(key, value.get(0));
            });
        }
        return paramMap;
    }

}
