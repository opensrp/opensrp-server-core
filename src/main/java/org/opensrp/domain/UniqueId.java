package org.opensrp.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "unique_ids")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UniqueId {

    public static final String tbName = "unique_ids";

    public static final String COL_OPENMRSID = "openmrs_id";

    public static final String COL_STATUS = "status";

    public static final String COL_USEDBY = "used_by";

    public static final String COL_LOCATION = "location";

    public static final String COL_CREATED_AT = "created_at";

    public static final String COL_UPDATED_AT = "updated_at";

    public static String STATUS_USED = "used";

    public static String STATUS_NOT_USED = "not_used";

    @Id
    @GeneratedValue
    @Column(name = "_id")
    private Long id;

    @Column(name = COL_OPENMRSID)
    private String openmrsId;

    @Column(name = COL_STATUS)
    private String status;

    @Column(name = COL_USEDBY)
    private String usedBy;

    @Column(name = COL_LOCATION)
    private String location;

    @Column(name = COL_CREATED_AT, columnDefinition = "DATETIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = COL_UPDATED_AT, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    private String identifier;

    private Long idSource;

    private boolean isReserved;

    public UniqueId(String openmrsId, String status, String usedBy, String location, Date createdAt, Date updatedAt,
                    String identifier, Long idSource, boolean isReserved) {
        this.openmrsId = openmrsId;
        this.status = status;
        this.usedBy = usedBy;
        this.location = location;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.identifier = identifier;
        this.idSource = idSource;
        this.isReserved = isReserved;
    }
}
