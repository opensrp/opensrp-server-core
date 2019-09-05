package org.opensrp.repository.postgres;

import org.apache.commons.lang3.StringUtils;
import org.opensrp.domain.postgres.Practitioner;
import org.opensrp.domain.postgres.PractitionerExample;
import org.opensrp.repository.PractitionerRepository;
import org.opensrp.repository.postgres.mapper.custom.CustomPractitionerMapper;
import org.opensrp.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class PractitionerRepositoryImpl extends BaseRepositoryImpl<Practitioner> implements PractitionerRepository {

    @Autowired
    private CustomPractitionerMapper practitionerMapper;

    @Override
    public Practitioner get(String id) {
        if (StringUtils.isBlank(id)) {
            return null;
        }

        PractitionerExample practitionerExample = new PractitionerExample();
        practitionerExample.createCriteria().andIdentifierEqualTo(id).andDateDeletedIsNull();

        List<Practitioner> practitionerList = practitionerMapper.selectByExample(practitionerExample);

        return Utils.isEmptyList(practitionerList) ? null : practitionerList.get(0);

    }

    @Override
    public void add(Practitioner practitioner) {
        if (practitioner == null) {
            return;
        }
        if (getUniqueField(practitioner) == null) {
            return;
        }

        if (retrievePrimaryKey(practitioner) != null) {
            return; // practitioner already added
        }

        practitionerMapper.insertSelective(practitioner);

    }

    @Override
    public void update(Practitioner practitioner) {
        if (practitioner == null) {
            return;
        }
        if (getUniqueField(practitioner) == null) {
            return;
        }

        if (retrievePrimaryKey(practitioner) == null) {
            return; // practitioner already added
        }

        practitionerMapper.updateByPrimaryKey(practitioner);
    }

    @Override
    public List<Practitioner> getAll() {
        PractitionerExample practitionerExample = new PractitionerExample();
        practitionerExample.createCriteria().andDateDeletedIsNull();
        List<Practitioner> practitionerList = practitionerMapper.selectMany(practitionerExample, 0,
                DEFAULT_FETCH_SIZE);
        return practitionerList;
    }

    @Override
    public void safeRemove(Practitioner practioner) {
        if (practioner == null) {
            return;
        }

        String id = retrievePrimaryKey(practioner);
        if (id == null) {
            return;
        }

        practioner.setDateDeleted(new Date());
        practitionerMapper.updateByPrimaryKey(practioner);
    }

    @Override
    protected String retrievePrimaryKey(Practitioner practitioner) {
        Object uniqueId = getUniqueField(practitioner);
        if (uniqueId == null) {
            return null;
        }

        String identifier = uniqueId.toString();
        Practitioner pgPractitioner = get(identifier);

        return pgPractitioner == null ? null : pgPractitioner.getIdentifier();
    }

    @Override
    protected Object getUniqueField(Practitioner practitioner) {
        return practitioner == null ? null : practitioner;
    }

}
