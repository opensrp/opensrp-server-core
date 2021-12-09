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
import org.opensrp.util.DateTypeConverter;
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

    @BeforeClass
    public static void bootStrap() {
        tableNames= Arrays.asList("core.template");
    }

    @Override
    protected Set<String> getDatabaseScripts() {
        Set<String> scripts = new HashSet<>();
        return scripts;
    }

    String planJson = "{\"identifier\":\"f375baef-ba6c-4ce3-bdf2-b2c9323db34f\",\"version\":\"3\",\"name\":\"A1-ป่าละอู (7707060301)-จารุวัฒน์_จันทร์อุปถัมภ์-2021-09-13-Site\",\"title\":\"A1 - ป่าละอู (7707060301) - จารุวัฒน์ จันทร์อุปถัมภ์ - 2021-09-13 - Site\",\"status\":\"retired\",\"date\":\"2021-09-13\",\"effectivePeriod\":{\"start\":\"2021-09-13\",\"end\":\"2021-10-03\"},\"useContext\":[{\"code\":\"interventionType\",\"valueCodableConcept\":\"FI\"},{\"code\":\"fiStatus\",\"valueCodableConcept\":\"A1\"},{\"code\":\"fiReason\",\"valueCodableConcept\":\"Case Triggered\"},{\"code\":\"caseNum\",\"valueCodableConcept\":\"141311000000055210913110043058\"},{\"code\":\"opensrpEventId\",\"valueCodableConcept\":\"83b59fae-1727-4371-8e0e-71e52451d83a\"},{\"code\":\"taskGenerationStatus\",\"valueCodableConcept\":\"True\"},{\"code\":\"teamAssignmentStatus\",\"valueCodableConcept\":\"True\"}],\"jurisdiction\":[{\"code\":\"1fd869c8-f511-4322-a45f-88a52fcfa4de\"}],\"serverVersion\":1601948174008,\"goal\":[{\"id\":\"Case_Confirmation\",\"description\":\"ยืนยันบ้านผู้ป่วย\",\"priority\":\"medium-priority\",\"target\":[{\"measure\":\"จำนวนผู้ป่วยที่ได้รับการยืนยัน\",\"detail\":{\"detailQuantity\":{\"value\":1.0,\"comparator\":\"\\u0026amp;amp;amp;amp;gt;\\u003d\",\"unit\":\"case(s)\"}},\"due\":\"2021-09-23\"}]},{\"id\":\"RACD_register_families\",\"description\":\"ลงทะเบียนครัวเรือนและสมาชิกในครัวเรือน (100%) ภายในพื้นที่ปฏิบัติงาน\",\"priority\":\"medium-priority\",\"target\":[{\"measure\":\"ร้อยละของบ้าน/สิ่งปลูกสร้างที่ได้ลงทะเบียนข้อมูลครัวเรือน\",\"detail\":{\"detailQuantity\":{\"value\":100.0,\"comparator\":\"\\u0026amp;amp;amp;amp;gt;\\u003d\",\"unit\":\"Percent\"}},\"due\":\"2021-10-03\"}]},{\"id\":\"RACD_Blood_Screening\",\"description\":\"เจาะเลือดรอบบ้านผู้ป่วยในรัศมี 1 กิโลเมตร (100%)\",\"priority\":\"medium-priority\",\"target\":[{\"measure\":\"จำนวนผู้ที่ได้รับการเจาะโลหิต\",\"detail\":{\"detailQuantity\":{\"value\":50.0,\"comparator\":\"\\u0026amp;amp;amp;amp;gt;\\u003d\",\"unit\":\"Person(s)\"}},\"due\":\"2021-10-03\"}]},{\"id\":\"BCC_Focus\",\"description\":\"ให้สุขศึกษาในพื้นที่ปฏิบัติงานอย่างน้อย 1 ครั้ง\",\"priority\":\"medium-priority\",\"target\":[{\"measure\":\"จำนวนกิจกรรมการให้สุขศึกษา\",\"detail\":{\"detailQuantity\":{\"value\":1.0,\"comparator\":\"\\u0026amp;amp;amp;amp;gt;\\u003d\",\"unit\":\"activit(y|ies)\"}},\"due\":\"2021-10-03\"}]},{\"id\":\"RACD_bednet_distribution\",\"description\":\"แจกมุ้งทุกหลังคาเรือนในพื้นที่ปฏิบัติงาน (100%)\",\"priority\":\"medium-priority\",\"target\":[{\"measure\":\"จำนวนบ้าน/สิ่งปลูกสร้างที่ได้รับมุ้ง\",\"detail\":{\"detailQuantity\":{\"value\":90.0,\"comparator\":\"\\u0026amp;amp;amp;amp;gt;\\u003d\",\"unit\":\"Percent\"}},\"due\":\"2021-10-03\"}]},{\"id\":\"Larval_Dipping\",\"description\":\"ดำเนินกิจกรรมจับลูกน้ำอย่างน้อย 3 แห่งในพื้นที่ปฏิบัติงาน\",\"priority\":\"medium-priority\",\"target\":[{\"measure\":\"จำนวนกิจกรรมการตักลูกน้ำ\",\"detail\":{\"detailQuantity\":{\"value\":3.0,\"comparator\":\"\\u0026amp;amp;amp;amp;gt;\\u003d\",\"unit\":\"activit(y|ies)\"}},\"due\":\"2021-10-03\"}]},{\"id\":\"Mosquito_Collection\",\"description\":\"กิจกรรมจับยุงกำหนดไว้อย่างน้อย 3 แห่ง\",\"priority\":\"medium-priority\",\"target\":[{\"measure\":\"จำนวนกิจกรรมการจับยุง\",\"detail\":{\"detailQuantity\":{\"value\":3.0,\"comparator\":\"\\u0026amp;amp;amp;amp;gt;\\u003d\",\"unit\":\"activit(y|ies)\"}},\"due\":\"2021-10-03\"}]}],\"action\":[{\"identifier\":\"662d4bff-43cc-4f50-af1e-3bc86f8af253\",\"prefix\":1,\"title\":\"การยืนยันบ้านผู้ป่วย\",\"description\":\"ยืนยันบ้านผู้ป่วย\",\"code\":\"Case Confirmation\",\"timingPeriod\":{\"start\":\"2021-09-13\",\"end\":\"2021-09-23\"},\"reason\":\"Investigation\",\"goalId\":\"Case_Confirmation\",\"subjectCodableConcept\":{\"text\":\"Jurisdiction\"},\"taskTemplate\":\"Case_Confirmation\",\"type\":\"create\"},{\"identifier\":\"04415dc9-6493-4b41-9bab-35f5b7400dc4\",\"prefix\":2,\"title\":\"ลงทะเบียนครัวเรือน\",\"description\":\"ลงทะเบียนครัวเรือนและสมาชิกในครัวเรือน (100%) ภายในพื้นที่ปฏิบัติงาน\",\"code\":\"RACD Register Family\",\"timingPeriod\":{\"start\":\"2021-09-13\",\"end\":\"2021-10-03\"},\"reason\":\"Investigation\",\"goalId\":\"RACD_register_families\",\"subjectCodableConcept\":{\"text\":\"Location\"},\"taskTemplate\":\"RACD_register_families\",\"type\":\"create\"},{\"identifier\":\"c269f0a3-7dcc-4663-ac97-56ae5f3219f2\",\"prefix\":3,\"title\":\"กิจกรรมการเจาะโลหิต\",\"description\":\"เจาะเลือดรอบบ้านผู้ป่วยในรัศมี 1 กิโลเมตร (100%)\",\"code\":\"Blood Screening\",\"timingPeriod\":{\"start\":\"2021-09-13\",\"end\":\"2021-10-03\"},\"reason\":\"Investigation\",\"goalId\":\"RACD_Blood_Screening\",\"subjectCodableConcept\":{\"text\":\"Person\"},\"taskTemplate\":\"RACD_Blood_Screening\",\"type\":\"create\"},{\"identifier\":\"b7e23f43-6769-4e20-85ac-c38bfaf7888f\",\"prefix\":4,\"title\":\"กิจกรรมการให้สุขศึกษา\",\"description\":\"ดำเนินกิจกรรมให้สุขศึกษา\",\"code\":\"BCC\",\"timingPeriod\":{\"start\":\"2021-09-13\",\"end\":\"2021-10-03\"},\"reason\":\"Investigation\",\"goalId\":\"BCC_Focus\",\"subjectCodableConcept\":{\"text\":\"Jurisdiction\"},\"taskTemplate\":\"BCC_Focus\",\"type\":\"create\"},{\"identifier\":\"b13d4ee2-7362-4cab-9653-c50bf0db3bfd\",\"prefix\":5,\"title\":\"กิจกรรมสำรวจ/ชุบ/แจกมุ้ง\",\"description\":\"แจกมุ้งทุกหลังคาเรือนในพื้นที่ปฏิบัติงาน (100%)\",\"code\":\"Bednet Distribution\",\"timingPeriod\":{\"start\":\"2021-09-13\",\"end\":\"2021-10-03\"},\"reason\":\"Investigation\",\"goalId\":\"RACD_bednet_distribution\",\"subjectCodableConcept\":{\"text\":\"Location\"},\"taskTemplate\":\"Bednet_Distribution\",\"type\":\"create\"},{\"identifier\":\"247679b8-e52b-48c4-b8b9-483651dbb934\",\"prefix\":6,\"title\":\"กิจกรรมการตักลูกน้ำ\",\"description\":\"ดำเนินกิจกรรมจับลูกน้ำอย่างน้อย 3 แห่งในพื้นที่ปฏิบัติงาน\",\"code\":\"Larval Dipping\",\"timingPeriod\":{\"start\":\"2021-09-13\",\"end\":\"2021-10-03\"},\"reason\":\"Investigation\",\"goalId\":\"Larval_Dipping\",\"subjectCodableConcept\":{\"text\":\"Location\"},\"taskTemplate\":\"Larval_Dipping\",\"type\":\"create\"},{\"identifier\":\"e82e1207-657f-4f29-bef1-adb3ae42a794\",\"prefix\":7,\"title\":\"กิจกรรมการจับยุง\",\"description\":\"กิจกรรมจับยุงกำหนดไว้อย่างน้อย 3 แห่ง\",\"code\":\"Mosquito Collection\",\"timingPeriod\":{\"start\":\"2021-09-13\",\"end\":\"2021-10-03\"},\"reason\":\"Investigation\",\"goalId\":\"Mosquito_Collection\",\"subjectCodableConcept\":{\"text\":\"Location\"},\"taskTemplate\":\"Mosquito_Collection_Point\",\"type\":\"create\"}],\"experimental\":false}";

    @Test
    public void testAddShouldAddNewTemplate() {
        Template template = initTestTemplate();
        templateRepository.add(template);

        List<Template> actualTemplates = templateRepository.getAll();
        Assert.assertNotNull(actualTemplates);
        Assert.assertEquals(1, actualTemplates.get(0).getTemplateId().intValue());
        Assert.assertEquals("1fd869c8-f511-4322-a45f-88a52fcfa4de", actualTemplates.get(0).getTemplate().getJurisdiction().get(0).getCode());
    }

    @Test
    public void testUpdateShouldUpdateExistingTemplate() {
        Template template = initTestTemplate();
        templateRepository.add(template);

        List<Template> actualTemplates = templateRepository.getAll();
        Assert.assertNotNull(actualTemplates);
        Assert.assertEquals(1, actualTemplates.size());
        Assert.assertEquals(1, actualTemplates.get(0).getTemplateId().intValue());
        Assert.assertEquals("1fd869c8-f511-4322-a45f-88a52fcfa4de", actualTemplates.get(0).getTemplate().getJurisdiction().get(0).getCode());

        template.setType("event");
        templateRepository.update(template);

        List<Template> updatedTemplates = templateRepository.getAll();
        Assert.assertNotNull(updatedTemplates);
        Assert.assertEquals(1, updatedTemplates.size());
        Template updatedTemplate = updatedTemplates.get(0);
        Assert.assertEquals("event", updatedTemplate.getType());
        Assert.assertEquals("1fd869c8-f511-4322-a45f-88a52fcfa4de", updatedTemplate.getTemplate().getJurisdiction().get(0).getCode());

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
        Assert.assertEquals("1fd869c8-f511-4322-a45f-88a52fcfa4de", actualTemplate.getTemplate().getJurisdiction().get(0).getCode());
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
