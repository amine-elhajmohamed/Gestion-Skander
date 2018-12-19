package managers;

public class StringsManager {

	public String getOnlyLetters(String text){
		String ch = "";
		
		for (int i = 0;i<text.length();i++){
			if (Character.isLetter(text.charAt(i)) || text.charAt(i) == ' '){
				ch += text.charAt(i);
			}
		}
		
		return ch;
	}
	
	public String getOnlyLettersAndNumbers(String text){
		String ch = "";
		
		for (int i = 0;i<text.length();i++){
			if (Character.isLetterOrDigit(text.charAt(i)) || text.charAt(i) == ' '){
				ch += text.charAt(i);
			}
		}
		
		return ch;
	}
	
	public String getOnlyNumbers(String text){
		String ch = "";
		
		for (int i = 0;i<text.length();i++){
			if (Character.isDigit(text.charAt(i))){
				ch += text.charAt(i);
			}
		}
		
		return ch;
	}
	
	
	public String removeUnwantedSpaces(String text){
		String ch = text.replaceAll("  ", " ");
		
		if (ch.length() > 0 && ch.charAt(0) == ' ') {
			ch = ch.substring(1);
		}
		
		return ch;
	}
	
	
	public String getDoubleFormat(String text){
		String ch = "";
		
		while (text.length() > 1 && text.startsWith("00")) {
			text = text.substring(1);
		}
		
		if (text.length() > 0 && text.charAt(0) == '.') {
			text = "0"+text;
		}
		
		for (int i = 0;i<text.length();i++){
			if (Character.isDigit(text.charAt(i))){
				ch += text.charAt(i);
			}else if (text.charAt(i) == '.' && !ch.contains(".")){
				ch += ".";
			}
		}
		
		if ( ch.startsWith("0") && ( (ch.indexOf(".") > 1) || (ch.indexOf(".") == -1 && ch.length()>1) ) ) {
			ch = ch.substring(1);
		}
		
		return ch;
	}
	
}
