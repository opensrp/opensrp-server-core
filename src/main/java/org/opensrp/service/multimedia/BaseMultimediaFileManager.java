package org.opensrp.service.multimedia;

import org.opensrp.domain.Client;
import org.opensrp.domain.Multimedia;
import org.opensrp.dto.form.MultimediaDTO;
import org.opensrp.repository.MultimediaRepository;
import org.opensrp.service.ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static org.opensrp.service.MultimediaService.IMAGES_DIR;
import static org.opensrp.service.MultimediaService.MULTI_VERSION;
import static org.opensrp.service.MultimediaService.VIDEOS_DIR;

/**
 * Created by Vincent Karuri on 24/10/2019
 */

public abstract class BaseMultimediaFileManager implements MultimediaFileManager {

	private final MultimediaRepository multimediaRepository;

	private final ClientService clientService;

	protected String multimediaDirPath;

	@Value("#{opensrp['multimedia.directory.name']}")
	protected String baseMultimediaDirPath;

	private static Logger logger = LoggerFactory.getLogger(FileSystemMultimediaFileManager.class.getName());

	private static final String SUCCESS = "success";

	private static final String FAIL = "fail";

	public BaseMultimediaFileManager(MultimediaRepository multimediaRepository, ClientService clientService) {
		this.multimediaRepository = multimediaRepository;
		this.clientService = clientService;
	}

	public void copyBytesToFile(File destination, byte[] fileBytes) throws IOException {
		try (FileOutputStream fos = new FileOutputStream(destination)) {
			fos.write(fileBytes);
		}
	}

	/**
	 * Persists a {@link File} with the given {@param fileName} to storage
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
			}
			catch (Exception e) {
				logger.error("", e);
			}
		}

		return FAIL;
	}

	/**
	 * Saves a multi-part file uploaded to the server
	 *
	 * @param multimediaDTO {@link MultimediaDTO} object populated with information about the file to be saved
	 * @param fileBytes     {@link File} file to save to disk
	 * @return true if the file was saved else false
	 */
	public boolean uploadFile(MultimediaDTO multimediaDTO, byte[] fileBytes, String originalFileName) {
		if (fileBytes != null) {
			try {
				multimediaDirPath = getMultiMediaDir();
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
					fileName = dirPath + File.separator + originalFileName;
				} else {
					// overwrite previously saved image
					makeMultimediaDir(multimediaDirPath);
					fileName = multimediaDirPath + File.separator + multimediaDTO.getCaseId() + fileExt;
				}

				multimediaDTO.withFilePath(fileName);
				persistFileToStorage(fileName, fileBytes);

				return true;
			}
			catch (Exception e) {
				logger.error("", e);
				return false;
			}
		} else {
			return false;
		}
	}

	protected abstract String getMultiMediaDir();

	private void makeMultimediaDir(String dirPath) {
		File file = new File(dirPath);
		if (!file.exists()) {
			file.mkdirs();
		}
	}
}
