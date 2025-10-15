package dto;

import java.util.List;

public class MeterValuesRequest {
	
	 private int connectorId;
	    private int transactionId;
	    private List<MeterValue> meterValue;

	    // Getters and Setters
	    public int getConnectorId() {
	        return connectorId;
	    }

	    public void setConnectorId(int connectorId) {
	        this.connectorId = connectorId;
	    }

	    public int getTransactionId() {
	        return transactionId;
	    }

	    public void setTransactionId(int transactionId) {
	        this.transactionId = transactionId;
	    }

	    public List<MeterValue> getMeterValue() {
	        return meterValue;
	    }

	    public void setMeterValue(List<MeterValue> meterValue) {
	        this.meterValue = meterValue;
	    }

}
