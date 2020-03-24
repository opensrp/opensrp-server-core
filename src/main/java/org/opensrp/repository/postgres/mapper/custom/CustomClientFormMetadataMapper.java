package org.opensrp.repository.postgres.mapper.custom;

import org.springframework.lang.NonNull;
import org.apache.ibatis.annotations.Param;
import org.opensrp.domain.IdVersionTuple;
import org.opensrp.domain.postgres.ClientFormMetadata;
import org.opensrp.repository.postgres.mapper.ClientFormMetadataMapper;

import java.util.List;

public interface CustomClientFormMetadataMapper extends ClientFormMetadataMapper {

	int countClientFormMetadataByFormIdentifier(String formIdentifier);

	ClientFormMetadata selectClientFormMetadataByFormVersionAndIdentifier(@Param("formVersion") @NonNull String formVersion,
			@Param("formIdentifier") @NonNull String formIdentifier);

	int insertClientFormMetadata(@NonNull ClientFormMetadata clientFormMetadata);

	List<IdVersionTuple> getAvailableClientFormVersions(@NonNull String formIdentifier);
}
