package org.opensrp.repository.postgres;

import org.apache.commons.lang3.StringUtils;
import org.opensrp.domain.PractitionerRoleDefinition;
import org.opensrp.domain.postgres.Practitioner;
import org.opensrp.domain.postgres.PractitionerRole;
import org.opensrp.domain.postgres.PractitionerRoleExample;
import org.opensrp.repository.PractitionerRoleRepository;
import org.opensrp.repository.postgres.mapper.custom.CustomPractitionerRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static org.opensrp.util.Utils.isEmptyList;

@Repository
public class PractitionerRoleRepositoryImpl extends BaseRepositoryImpl<PractitionerRoleDefinition> implements PractitionerRoleRepository {

    @Autowired
    private CustomPractitionerRoleMapper practitionerRoleMapper;

    @Autowired
    private PractitionerRepositoryImpl practitionerRepository;

    @Override
    public PractitionerRoleDefinition get(String id) {
        if (StringUtils.isBlank(id)) {
            return null;
        }

        PractitionerRoleDefinition practitionerRoleDefinition = convert(getPractitionerRole(id));
        return practitionerRoleDefinition;
    }

    @Override
    public PractitionerRole getPractitionerRole(String id) {
        if (StringUtils.isBlank(id)) {
            return null;
        }

        PractitionerRoleExample practitionerRoleExample = new PractitionerRoleExample();
        practitionerRoleExample.createCriteria().andIdentifierEqualTo(id);

        List<PractitionerRole> practitionerRoleList = practitionerRoleMapper.selectByExample(practitionerRoleExample);

        return isEmptyList(practitionerRoleList) ? null : practitionerRoleList.get(0);
    }

    @Override
    public void add(PractitionerRoleDefinition practitionerRoleDefinition) {
        if (practitionerRoleDefinition == null) {
            return;
        }
        if (getUniqueField(practitionerRoleDefinition) == null) {
            return;
        }

        if (retrievePrimaryKey(practitionerRoleDefinition) != null) {
            return; // practitionerRole already added
        }

        PractitionerRole pgPractitionerRole = convert(practitionerRoleDefinition);
        practitionerRoleMapper.insertSelective(pgPractitionerRole);
    }

    @Override
    public void update(PractitionerRoleDefinition practitionerRoleDefinition) {
        if (practitionerRoleDefinition == null) {
            return;
        }
        if (getUniqueField(practitionerRoleDefinition) == null) {
            return;
        }

        Long id = retrievePrimaryKey(practitionerRoleDefinition);
        if ( id == null) {
            return; // practitionerRole does not exist
        }

        PractitionerRole pgPractitionerRole = convert(practitionerRoleDefinition);
        pgPractitionerRole.setId(id);
        practitionerRoleMapper.updateByPrimaryKey(pgPractitionerRole);
    }

    @Override
    public List<PractitionerRoleDefinition> getAll() {
        PractitionerRoleExample practitionerRoleExample = new PractitionerRoleExample();
        List<PractitionerRole> pgPractitionerRoleList = practitionerRoleMapper.selectMany(practitionerRoleExample, 0,
                DEFAULT_FETCH_SIZE);
        return convert(pgPractitionerRoleList);
    }

    @Override
    public void safeRemove(PractitionerRoleDefinition practitionerRoleDefinition) {
        if (practitionerRoleDefinition == null) {
            return;
        }

        Long id = retrievePrimaryKey(practitionerRoleDefinition);
        if (id == null) {
            return;
        }

        PractitionerRole pgPractitionerRole = convert(practitionerRoleDefinition);
        pgPractitionerRole.setId(id);

        practitionerRoleMapper.deleteByPrimaryKey(pgPractitionerRole.getId());
    }

    @Override
    protected Long retrievePrimaryKey(PractitionerRoleDefinition practitionerRoleDefinition) {
        Object uniqueId = getUniqueField(practitionerRoleDefinition);
        if (uniqueId == null) {
            return null;
        }

        String identifier = uniqueId.toString();
        PractitionerRole pgPractitionerRole = getPractitionerRole(identifier);

        return  pgPractitionerRole == null ? null : pgPractitionerRole.getId();
    }

    @Override
    protected Object getUniqueField(PractitionerRoleDefinition practitionerRoleDefinition) {
        return practitionerRoleDefinition == null ? null : practitionerRoleDefinition.getIdentifier();
    }

    @Override
    public List<PractitionerRoleDefinition> getRolesForPractitioner(String practitionerIdentifier) {
        if (StringUtils.isBlank(practitionerIdentifier)) {
            return null;
        }

        Practitioner practitioner = practitionerRepository.getPractitioner(practitionerIdentifier);

        if (practitioner == null  || practitioner.getId() == null) {
            return null;
        }

        PractitionerRoleExample practitionerRoleExample = new PractitionerRoleExample();
        practitionerRoleExample.createCriteria().andPractitionerIdEqualTo(practitioner.getId());

        List<PractitionerRole> pgPractitionerRoles =  practitionerRoleMapper.selectMany(practitionerRoleExample, 0, DEFAULT_FETCH_SIZE);

        return  convert(pgPractitionerRoles);
    }

    private PractitionerRoleDefinition convert(PractitionerRole pgPractitionerRole) {
        if (pgPractitionerRole == null) {
            return null;
        }
        PractitionerRoleDefinition practitionerRoleDefinition = new PractitionerRoleDefinition();
        practitionerRoleDefinition.setIdentifier(pgPractitionerRole.getIdentifier());
        practitionerRoleDefinition.setActive(pgPractitionerRole.getActive());
        practitionerRoleDefinition.setOrganizationId(pgPractitionerRole.getOrganizationId());
        practitionerRoleDefinition.setPractitionerId(pgPractitionerRole.getPractitionerId());
        practitionerRoleDefinition.setCode(pgPractitionerRole.getCode());

        return practitionerRoleDefinition;
    }

    private PractitionerRole convert(PractitionerRoleDefinition practitionerRoleDefinition) {
        if (practitionerRoleDefinition == null) {
            return null;
        }
        PractitionerRole pgPractitionerRole = new PractitionerRole();
        pgPractitionerRole.setIdentifier(practitionerRoleDefinition.getIdentifier());
        pgPractitionerRole.setActive(practitionerRoleDefinition.getActive());
        pgPractitionerRole.setOrganizationId(practitionerRoleDefinition.getOrganizationId());
        pgPractitionerRole.setPractitionerId(practitionerRoleDefinition.getPractitionerId());
        pgPractitionerRole.setCode(practitionerRoleDefinition.getCode());

        return pgPractitionerRole;
    }

    private List<PractitionerRoleDefinition> convert(List<PractitionerRole> pgPractitionerRoles) {
        List<PractitionerRoleDefinition> practitionerRoles = new ArrayList<>();
        if (isEmptyList(pgPractitionerRoles)) {
            return practitionerRoles;
        }
        for(PractitionerRole pgPractitionerRole : pgPractitionerRoles) {
            practitionerRoles.add(convert(pgPractitionerRole));
        }
        return practitionerRoles;
    }
}
