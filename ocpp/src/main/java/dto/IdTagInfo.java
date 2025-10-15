package dto;

public class IdTagInfo {
	
	 private String status; // "Accepted", "Blocked", "Expired", "Invalid", "ConcurrentTx"

	    // Getter and Setter
	    public String getStatus() {
	        return status;
	    }

	    public void setStatus(String status) {
	        this.status = status;
	    }

}
