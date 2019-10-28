package org.opensrp;

import org.hamcrest.text.pattern.PatternMatcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.opensrp.domain.BaseMultimediaFileManager;
import org.opensrp.domain.Multimedia;
import org.opensrp.dto.form.MultimediaDTO;
import org.opensrp.repository.couch.MultimediaRepositoryImpl;
import org.opensrp.service.ClientService;
import org.opensrp.service.MultimediaService;
import org.powermock.reflect.Whitebox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.text.pattern.Patterns.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-applicationContext-opensrp.xml")
public class MultimediaServiceTest {

	@Autowired
	private MultimediaService multimediaService;
	
	@Mock
	private MultimediaRepositoryImpl multimediaRepository;
	
	@Mock
	private ClientService clientService;

	private BaseMultimediaFileManager fileManager;
	
	@Before
	public void setUp() {
		initMocks(this);
		multimediaService = new MultimediaService(multimediaRepository, clientService);
		fileManager = (BaseMultimediaFileManager) multimediaService.getFileManager();
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
		Whitebox.setInternalState(multimediaService, "baseMultimediaDirPath", BASE_MULTIMEDIA_DIR_PATH );

		MultimediaDTO multimedia = new MultimediaDTO("caseId1", "provideId1", "image/jpeg", "filePath1", "multi_version");
		fileManager.uploadFile(multimedia, mock(MultipartFile.class));
		PatternMatcher matcher = new PatternMatcher(sequence(text(BASE_MULTIMEDIA_DIR_PATH  + "/patient_images/caseId1/"), oneOrMore(anyCharacter()), text(".jpg")));
		assertThat(multimedia.getFilePath(), matcher);

		multimedia = new MultimediaDTO("caseId1", "provideId1", "image/jpeg", "filePath1", "profileimage");
		fileManager.uploadFile(multimedia, mock(MultipartFile.class));
		assertEquals(multimedia.getFilePath(), BASE_MULTIMEDIA_DIR_PATH  + "/patient_images/caseId1.jpg");
	}
}
