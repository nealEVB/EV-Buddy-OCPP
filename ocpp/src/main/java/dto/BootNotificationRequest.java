package dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BootNotificationRequest {
	
	private String chargePointVendor;
    private String chargePointModel;
    @JsonProperty("chargePointSerialNumber") // JSON field has a different name
    private String chargePointSerialNumber;
    @JsonProperty("firmwareVersion")
    private String firmwareVersion;

    // Getters and Setters
    public String getChargePointVendor() {
        return chargePointVendor;
    }

    public void setChargePointVendor(String chargePointVendor) {
        this.chargePointVendor = chargePointVendor;
    }

    public String getChargePointModel() {
        return chargePointModel;
    }

    public void setChargePointModel(String chargePointModel) {
        this.chargePointModel = chargePointModel;
    }

    public String getChargePointSerialNumber() {
        return chargePointSerialNumber;
    }

    public void setChargePointSerialNumber(String chargePointSerialNumber) {
        this.chargePointSerialNumber = chargePointSerialNumber;
    }

    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

}
