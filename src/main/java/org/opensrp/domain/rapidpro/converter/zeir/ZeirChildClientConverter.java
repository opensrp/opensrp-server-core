package org.opensrp.domain.rapidpro.converter.zeir;

import org.apache.commons.lang3.StringUtils;
import org.opensrp.domain.postgres.RapidproState;
import org.opensrp.domain.rapidpro.RapidProStateSyncStatus;
import org.opensrp.domain.rapidpro.contact.zeir.RapidProContact;
import org.opensrp.domain.rapidpro.contact.zeir.RapidProFields;
import org.opensrp.domain.rapidpro.converter.BaseRapidProClientConverter;
import org.opensrp.service.IdentifierSourceService;
import org.opensrp.service.UniqueIdentifierService;
import org.opensrp.service.rapidpro.ZeirRapidProStateService;
import org.opensrp.util.DateParserUtils;
import org.opensrp.util.constants.RapidProConstants;
import org.smartregister.domain.Address;
import org.smartregister.domain.Client;

import java.util.Locale;

import static org.opensrp.domain.rapidpro.ZeirRapidProEntity.CHILD;
import static org.opensrp.domain.rapidpro.ZeirRapidProEntityProperty.IDENTIFIER;

public class ZeirChildClientConverter extends BaseRapidProClientConverter {

	private final ZeirRapidProStateService zeirRapidProStateService;

	public ZeirChildClientConverter(ZeirRapidProStateService zeirRapidProStateService) {
		this.zeirRapidProStateService = zeirRapidProStateService;
	}

	public ZeirChildClientConverter(IdentifierSourceService identifierSourceService,
			UniqueIdentifierService uniqueIdentifierService, ZeirRapidProStateService zeirRapidProStateService) {
		super(identifierSourceService, uniqueIdentifierService);
		this.zeirRapidProStateService = zeirRapidProStateService;
	}

	@Override
	public Client convertContactToClient(RapidProContact rapidProContact) {
		RapidProFields fields = rapidProContact.getFields();
		if (RapidProConstants.CHILD.equalsIgnoreCase(fields.getPosition())) {
			Client childClient = new Client(rapidProContact.getUuid());
			addCommonZeirProperties(rapidProContact, childClient);

			addZeirClientIdentifier(rapidProContact, childClient, RapidProConstants.ZEIR_ID);

			mapMvaccIdToZeirId(fields, childClient);

			childClient.setBirthdate(DateParserUtils.parseZoneDateTime(fields.getDob()));
			childClient.addAttribute(RapidProConstants.CHILD_REGISTER_CARD_NUMBER, fields.getMvaccId());
			childClient.addAttribute(RapidProConstants.RESIDENTIAL_AREA, fields.getLocation());
			childClient.addAttribute(RapidProConstants.RESIDENTIAL_ADDRESS, fields.getLocation());

			//Place of birth facility saved as health_facility in ZEIR
			String birth = RapidProConstants.FACILITY.equalsIgnoreCase(fields.getBirth()) ?
					RapidProConstants.HEALTH_FACILITY : fields.getBirth();
			childClient.addAttribute(RapidProConstants.PLACE_OF_BIRTH, birth.toLowerCase(Locale.ROOT).replaceAll(" ", "_"));

			//Use Supervisor's location as the default place of birth and home facility both saved as location
			// id address property of client json
			Address address = new Address();
			address.setAddressType(RapidProConstants.USUAL_RESIDENCE);
			address.addAddressField(RapidProConstants.ADDRESS_SIX, fields.getFacilityLocationId());
			address.addAddressField(RapidProConstants.ADDRESS_SEVEN, fields.getFacilityLocationId());
			childClient.addAddress(address);

			childClient.setGender(fields.getSex().toLowerCase(Locale.ROOT));

			return childClient;
		}
		return null;
	}

	private void mapMvaccIdToZeirId(RapidProFields fields, Client childClient) {
		RapidproState rapidProState = new RapidproState();
		rapidProState.setUuid(childClient.getBaseEntityId());
		rapidProState.setEntity(CHILD.name());
		rapidProState.setProperty(IDENTIFIER.name());
		String facilityCode = fields.getFacilityCode();
		//Uniquely identify MVACC ID
		rapidProState.setPropertyKey(StringUtils.isNoneBlank(facilityCode) ? facilityCode + fields.getMvaccId() :
				fields.getFacilityLocationId() + fields.getMvaccId());

		rapidProState.setPropertyValue(childClient.getIdentifier(RapidProConstants.ZEIR_ID));
		rapidProState.setSyncStatus(RapidProStateSyncStatus.UN_SYNCED.name());
		zeirRapidProStateService.saveRapidProState(rapidProState);
	}

	@Override
	public RapidProContact convertClientToContact(Client client) {
		RapidProContact childContact = new RapidProContact();
		RapidProFields childFields = new RapidProFields();
		childContact.setFields(childFields);
		addCommonClientProperties(client, childContact);
		childFields.setPosition(RapidProConstants.CHILD);
		childFields.setMvaccId((String) client.getAttribute(RapidProConstants.CHILD_REGISTER_CARD_NUMBER));
		childFields.setOpensrpId(client.getIdentifier(RapidProConstants.ZEIR_ID));
		String birthPlace = (String) client.getAttribute(RapidProConstants.PLACE_OF_BIRTH);
		childFields.setBirth(RapidProConstants.HEALTH_FACILITY.equalsIgnoreCase(birthPlace)
				? RapidProConstants.FACILITY : RapidProConstants.HOME);
		childFields.setLocation((String) client.getAttribute(RapidProConstants.RESIDENTIAL_AREA));
		return childContact;
	}
}
