package aidenwaring.intro.cashcard.controller;

import aidenwaring.intro.cashcard.record.CashCard;
import aidenwaring.intro.cashcard.repository.CashCardRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import java.security.Principal;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/cashcards")
public class CashCardController {
    CashCardRepository cashCardRepository;

    public CashCardController(CashCardRepository $cashCardRepository) {
        this.cashCardRepository = $cashCardRepository;
    }

    // Spring Web will deserialize the data from the request body into an object
    /*
        We were able to add UriComponentsBuilder ucb as a method argument to this POST
        handler method, and it was automatically passed in. How so?
        It was injected from our now-familiar friend, Spring's IoC Container.
     */
    @PostMapping
    private ResponseEntity<Void> createCashCard(@RequestBody CashCard newCashCardRequest, UriComponentsBuilder ucb, Principal principal) {
        CashCard cashCardWithOwner = new CashCard(null, newCashCardRequest.amount(), principal.getName());
        CashCard savedCashCard = cashCardRepository.save(cashCardWithOwner);
        // Building a URI to provide
        URI locationOfNewCashCard = ucb
                .path("cashcards/{id}")
                .buildAndExpand(savedCashCard.id())
                .toUri();
        return ResponseEntity.created(locationOfNewCashCard).build();
    }

    @GetMapping
    public ResponseEntity<List<CashCard>> findAll(Pageable pageable, Principal principal) {
        Page<CashCard> page = cashCardRepository.findByOwner(principal.getName(),
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        pageable.getSortOr(Sort.by(Sort.Direction.ASC, "amount"))
                        // ^ Get sort from URI param or use the default specified here
                        // Spring provides default page num and page size (page 0 and size 20)
                ));
        return ResponseEntity.ok(page.getContent());
    }

    @GetMapping("/{requestedId}")

    public ResponseEntity<CashCard> findById(@PathVariable Long requestedId, Principal principal) {
        // The Principal holds our user's authenticated, authorized information from the request header.
        CashCard cashCard = getByIdAndOwner(requestedId, principal);
        if (cashCard != null) {
            return ResponseEntity.ok(cashCard);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @PutMapping("/{requestedId}")
    private ResponseEntity<Void> putCashCard(@PathVariable Long requestedId, @RequestBody CashCard cashCardUpdate, Principal principal) {
        CashCard cashCard = getByIdAndOwner(requestedId, principal);
        if (cashCard != null) {
            CashCard updatedCashCard = new CashCard(cashCard.id(), cashCardUpdate.amount(), principal.getName());
            cashCardRepository.save(updatedCashCard);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    private ResponseEntity<Void> deleteCashCard(@PathVariable Long id, Principal principal) {
        // Checks if the record exists and the Principal is the owner of the card
        if (!cashCardRepository.existsByIdAndOwner(id, principal.getName())) {
            return ResponseEntity.notFound().build();
        }
        cashCardRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private CashCard getByIdAndOwner(Long requestedId, Principal principal) {
        return cashCardRepository.findByIdAndOwner(requestedId, principal.getName());
    }
}