package org.opensrp.service.multimedia;

import org.opensrp.repository.MultimediaRepository;
import org.opensrp.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

/**
 * Created by Vincent Karuri on 24/10/2019
 */
@Profile("FileSystem")
//@Component("FileSystemMultimediaFileManager")
@Component
public class FileSystemMultimediaFileManager extends BaseMultimediaFileManager {

    @Autowired
    public FileSystemMultimediaFileManager(MultimediaRepository multimediaRepository, ClientService clientService) {
        super(multimediaRepository, clientService);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void persistFileToStorage(String fileName, byte[] fileBytes) throws IOException {
        File multimediaFilePath = new File(fileName);
        copyBytesToFile(multimediaFilePath, fileBytes);
    }

    @Override
    protected String getBaseMultiMediaDir() {
       return baseMultimediaDirPath + File.separator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public File retrieveFile(String filePath) {
        File file = new File(filePath);
        return file.exists() && file.canRead() ? file : null;
    }
}
