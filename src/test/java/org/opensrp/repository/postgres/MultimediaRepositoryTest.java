package org.opensrp.repository.postgres;

import org.junit.Test;
import org.opensrp.domain.Multimedia;
import org.opensrp.repository.MultimediaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class MultimediaRepositoryTest extends BaseRepositoryTest {

    @Autowired
    @Qualifier("multimediaRepositoryPostgres")
    private MultimediaRepository multimediaRepository;

    @Override
    protected Set<String> getDatabaseScripts() {
        Set<String> scripts = new HashSet<String>();
        scripts.add("multimedia.sql");
        return scripts;
    }

    @Test
    public void testGet() {
        Multimedia multimedia = multimediaRepository.get("05934ae338431f28bf6793b241f0c5ca");
        assertEquals("040d4f18-8140-479c-aa21-725612073490", multimedia.getCaseId());
        assertEquals("/opt/multimedia/patient_images/040d4f18-8140-479c-aa21-725612073490.jpg", multimedia.getFilePath());
        assertEquals("profilepic", multimedia.getFileCategory());

        assertNull(multimediaRepository.get("05934ae338431f28bf6793b241fa"));
    }

    @Test
    public void testAdd() {
        Multimedia multimedia = new Multimedia("2332kkj-jhjmmn-23423423", "tester1", "image/jpeg", "/tmp/123.jpg",
                "thumbnail");
        multimediaRepository.add(multimedia);

        assertEquals(6, multimediaRepository.getAll().size());

        Multimedia savedEntity = multimediaRepository.findByCaseId("2332kkj-jhjmmn-23423423");
        assertEquals("/tmp/123.jpg", savedEntity.getFilePath());
        assertEquals("thumbnail", savedEntity.getFileCategory());
        assertEquals("image/jpeg", savedEntity.getContentType());
    }

    @Test
    public void testUpdate() {
        Multimedia multimedia = multimediaRepository.get("3157f9339bf0c948dd5d12aff82111e1");
        multimedia.setContentType("image/png");
        multimedia.setFilePath("/opt/images/783434-34534.png");
        multimediaRepository.update(multimedia);

        Multimedia updatedEntity = multimediaRepository.get("3157f9339bf0c948dd5d12aff82111e1");
        assertEquals("/opt/images/783434-34534.png", updatedEntity.getFilePath());
        assertEquals("image/png", updatedEntity.getContentType());
    }

    @Test
    public void testGetAll() {
        assertEquals(5, multimediaRepository.getAll().size());

        Multimedia multimedia = new Multimedia("2332kkj-76385430sdfsd-23423423", "tester23", "image/jpeg",
                "/tmp/b7jhkh23.jpg", "thumbnail");
        multimediaRepository.add(multimedia);

        List<String> expectedClients = Arrays.asList("2332kkj-76385430sdfsd-23423423",
                "87dc3230-84f7-4088-b257-e8b3130ab86b", "24eec0d8-e0ee-4f22-9d6b-3cca84bdefcf");
        int found = 0;
        List<Multimedia> multimediaList = multimediaRepository.getAll();
        assertEquals(6, multimediaList.size());
        for (Multimedia media : multimediaList)
            if (expectedClients.contains(media.getCaseId()))
                found++;
        assertEquals(3, found);

    }

    @Test
    public void testSafeRemove() {

        Multimedia multimedia = multimediaRepository.get("3157f9339bf0c948dd5d12aff82111e1");
        multimediaRepository.safeRemove(multimedia);

        List<Multimedia> multimediaList = multimediaRepository.getAll();
        assertEquals(4, multimediaList.size());
        for (Multimedia media : multimediaList)
            assertNotEquals("3157f9339bf0c948dd5d12aff82111e1", media.getId());

        assertNull(multimediaRepository.get("3157f9339bf0c948dd5d12aff82111e1"));

    }

    @Test
    public void testFindByCaseId() {
        Multimedia multimedia = multimediaRepository.findByCaseId("87dc3230-84f7-4088-b257-e8b3130ab86b");
        assertEquals("091488163b6ecd589a915372a0ad3b0d", multimedia.getId());
        assertEquals("/opt/multimedia/patient_images/87dc3230-84f7-4088-b257-e8b3130ab86b.jpg", multimedia.getFilePath());
        assertEquals("profilepic", multimedia.getFileCategory());

        assertNull(multimediaRepository.findByCaseId("05934ae338431f28bf6793b241fa"));
    }

    @Test
    public void testAll() {
        assertEquals(4, multimediaRepository.all("biddemo").size());

        List<Multimedia> multimedia = multimediaRepository.all("tester11");
        assertEquals(1, multimedia.size());
        assertEquals("317f8db1bb6cc4b15ecc9993a2922f47", multimedia.get(0).getId());
        assertEquals("24eec0d8-e0ee-4f22-9d6b-3cca84bdefcf", multimedia.get(0).getCaseId());
    }

    @Test
    public void testGetExtended() {
        Multimedia multimedia = new Multimedia("caseId1", "providerId1", "contentType1", "filePath1", "fileCategory1");
        multimediaRepository.add(multimedia);
        multimedia = new Multimedia("caseId1", "providerId1", "contentType1", "filePath2", "fileCategory1");
        multimediaRepository.add(multimedia);

        List<Multimedia> multimediaFiles = multimediaRepository.get("caseId1", "contentType1", "fileCategory1");
        assertEquals(multimediaFiles.size(), 2);

        String filePath1 = multimediaFiles.get(0).getFilePath();
        String filePath2 = multimediaFiles.get(1).getFilePath();
        assertTrue("filePath1".equals(filePath1) || "filePath2".equals(filePath1));
        assertTrue("filePath1".equals(filePath2) || "filePath2".equals(filePath2));
    }
}
