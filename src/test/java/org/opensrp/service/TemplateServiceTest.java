package org.opensrp.service;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.opensrp.domain.PlanTemplate;
import org.opensrp.domain.Template;
import org.opensrp.repository.TemplateRepository;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TemplateServiceTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    private TemplateService templateService;

    @Mock
    private TemplateRepository templateRepository;

    @Before
    public void setUp() {
        templateService = new TemplateService();
        templateService.setTemplateRepository(templateRepository);
    }

    @Test
    public void testGetTemplateRepositoryShouldReturnTemplaterepository() {
        assertEquals(templateRepository, templateService.getTemplateRepository());
    }

    @Test
    public void testGetAllCallsRepositoryMethod() {
        templateService.getAll();
        verify(templateRepository).getAll();
    }

    @Test
    public void testGetAllWithLimitCallsRepositoryMethod() {
        templateService.getAll(10);
        verify(templateRepository).getAll(10);
    }

    @Test
    public void testGetTemplateByTemplateIdCallsRepositoryMethod() {
        templateService.getTemplateByTemplateId(1);
        verify(templateRepository).getTemplateByTemplateId(1);
    }

    @Test
    public void testAddOrUpdateTemplateCallsRepositoryUpdateMethod() {
        Template template = new Template();
        template.setTemplateId(1);
        template.setTemplate(new PlanTemplate());
        template.setType("plan");
        template.setVersion(0);
        when(templateRepository.getTemplateByTemplateId(1)).thenReturn(template);

        templateService.addOrUpdateTemplate(template);
        verify(templateRepository).getTemplateByTemplateId(1);
        verify(templateRepository).update(template);

    }

    @Test
    public void testAddOrUpdateTemplateCallsRepositoryAddMethod() {
        Template template = new Template();
        template.setTemplateId(1);
        template.setTemplate(new PlanTemplate());
        template.setType("plan");
        template.setVersion(0);
        when(templateRepository.getTemplateByTemplateId(1)).thenReturn(null);

        templateService.addOrUpdateTemplate(template);
        verify(templateRepository).getTemplateByTemplateId(1);
        verify(templateRepository).add(template);

    }
}
