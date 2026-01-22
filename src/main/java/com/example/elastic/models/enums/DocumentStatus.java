package com.example.elastic.models.enums;


import lombok.Getter;

@Getter
public enum DocumentStatus {

    /**
     * The document has been successfully uploaded and is awaiting entry into the queue */
    UPLOADED("Document uploaded, awaiting queue entry"),

    /**
     * The document has been added to the processing queue */
    QUEUED("In processing queue"),

    /**
     * Picked up the document for processing (OCR and summarization etc.). */
    PULLED("Processing started"),

    /**
     * The automated parsing and data extraction was successful and is ready for data finalization. */
    PROCESSED_OK("Automated extraction complete"),

    /**
     * The automated parsing failed or yielded a confidence score too low, requiring human intervention so put in waiting for manual approval . */
    PARSING_FAILED("Automated parsing failed, manual review required"),

    /**
     * The document is currently waiting in the manual approval/correction queue. */
    PENDING_APPROVAL("Awaiting manual review & approval"),

    /**
     * The human operator has reviewed, corrected, and approved the data for the next step. */
    APPROVED_MANUAL("Manually verified and approved"),

    /**
     * imp
     * The human operator has determined the document is invalid or irrelevant and rejected it from the main flow.
     * put the document out of flow
     * */
    REJECTED_MANUAL("Manually rejected"),

    /**
     * The extracted data has been summarized or structured for persistence.
     * the document get this after approval whether it is automatic or manual
     * */
    SUMMARIZED_OK("Data summarized/structured"),

    /** The system is currently writing the final data structure to the database. */
    SAVING("Writing to database"),

    /** The document flow is complete and the final data has been successfully persisted. */
    SAVED_TO_DB("Flow complete, data persisted"),

    /** The document has been intentionally moved out of the primary workflow (e.g., after rejection). */
    ARCHIVED("Document archived, flow terminated");

    /**
     * -- GETTER --
     *  Returns a human-readable description of the status.
     */
    private final String description;

    /**
     * Constructor for the DocumentStatus enum.
     * @param description A human-readable description of the status.
     */
    DocumentStatus(String description) {
        this.description = description;
    }

}
