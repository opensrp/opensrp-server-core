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

import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


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
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        super.setUp();
        ossMultimediaFileManager = new OSSMultimediaFileManager(multimediaRepository, clientService);
        OSSClientBuilder ossClientBuilder = mock(OSSClientBuilder.class);
        doReturn(new URI("http://region")).when(ossClient).getEndpoint();
        doReturn(ossClient).when(ossClientBuilder).getOssClient();
        Whitebox.setInternalState(ossMultimediaFileManager, "ossClientBuilder", ossClientBuilder);
    }

    @Test
    public void testPersistFileToStorageShouldPersistFileToOSS() throws IOException {
        Whitebox.setInternalState(ossMultimediaFileManager, "multimediaDirPath", "multimedia/directory/path");

        Whitebox.setInternalState(ossMultimediaFileManager, "objectStorageBucketName", "oss-bucket");
        Whitebox.setInternalState(ossMultimediaFileManager, "objectStorageBucketFolderPath", getTestFileFolder());

        byte[] data = "data".getBytes();
        ossMultimediaFileManager.persistFileToStorage("path/to/file", data);
        verify(ossClient).putObject(eq("oss-bucket"), stringArgumentCaptor.capture(), byteStreamArgumentCaptor.capture());
        assertEquals(ossMultimediaFileManager.getOSSObjectStorageFilePath("path/to/file"), stringArgumentCaptor.getValue());
        assertEquals(new String(data, "UTF-8"), IOUtils.toString(byteStreamArgumentCaptor.getValue(), StandardCharsets.UTF_8));
        verify(ossClient).shutdown();
    }

    @Test
    public void testRetrieveFileShouldRetrieveFileFromOSS() throws Exception {
        String testFilePath = getTestFilePath();
        Whitebox.setInternalState(ossMultimediaFileManager, "objectStorageBucketName", "oss-bucket");
        doReturn(true).when(ossClient).doesObjectExist(eq("oss-bucket"), eq(ossMultimediaFileManager.getOSSObjectStorageFilePath(testFilePath)));

        OSSObject ossObject = mock(OSSObject.class);
        InputStream inputStream = new FileInputStream(new File(getDataFilePath()));
        doReturn(inputStream).when(ossObject).getObjectContent();
        doReturn(ossObject).when(ossClient).getObject(stringArgumentCaptor.capture(), stringArgumentCaptor.capture());

        assertNotNull(ossMultimediaFileManager.retrieveFile(testFilePath));
        assertEquals(stringArgumentCaptor.getAllValues().get(0), "oss-bucket");
        assertEquals(stringArgumentCaptor.getAllValues().get(1), ossMultimediaFileManager.getOSSObjectStorageFilePath(testFilePath));
        assertFalse(isFileEmpty(testFilePath));
    }

    private boolean isFileEmpty(String path) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(path));
        return br.readLine() == null ? true : false;
    }

    @Test
    public void testRetrieveFileShouldReturnNullForNonExistentOSSFile() {
        Whitebox.setInternalState(ossMultimediaFileManager, "objectStorageBucketName", "oss-bucket");
        doReturn(false).when(ossClient).doesObjectExist("oss-bucket", "non_existent_file");
        assertNull(ossMultimediaFileManager.retrieveFile("non_existent_file"));
    }

    @Test
    public void testGetOSSClientShouldReturnNonNullOSSClient() throws Exception {
        Whitebox.setInternalState(ossMultimediaFileManager, "objectStorageRegion", "region");
        Whitebox.setInternalState(ossMultimediaFileManager, "objectStorageAccessKeyId", "region");
        Whitebox.setInternalState(ossMultimediaFileManager, "objectStorageSecretAccessKey", "awsSecretAccessKey");
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
