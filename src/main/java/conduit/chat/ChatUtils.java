package conduit.chat;

public class ChatUtils {
	public static final String CHAT_PREFIX = getChatPrefix();
	public static String highlightWord(String word) {
		return highlightWord(ChatColors.AQUA, ChatColors.GREEN, word);
	}
	public static String highlightWord(ChatColors surrounding, ChatColors highlight, String word) {
		return highlight + word + surrounding;
	}
	public static String getChatPrefix() {
		return getChatPrefix("Conduit");
	}
	public static String getChatPrefix(String prefix) {
		return getChatPrefix(ChatColors.DARK_AQUA, ChatColors.AQUA, prefix);
	}
	public static String getChatPrefix(ChatColors before, ChatColors after, String prefix) {
		return before + prefix + " \u00BB " + after;
	}
}
