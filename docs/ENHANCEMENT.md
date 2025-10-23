
## Aim - Functional & Non-Functional Requirement to be Achieved

### Functional Requirements

- [x] Ingestion and Data Normalization
- [x] Document Summarization
- [x] Entity & Keyword Extraction
- [] Content Routing & Personalization
- [] Information Retrieval & Traceability
- [] Real-time Alerts & Notifications
- [] Knowledge Base Management
- [] Data Integration
- [] Bilingual Support

### Non-Functional Requirements

- Performance
- Scalability
- Security
- Reliability
- Accuracy
- Usability
- Auditability & Compliance
- Availability

## Code Style & Code Quality

- CheckStyle Integration -> `config`

- Custom Annotations
```
@Retention(RetentionPolicy.RUNTIME) // available at runtime
@Target({ElementType.METHOD, ElementType.TYPE}) // usable on classes & methods
public @interface Todo
{
    String message() default "";   // message / reason
    String assignedTo() default "unassigned"; // dev name
    int priority() default 1;    // i.e 1 = low, 5 = high
}
```
- Custom Exceptions
```
public class LLMException extends Exception {
    public LLMException() {
        super();
    }
    public LLMException(String message) {
        super(message);
    }
}
```




## Functional Enhancement

- OCR Optimization
- Batch Processing
- Tests implementation
- Other Connectors
- Select Model runtime - easy to switch via spring profiles
- Fetch


## UI Suggestions

- Message OR file requesting among various departments - See for its Automation
- Folder View
- Next : Plan to collect all this
    file size, file upload date, other data which will identify department
    on UI keep description option - not mandatory to fill
    device from it is uploaded describe doc to search option -> description will be sent to
    LLM with related prompt to give search word and search will be done in elastic