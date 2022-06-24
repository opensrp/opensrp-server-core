package org.opensrp.repository.postgres.mapper.custom;

import org.apache.ibatis.annotations.Param;
import org.opensrp.domain.postgres.ClientForm;
import org.opensrp.repository.postgres.mapper.ClientFormMapper;
import org.springframework.lang.NonNull;

import java.util.List;

public interface CustomClientFormMapper extends ClientFormMapper {

    ClientForm selectClientFormById(Long id);

    int insertClientForm(@NonNull ClientForm clientForm);

    List<ClientForm> getAll(@Param("offset") int offset, @Param("batchSize") int batchSize);

    ClientForm getMostRecentFormValidator(@NonNull String formIdentifier);
}
