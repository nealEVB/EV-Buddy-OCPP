package service;

import com.fasterxml.jackson.databind.JsonNode;

public interface OcppService {
	
	/**
     * Processes the incoming OCPP request payload for a specific action.
     *
     * @param payload The JSON payload from the OCPP message.
     * @return An object representing the confirmation payload to be sent back.
     * Returns null if no response is required for the action (e.g., MeterValues).
     */
    Object process(JsonNode payload);

}
