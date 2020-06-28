package org.opensrp.repository;

import java.util.List;

import org.smartregister.domain.Client;
import org.opensrp.search.ClientSearchBean;

public interface SearchRepository {
	
	List<Client> findByCriteria(ClientSearchBean clientSearchBean, String firstName, String middleName, String lastName,
	        Integer limit);
}
