package org.opensrp.service.multimedia;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.opensrp.repository.MultimediaRepository;
import org.opensrp.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Vincent Karuri on 24/10/2019
 */
@Component("S3MultimediaFileManager")
public class S3MultimediaFileManager extends ObjectStorageMultimediaFileManager {

	private AmazonS3 s3Client;

    @Autowired
    public S3MultimediaFileManager(MultimediaRepository multimediaRepository, ClientService clientService) {
        super(multimediaRepository, clientService);
    }

	/**
	 * {@inheritDoc}
	 */
    @Override
    protected void persistFileToStorage(String fileName, byte[] fileBytes) throws IOException {
	    File multimediaFile = bytesToFile(fileName, fileBytes);
        byte[] md5 = DigestUtils.md5(new FileInputStream(multimediaFile));
        InputStream inputStream = new FileInputStream(multimediaFile);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(multimediaFile.length());
        metadata.setContentMD5(new String(Base64.encodeBase64(md5)));
        PutObjectRequest request = new PutObjectRequest(objectStorageBucketName, getObjectStorageFilePath(multimediaFile.getPath()), inputStream, metadata);
        s3Client.putObject(request);
        multimediaFile.delete();
    }

	/**
	 * {@inheritDoc}
	 */
    @Override
    public File retrieveFile(String filePath) {
        File file = null;
        if (s3Client.doesObjectExist(objectStorageBucketName, getObjectStorageFilePath(filePath))) {
            // make sure tomcat has permissions to save to filePath
            file = new File(filePath);
            s3Client.getObject(new GetObjectRequest(objectStorageBucketName, filePath), file);
        }
        return file;
    }

	/**
	 *
	 * Converts {@link File} to {@link File}
	 *
	 * @param fileName
	 * @param fileBytes
	 * @return
	 * @throws IOException
	 */
	private File bytesToFile(String fileName, byte[] fileBytes) throws IOException {
        File tempFile = new File(fileName);
		copyBytesToFile(tempFile, fileBytes);
        tempFile.deleteOnExit();
        return tempFile;
    }

    @PostConstruct
    @SuppressWarnings("unused")
    private AmazonS3 getS3Client() {
        if (s3Client == null) {
            s3Client = AmazonS3ClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(objectStorageAccessKeyId,
		                    objectStorageSecretAccessKey)))
                    .withRegion(objectStorageRegion)
                    .build();
        }
        return s3Client;
    }
}
