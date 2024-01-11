package com.bezruk.github.explorer.application;

import com.bezruk.github.explorer.exception.InternalErrorException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.util.internal.StringUtil;
import lombok.RequiredArgsConstructor;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

@RequiredArgsConstructor
public class ApplicationObjectMapper {

    private final ObjectMapper mapper;

    public <T> T readValue(String json, Class<T> objectType) {
        try {
            return mapper.readValue(json, objectType);
        } catch (IOException e) {
            throw decorateException(e, objectType.getName());
        }
    }

    public <T> T readValue(String json, TypeReference<T> typeReference) {
        try {
            return mapper.readValue(json, typeReference);
        } catch (JsonProcessingException e) {
            var objectType = typeReference.getType().getTypeName();
            throw decorateException(e, objectType);
        }
    }

    public <T> T readResponse(Response response, TypeReference<T> typeReference) {
        ResponseBody body = response.body();
        try (response; body) {
            return readValue(body.string(), typeReference);
        } catch (IOException e) {
            throw InternalErrorException.asErrorDuringResponseRead(e);
        }
    }

    public <T> T readResponse(Response response, Class<T> objectType) {
        ResponseBody body = response.body();
        try (response; body) {
            return readValue(body.string(), objectType);
        } catch (IOException e) {
            throw InternalErrorException.asErrorDuringResponseRead(e);
        }
    }

    private InternalErrorException decorateException(IOException e, String objectType) {
        objectType = StringUtils.substringAfterLast(objectType, ".");
        var message = String.format("Error during JSON deserialization. ObjectType: %s", objectType);
        return InternalErrorException.fromParent(e, message);
    }
}
