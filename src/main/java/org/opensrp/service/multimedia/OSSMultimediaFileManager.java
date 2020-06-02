package org.opensrp.service.multimedia;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import org.apache.commons.io.FileUtils;
import org.opensrp.repository.MultimediaRepository;
import org.opensrp.service.ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static org.opensrp.util.Utils.closeCloseable;

/**
 * Created by Vincent Karuri on 20/05/2020
 */
@Profile("AliCloud_OSS")
@Component("OSSMultimediaFileManager")
public class OSSMultimediaFileManager extends ObjectStorageMultimediaFileManager {

	private Logger logger = LoggerFactory.getLogger(OSSMultimediaFileManager.class.toString());
	private OSSClientBuilder ossClientBuilder = new OSSClientBuilder();
	
	@Autowired
	public OSSMultimediaFileManager(MultimediaRepository multimediaRepository, ClientService clientService) {
		super(multimediaRepository, clientService);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void persistFileToStorage(String fileName, byte[] fileBytes) {
		OSSClient ossClient = getOssClient();
		ossClient.putObject(objectStorageBucketName, getOSSObjectStorageFilePath(fileName), new ByteArrayInputStream(fileBytes));
		ossClient.shutdown();
	}

	public String getOSSObjectStorageFilePath(String fileName) {
		String objectStorageFilePath = getObjectStorageFilePath(fileName);
		return objectStorageFilePath.charAt(0) == File.separatorChar
				? objectStorageFilePath.substring(1) : objectStorageFilePath;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public File retrieveFile(String filePath) {
		File file = null;
		OSSClient ossClient = getOssClient();
		String filePathInBucket = getOSSObjectStorageFilePath(filePath);
		if (!ossClient.doesObjectExist(objectStorageBucketName, filePathInBucket)) {
			return file;
		}

		InputStream content = null;
		try {
			content = ossClient.getObject(objectStorageBucketName, filePathInBucket).getObjectContent();
			file = new File(filePath);
			FileUtils.copyInputStreamToFile(content, file);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		} catch (ClientException e) {
			logger.error(e.getMessage(), e);
		} catch (OSSException e) {
			logger.error(e.getMessage(), e);
		} finally {
			closeCloseable(content);
			ossClient.shutdown();
		}
		return file;
	}

	private OSSClient getOssClient() {
		return getOSSClientBuilder().getOssClient();
	}

	@PostConstruct
	@SuppressWarnings("unused")
	private OSSClientBuilder getOSSClientBuilder() {
		if (ossClientBuilder == null) {
			ossClientBuilder = new OSSClientBuilder();
			ossClientBuilder.withObjectStorageAccessKeyId(objectStorageAccessKeyId)
					.withObjectStorageSecretAccessKey(objectStorageSecretAccessKey)
					.withObjectStorageRegion(objectStorageRegion);
		}
		return ossClientBuilder;
	}
}
