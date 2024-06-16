package ru.salfa.messenger.dto.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.net.URL;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class AttachmentsDto {
    private String type;
    private URL url;
}
