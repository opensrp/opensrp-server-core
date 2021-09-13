package org.opensrp.repository;

import org.opensrp.domain.Template;

public interface TemplateRepository extends BaseRepository<Template> {

    Template getByPrimaryKey(Long id);

    org.opensrp.domain.postgres.Template  getTemplate(int templateId);

    Template getTemplateByTemplateId(int id);
}
