package org.opensrp.service;

import org.opensrp.domain.Template;
import org.opensrp.repository.TemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TemplateService {

    private TemplateRepository templateRepository;

    @Autowired
    public void setTemplateRepository(TemplateRepository templateRepository) {
        this.templateRepository = templateRepository;
    }

    public TemplateRepository getTemplateRepository() {
        return templateRepository;
    }

    public Template getTemplateByTemplateId(int templateId) {
        return getTemplateRepository().getTemplateByTemplateId(templateId);
    }

    public Template addOrUpdateTemplate(Template template) {
        if (template.getTemplateId() == null) {
            throw new IllegalArgumentException("Identifier not specified");
        }

        if (getTemplateRepository().getTemplateByTemplateId(template.getTemplateId()) != null) {
            getTemplateRepository().update(template);
        } else {
            getTemplateRepository().add(template);
        }
        return template;
    }

}
