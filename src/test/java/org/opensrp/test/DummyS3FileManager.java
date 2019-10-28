package org.opensrp.test;

import com.amazonaws.services.s3.AmazonS3;
import org.opensrp.domain.S3MultimediaFileManager;
import org.opensrp.repository.MultimediaRepository;
import org.opensrp.service.ClientService;
import org.springframework.context.annotation.Primary;

/**
 * Created by Vincent Karuri on 28/10/2019
 */

@Primary
public class DummyS3FileManager extends S3MultimediaFileManager {

	public DummyS3FileManager(MultimediaRepository multimediaRepository, ClientService clientService) {
		super(multimediaRepository, clientService);
	}

	@Override
	protected AmazonS3 getS3Client() {
		return null;
	}
}
