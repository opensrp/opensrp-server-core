package org.opensrp.domain.contract;

import org.opensrp.dto.form.MultimediaDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;


/**
 * Created by Vincent Karuri on 24/10/2019
 */
public interface MultimediaFileManager {

    boolean saveFile(MultimediaDTO multimedia, MultipartFile file);

    File retrieveFile(String filePath);
}
