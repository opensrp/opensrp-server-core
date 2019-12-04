package org.opensrp.service.multimedia;

import org.opensrp.domain.Client;
import org.opensrp.domain.Multimedia;
import org.opensrp.dto.form.MultimediaDTO;
import org.opensrp.repository.MultimediaRepository;
import org.opensrp.service.ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import static org.opensrp.service.MultimediaService.IMAGES_DIR;
import static org.opensrp.service.MultimediaService.MULTI_VERSION;
import static org.opensrp.service.MultimediaService.VIDEOS_DIR;

/**
 * Created by Vincent Karuri on 24/10/2019
 */

@Component
public abstract class BaseMultimediaFileManager implements MultimediaFileManager {

    private final MultimediaRepository multimediaRepository;

    private final ClientService clientService;

    private String multimediaDirPath;

    @Value("#{opensrp['multimedia.directory.name']}")
    private String baseMultimediaDirPath;

    private static Logger logger = LoggerFactory.getLogger(FileSystemMultimediaFileManager.class.getName());

    private final String SUCCESS = "success";

    private final String FAIL = "fail";

    public BaseMultimediaFileManager(MultimediaRepository multimediaRepository, ClientService clientService) {
        this.multimediaRepository = multimediaRepository;
        this.clientService = clientService;
    }

    /**
     * Persists a {@link MultipartFile} with the given {@param fileName} to storage
     * @param fileName
     * @param multimediaFile
     * @throws IOException
     */
    protected abstract void persistFileToStorage(String fileName, MultipartFile multimediaFile) throws IOException;

    /**
     * {@inheritDoc}
     */
    @Override
    public String saveFile(MultimediaDTO multimedia, MultipartFile file) {
        return saveMultimediaFile(multimedia, file);
    }

    public String saveMultimediaFile(MultimediaDTO multimediaDTO, MultipartFile file) {

        if (uploadFile(multimediaDTO, file)) {
            try {
                logger.info("Image path : " + multimediaDirPath);

                Multimedia multimediaFile = new Multimedia().withCaseId(multimediaDTO.getCaseId())
                        .withProviderId(multimediaDTO.getProviderId()).withContentType(multimediaDTO.getContentType())
                        .withFilePath(multimediaDTO.getFilePath()).withFileCategory(multimediaDTO.getFileCategory());

                multimediaRepository.add(multimediaFile);
                Client client = clientService.getByBaseEntityId(multimediaDTO.getCaseId());
                if (client != null) {
                    if (client.getAttribute("Patient Image") != null) {
                        client.removeAttribute("Patient Image");
                    }
                    client.addAttribute("Patient Image", multimediaDTO.getCaseId() + ".jpg");
                    client.setServerVersion(null);
                    clientService.updateClient(client);
                }
                return SUCCESS;
            } catch (Exception e) {
                e.getMessage();
            }
        }

        return FAIL;
    }

    /**
     * Saves a multi-part file uploaded to the server
     *
     * @param multimediaDTO {@link MultimediaDTO} object populated with information about the file to be saved
     * @param multimediaFile {@link MultipartFile} file to save to disk
     *
     * @return true if the file was saved else false
     */
    public boolean uploadFile(MultimediaDTO multimediaDTO, MultipartFile multimediaFile) {
        if (!multimediaFile.isEmpty()) {
            try {
                multimediaDirPath = baseMultimediaDirPath + File.separator;
                String fileExt = ".jpg";
                switch (multimediaDTO.getContentType()) {

                    case "application/octet-stream":
                        multimediaDirPath += VIDEOS_DIR;
                        fileExt = ".mp4";
                        break;
                    case "image/jpeg":
                        multimediaDirPath += IMAGES_DIR;
                        fileExt = ".jpg";
                        break;
                    case "image/gif":
                        multimediaDirPath += IMAGES_DIR;
                        fileExt = ".gif";
                        break;
                    case "image/png":
                        multimediaDirPath += IMAGES_DIR;
                        fileExt = ".png";
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown content type : " + multimediaDTO.getContentType());
                }

                String fileName;
                if (MULTI_VERSION.equals(multimediaDTO.getFileCategory())) {
                    // allow saving multiple multimedia associated with one client
                    String dirPath = multimediaDirPath + File.separator + multimediaDTO.getCaseId();
                    makeMultimediaDir(dirPath);
                    fileName = dirPath + File.separator + multimediaFile.getOriginalFilename() + fileExt;
                } else {
                    // overwrite previously saved image
                    makeMultimediaDir(multimediaDirPath);
                    fileName = multimediaDirPath + File.separator + multimediaDTO.getCaseId() + fileExt;
                }

                multimediaDTO.withFilePath(fileName);
                persistFileToStorage(fileName, multimediaFile);

                return true;
            } catch (Exception e) {
                logger.error("", e);
                return false;
            }
        } else {
            return false;
        }
    }

    private void makeMultimediaDir(String dirPath) {
        File file = new File(dirPath);
        if (!file.exists()) {
            file.mkdirs();
        }
    }
}
