package aidenwaring.intro.cashcard.record;

import org.springframework.data.annotation.Id;

// Id annotation tells our CashCardRepository that the Long id param is the id
public record CashCard(@Id Long id, Double amount) {
}
