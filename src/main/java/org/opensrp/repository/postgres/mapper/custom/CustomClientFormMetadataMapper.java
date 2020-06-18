package org.opensrp.repository.postgres.mapper.custom;

import org.apache.ibatis.annotations.Param;
import org.opensrp.domain.IdVersionTuple;
import org.opensrp.domain.postgres.ClientFormMetadata;
import org.opensrp.repository.postgres.mapper.ClientFormMetadataMapper;
import org.springframework.lang.NonNull;

import java.util.List;

public interface CustomClientFormMetadataMapper extends ClientFormMetadataMapper {

	int countClientFormMetadataByFormIdentifier(@Param("formIdentifier") String formIdentifier,
			@Param("isJsonValidator") boolean isJsonValidator);

	ClientFormMetadata selectClientFormMetadataByFormVersionAndIdentifier(@Param("formVersion") @NonNull String formVersion,
			@Param("formIdentifier") @NonNull String formIdentifier, @Param("isJsonValidator") boolean isJsonValidator);

	int insertClientFormMetadata(@NonNull ClientFormMetadata clientFormMetadata);

	List<IdVersionTuple> getAvailableClientFormVersions(@Param("formIdentifier") @NonNull String formIdentifier,
			@Param("isJsonValidator") boolean isJsonValidator);

	List<ClientFormMetadata> getDraftClientFormMetadata(boolean isDraft);

	List<ClientFormMetadata> getJsonWidgetValidatorClientFormMetadata(boolean isJsonValidator);

	List<ClientFormMetadata> getAllClientFormMetadata();
}
