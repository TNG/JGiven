package com.tngtech.jgiven.format;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * General formatter to format date values.
 * 
 * @author dgrandemange
 *
 */
public class DateFormatter implements ArgumentFormatter<Date> {

	public static final String DEFAULT_FORMAT = "dd/MM/yyyy HH:mm:ss";

	/**
	 * When an argument is set, a valid date format is expected<br>
	 * When no argument is provided, date is formatted with the
	 * {@link DEFAULT_FORMAT}
	 */
	@Override
	public String format(Date date, String... args) {
		if (date == null) {
			return null;
		}

		String format;

		if (args.length > 0) {
			format = args[0];
		} else {
			format = DEFAULT_FORMAT;
		}

		SimpleDateFormat sdf;
		try {
			sdf = new SimpleDateFormat(format);
		} catch (IllegalArgumentException e) {
			sdf = new SimpleDateFormat(DEFAULT_FORMAT);
		}

		return sdf.format(date);
	}

}
