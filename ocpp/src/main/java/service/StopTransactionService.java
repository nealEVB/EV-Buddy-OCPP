package service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import dto.IdTagInfo;
import dto.StopTransactionConfirmation;
import dto.StopTransactionRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service("StopTransaction")
public class StopTransactionService implements OcppService {
    private static final Logger logger = LoggerFactory.getLogger(StopTransactionService.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Object process(JsonNode payload) {
        try {
            StopTransactionRequest request = objectMapper.treeToValue(payload, StopTransactionRequest.class);
            logger.info("Stopping transaction ID: {} with meter stop value: {}", 
                request.getTransactionId(), request.getMeterStop());

            // Logic to find the transaction in the database, calculate costs, and mark it as complete would go here.

            StopTransactionConfirmation confirmation = new StopTransactionConfirmation();
            IdTagInfo idTagInfo = new IdTagInfo();
            idTagInfo.setStatus("Accepted"); // Acknowledge the stop
            confirmation.setIdTagInfo(idTagInfo);
            
            return confirmation;
        } catch (Exception e) {
            logger.error("Error processing StopTransaction", e);
            return null;
        }
    }
}
