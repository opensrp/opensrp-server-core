package org.opensrp.service.multimedia;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.opensrp.repository.MultimediaRepository;
import org.opensrp.service.ClientService;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class ObjectStorageMultimediaFileManagerTest extends BaseMultimediaFileManagerTest {

	@Value("#{opensrp['object.storage.bucket.folder.path'] ?: ''}")
	protected String objectStorageBucketFolderPath;

	@Mock
	private ClientService clientService;

	@Mock
	private MultimediaRepository multimediaRepository;

	private ObjectStorageMultimediaFileManager objectStorageMultimediaFileManager;

	private String LOCAL_FILE_PATH = "/test";

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		super.setUp();
		objectStorageMultimediaFileManager = new ObjectStorageMultimediaFileManager(multimediaRepository, clientService) {

			@Override
			protected void persistFileToStorage(String fileName, byte[] fileBytes) throws IOException {
               // do nothing
			}

			@Override
			public File retrieveFile(String filePath) {
				return null;
			}
		};
	}

	@Test
	public void testGetBaseMultimediaDir() {
		assertEquals(File.separator + "tmp" + File.separator, objectStorageMultimediaFileManager.getBaseMultiMediaDir());
	}

	@Test
	public void testGetObjectStorageFilePath() {
		assertEquals(StringUtils.isBlank(LOCAL_FILE_PATH) ?
						"" :
						objectStorageBucketFolderPath + File.separator + LOCAL_FILE_PATH
								.replace(objectStorageMultimediaFileManager.getBaseMultiMediaDir(), ""),
				objectStorageMultimediaFileManager.getObjectStorageFilePath(LOCAL_FILE_PATH));
	}

}
