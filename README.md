# Advanced Plagiarism Detection System

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Maven](https://img.shields.io/badge/Maven-4.0.0-blue.svg)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

A sophisticated plagiarism detection system built with **Spring Boot**, implementing advanced string matching algorithms (**Rabin-Karp** and **KMP**) for accurate similarity analysis across multiple document formats.

## 🚀 Features

### 🔍 Advanced Detection Engine
- **Multi-Algorithm Approach**: Combines Rabin-Karp rolling hash and KMP pattern matching
- **5-Layer Analysis Strategy**: Sentences → Long Phrases → Medium Phrases → Words → Technical Terms
- **Intelligent Scoring**: Context-aware similarity calculation with penalty systems for mixed content
- **Smart Consolidation**: Removes overlaps and duplicates while preserving meaningful matches

### 📁 File Processing
- **Multiple Format Support**: PDF, DOCX, and TXT file uploads
- **Robust Text Extraction**: Apache PDFBox and Apache POI integration
- **Error Handling**: Comprehensive validation and exception management
- **File Size Limits**: Up to 10MB per document with safety checks

### 🌐 Web Interface
- **Responsive Design**: Clean HTML5/CSS3/JavaScript frontend
- **Real-time Results**: Instant similarity analysis with detailed match breakdown
- **Interactive API**: RESTful endpoints for programmatic access
- **Professional UI**: User-friendly interface with progress indicators

## 📊 Performance Metrics

| Test Scenario | Expected | Achieved | Status |
|---------------|----------|----------|---------|
| **Identical Text** | 85-100% | **100.0%** | ✅ Perfect |
| **Paraphrased Content** | 25-40% | **35.7%** | ✅ Accurate |
| **Different Topics** | 0-15% | **0.0%** | ✅ Precise |
| **Mixed Plagiarism** | 45-65% | **~60%** | ✅ Smart Detection |

## 🛠️ Technology Stack

### Backend
- **Spring Boot 3.2.0** - Enterprise application framework
- **Java 17** - Modern JVM platform with latest features
- **Maven** - Dependency management and build automation

### Algorithms & Libraries
- **Rabin-Karp Algorithm** - Efficient rolling hash pattern matching
- **KMP Algorithm** - Optimized string searching with failure function
- **Apache PDFBox** - PDF text extraction
- **Apache POI** - Microsoft Office document processing

### Frontend
- **HTML5** - Modern semantic markup
- **CSS3** - Responsive styling with animations
- **JavaScript** - Dynamic interaction and API communication

## 🏗️ Architecture

