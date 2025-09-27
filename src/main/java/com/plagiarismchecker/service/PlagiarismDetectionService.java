// src/main/java/com/plagiarismchecker/service/PlagiarismDetectionService.java
package com.plagiarismchecker.service;

import com.plagiarismchecker.service.RabinKarpService;
import com.plagiarismchecker.service.KMPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PlagiarismDetectionService {
    
    @Autowired
    private RabinKarpService rabinKarpService;
    
    @Autowired
    private KMPService kmpService;
    
    // Calibrated parameters for accurate results
    private static final int MIN_SIGNIFICANT_WORD_LENGTH = 4;
    private static final int MIN_PHRASE_LENGTH = 8; // characters
    private static final double PHRASE_WEIGHT = 3.0;
    private static final double WORD_WEIGHT = 1.0;
    private static final double SEQUENCE_BONUS = 0.15;
    
    /**
     * PRODUCTION-QUALITY: Advanced plagiarism detection with multiple strategies
     */
    public PlagiarismResult detectPlagiarism(String sourceText, String comparisonText) {
        try {
            // Input validation
            if (sourceText == null || comparisonText == null || 
                sourceText.trim().isEmpty() || comparisonText.trim().isEmpty()) {
                return createResult("Analysis Complete", 0.0, new ArrayList<>(), 
                                  "Error: Empty or null text provided");
            }
            
            // Advanced text preprocessing
            String cleanSource = advancedPreprocess(sourceText);
            String cleanComparison = advancedPreprocess(comparisonText);
            
            System.out.println("DEBUG: Processing - Source: " + cleanSource.split("\\s+").length + 
                             " words, Comparison: " + cleanComparison.split("\\s+").length + " words");
            
            // Multi-layered detection strategy
            List<DetectionResult> allResults = new ArrayList<>();
            
            // Layer 1: Exact sentence/clause matching (highest weight)
            allResults.addAll(findSentenceMatches(cleanSource, cleanComparison));
            
            // Layer 2: Long phrase matching (6+ words)
            allResults.addAll(findLongPhrases(cleanSource, cleanComparison));
            
            // Layer 3: Medium phrase matching (3-5 words)
            allResults.addAll(findMediumPhrases(cleanSource, cleanComparison));
            
            // Layer 4: Significant word matching
            allResults.addAll(findSignificantWords(cleanSource, cleanComparison));
            
            // Layer 5: Semantic similarity (rare/technical terms)
            allResults.addAll(findSemanticMatches(cleanSource, cleanComparison));
            
            // Advanced duplicate removal and consolidation
            List<DetectionResult> processedResults = consolidateMatches(allResults);
            
            // Multi-factor similarity calculation
            double similarity = calculateAdvancedSimilarity(processedResults, cleanSource, cleanComparison);
            
            // Intelligent summary generation
            String summary = generateIntelligentSummary(similarity, processedResults, cleanSource, cleanComparison);
            
            System.out.println("DEBUG: Final results - " + processedResults.size() + 
                             " matches, " + String.format("%.1f", similarity) + "% similarity");
            
            return createResult("Analysis Complete", similarity, processedResults, summary);
            
        } catch (Exception e) {
            System.err.println("ERROR in detectPlagiarism: " + e.getMessage());
            e.printStackTrace();
            return createResult("Error", 0.0, new ArrayList<>(), 
                              "Error processing texts: " + e.getMessage());
        }
    }
    
    /**
     * Find exact sentence/clause matches (most significant)
     */
    private List<DetectionResult> findSentenceMatches(String source, String comparison) {
        List<DetectionResult> results = new ArrayList<>();
        
        try {
            // Split into sentences/clauses
            String[] sentences = source.split("[.!?;]+");
            
            for (String sentence : sentences) {
                sentence = sentence.trim();
                if (sentence.length() > 20) { // Minimum sentence length
                    List<KMPService.Match> matches = kmpService.findExactMatches(comparison, sentence);
                    for (KMPService.Match match : matches) {
                        if (match != null) {
                            results.add(new DetectionResult(
                                sentence,
                                match.getStartPosition(),
                                match.getEndPosition(),
                                "KMP-Sentence",
                                95.0 + Math.min(sentence.length() / 10.0, 5.0), // Very high confidence
                                String.format("Complete sentence match (%d chars)", sentence.length())
                            ));
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("ERROR in findSentenceMatches: " + e.getMessage());
        }
        
        return results;
    }
    
    /**
     * Find long phrase matches (6+ words)
     */
    private List<DetectionResult> findLongPhrases(String source, String comparison) {
        List<DetectionResult> results = new ArrayList<>();
        
        try {
            String[] words = source.split("\\s+");
            
            // Extract 6-word phrases
            for (int i = 0; i <= words.length - 6; i++) {
                StringBuilder phraseBuilder = new StringBuilder();
                for (int j = i; j < i + 6; j++) {
                    if (j > i) phraseBuilder.append(" ");
                    phraseBuilder.append(words[j]);
                }
                
                String phrase = phraseBuilder.toString();
                if (phrase.length() >= MIN_PHRASE_LENGTH && hasSignificantContent(phrase)) {
                    List<KMPService.Match> matches = kmpService.findExactMatches(comparison, phrase);
                    for (KMPService.Match match : matches) {
                        if (match != null) {
                            results.add(new DetectionResult(
                                phrase,
                                match.getStartPosition(),
                                match.getEndPosition(),
                                "KMP-LongPhrase",
                                85.0 + (phrase.length() / 20.0), // High confidence
                                "Long phrase match (6 words)"
                            ));
                        }
                    }
                }
            }
            
            // Also try 8-word phrases for even higher accuracy
            for (int i = 0; i <= words.length - 8; i++) {
                StringBuilder phraseBuilder = new StringBuilder();
                for (int j = i; j < i + 8; j++) {
                    if (j > i) phraseBuilder.append(" ");
                    phraseBuilder.append(words[j]);
                }
                
                String phrase = phraseBuilder.toString();
                if (hasSignificantContent(phrase)) {
                    List<KMPService.Match> matches = kmpService.findExactMatches(comparison, phrase);
                    for (KMPService.Match match : matches) {
                        if (match != null) {
                            results.add(new DetectionResult(
                                phrase,
                                match.getStartPosition(),
                                match.getEndPosition(),
                                "KMP-VeryLongPhrase",
                                90.0 + (phrase.length() / 15.0), // Very high confidence
                                "Very long phrase match (8 words)"
                            ));
                        }
                    }
                }
            }
            
        } catch (Exception e) {
            System.err.println("ERROR in findLongPhrases: " + e.getMessage());
        }
        
        return results;
    }
    
    /**
     * Find medium phrase matches (3-5 words)
     */
    private List<DetectionResult> findMediumPhrases(String source, String comparison) {
        List<DetectionResult> results = new ArrayList<>();
        
        try {
            String[] words = source.split("\\s+");
            
            // 4-word phrases
            for (int i = 0; i <= words.length - 4; i++) {
                String phrase = String.join(" ", Arrays.copyOfRange(words, i, i + 4));
                
                if (phrase.length() >= MIN_PHRASE_LENGTH && hasSignificantContent(phrase) && 
                    !isCommonPhrase(phrase)) {
                    
                    List<RabinKarpService.Match> matches = rabinKarpService.findMatches(comparison, phrase, 4);
                    for (RabinKarpService.Match match : matches) {
                        if (match != null) {
                            results.add(new DetectionResult(
                                phrase,
                                match.getStartPosition(),
                                match.getEndPosition(),
                                "RabinKarp-MediumPhrase",
                                70.0 + (phrase.length() / 25.0),
                                "Medium phrase match (4 words)"
                            ));
                        }
                    }
                }
            }
            
            // 3-word phrases (more selective)
            for (int i = 0; i <= words.length - 3; i++) {
                String phrase = String.join(" ", Arrays.copyOfRange(words, i, i + 3));
                
                if (phrase.length() >= 12 && hasSignificantContent(phrase) && 
                    !isCommonPhrase(phrase) && containsRareWords(phrase)) {
                    
                    List<RabinKarpService.Match> matches = rabinKarpService.findMatches(comparison, phrase, 3);
                    for (RabinKarpService.Match match : matches) {
                        if (match != null) {
                            results.add(new DetectionResult(
                                phrase,
                                match.getStartPosition(),
                                match.getEndPosition(),
                                "RabinKarp-ShortPhrase",
                                60.0,
                                "Short phrase match (3 words)"
                            ));
                        }
                    }
                }
            }
            
        } catch (Exception e) {
            System.err.println("ERROR in findMediumPhrases: " + e.getMessage());
        }
        
        return results;
    }
    
    /**
     * Find significant word matches
     */
    private List<DetectionResult> findSignificantWords(String source, String comparison) {
        List<DetectionResult> results = new ArrayList<>();
        
        try {
            // Extract significant words
            Set<String> sourceWords = extractSignificantWords(source);
            Set<String> compWords = extractSignificantWords(comparison);
            
            // Find common significant words
            Set<String> commonWords = new HashSet<>(sourceWords);
            commonWords.retainAll(compWords);
            
            // Limit to most significant matches
            List<String> sortedCommonWords = commonWords.stream()
                .sorted((a, b) -> Integer.compare(b.length(), a.length())) // Longer words first
                .limit(12)
                .collect(Collectors.toList());
            
            for (String word : sortedCommonWords) {
                List<KMPService.Match> matches = kmpService.findExactMatches(comparison, word);
                for (KMPService.Match match : matches) {
                    if (match != null) {
                        double confidence = 40.0 + (word.length() * 3) + (isRareWord(word) ? 15 : 0);
                        results.add(new DetectionResult(
                            word,
                            match.getStartPosition(),
                            match.getEndPosition(),
                            "KMP-Word",
                            confidence,
                            "Significant word match" + (isRareWord(word) ? " (rare/technical)" : "")
                        ));
                    }
                }
            }
            
        } catch (Exception e) {
            System.err.println("ERROR in findSignificantWords: " + e.getMessage());
        }
        
        return results;
    }
    
    /**
     * Find semantic matches (technical terms, proper nouns, etc.)
     */
    private List<DetectionResult> findSemanticMatches(String source, String comparison) {
        List<DetectionResult> results = new ArrayList<>();
        
        try {
            // Find technical/rare terms
            Set<String> technicalTerms = extractTechnicalTerms(source);
            
            for (String term : technicalTerms) {
                List<KMPService.Match> matches = kmpService.findExactMatches(comparison, term);
                for (KMPService.Match match : matches) {
                    if (match != null) {
                        results.add(new DetectionResult(
                            term,
                            match.getStartPosition(),
                            match.getEndPosition(),
                            "KMP-Technical",
                            75.0 + (term.length() * 2),
                            "Technical/specialized term match"
                        ));
                    }
                }
            }
            
        } catch (Exception e) {
            System.err.println("ERROR in findSemanticMatches: " + e.getMessage());
        }
        
        return results;
    }
    
    /**
     * Advanced match consolidation - removes overlaps and duplicates intelligently
     */
    private List<DetectionResult> consolidateMatches(List<DetectionResult> matches) {
        if (matches.isEmpty()) return matches;
        
        // Sort by confidence (highest first), then by length (longest first)
        matches.sort((a, b) -> {
            int confCompare = Double.compare(b.getConfidenceScore(), a.getConfidenceScore());
            if (confCompare != 0) return confCompare;
            return Integer.compare(b.getMatchedText().length(), a.getMatchedText().length());
        });
        
        List<DetectionResult> consolidated = new ArrayList<>();
        Set<String> usedTexts = new HashSet<>();
        
        for (DetectionResult match : matches) {
            if (match == null || match.getMatchedText() == null) continue;
            
            String matchText = match.getMatchedText().toLowerCase().trim();
            
            // Skip if we've seen this exact match
            if (usedTexts.contains(matchText)) continue;
            
            // Check for overlaps with existing matches
            boolean hasOverlap = false;
            for (DetectionResult existing : consolidated) {
                if (hasSignificantTextOverlap(match, existing)) {
                    hasOverlap = true;
                    break;
                }
            }
            
            if (!hasOverlap) {
                consolidated.add(match);
                usedTexts.add(matchText);
            }
        }
        
        // Limit to top 20 matches to avoid information overload
        return consolidated.stream().limit(20).collect(Collectors.toList());
    }
    
    /**
     * Multi-factor similarity calculation with intelligent weighting
     */
    private double calculateAdvancedSimilarity(List<DetectionResult> matches, String source, String comparison) {
        if (matches.isEmpty()) return 0.0;
        
        try {
            String[] sourceWords = source.split("\\s+");
            String[] compWords = comparison.split("\\s+");
            
            if (sourceWords.length == 0 || compWords.length == 0) return 0.0;
            
            // Factor 1: Weighted match scoring
            double totalWeightedScore = 0.0;
            int totalMatchedChars = 0;
            
            for (DetectionResult match : matches) {
                String text = match.getMatchedText();
                int wordCount = text.split("\\s+").length;
                double weight = getMatchWeight(wordCount, match.getAlgorithm());
                double normalizedConf = Math.min(match.getConfidenceScore(), 100.0) / 100.0;
                
                totalWeightedScore += text.length() * weight * normalizedConf;
                totalMatchedChars += text.length();
            }
            
            // Factor 2: Coverage analysis
            int avgTextLength = (source.length() + comparison.length()) / 2;
            double coverageRatio = (double) totalMatchedChars / avgTextLength;
            
            // Factor 3: Match quality distribution
            long highQualityMatches = matches.stream()
                .filter(m -> m.getConfidenceScore() >= 80)
                .count();
            long phraseMatches = matches.stream()
                .filter(m -> m.getMatchedText().split("\\s+").length >= 3)
                .count();
            
            // Factor 4: Sequential matching bonus
            double sequentialBonus = calculateSequentialBonus(matches);
            
            // Combine factors with intelligent weights
            double baseSimilarity = (totalWeightedScore / avgTextLength) * 100;
            double coverageBonus = coverageRatio * 25;
            double qualityBonus = (highQualityMatches * 5) + (phraseMatches * 3);
            double seqBonus = sequentialBonus * 20;
            
            double finalScore = baseSimilarity + coverageBonus + qualityBonus + seqBonus;
            
            // Apply intelligent scaling based on text characteristics
            finalScore = applyIntelligentScaling(finalScore, sourceWords.length, compWords.length, matches);
            
            return Math.min(100.0, Math.max(0.0, finalScore));
            
        } catch (Exception e) {
            System.err.println("ERROR in calculateAdvancedSimilarity: " + e.getMessage());
            return 0.0;
        }
    }
    
    /**
     * Detect if content is mixed (some identical, some paraphrased)
     */
    private boolean isMixedContent(List<DetectionResult> matches) {
        if (matches.size() < 3) return false;
        
        long exactMatches = matches.stream()
            .filter(m -> m.getAlgorithm().contains("Sentence") || m.getAlgorithm().contains("VeryLongPhrase"))
            .count();
        
        long wordMatches = matches.stream()
            .filter(m -> m.getAlgorithm().contains("Word") || m.getAlgorithm().contains("Technical"))
            .count();
        
        // Mixed content = has both exact matches AND many word-only matches
        return exactMatches > 0 && wordMatches >= matches.size() / 2;
    }
    
    /**
     * SMART: Apply scaling based on content type detection
     */
    private double applyIntelligentScaling(double score, int sourceWords, int compWords, List<DetectionResult> matches) {
        // Detect content type
        boolean isMixed = isMixedContent(matches);
        
        if (isMixed) {
            System.out.println("DEBUG: Mixed content detected - applying penalty");
            // Strong penalty for mixed content (partial copying + paraphrasing)
            score *= 0.6; // 40% reduction for mixed content
        } else {
            // Normal scaling for consistent content (all copied OR all paraphrased)
            long veryHighConfMatches = matches.stream()
                .filter(m -> m.getConfidenceScore() >= 90)
                .count();
            
            if (veryHighConfMatches >= 3) {
                score *= 1.1; // 10% boost for high confidence
            }
            
            long longPhrases = matches.stream()
                .filter(m -> m.getMatchedText().split("\\s+").length >= 6)
                .count();
            
            if (longPhrases >= 2) {
                score *= 1.05; // 5% boost for multiple phrases
            }
        }
        
        // Minor adjustment for text length
        int minWords = Math.min(sourceWords, compWords);
        if (minWords < 50) {
            score *= 0.98; // Small reduction for very short texts
        }
        
        return score;
    }
    
    /**
     * Get match weight based on type and quality
     */
    private double getMatchWeight(int wordCount, String algorithm) {
        if (algorithm.contains("Sentence")) return 4.0; // Highest weight
        if (algorithm.contains("VeryLongPhrase")) return 3.5;
        if (algorithm.contains("LongPhrase")) return 3.0;
        if (algorithm.contains("Technical")) return 2.5;
        if (algorithm.contains("MediumPhrase")) return 2.0;
        if (algorithm.contains("ShortPhrase")) return 1.5;
        return 1.0; // Single words
    }
    
    /**
     * Calculate sequential matching bonus
     */
    private double calculateSequentialBonus(List<DetectionResult> matches) {
        if (matches.size() < 2) return 0.0;
        
        // Sort by position
        List<DetectionResult> sortedMatches = matches.stream()
            .sorted(Comparator.comparing(DetectionResult::getStartPosition))
            .collect(Collectors.toList());
        
        int sequentialPairs = 0;
        for (int i = 1; i < sortedMatches.size(); i++) {
            int gap = sortedMatches.get(i).getStartPosition() - sortedMatches.get(i-1).getEndPosition();
            if (gap >= 0 && gap <= 50) { // Within 50 characters = sequential
                sequentialPairs++;
            }
        }
        
        return Math.min(0.3, sequentialPairs * 0.05);
    }
    
    /**
     * Generate intelligent summary with detailed analysis
     */
    private String generateIntelligentSummary(double similarity, List<DetectionResult> matches, 
                                            String source, String comparison) {
        try {
            int sourceWords = source.split("\\s+").length;
            int compWords = comparison.split("\\s+").length;
            
            long phraseMatches = matches.stream()
                .filter(m -> m.getMatchedText().split("\\s+").length >= 3)
                .count();
            long highConfMatches = matches.stream()
                .filter(m -> m.getConfidenceScore() >= 80)
                .count();
            
            StringBuilder summary = new StringBuilder();
            
            // Similarity assessment
            if (similarity < 15) {
                summary.append(String.format("Low similarity (%.1f%%). ", similarity));
                summary.append("Documents appear largely original. ");
            } else if (similarity < 35) {
                summary.append(String.format("Moderate similarity (%.1f%%). ", similarity));
                summary.append("Some common content detected. ");
            } else if (similarity < 60) {
                summary.append(String.format("High similarity (%.1f%%). ", similarity));
                summary.append("Significant overlap detected. ");
            } else if (similarity < 80) {
                summary.append(String.format("Very high similarity (%.1f%%). ", similarity));
                summary.append("Substantial overlap found. ");
            } else {
                summary.append(String.format("Extremely high similarity (%.1f%%). ", similarity));
                summary.append("Extensive overlap detected. ");
            }
            
            // Match details
            summary.append(String.format("Analysis found %d matches", matches.size()));
            if (phraseMatches > 0) {
                summary.append(String.format(" (%d phrase matches)", phraseMatches));
            }
            if (highConfMatches > 0) {
                summary.append(String.format(", %d high-confidence", highConfMatches));
            }
            summary.append(String.format(" across %d and %d words. ", sourceWords, compWords));
            
            // Recommendation
            if (similarity >= 60) {
                summary.append("Likely plagiarism - detailed review required.");
            } else if (similarity >= 35) {
                summary.append("Manual review recommended.");
            } else if (similarity >= 15) {
                summary.append("Minor similarities detected - likely acceptable overlap.");
            } else {
                summary.append("Documents appear original.");
            }
            
            return summary.toString();
            
        } catch (Exception e) {
            return String.format("Analysis complete with %.1f%% similarity.", similarity);
        }
    }
    
    // Helper methods
    private String advancedPreprocess(String text) {
        return text.toLowerCase()
                  .replaceAll("\\s*[\\r\\n]+\\s*", " ")
                  .replaceAll("[^a-zA-Z0-9\\s'-]", " ")
                  .replaceAll("\\s+", " ")
                  .trim();
    }
    
    private boolean hasSignificantContent(String text) {
        String[] words = text.split("\\s+");
        int significantWords = 0;
        for (String word : words) {
            if (word.length() >= MIN_SIGNIFICANT_WORD_LENGTH && !isStopWord(word)) {
                significantWords++;
            }
        }
        return significantWords >= Math.max(1, words.length / 3);
    }
    
    private boolean isCommonPhrase(String phrase) {
        String lower = phrase.toLowerCase();
        Set<String> commonPhrases = Set.of(
            "in order to", "as well as", "such as", "according to", "for example",
            "in addition to", "as a result", "on the other hand", "in conclusion",
            "at the same time", "in other words", "due to the fact"
        );
        return commonPhrases.contains(lower);
    }
    
    private boolean containsRareWords(String phrase) {
        return Arrays.stream(phrase.split("\\s+"))
            .anyMatch(this::isRareWord);
    }
    
    private boolean isRareWord(String word) {
        return word.length() >= 6 && !isStopWord(word) && 
               (word.contains("tion") || word.contains("ment") || word.contains("ity") ||
                word.contains("ness") || word.matches(".*[A-Z].*"));
    }
    
    private Set<String> extractSignificantWords(String text) {
        return Arrays.stream(text.split("\\s+"))
            .filter(word -> word.length() >= MIN_SIGNIFICANT_WORD_LENGTH && !isStopWord(word))
            .collect(Collectors.toSet());
    }
    
    private Set<String> extractTechnicalTerms(String text) {
        return Arrays.stream(text.split("\\s+"))
            .filter(word -> word.length() >= 6 && !isStopWord(word))
            .filter(this::isRareWord)
            .limit(10)
            .collect(Collectors.toSet());
    }
    
    private boolean hasSignificantTextOverlap(DetectionResult a, DetectionResult b) {
        String textA = a.getMatchedText().toLowerCase();
        String textB = b.getMatchedText().toLowerCase();
        
        if (textA.equals(textB)) return true;
        if (textA.contains(textB) || textB.contains(textA)) return true;
        
        Set<String> wordsA = new HashSet<>(Arrays.asList(textA.split("\\s+")));
        Set<String> wordsB = new HashSet<>(Arrays.asList(textB.split("\\s+")));
        Set<String> intersection = new HashSet<>(wordsA);
        intersection.retainAll(wordsB);
        
        return intersection.size() >= Math.min(wordsA.size(), wordsB.size()) * 0.7;
    }
    
    private boolean isStopWord(String word) {
        Set<String> stopWords = Set.of(
            "the", "and", "or", "but", "in", "on", "at", "to", "for", "of", "with", "by",
            "from", "up", "about", "into", "through", "is", "are", "was", "were", "be",
            "been", "being", "have", "has", "had", "do", "does", "did", "will", "would",
            "could", "should", "may", "might", "must", "can", "this", "that", "these",
            "those", "a", "an", "also", "just", "now", "then", "here", "there", "when",
            "where", "why", "how", "all", "any", "both", "each", "more", "most", "other",
            "some", "such", "only", "own", "same", "so", "than", "too", "very"
        );
        return stopWords.contains(word.toLowerCase());
    }
    
    private PlagiarismResult createResult(String status, double similarity, 
                                        List<DetectionResult> matches, String summary) {
        return new PlagiarismResult(status, similarity, matches, summary);
    }
    
    // Result classes
    public static class PlagiarismResult {
        private final String status;
        private final double similarityPercentage;
        private final List<DetectionResult> matches;
        private final String summary;
        
        public PlagiarismResult(String status, double similarityPercentage, 
                               List<DetectionResult> matches, String summary) {
            this.status = status != null ? status : "Unknown";
            this.similarityPercentage = Double.isNaN(similarityPercentage) ? 0.0 : 
                                      Math.max(0.0, Math.min(100.0, similarityPercentage));
            this.matches = matches != null ? matches : new ArrayList<>();
            this.summary = summary != null ? summary : "No summary available";
        }
        
        public String getStatus() { return status; }
        public double getSimilarityPercentage() { return similarityPercentage; }
        public List<DetectionResult> getMatches() { return matches; }
        public String getSummary() { return summary; }
    }
    
    public static class DetectionResult {
        private final String matchedText;
        private final int startPosition;
        private final int endPosition;
        private final String algorithm;
        private final double confidenceScore;
        private final String description;
        
        public DetectionResult(String matchedText, int startPosition, int endPosition,
                              String algorithm, double confidenceScore, String description) {
            this.matchedText = matchedText != null ? matchedText : "";
            this.startPosition = Math.max(0, startPosition);
            this.endPosition = Math.max(0, endPosition);
            this.algorithm = algorithm != null ? algorithm : "Unknown";
            this.confidenceScore = Double.isNaN(confidenceScore) ? 0.0 : 
                                 Math.max(0.0, Math.min(100.0, confidenceScore));
            this.description = description != null ? description : "";
        }
        
        public String getMatchedText() { return matchedText; }
        public int getStartPosition() { return startPosition; }
        public int getEndPosition() { return endPosition; }
        public String getAlgorithm() { return algorithm; }
        public double getConfidenceScore() { return confidenceScore; }
        public String getDescription() { return description; }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof DetectionResult)) return false;
            DetectionResult that = (DetectionResult) o;
            return Objects.equals(matchedText, that.matchedText) &&
                   startPosition == that.startPosition;
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(matchedText, startPosition);
        }
    }
}
