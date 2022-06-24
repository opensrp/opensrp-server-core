package org.opensrp.service.multimedia;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensrp.domain.Multimedia;
import org.opensrp.dto.form.MultimediaDTO;
import org.opensrp.repository.MultimediaRepository;
import org.opensrp.service.ClientService;
import org.smartregister.domain.Client;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static org.opensrp.service.MultimediaService.*;

/**
 * Created by Vincent Karuri on 24/10/2019
 */

public abstract class BaseMultimediaFileManager implements MultimediaFileManager {

    private static final String SUCCESS = "success";
    private static final String FAIL = "fail";
    private static Logger logger = LogManager.getLogger(BaseMultimediaFileManager.class.getName());
    private final MultimediaRepository multimediaRepository;
    private final ClientService clientService;
    protected String multimediaDirPath;
    @Value("#{opensrp['multimedia.directory.name']}")
    protected String baseMultimediaDirPath;

    public BaseMultimediaFileManager(MultimediaRepository multimediaRepository, ClientService clientService) {
        this.multimediaRepository = multimediaRepository;
        this.clientService = clientService;
    }

    /***
     * Persist the given bytes to disk
     *
     * @param destination
     * @param fileBytes
     * @throws IOException
     */
    public void copyBytesToFile(File destination, byte[] fileBytes) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(destination)) {
            fos.write(fileBytes);
        }
    }

    /**
     * Persists a {@link byte[]} with the given {@param fileName} to storage
     *
     * @param fileName
     * @param fileBytes
     * @throws IOException
     */
    protected abstract void persistFileToStorage(String fileName, byte[] fileBytes) throws IOException;

    /**
     * {@inheritDoc}
     */
    @Override
    public String saveFile(MultimediaDTO multimedia, byte[] fileBytes, String originalFileName) {
        return saveMultimediaFile(multimedia, fileBytes, originalFileName);
    }

    public String saveMultimediaFile(MultimediaDTO multimediaDTO, byte[] fileBytes, String originalFileName) {

        if (uploadFile(multimediaDTO, fileBytes, originalFileName)) {
            try {
                logger.info("Image path : " + multimediaDirPath);

                Multimedia multimediaFile = new Multimedia().withCaseId(multimediaDTO.getCaseId())
                        .withProviderId(multimediaDTO.getProviderId()).withContentType(multimediaDTO.getContentType())
                        .withFilePath(multimediaDTO.getFilePath()).withFileCategory(multimediaDTO.getFileCategory())
                        .withSummary(multimediaDTO.getSummary()).withOriginalFileName(multimediaDTO.getOriginalFileName())
                        .withDateUploaded(multimediaDTO.getDateUploaded());

                multimediaRepository.add(multimediaFile);
                Client client = clientService.getByBaseEntityId(multimediaDTO.getCaseId());
                if (client != null) {
                    if (client.getAttribute("Patient Image") != null) {
                        client.removeAttribute("Patient Image");
                    }
                    client.addAttribute("Patient Image", multimediaDTO.getCaseId() + ".jpg");
                    clientService.updateClient(client);
                }
                return SUCCESS;
            } catch (Exception e) {
                logger.error("", e);
            }
        }

        return FAIL;
    }

    /**
     * Saves a multi-part file uploaded to the server
     *
     * @param multimediaDTO    {@link MultimediaDTO} object populated with information about the file to be saved
     * @param fileBytes        {@link byte[]} bytes to save to disk
     * @param originalFileName {@link String} original name of the file
     * @return true if the file was saved else false
     */
    public boolean uploadFile(MultimediaDTO multimediaDTO, byte[] fileBytes, String originalFileName) {
        boolean wasFileSaved = false;
        if (fileBytes != null) {
            try {
                String fileName = getMultimediaFilePath(multimediaDTO, originalFileName);
                multimediaDTO.withFilePath(fileName);
                persistFileToStorage(fileName, fileBytes);
                wasFileSaved = true;
            } catch (Exception e) {
                logger.error("", e);
            }
        }
        return wasFileSaved;
    }

    public String getMultimediaFilePath(MultimediaDTO multimediaDTO, String originalFileName) {

        multimediaDirPath = getBaseMultiMediaDir();
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
            case "text/csv":
                multimediaDirPath += CSV_DIR;
                fileExt = ".csv";
                break;
            default:
                multimediaDirPath += OTHER_DIR;
                fileExt = getFileExtension(originalFileName);

                if (StringUtils.isBlank(fileExt))
                    throw new IllegalArgumentException("Unknown content type : " + multimediaDTO.getContentType());
                break;
        }

        String fileName;
        if (MULTI_VERSION.equals(multimediaDTO.getFileCategory())) {
            // allow saving multiple multimedia associated with one client
            String dirPath = multimediaDirPath + File.separator + multimediaDTO.getCaseId();
            createMultimediaDir(dirPath);
            fileName = dirPath + File.separator + originalFileName;
        } else {
            // overwrite previously saved image
            createMultimediaDir(multimediaDirPath);
            fileName = multimediaDirPath + File.separator + multimediaDTO.getCaseId() + fileExt;
        }

        return fileName;
    }

    public String getFileExtension(String fileName) {
        int lastIndexOf = fileName.lastIndexOf(".");
        if (lastIndexOf == -1)
            return "";
        return fileName.substring(lastIndexOf);
    }

    protected abstract String getBaseMultiMediaDir();

    private void createMultimediaDir(String dirPath) {
        File file = new File(dirPath);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public String getMultimediaDirPath() {
        return multimediaDirPath;
    }
}
