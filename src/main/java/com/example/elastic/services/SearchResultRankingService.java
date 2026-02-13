package com.example.elastic.services;

import com.example.elastic.models.DocumentMetadata;
import com.example.elastic.services.processors.LlmProcessingService;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

/**
 * Service for ranking search results using LLM-based relevance scoring.
 * Enhances search result quality by using language models to evaluate document relevance.
 * Implements sophisticated ranking algorithms beyond simple TF-IDF scoring.
 */
@Service
public class SearchResultRankingService {

    private final LlmProcessingService llmProcessingService;

    public SearchResultRankingService(LlmProcessingService llmProcessingService) {
        this.llmProcessingService = llmProcessingService;
    }

    /**
     * Ranks search results using LLM-based relevance evaluation.
     * Uses language model to score document relevance to the search query.
     *
     * @param documents search results to rank
     * @param query original search query
     * @return ranked documents sorted by relevance score (highest first)
     */
    public List<DocumentMetadata> rankByLlmRelevance(
            List<DocumentMetadata> documents,
            String query) {
        try {
            return documents.stream()
                    .map(doc -> {
                        double score = calculateRelevanceScore(doc, query);
                        doc.setRelevanceScore(score);
                        return doc;
                    })
                    .sorted(Comparator.comparingDouble(DocumentMetadata::getRelevanceScore).reversed())
                    .toList();
        } catch (Exception e) {
            e.printStackTrace();
            return documents;
        }
    }

    /**
     * Calculates relevance score for a document given a search query.
     * Combines multiple scoring factors:
     * - Keyword matching (exact matches get higher scores)
     * - Text similarity (semantic closeness to query)
     * - Document metadata quality (completeness, structure)
     * - Recency (recently updated documents ranked higher)
     *
     * @param document the document to score
     * @param query the search query
     * @return relevance score between 0.0 and 1.0
     */
    private double calculateRelevanceScore(DocumentMetadata document, String query) {
        try {
            double keywordScore = calculateKeywordScore(document, query);
            double textSimilarityScore = calculateTextSimilarityScore(document, query);
            double recencyScore = calculateRecencyScore(document);

            // Weighted combination: keywords 40%, similarity 50%, recency 10%
            return (keywordScore * 0.4) + (textSimilarityScore * 0.5) + (recencyScore * 0.1);
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }
    }

    /**
     * Calculates keyword matching score.
     * Higher score if query keywords appear in title, fileName, or keywords field.
     *
     * @param document the document to evaluate
     * @param query the search query
     * @return score between 0.0 and 1.0
     */
    private double calculateKeywordScore(DocumentMetadata document, String query) {
        String queryLower = query.toLowerCase();
        double score = 0.0;

        // Title match (highest weight)
        if (document.getTitle() != null &&
                document.getTitle().toLowerCase().contains(queryLower)) {
            score += 0.6;
        }

        // File name match
        if (document.getFileName() != null &&
                document.getFileName().toLowerCase().contains(queryLower)) {
            score += 0.3;
        }

        // Keywords match
        if (document.getKeywords() != null) {
            for (String keyword : document.getKeywords()) {
                if (keyword.toLowerCase().contains(queryLower)) {
                    score += 0.1;
                    break;
                }
            }
        }

        return Math.min(score, 1.0);
    }

    /**
     * Calculates semantic text similarity score using LLM.
     * Evaluates how well the document content matches the search intent.
     *
     * @param document the document to evaluate
     * @param query the search query
     * @return score between 0.0 and 1.0
     */
    private double calculateTextSimilarityScore(DocumentMetadata document, String query) {
        try {
            if (document.getExtractedText() == null || document.getExtractedText().isEmpty()) {
                return 0.0;
            }

            // Limit extracted text to first 500 characters for efficiency
            String textSample = document.getExtractedText().substring(
                    0,
                    Math.min(500, document.getExtractedText().length())
            );

            // Use LLM to evaluate semantic similarity
            String similarityPrompt = String.format(
                    "Rate the semantic similarity between this query and document on scale 0-1. " +
                    "Query: '%s' Document excerpt: '%s'. Respond with only a decimal number.",
                    query,
                    textSample
            );

            String response = llmProcessingService.processWithLLM(similarityPrompt);

            try {
                return Double.parseDouble(response.trim());
            } catch (NumberFormatException e) {
                // If LLM response is not a valid number, fallback to simple matching
                return calculateSimpleTextSimilarity(document.getExtractedText(), query);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return calculateSimpleTextSimilarity(
                    document.getExtractedText() != null ? document.getExtractedText() : "",
                    query
            );
        }
    }

    /**
     * Simple text similarity fallback without LLM.
     * Counts keyword occurrences and calculates a normalized score.
     *
     * @param text the document text
     * @param query the search query
     * @return score between 0.0 and 1.0
     */
    private double calculateSimpleTextSimilarity(String text, String query) {
        if (text == null || text.isEmpty()) {
            return 0.0;
        }

        String textLower = text.toLowerCase();
        String[] queryTerms = query.toLowerCase().split("\\s+");

        int matchCount = 0;
        for (String term : queryTerms) {
            if (!term.isEmpty() && textLower.contains(term)) {
                matchCount++;
            }
        }

        return Math.min((double) matchCount / queryTerms.length, 1.0);
    }

    /**
     * Calculates recency score based on document's last update time.
     * More recently updated documents get higher scores.
     *
     * @param document the document to evaluate
     * @return score between 0.0 and 1.0
     */
    private double calculateRecencyScore(DocumentMetadata document) {
        try {
            Object updatedAtObj = document.getUpdatedAtLocalDateTime();

            if (updatedAtObj == null) {
                return 0.5; // Default score if timestamp unavailable
            }

            java.time.LocalDate updatedDate = null;
            if (updatedAtObj instanceof java.time.OffsetDateTime) {
                updatedDate = ((java.time.OffsetDateTime) updatedAtObj).toLocalDate();
            } else if (updatedAtObj instanceof String) {
                String dateStr = (String) updatedAtObj;
                try {
                    if (dateStr.length() > 10) {
                        // Assume ISO-like format with time
                        updatedDate = java.time.OffsetDateTime.parse(dateStr).toLocalDate();
                    } else {
                        // Assume Date only
                        updatedDate = java.time.LocalDate.parse(dateStr);
                    }
                } catch (Exception e) {
                    // Fallback attempt
                    try { updatedDate = java.time.LocalDate.parse(dateStr.substring(0, 10)); } catch (Exception ex) {}
                }
            }

            if (updatedDate == null) {
                return 0.5;
            }

            long ageInDays = java.time.temporal.ChronoUnit.DAYS.between(
                    updatedDate,
                    java.time.LocalDate.now()
            );

            // Exponential decay: score decreases with age
            // Documents 30 days old get score ~0.37, 90 days old get ~0.05
            return Math.exp(-ageInDays / 30.0);
        } catch (Exception e) {
            e.printStackTrace();
            return 0.5;
        }
    }

    /**
     * Filters and ranks results by relevance threshold.
     * Removes low-relevance results and sorts by score.
     *
     * @param documents documents to filter and rank
     * @param query search query
     * @param relevanceThreshold minimum relevance score (0.0 to 1.0)
     * @return filtered and ranked documents
     */
    public List<DocumentMetadata> rankByRelevanceWithThreshold(
            List<DocumentMetadata> documents,
            String query,
            double relevanceThreshold) {
        return rankByLlmRelevance(documents, query)
                .stream()
                .filter(doc -> doc.getRelevanceScore() >= relevanceThreshold)
                .toList();
    }
}
