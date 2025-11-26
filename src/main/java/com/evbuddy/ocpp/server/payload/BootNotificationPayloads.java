package com.evbuddy.ocpp.server.payload;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public class BootNotificationPayloads {
	public static class Req {
		public String chargePointVendor;
		public String chargePointModel;
		public String firmwareVersion;
		private String chargePointSerialNumber;
		public String getChargePointSerialNumber() {
			return chargePointSerialNumber;
		}
		public void setChargePointSerialNumber(String chargePointSerialNumber) {
			this.chargePointSerialNumber = chargePointSerialNumber;
		}
	}

	public static class Res {
		public String status;
		public String currentTime;
		public int interval;

		public Res(String s, String t, int i) {
			status = s;
			currentTime = t;
			interval = i;
		}
	}
}
