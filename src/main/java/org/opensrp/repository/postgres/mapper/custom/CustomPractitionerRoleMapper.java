package org.opensrp.repository.postgres.mapper.custom;

import org.apache.ibatis.annotations.Param;
import org.opensrp.domain.postgres.PractitionerRole;
import org.opensrp.domain.postgres.PractitionerRoleExample;
import org.opensrp.repository.postgres.mapper.PractitionerRoleMapper;
import org.opensrp.search.PractitionerRoleSearchBean;

import java.util.List;

public interface CustomPractitionerRoleMapper extends PractitionerRoleMapper {
    List<PractitionerRole> selectMany(@Param("example") PractitionerRoleExample practitionerRoleExample,
                                      @Param("offset") int offset, @Param("limit") int limit);

}
