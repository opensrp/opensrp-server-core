package org.opensrp.service;

import java.util.List;

import org.smartregister.domain.Client;
import org.opensrp.repository.SearchRepository;
import org.opensrp.search.ClientSearchBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public class SearchService {
	
	private final SearchRepository search;
	
	@Autowired
	public SearchService(SearchRepository search) {
		this.search = search;
	}


	@PreAuthorize("hasRole('CLIENT_VIEW')")
	@PostFilter("hasPermission(filterObject, 'CLIENT_VIEW')")
	public List<Client> searchClient(ClientSearchBean clientSearchBean, String firstName, String middleName, String lastName,
	        Integer limit) {
		return search.findByCriteria(clientSearchBean, firstName, middleName, lastName, limit);
	}

	/**
	 * This method is similar to {@link #searchClient(ClientSearchBean, String, String, String, Integer)}. This method however does not enforce ACL
	 * so that users can search clients globally and not just those within their jurisdiction.
	 */
	public List<Client> searchGlobalClient(ClientSearchBean clientSearchBean, String firstName, String middleName, String lastName,
			Integer limit) {
		return search.findByCriteria(clientSearchBean, firstName, middleName, lastName, limit);
	}
	
}
