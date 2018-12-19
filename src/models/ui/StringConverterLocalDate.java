package models.ui;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javafx.util.StringConverter;

public class StringConverterLocalDate extends StringConverter<LocalDate> {

	private DateTimeFormatter dateTimeFormatter=DateTimeFormatter.ofPattern("dd/MM/yyyy");

	@Override
	public String toString(LocalDate localDate)
	{
		if (localDate != null) {
			return dateTimeFormatter.format(localDate);	
		}else{
			return null;
		}
	}

	@Override
	public LocalDate fromString(String dateString)
	{
		return LocalDate.parse(dateString,dateTimeFormatter);
	}

}
