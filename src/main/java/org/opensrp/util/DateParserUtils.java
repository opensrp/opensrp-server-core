package org.opensrp.util;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.time.ZonedDateTime;
import java.util.TimeZone;

public final class DateParserUtils {

	public static DateTime parseZoneDateTime(String dateString) {
		ZonedDateTime zonedDateTime = ZonedDateTime.parse(dateString);
		return new DateTime(zonedDateTime.toInstant().toEpochMilli(),
				DateTimeZone.forTimeZone(TimeZone.getTimeZone(zonedDateTime.getZone())));
	}
}
