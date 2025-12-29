package com.inditex.backenddevtest.product.infrastructure.config;

import com.inditex.backenddevtest.product.domain.ProductNotFoundException;
import com.inditex.backenddevtest.product.domain.ProductServiceException;
import feign.Response;
import feign.codec.ErrorDecoder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProductFeignErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        String productId = extractProductIdFromRequest(response.request()
                                                               .url());

        if (response.status() == 404) {
            return new ProductNotFoundException(productId);
        }

        if (response.status() >= 500) {
            return new ProductServiceException(productId, "Upstream service returned " + response.status());
        }

        return defaultDecoder.decode(methodKey, response);
    }

    private String extractProductIdFromRequest(String url) {
        Pattern pattern = Pattern.compile("/product/([^/]+)");
        Matcher matcher = pattern.matcher(url);
        return matcher.find() ? matcher.group(1) : "unknown";
    }
}
