package org.opensrp.repository.postgres.mapper.custom;

import org.apache.ibatis.annotations.Param;
import org.opensrp.domain.postgres.Alert;
import org.opensrp.domain.postgres.AlertMetadataExample;
import org.opensrp.repository.postgres.mapper.AlertMetadataMapper;

import java.util.List;

public interface CustomAlertMetadataMapper extends AlertMetadataMapper {

    Alert selectByDocumentId(String documentId);

    List<Alert> selectMany(@Param("example") AlertMetadataExample example, @Param("offset") int offset,
                           @Param("limit") int limit);
}
