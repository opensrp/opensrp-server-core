package org.opensrp.repository.postgres;

import org.apache.commons.lang.NotImplementedException;
import org.opensrp.domain.PlanTemplate;
import org.opensrp.domain.Template;
import org.opensrp.domain.postgres.TemplateExample;
import org.opensrp.repository.TemplateRepository;
import org.opensrp.repository.postgres.mapper.custom.CustomTemplateMapper;
import org.opensrp.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class TemplateRepositoryImpl extends BaseRepositoryImpl<Template> implements TemplateRepository {

    @Autowired
    private CustomTemplateMapper templateMapper;

    @Override
    public Template get(String id) {
        throw new NotImplementedException();
    }

    @Override
    public void add(Template template) {
        if (template == null) {
            return;
        }
        if (getUniqueField(template) == null) {
            return;
        }

        if (retrievePrimaryKey(template) != null) {
            return; // template already added
        }

        org.opensrp.domain.postgres.Template pgTemplate = convert(template);

        templateMapper.insertSelective(pgTemplate);
    }

    @Override
    public void update(Template template) {
        if (template == null) {
            return;
        }
        if (getUniqueField(template) == null) {
            return;
        }

        Long id = retrievePrimaryKey(template);
        if ( id == null) {
            return; // template does not exist
        }

        org.opensrp.domain.postgres.Template pgTemplate = convert(template);

        pgTemplate.setId(id);templateMapper.updateByPrimaryKeySelective(pgTemplate);
        //templateMapper.updateByPrimaryKey(pgTemplate);
    }

    @Override
    public List<Template> getAll() {
        TemplateExample templateExample = new TemplateExample();
        templateExample.createCriteria();
        List<org.opensrp.domain.postgres.Template> pgTemplateList = templateMapper.selectMany(templateExample, 0,
                DEFAULT_FETCH_SIZE);
        return convert(pgTemplateList);
    }

    @Override
    public List<Template> getAll(int limit) {
        TemplateExample templateExample = new TemplateExample();
        List<org.opensrp.domain.postgres.Template> pgTemplateList = templateMapper.selectMany(templateExample, 0, limit);
        return convert(pgTemplateList);
    }

    @Override
    public void safeRemove(Template template) {
        if (template == null) {
            return;
        }

        Long id = retrievePrimaryKey(template);
        if (id == null) {
            return;
        }

        templateMapper.deleteByPrimaryKey(id);
    }

    @Override
    public Template getByPrimaryKey(Long id) {
        if (id == null) {
            return null;
        }
        TemplateExample example = new TemplateExample();
        example.createCriteria().andIdEqualTo(id);
        List<org.opensrp.domain.postgres.Template> templates = templateMapper.selectByExample(example);
        return templates.isEmpty() ? null : convert(templates.get(0));
    }

    @Override
    public Template getTemplateByTemplateId(int templateId) {

        org.opensrp.domain.postgres.Template pgTemplate = getTemplate(templateId);

        return pgTemplate != null ? convert(pgTemplate) : null;
    }

    @Override
    public org.opensrp.domain.postgres.Template getTemplate(int templateId) {
        TemplateExample templateExample = new TemplateExample();
        templateExample.createCriteria().andTemplateIdEqualTo(templateId);

        List<org.opensrp.domain.postgres.Template> templateList = templateMapper.selectByExample(templateExample);

        return Utils.isEmptyList(templateList) ? null : templateList.get(0);
    }

    @Override
    protected Long retrievePrimaryKey(Template template) {
        Object uniqueId = getUniqueField(template);
        if (uniqueId == null) {
            return null;
        }

        int id = (int) uniqueId;
        org.opensrp.domain.postgres.Template pgPractitioner = getTemplate(id);

        return pgPractitioner == null ? null : pgPractitioner.getId();
    }

    @Override
    protected Object getUniqueField(Template template) {
        return template == null ? null : template.getTemplateId();
    }

    private Template convert(org.opensrp.domain.postgres.Template pgTemplate) {
        if (pgTemplate == null) {
            return null;
        }
        Template template = new Template();
        template.setTemplate((PlanTemplate) pgTemplate.getTemplate());
        template.setTemplateId(pgTemplate.getTemplateId());
        template.setVersion(pgTemplate.getVersion());
        template.setType(pgTemplate.getType());

        return template;
    }

    private org.opensrp.domain.postgres.Template convert(Template template) {
        if (template == null) {
            return null;
        }
        org.opensrp.domain.postgres.Template pgTemplate = new org.opensrp.domain.postgres.Template();
        pgTemplate.setTemplate(template.getTemplate());
        pgTemplate.setTemplateId(template.getTemplateId());
        pgTemplate.setVersion(template.getVersion());
        pgTemplate.setType(template.getType());

        return pgTemplate;
    }

    private List<Template> convert(List<org.opensrp.domain.postgres.Template> pgTemplates) {
        List<Template> templates = new ArrayList<>();
        if (Utils.isEmptyList(pgTemplates)) {
            return templates;
        }
        for(org.opensrp.domain.postgres.Template pgTemplate : pgTemplates) {
            templates.add(convert(pgTemplate));
        }
        return templates;
    }

}
