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

import javax.transaction.InvalidTransactionException;
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
	public int countClientFormByFormIdentifier(@NonNull String formIdentifier) {
		return clientFormMetadataMapper.countClientFormMetadataByFormIdentifier(formIdentifier);
	}

	@Override
	public ClientFormMetadata getClientFormMetadata(@NonNull String formVersion, @NonNull String formIdentifier) {
		return clientFormMetadataMapper.selectClientFormMetadataByFormVersionAndIdentifier(formVersion, formIdentifier);
	}

	@Override
	public List<IdVersionTuple> getAvailableClientFormVersions(@NonNull String formIdentifier) {
		return clientFormMetadataMapper.getAvailableClientFormVersions(formIdentifier);
	}

	@Override
	public ClientFormMetadata getFormMetadata(long id) {
		return clientFormMetadataMapper.selectByPrimaryKey(id);
	}

	@Nullable
	@Transactional(rollbackFor = { InvalidTransactionException.class })
	@Override
	public ClientFormService.CompleteClientForm create(@NonNull ClientForm clientForm,
			@NonNull ClientFormMetadata clientFormMetadata) throws InvalidTransactionException {
		Long clientFormId = clientForm.getId();
		Long clientFormMetadataId = clientFormMetadata.getId();
		if ((clientFormId != null && clientFormId != 0) || (clientFormMetadataId != null && clientFormMetadataId != 0)) {
			logger.error("ClientForm & ClientFormMetadata Id must be NULL or 0", new Exception());
			return null;
		}

		int rowsAffected = clientFormMapper.insertClientForm(clientForm);
		if (rowsAffected < 1) {
			throw new InvalidTransactionException(
					"ClientForm was not created and transaction cannot continue to created ClientFormMetadata");
		}

		logger.info("Generated id for Client form is " + clientForm.getId());

		clientFormMetadata.setId(clientForm.getId());
		int resultClientFormMetadataId = clientFormMetadataMapper.insertClientFormMetadata(clientFormMetadata);
		logger.info("Generated id for Client Form Metadata is " + resultClientFormMetadataId);

		return new ClientFormService.CompleteClientForm(clientForm, clientFormMetadata);
	}

	@Nullable
	@Override
	public ClientFormService.CompleteClientForm create(@NonNull ClientFormService.CompleteClientForm completeClientForm)
			throws InvalidTransactionException {
		if (completeClientForm.clientForm == null || completeClientForm.clientFormMetadata == null) {
			return null;
		}

		return create(completeClientForm.clientForm, completeClientForm.clientFormMetadata);
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
		if (entity == null || (entity.getId() != null && entity.getId() == 0)) {
			return;
		}

		clientFormMapper.insertClientForm(entity);
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
		return clientFormMapper.getAll(0, 10000);
	}

	@Override
	public void safeRemove(@Nullable ClientForm entity) {
		if (entity == null || entity.getId() == null || entity.getId() == 0L) {
			return;
		}

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
