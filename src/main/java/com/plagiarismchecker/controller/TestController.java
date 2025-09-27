// src/main/java/com/plagiarismchecker/controller/TestController.java
package com.plagiarismchecker.controller;

import com.plagiarismchecker.service.PlagiarismDetectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
@CrossOrigin(origins = "*") // Enable CORS for frontend
public class TestController {
    
    @Autowired
    private PlagiarismDetectionService plagiarismDetectionService;
    
    @GetMapping("/api/health")
    public String health() {
        return "✅ Backend is running perfectly!";
    }
    
    @GetMapping("/api/test")
    public TestResponse test() {
        return new TestResponse("Backend working", "Ready for plagiarism detection", true);
    }
    
    /**
     * FIXED: Main plagiarism detection endpoint with proper error handling
     */
    @PostMapping("/api/detect-plagiarism")
    public ResponseEntity<?> detectPlagiarism(@RequestBody PlagiarismRequest request) {
        try {
            // Validate input
            if (request == null) {
                return ResponseEntity.badRequest().body(createErrorResponse("Request body is required"));
            }
            
            if (request.getSourceText() == null || request.getSourceText().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("Source text is required and cannot be empty"));
            }
            
            if (request.getComparisonText() == null || request.getComparisonText().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("Comparison text is required and cannot be empty"));
            }
            
            // Process plagiarism detection
            PlagiarismDetectionService.PlagiarismResult result = plagiarismDetectionService.detectPlagiarism(
                request.getSourceText(), 
                request.getComparisonText()
            );
            
            // Create safe response object
            Map<String, Object> response = createSafeResponse(result);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            // Log the error for debugging
            System.err.println("ERROR in detectPlagiarism: " + e.getMessage());
            e.printStackTrace();
            
