package es.MercadonaITDiegoRB.client;

import es.MercadonaITDiegoRB.client.dto.StoreDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withResourceNotFound;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class StoresApiClientTest {

    private MockRestServiceServer server;
    private StoresApiClient client;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        client = new StoresApiClient(
                builder.baseUrl("http://localhost:8080").build()
        );
    }

    @Test
    void retrievesStoreById() {
        server.expect(once(), requestTo("http://localhost:8080/stores/1"))
                .andExpect(method(org.springframework.http.HttpMethod.GET))
                .andRespond(withSuccess(
                        """
                                {
                                  "id": 1,
                                  "description": "SANTOMERA",
                                  "address": "AVDA JUAN CARLOS I . 17",
                                  "city": "SANTOMERA"
                                }
                                """,
                        MediaType.APPLICATION_JSON
                ));

        StoreDto result = client.getStore(1L);

        assertEquals(1L, result.id());
        assertEquals("SANTOMERA", result.description());
        assertEquals("AVDA JUAN CARLOS I . 17", result.address());
        assertEquals("SANTOMERA", result.city());
        server.verify();
    }

    @Test
    void returnsFallbackStoreWhenExternalApiFails() {
        server.expect(once(), requestTo("http://localhost:8080/stores/99"))
                .andRespond(withResourceNotFound());

        StoreDto result = client.getStore(99L);

        assertEquals(99L, result.id());
        assertEquals("?", result.description());
        assertEquals("?", result.address());
        assertEquals("?", result.city());
        server.verify();
    }
}
