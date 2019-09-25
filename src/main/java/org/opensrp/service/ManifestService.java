package org.opensrp.service;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.opensrp.domain.Manifest;
import org.opensrp.repository.ManifestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    public List<Manifest> getAllManifest() {
        return manifestRepository.getAll();
    }

    public void addOrUpdateManifest(Manifest manifest) {
        if (StringUtils.isBlank(manifest.getId()))
            throw new IllegalArgumentException("Id not specified");
        manifest.setUpdatedAt(new DateTime());
        if (manifestRepository.get(manifest.getId()) != null) {
            manifestRepository.update(manifest);
        } else {
            manifest.setCreatedAt(new DateTime());
            manifestRepository.add(manifest);
        }
    }

    public Manifest addManifest(Manifest manifest) {
        if (StringUtils.isBlank(manifest.getId()))
            throw new IllegalArgumentException("Id not specified");
        manifest.setCreatedAt(new DateTime());
        manifest.setUpdatedAt(new DateTime());
        manifestRepository.add(manifest);
        return manifest;

    }

    public Manifest updateManifest(Manifest manifest) {
        if (StringUtils.isBlank(manifest.getId()))
            throw new IllegalArgumentException("Id not specified");
        manifest.setUpdatedAt(new DateTime());
        manifestRepository.update(manifest);
        return manifest;
    }

    public Manifest getManifest(String identifier) {
        if (StringUtils.isBlank(identifier))
            return null;
        return manifestRepository.get(identifier);
    }

    public Set<String> saveManifests(List<Manifest> manifests) {
        Set<String> manifestWithErrors = new HashSet<>();
        for (Manifest manifest : manifests) {
            try {
                addOrUpdateManifest(manifest);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                manifestWithErrors.add(manifest.getId());
            }
        }
        return manifestWithErrors;
    }
}
