package models.ui;

import javafx.util.StringConverter;
import managers.StringsManager;

public class StringConverterInteger extends StringConverter<Integer>{
	
	@Override
	public String toString(Integer object) {
		return Integer.toString(object);
	}

	
	@Override
	public Integer fromString(String string) {
		StringsManager stringsManager = new StringsManager();
		String newValue = stringsManager.getOnlyNumbers(string);
		if (newValue.equals("")) {
			newValue = "0";
		}
		return Integer.parseInt(newValue);
	}
	
}
