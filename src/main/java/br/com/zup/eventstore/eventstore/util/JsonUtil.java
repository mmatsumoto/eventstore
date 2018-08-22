package br.com.zup.eventstore.eventstore.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class JsonUtil {

    private static Logger LOG = LogManager.getLogger(JsonUtil.class);
    private static final ObjectMapper mapper;

    static {
        mapper =  new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private JsonUtil() { }

    public static String objectToJson(Object obj) throws JsonProcessingException {
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            LOG.error("Error parsing obj to json {}", obj);
            throw e;
        }
    }

    public static <T> T mapToObject(Object from, Class<T> clazz) {
        try {
            return mapper.convertValue(from, clazz);
        } catch (IllegalArgumentException e) {
            LOG.error("Error converting {} to {}", from, clazz);
            throw e;
        }
    }

    public static <T> T jsonToObject(String json, Class<T> clazz) throws IOException {
        try {
            return mapper.readValue(json, clazz);
        } catch (IOException e) {
            LOG.error("Error parsing json to obj {} {}", json, clazz);
            throw e;
        }
    }

    public static <T> T jsonToObject(String json, TypeReference<T> typeReference) throws IOException {
        try {
            return mapper.readValue(json, typeReference);
        } catch (IOException e) {
            LOG.error("Error parsing json to obj {} {}", json, typeReference);
            throw e;
        }
    }

    public static Map<String, Object> jsonToMap(String json) throws IOException {
        try {
            return mapper.readValue(json, new TypeReference<Map<String, Object>>(){});
        } catch (IOException e) {
            LOG.error("Error parsing json to Map<String,String> {} {}", json);
            throw e;
        }
    }

    public static <T> List<T> jsonToList(String json, Class<T> clazz) throws IOException {
        try {
            return mapper.readValue(json, TypeFactory.defaultInstance().constructCollectionType(List.class, clazz));
        } catch (IOException e) {
            LOG.error("Error parsing list json to obj {} {}", json, clazz);
            throw e;
        }
    }

    public static <T extends JsonNode> T valueToTree(Object from) {
        return mapper.valueToTree(from);
    }

    public static JsonNode readTree(String from) throws IOException {
        try {
            return mapper.readTree(from);
        } catch (IOException e) {
            LOG.error("Error readTree(String) {}", from);
            throw e;
        }
    }

    public static JsonNode readTree(byte[] from) throws IOException {
        try {
            return mapper.readTree(from);
        } catch (IOException e) {
            LOG.error("Error readTree(byte[]) {}", from);
            throw e;
        }
    }

    public static ObjectMapper getMapper() {
        return mapper.copy();
    }
}