package org.opensrp.repository;

import org.opensrp.domain.Template;

import java.util.List;

public interface TemplateRepository extends BaseRepository<Template> {

    Template getByPrimaryKey(Long id);

    org.opensrp.domain.postgres.Template  getTemplate(int templateId);

    Template getTemplateByTemplateId(int id);

    List<Template> getAll(int limit);
}
