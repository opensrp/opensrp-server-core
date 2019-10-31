package org.opensrp.service.multimedia;

import org.opensrp.dto.form.MultimediaDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;


/**
 * Created by Vincent Karuri on 24/10/2019
 */
public interface MultimediaFileManager {

    /**
     *
     * Persists a {@link MultipartFile} to storage
     *
     * @param multimedia
     * @param file
     * @return
     */
    String saveFile(MultimediaDTO multimedia, MultipartFile file);

    /**
     *
     * Retrieves a file from storage or null if it doesn't exist
     *
     * @param filePath
     * @return
     */
    File retrieveFile(String filePath);
}
