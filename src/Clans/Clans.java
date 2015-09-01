package Clans;

import PluginReference.MC_EventInfo;
import PluginReference.MC_Player;
import PluginReference.MC_Server;
import PluginReference.PluginBase;
import gyurix.konfigfajl.ConfigFile;
import gyurix.konfigfajl.KFA;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Clans extends PluginBase
{
  public Clans()
  {
    Utils.load();
    KFA.srv.registerCommand(new Command());
  }

  public void onShutdown() {
    Utils.saveclans();
  }
  public static Clan getClan(MC_Player plr) {
    String pln = plr.getName();
    for (Clan c : Utils.clans.values()) {
      if (c.pls.contains(pln)) {
        return c;
      }
    }
    return null;
  }

  public void onPlayerJoin(MC_Player plr) {
    Clan plc = getClan(plr);
    if ((Utils.kf.getBoolean("chat.clan.forceon")) && (plc != null)) {
      Utils.chats.put(plr.getName(), plc);
    }
    if (Utils.kf.getBoolean("scoreboard"))
      for (Clan c : Utils.clans.values())
        c.sendPacket(plr, 0);
  }

  public void onPlayerLogout(String pln, UUID uuid)
  {
    if (!Utils.kf.getBoolean("chat.remember"))
      Utils.chats.remove(pln);
    Utils.chats.remove(pln);
    Utils.invitations.remove(pln);
  }

  public void onPlayerInput(MC_Player plr, String msg, MC_EventInfo ei) {
    if (!msg.startsWith("/")) {
      Clan c = (Clan)Utils.chats.get(plr.getName());
      if (c != null) {
        ei.isCancelled = true;
        Utils.clanMsg(c, plr, msg);
      }
      else if (Utils.kf.getBoolean("chat.editregular")) {
        c = getClan(plr);
        if (c != null) {
          ei.isCancelled = true;
          for (MC_Player p : KFA.srv.getPlayers())
            Utils.msgNoPrefix(p, "chat.normalformat", new String[] { 
              "<prefix>", c.prefix, "<name>", c.displayname, "<suffix>", c.suffix, 
              "<rank>", c.getRank(plr).name, "<player>", plr.getName(), 
              "<message>", msg.replaceAll("(?i)&([a-f0-9k-or])", "§$1") });
        }
      }
    }
  }
}

/* Location:           C:\Users\Gyuri\Downloads\Clans.jar
 * Qualified Name:     Clans.Clans
 * JD-Core Version:    0.6.2
 */