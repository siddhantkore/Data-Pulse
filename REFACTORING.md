# Document Processor Service Refactoring - SOLID Principles

## Overview
The monolithic `DocumentProcessorService` has been refactored into 6 focused, single-responsibility services following SOLID principles (specifically **Single Responsibility Principle** and **Dependency Injection**).

---

## Class Architecture

### 1. **OcrExtractionService** 
📄 `src/main/java/com/example/elastic/services/processors/OcrExtractionService.java`

**Responsibility:** Extract text from images/PDFs using Tesseract OCR

**Key Methods:**
- `extractText(File tempFile)` - Performs OCR on a file
- `initializeTesseract()` - Configures Tesseract instance

**Benefits:**
- Isolated OCR logic
- Easy to mock for testing
- Can swap Tesseract with other OCR engines

---

### 2. **TextCleaningService**
📄 `src/main/java/com/example/elastic/services/processors/TextCleaningService.java`

**Responsibility:** Normalize and clean extracted text

**Key Methods:**
- `cleanText(String rawText)` - Removes extra spaces, normalizes line breaks
- `sanitizeLlmResponse(String llmResponse)` - Removes markdown code blocks

**Benefits:**
- Reusable text cleaning logic
- Can be used by other services
- Consistent text preprocessing

---

### 3. **LlmProcessingService**
📄 `src/main/java/com/example/elastic/services/processors/LlmProcessingService.java`

**Responsibility:** Process text with LLM (Large Language Model)

**Key Methods:**
- `processWithLLM(String cleanedText)` - Sends text to LLM and returns structured response

**Benefits:**
- Encapsulates LLM integration
- Integrates cleaning service automatically
- Easy to add caching or retries

---

### 4. **DocumentDataMapper**
📄 `src/main/java/com/example/elastic/services/processors/DocumentDataMapper.java`

**Responsibility:** Map JSON response to DocumentMetadata domain model

**Key Methods:**
- `mapLLMResponseToDocument(String llmResponse, DocumentMetadata doc)` - Maps all fields
- `mapMetadata(JsonNode root)` - Maps nested metadata object

**Benefits:**
- Centralized mapping logic
- Easy to modify JSON structure handling
- Testable data transformation

---

### 5. **DocumentPersistenceService**
📄 `src/main/java/com/example/elastic/services/processors/DocumentPersistenceService.java`

**Responsibility:** All database operations (CRUD, status updates)

**Key Methods:**
- `createPendingDocument(DocumentMetadata doc)` - Create document with PENDING status
- `updateDocumentStatus(String docId, DocumentStatus status)` - Update status
- `getDocumentById(String docId)` - Retrieve document
- `saveDocument(DocumentMetadata doc)` - Save processed document
- `markAsFailedWithError(String docId, String msg)` - Mark as failed

**Benefits:**
- Single point for database access
- Easy to add caching or transactions
- Decouples processing logic from persistence

---

### 6. **FileHandlingService**
📄 `src/main/java/com/example/elastic/services/processors/FileHandlingService.java`

**Responsibility:** File I/O operations (temp file creation/cleanup)

**Key Methods:**
- `createTempFileFromBytes(String docId, byte[] bytes)` - Create temp file
- `deleteTempFile(File file)` - Safely delete temp file

**Benefits:**
- Encapsulates file operations
- Ensures consistent cleanup
- Static utility methods for easy access

---

### 7. **DocumentProcessorService (Refactored)**
📄 `src/main/java/com/example/elastic/services/DocumentProcessorService.java`

**Responsibility:** Orchestrate the document processing pipeline

**Key Methods:**
- `processAndStore(byte[] fileBytes, String documentId)` - Main processing orchestrator
- `createPendingDocument(DocumentMetadata doc)` - Delegate to persistence service
- `getDocumentStatus(String documentId)` - Delegate to persistence service
- `markDocumentAsFailed(String documentId, String msg)` - Delegate to persistence service

