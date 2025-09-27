// src/main/java/com/plagiarismchecker/service/RabinKarpService.java
package com.plagiarismchecker.service;

import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class RabinKarpService {
    
    private static final int PRIME = 101; // Prime number for hashing
    private static final int BASE = 256;  // Number of characters in ASCII
    
    /**
     * Rabin-Karp algorithm for multiple pattern searching
     * Perfect for plagiarism detection - finds all occurrences quickly
     */
    public List<Match> findMatches(String text, String pattern, int minLength) {
        List<Match> matches = new ArrayList<>();
        
        if (pattern == null || text == null || pattern.length() < minLength || 
            pattern.length() > text.length()) {
            return matches;
        }
        
        // Convert to lowercase for case-insensitive matching
        text = text.toLowerCase();
        pattern = pattern.toLowerCase();
        
        List<Integer> positions = rabinKarpSearch(text, pattern);
        
        for (int pos : positions) {
            matches.add(new Match(
                pattern, 
                pos, 
                pos + pattern.length(), 
                "Rabin-Karp",
                calculateSimilarityScore(pattern.length(), text.length())
            ));
        }
        
        return matches;
    }
    
    /**
     * Core Rabin-Karp search implementation
     * Uses rolling hash for efficient pattern matching
     */
    private List<Integer> rabinKarpSearch(String text, String pattern) {
        List<Integer> matches = new ArrayList<>();
        int textLength = text.length();
        int patternLength = pattern.length();
        
        if (patternLength > textLength) return matches;
        
        // Calculate hash values
        long patternHash = calculateHash(pattern, patternLength);
        long textHash = calculateHash(text, patternLength);
        
        // Check first window
        if (patternHash == textHash && verifyMatch(text, pattern, 0)) {
            matches.add(0);
        }
        
        // Rolling hash for remaining windows
        for (int i = 1; i <= textLength - patternLength; i++) {
            // Update hash using rolling hash technique
            textHash = recalculateHash(text, i - 1, i + patternLength - 1, 
                                     textHash, patternLength);
            
            // If hash matches, verify actual string match
            if (patternHash == textHash && verifyMatch(text, pattern, i)) {
                matches.add(i);
            }
        }
        
        return matches;
    }
    
    /**
     * Calculate initial hash for a string
     */
    private long calculateHash(String str, int length) {
        long hash = 0;
        for (int i = 0; i < length; i++) {
            hash = (hash * BASE + str.charAt(i)) % PRIME;
        }
        return hash;
    }
    
    /**
     * Rolling hash: Remove old character, add new character
     * This is what makes Rabin-Karp efficient!
     */
    private long recalculateHash(String str, int oldIndex, int newIndex, 
                                long oldHash, int patternLength) {
        long newHash = oldHash;
        
        // Remove the leftmost character
        newHash = (newHash - str.charAt(oldIndex) * 
                  (long)Math.pow(BASE, patternLength - 1)) % PRIME;
        
        // Add the rightmost character
        newHash = (newHash * BASE + str.charAt(newIndex)) % PRIME;
        
        // Handle negative hash values
        if (newHash < 0) {
            newHash += PRIME;
        }
        
        return newHash;
    }
    
    /**
     * Verify actual string match (in case of hash collision)
     */
    private boolean verifyMatch(String text, String pattern, int startIndex) {
        for (int i = 0; i < pattern.length(); i++) {
            if (text.charAt(startIndex + i) != pattern.charAt(i)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Calculate similarity score based on pattern length
     */
    private double calculateSimilarityScore(int patternLength, int textLength) {
        return (double) patternLength / textLength * 100;
    }
    
    // Match result class
    public static class Match {
        private String matchedText;
        private int startPosition;
        private int endPosition;
        private String algorithm;
        private double similarityScore;
        
        public Match(String matchedText, int startPosition, int endPosition, 
                    String algorithm, double similarityScore) {
            this.matchedText = matchedText;
            this.startPosition = startPosition;
            this.endPosition = endPosition;
            this.algorithm = algorithm;
            this.similarityScore = similarityScore;
        }
        
        // Getters
        public String getMatchedText() { return matchedText; }
        public int getStartPosition() { return startPosition; }
        public int getEndPosition() { return endPosition; }
        public String getAlgorithm() { return algorithm; }
        public double getSimilarityScore() { return similarityScore; }
    }
}
