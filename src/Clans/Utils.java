package Clans;

import PluginReference.MC_Player;
import PluginReference.MC_Server;
import com.google.common.collect.Lists;
import gyurix.chatapi.ChatAPI;
import gyurix.konfigfajl.ConfigFile;
import gyurix.konfigfajl.KFA;
import gyurix.konfigfajl.LangFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;

public class Utils
{
  public static ConfigFile kf;
  public static ConfigFile cf;
  public static String dir;
  public static final HashMap<String, Clan> clans = new HashMap();
  public static final HashMap<String, Clan> chats = new HashMap();
  public static final HashMap<String, Clan> invitations = new HashMap();
  public static int version;

  public static void load()
  {
    dir = KFA.fileCopy(MyPlugin.pl, "config.yml", false);
    kf = new ConfigFile(dir + "/config.yml");
    LangFile lf = new LangFile(dir + "/lang.lng");
    version = (int)kf.getLong("version", 1L);
    boolean forcelangcopy = kf.getBoolean("forcelangcopy", true);
    if (version == 1) {
      kf.set("version", "2");
      lf.insert(new LangFile(KFA.getFileStream(MyPlugin.pl, "lang.lng")));
      lf.save();
    }
    if (forcelangcopy) {
      kf.set("forcelangcopy", "-");
      KFA.lf.remove("clans");
      KFA.lf.insert(lf);
    }
    if ((version != 2) || (forcelangcopy)) {
      kf.save();
    }
    loadclans();
  }
  public static void loadclans() {
    clans.clear();
    chats.clear();
    invitations.clear();
    cf = new ConfigFile(dir + "/clans.yml");
    boolean sb = kf.getBoolean("scoreboard");
    for (String cn : cf.mainAdressList("clans")) {
      Clan c = new Clan(cn);
      String cn2 = "clans." + cn + ".";
      c.spawnloc = cf.getLocation(cn2 + "spawnloc");
      c.maxpls = ((int)cf.getLong(cn2 + "maxplayers", 16L));
      c.prefix = cf.get(cn2 + "prefix");
      c.suffix = cf.get(cn2 + "suffix");
      c.displayname = cf.get(cn2 + "displayname", cn);
      c.pls = Lists.newArrayList(cf.get(cn2 + "players").split("\\ "));
      c.ranks = new ArrayList();
      for (String rn : cf.mainAdressList(cn2 + "ranks")) {
        Rank r = new Rank(rn);
        r.players = Lists.newArrayList(cf.get(cn2 + "ranks." + rn + ".players", "").split("\\ "));
        r.setPerms(cf.get(cn2 + "ranks." + rn + ".perms", ""));
        c.ranks.add(r);
      }
      if (sb)
        c.sendPacket(null, 0);
      clans.put(cn, c);
    }
    for (String cn : cf.mainAdressList("chats"))
      chats.put(cn, (Clan)clans.get(kf.get("chats." + cn)));
  }

  public static void saveclans() {
    cf.clear();
    Iterator localIterator2;
    for (Iterator localIterator1 = clans.entrySet().iterator(); localIterator1.hasNext(); 
      localIterator2.hasNext())
    {
      Map.Entry e = (Map.Entry)localIterator1.next();
      String cn = "clans." + (String)e.getKey() + ".";
      Clan c = (Clan)e.getValue();
      cf.set(cn + "maxplayers", Integer.valueOf(c.maxpls));
      cf.set(cn + "prefix", c.prefix);
      cf.set(cn + "suffix", c.suffix);
      cf.set(cn + "displayname", c.displayname);
      cf.set(cn + "players", StringUtils.join(c.pls, " "));
      cf.set(cn + "allys", StringUtils.join(c.allys, " "));
      cf.set(cn + "pendingallys", StringUtils.join(c.pendingallys, " "));
      cf.set(cn + "allyrequests", StringUtils.join(c.allyrequests, " "));
      if (c.spawnloc != null)
        cf.setlocation(cn + "spawnloc", c.spawnloc);
      localIterator2 = c.ranks.iterator(); continue; Rank r = (Rank)localIterator2.next();
      cf.set(cn + "ranks." + r.name + ".players", StringUtils.join(r.players, " "));
      cf.set(cn + "ranks." + r.name + ".perms", r.getPermissions().replace(", ", " "));
    }

    for (Map.Entry e : chats.entrySet()) {
      cf.set("chats." + (String)e.getKey(), ((Clan)e.getValue()).name);
    }
    cf.save();
  }
  public static void msg(MC_Player plr, String adr, String[] args) {
    ChatAPI.msg(KFA.l(plr, "clans.messageprefix"), "", plr, "clans." + adr, args);
  }
  public static void msgNoPrefix(MC_Player plr, String adr, String[] args) {
    String s = KFA.l(plr, "clans." + adr);
    for (int i = 0; i < args.length - 1; i += 2) {
      s = s.replace(args[i], args[(i + 1)]);
    }
    plr.sendMessage(s);
  }
  public static void clanMsg(Clan c, MC_Player senderplr, String msg) {
    String rank = c.getRank(senderplr).name;
    String sender = senderplr.getName();
    for (String rec : c.pls) {
      msgNoPrefix(KFA.srv.getOnlinePlayerByName(rec), "chat.clanformat", new String[] { 
        "<rank>", rank, "<player>", sender, "<message>", msg.replace("&", "§") });
    }
    for (MC_Player plr : KFA.srv.getPlayers())
      if ((!c.pls.contains(plr.getName())) && (plr.hasPermission("clans.chat.receiveall")))
        msgNoPrefix(plr, "chat.adminformat", new String[] { 
          "<name>", c.displayname, "<rank>", rank, "<player>", sender, "<message>", msg.replace("&", "§") });
  }

  public static CommandType getCommand(MC_Player plr, String name)
  {
    for (CommandType t : CommandType.values()) {
      if ((KFA.l(plr, new StringBuilder("clans.command.").append(t.name().toLowerCase()).toString()) + " ").contains(name + " "))
        return t;
    }
    return null;
  }
  public static Clan clanTest(MC_Player plr, String cn, Rank.PermType perm) {
    Clan c = Clans.getClan(plr);
    if (c == null) {
      msg(plr, "noclan", new String[0]);
      return null;
    }
    if (!c.hasPerm(plr, perm)) {
      msg(plr, "noperm." + cn, new String[0]);
      return null;
    }
    return c;
  }
  public static void adminMsg(MC_Player plr, Clan c, String adr, String[] args) {
    String pln = plr.getName();
    for (Rank r : c.ranks)
      if (r.hasPerm(Rank.PermType.AdminMessages))
        for (String pn : r.players)
          if (!pn.equals(pln)) {
            MC_Player pl = KFA.srv.getOnlinePlayerByName(pn);
            if (pl != null)
              msg(pl, adr, args);
          }
  }

  public static enum CommandType
  {
    New, Remove, Invite, Accept, Deny, Kick, Leave, Rank, EditRanks, 
    Info, List, Prefix, Suffix, Ally, Enemy, Spawn, SetSpawn, Chat, MaxPlayers, Save, Reload, DisplayName;
  }
}

/* Location:           C:\Users\Gyuri\Downloads\Clans.jar
 * Qualified Name:     Clans.Utils
 * JD-Core Version:    0.6.2
 */