**Processing Pipeline:**
```
1. Retrieve existing document from DB
2. Update status to PROCESSING
3. Create temp file from bytes
4. Extract text via OCR (OcrExtractionService)
5. Clean extracted text (TextCleaningService)
6. Process with LLM (LlmProcessingService)
7. Map LLM response to document (DocumentDataMapper)
8. Save to database (DocumentPersistenceService)
9. Cleanup temp files (FileHandlingService)
```

**Benefits:**
- Clean, readable orchestration flow
- Delegates to specialized services
- Easy to debug: each step is isolated
- Easy to extend: add new processors without changing orchestrator

---

## SOLID Principles Applied

### ✅ Single Responsibility Principle (SRP)
Each service has ONE reason to change:
- `OcrExtractionService` - Only changes if Tesseract changes
- `TextCleaningService` - Only changes if cleaning logic changes
- `LlmProcessingService` - Only changes if LLM API changes
- `DocumentDataMapper` - Only changes if JSON structure changes
- `DocumentPersistenceService` - Only changes if database changes
- `FileHandlingService` - Only changes if file I/O changes

### ✅ Dependency Injection
All services use constructor injection:
```java
public DocumentProcessorService(
    OcrExtractionService ocrService,
    TextCleaningService textCleaningService,
    LlmProcessingService llmProcessingService,
    DocumentDataMapper documentDataMapper,
    DocumentPersistenceService documentPersistenceService
)
```

Benefits:
- Easy to test (inject mocks)
- Loose coupling
- Spring automatically manages dependencies

### ✅ Open/Closed Principle (OCP)
- New processing steps can be added without modifying existing code
- Each service can be extended independently

### ✅ Interface Segregation Principle (ISP)
- Each service exposes only the methods clients need
- No fat interfaces

---

## Usage in Other Classes

### In KafkaFilesConsumer
```java
// No changes needed - same interface
documentProcessorService.processAndStore(fileBytes, documentId);
```

### In DocumentController
```java
// Create pending document
DocumentMetadata saved = documentProcessorService.createPendingDocument(pendingDocument);

// Get status
DocumentMetadata doc = documentProcessorService.getDocumentStatus(documentId);

// Mark as failed
documentProcessorService.markDocumentAsFailed(documentId, errorMsg);
```

---

## Testing Benefits

Now it's MUCH easier to write unit tests:

```java
@ExtendWith(MockitoExtension.class)
public class DocumentProcessorServiceTest {
    
    @Mock private OcrExtractionService ocrService;
    @Mock private TextCleaningService textCleaningService;
    @Mock private LlmProcessingService llmProcessingService;
    @Mock private DocumentDataMapper documentDataMapper;
    @Mock private DocumentPersistenceService documentPersistenceService;
    
    @InjectMocks private DocumentProcessorService documentProcessorService;
    
    @Test
    public void testProcessAndStore() {
        // Each service can be mocked independently
        when(ocrService.extractText(any())).thenReturn("mocked text");
        // ... test orchestration logic
    }
}
```

---

## Migration Path

### Before Refactoring
- ❌ 319 lines in DocumentProcessorService
- ❌ Multiple responsibilities mixed
- ❌ Hard to test individual components
- ❌ Hard to reuse text cleaning or data mapping

### After Refactoring
- ✅ 147 lines in refactored DocumentProcessorService
- ✅ Clear separation of concerns
- ✅ Each service testable independently
- ✅ Services can be reused elsewhere

---

## File Structure
```
src/main/java/com/example/elastic/services/
├── DocumentProcessorService.java (REFACTORED - Orchestrator)
└── processors/
    ├── OcrExtractionService.java
    ├── TextCleaningService.java
    ├── LlmProcessingService.java
    ├── DocumentDataMapper.java
    ├── DocumentPersistenceService.java
    └── FileHandlingService.java
```

---

## Summary

**Before:** One large class doing everything ❌  
**After:** 6 focused services, one orchestrator ✅

This refactoring makes the code:
- **More maintainable** - Each class has one job
- **More testable** - Easy to mock individual services
- **More reusable** - Services can be used independently
- **More flexible** - Easy to swap implementations
- **More readable** - Clear intent and flow
