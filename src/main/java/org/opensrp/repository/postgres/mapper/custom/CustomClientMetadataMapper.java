package org.opensrp.repository.postgres.mapper.custom;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.opensrp.domain.postgres.Client;
import org.opensrp.domain.postgres.ClientMetadataExample;
import org.opensrp.domain.postgres.ClientCustomField;
import org.opensrp.domain.postgres.CustomClient;
import org.opensrp.repository.postgres.mapper.ClientMetadataMapper;
import org.opensrp.search.AddressSearchBean;
import org.opensrp.search.ClientSearchBean;

public interface CustomClientMetadataMapper extends ClientMetadataMapper {
	
	List<Client> selectMany(@Param("example") ClientMetadataExample example, @Param("offset") int offset,
	                        @Param("limit") int limit);
	
	Client selectOne(String baseEntityId);
	
	Client selectByDocumentId(String documentId);
	
	List<Client> selectBySearchBean(@Param("clientBean") ClientSearchBean searchBean,
	                                @Param("addressBean") AddressSearchBean addressSearchBean, @Param("offset") int offset,
	                                @Param("limit") int limit);
	
	ClientCustomField selectCountBySearchBean(@Param("clientBean") ClientSearchBean searchBean,
	                                          @Param("addressBean") AddressSearchBean addressSearchBean);
	
	List<Client> selectByName(@Param("name") String nameMatches, @Param("offset") int offset, @Param("limit") int limit);
	
	List<ClientCustomField> selectMemberCountHouseholdHeadProviderByClients(@Param("example") ClientMetadataExample example,
	                                                                        @Param("clientType") String clientType);
	
	List<CustomClient> selectMembersByRelationshipId(@Param("baseEntityId") String baseEntityId);
	
	List<CustomClient> selectAllClients(@Param("clientBean") ClientSearchBean searchBean,
	                                    @Param("addressBean") AddressSearchBean addressSearchBean,
	                                    @Param("offset") int offset, @Param("limit") int limit);
	
	ClientCustomField selectCountAllClients(@Param("clientBean") ClientSearchBean searchBean,
	                                        @Param("addressBean") AddressSearchBean addressSearchBean);
}
