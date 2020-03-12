package org.opensrp.repository.postgres;

import java.util.ArrayList;
import java.util.List;
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
    CustomUniqueIdMapper uniqueIdMapper;

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
        uniqueId.setCreatedAt(pgUniqueId.getCreatedAt());
        uniqueId.setLocation(pgUniqueId.getLocation());
        uniqueId.setOpenmrsId(pgUniqueId.getOpenmrsId());
        uniqueId.setStatus(pgUniqueId.getStatus());
        uniqueId.setUsedBy(pgUniqueId.getUsedBy());
        return  uniqueId;
    }

    @Override
    protected Long retrievePrimaryKey(UniqueId uniqueId) {
        Object uniqueIdentifier = getUniqueField(uniqueId);
        if (uniqueIdentifier == null) {
            return null;
        }
        String identifier = uniqueIdentifier.toString();
        
        UniqueIdExample example = new UniqueIdExample();
        example.createCriteria().andOpenmrsIdEqualTo(identifier);
        List<org.opensrp.domain.postgres.UniqueId> pgEntities = uniqueIdMapper.selectByExample(example);

        return pgEntities.isEmpty() ? null : pgEntities.get(0).getId();
    }

    @Override
    protected Object getUniqueField(UniqueId uniqueId) {
        if (uniqueId == null) {
            return null;
        }
        return uniqueId.getOpenmrsId();
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
        return  pgUniqueId;
    }
}
