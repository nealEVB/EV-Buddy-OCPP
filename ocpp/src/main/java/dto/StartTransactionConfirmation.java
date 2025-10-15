package dto;

public class StartTransactionConfirmation {
	
	private IdTagInfo idTagInfo;
    private int transactionId;

    // Getters and Setters
    public IdTagInfo getIdTagInfo() {
        return idTagInfo;
    }

    public void setIdTagInfo(IdTagInfo idTagInfo) {
        this.idTagInfo = idTagInfo;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

}
