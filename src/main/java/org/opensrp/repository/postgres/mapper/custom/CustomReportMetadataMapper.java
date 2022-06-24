package org.opensrp.repository.postgres.mapper.custom;

import org.apache.ibatis.annotations.Param;
import org.opensrp.domain.postgres.Report;
import org.opensrp.domain.postgres.ReportMetadataExample;
import org.opensrp.repository.postgres.mapper.ReportMetadataMapper;

import java.util.List;

public interface CustomReportMetadataMapper extends ReportMetadataMapper {

    Report selectByDocumentId(String documentId);

    List<Report> selectMany(@Param("example") ReportMetadataExample example, @Param("offset") int offset,
                            @Param("limit") int limit);

}
