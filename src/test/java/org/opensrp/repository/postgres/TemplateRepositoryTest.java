package org.opensrp.repository.postgres;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opensrp.domain.PlanTemplate;
import org.opensrp.domain.Template;
import org.opensrp.repository.TemplateRepository;
import org.smartregister.domain.Jurisdiction;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class TemplateRepositoryTest extends BaseRepositoryTest{

    @Autowired
    private TemplateRepository templateRepository;

    @BeforeClass
    public static void bootStrap() {
        tableNames= Arrays.asList("core.template");
    }

    @Override
    protected Set<String> getDatabaseScripts() {
        Set<String> scripts = new HashSet<>();
        return scripts;
    }

    @Test
    public void testAddShouldAddNewTemplate() {
        Template template = initTestTemplate();
        templateRepository.add(template);

        List<Template> actualTemplates = templateRepository.getAll();
        Assert.assertNotNull(actualTemplates);
        Assert.assertEquals(1, actualTemplates.get(0).getTemplateId().intValue());
        Assert.assertEquals("operation_area_1", actualTemplates.get(0).getTemplate().getJurisdiction().get(0).getCode());
    }

    @Test
    public void testUpdateShouldUpdateExistingTemplate() {
        Template template = initTestTemplate();
        templateRepository.add(template);

        List<Template> actualTemplates = templateRepository.getAll();
        Assert.assertNotNull(actualTemplates);
        Assert.assertEquals(1, actualTemplates.size());
        Assert.assertEquals(1, actualTemplates.get(0).getTemplateId().intValue());
        Assert.assertEquals("operation_area_1", actualTemplates.get(0).getTemplate().getJurisdiction().get(0).getCode());

        template.setType("event");
        templateRepository.update(template);

        List<Template> updatedTemplates = templateRepository.getAll();
        Assert.assertNotNull(updatedTemplates);
        Assert.assertEquals(1, updatedTemplates.size());
        Template updatedTemplate = updatedTemplates.get(0);
        Assert.assertEquals("event", updatedTemplate.getType());
        Assert.assertEquals("operation_area_1", updatedTemplate.getTemplate().getJurisdiction().get(0).getCode());

    }

    @Test
    public void testGetAll() {
        Template template = initTestTemplate();
        templateRepository.add(template);

        Template template2 = initTestTemplate();
        template2.setTemplateId(2);
        template2.setType("event");
        templateRepository.add(template2);

        List<Template> actualTemplates = templateRepository.getAll();
        Assert.assertNotNull(actualTemplates);
        Assert.assertEquals(2, actualTemplates.size());

    }

    @Test
    public void testGetAllWithLimit() {
        Template template = initTestTemplate();
        templateRepository.add(template);

        Template template2 = initTestTemplate();
        template2.setTemplateId(2);
        template2.setType("event");
        templateRepository.add(template2);

        List<Template> actualTemplates = templateRepository.getAll(1);
        Assert.assertNotNull(actualTemplates);
        Assert.assertEquals(1, actualTemplates.size());

    }

    @Test
    public void testGetTemplateByTemplateIdShouldReturnCorrectTemplate() {
        Template template = initTestTemplate();
        templateRepository.add(template);

        Template template2 = initTestTemplate();
        template2.setTemplateId(2);
        template2.setType("event");
        templateRepository.add(template2);

        Template actualTemplate = templateRepository.getTemplateByTemplateId(2);
        Assert.assertNotNull(actualTemplate);
        Assert.assertEquals(2, actualTemplate.getTemplateId().intValue());
        Assert.assertEquals("event", actualTemplate.getType());
        Assert.assertEquals("operation_area_1", actualTemplate.getTemplate().getJurisdiction().get(0).getCode());
    }

    @Test
    public void testGetTemplateShouldReturnCorrectPgTemplate() {
        Template template = initTestTemplate();
        templateRepository.add(template);

        Template template2 = initTestTemplate();
        template2.setTemplateId(2);
        template2.setType("event");
        templateRepository.add(template2);

        org.opensrp.domain.postgres.Template actualTemplate = templateRepository.getTemplate(2);
        Assert.assertNotNull(actualTemplate);
        Assert.assertEquals(2, actualTemplate.getTemplateId().intValue());
        Assert.assertEquals("event", actualTemplate.getType());
    }

    private Template initTestTemplate() {
        PlanTemplate planTemplate = new PlanTemplate();

        List<Jurisdiction> jurisdictions = new ArrayList<>();
        Jurisdiction jurisdiction = new Jurisdiction();
        jurisdiction.setCode("operation_area_1");
        jurisdictions.add(jurisdiction);
        planTemplate.setJurisdiction(jurisdictions);

        Template template = new Template();
        template.setTemplate(planTemplate);
        template.setTemplateId(1);
        template.setType("1");
        return template;
    }

}
