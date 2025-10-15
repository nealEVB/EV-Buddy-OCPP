package com.example.ocpp.ws;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class OcppMessageCodec {
    private final ObjectMapper mapper = new ObjectMapper();
    public JsonNode parse(String text) throws Exception { return mapper.readTree(text); }
    public String toJson(Object obj) throws Exception { return mapper.writeValueAsString(obj); }
}
