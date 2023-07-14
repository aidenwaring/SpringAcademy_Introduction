package aidenwaring.intro.cashcard;

import org.springframework.data.repository.CrudRepository;

// We need to tell CrudRepository which data object the CashCardRepository should manage
// The 'domain type' of this repository is 'CashCard'

// CrudRepository<Object, Id>
// Need to indicate Id in the record
public interface CashCardRepository extends CrudRepository<CashCard, Long> {
}
