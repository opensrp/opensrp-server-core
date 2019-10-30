package org.opensrp.service.multimedia;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.opensrp.repository.MultimediaRepository;
import org.opensrp.service.ClientService;
import org.powermock.reflect.Whitebox;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.utils.TestUtils.getBasePackageFilePath;

/**
 * Created by Vincent Karuri on 30/10/2019
 */
public class S3MultimediaFileManagerTest {

	private S3MultimediaFileManager s3MultimediaFileManager;

	@Captor
	private ArgumentCaptor<File> fileArgumentCaptor = ArgumentCaptor.forClass(File.class);

	@Captor
	private ArgumentCaptor<GetObjectRequest> getObjectRequestArgumentCaptor = ArgumentCaptor.forClass(GetObjectRequest.class);

	@Captor
	private ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);

	@Captor
	private ArgumentCaptor<PutObjectRequest> putObjectRequestArgumentCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);

	@Captor
	private ArgumentCaptor<InputStream> inputStreamArgumentCaptor = ArgumentCaptor.forClass(InputStream.class);

	@Captor
	private ArgumentCaptor<ObjectMetadata> objectMetadataArgumentCaptor = ArgumentCaptor.forClass(ObjectMetadata.class);

	@Mock
	private ClientService clientService;

	@Mock
	private MultimediaRepository multimediaRepository;

	@Mock
	private AmazonS3Client s3Client;

	@Before
	public void setUp() throws IOException {
		MockitoAnnotations.initMocks(this);
		createTestFile();
		s3MultimediaFileManager = new S3MultimediaFileManager(multimediaRepository, clientService);
		Whitebox.setInternalState(s3MultimediaFileManager, "s3Client", s3Client);
	}

	@Test
	public void testPersistFileToStorageShouldPersistFileToS3() throws IOException {
		Whitebox.setInternalState(s3MultimediaFileManager, "s3Bucket", "s3Bucket");

		MultipartFile multipartFile = mock(MultipartFile.class);
		s3MultimediaFileManager.persistFileToStorage(getTestFilePath(), multipartFile);
		verify(multipartFile).transferTo(fileArgumentCaptor.capture());

		verify(s3Client).putObject(putObjectRequestArgumentCaptor.capture());

		PutObjectRequest putObjectRequest = putObjectRequestArgumentCaptor.getValue();
		assertEquals(putObjectRequest.getBucketName(), "s3Bucket");
		assertEquals(putObjectRequest.getKey(), getTestFilePath());

		File file = fileArgumentCaptor.getValue();
		assertNotNull(file);
		assertEquals(file.getPath(), getTestFilePath());

		// ensure it cleans up temp file
		assertFalse(new File(getTestFilePath()).exists());
	}

	@Test
	public void testRetrieveFileShouldRetrieveFileFromS3() {
		String testFilePath = getTestFilePath();
		Whitebox.setInternalState(s3MultimediaFileManager, "s3Bucket", "s3Bucket");
		doReturn(true).when(s3Client).doesObjectExist("s3Bucket", testFilePath);
		doReturn(mock(ObjectMetadata.class)).when(s3Client).getObject(getObjectRequestArgumentCaptor.capture(), fileArgumentCaptor.capture());
		assertNotNull(s3MultimediaFileManager.retrieveFile(testFilePath));

		File file = fileArgumentCaptor.getValue();
		assertNotNull(file);
		assertEquals(testFilePath, file.getPath());

		GetObjectRequest getObjectRequest = getObjectRequestArgumentCaptor.getValue();
		assertNotNull(getObjectRequest);
		assertEquals(getObjectRequest.getBucketName(), "s3Bucket");
		assertEquals(getObjectRequest.getKey(), testFilePath);
	}

	@Test
	public void testRetrieveFileShouldReturnNullForNonExistentS3File() {
		Whitebox.setInternalState(s3MultimediaFileManager, "s3Bucket", "s3Bucket");
		doReturn(false).when(s3Client).doesObjectExist("s3Bucket", "non_existent_file");
		assertNull(s3MultimediaFileManager.retrieveFile("non_existent_file"));
	}

	private String getTestFilePath() {
		return getBasePackageFilePath() + "/src/test/java/org/opensrp/service/multimedia/test_file";
	}

	private void createTestFile() throws IOException {
		PrintWriter writer = new PrintWriter(getTestFilePath(), "UTF-8");
		writer.println("The first line");
		writer.close();
	}
}
