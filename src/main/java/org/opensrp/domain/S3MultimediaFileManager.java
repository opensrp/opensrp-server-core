package org.opensrp.domain;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.opensrp.repository.MultimediaRepository;
import org.opensrp.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Vincent Karuri on 24/10/2019
 */
public class S3MultimediaFileManager extends BaseMultimediaFileManager {

    @Value("#{opensrp['aws_access_key_id']}")
    String awsAccessKeyId;

    @Value("#{opensrp['aws_secret_access_key']}")
    String awsSecretAccessKey;

    @Value("#{opensrp['aws_region']}")
    String awsRegion;

    @Value("#{opensrp['aws_bucket']}")
    String awsBucket;

    @Value("#{opensrp['aws_key_folder']}")
    String awsKeyFolder;

    @Autowired
    public S3MultimediaFileManager(MultimediaRepository multimediaRepository, ClientService clientService) {
        super(multimediaRepository, clientService);
    }

    @Override
    protected void persistFileToStorage(String fileName, MultipartFile multipartFile) throws IOException {
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(awsAccessKeyId, awsSecretAccessKey)))
                .withRegion(awsRegion)
                .build();

        File multimediaFile = multipartFileToFile(fileName, multipartFile);
        byte[] md5 = DigestUtils.md5(new FileInputStream(multimediaFile));
        InputStream inputStream = new FileInputStream(multimediaFile);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(multimediaFile.length());
        metadata.setContentMD5(new String(Base64.encodeBase64(md5)));
        PutObjectRequest request = new PutObjectRequest(awsBucket, awsKeyFolder + multimediaFile.getName(), inputStream, metadata);
        s3Client.putObject(request);
        multimediaFile.delete();
    }

    private File multipartFileToFile(String fileName, MultipartFile multipart) throws IOException {
        File tempFile = new File(fileName);
        multipart.transferTo(tempFile);
        tempFile.deleteOnExit();
        return tempFile;
    }
}
