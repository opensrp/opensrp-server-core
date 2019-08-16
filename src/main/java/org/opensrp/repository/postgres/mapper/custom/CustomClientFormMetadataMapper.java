package org.opensrp.repository.postgres.mapper.custom;

import edu.umd.cs.findbugs.annotations.NonNull;
import org.opensrp.domain.IdVersionTuple;
import org.opensrp.domain.postgres.ClientFormMetadata;
import org.opensrp.repository.postgres.mapper.ClientFormMetadataMapper;

import java.util.List;

public interface CustomClientFormMetadataMapper extends ClientFormMetadataMapper {

    int countClientFormMetadataByFormIdentifier(String formIdentifier);

    ClientFormMetadata selectClientFormMetadataByFormVersionAndIdentifier(@NonNull String formVersion, @NonNull String formIdentifier);

    int insertClientFormMetadata(@NonNull ClientFormMetadata clientFormMetadata);

    List<IdVersionTuple> getAvailableClientFormVersions(@NonNull String formIdentifier);
}
