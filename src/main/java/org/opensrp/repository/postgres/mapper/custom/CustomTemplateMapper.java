package org.opensrp.repository.postgres.mapper.custom;

import org.apache.ibatis.annotations.Param;
import org.opensrp.domain.postgres.Template;
import org.opensrp.domain.postgres.TemplateExample;
import org.opensrp.repository.postgres.mapper.TemplateMapper;

import java.util.List;

public interface CustomTemplateMapper extends TemplateMapper {

    List<Template> selectMany(@Param("example") TemplateExample templateExample,
                              @Param("offset") int offset, @Param("limit") int limit);
}
