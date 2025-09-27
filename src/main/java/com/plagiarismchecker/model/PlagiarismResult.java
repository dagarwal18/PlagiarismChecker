// src/main/java/com/plagiarismchecker/model/PlagiarismResult.java
package com.plagiarismchecker.model;

import java.util.List;

public class PlagiarismResult {
    private String filename;
    private double similarityPercentage;
    private List<Match> matches;
    private String summary;
    
    // Constructors
    public PlagiarismResult() {}
    
    public PlagiarismResult(String filename, double similarityPercentage, 
                           List<Match> matches, String summary) {
        this.filename = filename;
        this.similarityPercentage = similarityPercentage;
        this.matches = matches;
        this.summary = summary;
    }
    
    // Getters and Setters
    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }
    
    public double getSimilarityPercentage() { return similarityPercentage; }
    public void setSimilarityPercentage(double similarityPercentage) { 
        this.similarityPercentage = similarityPercentage; 
    }
    
    public List<Match> getMatches() { return matches; }
    public void setMatches(List<Match> matches) { this.matches = matches; }
    
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    
    // Inner Match class
    public static class Match {
        private String text;
        private int startPosition;
        private int endPosition;
        private String algorithm;
        
        public Match() {}
        
        public Match(String text, int startPosition, int endPosition, String algorithm) {
            this.text = text;
            this.startPosition = startPosition;
            this.endPosition = endPosition;
            this.algorithm = algorithm;
        }
        
        // Getters and Setters
        public String getText() { return text; }
        public void setText(String text) { this.text = text; }
        
        public int getStartPosition() { return startPosition; }
        public void setStartPosition(int startPosition) { this.startPosition = startPosition; }
        
        public int getEndPosition() { return endPosition; }
        public void setEndPosition(int endPosition) { this.endPosition = endPosition; }
        
        public String getAlgorithm() { return algorithm; }
        public void setAlgorithm(String algorithm) { this.algorithm = algorithm; }
    }
}
