package org.opensrp.repository.postgres;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.apache.http.util.TextUtils;
import org.opensrp.domain.IdVersionTuple;
import org.opensrp.domain.postgres.ClientForm;
import org.opensrp.domain.postgres.ClientFormMetadata;
import org.opensrp.repository.ClientFormRepository;
import org.opensrp.repository.postgres.mapper.custom.CustomClientFormMapper;
import org.opensrp.repository.postgres.mapper.custom.CustomClientFormMetadataMapper;
import org.opensrp.service.ClientFormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class ClientFormRepositoryImpl extends BaseRepositoryImpl<ClientForm> implements ClientFormRepository {

	@Autowired
	private CustomClientFormMapper clientFormMapper;

	@Autowired
	private CustomClientFormMetadataMapper clientFormMetadataMapper;

	@Override
	public ClientForm get(long id) {
		return clientFormMapper.selectClientFormById(id);
	}

	@Override
	public int countClientFormByFormIdentifier(String formIdentifier) {
		return countClientFormByFormIdentifier(formIdentifier, false);
	}

	@Override
	public int countClientFormByFormIdentifier(@NonNull String formIdentifier, boolean isJsonValidator) {
		return clientFormMetadataMapper.countClientFormMetadataByFormIdentifier(formIdentifier, isJsonValidator);
	}

	@Override
	public ClientFormMetadata getClientFormMetadata(String formVersion, String formIdentifier) {
		return getClientFormMetadata(formVersion, formIdentifier, false);
	}

	@Override
	public ClientFormMetadata getClientFormMetadata(@NonNull String formVersion, @NonNull String formIdentifier, boolean isJsonValidator) {
		return clientFormMetadataMapper.selectClientFormMetadataByFormVersionAndIdentifier(formVersion, formIdentifier, isJsonValidator);
	}

	@Override
	public List<IdVersionTuple> getAvailableClientFormVersions(String formIdentifier) {
		return getAvailableClientFormVersions(formIdentifier, false);
	}

	@Override
	public List<IdVersionTuple> getAvailableClientFormVersions(@NonNull String formIdentifier, boolean isJsonValidator) {
		return clientFormMetadataMapper.getAvailableClientFormVersions(formIdentifier, isJsonValidator);
	}

	@Override
	public ClientFormMetadata getFormMetadata(long id) {
		return clientFormMetadataMapper.selectByPrimaryKey(id);
	}

	@Nullable
	@Transactional
	@Override
	public ClientFormService.CompleteClientForm create(@NonNull ClientForm clientForm,
			@NonNull ClientFormMetadata clientFormMetadata) {
		Long clientFormId = clientForm.getId();
		Long clientFormMetadataId = clientFormMetadata.getId();
		if ((clientFormId != null && clientFormId != 0) || (clientFormMetadataId != null && clientFormMetadataId != 0)) {
			throw new IllegalArgumentException("ClientForm & ClientFormMetadata Id must be NULL or 0");
		}

		clientFormMapper.insertClientForm(clientForm);
		clientFormMetadata.setId(clientForm.getId());
		int resultClientFormMetadataId = clientFormMetadataMapper.insertClientFormMetadata(clientFormMetadata);
		logger.info("Generated id for Client Form Metadata is " + resultClientFormMetadataId);

		return new ClientFormService.CompleteClientForm(clientForm, clientFormMetadata);
	}

	@Nullable
	@Override
	public ClientFormService.CompleteClientForm create(@NonNull ClientFormService.CompleteClientForm completeClientForm) {
		if (completeClientForm.clientForm == null || completeClientForm.clientFormMetadata == null) {
			return null;
		}

		return create(completeClientForm.clientForm, completeClientForm.clientFormMetadata);
	}

	@Nullable
	@Override
	public List<ClientFormMetadata> getAllClientFormMetadata(boolean isDraft) {
		return clientFormMetadataMapper.getClientFormMetadata(isDraft);
	}

	@Override
	public List<ClientFormMetadata> getAllClientFormMetadata() {
		return clientFormMetadataMapper.getAllClientFormMetadata();
	}

	@Nullable
	@Override
	public ClientForm get(String id) {
		if (TextUtils.isEmpty(id)) {
			return null;
		}

		long clientFormId;
		try {
			clientFormId = Long.parseLong(id);
		}
		catch (NumberFormatException ex) {
			logger.error("Could not retrieve long ClientForm.id from string", ex);
			return null;
		}

		return clientFormMapper.selectClientFormById(clientFormId);
	}

	@Override
	public void add(@Nullable ClientForm entity) {
		throw new UnsupportedOperationException(
				"This operation is unsupported and will lead to integrity issues with ClientFormMetadata");
	}

	@Override
	public void update(@Nullable ClientForm entity) {
		if (entity == null || entity.getId() == null || entity.getId() == 0) {
			return;
		}

		clientFormMapper.updateByPrimaryKeySelective(entity);
	}

	@Override
	public List<ClientForm> getAll() {
		return clientFormMapper.getAll(0, DEFAULT_FETCH_SIZE);
	}

	@Transactional
	@Override
	public void safeRemove(@Nullable ClientForm entity) {
		if (entity == null || entity.getId() == null || entity.getId() == 0L) {
			return;
		}

		clientFormMetadataMapper.deleteByPrimaryKey(entity.getId());
		clientFormMapper.deleteByPrimaryKey(entity.getId());
	}

	@Nullable
	@Override
	protected Object retrievePrimaryKey(@Nullable ClientForm clientForm) {
		return clientForm != null ? clientForm.getId() : null;
	}

	@Nullable
	@Override
	protected Object getUniqueField(@Nullable ClientForm clientForm) {
		return clientForm != null ? clientForm.getId() : null;
	}
}
