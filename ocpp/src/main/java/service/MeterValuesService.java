package service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import dto.MeterValuesRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service("MeterValues")
public class MeterValuesService implements OcppService {
    private static final Logger logger = LoggerFactory.getLogger(MeterValuesService.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Object process(JsonNode payload) {
        try {
            MeterValuesRequest request = objectMapper.treeToValue(payload, MeterValuesRequest.class);
            logger.info("Received MeterValues for transaction ID: {}", request.getTransactionId());
            
            // In a real application, you would iterate through request.getMeterValue()
            // and save each sampled value to your database for billing and analytics.
            
        } catch (Exception e) {
            logger.error("Error processing MeterValues", e);
        }
        
        // The OCPP 1.6 spec does not require a response for MeterValues, so we return null.
        return null;
    }
}