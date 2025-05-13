public abstract class Payment {
    protected String paymentID;
    protected double amount;

    public Payment(String paymentID, double amount) {
        this.paymentID = paymentID;
        this.amount = amount;
    }
}