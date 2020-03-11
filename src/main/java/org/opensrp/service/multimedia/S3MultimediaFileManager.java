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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Vincent Karuri on 24/10/2019
 */
@Component("S3MultimediaFileManager")
public class S3MultimediaFileManager extends BaseMultimediaFileManager {

    @Value("#{opensrp['aws_access_key_id'] ?: ''}")
    private String awsAccessKeyId;

    @Value("#{opensrp['aws_secret_access_key'] ?: ''}")
    private String awsSecretAccessKey;

    @Value("#{opensrp['aws_region'] ?: ''}")
    private String awsRegion;

    @Value("#{opensrp['s3_bucket_name'] ?: ''}")
    private String s3BucketName;

	@Value("#{opensrp['s3_bucket_folder_path'] ?: ''}")
	private String s3BucketFolderPath;

	private AmazonS3 s3Client;

    @Autowired
    public S3MultimediaFileManager(MultimediaRepository multimediaRepository, ClientService clientService) {
        super(multimediaRepository, clientService);
    }

	/**
	 * {@inheritDoc}
	 */
    @Override
    protected void persistFileToStorage(String fileName, MultipartFile multipartFile) throws IOException {
        File multimediaFile = multipartFileToFile(fileName, multipartFile);
        byte[] md5 = DigestUtils.md5(new FileInputStream(multimediaFile));
        InputStream inputStream = new FileInputStream(multimediaFile);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(multimediaFile.length());
        metadata.setContentMD5(new String(Base64.encodeBase64(md5)));
        PutObjectRequest request = new PutObjectRequest(s3BucketName, getS3FilePath(multimediaFile.getPath()), inputStream, metadata);
        s3Client.putObject(request);
        multimediaFile.delete();
    }

    private String getS3FilePath(String localFilePath) {
    	return StringUtils.isBlank(localFilePath) ? "" : localFilePath.replace(getMultiMediaDir(), "");
    }

	@Override
	protected String getMultiMediaDir() {
		return File.separator + "tmp" + File.separator;
	}

	/**
	 * {@inheritDoc}
	 */
    @Override
    public File retrieveFile(String filePath) {
        File file = null;
        if (s3Client.doesObjectExist(s3BucketName, filePath)) {
            // make sure tomcat has permissions to save to filePath
            file = new File(filePath);
            s3Client.getObject(new GetObjectRequest(s3BucketName, filePath), file);
        }
        return file;
    }

	/**
	 *
	 * Converts {@link MultipartFile} to {@link File}
	 *
	 * @param fileName
	 * @param multipart
	 * @return
	 * @throws IOException
	 */
	private File multipartFileToFile(String fileName, MultipartFile multipart) throws IOException {
        File tempFile = new File(fileName);
        multipart.transferTo(tempFile);
        tempFile.deleteOnExit();
        return tempFile;
    }

    @PostConstruct
    @SuppressWarnings("unused")
    private AmazonS3 getS3Client() {
        if (s3Client == null) {
            s3Client = AmazonS3ClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(awsAccessKeyId, awsSecretAccessKey)))
                    .withRegion(awsRegion)
                    .build();
        }
        return s3Client;
    }
}
