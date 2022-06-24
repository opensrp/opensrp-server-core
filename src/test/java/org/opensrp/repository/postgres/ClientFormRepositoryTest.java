package org.opensrp.repository.postgres;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.opensrp.domain.postgres.ClientForm;
import org.opensrp.domain.postgres.ClientFormMetadata;
import org.opensrp.repository.ClientFormRepository;
import org.opensrp.service.ClientFormService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class ClientFormRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private ClientFormRepository clientFormRepository;

    private Set<String> scripts = new HashSet<String>();

    @Override
    protected Set<String> getDatabaseScripts() {
        scripts.add("client_form.sql");
        return scripts;
    }

    @Test
    public void testGet() {
        assertNotNull(clientFormRepository.get(5));
    }

    @Test
    public void testCountClientFormByFormIdentifier() {
        assertEquals(5, clientFormRepository.countClientFormByFormIdentifier("json.form/adverse_event.json"));
    }

    @Test
    public void testGetClientFormMetadataFromIdentifierAndVersion() {
        ClientFormMetadata clientFormMetadata = clientFormRepository
                .getClientFormMetadata("0.0.1", "json.form/adverse_event.json");
        assertEquals("AEFI", clientFormMetadata.getLabel());
        assertEquals("child", clientFormMetadata.getModule());
    }

    @Test
    public void testGetAvailableClientFormVersions() {
        assertEquals(5, clientFormRepository.getAvailableClientFormVersions("json.form/adverse_event.json").size());
    }

    @Test
    public void testGetFormMetadata() {
        ClientFormMetadata clientFormMetadata = clientFormRepository.getFormMetadata(3);
        assertEquals("child", clientFormMetadata.getModule());
        assertEquals("0.1.0", clientFormMetadata.getVersion());
        assertEquals("json.form/adverse_event.json", clientFormMetadata.getIdentifier());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateShouldReturnNull() {
        ClientForm clientForm = new ClientForm();
        clientForm.setId(2L);
        clientFormRepository.create(clientForm, new ClientFormMetadata());
    }

    @Test
    public void testCreateShouldReturnValidObject() {
        ClientForm clientForm = new ClientForm();
        clientForm.setCreatedAt(new Date());
        clientForm.setJson("{'from': 'child'}");

        ClientFormMetadata clientFormMetadata = new ClientFormMetadata();
        clientFormMetadata.setModule("child");
        clientFormMetadata.setVersion("1.0.0");
        clientFormMetadata.setIdentifier("json.form/child/sample.json");
        clientFormMetadata.setLabel("SAMPLE FORM");
        clientFormMetadata.setCreatedAt(new Date());

        ClientFormService.CompleteClientForm completeClientForm = clientFormRepository
                .create(clientForm, clientFormMetadata);

        assertEquals(7, (long) completeClientForm.clientForm.getId());
        assertEquals(7, (long) completeClientForm.clientFormMetadata.getId());
    }

    @Test
    public void testFormRelationshipIsNullByDefault() {
        ClientFormMetadata clientFormMetadata = clientFormRepository.getFormMetadata(3);
        assertNull(clientFormMetadata.getRelation());
    }

    @Test
    public void canCreateMetadataWithFormRelationship() {
        ClientForm clientForm = new ClientForm();
        clientForm.setCreatedAt(new Date());
        clientForm.setJson("calculation =  helper.getDuration(step1_date_died , step1_dob)");

        ClientFormMetadata clientFormMetadata = new ClientFormMetadata();
        clientFormMetadata.setModule("anc");
        clientFormMetadata.setVersion("1.0.0");
        clientFormMetadata.setIdentifier("rule/sample_calc.yml");
        clientFormMetadata.setRelation("json.form/anc/sample.json");
        clientFormMetadata.setLabel("SAMPLE CALC FORM");
        clientFormMetadata.setCreatedAt(new Date());

        ClientFormService.CompleteClientForm completeClientForm = clientFormRepository
                .create(clientForm, clientFormMetadata);

        assertEquals(7, (long) completeClientForm.clientForm.getId());
        assertEquals("json.form/anc/sample.json", completeClientForm.clientFormMetadata.getRelation());
    }

    @Test
    public void testGetAll() {
        assertEquals(6, clientFormRepository.getAll().size());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testAdd() {
        ClientForm clientForm = new ClientForm();
        clientForm.setCreatedAt(new Date());
        clientForm.setJson("{}");

        clientFormRepository.add(clientForm);
    }

    @Test
    public void testGetShouldReturnNullWhenIdIsEmpty() {
        assertNull(clientFormRepository.get(null));
    }

    @Test
    public void testGetShouldReturnNullWhenIdIsNotValidLong() {
        assertNull(clientFormRepository.get("isd98"));
    }

    @Test
    public void testGetShouldValidForm() throws JsonProcessingException {
        ClientForm clientForm = clientFormRepository.get("1");

        ObjectMapper mapper = new ObjectMapper();
        JsonNode tree1 = mapper.readTree((String) clientForm.getJson());
        JsonNode tree2 = mapper.readTree("{\"count\":\"1\",\"encounter_type\":\"AEFI\",\"entity_id\":\"\",\"metadata\":{\"start\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"start\",\"openmrs_entity_id\":\"163137AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"end\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"end\",\"openmrs_entity_id\":\"163138AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"today\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"encounter\",\"openmrs_entity_id\":\"encounter_date\"},\"deviceid\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"deviceid\",\"openmrs_entity_id\":\"163149AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"subscriberid\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"subscriberid\",\"openmrs_entity_id\":\"163150AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"simserial\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"simserial\",\"openmrs_entity_id\":\"163151AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"phonenumber\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"phonenumber\",\"openmrs_entity_id\":\"163152AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"encounter_location\":\"\"},\"step1\":{\"title\":\"Adverse Event Reporting\",\"fields\":[{\"key\":\"Reaction_Vaccine\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"6042AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_data_type\":\"select one\",\"type\":\"spinner\",\"hint\":\"Vaccine that caused the reaction\",\"values\":[\"BCG\",\"HepB\",\"OPV\",\"Penta\",\"PCV\",\"Rota\",\"Measles\",\"MR\",\"Yellow Fever\"],\"openmrs_choice_ids\":{\"BCG\":\"149310AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"HepB\":\"162269AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"OPV\":\"129578AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"Penta\":\"162265AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"PCV\":\"162266AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"Rota\":\"162272AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"Measles\":\"149286AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"MR\":\"149286AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"Yellow Fever\":\"149253AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"v_required\":{\"value\":true,\"err\":\"Please enter the vaccine that caused the reaction\"}},{\"key\":\"aefi_start_date\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"type\":\"date_picker\",\"hint\":\"Date the adverse effects began\",\"expanded\":false,\"v_required\":{\"value\":true,\"err\":\"Please enter the date the adverse effects began\"}},{\"key\":\"reaction\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"openmrs_data_type\":\"\",\"type\":\"check_box\",\"label\":\"Select the reaction\",\"hint\":\"Select the reaction\",\"label_text_style\":\"bold\",\"options\":[{\"key\":\"bacteria_abscesses\",\"text\":\"Minor AEFI Bacteria abscesses\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\"},{\"key\":\"lymphadenitis\",\"text\":\"Minor AEFI Lymphadenitis\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\"},{\"key\":\"sepsis\",\"text\":\"Minor AEFI Sepsis\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\"},{\"key\":\"local_reaction\",\"text\":\"Minor AEFI Severe local reaction\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\"},{\"key\":\"birth_defect\",\"text\":\"Serious AEFI Birth Defect\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\"},{\"key\":\"death\",\"text\":\"Serious AEFI Death\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\"},{\"key\":\"encephalopathy\",\"text\":\"Serious AEFI Encephalopathy\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\"},{\"key\":\"high_fever \",\"text\":\"Serious AEFI High fever > 38 Degrees Celcius\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\"},{\"key\":\"paralysis\",\"text\":\"Serious AEFI Paralysis\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\"},{\"key\":\"seizures\",\"text\":\"Serious AEFI Seizures\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\"},{\"key\":\"birth_defect\",\"text\":\"Serious AEFI Significant Disability\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\"},{\"key\":\"toxic_shock_syndrome\",\"text\":\"Serious AEFI Toxic shock syndrome\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\"},{\"key\":\"other\",\"text\":\"Other (specify)\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\"}],\"v_required\":{\"value\":false,\"err\":\"Please select at least one reaction\"}},{\"key\":\"other_reaction\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"openmrs_data_type\":\"text\",\"type\":\"edit_text\",\"hint\":\"Other Reaction\",\"relevance\":{\"step1:reaction\":{\"ex-checkbox\":[{\"or\":[\"other\"]}]}}},{\"key\":\"child_referred\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"163340AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_data_type\":\"select one\",\"type\":\"native_radio\",\"label\":\"Child Referred?\",\"label_text_style\":\"bold\",\"options\":[{\"key\":\"Yes\",\"text\":\"Yes\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"1267AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"No\",\"text\":\"No\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"163339AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}]},{\"key\":\"aefi_form\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"163340AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_data_type\":\"select one\",\"type\":\"native_radio\",\"label\":\"Was the AEFI form completed?\",\"label_text_style\":\"bold\",\"options\":[{\"key\":\"Yes\",\"text\":\"Yes\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"1267AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"No\",\"text\":\"No\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"163339AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}]}]}}");

        assertEquals(tree1, tree2);
    }

    @Test
    public void testSafeRemove() {
        assertNotNull(clientFormRepository.get(1));

        ClientForm clientForm = new ClientForm();
        clientForm.setId(1L);

        clientFormRepository.safeRemove(clientForm);

        assertNull(clientFormRepository.get(1L));
        assertNull(clientFormRepository.getClientFormMetadata("0.0.1", "json.form/adverse_event.json"));
    }

}
