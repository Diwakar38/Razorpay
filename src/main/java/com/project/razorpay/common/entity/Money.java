package com.project.razorpay.common.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;

import java.util.Objects;

@AllArgsConstructor
@Embeddable
public class Money {
    private int amountUnits;
    private String currency;

    public static Money of(int amountUnits, String currency) {
        return new Money(amountUnits, currency);
    }

    public static Money inr(int amount) {
        return new Money(amount, "INR");
    }

    public static Money usd(int amount) {
        return new Money(amount, "USD");
    }

    public static Money eur(int amount) {
        return new Money(amount, "EUR");
    }

    public static Money jpy(int amount) {
        return new Money(amount, "JPY");
    }

    public Money add(Money other) {
        if (Objects.equals(this.currency, other.currency)) {
            return new Money(this.amountUnits + other.amountUnits, this.currency);
        } else {
            throw new IllegalArgumentException("Cannot add money with different currencies");
        }
    }

    public Money subtract(Money other) {
        if (Objects.equals(this.currency, other.currency)) {
            return new Money(this.amountUnits - other.amountUnits, this.currency);
        } else {
            throw new IllegalArgumentException("Cannot subtract money with different currencies");
        }
    }

}
