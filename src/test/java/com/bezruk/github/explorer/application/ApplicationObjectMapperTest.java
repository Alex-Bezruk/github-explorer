package com.bezruk.github.explorer.application;

import com.bezruk.github.explorer.exception.InternalErrorException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ApplicationObjectMapperTest {

    @Mock
    private ObjectMapper mapper;

    @InjectMocks
    private ApplicationObjectMapper applicationObjectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void readValue_withClass_shouldReturnObject() throws IOException {
        String json = "{\"key\":\"value\"}";
        TestObject testObject = new TestObject("value");

        when(mapper.readValue(json, TestObject.class)).thenReturn(testObject);

        TestObject result = applicationObjectMapper.readValue(json, TestObject.class);

        assertEquals(testObject, result);
    }

    @Test
    void readValue_withTypeReference_shouldReturnObject() throws IOException {
        String json = "[{\"key\":\"value\"}]";
        TestObject testObject = new TestObject("value");

        TypeReference<TestObject> typeReference = new TypeReference<TestObject>() {
        };
        when(mapper.readValue(json, typeReference)).thenReturn(testObject);

        TestObject result = applicationObjectMapper.readValue(json, typeReference);

        assertEquals(testObject, result);
    }

    @Test
    void readResponse_withTypeReference_shouldReturnObject() throws IOException {
        String json = "[{\"key\":\"value\"}]";
        TestObject testObject = new TestObject("value");

        Response response = mock(Response.class);
        ResponseBody responseBody = mock(ResponseBody.class);
        when(response.body()).thenReturn(responseBody);
        when(responseBody.string()).thenReturn(json);

        TypeReference<TestObject> typeReference = new TypeReference<TestObject>() {
        };
        when(mapper.readValue(json, typeReference)).thenReturn(testObject);

        TestObject result = applicationObjectMapper.readResponse(response, typeReference);

        assertEquals(testObject, result);
        verify(response, times(1)).body();
    }

    @Test
    void readResponse_withClass_shouldReturnObject() throws IOException {
        String json = "{\"key\":\"value\"}";
        TestObject testObject = new TestObject("value");

        Response response = mock(Response.class);
        ResponseBody responseBody = mock(ResponseBody.class);
        when(response.body()).thenReturn(responseBody);
        when(responseBody.string()).thenReturn(json);

        when(mapper.readValue(json, TestObject.class)).thenReturn(testObject);

        TestObject result = applicationObjectMapper.readResponse(response, TestObject.class);

        assertEquals(testObject, result);
        verify(response, times(1)).body();
    }

    @Test
    void readValue_withJsonProcessingException_shouldThrowInternalErrorException() throws IOException {
        String json = "{\"key\":\"value\"}";


        when(mapper.readValue(json, TestObject.class)).thenAnswer(invocationOnMock -> {
            throw new IOException("Test exception");
        });

        try {
            applicationObjectMapper.readValue(json, TestObject.class);
        } catch (InternalErrorException e) {
            var expectedMessage = "Unexpected Internal Error." +
                    " Error during JSON deserialization." +
                    " ObjectType: ApplicationObjectMapperTest$TestObject." +
                    " Parent Exception: Test exception";

            assertEquals(expectedMessage, e.getMessage());
        }
    }

    private static class TestObject {
        private String key;

        public TestObject(String key) {
            this.key = key;
        }
    }
}
