package org.opensrp.repository.postgres.mapper.custom;

import org.apache.ibatis.annotations.Param;
import org.opensrp.domain.postgres.Settings;
import org.opensrp.domain.postgres.SettingsMetadataExample;
import org.opensrp.repository.postgres.mapper.SettingsMetadataMapper;

import java.util.List;

public interface CustomSettingMetadataMapper extends SettingsMetadataMapper {
	
	List<Settings> selectMany(@Param("example") SettingsMetadataExample settingsExample, @Param("offset") int offset,
	                          @Param("limit") int limit);
	
	Settings selectByDocumentId(String documentId);
	
}

