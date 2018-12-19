package models.ui;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javafx.util.StringConverter;

public class StringConverterTimestamp extends StringConverter<Timestamp>{

	
	private SimpleDateFormat dateFormater = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	
	@Override
	public String toString(Timestamp object) {
		return dateFormater.format(object);
	}

	@Override
	public Timestamp fromString(String string) {
		try {
			return new Timestamp(dateFormater.parse(string).getTime());
		} catch (ParseException e) {
			return null;
		}
	}

}
