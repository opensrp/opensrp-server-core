package org.opensrp.repository.postgres;

import static org.opensrp.service.MultimediaService.MULTI_VERSION;

import java.util.*;

import org.opensrp.domain.Multimedia;
import org.opensrp.domain.postgres.MultiMedia;
import org.opensrp.domain.postgres.MultiMediaExample;
import org.opensrp.repository.MultimediaRepository;
import org.opensrp.repository.postgres.mapper.custom.CustomMultiMediaMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("multimediaRepositoryPostgres")
public class MultimediaRepositoryImpl extends BaseRepositoryImpl<Multimedia> implements MultimediaRepository {
	
	@Autowired
	private CustomMultiMediaMapper multiMediaMapper;
	
	@Override
	public Multimedia get(String id) {
		MultiMediaExample example = new MultiMediaExample();
		example.createCriteria().andDocumentIdEqualTo(id);
		List<MultiMedia> files = multiMediaMapper.selectByExample(example);
		return files.isEmpty() ? null : convert(files.get(0));
	}

    /**
     *{@inheritDoc}
     */
	@Override
	public List<Multimedia> get(String entityId, String contentType, String fileCategory) {
		MultiMediaExample example = new MultiMediaExample();
		example.createCriteria().andCaseIdEqualTo(entityId).andContentTypeEqualTo(contentType).andFileCategoryEqualTo(fileCategory);
		List<MultiMedia> files = multiMediaMapper.selectByExample(example);
		return convert(files);
	}

	/**
	 *{@inheritDoc}
	 */
	@Override
	public List<Multimedia> getByProviderID(String providerID, String fileCategory, int offset, int count) {
		MultiMediaExample example = new MultiMediaExample();
		example.createCriteria().andProviderIdEqualTo(providerID).andFileCategoryEqualTo(fileCategory);
		return convert(multiMediaMapper.selectMany(example, offset, count));
	}

	@Override
	public void add(Multimedia entity) {
		if (entity == null || entity.getCaseId() == null) {
			return;
		}
		
		if (retrievePrimaryKey(entity) != null) { //Multimedia already added
			return;
		}
		
		if (entity.getId() == null)
			entity.setId(UUID.randomUUID().toString());
		setRevision(entity);
		
		MultiMedia pgMultiMedia = convert(entity, null);
		if (pgMultiMedia == null) {
			return;
		}
		
		multiMediaMapper.insertSelective(pgMultiMedia);
		
	}
	
	@Override
	public void update(Multimedia entity) {
		if (entity == null || entity.getId() == null || entity.getCaseId() == null) {
			return;
		}
		
		Long id = retrievePrimaryKey(entity);
		
		if (id == null) { //Multimedia doesn't not exist
			return;
		}
		setRevision(entity);
		
		MultiMedia pgEntity = convert(entity, id);
		multiMediaMapper.updateByPrimaryKey(pgEntity);
		
	}
	
	@Override
	public List<Multimedia> getAll() {
		return convert(multiMediaMapper.selectMany(new MultiMediaExample(), 0, DEFAULT_FETCH_SIZE));
	}
	
	@Override
	public void safeRemove(Multimedia entity) {
		if (entity == null || entity.getCaseId() == null) {
			return;
		}
		
		Long id = retrievePrimaryKey(entity);
		if (id == null) {
			return;
		}
		
		multiMediaMapper.deleteByPrimaryKey(id);
		
	}

    /**
     * Returns the first Multimedia object that matches the entityId passed
     * and excluding all MULTI_VERSION category files
     *
     * @param entityId The baseEntityId of the client who owns the multimedia file(s)
     *
     * @return A {@link Multimedia} object
     */
	@Override
	public Multimedia findByCaseId(String entityId) {
		MultiMediaExample example = new MultiMediaExample();
		example.createCriteria().andCaseIdEqualTo(entityId).andFileCategoryNotEqualTo(MULTI_VERSION);
		List<MultiMedia> multiMediaFiles = multiMediaMapper.selectByExample(example);
		return multiMediaFiles.isEmpty() ? null : convert(multiMediaFiles.get(0));
	}
	
	@Override
	public List<Multimedia> all(String providerId) {
		MultiMediaExample example = new MultiMediaExample();
		example.createCriteria().andProviderIdEqualTo(providerId);
		List<MultiMedia> multiMediaFiles = multiMediaMapper.selectByExample(example);
		return convert(multiMediaFiles);
	}
	
	@Override
	protected Long retrievePrimaryKey(Multimedia multimedia) {
		if (multimedia == null || multimedia.getId() == null) {
			return null;
		}
		String documentId = multimedia.getId();
		
		MultiMediaExample example = new MultiMediaExample();
		example.createCriteria().andDocumentIdEqualTo(documentId);
		List<MultiMedia> files = multiMediaMapper.selectByExample(example);
		return files.isEmpty() ? null : files.get(0).getId();
	}
	
	@Override
	protected Object getUniqueField(Multimedia multiMedia) {
		return multiMedia == null ? multiMedia : multiMedia.getId();
	}
	
	//private Methods
	private Multimedia convert(MultiMedia pgMultiMedia) {
		Multimedia multimedia = new Multimedia();
		multimedia.setId(pgMultiMedia.getDocumentId());
		multimedia.setCaseId(pgMultiMedia.getCaseId());
		multimedia.setProviderId(pgMultiMedia.getProviderId());
		multimedia.setContentType(pgMultiMedia.getContentType());
		multimedia.setFilePath(pgMultiMedia.getFilePath());
		multimedia.setFileCategory(pgMultiMedia.getFileCategory());
		multimedia.setOriginalFileName(pgMultiMedia.getOriginalFileName());
		multimedia.setDateUploaded(pgMultiMedia.getDateUploaded());
		multimedia.setSummary(pgMultiMedia.getSummary());
		return multimedia;
	}
	
	private MultiMedia convert(Multimedia entity, Long primaryKey) {
		if (entity == null) {
			return null;
		}
		MultiMedia pgMultiMedia = new MultiMedia();
		pgMultiMedia.setId(primaryKey);
		pgMultiMedia.setDocumentId(entity.getId());
		pgMultiMedia.setCaseId(entity.getCaseId());
		pgMultiMedia.setProviderId(entity.getProviderId());
		pgMultiMedia.setContentType(entity.getContentType());
		pgMultiMedia.setFilePath(entity.getFilePath());
		pgMultiMedia.setFileCategory(entity.getFileCategory());
		pgMultiMedia.setOriginalFileName(entity.getOriginalFileName());
		pgMultiMedia.setDateUploaded(entity.getDateUploaded());
		pgMultiMedia.setSummary(entity.getSummary());
		return pgMultiMedia;
	}
	
	private List<Multimedia> convert(List<MultiMedia> multiMediaFiles) {
		if (multiMediaFiles == null || multiMediaFiles.isEmpty()) {
			return new ArrayList<>();
		}
		List<Multimedia> convertedList = new ArrayList<>();
		for (MultiMedia pgMultiMedia : multiMediaFiles) {
			Multimedia multimedia = convert(pgMultiMedia);
			if (multimedia != null) {
				convertedList.add(multimedia);
			}
		}
		
		return convertedList;
	}
	
	/**
	 * Method should be used only during Unit testing
	 * Deletes all existing records
	 */
	public void removeAll() {
		multiMediaMapper.deleteByExample(new MultiMediaExample());
		
	}

	@Override
	protected String getSequenceName() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
