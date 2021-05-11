package org.opensrp.repository.postgres.mapper.custom;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.opensrp.domain.postgres.Client;
import org.opensrp.domain.postgres.ClientMetadataExample;
import org.opensrp.domain.postgres.CustomClient;
import org.opensrp.domain.postgres.HouseholdClient;
import org.opensrp.repository.postgres.mapper.ClientMetadataMapper;
import org.opensrp.search.AddressSearchBean;
import org.opensrp.search.ClientSearchBean;

public interface CustomClientMetadataMapper extends ClientMetadataMapper {
	
	List<Client> selectMany(@Param("example") ClientMetadataExample example, @Param("offset") int offset,
	                        @Param("limit") int limit);
	
	Client selectOne(@Param("example") ClientMetadataExample example);
	
	Client selectByDocumentId(String documentId);
	
	List<Client> selectBySearchBean(@Param("clientBean") ClientSearchBean searchBean,
	                                @Param("addressBean") AddressSearchBean addressSearchBean, @Param("offset") int offset,
	                                @Param("limit") int limit);
	
	HouseholdClient selectHouseholdCountBySearchBean(@Param("clientBean") ClientSearchBean searchBean,
	                                                 @Param("addressBean") AddressSearchBean addressSearchBean);
	
	List<Client> selectByName(@Param("name") String nameMatches, @Param("offset") int offset, @Param("limit") int limit);
	
	List<HouseholdClient> selectMemberCountHouseholdHeadProviderByClients(@Param("example") ClientMetadataExample example,
	                                                                      @Param("clientType") String clientType);
	
	List<CustomClient> selectMembersByRelationshipId(@Param("baseEntityId") String baseEntityId);
	
	List<CustomClient> selectAllClientsBySearchBean(@Param("clientBean") ClientSearchBean searchBean,
	                                                @Param("addressBean") AddressSearchBean addressSearchBean,
	                                                @Param("offset") int offset, @Param("limit") int limit);
	
	HouseholdClient selectCountAllClientsBySearchBean(@Param("clientBean") ClientSearchBean searchBean,
	                                                  @Param("addressBean") AddressSearchBean addressSearchBean);
	
	List<CustomClient> selectHouseholdBySearchBean(@Param("clientBean") ClientSearchBean searchBean,
	                                               @Param("addressBean") AddressSearchBean addressSearchBean,
	                                               @Param("offset") int offset, @Param("limit") int limit);
	
	List<CustomClient> selectANCBySearchBean(@Param("clientBean") ClientSearchBean searchBean,
	                                         @Param("addressBean") AddressSearchBean addressSearchBean,
	                                         @Param("offset") int offset, @Param("limit") int limit);
	
	int selectCountANCBySearchBean(@Param("clientBean") ClientSearchBean searchBean,
	                               @Param("addressBean") AddressSearchBean addressSearchBean);
	
	List<CustomClient> selectChildBySearchBean(@Param("clientBean") ClientSearchBean searchBean,
	                                           @Param("addressBean") AddressSearchBean addressSearchBean,
	                                           @Param("offset") int offset, @Param("limit") int limit);
	
	int selectCountChildBySearchBean(@Param("clientBean") ClientSearchBean searchBean,
	                                 @Param("addressBean") AddressSearchBean addressSearchBean);

	List<String> selectManyIds(@Param("example") ClientMetadataExample example, @Param("offset") int offset,
			@Param("limit") int limit);

	List<Client> selectByLocationIdOfType(@Param("clientType") String clientType, @Param("locationId") String locationId);

	List<Client> selectByLocationIdAndNotOfType(@Param("locationId") String locationId, @Param("clientType") String clientType);

	Long countMany(@Param("example") ClientMetadataExample clientMetadataExample);
}
