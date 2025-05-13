public class DigitalPayment extends Payment implements Payable {
    private String transactionID;

    public DigitalPayment(String paymentID, double amount, String transactionID) {
        super(paymentID, amount);
        if (transactionID == null || transactionID.isEmpty()) {
            throw new IllegalArgumentException("Transaction ID cannot be empty.");
        }
        this.transactionID = transactionID;
    }

    @Override
    public boolean processPayment(Order order) {
        if (transactionID.startsWith("TXN")) {
            order.getVendor().updateEarnings(amount);
            return true;
        }
        return false;
    }
}