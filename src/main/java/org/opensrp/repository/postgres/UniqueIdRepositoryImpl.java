package org.opensrp.repository.postgres;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.opensrp.domain.UniqueId;
import org.opensrp.domain.postgres.UniqueIdExample;
import org.opensrp.repository.UniqueIdRepository;
import org.opensrp.repository.postgres.mapper.custom.CustomUniqueIdMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UniqueIdRepositoryImpl extends BaseRepositoryImpl<UniqueId> implements UniqueIdRepository {

    @Autowired
    private CustomUniqueIdMapper uniqueIdMapper;

    @Override
    public List<UniqueId> getNotUsedIds( int limit) {
        UniqueIdExample example = new UniqueIdExample();
        example.createCriteria().andStatusEqualTo(UniqueId.STATUS_NOT_USED);
        int fetchLimit = limit > 0 ? limit : DEFAULT_FETCH_SIZE;
        List<org.opensrp.domain.postgres.UniqueId> pgUniqueIds = uniqueIdMapper.selectMany(example, 0, fetchLimit);
        return convert(pgUniqueIds);
    }

    @Override
    public List<String> getNotUsedIdsAsString(int limit) {
        UniqueIdExample example = new UniqueIdExample();
        example.createCriteria().andStatusEqualTo(UniqueId.STATUS_NOT_USED);
        int fetchLimit = limit > 0 ? limit : DEFAULT_FETCH_SIZE;

        return uniqueIdMapper.selectManyIds(example, 0, fetchLimit);
    }

    @Override
    public Long totalUnUsedIds() {
        UniqueIdExample example = new UniqueIdExample();
        example.createCriteria().andStatusEqualTo(UniqueId.STATUS_NOT_USED);
        long unUsedIdsCount = uniqueIdMapper.countByExample(example);
        return unUsedIdsCount;
    }

    @Override
    public Long[] markAsUsed(List<String> ids) {

        List<Long> updatedrecords = new ArrayList<>();
        UniqueIdExample example = new UniqueIdExample();
        example.createCriteria().andOpenmrsIdIn(ids);
        List<org.opensrp.domain.postgres.UniqueId> uniqueIdsToMarkAsUnused = uniqueIdMapper.selectByExample(example);

        if (uniqueIdsToMarkAsUnused == null || uniqueIdsToMarkAsUnused.isEmpty()) {
            return null;
        }

        for (org.opensrp.domain.postgres.UniqueId uniqueId: uniqueIdsToMarkAsUnused){
            uniqueId.setStatus(UniqueId.STATUS_USED);
            uniqueIdMapper.updateByPrimaryKey(uniqueId);
            updatedrecords.add(uniqueId.getId());
        }
        return updatedrecords.toArray(new Long[uniqueIdsToMarkAsUnused.size()]);
    }

    @Override
    public void clearTable() {
        UniqueIdExample example = new UniqueIdExample();
        uniqueIdMapper.deleteByExample(example);
    }

    @Override
    public boolean checkIfClientExists(String usedBy, String location) {
        UniqueIdExample example = new UniqueIdExample();
        example.createCriteria().andUsedByEqualTo(usedBy).andLocationEqualTo(location);

        List<org.opensrp.domain.postgres.UniqueId> uniqueIds = uniqueIdMapper.selectByExample(example);

        return (uniqueIds != null && !uniqueIds.isEmpty());
    }

    @Override
    public UniqueId findByIdentifierSourceOrderByIdDesc(Long idSource) {
        UniqueIdExample example = new UniqueIdExample();
        example.createCriteria().andIdSourceEqualTo(idSource);
        example.setOrderByClause("id DESC");

        List<org.opensrp.domain.postgres.UniqueId> uniqueIds = uniqueIdMapper.selectByExample(example);
        List<UniqueId> convertedUniqueIds = convert(uniqueIds);
        return convertedUniqueIds != null && convertedUniqueIds.size() >= 1 ? convertedUniqueIds.get(0) : null;
    }

    @Override
    public Set<String> findReservedIdentifiers() {
        UniqueIdExample example = new UniqueIdExample();
        example.createCriteria().andIsReservedEqualTo(Boolean.TRUE).andIdentifierIsNotNull();

        List<org.opensrp.domain.postgres.UniqueId> uniqueIds = uniqueIdMapper.selectByExample(example);
       return getReservedIdentifiers(convert(uniqueIds));
        
    }

    @Override
    public void markIdentifierAsUsed(String identifier) {
        UniqueIdExample uniqueIdExample = new UniqueIdExample();
        uniqueIdExample.createCriteria().andIdentifierEqualTo(identifier);
        org.opensrp.domain.postgres.UniqueId uniqueId = new org.opensrp.domain.postgres.UniqueId();
        uniqueId.setStatus(UniqueId.STATUS_USED);
        uniqueIdMapper.updateByExampleSelective(uniqueId, uniqueIdExample);
    }

    @Override
    public UniqueId get(String id) {
        if (StringUtils.isBlank(id)) {
            return null;
        }

         return convert(findUniqueIdByOpenMrsId(id));
    }

    private org.opensrp.domain.postgres.UniqueId findUniqueIdByOpenMrsId(String openMrsId) {
        if (StringUtils.isBlank(openMrsId)) {
            return null;
        }

        UniqueIdExample example = new UniqueIdExample();
        example.createCriteria().andOpenmrsIdEqualTo(openMrsId);
        List<org.opensrp.domain.postgres.UniqueId> uniqueIds = uniqueIdMapper.selectByExample(example);
        return uniqueIds.isEmpty() ? null : uniqueIds.get(0);
    }

    @Override
    public void add(UniqueId entity) {
        if (getUniqueField(entity) == null) {
            return;
        }

        if (retrievePrimaryKey(entity) != null){// unique id already exists
            return;
        }

        org.opensrp.domain.postgres.UniqueId pgUniqueId = convert(entity, null);
        if (pgUniqueId == null) {
            return;
        }

        uniqueIdMapper.insertSelective(pgUniqueId);
    }

    @Override
    public void update(UniqueId entity) {
        if (getUniqueField(entity) == null){
            return;
        }

        Long id = retrievePrimaryKey(entity);
        if (id == null){ // unique id does not exist
            return;
        }

        org.opensrp.domain.postgres.UniqueId pgUiqueId = convert(entity, id);
        if (pgUiqueId == null) {
            return;
        }

        uniqueIdMapper.updateByPrimaryKey(pgUiqueId);

    }

    @Override
    public List<UniqueId> getAll() {
        return null;
    }

    @Override
    public void safeRemove(UniqueId entity) {
        if (getUniqueField(entity) == null){
            return;
        }

        Long id = retrievePrimaryKey(entity);
        if (id == null){ // unique id does not exist
            return;
        }

        uniqueIdMapper.deleteByPrimaryKey(id);

    }

    private List<UniqueId> convert(List<org.opensrp.domain.postgres.UniqueId> pgEntities) {
        List<UniqueId> uniqueIds = new ArrayList<>();
        for (org.opensrp.domain.postgres.UniqueId entity: pgEntities) {
            uniqueIds.add(convert(entity));
        }
        return uniqueIds;
    }

    private UniqueId convert(org.opensrp.domain.postgres.UniqueId pgUniqueId) {
        UniqueId uniqueId = new UniqueId();
        uniqueId.setId(pgUniqueId.getId());
        uniqueId.setCreatedAt(pgUniqueId.getCreatedAt());
        uniqueId.setLocation(pgUniqueId.getLocation());
        uniqueId.setOpenmrsId(pgUniqueId.getOpenmrsId());
        uniqueId.setStatus(pgUniqueId.getStatus());
        uniqueId.setUsedBy(pgUniqueId.getUsedBy());
        uniqueId.setIdentifier(pgUniqueId.getIdentifier());
        uniqueId.setIdSource(pgUniqueId.getIdSource());
        uniqueId.setReserved(pgUniqueId.getIsReserved());
        return  uniqueId;
    }

    @Override
    protected Long retrievePrimaryKey(UniqueId uniqueId) {
        Object uniqueOpenMrsIdentifier = getUniqueField(uniqueId);
        Object uniqueIdentifier = null;
        Boolean fromOpenMrs = true;
        if (uniqueOpenMrsIdentifier == null || StringUtils.isEmpty(uniqueOpenMrsIdentifier.toString())) {
            uniqueIdentifier = getUniqueIdentifierField(uniqueId);
            fromOpenMrs = false;
        }

        if (uniqueOpenMrsIdentifier == null || uniqueIdentifier == null) {
            return null;
        }

        String identifier;
        UniqueIdExample example;
        List<org.opensrp.domain.postgres.UniqueId> pgEntities;

        if (fromOpenMrs == Boolean.TRUE) {
            identifier = uniqueOpenMrsIdentifier.toString();
            example = new UniqueIdExample();
            example.createCriteria().andOpenmrsIdEqualTo(identifier);
            pgEntities = uniqueIdMapper.selectByExample(example);
        } else {
            identifier = uniqueIdentifier.toString();
            example = new UniqueIdExample();
            example.createCriteria().andIdentifierEqualTo(identifier);
            pgEntities = uniqueIdMapper.selectByExample(example);
        }

        return pgEntities.isEmpty() ? null : pgEntities.get(0).getId();
    }

    @Override
    protected Object getUniqueField(UniqueId uniqueId) {
        if (uniqueId == null) {
            return null;
        }
        return uniqueId.getOpenmrsId();
    }
    
    protected  Object getUniqueIdentifierField(UniqueId uniqueId) {
        if (uniqueId == null) {
            return null;
        }
        return uniqueId.getIdentifier();
    }

    private org.opensrp.domain.postgres.UniqueId convert(UniqueId uniqueId, Long primaryKey) {
        if (uniqueId == null) {
            return null;
        }

        org.opensrp.domain.postgres.UniqueId pgUniqueId = new org.opensrp.domain.postgres.UniqueId();
        pgUniqueId.setId(primaryKey);
        pgUniqueId.setCreatedAt(uniqueId.getCreatedAt());
        pgUniqueId.setLocation(uniqueId.getLocation());
        pgUniqueId.setOpenmrsId(uniqueId.getOpenmrsId());
        pgUniqueId.setStatus(uniqueId.getStatus());
        pgUniqueId.setUsedBy(uniqueId.getUsedBy());
        pgUniqueId.setUpdatedAt(uniqueId.getUpdatedAt());
        pgUniqueId.setIdentifier(uniqueId.getIdentifier());
        pgUniqueId.setIdSource(uniqueId.getIdSource());
        pgUniqueId.setIsReserved(uniqueId.isReserved());
        
        return  pgUniqueId;
    }
    
    private Set<String> getReservedIdentifiers(List<UniqueId> uniqueIds) {
        Set<String> reservedIdentifiers = new HashSet<>();
        for(UniqueId uniqueId : uniqueIds) {
            reservedIdentifiers.add(uniqueId.getIdentifier());
        }
        return reservedIdentifiers;
    }
    
}
