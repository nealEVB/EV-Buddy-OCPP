package dto;

public class SampledValue {
	
	private String value;
    // According to OCPP 1.6, other optional fields can be added here, such as:
    // private String context;
    // private String format;
    // private String measurand;
    // private String phase;
    // private String location;
    // private String unit;
    
    // Getter and Setter
    public String getValue() { 
        return value; 
    }
    
    public void setValue(String value) { 
        this.value = value; 
    }

}
