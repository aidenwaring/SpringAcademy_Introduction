package aidenwaring.intro.cashcard.repository;

import aidenwaring.intro.cashcard.record.CashCard;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

// We need to tell CrudRepository which data object the CashCardRepository should manage
// The 'domain type' of this repository is 'CashCard'

// CrudRepository<Object, Id>
// Need to indicate Id in the record
public interface CashCardRepository extends CrudRepository<CashCard, Long>, PagingAndSortingRepository<CashCard, Long> {
}
