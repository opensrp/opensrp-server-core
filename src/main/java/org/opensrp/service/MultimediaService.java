package org.opensrp.service;

import org.opensrp.domain.Multimedia;
import org.opensrp.dto.form.MultimediaDTO;
import org.opensrp.repository.MultimediaRepository;
import org.opensrp.service.multimedia.MultimediaFileManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Service
public class MultimediaService {

	public static final String VIDEOS_DIR = "videos";

	public static final String IMAGES_DIR = "patient_images";

	public static final String CSV_DIR = "csv";

	public static final String OTHER_DIR = "unknown_files";

	public static final String MULTI_VERSION = "multi_version";

	private final MultimediaRepository multimediaRepository;

	private MultimediaFileManager fileManager;

	private ClientService clientService;

	@Autowired
	public MultimediaService(MultimediaRepository multimediaRepository, ClientService clientService) {
		this.multimediaRepository = multimediaRepository;
		this.clientService = clientService;
	}

	@PreAuthorize("hasRole('MULTIMEDIA_VIEW')")
	@PostFilter("hasPermission(filterObject, 'MULTIMEDIA_VIEW')")
	public List<Multimedia> getMultimediaFiles(String providerId) {
		return multimediaRepository.all(providerId);
	}

	@PreAuthorize("hasRole('MANIFEST_VIEW')")
	@PostAuthorize("hasPermission(returnObject,'Multimedia', 'MANIFEST_VIEW')")
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
	@PreAuthorize("hasRole('MULTIMEDIA_VIEW')")
	@PostFilter("hasPermission(filterObject, 'MULTIMEDIA_VIEW')")
	public List<Multimedia> getMultimediaFiles(String entityId, String contentType, String fileCategory) {
		return multimediaRepository.get(entityId, contentType, fileCategory);
	}

	/**
	 *
	 * Persists a {@link byte[]} to storage
	 *
	 * @param multimedia
	 * @param fileBytes
	 * @param originalFileName
	 *
	 * @return
	 */
	@PreAuthorize("hasRole('MULTIMEDIA_CREATE')")
	public String saveFile(MultimediaDTO multimedia, byte[] fileBytes, String originalFileName) {
		return fileManager.saveFile(multimedia, fileBytes, originalFileName);
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

	public void deleteMultimedia(Multimedia multimedia) {
		if (multimedia == null) {
			return;
		}

		multimediaRepository.safeRemove(multimedia);

	}

	@Autowired
	@Qualifier("multimedia.file.manager")
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
