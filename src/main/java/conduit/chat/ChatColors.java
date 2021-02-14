package conduit.chat;

public enum ChatColors {
	BLACK('0'), DARK_BLUE('1'), DARK_GREEN('2'), DARK_AQUA('3'), DARK_RED('4'), 
	DARK_PURPLE('5'), GOLD('6'), GRAY('7'), DARK_GRAY('8'), BLUE('9'), GREEN('a'),
	AQUA('b'), RED('c'), LIGHT_PURPLE('d'), YELLOW('e'), WHITE('f');
	
	private final char colorCode;
	ChatColors(char code) {
		colorCode = code;
	}
	public char getColorCode() {
		return colorCode;
	}
	@Override
	public String toString() {
		return "\u00A7" + colorCode;
	}
}
