package com.example.elastic.model;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@Document
public class MailModelDTO {
    @Id
    private String id;

    @NonNull
    private String sender;
    private String subject;
    private LocalDateTime sentOn;
    private String body;
    private boolean haveAttachment;
}
