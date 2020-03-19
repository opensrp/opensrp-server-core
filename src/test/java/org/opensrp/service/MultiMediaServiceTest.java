package org.opensrp.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.opensrp.service.MultimediaService.IMAGES_DIR;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.junit.Before;
import org.junit.Test;
import org.opensrp.service.multimedia.BaseMultimediaFileManager;
import org.opensrp.domain.Client;
import org.opensrp.domain.Multimedia;
import org.opensrp.dto.form.MultimediaDTO;
import org.opensrp.repository.ClientsRepository;
import org.opensrp.repository.MultimediaRepository;
import org.opensrp.repository.postgres.BaseRepositoryTest;
import org.powermock.reflect.Whitebox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

public class MultiMediaServiceTest extends BaseRepositoryTest {
	
	private MultimediaService multimediaService;
	
	private ClientService clientService;
	
	@Autowired
	@Qualifier("multimediaRepositoryPostgres")
	private MultimediaRepository multimediaRepository;
	
	@Autowired
	@Qualifier("clientsRepositoryPostgres")
	private ClientsRepository clientsRepository;
	
	@Value("#{opensrp['multimedia.directory.name']}")
	private String baseMultimediaDirPath;

	@Autowired
	@Qualifier("FileSystemMultimediaFileManager")
	private BaseMultimediaFileManager fileManager;

	@Before
	public void setUp() {
		clientService = new ClientService(clientsRepository);
		multimediaService = new MultimediaService(multimediaRepository, clientService);
		Whitebox.setInternalState(fileManager, "baseMultimediaDirPath", baseMultimediaDirPath);
		Whitebox.setInternalState(fileManager, "clientService", clientService);
	}
	
	@Override
	protected Set<String> getDatabaseScripts() {
		Set<String> scripts = new HashSet<String>();
		scripts.add("multimedia.sql");
		scripts.add("client.sql");
		return scripts;
	}
	
	@Test
	public void testUploadFile() throws IOException {
		String content = "876nsfsdfs-sdfsfsdf";
		String baseEntityId = "67007c17-97bb-4732-a1b8-3a0c292b5432";
		byte[] contentBytes = content.getBytes();
		//MultipartFile multimediaFile = new MockMultipartFile("mockFile", "test.jpg", "image/jpeg", content.getBytes());
		MultimediaDTO multimediaDTO = new MultimediaDTO(baseEntityId, "biddemo", "image/jpeg", "",
				"profilepic");

		assertTrue(fileManager.uploadFile(multimediaDTO, contentBytes, "test.jpg"));

		//assertEquals(multimediaFile, multimediaService.findByCaseId("469597f0-eefe-4171-afef-f7234cbb2859"));

		File file = new File(baseMultimediaDirPath + File.separator + IMAGES_DIR + File.separator
				+ baseEntityId + ".jpg");
		System.out.println(file.getAbsolutePath());
		assertTrue(file.exists());
		assertTrue(file.canRead());

		assertEquals(content, new String(Files.readAllBytes(Paths.get(file.getAbsolutePath()))));
	}
	
	@Test
	public void testSaveMultimediaFile() throws IOException {
		assertEquals(5, multimediaRepository.getAll().size());
		String baseEntityId = "040d4f18-8140-479c-aa21-725612073490";
		String content = "876nsfsdfs-sdfsfsdf";

		byte[] contentBytes = content.getBytes();
		//MultipartFile multimediaFile = new MockMultipartFile("mockFile", "test1.jpg", "image/jpeg", content.getBytes());
		MultimediaDTO multimediaDTO = new MultimediaDTO(baseEntityId, "biddemo2", "image/jpeg", "",
				"profilepic");

		assertEquals("success", fileManager.saveMultimediaFile(multimediaDTO, contentBytes, "test1.jpg"));

		File file = new File(baseMultimediaDirPath + File.separator + IMAGES_DIR + File.separator
				+ baseEntityId + ".jpg");
		System.out.println(file.getAbsolutePath());
		assertTrue(file.exists());
		assertTrue(file.canRead());

		assertEquals(content, new String(Files.readAllBytes(Paths.get(file.getAbsolutePath()))));
		Client client = clientService.find(baseEntityId);
		Multimedia savedMultimedia = multimediaService.findByCaseId(baseEntityId);
		assertEquals(multimediaDTO.getFilePath(), savedMultimedia.getFilePath());
		assertEquals(file.getAbsolutePath(), savedMultimedia.getFilePath());
		assertEquals(multimediaDTO.getContentType(), savedMultimedia.getContentType());
		assertEquals(multimediaDTO.getFileCategory(), savedMultimedia.getFileCategory());

		assertEquals(4, multimediaService.getMultimediaFiles("biddemo").size());
		assertEquals(6, multimediaRepository.getAll().size());
		assertEquals(baseEntityId + ".jpg", client.getAttribute("Patient Image"));
		assertEquals(0, Minutes.minutesBetween(client.getDateEdited(), DateTime.now()).getMinutes());
	}
}
