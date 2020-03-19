package org.opensrp.repository.postgres;

import org.apache.commons.lang3.StringUtils;
import org.opensrp.domain.Manifest;
import org.opensrp.domain.postgres.ManifestExample;
import org.opensrp.repository.ManifestRepository;
import org.opensrp.repository.postgres.mapper.custom.CustomManifestMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.opensrp.util.Utils.isEmptyList;


@Repository
public class ManifestRepositoryImpl extends BaseRepositoryImpl<Manifest> implements ManifestRepository {

    @Autowired
    private CustomManifestMapper manifestMapper;

    @Override
    public Manifest get(String id) {
        if (StringUtils.isBlank(id)) {
            return null;
        }

        Long myID = Long.parseLong(id);
        org.opensrp.domain.postgres.Manifest pgManifest = manifestMapper.selectByIdentifier(myID);
        if (pgManifest == null) {
            return null;
        }

        return convert(pgManifest);
    }

    @Override
    @Transactional
    public void add(Manifest entity) {
        if (getUniqueField(entity) == null) {
            return;
        }

        if (retrievePrimaryKey(entity) != null) { // Manifest already added
            return;
        }

        org.opensrp.domain.postgres.Manifest pgManifest = convert(entity, null);
        if (pgManifest == null) {
            return;
        }
        manifestMapper.insertSelectiveAndSetId(pgManifest);

    }

    @Override
    @Transactional
    public void update(Manifest entity) {
        if (getUniqueField(entity) == null) {
            return;
        }

        Long id = retrievePrimaryKey(entity);
        if (id == null) { // Manifest does not exist
            return;
        }

        org.opensrp.domain.postgres.Manifest pgManifest = convert(entity, id);
        if (pgManifest == null) {
            return;
        }

        manifestMapper.updateByPrimaryKey(pgManifest);
    }

    @Override
    public List<Manifest> getAll() {
        ManifestExample manifestExample = new ManifestExample();
        List<org.opensrp.domain.postgres.Manifest> pgManifestList = manifestMapper.selectMany(manifestExample, 0,
                DEFAULT_FETCH_SIZE);
        return convert(pgManifestList);
    }

    @Override
    public Manifest getManifestByAppId(String appId) {
        if (StringUtils.isBlank(appId)) {
            return null;
        }
        Long myID = Long.parseLong(appId);
        List<org.opensrp.domain.postgres.Manifest> manifestList = manifestMapper.selectByAppId(myID);

        if(manifestList == null ) {
        	return null;
        }
     	return convert(manifestList.get(0));
    }

    @Override
    @Transactional
    public void safeRemove(Manifest entity) {
        if (entity == null) {
            return;
        }

        Long id = retrievePrimaryKey(entity);
        if (id == null) {
            return;
        }

        manifestMapper.deleteByPrimaryKey(id);
    }

    @Override
    public org.opensrp.domain.postgres.Manifest getManifest(int id) {
        if (id == 0) {
            return null;
        }

        ManifestExample manifestExample = new ManifestExample();
        List<org.opensrp.domain.postgres.Manifest> manifestList = manifestMapper.selectByExample(manifestExample);

        return isEmptyList(manifestList) ? null : manifestList.get(0);

    }

    @Override
    protected Long retrievePrimaryKey(Manifest manifest) {
        Object uniqueId = getUniqueField(manifest);
        if (uniqueId == null) {
            return null;
        }

        Long identifier = Long.parseLong(uniqueId.toString());

        org.opensrp.domain.postgres.Manifest pgManifest = manifestMapper.selectByIdentifier(identifier);
        if (pgManifest == null) {
            return null;
        }
        return pgManifest.getId();
    }

    @Override
    protected Object getUniqueField(Manifest manifest) {
        if (manifest == null) {
            return null;
        }
        return manifest.getId();
    }

    private Manifest convert(org.opensrp.domain.postgres.Manifest pgManifest) {
        if (pgManifest == null || pgManifest.getJson() == null || !(pgManifest.getJson() instanceof Manifest)) {
            return null;
        }

        return (Manifest) pgManifest.getJson();
    }

    private org.opensrp.domain.postgres.Manifest convert(Manifest manifest, Long primaryKey) {
        if (manifest == null) {
            return null;
        }

        org.opensrp.domain.postgres.Manifest pgManifest = new org.opensrp.domain.postgres.Manifest();
        pgManifest.setId(primaryKey);
        pgManifest.setJson(manifest);
        pgManifest.setAppId(manifest.getAppId());
        pgManifest.setAppVersion(manifest.getAppVersion());

        if (manifest.getCreatedAt() != null)
            pgManifest.setCreatedAt(manifest.getCreatedAt());
        if (manifest.getUpdatedAt() != null)
            pgManifest.setUpdatedAt(manifest.getUpdatedAt());

        return pgManifest;
    }

    private List<Manifest> convert(List<org.opensrp.domain.postgres.Manifest> manifests) {
        if (manifests == null || manifests.isEmpty()) {
            return new ArrayList<>();
        }

        List<Manifest> convertedManifests = new ArrayList<>();
        for (org.opensrp.domain.postgres.Manifest manifest : manifests) {
            Manifest convertedManifest = convert(manifest);
            if (convertedManifest != null) {
                convertedManifests.add(convertedManifest);
            }
        }

        return convertedManifests;
    }
}