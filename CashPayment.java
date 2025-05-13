public class CashPayment extends Payment implements Payable {
    private double cashReceived;

    public CashPayment(String paymentID, double amount, double cashReceived) {
        super(paymentID, amount);
        if (cashReceived < 0) {
            throw new IllegalArgumentException("Cash received cannot be negative.");
        }
        this.cashReceived = cashReceived;
    }

    @Override
    public boolean processPayment(Order order) {
        if (cashReceived >= amount) {
            order.getVendor().updateEarnings(amount);
            return true;
        }
        return false;
    }

    public double calculateChange() {
        return cashReceived - amount;
    }

    @Override
    public String toString() {
        return "CashPayment{" +
                "paymentID='" + paymentID + '\'' +
                ", amount=" + amount +
                ", cashReceived=" + cashReceived +
                ", change=" + calculateChange() +
                '}';
    }
}