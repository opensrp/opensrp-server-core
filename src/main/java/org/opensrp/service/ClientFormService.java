package org.opensrp.service;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.opensrp.domain.IdVersionTuple;
import org.opensrp.domain.postgres.ClientForm;
import org.opensrp.domain.postgres.ClientFormMetadata;
import org.opensrp.repository.ClientFormRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.InvalidTransactionException;
import java.util.List;

@Service
public class ClientFormService {

	private ClientFormRepository clientFormRepository;

	private static Logger logger = LoggerFactory.getLogger(ClientFormService.class.toString());

	@Autowired
	public void setClientFormRepository(ClientFormRepository clientFormRepository) {
		this.clientFormRepository = clientFormRepository;
	}

	public boolean isClientFormExists(String formIdentifier) {
		return clientFormRepository.countClientFormByFormIdentifier(formIdentifier) > 0;
	}

	@Nullable
	public ClientFormMetadata getClientFormMetadataByIdentifierAndVersion(String formIdentifier, String formVersion) {
		return clientFormRepository.getClientFormMetadata(formVersion, formIdentifier);
	}

	public ClientForm getClientFormById(Long id) {
		return clientFormRepository.get(id);
	}

	public List<IdVersionTuple> getAvailableClientFormMetadataVersionByIdentifier(String formIdentifier) {
		return clientFormRepository.getAvailableClientFormVersions(formIdentifier);
	}

	public ClientFormMetadata getClientFormMetadataById(long formId) {
		return clientFormRepository.getFormMetadata(formId);
	}

	@Nullable
	public CompleteClientForm addClientForm(@NonNull ClientForm clientForm, @NonNull ClientFormMetadata clientFormMetadata) {
		// Check if the same client form with that version & identifier exists
		ClientFormMetadata clientFormMetadataResult = clientFormRepository
				.getClientFormMetadata(clientFormMetadata.getVersion(), clientFormMetadata.getIdentifier());
		if (clientFormMetadataResult != null) {
			logger.error("ClientFormMetadata with version " + clientFormMetadata.getVersion() + " and Identifier "
					+ clientFormMetadata.getIdentifier() + " Already exists", new Exception());
			return null;
		}

		try {
			return clientFormRepository.create(clientForm, clientFormMetadata);
		}
		catch (InvalidTransactionException e) {
			logger.error("An error occurred trying to save the client form", e);
		}

		return null;
	}

	public static class CompleteClientForm {

		public ClientForm clientForm;

		public ClientFormMetadata clientFormMetadata;

		public CompleteClientForm(@NonNull ClientForm clientForm, @NonNull ClientFormMetadata clientFormMetadata) {
			this.clientForm = clientForm;
			this.clientFormMetadata = clientFormMetadata;
		}
	}

}
