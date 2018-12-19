package models.ui;

import javafx.util.StringConverter;
import managers.StringsManager;

public class StringConverterDouble extends StringConverter<Double>{

	@Override
	public String toString(Double object) {
		return Double.toString(object);
	}

	@Override
	public Double fromString(String string) {
		StringsManager stringsManager = new StringsManager();
		String newValue = stringsManager.getDoubleFormat(string);
		if (newValue.equals("")) {
			newValue = "0";
		}
		return Double.parseDouble(newValue);
	}

}
