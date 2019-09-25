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

@Repository
public class ManifestRepositoryImpl extends BaseRepositoryImpl<Manifest> implements ManifestRepository {

    @Autowired
    private CustomManifestMapper manifestMapper;

    @Override
    public Manifest get(String id) {
        if (StringUtils.isBlank(id)) {
            return null;
        }

        org.opensrp.domain.postgres.Manifest pgManifest = manifestMapper.selectByIdentifier(id);
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

        manifestMapper.insertSelective(pgManifest);

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
        List<org.opensrp.domain.postgres.Manifest> tasks = manifestMapper.selectMany(new ManifestExample(), 0,
                DEFAULT_FETCH_SIZE);
        return convert(tasks);
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
    protected Long retrievePrimaryKey(Manifest manifest) {
        Object uniqueId = getUniqueField(manifest);
        if (uniqueId == null) {
            return null;
        }

        String identifier = uniqueId.toString();

        return StringUtils.isNotBlank(identifier) ? Long.parseLong(identifier) : null;
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

        return pgManifest;
    }

    private List<Manifest> convert(List<org.opensrp.domain.postgres.Manifest> tasks) {
        if (tasks == null || tasks.isEmpty()) {
            return new ArrayList<>();
        }

        List<Manifest> convertedTasks = new ArrayList<>();
        for (org.opensrp.domain.postgres.Manifest manifest : tasks) {
            Manifest convertedTask = convert(manifest);
            if (convertedTask != null) {
                convertedTasks.add(convertedTask);
            }
        }

        return convertedTasks;
    }
}