 package dto;

public class BootNotificationConfirmation {
	
	 private String currentTime;
	    private int interval;
	    private String status; // Can be "Accepted", "Pending", "Rejected"

	    // Getters and Setters
	    public String getCurrentTime() {
	        return currentTime;
	    }

	    public void setCurrentTime(String currentTime) {
	        this.currentTime = currentTime;
	    }

	    public int getInterval() {
	        return interval;
	    }

	    public void setInterval(int interval) {
	        this.interval = interval;
	    }

	    public String getStatus() {
	        return status;
	    }

	    public void setStatus(String status) {
	        this.status = status;
	    }

}
