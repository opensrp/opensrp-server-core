package org.opensrp.service.multimedia;

import com.aliyun.oss.OSSClient;

/**
 * Created by Vincent Karuri on 26/05/2020
 */
public class OSSClientBuilder {
	
	private String objectStorageAccessKeyId;

	private String objectStorageSecretAccessKey;
	
	private String objectStorageRegion;

	public OSSClientBuilder withObjectStorageAccessKeyId(String objectStorageAccessKeyId) {
		this.objectStorageAccessKeyId = objectStorageAccessKeyId;
		return this;
	}

	public OSSClientBuilder withObjectStorageSecretAccessKey(String objectStorageSecretAccessKey) {
		this.objectStorageSecretAccessKey = objectStorageSecretAccessKey;
		return this;
	}

	public OSSClientBuilder withObjectStorageRegion(String objectStorageRegion) {
		this.objectStorageRegion = objectStorageRegion;
		return this;
	}

	public OSSClient getOssClient() {
		return new OSSClient(objectStorageRegion, objectStorageAccessKeyId, objectStorageSecretAccessKey);
	}
}
