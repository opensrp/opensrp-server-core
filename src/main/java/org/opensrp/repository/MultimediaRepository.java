package org.opensrp.repository;

import org.opensrp.domain.Multimedia;
import org.springframework.lang.NonNull;

import java.util.List;

public interface MultimediaRepository extends BaseRepository<Multimedia> {

    Multimedia findByCaseId(String entityId);

    List<Multimedia> all(String providerId);

    /**
     * Returns a {@link List} of {@link Multimedia} objects that match the given parameters
     *
     * @param entityId     The baseEntityId of the client who owns the multimedia file(s)
     * @param contentType  The contentType of the multimedia file(s) to be fetched
     * @param fileCategory The file category of the multimedia file(s)
     * @return A {@link List} of {@link Multimedia} objects
     */
    List<Multimedia> get(@NonNull String entityId, @NonNull String contentType, @NonNull String fileCategory);

    /***
     * Returns a {@link List} of {@link Multimedia} objects created by the provider and match content type and paginated by
     * given parameters
     *
     * @param providerID
     * @param fileCategory
     * @param offset
     * @param count
     * @return
     */
    List<Multimedia> getByProviderID(@NonNull String providerID, @NonNull String fileCategory, int offset, int count);
}
