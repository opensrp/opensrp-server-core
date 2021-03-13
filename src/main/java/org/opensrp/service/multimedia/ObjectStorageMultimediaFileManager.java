package org.opensrp.service.multimedia;

import org.apache.commons.lang3.StringUtils;
import org.opensrp.repository.MultimediaRepository;
import org.opensrp.service.ClientService;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;

/**
 * Created by Vincent Karuri on 20/05/2020
 */
public abstract class ObjectStorageMultimediaFileManager extends BaseMultimediaFileManager {
	
	@Value("#{opensrp['object.storage.access.key.id'] ?: ''}")
	protected String objectStorageAccessKeyId;

	@Value("#{opensrp['object.storage.secret.access.key'] ?: ''}")
	protected String objectStorageSecretAccessKey;

	@Value("#{opensrp['object.storage.region'] ?: ''}")
	protected String objectStorageRegion;

	@Value("#{opensrp['object.storage.bucket.name'] ?: ''}")
	protected String objectStorageBucketName;

	@Value("#{opensrp['object.storage.bucket.folder.path'] ?: ''}")
	protected String objectStorageBucketFolderPath;

	public ObjectStorageMultimediaFileManager(MultimediaRepository multimediaRepository, ClientService clientService) {
		super(multimediaRepository, clientService);
	}

	@Override
	protected String getBaseMultiMediaDir() {
		return File.separator + "tmp" + File.separator;
	}

	public String getObjectStorageFilePath(String localFilePath) {
		String cleanPath = localFilePath.startsWith("/") ? localFilePath.substring(1) : localFilePath;
		return StringUtils.isBlank(cleanPath) ? "" : objectStorageBucketFolderPath + File.separator + cleanPath.replace(getBaseMultiMediaDir(), "");
	}
}
