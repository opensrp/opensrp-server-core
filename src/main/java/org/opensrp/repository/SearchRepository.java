package org.opensrp.repository;

import org.opensrp.search.ClientSearchBean;
import org.smartregister.domain.Client;

import java.util.List;

public interface SearchRepository {

    List<Client> findByCriteria(ClientSearchBean clientSearchBean, String firstName, String middleName, String lastName,
                                Integer limit);
}
