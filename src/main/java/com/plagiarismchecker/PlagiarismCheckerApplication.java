// src/main/java/com/plagiarismchecker/PlagiarismCheckerApplication.java
package com.plagiarismchecker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PlagiarismCheckerApplication {
    public static void main(String[] args) {
        SpringApplication.run(PlagiarismCheckerApplication.class, args);
        System.out.println("\n🚀 Plagiarism Checker is running!");
        System.out.println("📖 Open: http://localhost:8080");
        System.out.println("🔍 Upload documents to check for plagiarism\n");
    }
}
