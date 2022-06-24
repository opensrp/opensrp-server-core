package org.opensrp.service.multimedia;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import org.opensrp.repository.MultimediaRepository;
import org.opensrp.service.ClientService;
import org.powermock.reflect.Whitebox;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Created by Vincent Karuri on 30/10/2019
 */
public class S3MultimediaFileManagerTest extends BaseMultimediaFileManagerTest {

    private S3MultimediaFileManager s3MultimediaFileManager;

    @Captor
    private ArgumentCaptor<File> fileArgumentCaptor = ArgumentCaptor.forClass(File.class);

    @Captor
    private ArgumentCaptor<GetObjectRequest> getObjectRequestArgumentCaptor = ArgumentCaptor.forClass(GetObjectRequest.class);

    @Captor
    private ArgumentCaptor<PutObjectRequest> putObjectRequestArgumentCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);

    @Mock
    private ClientService clientService;

    @Mock
    private MultimediaRepository multimediaRepository;

    @Mock
    private AmazonS3Client s3Client;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        super.setUp();
        s3MultimediaFileManager = new S3MultimediaFileManager(multimediaRepository, clientService);
        Whitebox.setInternalState(s3MultimediaFileManager, "s3Client", s3Client);
    }

    @Test
    public void testPersistFileToStorageShouldPersistFileToS3() throws IOException {
        Whitebox.setInternalState(s3MultimediaFileManager, "multimediaDirPath", "multimedia/directory/path");

        Whitebox.setInternalState(s3MultimediaFileManager, "objectStorageBucketName", "s3Bucket");
        Whitebox.setInternalState(s3MultimediaFileManager, "objectStorageBucketFolderPath", getTestFileFolder());

        byte[] testBytes = new byte[10];
        s3MultimediaFileManager = Mockito.spy(s3MultimediaFileManager);
        s3MultimediaFileManager.persistFileToStorage(getTestFilePath(), testBytes);

        verify(s3MultimediaFileManager).copyBytesToFile(fileArgumentCaptor.capture(), Mockito.eq(testBytes));
        verify(s3Client).putObject(putObjectRequestArgumentCaptor.capture());

        PutObjectRequest putObjectRequest = putObjectRequestArgumentCaptor.getValue();
        assertEquals(putObjectRequest.getBucketName(), "s3Bucket");
        assertEquals(putObjectRequest.getKey(), getTestFileFolder() + File.separator + getTestFilePath());

        File file = fileArgumentCaptor.getValue();
        assertNotNull(file);
        assertEquals(file.getPath(), getTestFilePath());

        // ensure it cleans up temp file
        assertFalse(new File(getTestFilePath()).exists());
    }

    @Test
    public void testRetrieveFileShouldRetrieveFileFromS3() {
        String testFilePath = getTestFilePath();
        Whitebox.setInternalState(s3MultimediaFileManager, "objectStorageBucketFolderPath", "opensrp");
        Whitebox.setInternalState(s3MultimediaFileManager, "objectStorageBucketName", "s3Bucket");
        doReturn(true).when(s3Client).doesObjectExist(eq("s3Bucket"), eq(s3MultimediaFileManager.getObjectStorageFilePath(testFilePath)));
        doReturn(mock(ObjectMetadata.class)).when(s3Client).getObject(getObjectRequestArgumentCaptor.capture(), fileArgumentCaptor.capture());
        assertNotNull(s3MultimediaFileManager.retrieveFile(testFilePath));

        File file = fileArgumentCaptor.getValue();
        assertNotNull(file);
        assertEquals(testFilePath, file.getPath());

        GetObjectRequest getObjectRequest = getObjectRequestArgumentCaptor.getValue();
        assertNotNull(getObjectRequest);
        assertEquals(getObjectRequest.getBucketName(), "s3Bucket");
        assertEquals(getObjectRequest.getKey(), "opensrp/" + testFilePath);
    }

    @Test
    public void testRetrieveFileShouldReturnNullForNonExistentS3File() {
        Whitebox.setInternalState(s3MultimediaFileManager, "objectStorageBucketName", "s3Bucket");
        doReturn(false).when(s3Client).doesObjectExist("s3Bucket", "non_existent_file");
        assertNull(s3MultimediaFileManager.retrieveFile("non_existent_file"));
    }

    @Test
    public void testGetS3ClientShouldReturnNonNullS3Client() throws Exception {
        Whitebox.setInternalState(s3MultimediaFileManager, "objectStorageRegion", "region");
        Whitebox.setInternalState(s3MultimediaFileManager, "objectStorageAccessKeyId", "region");
        Whitebox.setInternalState(s3MultimediaFileManager, "objectStorageSecretAccessKey", "awsSecretAccessKey");
        Whitebox.setInternalState(s3MultimediaFileManager, "s3Client", (Object[]) null);
        AmazonS3Client s3Client = Whitebox.invokeMethod(s3MultimediaFileManager, "getS3Client");
        assertNotNull(s3Client);
        assertEquals("region", s3Client.getRegionName());
    }

    @Test
    public void testGetMultiMediaDirShouldReturnCorrectFilePath() {
        assertEquals(File.separator + "tmp" + File.separator, s3MultimediaFileManager.getBaseMultiMediaDir());
    }

    @Test
    public void testGetS3FilePathShouldReturnCorrectFilePath() throws Exception {
        Whitebox.setInternalState(s3MultimediaFileManager, "objectStorageBucketFolderPath", "bucket_folder_path");
        Whitebox.setInternalState(s3MultimediaFileManager, "baseMultimediaDirPath", "base/directory/path");
        String truncatedFilePath = Whitebox.invokeMethod(s3MultimediaFileManager, "getObjectStorageFilePath", s3MultimediaFileManager.getBaseMultiMediaDir() + "path/to/file");
        assertEquals("bucket_folder_path" + File.separator + "path/to/file", truncatedFilePath);
    }
}
