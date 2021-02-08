package org.opensrp.repository;

import org.opensrp.domain.ClientMigrationFile;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.List;

public interface ClientMigrationFileRepository extends BaseRepository<ClientMigrationFile> {

    @Nullable
    ClientMigrationFile getClientMigrationFileByFilename(String filename);

    @Nullable
    List<ClientMigrationFile> getClientMigrationFileByVersion(int version);

    @Nullable
    List<ClientMigrationFile> getClientMigrationFileByManifestId(int manifestId);

    @Nullable
    ClientMigrationFile getClientMigrationFileById(@NonNull long id);

    @Nullable
    ClientMigrationFile getClientMigrationFileByIdentifier(@NonNull String identifier);

    @Nullable
    List<ClientMigrationFile> getAll(int limit);
}
