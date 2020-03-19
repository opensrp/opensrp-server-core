package org.opensrp.repository.postgres.mapper.custom;

import edu.umd.cs.findbugs.annotations.NonNull;
import org.opensrp.domain.postgres.ClientForm;
import org.opensrp.repository.postgres.mapper.ClientFormMapper;

public interface CustomClientFormMapper extends ClientFormMapper {

    int selectClientFormById(int id);

    int insertClientForm(@NonNull ClientForm clientForm);
}
