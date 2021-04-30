package org.opensrp.repository.postgres.mapper.custom;

import org.apache.ibatis.annotations.Param;
import org.opensrp.domain.postgres.ClientMigrationFile;
import org.opensrp.repository.postgres.mapper.ClientMigrationFileMapper;

import java.util.List;

public interface CustomClientMigrationFileMapper extends ClientMigrationFileMapper {

    ClientMigrationFile selectByFilename(String filename);

    List<ClientMigrationFile> selectByVersion(int version);

    List<ClientMigrationFile> selectByManifestId(int manifestId);

    List<ClientMigrationFile> selectByManifestIdentifier(int manifestId);

    ClientMigrationFile selectByIdentifier(String identifier);

    List<ClientMigrationFile> getAll(@Param("offset") int offset, @Param("batchSize") int batchSize);

}
