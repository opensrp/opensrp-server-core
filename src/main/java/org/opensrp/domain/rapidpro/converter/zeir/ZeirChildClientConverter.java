package org.opensrp.domain.rapidpro.converter.zeir;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.opensrp.domain.rapidpro.contact.zeir.RapidProContact;
import org.opensrp.domain.rapidpro.contact.zeir.RapidProFields;
import org.opensrp.domain.rapidpro.converter.BaseRapidProClientConverter;
import org.opensrp.util.constants.RapidProConstants;
import org.smartregister.domain.Address;
import org.smartregister.domain.Client;

import java.time.Instant;
import java.util.Locale;

public class ZeirChildClientConverter extends BaseRapidProClientConverter {

	@Override
	public Client convertContactToClient(RapidProContact rapidProContact) {

		RapidProFields fields = rapidProContact.getFields();
		if (RapidProConstants.CHILD.equalsIgnoreCase(fields.getPosition())) {
			Client client = new Client(rapidProContact.getUuid());
			setCommonZeirClientAttributes(rapidProContact, client);

			String zeirId = getIdentifier(rapidProContact);
			if (StringUtils.isNoneBlank(zeirId)) {
				client.getIdentifiers().put(RapidProConstants.ZEIR_ID, zeirId.replaceAll("-", ""));
			}

			client.setBirthdate(new DateTime(Instant.parse(fields.getDob()).toEpochMilli()));
			client.addAttribute(RapidProConstants.CHILD_REGISTER_CARD_NUMBER, fields.getMvaccId());
			client.addAttribute(RapidProConstants.RESIDENTIAL_AREA, fields.getLocation());

			//Place of birth facility saved as health_facility in ZEIR
			String birth = RapidProConstants.FACILITY.equalsIgnoreCase(fields.getBirth()) ?
					RapidProConstants.HEALTH_FACILITY :
					fields.getBirth();
			client.addAttribute(RapidProConstants.PLACE_OF_BIRTH, birth.toLowerCase(Locale.ROOT).replaceAll(" ", "_"));

			//Use Supervisor's location as the default place of birth which is saved as location id address in client json object
			Address address = new Address();
			address.setAddressType(RapidProConstants.USUAL_RESIDENCE);
			address.addAddressField(RapidProConstants.ADDRESS_SIX, fields.getFacilityLocationId());
			client.addAddress(address);

			client.setGender(fields.getSex().toLowerCase(Locale.ROOT));

			return client;
		}
		return null;
	}

	@Override
	public RapidProContact convertClientToContact(Client client) {
		return null;
	}
}
