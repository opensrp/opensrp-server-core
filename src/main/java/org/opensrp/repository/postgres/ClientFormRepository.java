package org.opensrp.repository.postgres;

import org.opensrp.domain.ClientForm;
import org.springframework.stereotype.Repository;

@Repository("formRepositoryPostgres")
public class ClientFormRepository extends BaseRepositoryImpl<ClientForm> {

    @Override
    protected Long retrievePrimaryKey(ClientForm clientForm) {
        return clientForm.getId();
    }

    @Override
    protected Object getUniqueField(ClientForm clientForm) {
        return clientForm.getId();
    }


}
