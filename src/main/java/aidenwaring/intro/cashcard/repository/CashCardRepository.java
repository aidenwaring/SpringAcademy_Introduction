package aidenwaring.intro.cashcard.repository;

import aidenwaring.intro.cashcard.record.CashCard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

// We need to tell CrudRepository which data object the CashCardRepository should manage
// The 'domain type' of this repository is 'CashCard'

// CrudRepository<Object, Id>
// Need to indicate Id in the record
public interface CashCardRepository extends CrudRepository<CashCard, Long>, PagingAndSortingRepository<CashCard, Long> {
    CashCard findByIdAndOwner(Long id, String owner);
    Page<CashCard> findByOwner(String owner, PageRequest pageRequest);

    /*
        Why not just use the findByIdAndOwner() method and check whether it returns null?
        Such a call would return extra information (the content of the Cash Card retrieved),
        so we'd like to avoid it as to not introduce extra complexity.
     */
    boolean existsByIdAndOwner(Long id, String owner);

}