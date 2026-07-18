package es.MercadonaITDiegoRB.client;

import es.MercadonaITDiegoRB.client.dto.StoreDto;
import es.MercadonaITDiegoRB.exception.ExternalApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class StoresApiClient {

    private final RestClient restClient;

    @Autowired
    public StoresApiClient(@Value("${stores.api.base-url}") String baseUrl) {
        this(RestClient.builder()
                .baseUrl(baseUrl)
                .build());
    }

    StoresApiClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public StoreDto getStore(Long tiendaId) {
        try {
            StoreDto store = restClient.get()
                    .uri("/stores/{id}", tiendaId)
                    .retrieve()
                    .body(StoreDto.class);

            if (store == null) {
                throw new ExternalApiException(tiendaId);
            }

            return store;
        } catch (RestClientException exception) {
            throw new ExternalApiException(tiendaId, exception);
        }
    }
}
