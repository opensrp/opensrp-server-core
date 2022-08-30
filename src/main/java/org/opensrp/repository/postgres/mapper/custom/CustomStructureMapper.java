package org.opensrp.repository.postgres.mapper.custom;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.opensrp.domain.postgres.Structure;
import org.opensrp.domain.postgres.StructureFamilyDetails;
import org.opensrp.repository.postgres.mapper.StructureMapper;

public interface CustomStructureMapper extends StructureMapper {

    int insertSelectiveAndSetId(Structure structure);

    List<StructureFamilyDetails> selectStructureAndFamilyWithinRadius(@Param("latitude") double latitude,
                                                                      @Param("longitude") double longitude, @Param("radius") double radius);

    Long selectServerVersionByPrimaryKey(Long id);

    int updateByPrimaryKeyAndGenerateServerVersion(Structure structure);
}
