package org.opensrp.service.multimedia;

import com.aliyun.oss.OSSClient;
import org.apache.commons.io.FileUtils;
import org.opensrp.repository.MultimediaRepository;
import org.opensrp.service.ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
@Component("OSSMultimediaFileManager")
public class OSSMultimediaFileManager extends ObjectStorageMultimediaFileManager {

	private Logger logger = LoggerFactory.getLogger(OSSMultimediaFileManager.class.toString());
	private OSSClient ossClient;
	
	@Autowired
	public OSSMultimediaFileManager(MultimediaRepository multimediaRepository, ClientService clientService) {
		super(multimediaRepository, clientService);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void persistFileToStorage(String fileName, byte[] fileBytes) {
		ossClient.putObject(objectStorageBucketName, getOSSObjectStorageFilePath(fileName), new ByteArrayInputStream(fileBytes));
		ossClient.shutdown();
	}

	private String getOSSObjectStorageFilePath(String fileName) {
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

		if (!ossClient.doesObjectExist(objectStorageBucketName, filePath)) { return file; }

		InputStream content = ossClient.getObject(objectStorageBucketName, filePath).getObjectContent();
		try {
			file = new File(filePath);
			FileUtils.copyInputStreamToFile(content, file);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		} finally {
			closeCloseable(content);
			ossClient.shutdown();
		}
		return file;
	}

	@PostConstruct
	@SuppressWarnings("unused")
	private OSSClient getOssClient () {
		if (ossClient  == null) {
			ossClient = new OSSClient(objectStorageRegion, objectStorageAccessKeyId, objectStorageSecretAccessKey);
		}
		return ossClient ;
	}
}
