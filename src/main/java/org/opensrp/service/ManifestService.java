package org.opensrp.service;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.opensrp.domain.Manifest;
import org.opensrp.repository.ManifestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ManifestService {

    private static Logger logger = LoggerFactory.getLogger(ManifestService.class.toString());

    private ManifestRepository manifestRepository;

    @Autowired
    public void setManifestRepository(ManifestRepository manifestRepository) {
        this.manifestRepository = manifestRepository;
    }
    public ManifestRepository getManifestRepository() {
        return manifestRepository;
    }

    public List<Manifest> getAllManifest() {
        return manifestRepository.getAll();
    }

    public void addOrUpdateManifest(Manifest manifest) {
        if (StringUtils.isBlank(manifest.getIdentifier()))
            throw new IllegalArgumentException("Id not specified");
        manifest.setUpdatedAt(new DateTime());
        if (manifestRepository.get(manifest.getIdentifier()) != null) {
            manifestRepository.update(manifest);
        } else {
            manifest.setCreatedAt(new DateTime());
            manifestRepository.add(manifest);
        }
    }

    public Manifest addManifest(Manifest manifest) {
        if (StringUtils.isBlank(manifest.getIdentifier()))
            throw new IllegalArgumentException("Id not specified");
        manifest.setCreatedAt(new DateTime());
        manifest.setUpdatedAt(new DateTime());
        manifestRepository.add(manifest);
        return manifest;

    }

    public Manifest updateManifest(Manifest manifest) {
        if (StringUtils.isBlank(manifest.getIdentifier()))
            throw new IllegalArgumentException("Id not specified");
        manifest.setUpdatedAt(new DateTime());
        manifestRepository.update(manifest);
        return manifest;
    }

    public Manifest getManifest(String identifier) {
        if (StringUtils.isBlank(identifier))
            return null;
        return getManifestRepository().get(identifier);
    }

    public Set<String> saveManifests(List<Manifest> manifests) {
        Set<String> manifestWithErrors = new HashSet<>();
        for (Manifest manifest : manifests) {
            try {
                addOrUpdateManifest(manifest);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                manifestWithErrors.add(manifest.getIdentifier());
            }
        }
        return manifestWithErrors;
    }


    public void deleteManifest(Manifest manifest) {
        if (StringUtils.isBlank(manifest.getIdentifier())) {
            throw new IllegalArgumentException("Identifier not specified");
        }
        manifestRepository.safeRemove(manifest);
    }

    public Manifest getManifestByAppId(String appId) {
        if (StringUtils.isBlank(appId))
            return null;
        return manifestRepository.getManifestByAppId(appId);
    }

    public List<Manifest> getManifestsByAppId(String appId) {
        if (StringUtils.isBlank(appId))
            return null;
        return manifestRepository.getManifestsByAppId(appId);
    }

    public Manifest getManifest(@NonNull String appId, @NonNull String appVersion) {
        return manifestRepository.getManifest(appId, appVersion);
    }
}
