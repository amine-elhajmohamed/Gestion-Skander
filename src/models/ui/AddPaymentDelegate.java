package models.ui;

import models.Payment;

public interface AddPaymentDelegate {
	public void didAddNewPayment(Payment payment);
}
