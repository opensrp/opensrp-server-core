package org.opensrp.repository.postgres;

import org.apache.commons.lang3.StringUtils;
import org.opensrp.domain.postgres.PractitionerRole;
import org.opensrp.domain.postgres.PractitionerRoleExample;
import org.opensrp.repository.PractitionerRoleRepository;
import org.opensrp.repository.postgres.mapper.custom.CustomPractitionerRoleMapper;
import org.opensrp.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PractitionerRoleRepositoryImpl extends BaseRepositoryImpl<PractitionerRole> implements PractitionerRoleRepository {

    @Autowired
    private CustomPractitionerRoleMapper practitionerRoleMapper;

    @Override
    public PractitionerRole get(String id) {
        if (StringUtils.isBlank(id)) {
            return null;
        }

        PractitionerRoleExample practitionerRoleExample = new PractitionerRoleExample();
        practitionerRoleExample.createCriteria().andIdentifierEqualTo(id);

        List<PractitionerRole> practitionerRoleList = practitionerRoleMapper.selectByExample(practitionerRoleExample);

        return Utils.isEmptyList(practitionerRoleList) ? null : practitionerRoleList.get(0);
    }

    @Override
    public void add(PractitionerRole practitionerRole) {
        if (practitionerRole == null) {
            return;
        }
        if (getUniqueField(practitionerRole) == null) {
            return;
        }

        if (retrievePrimaryKey(practitionerRole) != null) {
            return; // practitionerRole already added
        }

        practitionerRoleMapper.insertSelective(practitionerRole);
    }

    @Override
    public void update(PractitionerRole practitionerRole) {
        if (practitionerRole == null) {
            return;
        }
        if (getUniqueField(practitionerRole) == null) {
            return;
        }

        if (retrievePrimaryKey(practitionerRole) == null) {
            return; // practitionerRole already added
        }

        practitionerRoleMapper.updateByPrimaryKey(practitionerRole);
    }

    @Override
    public List<PractitionerRole> getAll() {
        PractitionerRoleExample practitionerRoleExample = new PractitionerRoleExample();
        List<PractitionerRole> practitionerRoleList = practitionerRoleMapper.selectMany(practitionerRoleExample, 0,
                DEFAULT_FETCH_SIZE);
        return practitionerRoleList;
    }

    @Override
    public void safeRemove(PractitionerRole practitionerRole) {
        if (practitionerRole == null) {
            return;
        }

        String id = retrievePrimaryKey(practitionerRole);
        if (id == null) {
            return;
        }

        practitionerRoleMapper.deleteByPrimaryKey(practitionerRole.getId());
    }

    @Override
    protected String retrievePrimaryKey(PractitionerRole practitionerRole) {
        Object uniqueId = getUniqueField(practitionerRole);
        if (uniqueId == null) {
            return null;
        }

        String identifier = uniqueId.toString();
        PractitionerRole pgPractitionerRole = get(identifier);

        return  pgPractitionerRole == null ? null : practitionerRole.getIdentifier();
    }

    @Override
    protected Object getUniqueField(PractitionerRole practitionerRole) {
        return practitionerRole == null ? null : practitionerRole;
    }

    @Override
    public List<PractitionerRole> getRolesForPractitioner(Long practitionerId) {
        if (practitionerId == null) {
            return null;
        }

        PractitionerRoleExample practitionerRoleExample = new PractitionerRoleExample();
        practitionerRoleExample.createCriteria().andPractitionerIdEqualTo(practitionerId);

        return  practitionerRoleMapper.selectMany(practitionerRoleExample, 0, DEFAULT_FETCH_SIZE);
    }
}
