package aidenwaring.intro.cashcard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
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

    @GetMapping()
    public ResponseEntity<Iterable<CashCard>> findAll() {
        return ResponseEntity.ok(cashCardRepository.findAll());
    }

    @GetMapping("/{requestedId}")
    public ResponseEntity<CashCard> findById(@PathVariable Long requestedId) {
        Optional<CashCard> cashCardOptional = cashCardRepository.findById(requestedId);
        if (cashCardOptional.isPresent()) {
            return ResponseEntity.ok(cashCardOptional.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}