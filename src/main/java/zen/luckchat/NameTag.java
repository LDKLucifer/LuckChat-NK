package zen.luckchat;

import cn.nukkit.Player;
import cn.nukkit.utils.TextFormat;
import me.lucko.luckperms.api.User;
import me.lucko.luckperms.api.caching.MetaData;

import static zen.luckchat.LuckChatPlugin.*;

public class NameTag extends Thread {

    private LuckChatPlugin plugin;

    NameTag(LuckChatPlugin plugin) {
        this.plugin = plugin;
        setName("NameTag");
    }

    @Override
    public void run() {
        for (Player p : plugin.getServer().getOnlinePlayers().values()) {
            String name = p.getDisplayName();
            User user = LuckChatPlugin.luckPerms.getUser(p.getUniqueId());
            if (user == null) {
                plugin.getLogger().warning("An error occurred when attempting to retrieve " + p.getName() + "'s user data!");
                return;
            }

            MetaData metaData = user.getCachedData().getMetaData(luckPerms.getContextManager().getApplicableContexts(p));
            String prefix = metaData.getPrefix() != null ? metaData.getPrefix() : "";
            String suffix = metaData.getSuffix() != null ? metaData.getSuffix() : "";
            String perm = user.getPrimaryGroup();

            String tag = (LuckChatPlugin.config.getString("NameTag."+perm)
                    .replace("%name%", p.getName())
                    .replace("%disname%", name)
                    .replace("%prefix%", prefix)
                    .replace("%suffix%", suffix)
                    .replace("%group%", perm)
                    .replace("%device%", getOS(p))
                    .replace("%faction%", getFaction(p))
                    .replace("%money%", getMoney(p)));

            if (LuckChatPlugin.placeholderApi != null) {
                tag = LuckChatPlugin.placeholderApi.translateString(tag, p);
            }
            p.setNameTag(TextFormat.colorize('&', tag));
        }
    }
}
