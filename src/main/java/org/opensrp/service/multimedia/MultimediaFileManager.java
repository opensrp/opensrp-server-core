package org.opensrp.service.multimedia;

import org.opensrp.dto.form.MultimediaDTO;

import java.io.File;


/**
 * Created by Vincent Karuri on 24/10/2019
 */
public interface MultimediaFileManager {

    /**
     * Persists a {@link byte[]} to storage
     *
     * @param multimedia
     * @param fileBytes
     * @param originalFileName
     * @return
     */
    String saveFile(MultimediaDTO multimedia, byte[] fileBytes, String originalFileName);

    /**
     * Retrieves a file from storage or null if it doesn't exist
     *
     * @param filePath
     * @return
     */
    File retrieveFile(String filePath);

    /**
     * Gets the path to the local (filesystem) multimedia file directory
     *
     * @return
     */
    String getMultimediaDirPath();


    /**
     * Gets the the local (filesystem) path where a file should be saved
     *
     * @return
     */
    String getMultimediaFilePath(MultimediaDTO multimediaDTO, String originalFileName);
}
