package com.ak.rfp.serviceImplementation;

import com.ak.rfp.dto.ProposalScoreDto;
import com.ak.rfp.service.AiService;
import com.ak.rfp.config.PerplexityConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AiServiceImplementation implements AiService {
    private static final Logger logger = LoggerFactory.getLogger(AiServiceImplementation.class);
    private final PerplexityConfig config;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Autowired
    public AiServiceImplementation(PerplexityConfig config, WebClient.Builder webClientBuilder) {
        this.config = config;
        this.objectMapper = new ObjectMapper();

        // Validate API key
        if (config.getKey() == null || config.getKey().isEmpty()) {
            logger.error("⚠️ PERPLEXITY_API_KEY not set!");
        } else {
            logger.info("✅ Perplexity AI configured");
            logger.info("Model: {}", config.getModel());
        }

        this.webClient = webClientBuilder
                .baseUrl(config.getBaseUrl())
                .defaultHeader("Authorization", "Bearer " + config.getKey())
                .build();
    }



    /**
     * Generate RFP from natural language description
     *
     * @param description Natural language description (e.g., "I need 20 laptops with 16GB RAM...")
     * @return Structured RFP as JSON string
     */
    @Override
    public String generateRfpFromText(String description) {
        // Fix 1: Validate input
        if (description == null || description.trim().isEmpty()) {
            logger.warn("Empty description provided—returning empty RFP JSON");
            return "{}";
        }

        logger.info("🔄 Generating RFP from text...");

        String systemPrompt = """
            You are a JSON generator. Parse the request and output EXACTLY one JSON object. 
            NO text, NO markdown, NO lists, NO headers. JSON ONLY. { first, } last.
            
            Infer all fields reasonably (budget from items, timeline 30 days default, 1-5 items).
            
            Example Request: "20 laptops 16GB RAM"
            JSON: {"title":"Laptop Procurement","budget":20000,"deliveryTimelineDays":30,"paymentTerms":"Net 30","warrantyTerms":"1 Year","items":[{"itemType":"Laptop","quantity":20,"requiredSpecs":"16GB RAM"}]}
            
            STRUCTURE:
            {
              "title": "concise title",
              "budget": number,
              "deliveryTimelineDays": number,
              "paymentTerms": "string",
              "warrantyTerms": "string",
              "items": [
                {
                  "itemType": "string",
                  "quantity": number,
                  "requiredSpecs": "string"
                }
              ]
            }
            """;

        String userPrompt = description.trim();

        try {
            String rawResponse = callPerplexityApi(systemPrompt, userPrompt);

            String logSnippet = (rawResponse != null && rawResponse.length() > 0)
                    ? rawResponse.substring(0, Math.min(300, rawResponse.length()))
                    : "Empty response";
            logger.debug("Raw AI response (first 300 chars): {}", logSnippet);

            String jsonResponse = extractJsonFromResponse(rawResponse);
            Map<String, Object> rfpData = objectMapper.readValue(jsonResponse, Map.class);

            String title = (String) rfpData.getOrDefault("title", "");
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> items = (List<Map<String, Object>>) rfpData.getOrDefault("items", List.of());
            if (title.isEmpty() && items.isEmpty()) {
                logger.warn("AI returned essentially empty RFP: {}", rfpData);
                throw new RuntimeException("AI generated empty RFP—try a more detailed description or check model.");
            }
            if (title.isEmpty()) {
                logger.warn("AI omitted title—using default: {}", rfpData);
            }
            if (items.isEmpty()) {
                logger.warn("AI omitted items—using empty list");
            }

            logger.info("✅ RFP generated and parsed: Title='{}', Items={}",
                    title.isEmpty() ? "N/A" : title,
                    items.size());

            return jsonResponse;
        } catch (Exception e) {
            // Fix 4: Proper logger.error with exception
            logger.error("❌ Error generating RFP. Raw response snippet: {}", e);
            throw new RuntimeException("Failed to generate RFP: Invalid JSON from AI. Check prompt/response.", e);
        }
    }

    private String extractJsonFromResponse(String response) {
        if (response == null || response.trim().isEmpty()) return "{}";

        response = response.trim();

        // Try direct
        try {
            objectMapper.readTree(response);
            return response;
        } catch (Exception ignored) {
            logger.warn("Direct failed. Snippet: {}", response.substring(0, 200));
        }

        java.util.regex.Pattern jsonPattern = java.util.regex.Pattern.compile("\\{.*\\}", java.util.regex.Pattern.DOTALL);
        java.util.regex.Matcher matcher = jsonPattern.matcher(response);
        if (matcher.find()) {
            String jsonBlock = matcher.group();
            try {
                objectMapper.readTree(jsonBlock);
                logger.info("Extracted JSON: {} chars", jsonBlock.length());
                return jsonBlock;
            } catch (Exception e) {
                logger.warn("Block invalid: {}", e.getMessage());
            }
        }

        String[] lines = response.split("\n");
        StringBuilder potentialJson = new StringBuilder();
        for (String line : lines) {
            if (line.trim().startsWith("{") || line.contains("{")) {
                potentialJson.append(line.trim());
            }
        }
        if (potentialJson.length() > 2) {  // Has { }
            try {
                return objectMapper.writeValueAsString(objectMapper.readTree(potentialJson.toString()));
            } catch (Exception ignored) {}
        }

        logger.error("No JSON in: {}", response.substring(0, 300));
        return "{}";
    }


    public String parseVendorEmail(String rfpRequirements, String emailBody) {
        logger.info("🔄 Parsing vendor email...");

        String systemPrompt = """
            You are an expert at extracting procurement information from vendor emails.
            Extract the proposal details from the email below and return ONLY valid JSON (no markdown) with this structure:
            {
              "totalPrice": number,
              "currency": "string",
              "deliveryDays": number,
              "paymentTerms": "string",
              "warrantyTerms": "string",
              "items": [
                {
                  "forRfpItemId": number,
                  "quantity": number,
                  "unitPrice": number,
                  "totalPrice": number
                }
              ]
            }
            
            Be strict about JSON format. Return ONLY JSON, nothing else.
            """;

        String userPrompt = String.format("""
            Original RFP Requirements:
            %s
            
            Vendor Email Response:
            %s
            
            Extract the proposal details from this email.
            """, rfpRequirements, emailBody);

        try {
            String response = callPerplexityApi(systemPrompt, userPrompt);
            logger.info("✅ Email parsed successfully");
            return response;
        } catch (Exception e) {
            logger.error("❌ Error parsing email", e);
            throw new RuntimeException("Failed to parse email: " + e.getMessage());
        }
    }


    public String scoreProposals(String rfpDetails, String proposals) {
        logger.info("🔄 Scoring proposals...");

        String systemPrompt = """
            You are an expert procurement evaluator. Score the following proposals against the RFP requirements.
            
            Return ONLY valid JSON (no markdown) with this structure:
            [
              {
                "proposalId": number,
                "overallScore": number (0-10),
                "priceScore": number (0-10),
                "timelineScore": number (0-10),
                "qualityScore": number (0-10),
                "explanation": "string"
              }
            ]
            
            Be strict about JSON format. Return ONLY JSON array, nothing else.
            """;

        String userPrompt = String.format("""
            RFP Details:
            %s
            
            Proposals to Score:
            %s
            
            Score each proposal fairly and provide explanations.
            """, rfpDetails, proposals);

        try {
            String response = callPerplexityApi(systemPrompt, userPrompt);
            logger.info("✅ Proposals scored successfully");
            return response;
        } catch (Exception e) {
            logger.error("❌ Error scoring proposals", e);
            throw new RuntimeException("Failed to score proposals: " + e.getMessage());
        }
    }


    private String callPerplexityApi(String systemPrompt, String userPrompt) {
        logger.debug("📤 Calling Perplexity API...");

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", config.getModel());  // Ensure Perplexity model
        requestBody.put("max_tokens", 2048);
        requestBody.put("temperature", 0.1);

        List<Map<String, String>> messages = List.of(
                Map.of("role", "system", "content", systemPrompt),
                Map.of("role", "user", "content", userPrompt)
        );
        requestBody.put("messages", messages);

        try {
            String bodyJson = objectMapper.writeValueAsString(requestBody);
            logger.debug("Request body sent: {}", bodyJson.substring(0, 500) + (bodyJson.length() > 500 ? "..." : ""));

            String responseBody = webClient.post()
                    .uri("/chat/completions")
                    .header("Authorization", "Bearer " + config.getKey())
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, clientResponse ->
                            clientResponse.bodyToMono(String.class)
                                    .flatMap(body -> {
                                        logger.error("Perplexity 400 Error Body: {}", body);
                                        return Mono.error(new RuntimeException("Bad Request: " + body));
                                    })
                    )
                    .onStatus(HttpStatusCode::is5xxServerError, clientResponse ->
                            clientResponse.bodyToMono(String.class)
                                    .flatMap(body ->
                                            Mono.error(new RuntimeException("Server Error: " + body))
                                    )
                    )
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(config.getTimeoutSeconds()))
                    .block();

            logger.debug("📥 Response length: {} chars", responseBody.length());

            JsonNode root = objectMapper.readTree(responseBody);
            String content = root.path("choices").get(0).path("message").path("content").asText();

            if (content.isEmpty()) {
                logger.warn("Empty content from Perplexity");
                return "{}";
            }

            logger.debug("Content snippet: {}", content.substring(0, 200));
            return content;

        } catch (WebClientResponseException e) {
            logger.error("Perplexity API Exception: Status={}, Body={}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Perplexity API call failed: " + e.getStatusCode() + " - " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            logger.error("Unexpected Perplexity error: {}", e.getMessage(), e);
            throw new RuntimeException("Perplexity API call failed: " + e.getMessage(), e);
        }
    }


    public boolean isConfigured() {
        return config.getKey() != null && !config.getKey().isEmpty();
    }


    public String getModel() {
        return config.getModel();
    }
}
