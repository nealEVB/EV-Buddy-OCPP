package dto;

import java.util.List;

public class MeterValue {
	 private String timestamp;
	    private List<SampledValue> sampledValue;
	    
	    // Getters and Setters
	    public String getTimestamp() { return timestamp; }
	    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
	    public List<SampledValue> getSampledValue() { return sampledValue; }
	    public void setSampledValue(List<SampledValue> sampledValue) { this.sampledValue = sampledValue; }

}
