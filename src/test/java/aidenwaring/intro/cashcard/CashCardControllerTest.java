package aidenwaring.intro.cashcard;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CashCardControllerTest {
    /*
    Inject a test helper that’ll allow us to
    make HTTP requests to the locally running application.
     */
    @Autowired
    TestRestTemplate restTemplate;

    @Test
    void shouldCreateANewCashCard() {
        CashCard cashcard = new CashCard(null, 250.00);
        //Create the post request
        ResponseEntity<Void> createResponse = restTemplate.postForEntity("/cashcards", cashcard, Void.class);
        // Assert that it is created
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // Perform a get on the newly created resource, then assert it was GET successfully
        URI locationOfNewCashCard = createResponse.getHeaders().getLocation();
        ResponseEntity<String> getResponse = restTemplate.getForEntity(locationOfNewCashCard, String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Json path assertions
        // Parse string response into JSON
        DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
        // Assign JSON values at JSON path values a variable
        Number id = documentContext.read("$.id");
        Double amount = documentContext.read("$.amount");

        // Assert that the values created from the POST
        // are that the values once parsed meet our expectations
        assertThat(id).isNotNull();
        assertThat(amount).isEqualTo(250.00);
    }

    @Test
    void shouldReturnACashCardWhenDataIsSaved() {
        ResponseEntity<String> response = restTemplate.getForEntity("/cashcards/99", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        // This converts the response String into a JSON-aware object with lots of helper methods.
        DocumentContext documentContext = JsonPath.parse(response.getBody());

        // Store value of ID in JSON path to variable, assert it is not null
        Number id = documentContext.read("$.id");
        assertThat(id).isEqualTo(99);

        Double amount = documentContext.read("$.amount");
        assertThat(amount).isEqualTo(123.45);
    }

    @Test
    void shouldNotReturnACashCardWithAnUnknownId() {
        ResponseEntity<String> response = restTemplate.getForEntity("/cashcards/1000", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isBlank();
    }
}