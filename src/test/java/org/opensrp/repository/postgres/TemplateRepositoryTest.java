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
    public void testAddShouldAddnewTemplate() {
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
        templateRepository.add(template);

        List<Template> actualTemplates = templateRepository.getAll();
        Assert.assertNotNull(actualTemplates);
    }

}
