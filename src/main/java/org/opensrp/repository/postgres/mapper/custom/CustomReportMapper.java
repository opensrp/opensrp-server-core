package org.opensrp.repository.postgres.mapper.custom;

import org.apache.ibatis.annotations.Param;
import org.opensrp.domain.postgres.Report;
import org.opensrp.repository.postgres.mapper.ReportMapper;

import java.util.List;

public interface CustomReportMapper extends ReportMapper {

    int insertSelectiveAndSetId(Report report);

    List<Report> selectByIdentifier(@Param("identifier") String identifier, @Param("offset") int offset,
                                    @Param("limit") int limit);

    Long selectServerVersionByPrimaryKey(Long id);

    int updateByPrimaryKeyAndGenerateServerVersion(Report report);

}
