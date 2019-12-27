package dev.itseternity.chatfilter.filter;

import dev.itseternity.chatfilter.ChatFilterPlugin;
import dev.itseternity.chatfilter.utils.Pair;
import org.bukkit.entity.Player;

public class SwearFilter implements Filter {

    private final ChatFilterPlugin plugin;

    public SwearFilter(ChatFilterPlugin plugin) {
        this.plugin = plugin;
    }

    // TODO Clean up this class, I don't like this

    @Override
    public FilterResult filter(Player player, String message) {
        // If we don't find anything wrong with the message, we don't want to
        // change the original casing
        String lowerCase = message.toLowerCase();
        for (String blacklisted : plugin.getChatManager().getBlacklistedWords()) {
            if (message.toLowerCase().contains(blacklisted)) {
                plugin.debug("Contains blacklisted word");

                // The message contains a blacklisted word, let's check the individual words
                // and check it is not whitelisted
                Pair<Boolean, String> result = checkEachWord(player, lowerCase);
                return new FilterResult(false, result.getLeft(), result.getRight());
            }
        }

        return new FilterResult(false, false, message);
    }

    private Pair<Boolean, String> checkEachWord(Player player, String message) {
        String copy = message;
        boolean caught = false;
        for (String word : copy.split(" ")) {
            if (isWhitelisted(word.toLowerCase())) {
                continue;
            }

            for (String blacklist : plugin.getChatManager().getBlacklistedWords()) {
                if (word.contains(blacklist)) {
                    word = word.toLowerCase();
                    copy = copy.replace(word, generateStars(word));
                    caught = true;
                }
            }
        }

        return new Pair<>(caught, copy);
    }

    private boolean isWhitelisted(String message) {
        boolean whitelisted = false;
        for (String whitelistedWord : plugin.getChatManager().getWhitelistedWords()) {
            if (message.toLowerCase().contains(whitelistedWord.toLowerCase())) {
                whitelisted = true;
                break;
            }
        }

        return whitelisted;
    }

    private static String generateStars(String word) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < word.length(); i++) {
            builder.append("*");
        }

        return builder.toString();
    }
}

