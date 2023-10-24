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

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/cashcards")
public class CashCardController {
    CashCardRepository cashCardRepository;

    public CashCardController(CashCardRepository $cashCardRepository) {
        this.cashCardRepository = $cashCardRepository;
    }

    @PostMapping
    // Spring Web will deserialize the data from the request body into an object
    /*
    We were able to add UriComponentsBuilder ucb as a method argument to this POST
    handler method, and it was automatically passed in. How so?
    It was injected from our now-familiar friend, Spring's IoC Container.
     */
    private ResponseEntity<Void> createCashCard(@RequestBody CashCard newCashCardRequest, UriComponentsBuilder ucb) {
        CashCard savedCashCard = cashCardRepository.save(newCashCardRequest);
        // Building a URI to provide
        URI locationOfNewCashCard = ucb
                .path("cashcards/{id}")
                .buildAndExpand(savedCashCard.id())
                .toUri();
        return ResponseEntity.created(locationOfNewCashCard).build();
    }

    @GetMapping
    public ResponseEntity<List<CashCard>> getCashCards(Pageable pageable) {
        Page<CashCard> page = cashCardRepository.findAll(
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
    public ResponseEntity<CashCard> getCashCard(@PathVariable Long requestedId) {
        Optional<CashCard> cashCardOptional = cashCardRepository.findById(requestedId);
        if (cashCardOptional.isPresent()) {
            return ResponseEntity.ok(cashCardOptional.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}