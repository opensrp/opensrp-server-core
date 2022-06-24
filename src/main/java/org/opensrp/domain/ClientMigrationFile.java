package org.opensrp.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class ClientMigrationFile {

    private Long id;


    private String identifier;


    private String filename;


    private Boolean onObjectStorage;


    private String objectStoragePath;


    private String jurisdiction;


    private Integer version;


    private Integer manifestId;


    private String fileContents;


    private Date createdAt;

}
