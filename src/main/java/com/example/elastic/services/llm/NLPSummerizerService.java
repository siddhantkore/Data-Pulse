package com.example.elastic.services.llm;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class NLPSummerizerService {

    private final WebClient webClient;
    private final String endpoint;

    @Autowired
    public NLPSummerizerService(WebClient webClient, @Value("${nlp.endpoint}") String endpoint) {
        this.webClient = webClient;
        this.endpoint = endpoint;
    }

    public String processWithNLPPipeline(String extractedText) {
        Map<String, String> body = Map.of("text_content", extractedText);

        return webClient.post()
                .uri(endpoint)
                .header("Content-Type", "application/json")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .block();

    }
}
