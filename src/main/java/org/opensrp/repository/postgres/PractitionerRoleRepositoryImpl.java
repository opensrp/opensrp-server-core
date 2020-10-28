package org.opensrp.repository.postgres;

import org.apache.commons.lang3.StringUtils;
import org.opensrp.domain.Organization;
import org.opensrp.domain.PractitionerRole;
import org.opensrp.domain.PractitionerRoleCode;
import org.opensrp.domain.postgres.Practitioner;
import org.opensrp.domain.postgres.PractitionerRoleExample;
import org.opensrp.repository.OrganizationRepository;
import org.opensrp.repository.PractitionerRoleRepository;
import org.opensrp.repository.postgres.mapper.custom.CustomPractitionerRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static org.opensrp.util.Utils.isEmptyList;

@Repository
public class PractitionerRoleRepositoryImpl extends BaseRepositoryImpl<PractitionerRole> implements PractitionerRoleRepository {

    @Autowired
    private CustomPractitionerRoleMapper practitionerRoleMapper;

    @Autowired
    private PractitionerRepositoryImpl practitionerRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Override
    public PractitionerRole get(String id) {
        if (StringUtils.isBlank(id)) {
            return null;
        }

        PractitionerRole practitionerRole = convert(getPractitionerRole(id));
        return practitionerRole;
    }

    @Override
    public org.opensrp.domain.postgres.PractitionerRole getPractitionerRole(String id) {
        if (StringUtils.isBlank(id)) {
            return null;
        }

        PractitionerRoleExample practitionerRoleExample = new PractitionerRoleExample();
        practitionerRoleExample.createCriteria().andIdentifierEqualTo(id);

        List<org.opensrp.domain.postgres.PractitionerRole> practitionerRoleList = practitionerRoleMapper.selectByExample(practitionerRoleExample);

        return isEmptyList(practitionerRoleList) ? null : practitionerRoleList.get(0);
    }

    @Override
    public List<org.opensrp.domain.postgres.PractitionerRole> getPractitionerRole(Long organizationId, Long practitionerId) {
        if (organizationId == null || practitionerId == null) {
            return null;
        }

        PractitionerRoleExample practitionerRoleExample = new PractitionerRoleExample();
        practitionerRoleExample.createCriteria().andOrganizationIdEqualTo(organizationId).andPractitionerIdEqualTo(practitionerId);

        List<org.opensrp.domain.postgres.PractitionerRole> practitionerRoleList = practitionerRoleMapper.selectByExample(practitionerRoleExample);

        return isEmptyList(practitionerRoleList) ? null : practitionerRoleList;
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

        org.opensrp.domain.postgres.PractitionerRole pgPractitionerRole = convert(practitionerRole);
        practitionerRoleMapper.insertSelective(pgPractitionerRole);
    }

    @Override
    public void update(PractitionerRole practitionerRole) {
        if (practitionerRole == null) {
            return;
        }
        if (getUniqueField(practitionerRole) == null) {
            return;
        }

        Long id = retrievePrimaryKey(practitionerRole);
        if ( id == null) {
            return; // practitionerRole does not exist
        }

        org.opensrp.domain.postgres.PractitionerRole pgPractitionerRole = convert(practitionerRole);
        pgPractitionerRole.setId(id);
        practitionerRoleMapper.updateByPrimaryKey(pgPractitionerRole);
    }

    @Override
    public List<PractitionerRole> getAll() {
        PractitionerRoleExample practitionerRoleExample = new PractitionerRoleExample();
        List<org.opensrp.domain.postgres.PractitionerRole> pgPractitionerRoleList = practitionerRoleMapper.selectMany(practitionerRoleExample, 0,
                DEFAULT_FETCH_SIZE);
        return convert(pgPractitionerRoleList);
    }

    @Override
    public void safeRemove(PractitionerRole practitionerRole) {
        if (practitionerRole == null) {
            return;
        }

        Long id = retrievePrimaryKey(practitionerRole);
        if (id == null) {
            return;
        }


        org.opensrp.domain.postgres.PractitionerRole pgPractitionerRole = convert(practitionerRole);
        pgPractitionerRole.setId(id);

        practitionerRoleMapper.deleteByPrimaryKey(pgPractitionerRole.getId());
    }

    @Override
    public void safeRemove(String identifier) {

        org.opensrp.domain.postgres.PractitionerRole pgPractitionerRole = getPractitionerRole(identifier);

        if (pgPractitionerRole == null) {
            return;
        }

        practitionerRoleMapper.deleteByPrimaryKey(pgPractitionerRole.getId());

    }

    @Override
    public void safeRemove(Long organizationId, Long practitionerId) {

        List<org.opensrp.domain.postgres.PractitionerRole> pgPractitionerRoles = getPractitionerRole(organizationId, practitionerId);

        if (pgPractitionerRoles == null || pgPractitionerRoles.isEmpty()) {
            return;
        }

        for (org.opensrp.domain.postgres.PractitionerRole pgPractitionerRole: pgPractitionerRoles ) {
            practitionerRoleMapper.deleteByPrimaryKey(pgPractitionerRole.getId());
        }

    }

    @Override
    protected Long retrievePrimaryKey(PractitionerRole practitionerRole) {
        Object uniqueId = getUniqueField(practitionerRole);
        if (uniqueId == null) {
            return null;
        }

        String identifier = uniqueId.toString();
        org.opensrp.domain.postgres.PractitionerRole pgPractitionerRole = getPractitionerRole(identifier);

        return  pgPractitionerRole == null ? null : pgPractitionerRole.getId();
    }

