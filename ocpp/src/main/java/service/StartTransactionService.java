package service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import dto.IdTagInfo;
import dto.StartTransactionConfirmation;
import dto.StartTransactionRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.concurrent.atomic.AtomicInteger;

@Service("StartTransaction")
public class StartTransactionService implements OcppService {
    private static final Logger logger = LoggerFactory.getLogger(StartTransactionService.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    // Using AtomicInteger for a simple, thread-safe way to generate unique transaction IDs.
    // In a production system, this would likely come from a database sequence.
    private final AtomicInteger transactionIdGenerator = new AtomicInteger(1);

    @Override
    public Object process(JsonNode payload) {
        try {
            StartTransactionRequest request = objectMapper.treeToValue(payload, StartTransactionRequest.class);
            logger.info("Starting transaction for idTag: {} on connector: {}", request.getIdTag(), request.getConnectorId());

            // Here you would typically validate the idTag against a database of authorized users.
            
            StartTransactionConfirmation confirmation = new StartTransactionConfirmation();
            IdTagInfo idTagInfo = new IdTagInfo();
            idTagInfo.setStatus("Accepted"); // Assuming the tag is always valid for this example
            
            confirmation.setIdTagInfo(idTagInfo);
            confirmation.setTransactionId(transactionIdGenerator.getAndIncrement());
            
            return confirmation;
        } catch (Exception e) {
            logger.error("Error processing StartTransaction", e);
            return null; // Or return a rejected confirmation
        }
    }
}
