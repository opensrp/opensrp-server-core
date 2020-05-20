package org.opensrp.service.multimedia;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.OSSObject;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.opensrp.repository.MultimediaRepository;
import org.opensrp.service.ClientService;
import org.powermock.reflect.Whitebox;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;


/**
 * Created by Vincent Karuri on 20/05/2020
 */
public class OSSMultimediaFileManagerTest extends BaseMultimediaFileManagerTest {
	
	private OSSMultimediaFileManager ossMultimediaFileManager;

	@Captor
	private ArgumentCaptor<ByteArrayInputStream> byteStreamArgumentCaptor = ArgumentCaptor.forClass(ByteArrayInputStream.class);

	@Captor
	private ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);

	@Mock
	private ClientService clientService;

	@Mock
	private MultimediaRepository multimediaRepository;

	@Mock
	private OSSClient ossClient;

	@Before
	public void setUp() throws IOException {
		MockitoAnnotations.initMocks(this);
		super.setUp();
		ossMultimediaFileManager = new OSSMultimediaFileManager(multimediaRepository, clientService);
		Whitebox.setInternalState(ossMultimediaFileManager, "ossClient", ossClient);
	}

	@Test
	public void testPersistFileToStorageShouldPersistFileToOSS() throws IOException {
		Whitebox.setInternalState(ossMultimediaFileManager, "multimediaDirPath", "multimedia/directory/path");

		Whitebox.setInternalState(ossMultimediaFileManager, "objectStorageBucketName", "ossBucket");
		Whitebox.setInternalState(ossMultimediaFileManager, "objectStorageBucketFolderPath", getTestFileFolder());

		byte[] data = "data".getBytes();
		ossMultimediaFileManager.persistFileToStorage("file_name", data);
		verify(ossClient).putObject(eq("ossBucket"), eq(getTestFileFolder() + File.separator + "file_name"), byteStreamArgumentCaptor.capture());
		assertEquals(new String(data, "UTF-8"), IOUtils.toString(byteStreamArgumentCaptor.getValue(), StandardCharsets.UTF_8));
		verify(ossClient).shutdown();
	}

	@Test
	public void testRetrieveFileShouldRetrieveFileFromOSS() throws Exception {
		String testFilePath = getTestFilePath();
		Whitebox.setInternalState(ossMultimediaFileManager, "objectStorageBucketName", "ossBucket");
		doReturn(true).when(ossClient).doesObjectExist("ossBucket", testFilePath);

		OSSObject ossObject = mock(OSSObject.class);
		InputStream inputStream = new FileInputStream(new File(getDataFilePath()));
		doReturn(inputStream).when(ossObject).getObjectContent();
		doReturn(ossObject).when(ossClient).getObject(stringArgumentCaptor.capture(), stringArgumentCaptor.capture());

		assertNotNull(ossMultimediaFileManager.retrieveFile(testFilePath));
		assertEquals(stringArgumentCaptor.getAllValues().get(0), "ossBucket");
		assertEquals(stringArgumentCaptor.getAllValues().get(1), testFilePath);
		assertFalse(isFileEmpty(testFilePath));
	}

	private boolean isFileEmpty(String path) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(path));
		return br.readLine() == null ? true : false;
	}
	
	@Test
	public void testRetrieveFileShouldReturnNullForNonExistentOSSFile() {
		Whitebox.setInternalState(ossMultimediaFileManager, "objectStorageBucketName", "ossBucket");
		doReturn(false).when(ossClient).doesObjectExist("ossBucket", "non_existent_file");
		assertNull(ossMultimediaFileManager.retrieveFile("non_existent_file"));
	}

	@Test
	public void testGetOSSClientShouldReturnNonNullOSSClient() throws Exception {
		Whitebox.setInternalState(ossMultimediaFileManager, "objectStorageRegion", "region");
		Whitebox.setInternalState(ossMultimediaFileManager, "objectStorageAccessKeyId", "region");
		Whitebox.setInternalState(ossMultimediaFileManager, "objectStorageSecretAccessKey", "awsSecretAccessKey");
		Whitebox.setInternalState(ossMultimediaFileManager, "ossClient", (Object[]) null);
		OSSClient ossClient = Whitebox.invokeMethod(ossMultimediaFileManager, "getOssClient");
		assertNotNull(ossClient);
		assertEquals("http://region", ossClient.getEndpoint().toString());
	}

	@Test
	public void testGetMultiMediaDirShouldReturnCorrectFilePath() {
		assertEquals(File.separator + "tmp" + File.separator, ossMultimediaFileManager.getBaseMultiMediaDir());
	}

	@Test
	public void testGetOSSFilePathShouldReturnCorrectFilePath() throws Exception {
		Whitebox.setInternalState(ossMultimediaFileManager, "objectStorageBucketFolderPath", "bucket_folder_path");
		Whitebox.setInternalState(ossMultimediaFileManager, "baseMultimediaDirPath", "base/directory/path");
		String truncatedFilePath = Whitebox.invokeMethod(ossMultimediaFileManager, "getObjectStorageFilePath", ossMultimediaFileManager.getBaseMultiMediaDir() + "path/to/file");
		assertEquals("bucket_folder_path" + File.separator + "path/to/file", truncatedFilePath);
	}
}
