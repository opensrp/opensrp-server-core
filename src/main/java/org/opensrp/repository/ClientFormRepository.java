package org.opensrp.repository;

import org.opensrp.domain.postgres.ClientForm;

public interface ClientFormRepository extends BaseRepository<ClientForm>{
	
	ClientForm get(int id);
}
