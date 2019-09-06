package org.opensrp.repository.postgres;

import org.apache.commons.lang3.StringUtils;
import org.opensrp.domain.PractitionerDefinition;
import org.opensrp.domain.postgres.Practitioner;
import org.opensrp.domain.postgres.PractitionerExample;
import org.opensrp.repository.PractitionerRepository;
import org.opensrp.repository.postgres.mapper.custom.CustomPractitionerMapper;
import org.opensrp.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.opensrp.util.Utils.isEmptyList;

@Repository
public class PractitionerRepositoryImpl extends BaseRepositoryImpl<PractitionerDefinition> implements PractitionerRepository {

    @Autowired
    private CustomPractitionerMapper practitionerMapper;

    @Override
    public PractitionerDefinition get(String id) {
        if (StringUtils.isBlank(id)) {
            return null;
        }

        PractitionerDefinition practitionerDefinition = convert(getPractitioner(id));
        return practitionerDefinition;
    }

    @Override
    public Practitioner getPractitioner(String id) {
        if (StringUtils.isBlank(id)) {
            return null;
        }

        PractitionerExample practitionerExample = new PractitionerExample();
        practitionerExample.createCriteria().andIdentifierEqualTo(id).andDateDeletedIsNull();

        List<Practitioner> practitionerList = practitionerMapper.selectByExample(practitionerExample);

        return Utils.isEmptyList(practitionerList) ? null : practitionerList.get(0);

    }

    @Override
    public void add(PractitionerDefinition practitionerDefinition) {
        if (practitionerDefinition == null) {
            return;
        }
        if (getUniqueField(practitionerDefinition) == null) {
            return;
        }

        if (retrievePrimaryKey(practitionerDefinition) != null) {
            return; // practitioner already added
        }

        Practitioner pgPractitioner = convert(practitionerDefinition);

        practitionerMapper.insertSelective(pgPractitioner);

    }

    @Override
    public void update(PractitionerDefinition practitionerDefinition) {
        if (practitionerDefinition == null) {
            return;
        }
        if (getUniqueField(practitionerDefinition) == null) {
            return;
        }

        Long id = retrievePrimaryKey(practitionerDefinition);
        if ( id == null) {
            return; // practitioner does not exist
        }

        Practitioner pgPractitioner = convert(practitionerDefinition);

        pgPractitioner.setId(id);
        practitionerMapper.updateByPrimaryKey(pgPractitioner);
    }

    @Override
    public List<PractitionerDefinition> getAll() {
        PractitionerExample practitionerExample = new PractitionerExample();
        practitionerExample.createCriteria().andDateDeletedIsNull();
        List<Practitioner> pgPractitionerList = practitionerMapper.selectMany(practitionerExample, 0,
                DEFAULT_FETCH_SIZE);
        return convert(pgPractitionerList);
    }

    @Override
    public void safeRemove(PractitionerDefinition practitionerDefinition) {
        if (practitionerDefinition == null) {
            return;
        }

        Long id = retrievePrimaryKey(practitionerDefinition);
        if (id == null) {
            return;
        }

        Practitioner pgPractitioner = convert(practitionerDefinition);
        pgPractitioner.setId(id);
        pgPractitioner.setDateDeleted(new Date());
        practitionerMapper.updateByPrimaryKey(pgPractitioner);
    }

    @Override
    protected Long retrievePrimaryKey(PractitionerDefinition practitionerDefinition) {
        Object uniqueId = getUniqueField(practitionerDefinition);
        if (uniqueId == null) {
            return null;
        }

        String identifier = uniqueId.toString();
        Practitioner pgPractitioner = getPractitioner(identifier);

        return pgPractitioner == null ? null : pgPractitioner.getId();
    }

    @Override
    protected Object getUniqueField(PractitionerDefinition practitionerDefinition) {
        return practitionerDefinition == null ? null : practitionerDefinition.getIdentifier();
    }

    private PractitionerDefinition convert(Practitioner pgPractitioner) {
        if (pgPractitioner == null) {
            return null;
        }
        PractitionerDefinition practitionerDefinition = new PractitionerDefinition();
        practitionerDefinition.setIdentifier(pgPractitioner.getIdentifier());
        practitionerDefinition.setActive(pgPractitioner.getActive());
        practitionerDefinition.setName(pgPractitioner.getName());
        practitionerDefinition.setUserId(pgPractitioner.getUserId());
        practitionerDefinition.setUserName(pgPractitioner.getUsername());
        practitionerDefinition.setDateDeleted(pgPractitioner.getDateDeleted());

        return practitionerDefinition;
    }

    private Practitioner convert(PractitionerDefinition practitionerDefinition) {
        if (practitionerDefinition == null) {
            return null;
        }
        Practitioner pgPractitioner = new Practitioner();
        pgPractitioner.setIdentifier(practitionerDefinition.getIdentifier());
        pgPractitioner.setActive(practitionerDefinition.getActive());
        pgPractitioner.setName(practitionerDefinition.getName());
        pgPractitioner.setUserId(practitionerDefinition.getUserId());
        pgPractitioner.setUsername(practitionerDefinition.getUserName());
        pgPractitioner.setDateDeleted(practitionerDefinition.getDateDeleted());

        return pgPractitioner;
    }

    private List<PractitionerDefinition> convert(List<Practitioner> pgPractitioners) {
        List<PractitionerDefinition> practitioners = new ArrayList<>();
        if (isEmptyList(pgPractitioners)) {
            return practitioners;
        }
        for(Practitioner pgPractitioner : pgPractitioners) {
            practitioners.add(convert(pgPractitioner));
        }
        return practitioners;
    }

}
