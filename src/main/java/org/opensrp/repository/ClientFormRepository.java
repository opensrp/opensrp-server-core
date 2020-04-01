package org.opensrp.repository;

import org.springframework.lang.NonNull;
import org.opensrp.domain.IdVersionTuple;
import org.opensrp.domain.postgres.ClientForm;
import org.opensrp.domain.postgres.ClientFormMetadata;
import org.opensrp.service.ClientFormService;
import java.util.List;

public interface ClientFormRepository extends BaseRepository<ClientForm> {

	ClientForm get(long id);

	int countClientFormByFormIdentifier(@NonNull String formIdentifier);

	ClientFormMetadata getClientFormMetadata(@NonNull String formVersion, @NonNull String formIdentifier);

	List<IdVersionTuple> getAvailableClientFormVersions(@NonNull String formIdentifier);

	ClientFormMetadata getFormMetadata(long id);

	ClientFormService.CompleteClientForm create(@NonNull ClientForm clientForm,
			@NonNull ClientFormMetadata clientFormMetadata);

	ClientFormService.CompleteClientForm create(@NonNull ClientFormService.CompleteClientForm completeClientForm);

}