            // Return safe error response
            return ResponseEntity.status(500).body(createErrorResponse("Internal server error: " + e.getMessage()));
        }
    }
    
    /**
     * FIXED: Algorithm tests with proper error handling
     */
    @GetMapping("/api/test-algorithms")
    public ResponseEntity<?> testAlgorithms() {
        try {
            String sourceText = "Machine learning algorithms are powerful tools for data analysis and pattern recognition in modern computing.";
            String comparisonText = "Machine learning algorithms are powerful tools used in data analysis and pattern recognition for computing applications.";
            
            PlagiarismDetectionService.PlagiarismResult result = plagiarismDetectionService.detectPlagiarism(sourceText, comparisonText);
            return ResponseEntity.ok(createSafeResponse(result));
            
        } catch (Exception e) {
            System.err.println("ERROR in testAlgorithms: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(createErrorResponse("Test failed: " + e.getMessage()));
        }
    }
    
    @GetMapping("/api/test-algorithms-high")
    public ResponseEntity<?> testAlgorithmsHigh() {
        try {
            String sourceText = "Artificial intelligence represents a paradigm shift in computational methodologies that has fundamentally transformed how we approach complex problem-solving tasks. Machine learning algorithms utilize sophisticated statistical techniques to enable computer systems to automatically improve their performance through iterative experience and data analysis.";
            String comparisonText = "Artificial intelligence represents a paradigm shift in computational methodologies that has fundamentally transformed how we approach complex problem-solving tasks. Machine learning algorithms utilize sophisticated statistical techniques to enable computer systems to automatically improve their performance through iterative experience and data analysis.";
            
            PlagiarismDetectionService.PlagiarismResult result = plagiarismDetectionService.detectPlagiarism(sourceText, comparisonText);
            return ResponseEntity.ok(createSafeResponse(result));
            
        } catch (Exception e) {
            System.err.println("ERROR in testAlgorithmsHigh: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(createErrorResponse("High similarity test failed: " + e.getMessage()));
        }
    }
    
    @GetMapping("/api/test-algorithms-low")
    public ResponseEntity<?> testAlgorithmsLow() {
        try {
            String sourceText = "Quantum computing utilizes quantum mechanical phenomena such as superposition and entanglement to process information in fundamentally different ways than classical systems.";
            String comparisonText = "Blockchain technology represents a distributed ledger system that maintains comprehensive transaction records across multiple network nodes without central authority.";
            
            PlagiarismDetectionService.PlagiarismResult result = plagiarismDetectionService.detectPlagiarism(sourceText, comparisonText);
            return ResponseEntity.ok(createSafeResponse(result));
            
        } catch (Exception e) {
            System.err.println("ERROR in testAlgorithmsLow: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(createErrorResponse("Low similarity test failed: " + e.getMessage()));
        }
    }
    
    /**
     * Extract text from uploaded file - SIMPLIFIED VERSION
     */
    @PostMapping("/api/extract-text")
    public ResponseEntity<String> extractTextFromFile(@RequestParam("file") MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest().body("File is empty or null");
            }
            
            String text = extractTextContent(file);
            if (text == null || text.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("No readable text found in file");
            }
            
            return ResponseEntity.ok(text);
        } catch (Exception e) {
            System.err.println("ERROR in extractTextFromFile: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error extracting text: " + e.getMessage());
        }
    }
    
    /**
     * Compare two files directly
     */
    @PostMapping("/api/compare-files")
    public ResponseEntity<?> compareFiles(
            @RequestParam("sourceFile") MultipartFile sourceFile,
            @RequestParam("comparisonFile") MultipartFile comparisonFile) {
        
        try {
            if (sourceFile == null || sourceFile.isEmpty() || comparisonFile == null || comparisonFile.isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("Both files are required"));
            }
            
            String sourceText = extractTextContent(sourceFile);
            String comparisonText = extractTextContent(comparisonFile);
            
            if (sourceText == null || sourceText.trim().isEmpty() || comparisonText == null || comparisonText.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("Could not extract readable text from one or both files"));
            }
            
            PlagiarismDetectionService.PlagiarismResult result = 
                plagiarismDetectionService.detectPlagiarism(sourceText, comparisonText);
            
            return ResponseEntity.ok(createSafeResponse(result));
            
        } catch (Exception e) {
            System.err.println("ERROR in compareFiles: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(createErrorResponse("Failed to process files: " + e.getMessage()));
        }
    }
    
    /**
     * FIXED: Create response matching frontend expectations exactly
     */
    private Map<String, Object> createSafeResponse(PlagiarismDetectionService.PlagiarismResult result) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // FIXED: Handle similarity percentage safely - NO NULL CHECK ON PRIMITIVES
            double similarity = 0.0;
            if (result != null) {
                double rawSimilarity = result.getSimilarityPercentage(); // This returns double primitive
                if (!Double.isNaN(rawSimilarity) && !Double.isInfinite(rawSimilarity)) {
                    similarity = rawSimilarity;
                }
            }
            similarity = Math.max(0.0, Math.min(100.0, similarity));
            similarity = Math.round(similarity * 10.0) / 10.0; // Round to 1 decimal place
            
            // Create safe matches list - EXACT FIELD NAMES FOR FRONTEND
            List<Map<String, Object>> safeMatches = new ArrayList<>();
            if (result != null && result.getMatches() != null) {
                for (PlagiarismDetectionService.DetectionResult match : result.getMatches()) {
                    if (match != null) {
                        safeMatches.add(createSafeMatch(match));
                    }
                }
            }
            
            // EXACT RESPONSE STRUCTURE YOUR FRONTEND EXPECTS
            response.put("status", result != null && result.getStatus() != null ? 
                        result.getStatus() : "Analysis Complete");
            response.put("similarityPercentage", similarity); // CRITICAL: Always a valid number
            response.put("summary", result != null && result.getSummary() != null ? 
                        result.getSummary() : "Analysis completed successfully");
            response.put("matches", safeMatches); // Frontend expects this exact field name
            
        } catch (Exception e) {
            System.err.println("ERROR in createSafeResponse: " + e.getMessage());
            e.printStackTrace();
            // Return safe fallback response
            response.put("status", "Error");
            response.put("similarityPercentage", 0.0); // Always return a valid number
            response.put("summary", "Error processing results: " + e.getMessage());
            response.put("matches", new ArrayList<>());
        }
        
        return response;
    }
    
    /**
     * FIXED: Create safe match with exact field names
     */
    private Map<String, Object> createSafeMatch(PlagiarismDetectionService.DetectionResult match) {
        Map<String, Object> safeMatch = new HashMap<>();
        
        try {
            // FIXED: Handle confidence score safely - NO NULL CHECK ON PRIMITIVES
            double confidence = 0.0;
            if (match != null) {
                double rawConfidence = match.getConfidenceScore(); // This returns double primitive
                if (!Double.isNaN(rawConfidence) && !Double.isInfinite(rawConfidence)) {
                    confidence = rawConfidence;
                }
            }
            confidence = Math.max(0.0, Math.min(100.0, confidence));
            confidence = Math.round(confidence * 10.0) / 10.0; // Round to 1 decimal
            
            // EXACT FIELD NAMES YOUR FRONTEND EXPECTS
            safeMatch.put("matchedText", match != null && match.getMatchedText() != null ? 
                         match.getMatchedText() : "");
            safeMatch.put("startPosition", match != null ? Math.max(0, match.getStartPosition()) : 0);
            safeMatch.put("endPosition", match != null ? Math.max(0, match.getEndPosition()) : 0);
            safeMatch.put("algorithm", match != null && match.getAlgorithm() != null ? 
                         match.getAlgorithm() : "Unknown");
            safeMatch.put("confidenceScore", confidence); // CRITICAL: Always valid number
            safeMatch.put("description", match != null && match.getDescription() != null ? 
                         match.getDescription() : "");
            
        } catch (Exception e) {
            System.err.println("ERROR in createSafeMatch: " + e.getMessage());
            // Return minimal safe match with valid numbers
            safeMatch.put("matchedText", "Error processing match");
            safeMatch.put("startPosition", 0);
            safeMatch.put("endPosition", 0);
            safeMatch.put("algorithm", "Error");
            safeMatch.put("confidenceScore", 0.0); // Always return valid number
            safeMatch.put("description", "Error processing match");
        }
        
        return safeMatch;
    }
    
    /**
     * Create standardized error response
     */
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("status", "Error");
        error.put("similarityPercentage", 0.0);
        error.put("summary", message != null ? message : "Unknown error occurred");
        error.put("matches", new ArrayList<>());
        error.put("error", message != null ? message : "Unknown error occurred");
        return error;
    }
    
    /**
     * SIMPLIFIED: Text extraction from files
     */
    private String extractTextContent(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IOException("File is null or empty");
        }
        
        String filename = file.getOriginalFilename();
        if (filename == null || filename.isEmpty()) {
            throw new IOException("Invalid file - no filename provided");
        }
        
        // Check file size (10MB limit)
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new IOException("File size exceeds 10MB limit");
        }
        
        // Check if file is empty
        if (file.getSize() == 0) {
            throw new IOException("File is empty: " + filename);
        }
        
        String fileExtension = filename.toLowerCase();
        
        try {
            if (fileExtension.endsWith(".txt")) {
                return extractFromTXT(file);
            } else if (fileExtension.endsWith(".docx")) {
                return extractFromDOCX(file);
            } else if (fileExtension.endsWith(".pdf")) {
                return extractFromPDF(file);
            } else {
                throw new IOException("Unsupported file type: " + filename + 
                    ". Supported formats: .txt, .docx, .pdf");
            }
        } catch (Exception e) {
            throw new IOException("Failed to extract text from " + filename + ": " + e.getMessage());
        }
    }
    
    /**
     * Extract text from TXT file
     */
    private String extractFromTXT(MultipartFile file) throws IOException {
        try {
            byte[] bytes = file.getBytes();
            if (bytes.length == 0) {
                throw new IOException("TXT file is empty");
            }
            
            String text = new String(bytes, StandardCharsets.UTF_8);
            
            if (text.trim().isEmpty()) {
                text = new String(bytes); // Fallback to default encoding
            }
            
            return cleanText(text);
        } catch (Exception e) {
            throw new IOException("Error reading TXT file: " + e.getMessage());
        }
    }
    
    /**
     * Extract text from DOCX file using Apache POI
     */
    private String extractFromDOCX(MultipartFile file) throws IOException {
        try (InputStream inputStream = file.getInputStream()) {
            // Use reflection to avoid compile-time dependency issues
            Class<?> docClass = Class.forName("org.apache.poi.xwpf.usermodel.XWPFDocument");
            Object document = docClass.getConstructor(InputStream.class).newInstance(inputStream);
            
            Class<?> extractorClass = Class.forName("org.apache.poi.xwpf.extractor.XWPFWordExtractor");
            Object extractor = extractorClass.getConstructor(docClass).newInstance(document);
            
            String text = (String) extractorClass.getMethod("getText").invoke(extractor);
            
            // Close resources
            extractorClass.getMethod("close").invoke(extractor);
            docClass.getMethod("close").invoke(document);
            
            if (text == null || text.trim().isEmpty()) {
                throw new IOException("DOCX file contains no readable text");
            }
            
            return cleanText(text);
            
        } catch (ClassNotFoundException e) {
            throw new IOException("Apache POI library not found. Please ensure poi-ooxml dependency is included.");
        } catch (Exception e) {
            throw new IOException("Failed to extract text from DOCX: " + e.getMessage());
        }
    }
    
    /**
     * Extract text from PDF file using Apache PDFBox
     */
    private String extractFromPDF(MultipartFile file) throws IOException {
        try (InputStream inputStream = file.getInputStream()) {
            // Use reflection to avoid compile-time dependency issues
            Class<?> docClass = Class.forName("org.apache.pdfbox.pdmodel.PDDocument");
            Object document = docClass.getMethod("load", InputStream.class).invoke(null, inputStream);
            
            Class<?> stripperClass = Class.forName("org.apache.pdfbox.text.PDFTextStripper");
            Object stripper = stripperClass.getConstructor().newInstance();
            
            String text = (String) stripperClass.getMethod("getText", docClass).invoke(stripper, document);
            
            // Close document
            docClass.getMethod("close").invoke(document);
            
            if (text == null || text.trim().isEmpty()) {
                throw new IOException("PDF file contains no readable text or may be password protected");
            }
            
            return cleanText(text);
            
        } catch (ClassNotFoundException e) {
            throw new IOException("Apache PDFBox library not found. Please ensure pdfbox dependency is included.");
        } catch (Exception e) {
            throw new IOException("Failed to extract text from PDF: " + e.getMessage());
        }
    }
    
    /**
     * Clean extracted text
     */
    private String cleanText(String text) {
        if (text == null) return "";
        
        return text
            .replaceAll("\\r\\n", "\n")           
            .replaceAll("\\r", "\n")              
            .replaceAll("\\s+", " ")              
            .replaceAll("\\n\\s*\\n", "\n")       
            .replaceAll("^\\s+|\\s+$", "")        
            .trim();
    }
    
    // Request/Response classes
    public static class TestResponse {
        private String message;
        private String description;
        private boolean status;
        
        public TestResponse(String message, String description, boolean status) {
            this.message = message;
            this.description = description;
            this.status = status;
        }
        
        public String getMessage() { return message; }
        public String getDescription() { return description; }
        public boolean isStatus() { return status; }
    }
    
    public static class PlagiarismRequest {
        private String sourceText;
        private String comparisonText;
        
        public PlagiarismRequest() {}
        
        public PlagiarismRequest(String sourceText, String comparisonText) {
            this.sourceText = sourceText;
            this.comparisonText = comparisonText;
        }
        
        public String getSourceText() { return sourceText; }
        public void setSourceText(String sourceText) { this.sourceText = sourceText; }
        
        public String getComparisonText() { return comparisonText; }
        public void setComparisonText(String comparisonText) { this.comparisonText = comparisonText; }
    }
}
