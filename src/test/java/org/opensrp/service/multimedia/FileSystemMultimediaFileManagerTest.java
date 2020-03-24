package org.opensrp.service.multimedia;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.opensrp.repository.MultimediaRepository;
import org.opensrp.service.ClientService;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;
import static org.utils.TestUtils.getBasePackageFilePath;

/**
 * Created by Vincent Karuri on 30/10/2019
 */
public class FileSystemMultimediaFileManagerTest extends BaseMultimediaFileManagerTest {

	private FileSystemMultimediaFileManager fileSystemMultimediaFileManager;

	@Value("#{opensrp['multimedia.directory.name']}")
	private String baseMultimediaDirPath;

	@Captor
	private ArgumentCaptor<File> fileArgumentCaptor = ArgumentCaptor.forClass(File.class);

	@Mock
	private ClientService clientService;

	@Mock
	private MultimediaRepository multimediaRepository;

	@Before
	public void setUp() throws IOException {
		MockitoAnnotations.initMocks(this);
		super.setUp();
		fileSystemMultimediaFileManager = new FileSystemMultimediaFileManager(multimediaRepository, clientService);
	}

	@Test
	public void testPersistFileToStorageShouldPersistFileToFileSystem() throws IOException {
		byte[] testBytes = new byte[10];
		fileSystemMultimediaFileManager = Mockito.spy(fileSystemMultimediaFileManager);
		fileSystemMultimediaFileManager.persistFileToStorage("file_name", testBytes);
		verify(fileSystemMultimediaFileManager).copyBytesToFile(fileArgumentCaptor.capture(), Mockito.eq(testBytes));

		File file = fileArgumentCaptor.getValue();
		assertNotNull(file);
		assertEquals(file.getPath(), "file_name");
	}

	@Test
	public void testRetrieveFileShouldRetrieveFileFromFileSystem() {
		String testFilePath = getBasePackageFilePath() + "/src/test/java/org/opensrp/service/multimedia/test_file";
		assertNotNull(fileSystemMultimediaFileManager.retrieveFile(testFilePath));
	}

	@Test
	public void testRetrieveFileShouldReturnNullForNonExistentFile() {
		assertNull(fileSystemMultimediaFileManager.retrieveFile("non_existent_file"));
	}

	@Test
	public void testGetMultiMediaDirShouldReturnCorrectFilePath() {
		assertEquals(baseMultimediaDirPath + File.separator, fileSystemMultimediaFileManager.getBaseMultiMediaDir());
	}
}
