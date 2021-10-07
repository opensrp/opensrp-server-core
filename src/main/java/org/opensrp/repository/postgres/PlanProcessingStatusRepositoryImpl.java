package org.opensrp.repository.postgres;

import org.apache.commons.lang.StringUtils;
import org.opensrp.domain.postgres.Event;
import org.opensrp.domain.postgres.PlanProcessingStatus;
import org.opensrp.domain.postgres.PlanProcessingStatusExample;
import org.opensrp.repository.EventsRepository;
import org.opensrp.repository.PlanProcessingStatusRepository;
import org.opensrp.repository.postgres.mapper.custom.CustomPlanProcessingStatusMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PlanProcessingStatusRepositoryImpl extends  BaseRepositoryImpl<PlanProcessingStatus> implements PlanProcessingStatusRepository {

    @Autowired
    private CustomPlanProcessingStatusMapper planProcessingStatusMapper;

    @Autowired
    private EventsRepository allEvents;

    @Autowired
    private PlanRepositoryImpl planRepository;

    @Override
    public PlanProcessingStatus get(String id) {
        return null;
    }

    @Override
    public void add(PlanProcessingStatus planProcessingStatus) {
        if (planProcessingStatus == null) {
            return;
        }
        if (getUniqueField(planProcessingStatus) == null) {
            return;
        }

        if (retrievePrimaryKey(planProcessingStatus) != null) {
            return; // planProcessingStatus already added
        }

        planProcessingStatusMapper.insertSelective(planProcessingStatus);
    }

    @Override
    public void update(PlanProcessingStatus planProcessingStatus) {
        if (planProcessingStatus == null) {
            return;
        }
        if (getUniqueField(planProcessingStatus) == null) {
            return;
        }

        Long id = retrievePrimaryKey(planProcessingStatus);
        if ( id == null) {
            return; // planProcessingStatus does not exist
        }

        planProcessingStatusMapper.updateByPrimaryKey(planProcessingStatus);
    }

    @Override
    public List<PlanProcessingStatus> getAll() {
        PlanProcessingStatusExample planProcessingStatusExample = new PlanProcessingStatusExample();
        planProcessingStatusExample.createCriteria();
        List<PlanProcessingStatus> pgPlanProcessingStatuses = planProcessingStatusMapper.selectMany(planProcessingStatusExample, 0,
                DEFAULT_FETCH_SIZE);
        return pgPlanProcessingStatuses;
    }

    @Override
    public void safeRemove(PlanProcessingStatus planProcessingStatus) {
        if (planProcessingStatus == null) {
            return;
        }

        Long id = retrievePrimaryKey(planProcessingStatus);
        if (id == null) {
            return;
        }

        planProcessingStatusMapper.deleteByPrimaryKey(id);
    }

    @Override
    public PlanProcessingStatus getByPrimaryKey(Long id) {
        if (id == null) {
            return null;
        }
        PlanProcessingStatusExample example = new PlanProcessingStatusExample();
        example.createCriteria().andIdEqualTo(id);
        List<PlanProcessingStatus> templates = planProcessingStatusMapper.selectByExample(example);
        return templates.isEmpty() ? null : templates.get(0);
    }

    @Override
    public PlanProcessingStatus getByEventId(Long eventId) {
        if (eventId == null) {
            return null;
        }
        PlanProcessingStatusExample example = new PlanProcessingStatusExample();
        example.createCriteria().andEventIdEqualTo(eventId);
        List<PlanProcessingStatus> templates = planProcessingStatusMapper.selectByExample(example);
        return templates.isEmpty() ? null : templates.get(0);
    }

    @Override
    public List<PlanProcessingStatus>  getByStatus(Integer status) {
        if (status == null) {
            return null;
        }
        PlanProcessingStatusExample example = new PlanProcessingStatusExample();
        example.createCriteria().andStatusEqualTo(status);

        return planProcessingStatusMapper.selectByExample(example);
    }

    @Override
    public void updatePlanProcessingStatus(String eventIdentifier, String planIdentifier, int status) {

        PlanProcessingStatus processingStatus = new PlanProcessingStatus();
        processingStatus.setStatus(status);
        if (StringUtils.isNotBlank(eventIdentifier)) {
            Event pgEvent = allEvents.getDbEventByIdentifier(eventIdentifier);
            processingStatus.setEventId(pgEvent.getId());
        }
        if (StringUtils.isNotBlank(planIdentifier)){
            Long planId = planRepository.retrievePrimaryKey(planIdentifier);
            processingStatus.setPlanId(planId);
        }
        add(processingStatus);
    }

    @Override
    protected Long retrievePrimaryKey(PlanProcessingStatus planProcessingStatus) {
        Object uniqueId = getUniqueField(planProcessingStatus);
        if (uniqueId == null) {
            return null;
        }

        return (Long) uniqueId;
    }

    @Override
    protected Object getUniqueField(PlanProcessingStatus planProcessingStatus) {
        return planProcessingStatus == null ? null : planProcessingStatus.getId();
    }
}
