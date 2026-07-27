package com.midea.cloud.srm.feign.pj.util;

import feign.FeignException;
import feign.Response;
import feign.codec.DecodeException;
import feign.codec.Decoder;
import java.io.IOException;
import java.lang.reflect.Type;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-18
 */
public class StringDecoder implements Decoder {
    private Decoder decoder;

    public StringDecoder(Decoder decoder) {
        this.decoder = decoder;
    }

    @Override
    public Object decode(Response response, Type type) throws IOException, DecodeException, FeignException {
        return this.decoder.decode(response, type);
    }
}
