package org.opensrp.domain;

import java.util.Date;

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

    
    public Long getId() {
        return id;
    }

    
    public void setId(Long id) {
        this.id = id;
    }

    
    public String getIdentifier() {
        return identifier;
    }

    
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    
    public String getFilename() {
        return filename;
    }

    
    public void setFilename(String filename) {
        this.filename = filename;
    }

    
    public Boolean getOnObjectStorage() {
        return onObjectStorage;
    }

    
    public void setOnObjectStorage(Boolean onObjectStorage) {
        this.onObjectStorage = onObjectStorage;
    }

    
    public String getObjectStoragePath() {
        return objectStoragePath;
    }

    
    public void setObjectStoragePath(String objectStoragePath) {
        this.objectStoragePath = objectStoragePath;
    }

    
    public String getJurisdiction() {
        return jurisdiction;
    }

    
    public void setJurisdiction(String jurisdiction) {
        this.jurisdiction = jurisdiction;
    }

    
    public Integer getVersion() {
        return version;
    }

    
    public void setVersion(Integer version) {
        this.version = version;
    }

    
    public Integer getManifestId() {
        return manifestId;
    }

    
    public void setManifestId(Integer manifestId) {
        this.manifestId = manifestId;
    }

    
    public String getFileContents() {
        return fileContents;
    }

    
    public void setFileContents(String fileContents) {
        this.fileContents = fileContents;
    }

    
    public Date getCreatedAt() {
        return createdAt;
    }

    
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
