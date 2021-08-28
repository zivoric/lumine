package conduit.chat;

public enum ChatColors {
	BLACK('0'), DARK_BLUE('1'), DARK_GREEN('2'), DARK_AQUA('3'), DARK_RED('4'), 
	DARK_PURPLE('5'), GOLD('6'), GRAY('7'), DARK_GRAY('8'), BLUE('9'), GREEN('a'),
	AQUA('b'), RED('c'), LIGHT_PURPLE('d'), YELLOW('e'), WHITE('f'), MAGIC('k'), BOLD('l'),
	STRIKETHROUGH('m'), UNDERLINE('n'), ITALIC('o'), RESET('r');
	
	private static final char COLOR_SYMBOL = '\u00A7';
	private static String validCodes = "0123456789AaBbCcDdEeFfKkLlMmNnOoRr";
	private final char colorCode;
	ChatColors(char code) {
		colorCode = code;
	}
	public char getColorCode() {
		return colorCode;
	}
	public static String convertColorCodes(String text) {
		return convertColorCodes(text, '&');
	}
	public static String convertColorCodes(String text, char replace) {
		char[] converted = text.toCharArray();
		for (int i = 0; i < converted.length-1; i++) {
			if (converted[i] == replace && validCodes.indexOf(converted[i+1]) != -1)
				converted[i] = COLOR_SYMBOL;
		}
		return new String(converted);
	}
	public static String removeColorCodes(String text) {
		String converted = new String(text);
		boolean iterating = true;
		while (converted.indexOf(COLOR_SYMBOL) != -1 && iterating) {
			int index = converted.indexOf(COLOR_SYMBOL);
			if (validCodes.indexOf(converted.charAt(index+1)) != -1)
				converted = converted.substring(0, index) + converted.substring(index+2);
		}
		return converted;
	}
	@Override
	public String toString() {
		return COLOR_SYMBOL + "" + colorCode;
	}
}
