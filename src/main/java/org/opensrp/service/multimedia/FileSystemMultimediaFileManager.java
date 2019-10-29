package org.opensrp.service.multimedia;

import org.opensrp.repository.MultimediaRepository;
import org.opensrp.service.ClientService;
import org.opensrp.service.multimedia.BaseMultimediaFileManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * Created by Vincent Karuri on 24/10/2019
 */

@Component("FileSystemMultimediaFileManager")
public class FileSystemMultimediaFileManager extends BaseMultimediaFileManager {

    @Autowired
    public FileSystemMultimediaFileManager(MultimediaRepository multimediaRepository, ClientService clientService) {
        super(multimediaRepository, clientService);
    }

    @Override
    protected void persistFileToStorage(String fileName, MultipartFile multimediaFile) throws IOException {
        File multimediaFilePath = new File(fileName);
        multimediaFile.transferTo(multimediaFilePath);
    }

    @Override
    public File retrieveFile(String filePath) {
        File file = new File(filePath);
        return file.exists() ? file : null;
    }
}
