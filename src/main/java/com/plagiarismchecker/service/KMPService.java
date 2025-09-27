// src/main/java/com/plagiarismchecker/service/KMPService.java
package com.plagiarismchecker.service;

import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class KMPService {
    
    /**
     * KMP algorithm for precise single pattern matching
     * Optimal O(n+m) complexity with no backtracking
     */
    public List<Match> findExactMatches(String text, String pattern) {
        List<Match> matches = new ArrayList<>();
        
        if (pattern == null || text == null || pattern.isEmpty() || text.isEmpty()) {
            return matches;
        }
        
        // Convert to lowercase for case-insensitive matching
        text = text.toLowerCase();
        pattern = pattern.toLowerCase();
        
        int[] lps = computeLPSArray(pattern);
        List<Integer> positions = kmpSearch(text, pattern, lps);
        
        for (int pos : positions) {
            matches.add(new Match(
                pattern,
                pos,
                pos + pattern.length(),
                "KMP",
                calculatePrecisionScore(pattern.length())
            ));
        }
        
        return matches;
    }
    
    /**
     * Core KMP search implementation
     * Uses LPS array to avoid unnecessary comparisons
     */
    private List<Integer> kmpSearch(String text, String pattern, int[] lps) {
        List<Integer> matches = new ArrayList<>();
        int textLength = text.length();
        int patternLength = pattern.length();
        
        int i = 0; // Index for text
        int j = 0; // Index for pattern
        
        while (i < textLength) {
            if (pattern.charAt(j) == text.charAt(i)) {
                i++;
                j++;
            }
            
            if (j == patternLength) {
                // Pattern found
                matches.add(i - j);
                j = lps[j - 1];
            } else if (i < textLength && pattern.charAt(j) != text.charAt(i)) {
                // Mismatch: use LPS array to skip characters
                if (j != 0) {
                    j = lps[j - 1];
                } else {
                    i++;
                }
            }
        }
        
        return matches;
    }
    
    /**
     * Compute Longest Proper Prefix which is also Suffix (LPS) array
     * This is the key to KMP's efficiency!
     */
    private int[] computeLPSArray(String pattern) {
        int patternLength = pattern.length();
        int[] lps = new int[patternLength];
        int length = 0; // Length of previous longest prefix suffix
        int i = 1;
        
        lps[0] = 0; // First element is always 0
        
        // Calculate lps[i] for i = 1 to patternLength-1
        while (i < patternLength) {
            if (pattern.charAt(i) == pattern.charAt(length)) {
                length++;
                lps[i] = length;
                i++;
            } else {
                if (length != 0) {
                    // Consider the previous longest prefix suffix
                    length = lps[length - 1];
                } else {
                    lps[i] = 0;
                    i++;
                }
            }
        }
        
        return lps;
    }
    
    /**
     * Calculate precision score for KMP matches
     */
    private double calculatePrecisionScore(int patternLength) {
        // KMP provides exact matches, so high precision score
        return Math.min(100.0, patternLength * 2.0);
    }
    
    // Match result class
    public static class Match {
        private String matchedText;
        private int startPosition;
        private int endPosition;
        private String algorithm;
        private double precisionScore;
        
        public Match(String matchedText, int startPosition, int endPosition, 
                    String algorithm, double precisionScore) {
            this.matchedText = matchedText;
            this.startPosition = startPosition;
            this.endPosition = endPosition;
            this.algorithm = algorithm;
            this.precisionScore = precisionScore;
        }
        
        // Getters
        public String getMatchedText() { return matchedText; }
        public int getStartPosition() { return startPosition; }
        public int getEndPosition() { return endPosition; }
        public String getAlgorithm() { return algorithm; }
        public double getPrecisionScore() { return precisionScore; }
    }
}
