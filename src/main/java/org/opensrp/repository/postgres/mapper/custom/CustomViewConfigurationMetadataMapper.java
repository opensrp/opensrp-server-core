package org.opensrp.repository.postgres.mapper.custom;

import org.apache.ibatis.annotations.Param;
import org.opensrp.domain.postgres.ViewConfiguration;
import org.opensrp.domain.postgres.ViewConfigurationMetadataExample;
import org.opensrp.repository.postgres.mapper.ViewConfigurationMetadataMapper;

import java.util.List;

public interface CustomViewConfigurationMetadataMapper extends ViewConfigurationMetadataMapper {

    List<ViewConfiguration> selectMany(@Param("example") ViewConfigurationMetadataExample viewConfigurationExample,
                                       @Param("offset") int offset, @Param("limit") int limit);

    ViewConfiguration selectByDocumentId(String documentId);

}
