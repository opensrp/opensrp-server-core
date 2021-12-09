package org.opensrp.repository.postgres;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opensrp.domain.PlanTemplate;
import org.opensrp.domain.Template;
import org.opensrp.repository.TemplateRepository;
import org.smartregister.utils.DateTypeConverter;
import org.smartregister.utils.TaskDateTimeTypeConverter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class TemplateRepositoryTest extends BaseRepositoryTest{

    @Autowired
    private TemplateRepository templateRepository;

    public Gson gson = new GsonBuilder().registerTypeAdapter(DateTime.class, new TaskDateTimeTypeConverter())
            .registerTypeAdapter(LocalDate.class, new DateTypeConverter()).create();

    String planJson = "{\"identifier\":\"64ae21ed-e60f-4ab0-8060-335375f8482b\",\"version\":\"2\",\"name\":\"A1-เมืองทอง(Site)-4-Madrine_QA Test-2021-12-07-Site\",\"title\":\"A1 - เมืองทอง(Site)-4 - Madrine QA Test - 2021-12-07 - Site\",\"status\":\"active\",\"date\":\"2021-12-07\",\"effectivePeriod\":{\"start\":\"2021-12-07\",\"end\":\"2026-12-07\"},\"useContext\":[{\"code\":\"interventionType\",\"valueCodableConcept\":\"Dynamic-FI\"},{\"code\":\"fiStatus\",\"valueCodableConcept\":\"A1\"},{\"code\":\"fiReason\",\"valueCodableConcept\":\"Case Triggered\"},{\"code\":\"caseNum\",\"valueCodableConcept\":\"2401224081141261323\"},{\"code\":\"opensrpEventId\",\"valueCodableConcept\":\"685f019c-01b7-4812-b80f-277321f6f8f3\"},{\"code\":\"taskGenerationStatus\",\"valueCodableConcept\":\"internal\"},{\"code\":\"teamAssignmentStatus\",\"valueCodableConcept\":\"True\"}],\"jurisdiction\":[{\"code\":\"b49faa39-5e24-4de8-bf3f-cdda6f320179\"}],\"serverVersion\":1607066368048,\"goal\":[{\"id\":\"Case_Confirmation\",\"description\":\"ยืนยันบ้านผู้ป่วย\",\"priority\":\"medium-priority\",\"target\":[{\"measure\":\"จำนวนผู้ป่วยที่ได้รับการยืนยัน\",\"detail\":{\"detailQuantity\":{\"value\":1.0,\"comparator\":\"\\u0026amp;gt;\\u003d\",\"unit\":\"case(s)\"}},\"due\":\"2026-12-07\"}]},{\"id\":\"RACD_register_families\",\"description\":\"ลงทะเบียนครัวเรือนและสมาชิกในครัวเรือน (100%) ภายในพื้นที่ปฏิบัติงาน\",\"priority\":\"medium-priority\",\"target\":[{\"measure\":\"ร้อยละของบ้าน/สิ่งปลูกสร้างที่ได้ลงทะเบียนข้อมูลครัวเรือน\",\"detail\":{\"detailQuantity\":{\"value\":100.0,\"comparator\":\"\\u0026amp;gt;\\u003d\",\"unit\":\"Percent\"}},\"due\":\"2026-12-07\"}]},{\"id\":\"RACD_Blood_Screening\",\"description\":\"เจาะเลือดรอบบ้านผู้ป่วยในรัศมี 1 กิโลเมตร (100%)\",\"priority\":\"medium-priority\",\"target\":[{\"measure\":\"จำนวนผู้ที่ได้รับการเจาะโลหิต\",\"detail\":{\"detailQuantity\":{\"value\":50.0,\"comparator\":\"\\u0026amp;gt;\\u003d\",\"unit\":\"Person(s)\"}},\"due\":\"2026-12-07\"}]},{\"id\":\"BCC_Focus\",\"description\":\"ให้สุขศึกษาในพื้นที่ปฏิบัติงานอย่างน้อย 1 ครั้ง\",\"priority\":\"medium-priority\",\"target\":[{\"measure\":\"จำนวนกิจกรรมการให้สุขศึกษา\",\"detail\":{\"detailQuantity\":{\"value\":1.0,\"comparator\":\"\\u0026amp;gt;\\u003d\",\"unit\":\"activit(y|ies)\"}},\"due\":\"2026-12-07\"}]},{\"id\":\"RACD_bednet_distribution\",\"description\":\"แจกมุ้งทุกหลังคาเรือนในพื้นที่ปฏิบัติงาน (100%)\",\"priority\":\"medium-priority\",\"target\":[{\"measure\":\"จำนวนบ้าน/สิ่งปลูกสร้างที่ได้รับมุ้ง\",\"detail\":{\"detailQuantity\":{\"value\":90.0,\"comparator\":\"\\u0026amp;gt;\\u003d\",\"unit\":\"Percent\"}},\"due\":\"2026-12-07\"}]},{\"id\":\"Larval_Dipping\",\"description\":\"ดำเนินกิจกรรมจับลูกน้ำอย่างน้อย 3 แห่งในพื้นที่ปฏิบัติงาน\",\"priority\":\"medium-priority\",\"target\":[{\"measure\":\"จำนวนกิจกรรมการตักลูกน้ำ\",\"detail\":{\"detailQuantity\":{\"value\":3.0,\"comparator\":\"\\u0026amp;gt;\\u003d\",\"unit\":\"activit(y|ies)\"}},\"due\":\"2026-12-07\"}]},{\"id\":\"Mosquito_Collection\",\"description\":\"กิจกรรมจับยุงกำหนดไว้อย่างน้อย 3 แห่ง\",\"priority\":\"medium-priority\",\"target\":[{\"measure\":\"จำนวนกิจกรรมการจับยุง\",\"detail\":{\"detailQuantity\":{\"value\":3.0,\"comparator\":\"\\u0026amp;gt;\\u003d\",\"unit\":\"activit(y|ies)\"}},\"due\":\"2026-12-07\"}]}],\"action\":[{\"identifier\":\"662d4bff-43cc-4f50-af1e-3bc86f8af253\",\"prefix\":1,\"title\":\"การยืนยันบ้านผู้ป่วย\",\"description\":\"ยืนยันบ้านผู้ป่วย\",\"code\":\"Case Confirmation\",\"timingPeriod\":{\"start\":\"2021-09-13\",\"end\":\"2021-09-23\"},\"reason\":\"Investigation\",\"goalId\":\"Case_Confirmation\",\"subjectCodableConcept\":{\"text\":\"QuestionnaireResponse\"},\"trigger\":[{\"type\":\"named-event\",\"name\":\"plan-activation\"}],\"condition\":[{\"kind\":\"applicability\",\"expression\":{\"description\":\"Event is case details event\",\"expression\":\"questionnaire \\u003d \\u0027Case_Details\\u0027\"}}],\"definitionUri\":\"case_confirmation.json\",\"type\":\"create\"},{\"identifier\":\"d0a9e189-240e-40fb-9761-6960a735426c\",\"prefix\":2,\"title\":\"ลงทะเบียนครัวเรือน\",\"description\":\"ลงทะเบียนครัวเรือนและสมาชิกในครัวเรือน (100%) ภายในพื้นที่ปฏิบัติงาน\",\"code\":\"RACD Register Family\",\"timingPeriod\":{\"start\":\"2021-12-07\",\"end\":\"2026-12-07\"},\"reason\":\"Investigation\",\"goalId\":\"RACD_register_families\",\"subjectCodableConcept\":{\"text\":\"Location\"},\"trigger\":[{\"type\":\"named-event\",\"name\":\"plan-activation\"},{\"type\":\"named-event\",\"name\":\"event-submission\",\"expression\":{\"description\":\"Trigger when a Register_Structure event is submitted\",\"expression\":\"questionnaire \\u003d \\u0027Register_Structure\\u0027 or questionnaire \\u003d \\u0027Archive_Family\\u0027\"}}],\"condition\":[{\"kind\":\"applicability\",\"expression\":{\"description\":\"Apply to residential structures in Register_Structure questionnaires\",\"expression\":\"$this.is(FHIR.Location) or (questionnaire \\u003d \\u0027Register_Structure\\u0027 and $this.item.where(linkId\\u003d\\u0027structureType\\u0027).answer.value \\u003d\\u0027Residential Structure\\u0027)\"}},{\"kind\":\"applicability\",\"expression\":{\"description\":\"Structure is residential or type does not exist\",\"expression\":\"$this.is(FHIR.QuestionnaireResponse) or (($this.type.where(id\\u003d\\u0027locationType\\u0027).exists().not() or $this.type.where(id\\u003d\\u0027locationType\\u0027).text \\u003d \\u0027Residential Structure\\u0027) and $this.contained.exists().not())\",\"subjectCodableConcept\":{\"text\":\"Family\"}}}],\"definitionUri\":\"family_register.json\",\"type\":\"create\"},{\"identifier\":\"fdf297df-3593-47e9-97fc-e6a93851cf37\",\"prefix\":3,\"title\":\"กิจกรรมการเจาะโลหิต\",\"description\":\"เจาะเลือดรอบบ้านผู้ป่วยในรัศมี 1 กิโลเมตร (100%)\",\"code\":\"Blood Screening\",\"timingPeriod\":{\"start\":\"2021-12-07\",\"end\":\"2026-12-07\"},\"reason\":\"Investigation\",\"goalId\":\"RACD_Blood_Screening\",\"subjectCodableConcept\":{\"text\":\"Person\"},\"trigger\":[{\"type\":\"named-event\",\"name\":\"plan-activation\"},{\"type\":\"named-event\",\"name\":\"event-submission\",\"expression\":{\"description\":\"Trigger when a Family Registration or Family Member Registration event is submitted\",\"expression\":\"questionnaire \\u003d \\u0027Family_Registration\\u0027 or questionnaire \\u003d \\u0027Family_Member_Registration\\u0027\"}}],\"condition\":[{\"kind\":\"applicability\",\"expression\":{\"description\":\"Person is older than 5 years or person associated with questionnaire response if older than 5 years\",\"expression\":\"($this.is(FHIR.Patient) and $this.birthDate \\u0026amp;amp;lt;\\u003d today() - 5 \\u0027years\\u0027) or ($this.contained.where(Patient.birthDate \\u0026amp;amp;lt;\\u003d today() - 5 \\u0027years\\u0027).exists())\"}}],\"definitionUri\":\"blood_screening.json\",\"type\":\"create\"},{\"identifier\":\"9ef064b3-023e-411f-8b37-491e3d0cf830\",\"prefix\":4,\"title\":\"กิจกรรมการให้สุขศึกษา\",\"description\":\"ดำเนินกิจกรรมให้สุขศึกษา\",\"code\":\"BCC\",\"timingPeriod\":{\"start\":\"2021-12-07\",\"end\":\"2026-12-07\"},\"reason\":\"Investigation\",\"goalId\":\"BCC_Focus\",\"subjectCodableConcept\":{\"text\":\"Jurisdiction\"},\"trigger\":[{\"type\":\"named-event\",\"name\":\"plan-activation\"}],\"condition\":[{\"kind\":\"applicability\",\"expression\":{\"description\":\"Jurisdiction type location\",\"expression\":\"Location.physicalType.coding.exists(code\\u003d\\u0027jdn\\u0027)\"}}],\"definitionUri\":\"behaviour_change_communication.json\",\"type\":\"create\"},{\"identifier\":\"21c75b01-f0c5-4fb0-ae11-58da4b23c036\",\"prefix\":5,\"title\":\"กิจกรรมสำรวจ/ชุบ/แจกมุ้ง\",\"description\":\"แจกมุ้งทุกหลังคาเรือนในพื้นที่ปฏิบัติงาน (100%)\",\"code\":\"Bednet Distribution\",\"timingPeriod\":{\"start\":\"2021-12-07\",\"end\":\"2026-12-07\"},\"reason\":\"Investigation\",\"goalId\":\"RACD_bednet_distribution\",\"subjectCodableConcept\":{\"text\":\"Location\"},\"trigger\":[{\"type\":\"named-event\",\"name\":\"plan-activation\"},{\"type\":\"named-event\",\"name\":\"event-submission\",\"expression\":{\"description\":\"Trigger when a Family Registration event is submitted\",\"expression\":\"questionnaire \\u003d \\u0027Family_Registration\\u0027\"}}],\"condition\":[{\"kind\":\"applicability\",\"expression\":{\"description\":\"Structure is residential or type does not exist\",\"expression\":\"$this.is(FHIR.QuestionnaireResponse) or (($this.type.where(id\\u003d\\u0027locationType\\u0027).exists().not() or $this.type.where(id\\u003d\\u0027locationType\\u0027).text \\u003d \\u0027Residential Structure\\u0027) and $this.contained.exists())\",\"subjectCodableConcept\":{\"text\":\"Family\"}}}],\"definitionUri\":\"bednet_distribution.json\",\"type\":\"create\"},{\"identifier\":\"90032f7c-e9fc-4a0a-9962-78584cb42f82\",\"prefix\":6,\"title\":\"กิจกรรมการตักลูกน้ำ\",\"description\":\"ดำเนินกิจกรรมจับลูกน้ำอย่างน้อย 3 แห่งในพื้นที่ปฏิบัติงาน\",\"code\":\"Larval Dipping\",\"timingPeriod\":{\"start\":\"2021-12-07\",\"end\":\"2026-12-07\"},\"reason\":\"Investigation\",\"goalId\":\"Larval_Dipping\",\"subjectCodableConcept\":{\"text\":\"Location\"},\"trigger\":[{\"type\":\"named-event\",\"name\":\"event-submission\",\"expression\":{\"description\":\"Trigger when a Register_Structure event is submitted\",\"expression\":\"questionnaire \\u003d \\u0027Register_Structure\\u0027\"}},{\"type\":\"named-event\",\"name\":\"plan-activation\"}],\"condition\":[{\"kind\":\"applicability\",\"expression\":{\"description\":\"Apply to larval breeding sites in Register_Structure questionnaires\",\"expression\":\"$this.is(FHIR.Location) or (questionnaire \\u003d \\u0027Register_Structure\\u0027 and $this.item.where(linkId\\u003d\\u0027structureType\\u0027).answer.value \\u003d\\u0027Larval Breeding Site\\u0027)\"}},{\"kind\":\"applicability\",\"expression\":{\"description\":\"Structure is a larval breeding site\",\"expression\":\"$this.is(FHIR.QuestionnaireResponse) or $this.type.where(id\\u003d\\u0027locationType\\u0027).text \\u003d \\u0027Larval Breeding Site\\u0027\"}}],\"definitionUri\":\"larval_dipping_form.json\",\"type\":\"create\"},{\"identifier\":\"b0a7b972-dfe9-42c9-b07a-54f7224ae144\",\"prefix\":7,\"title\":\"กิจกรรมการจับยุง\",\"description\":\"กิจกรรมจับยุงกำหนดไว้อย่างน้อย 3 แห่ง\",\"code\":\"Mosquito Collection\",\"timingPeriod\":{\"start\":\"2021-12-07\",\"end\":\"2026-12-07\"},\"reason\":\"Investigation\",\"goalId\":\"Mosquito_Collection\",\"subjectCodableConcept\":{\"text\":\"Location\"},\"trigger\":[{\"type\":\"named-event\",\"name\":\"plan-activation\"},{\"type\":\"named-event\",\"name\":\"event-submission\",\"expression\":{\"description\":\"Trigger when a Register_Structure event is submitted\",\"expression\":\"questionnaire \\u003d \\u0027Register_Structure\\u0027\"}}],\"condition\":[{\"kind\":\"applicability\",\"expression\":{\"description\":\"Structure is a mosquito collection point\",\"expression\":\"$this.is(FHIR.QuestionnaireResponse) or $this.type.where(id\\u003d\\u0027locationType\\u0027).text \\u003d \\u0027Mosquito Collection Point\\u0027\"}},{\"kind\":\"applicability\",\"expression\":{\"description\":\"Apply to mosquito collection point in Register_Structure questionnaires\",\"expression\":\"$this.is(FHIR.Location) or (questionnaire \\u003d \\u0027Register_Structure\\u0027 and $this.item.where(linkId\\u003d\\u0027structureType\\u0027).answer.value \\u003d\\u0027Mosquito Collection Point\\u0027)\"}}],\"definitionUri\":\"mosquito_collection_form.json\",\"type\":\"create\"}],\"experimental\":false}";

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
        Assert.assertEquals("b49faa39-5e24-4de8-bf3f-cdda6f320179", actualTemplates.get(0).getTemplate().getJurisdiction().get(0).getCode());
    }

    @Test
    public void testUpdateShouldUpdateExistingTemplate() {
        Template template = initTestTemplate();
        templateRepository.add(template);

        List<Template> actualTemplates = templateRepository.getAll();
        Assert.assertNotNull(actualTemplates);
        Assert.assertEquals(1, actualTemplates.size());
        Assert.assertEquals(1, actualTemplates.get(0).getTemplateId().intValue());
        Assert.assertEquals("b49faa39-5e24-4de8-bf3f-cdda6f320179", actualTemplates.get(0).getTemplate().getJurisdiction().get(0).getCode());

        template.setType("event");
        templateRepository.update(template);

        List<Template> updatedTemplates = templateRepository.getAll();
        Assert.assertNotNull(updatedTemplates);
        Assert.assertEquals(1, updatedTemplates.size());
        Template updatedTemplate = updatedTemplates.get(0);
        Assert.assertEquals("event", updatedTemplate.getType());
        Assert.assertEquals("b49faa39-5e24-4de8-bf3f-cdda6f320179", updatedTemplate.getTemplate().getJurisdiction().get(0).getCode());

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
        Assert.assertEquals("b49faa39-5e24-4de8-bf3f-cdda6f320179", actualTemplate.getTemplate().getJurisdiction().get(0).getCode());
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
        PlanTemplate planTemplate = gson.fromJson(planJson, PlanTemplate.class);
        Template template = new Template();
        template.setTemplate(planTemplate);
        template.setTemplateId(1);
        template.setType("1");
        return template;
    }

}
