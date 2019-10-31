package org.opensrp.service;

import org.opensrp.domain.Multimedia;
import org.opensrp.service.multimedia.MultimediaFileManager;
import org.opensrp.dto.form.MultimediaDTO;
import org.opensrp.repository.MultimediaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

@Service
public class MultimediaService {

	public static final String VIDEOS_DIR = "videos";

	public static final String IMAGES_DIR = "patient_images";

	public static final String MULTI_VERSION = "multi_version";

	private final MultimediaRepository multimediaRepository;

	private MultimediaFileManager fileManager;

	private ClientService clientService;

	@Autowired
	public MultimediaService(MultimediaRepository multimediaRepository, ClientService clientService) {
		this.multimediaRepository = multimediaRepository;
		this.clientService = clientService;
	}

	public List<Multimedia> getMultimediaFiles(String providerId) {
		return multimediaRepository.all(providerId);
	}
	
	public Multimedia findByCaseId(String entityId) {
		return multimediaRepository.findByCaseId(entityId);
	}

	/**
	 * Returns a {@link List} of {@link Multimedia} objects that match the given parameters
	 *
	 * @param entityId The baseEntityId of the client who owns the multimedia file(s)
	 * @param contentType The contentType of the multimedia file(s) to be fetched
	 * @param fileCategory The file category of the multimedia file(s)
	 *
	 * @return A {@link List} of {@link Multimedia} objects
	 */
	public List<Multimedia> getMultimediaFiles(String entityId, String contentType, String fileCategory) {
		return multimediaRepository.get(entityId, contentType, fileCategory);
	}

	/**
	 *
	 * Persists a {@link MultipartFile} to storage
	 *
	 * @param multimedia
	 * @param file
	 * @return
	 */
	public String saveFile(MultimediaDTO multimedia, MultipartFile file) {
		return fileManager.saveFile(multimedia, file);
	}

	/**
	 *
	 * Retrieves a file from storage or null if it doesn't exist
	 *
	 * @param filePath
	 * @return
	 */
	public File retrieveFile(String filePath) {
		return fileManager.retrieveFile(filePath);
	}

	@Autowired
	@Qualifier("multimedia_file_manager")
	public void setFileManager(MultimediaFileManager fileManager) {
		this.fileManager = fileManager;
	}

	public MultimediaFileManager getFileManager() {
		return fileManager;
	}

	public ClientService getClientService() {
		return clientService;
	}
}
