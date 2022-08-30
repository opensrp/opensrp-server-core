package org.opensrp;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.opensrp.domain.Multimedia;
import org.opensrp.dto.form.MultimediaDTO;
import org.opensrp.repository.postgres.MultimediaRepositoryImpl;
import org.opensrp.service.ClientService;
import org.opensrp.service.MultimediaService;
import org.opensrp.service.multimedia.BaseMultimediaFileManager;
import org.powermock.reflect.Whitebox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-applicationContext-opensrp.xml")
public class MultimediaServiceTest {

    @Autowired
    private MultimediaService multimediaService;

    @Mock
    private MultimediaRepositoryImpl multimediaRepository;

    @Mock
    private ClientService clientService;

    @Autowired
    @Qualifier("multimedia.file.manager")
    private BaseMultimediaFileManager fileManager;

    @Before
    public void setUp() {
        initMocks(this);
        multimediaService = new MultimediaService(multimediaRepository, clientService);
    }

    @Test
    public void testGetMultimediaFilesShouldReturnAllClientMultimediaFiles() {
        List<Multimedia> multimediaFiles = new ArrayList<>();
        multimediaFiles.add(new Multimedia("caseId1", "provideId1", "contentType1", "filePath1", "fileCategory1"));
        multimediaFiles.add(new Multimedia("caseId2", "provideId2", "contentType2", "filePath2", "fileCategory2"));
        doReturn(multimediaFiles).when(multimediaRepository).get(anyString(), anyString(), anyString());
        List<Multimedia> result = multimediaService.getMultimediaFiles("entityId", "contentType", "fileCategory");
        assertEquals(result.size(), 2);
        assertEquals(result.get(0).getCaseId(), "caseId1");
        assertEquals(result.get(1).getCaseId(), "caseId2");
    }

    @Test
    public void testUploadFileShouldSetCorrectFilePath() {
        final String BASE_MULTIMEDIA_DIR_PATH = "baseMultimediaDirPath";
        Whitebox.setInternalState(fileManager, "baseMultimediaDirPath", BASE_MULTIMEDIA_DIR_PATH);

        MultimediaDTO multimedia = new MultimediaDTO("caseId1", "provideId1", "image/jpeg", "filePath1", "multi_version");
        byte[] testBytes = new byte[10];
        String originalFileName = "original_file_name";
        //doReturn("original_file_name").when(multipartFile).getOriginalFilename();
        fileManager.uploadFile(multimedia, testBytes, originalFileName);
        assertEquals(multimedia.getFilePath(), BASE_MULTIMEDIA_DIR_PATH + "/patient_images/caseId1/original_file_name");

        multimedia = new MultimediaDTO("caseId1", "provideId1", "image/jpeg", "filePath1", "profileimage");
        fileManager.uploadFile(multimedia, new byte[10], originalFileName);
        assertEquals(multimedia.getFilePath(), BASE_MULTIMEDIA_DIR_PATH + "/patient_images/caseId1.jpg");
    }

    @Test
    public void testGetFileExtension() {
        assertEquals(".csv", fileManager.getFileExtension("text.csv"));
        assertEquals("", fileManager.getFileExtension("text"));
        assertEquals(".gz", fileManager.getFileExtension("sample.tr.gz"));
    }
}
