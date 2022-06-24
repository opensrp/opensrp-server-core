package org.opensrp.domain;

import java.util.HashSet;
import java.util.Set;

public class StructureDetails {

    private String structureId;

    private String structureParentId;

    private String structureType;

    private String familyId;

    private Set<String> familyMembers = new HashSet<>();

    public StructureDetails(String structureId, String structureParentId, String structureType) {
        super();
        this.structureId = structureId;
        this.structureParentId = structureParentId;
        this.structureType = structureType;
    }

    public String getStructureId() {
        return structureId;
    }

    public void setStructureId(String structureId) {
        this.structureId = structureId;
    }

    public String getStructureParentId() {
        return structureParentId;
    }

    public void setStructureParentId(String structureParentId) {
        this.structureParentId = structureParentId;
    }

    public String getStructureType() {
        return structureType;
    }

    public void setStructureType(String structureType) {
        this.structureType = structureType;
    }

    public String getFamilyId() {
        return familyId;
    }

    public void setFamilyId(String familyId) {
        this.familyId = familyId;
    }

    public Set<String> getFamilyMembers() {
        return familyMembers;
    }

    public void setFamilyMembers(Set<String> familyMembers) {
        this.familyMembers = familyMembers;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof StructureDetails))
            return false;
        StructureDetails other = (StructureDetails) obj;
        return structureId.equals(other.getStructureId());
    }

}
