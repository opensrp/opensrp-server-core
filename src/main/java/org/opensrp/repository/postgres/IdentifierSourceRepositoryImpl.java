package org.opensrp.repository.postgres;

import org.apache.commons.lang.NotImplementedException;
import org.opensrp.domain.IdentifierSource;
import org.opensrp.domain.postgres.IdentifierSourceExample;
import org.opensrp.repository.IdentifierSourceRepository;
import org.opensrp.repository.postgres.mapper.custom.CustomIdentifierSourceMapper;
import org.opensrp.util.IdentifierValidatorAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository("identifierSourceRepositoryPostgres")
public class IdentifierSourceRepositoryImpl extends BaseRepositoryImpl<IdentifierSource>
		implements IdentifierSourceRepository {

	@Autowired
	private CustomIdentifierSourceMapper customIdentifierSourceMapper;

	@Override
	public IdentifierSource findByIdentifier(String identifier) {
		org.opensrp.domain.postgres.IdentifierSource identifierSource = customIdentifierSourceMapper
				.selectByIdentifier(identifier);
		return convert(identifierSource);
	}

	@Override
	public IdentifierSource get(String id) {
		throw new NotImplementedException();
	}

	@Override
	public void add(IdentifierSource entity) {
		if (entity == null || entity.getIdentifier() == null) {
			return;
		}

		if (retrievePrimaryKey(entity) != null) { // IdentifierSource already added
			return;
		}
		org.opensrp.domain.postgres.IdentifierSource pgIdentifierSource = convert(entity, null);
		if (pgIdentifierSource == null) {
			return;
		}

		int rowsAffected = customIdentifierSourceMapper.insertSelectiveAndSetId(pgIdentifierSource);
		if (rowsAffected < 1 || pgIdentifierSource.getId() == null) {
			return;
		}

	}

	@Override
	public void update(IdentifierSource entity) {
		if (entity == null) {
			return;
		}
		Long id = retrievePrimaryKey(entity);

		if (id == null) { //Identifier Source doesn't not exist
			return;
		}

		org.opensrp.domain.postgres.IdentifierSource pgIdentifierSource = convert(entity, id);
		customIdentifierSourceMapper.updateByPrimaryKey(pgIdentifierSource);
	}

	@Override
	public List<IdentifierSource> getAll() {
		IdentifierSourceExample identifierSourceExample = new IdentifierSourceExample();
		identifierSourceExample.createCriteria().andIdentifierIsNotNull();
		List<org.opensrp.domain.postgres.IdentifierSource> identifierSources = customIdentifierSourceMapper
				.selectMany(identifierSourceExample, 0, DEFAULT_FETCH_SIZE);
		return convert(identifierSources);
	}

	@Override
	public void safeRemove(IdentifierSource entity) {
		throw new NotImplementedException();
	}

	@Override
	protected Long retrievePrimaryKey(IdentifierSource entity) {

		IdentifierSourceExample identifierSourceExample = new IdentifierSourceExample();
		IdentifierSourceExample.Criteria criteria = identifierSourceExample.createCriteria();
		if (entity.getId() != null && entity.getId() != 0) {
			criteria.andIdEqualTo(entity.getId());
		} else {
			criteria.andIdentifierEqualTo(entity.getIdentifier());
		}

		org.opensrp.domain.postgres.IdentifierSource identifierSource = customIdentifierSourceMapper
				.selectOne(identifierSourceExample);
		if (identifierSource == null) {
			return null;
		}
		return identifierSource.getId();
	}

	@Override
	protected Object getUniqueField(IdentifierSource identifierSource) {
		if (identifierSource == null) {
			return null;
		}
		return identifierSource.getIdentifier();
	}

	// Private Methods
	protected List<IdentifierSource> convert(List<org.opensrp.domain.postgres.IdentifierSource> identifierSources) {
		if (identifierSources == null || identifierSources.isEmpty()) {
			return new ArrayList<>();
		}

		List<IdentifierSource> convertedIdentifierSources = new ArrayList<>();
		for (org.opensrp.domain.postgres.IdentifierSource identifierSource : identifierSources) {
			IdentifierSource convertedIdentifierSource = convert(identifierSource);
			if (convertedIdentifierSource != null) {
				convertedIdentifierSources.add(convertedIdentifierSource);
			}
		}

		return convertedIdentifierSources;
	}

	private IdentifierSource convert(org.opensrp.domain.postgres.IdentifierSource identifierSource) {
		if (identifierSource == null) {
			return null;
		}
		IdentifierSource convertedIdentifierSource = new IdentifierSource();
		convertedIdentifierSource.setId(identifierSource.getId());
		convertedIdentifierSource.setIdentifier(identifierSource.getIdentifier());
		convertedIdentifierSource.setDescription(identifierSource.getDescription());
		convertedIdentifierSource.setIdentifierValidatorAlgorithm(IdentifierValidatorAlgorithm.get(identifierSource.getIdentifierValidatorAlgorithm()));
		convertedIdentifierSource.setBaseCharacterSet(identifierSource.getBaseCharacterSet());
		convertedIdentifierSource.setFirstIdentifierBase(identifierSource.getFirstIdentifierBase());
		convertedIdentifierSource.setPrefix(identifierSource.getPrefix());
		convertedIdentifierSource.setSuffix(identifierSource.getSuffix());
		convertedIdentifierSource.setMinLength(identifierSource.getMinLength());
		convertedIdentifierSource.setMaxLength(identifierSource.getMaxLength());
		convertedIdentifierSource.setRegexFormat(identifierSource.getRegexFormat());

		return convertedIdentifierSource;
	}

	private org.opensrp.domain.postgres.IdentifierSource convert(IdentifierSource identifierSource, Long primaryKey) {
		if (identifierSource == null) {
			return null;
		}

		org.opensrp.domain.postgres.IdentifierSource pgIdentifierSource = new org.opensrp.domain.postgres.IdentifierSource();
		pgIdentifierSource.setId(primaryKey);
		pgIdentifierSource.setIdentifier(identifierSource.getIdentifier());
		pgIdentifierSource.setDescription(identifierSource.getDescription());
		pgIdentifierSource.setIdentifierValidatorAlgorithm(identifierSource.getIdentifierValidatorAlgorithm() != null ?
				identifierSource.getIdentifierValidatorAlgorithm().name() : null);
		pgIdentifierSource.setBaseCharacterSet(identifierSource.getBaseCharacterSet());
		pgIdentifierSource.setFirstIdentifierBase(identifierSource.getFirstIdentifierBase());
		pgIdentifierSource.setPrefix(identifierSource.getPrefix());
		pgIdentifierSource.setSuffix(identifierSource.getSuffix());
		pgIdentifierSource.setMinLength(identifierSource.getMinLength());
		pgIdentifierSource.setMaxLength(identifierSource.getMaxLength());
		pgIdentifierSource.setRegexFormat(identifierSource.getRegexFormat());

		return pgIdentifierSource;
	}

}
