package org.opensrp.repository.postgres;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.smartregister.domain.Practitioner;
import org.opensrp.domain.postgres.PractitionerExample;
import org.opensrp.repository.PractitionerRepository;
import org.opensrp.repository.postgres.mapper.custom.CustomPractitionerMapper;
import org.opensrp.search.PractitionerSearchBean;
import org.apache.commons.lang3.tuple.Pair;
import org.opensrp.util.RepositoryUtil;
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
    public Practitioner getByPrimaryKey(Long id) {
        if (id == null) {
            return null;
        }
        PractitionerExample example = new PractitionerExample();
        example.createCriteria().andIdEqualTo(id).andDateDeletedIsNull();
        List<org.opensrp.domain.postgres.Practitioner> practitioners = practitionerMapper.selectByExample(example);
        return practitioners.isEmpty() ? null : convert(practitioners.get(0));
    }

    @Override
    public List<Practitioner> getPractitionersByOrgId(Long orgId) {
        if (orgId == null) {
            return new ArrayList<>();
        }
        PractitionerExample practitionerExample = new PractitionerExample();
        practitionerExample.createCriteria().andDateDeletedIsNull();
        List<org.opensrp.domain.postgres.Practitioner> pgPractitionerList = practitionerMapper.selectManyByOrgId(practitionerExample,
                orgId,0, DEFAULT_FETCH_SIZE);
        return convert(pgPractitionerList);
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

        if (getPractitionerByUserId(practitioner.getUserId()) != null) {
            return; // practitioner with this user id already added
        }

        org.opensrp.domain.postgres.Practitioner pgPractitioner = convert(practitioner);

        practitionerMapper.insertSelectiveAndGenerateServerVersion(pgPractitioner);

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
        practitionerMapper.updateByPrimaryKeyAndGenerateServerVersion(pgPractitioner);
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
    public void safeRemove(String identifier) {

        org.opensrp.domain.postgres.Practitioner pgPractitioner = getPractitioner(identifier);

        if (pgPractitioner == null) {
            return;
        }

        pgPractitioner.setDateDeleted(new Date());
        practitionerMapper.updateByPrimaryKey(pgPractitioner);
    }
    
    @Override
    public Practitioner getPractitionerByUserId(String userId) {
        if (StringUtils.isBlank(userId)) {
            return null;
        }

        PractitionerExample practitionerExample = new PractitionerExample();
        practitionerExample.createCriteria().andUserIdEqualTo(userId).andDateDeletedIsNull();

        List<org.opensrp.domain.postgres.Practitioner> practitionerList = practitionerMapper.selectByExample(practitionerExample);

        return  isEmptyList(practitionerList) ? null : convert(practitionerList.get(0));

    }
    
    
    @Override
   /**
    * {@inheritDoc}
    */
    public Practitioner getPractitionerByUsername(String username) {
        if (StringUtils.isBlank(username)) {
            return null;
        }

        PractitionerExample practitionerExample = new PractitionerExample();
        practitionerExample.createCriteria().andUsernameEqualTo(username).andDateDeletedIsNull();

        List<org.opensrp.domain.postgres.Practitioner> practitionerList = practitionerMapper.selectByExample(practitionerExample);

        return  isEmptyList(practitionerList) ? null : convert(practitionerList.get(0));

    }

    @Override
    public List<Practitioner> getAllPractitioners(PractitionerSearchBean practitionerSearchBean) {
        Pair<Integer, Integer> pageSizeAndOffset = RepositoryUtil.getPageSizeAndOffset(practitionerSearchBean);
        PractitionerExample practitionerExample = new PractitionerExample();
        practitionerExample.createCriteria().andDateDeletedIsNull();
        if(practitionerSearchBean.getOrderByFieldName() != null && practitionerSearchBean.getOrderByType() != null) {
            practitionerExample.setOrderByClause(practitionerSearchBean.getOrderByFieldName() + " " + practitionerSearchBean.getOrderByType());
        }
        List<org.opensrp.domain.postgres.Practitioner> pgPractitionerList = practitionerMapper.selectMany(practitionerExample, pageSizeAndOffset.getRight(), pageSizeAndOffset.getLeft());
        return convert(pgPractitionerList);
    }

	@Override
	public List<Practitioner> getAllPractitionersByIdentifiers(List<String> practitionerIdentifiers) {
    	PractitionerSearchBean practitionerSearchBean = new PractitionerSearchBean();
		Pair<Integer, Integer> pageSizeAndOffset = RepositoryUtil.getPageSizeAndOffset(practitionerSearchBean);
		PractitionerExample practitionerExample = new PractitionerExample();
		practitionerExample.createCriteria().andDateDeletedIsNull().andIdentifierIn(practitionerIdentifiers);
		List<org.opensrp.domain.postgres.Practitioner> pgPractitionerList = practitionerMapper.selectMany(practitionerExample, pageSizeAndOffset.getRight(), pageSizeAndOffset.getLeft());
		return convert(pgPractitionerList);
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
        practitioner.setUsername(pgPractitioner.getUsername());
        if (pgPractitioner.getDateCreated() != null)
            practitioner.setDateCreated(new DateTime(pgPractitioner.getDateCreated()));
        if (pgPractitioner.getDateEdited() != null)
            practitioner.setDateEdited(new DateTime(pgPractitioner.getDateEdited()));
        if (pgPractitioner.getServerVersion() != null)
            practitioner.setServerVersion(pgPractitioner.getServerVersion());

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
        pgPractitioner.setUsername(practitioner.getUsername());
        if (practitioner.getDateCreated() != null)
            pgPractitioner.setDateCreated(practitioner.getDateCreated().toDate());
        if (practitioner.getDateEdited() != null)
            pgPractitioner.setDateEdited(practitioner.getDateEdited().toDate());
        pgPractitioner.setServerVersion(practitioner.getServerVersion());

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

    @Override
    public long countAllPractitioners() {
      return practitionerMapper.countByExample(new PractitionerExample());
    }

    @Override
    public Practitioner getPractitionerByIdentifier(String practitionerIdentifier) {
        org.opensrp.domain.postgres.Practitioner practitioner = getPractitioner(practitionerIdentifier);
        if (practitioner != null) {
            return convert(practitioner);
        } else {
            return null;
        }
    }
}
