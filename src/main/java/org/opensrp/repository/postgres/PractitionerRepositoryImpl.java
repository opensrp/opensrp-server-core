package org.opensrp.repository.postgres;

import org.apache.commons.lang3.StringUtils;
import org.opensrp.domain.Practitioner;
import org.opensrp.domain.postgres.PractitionerExample;
import org.opensrp.repository.PractitionerRepository;
import org.opensrp.repository.postgres.mapper.custom.CustomPractitionerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.opensrp.util.Utils.isEmptyList;

@Repository
public class PractitionerRepositoryImpl extends BaseRepositoryImpl<Practitioner> implements PractitionerRepository {

    @Autowired
    private CustomPractitionerMapper practitionerMapper;

    @Override
    public Practitioner get(String id) {
        if (StringUtils.isBlank(id)) {
            return null;
        }

        Practitioner practitioner = convert(getPractitioner(id));
        return practitioner;
    }

    @Override
    public org.opensrp.domain.postgres.Practitioner getPractitioner(String id) {
        if (StringUtils.isBlank(id)) {
            return null;
        }

        PractitionerExample practitionerExample = new PractitionerExample();
        practitionerExample.createCriteria().andIdentifierEqualTo(id).andDateDeletedIsNull();

        List<org.opensrp.domain.postgres.Practitioner> practitionerList = practitionerMapper.selectByExample(practitionerExample);

        return isEmptyList(practitionerList) ? null : practitionerList.get(0);

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

        org.opensrp.domain.postgres.Practitioner pgPractitioner = convert(practitioner);

        practitionerMapper.insertSelective(pgPractitioner);

    }

    @Override
    public void update(Practitioner practitioner) {
        if (practitioner == null) {
            return;
        }
        if (getUniqueField(practitioner) == null) {
            return;
        }

        Long id = retrievePrimaryKey(practitioner);
        if ( id == null) {
            return; // practitioner does not exist
        }

        org.opensrp.domain.postgres.Practitioner pgPractitioner = convert(practitioner);

        pgPractitioner.setId(id);
        practitionerMapper.updateByPrimaryKey(pgPractitioner);
    }

    @Override
    public List<Practitioner> getAll() {
        PractitionerExample practitionerExample = new PractitionerExample();
        practitionerExample.createCriteria().andDateDeletedIsNull();
        List<org.opensrp.domain.postgres.Practitioner> pgPractitionerList = practitionerMapper.selectMany(practitionerExample, 0,
                DEFAULT_FETCH_SIZE);
        return convert(pgPractitionerList);
    }

    @Override
    public void safeRemove(Practitioner practitioner) {
        if (practitioner == null) {
            return;
        }

        Long id = retrievePrimaryKey(practitioner);
        if (id == null) {
            return;
        }

        org.opensrp.domain.postgres.Practitioner pgPractitioner = convert(practitioner);
        pgPractitioner.setId(id);
        pgPractitioner.setDateDeleted(new Date());
        practitionerMapper.updateByPrimaryKey(pgPractitioner);
    }

    @Override
    protected Long retrievePrimaryKey(Practitioner practitioner) {
        Object uniqueId = getUniqueField(practitioner);
        if (uniqueId == null) {
            return null;
        }

        String identifier = uniqueId.toString();
        org.opensrp.domain.postgres.Practitioner pgPractitioner = getPractitioner(identifier);

        return pgPractitioner == null ? null : pgPractitioner.getId();
    }

    @Override
    protected Object getUniqueField(Practitioner practitioner) {
        return practitioner == null ? null : practitioner.getIdentifier();
    }

    private Practitioner convert(org.opensrp.domain.postgres.Practitioner pgPractitioner) {
        if (pgPractitioner == null) {
            return null;
        }
        Practitioner practitioner = new Practitioner();
        practitioner.setIdentifier(pgPractitioner.getIdentifier());
        practitioner.setActive(pgPractitioner.getActive());
        practitioner.setName(pgPractitioner.getName());
        practitioner.setUserId(pgPractitioner.getUserId());
        practitioner.setUserName(pgPractitioner.getUsername());
        practitioner.setDateDeleted(pgPractitioner.getDateDeleted());

        return practitioner;
    }

    private org.opensrp.domain.postgres.Practitioner convert(Practitioner practitioner) {
        if (practitioner == null) {
            return null;
        }
        org.opensrp.domain.postgres.Practitioner pgPractitioner = new org.opensrp.domain.postgres.Practitioner();
        pgPractitioner.setIdentifier(practitioner.getIdentifier());
        pgPractitioner.setActive(practitioner.getActive());
        pgPractitioner.setName(practitioner.getName());
        pgPractitioner.setUserId(practitioner.getUserId());
        pgPractitioner.setUsername(practitioner.getUserName());
        pgPractitioner.setDateDeleted(practitioner.getDateDeleted());

        return pgPractitioner;
    }

    private List<Practitioner> convert(List<org.opensrp.domain.postgres.Practitioner> pgPractitioners) {
        List<Practitioner> practitioners = new ArrayList<>();
        if (isEmptyList(pgPractitioners)) {
            return practitioners;
        }
        for(org.opensrp.domain.postgres.Practitioner pgPractitioner : pgPractitioners) {
            practitioners.add(convert(pgPractitioner));
        }
        return practitioners;
    }

}
