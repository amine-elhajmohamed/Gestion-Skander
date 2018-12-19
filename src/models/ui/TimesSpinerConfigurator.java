package models.ui;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;
import managers.StringsManager;

public class TimesSpinerConfigurator {
	private Spinner<Integer> spinerHoures;
	private Spinner<Integer> spinerMinutes;

	private StringsManager stringsManager = new StringsManager();

	private CustumStringConverterInteger custumStringConverterInteger;

	private IntegerSpinnerValueFactory integerSpinnerValueFactoryHoures;
	private IntegerSpinnerValueFactory integerSpinnerValueFactoryMinutes;

	public TimesSpinerConfigurator(Spinner<Integer> spinerHoures, Spinner<Integer> spinerMinutes) {
		this.spinerHoures = spinerHoures;
		this.spinerMinutes = spinerMinutes;
	}


	public void configure(){
		Date date = new Date();
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(date);

		custumStringConverterInteger = new CustumStringConverterInteger();

		integerSpinnerValueFactoryHoures = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 
				calendar.get(Calendar.HOUR_OF_DAY));
		integerSpinnerValueFactoryHoures.setConverter(custumStringConverterInteger);

		integerSpinnerValueFactoryMinutes = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59,
				calendar.get(Calendar.MINUTE));
		integerSpinnerValueFactoryMinutes.setConverter(custumStringConverterInteger);

		spinerHoures.setValueFactory(integerSpinnerValueFactoryHoures);
		spinerMinutes.setValueFactory(integerSpinnerValueFactoryMinutes);

		spinerHoures.getEditor().textProperty().addListener(new StringChangeListener(integerSpinnerValueFactoryHoures));
		spinerMinutes.getEditor().textProperty().addListener(new StringChangeListener(integerSpinnerValueFactoryMinutes));

		spinerHoures.getEditor().focusedProperty().addListener(new BooleanChangeListener(spinerHoures.getEditor()));
		spinerMinutes.getEditor().focusedProperty().addListener(new BooleanChangeListener(spinerMinutes.getEditor()));
	}


	private class BooleanChangeListener implements ChangeListener<Boolean> {

		private TextField txt;

		public BooleanChangeListener(TextField txt) {
			this.txt = txt;
		}

		@Override
		public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
			if (newValue) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						txt.selectAll();
					}
				});
			}
		}	
	}

	private class StringChangeListener implements ChangeListener<String> {

		private IntegerSpinnerValueFactory integerSpinnerValueFactory;

		public StringChangeListener(IntegerSpinnerValueFactory integerSpinnerValueFactory) {
			this.integerSpinnerValueFactory = integerSpinnerValueFactory;
		}

		@Override
		public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
			String newValueOnlyNumbers = stringsManager.getOnlyNumbers(newValue);

			while (newValueOnlyNumbers.startsWith("0")) {
				newValueOnlyNumbers = newValueOnlyNumbers.substring(1);
			}

			if (newValueOnlyNumbers.equals("")) {
				newValueOnlyNumbers = "0";
			}

			if (newValueOnlyNumbers.length() > 2) {
				newValueOnlyNumbers = newValueOnlyNumbers.substring(0, 2);
			}

			int newValueInt = Integer.parseInt(newValueOnlyNumbers);
			
			if (newValueInt > integerSpinnerValueFactory.getMax()) {
				newValueInt = newValueInt/10; // newValueInt contain only 2 numbers at max
			}
			
			integerSpinnerValueFactory.setValue(newValueInt);

			TextField txt = (TextField) ((StringProperty)observable).getBean();
			txt.setText(custumStringConverterInteger.toString(integerSpinnerValueFactory.getValue()));
		}

	}


	private class CustumStringConverterInteger extends StringConverterInteger{
		@Override
		public String toString(Integer object) {
			String ch = super.toString(object);
			if (ch.length() == 1) {
				ch = "0"+ch;
			}
			return ch;
		}
	}
}