    @Override
    protected Object getUniqueField(PractitionerRole practitionerRole) {
        return practitionerRole == null ? null : practitionerRole.getIdentifier();
    }

    @Override
    public List<PractitionerRole> getRolesForPractitioner(String practitionerIdentifier) {

        return  convert(getPgRolesForPractitioner(practitionerIdentifier));
    }

    @Override
    public List<org.opensrp.domain.postgres.PractitionerRole> getPgRolesForPractitioner(String practitionerIdentifier) {
        if (StringUtils.isBlank(practitionerIdentifier)) {
            return null;
        }

        Practitioner practitioner = practitionerRepository.getPractitioner(practitionerIdentifier);

        if (practitioner == null  || practitioner.getId() == null) {
            return null;
        }

        PractitionerRoleExample practitionerRoleExample = new PractitionerRoleExample();
        practitionerRoleExample.createCriteria().andPractitionerIdEqualTo(practitioner.getId());

        List<org.opensrp.domain.postgres.PractitionerRole> pgPractitionerRoles =  practitionerRoleMapper.selectMany(practitionerRoleExample, 0, DEFAULT_FETCH_SIZE);
        return pgPractitionerRoles;
    }

    private PractitionerRole convert(org.opensrp.domain.postgres.PractitionerRole pgPractitionerRole) {
        if (pgPractitionerRole == null) {
            return null;
        }
        org.opensrp.domain.Practitioner pgPractitioner = practitionerRepository.getByPrimaryKey(pgPractitionerRole.getPractitionerId());
        if (pgPractitioner == null) {
            return null; // practitioner already deleted
        }

        Organization pgOrganinization = organizationRepository.getByPrimaryKey(pgPractitionerRole.getOrganizationId());
        if (pgOrganinization == null) {
            return null; // organization already deleted
        }
        PractitionerRole practitionerRole = new PractitionerRole();
        practitionerRole.setIdentifier(pgPractitionerRole.getIdentifier());
        practitionerRole.setActive(pgPractitionerRole.getActive());
        practitionerRole.setOrganizationIdentifier(pgOrganinization.getIdentifier());
        practitionerRole.setPractitionerIdentifier(pgPractitioner.getIdentifier());
        PractitionerRoleCode code =  new PractitionerRoleCode();
        code.setText(pgPractitionerRole.getCode());
        practitionerRole.setCode(code);

        return practitionerRole;
    }

    private org.opensrp.domain.postgres.PractitionerRole convert(PractitionerRole practitionerRole) {
        if (practitionerRole == null) {
            return null;
        }

        org.opensrp.domain.postgres.PractitionerRole pgPractitionerRole = new org.opensrp.domain.postgres.PractitionerRole();
        pgPractitionerRole.setIdentifier(practitionerRole.getIdentifier());
        pgPractitionerRole.setActive(practitionerRole.getActive());
        Long  organizationId = getOrganizationId(practitionerRole.getOrganizationIdentifier());
        pgPractitionerRole.setOrganizationId(organizationId);
        Long practitionerId = getPractitionerId(practitionerRole.getPractitionerIdentifier());
        pgPractitionerRole.setPractitionerId(practitionerId);
        if(practitionerRole.getCode()!=null)
        pgPractitionerRole.setCode(practitionerRole.getCode().getText());

        return pgPractitionerRole;
    }

    private List<PractitionerRole> convert(List<org.opensrp.domain.postgres.PractitionerRole> pgPractitionerRoles) {
        List<PractitionerRole> practitionerRoles = new ArrayList<>();
        if (isEmptyList(pgPractitionerRoles)) {
            return practitionerRoles;
        }
        for(org.opensrp.domain.postgres.PractitionerRole pgPractitionerRole : pgPractitionerRoles) {
            practitionerRoles.add(convert(pgPractitionerRole));
        }
        return practitionerRoles;
    }

    private Long getPractitionerId (String practitionerIdentifier) {
        Practitioner practitioner = practitionerRepository.getPractitioner(practitionerIdentifier);
        Long practitionerId = practitioner != null ? practitioner.getId() : null;
        return practitionerId;
    }

    private Long getOrganizationId (String organizationIdentifier) {
        Organization organization = organizationRepository.get(organizationIdentifier);
        Long practitionerId = organization != null ? organization.getId() : null;
        return practitionerId;
    }

    @Override
    public void assignPractitionerRole(Long organizationId, Long practitionerId, String practitionerIdentifier, String code,
            PractitionerRole practitionerRole) {
        List<org.opensrp.domain.postgres.PractitionerRole> practitionerRoles = getPgRolesForPractitioner(
                practitionerIdentifier);
        for (org.opensrp.domain.postgres.PractitionerRole pgPractitionerRole : practitionerRoles) {
            if (isExistingPractitionerRole(organizationId, practitionerId, code, pgPractitionerRole)) {
                pgPractitionerRole.setActive(Boolean.TRUE);
                pgPractitionerRole.setCode(code);
                PractitionerRoleExample example = new PractitionerRoleExample();
                example.createCriteria().andIdEqualTo(pgPractitionerRole.getId());
                practitionerRoleMapper.updateByExample(pgPractitionerRole, example);
                return;
            }
        }
        add(practitionerRole);

    }

    private boolean isExistingPractitionerRole(Long organizationId, Long practitionerId, String code,
            org.opensrp.domain.postgres.PractitionerRole practitionerRole) {
        if (organizationId != null && practitionerId != null) {
            return practitionerRole.getPractitionerId().equals(practitionerId)
                    && practitionerRole.getOrganizationId().equals(organizationId)
                    && (practitionerRole.getCode() == null || practitionerRole.getCode().equals(code));
        }
        return false;
    }
}
