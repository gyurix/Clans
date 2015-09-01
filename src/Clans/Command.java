package Clans;

import PluginReference.MC_Command;
import PluginReference.MC_Location;
import PluginReference.MC_Player;
import PluginReference.MC_Server;
import PluginReference.MC_World;
import com.google.common.collect.Lists;
import gyurix.konfigfajl.ConfigFile;
import gyurix.konfigfajl.KFA;
import gyurix.konfigfajl.LangFile;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public class Command
  implements MC_Command
{
  public List<String> getAliases()
  {
    return Lists.newArrayList(KFA.lf.get("clans.command.default").split("\\ "));
  }

  public String getCommandName()
  {
    return KFA.lf.get("clans.command.default").split("\\ ")[0];
  }

  public String getHelpLine(MC_Player plr)
  {
    return KFA.l(plr, "clans.default");
  }

  public List<String> getTabCompletionList(MC_Player plr, String[] args)
  {
    List out = new ArrayList();
    args[0] = args[0].toLowerCase();
    String cn;
    String[] cmds;
    String s;
    if (args.length == 1) {
      for (Utils.CommandType ct : Utils.CommandType.values()) {
        cn = ct.name().toLowerCase();
        if (plr.hasPermission("clans.command." + cn)) {
          cmds = KFA.l(plr, "clans.command." + cn).split("\\ ");
          for (s : cmds) {
            if (s.startsWith(args[0]))
              out.add(s);
          }
        }
      }
    }
    else
    {
      Utils.CommandType ct = Utils.getCommand(plr, args[0]);
      if ((ct == null) || (!plr.hasPermission("clans.command." + ct.name().toLowerCase())))
        return null;
      Clan c = Clans.getClan(plr);
      if (args.length == 2) {
        if (((ct == Utils.CommandType.Info) || (ct == Utils.CommandType.Remove)) && (plr.hasPermission("clans." + ct.name().toLowerCase() + "anyclan"))) {
          out.addAll(Utils.clans.keySet());
          out = KFA.tabFilter(args[1], out);
          Collections.sort(out);
        }
        else if (c == null) {
          return null;
        }args[1] = args[1].toLowerCase();
        Object pl;
        if (ct == Utils.CommandType.Invite) {
          List pls = KFA.srv.getPlayers();
          for (cn = pls.iterator(); cn.hasNext(); ) { pl = (MC_Player)cn.next();
            if ((((MC_Player)pl).getName().toLowerCase().startsWith(args[1])) && (Clans.getClan((MC_Player)pl) == null)) {
              out.add(((MC_Player)pl).getName());
            }
          }
        }
        else if (ct == Utils.CommandType.Ally) {
          out.addAll(Utils.clans.keySet());
          out.remove(c.name);
          out = KFA.tabFilter(args[1], out);
        }
        else if (ct == Utils.CommandType.EditRanks) {
          for (pl = c.ranks.iterator(); ((Iterator)pl).hasNext(); ) { Rank r = (Rank)((Iterator)pl).next();
            if ((r.name.toLowerCase().startsWith(args[1])) && 
              (!r.name.equals("owner")))
              out.add(r.name);
          }
        }
        else if ((ct == Utils.CommandType.Kick) || (ct == Utils.CommandType.Rank)) {
          out.addAll(KFA.tabFilter(args[1], c.pls));
          Rank r = c.containsRank("owner");
          if ((Utils.kf.getBoolean("owners.mustbeoneowner")) && (r.players.size() == 1))
            out.remove(r.players.get(0));
        }
      }
      else {
        if (c == null)
          return null;
        Object perms;
        if (ct == Utils.CommandType.EditRanks) {
          int id = args.length - 1;
          for (int i = 2; i < args.length; i++)
            args[i] = args[i].toLowerCase();
          perms = (String[])Arrays.copyOfRange(args, 2, args.length);
          Rank.PermType[] arrayOfPermType;
          s = (arrayOfPermType = Rank.PermType.values()).length; for (cmds = 0; cmds < s; cmds++) { Rank.PermType t = arrayOfPermType[cmds];
            String tn = t.name().toLowerCase();
            if ((tn.startsWith(args[id])) && (KFA.search((Object[])perms, tn) == -1))
              out.add(t.name());
          }
        }
        else if ((args.length == 3) && (ct == Utils.CommandType.Rank)) {
          args[2] = args[2].toLowerCase();
          for (perms = c.ranks.iterator(); ((Iterator)perms).hasNext(); ) { Rank r = (Rank)((Iterator)perms).next();
            if (r.name.toLowerCase().startsWith(args[2])) {
              out.add(r.name);
            }
          }
        }
      }
    }
    Collections.sort(out);
    return out;
  }

  public void handleCommand(MC_Player plr, String[] args)
  {
    if (!plr.hasPermission("clans.use")) {
      Utils.msg(plr, "noperm", new String[0]);
      return;
    }
    if (args.length == 0) {
      Utils.msg(plr, "default", new String[0]);
      return;
    }
    Utils.CommandType ct = Utils.getCommand(plr, args[0]);
    if (ct == null) {
      Utils.msg(plr, "default", new String[0]);
      return;
    }
    String cn = ct.name().toLowerCase();
    if (!plr.hasPermission("clans.command." + cn)) {
      Utils.msg(plr, "noperm." + cn, new String[0]);
      return;
    }
    String pln = plr.getName();
    if (ct == Utils.CommandType.New) {
      if (Clans.getClan(plr) != null) {
        Utils.msg(plr, "inclan", new String[0]);
        return;
      }
      if (args.length == 1) {
        Utils.msg(plr, "new.noname", new String[0]);
        return;
      }
      if (Utils.clans.containsKey(args[1])) {
        Utils.msg(plr, "exists", new String[] { "<name>", args[1] });
        return;
      }
      new Clan(plr, args[1]);
      Utils.msg(plr, "new", new String[] { "<name>", args[1] });
    }
    else if (ct == Utils.CommandType.Remove) {
      if ((args.length > 1) && (plr.hasPermission("clans.removeanyclan"))) {
        Clan c = (Clan)Utils.clans.get(args[1]);
        if (c == null) {
          Utils.msg(plr, "notexists", new String[] { "<name>", args[1] });
        }
        else {
          c.remove();
          Utils.msg(plr, "remove", new String[] { "<name>", c.name });
        }
      }
      else {
        Clan c = Utils.clanTest(plr, cn, Rank.PermType.Remove);
        if (c != null) {
          c.remove();
          Utils.msg(plr, "remove", new String[] { "<name>", c.name });
        }
      }
    }
    else if ((ct == Utils.CommandType.Prefix) || (ct == Utils.CommandType.Suffix)) {
      Clan c = Utils.clanTest(plr, cn, args.length == 0 ? null : Rank.PermType.valueOf(ct.name()));
      if (c == null)
        return;
      if (args.length == 1) {
        Utils.msg(plr, cn, new String[] { "<" + cn + ">", (ct == Utils.CommandType.Prefix ? c.prefix : c.suffix).replace("§", "&") });
      }
      else {
        String msg = (args[1].length() < 17 ? args[1] : args[1].substring(0, 16)).replaceAll("(?i)&([a-f0-9k-or])", "§$1");
        if (ct == Utils.CommandType.Prefix)
          c.prefix = msg;
        else
          c.suffix = msg;
        c.sendPacket(null, 2);
        Utils.msg(plr, cn + ".set", new String[] { "<" + cn + ">", ct == Utils.CommandType.Prefix ? c.prefix : c.suffix });
      }
    }
    else if (ct == Utils.CommandType.Chat) {
      Clan c = Utils.clanTest(plr, cn, null);
      if (c == null)
        return;
      boolean status = Utils.chats.remove(plr.getName()) == null;
      if (status) {
        Utils.chats.put(plr.getName(), c);
      }
      Utils.msg(plr, "chat." + (status ? "en" : "dis") + "abled", new String[0]);
    }
    else if (ct == Utils.CommandType.Leave) {
      Clan c = Utils.clanTest(plr, cn, null);
      if (c == null)
        return;
      Rank r = c.getRank(plr);
      if ((Utils.kf.getBoolean("owners.mustbeoneowner")) && (r.name.equals("owner")) && (r.players.size() == 1)) {
        Utils.msg(plr, "leave.lastowner", new String[0]);
      }
      else {
        c.removePlayer(plr.getName());
        Utils.msg(plr, "leave", new String[0]);
        Utils.adminMsg(plr, c, "left", new String[] { "<name>", plr.getName() });
      }
    }
    else if (ct == Utils.CommandType.Kick) {
      Clan c = Utils.clanTest(plr, cn, Rank.PermType.Kick);
      if (c == null)
        return;
      if (args.length == 1) {
        Utils.msg(plr, "noplrname", new String[0]);
        return;
      }
      if (c.pls.contains(args[1])) {
        Utils.msg(plr, "noclan.other", new String[] { "<player>", args[1] });
        return;
      }
      Rank r = c.getPlayerRank(args[1]);
      if ((Utils.kf.getBoolean("owners.mustbeoneowner")) && (r.name.equals("owner")) && (r.players.size() == 1)) {
        Utils.msg(plr, "kick.lastowner", new String[] { "<name>", args[1] });
      }
      else {
        c.removePlayer(args[1]);
        MC_Player kicked = KFA.srv.getOnlinePlayerByName(args[1]);
        String reason = args.length == 2 ? KFA.lf.get("chat.kick.defaultreason") : 
          StringUtils.join(new Serializable[] { args, Integer.valueOf(2), Integer.valueOf(args.length), " " }).replace("&", "§");
        Utils.msg(plr, "kick", new String[] { "<name>", args[1], "<reason>", reason });
        Utils.adminMsg(plr, c, "kick.player", new String[] { "<player>", pln, "<name>", args[1], "<reason>", reason });
        if (kicked != null)
          Utils.msg(kicked, "kicked", new String[] { "<reason>", reason });
      }
    }
    else
    {
      Rank r;
      if (ct == Utils.CommandType.EditRanks) {
        Clan c = Utils.clanTest(plr, cn, Rank.PermType.EditRanks);
        if (c == null)
          return;
        if (args.length == 1) {
          String f = KFA.l(plr, "clans.editranks.ranklistformat");
          String ranks = "";
          for (Iterator localIterator1 = c.ranks.iterator(); localIterator1.hasNext(); ) { r = (Rank)localIterator1.next();
            ranks = ranks + f.replace("<rankname>", r.name).replace("<permissions>", r.getPermissions());
          }
          Utils.msg(plr, "editranks", new String[] { "<ranks>", ranks });
        }
        else if (args.length == 2) {
          if ((args[1].equals("owner")) || (args[1].equals("member"))) {
            Utils.msg(plr, "editranks.cantremove", new String[] { "<rank>", args[1] });
          }
          else if (c.removeRank(args[1])) {
            Utils.msg(plr, "editranks.removed", new String[] { "<rank>", args[1] });
          }
          else {
            c.ranks.add(new Rank(args[1]));
            Utils.msg(plr, "editranks.created", new String[] { "<rank>", args[1] });
          }

        }
        else if ((args[1].equals("owner")) || (args[1].equals("member"))) {
          Utils.msg(plr, "editranks.cantedit", new String[] { "<rank>", args[1] });
        }
        else {
          Rank r = c.containsRank(args[1]);
          if (r == null) {
            c.ranks.add(new Rank(plr, (String[])ArrayUtils.subarray(args, 1, args.length)));
            Utils.msg(plr, "editranks.created", new String[] { "<rank>", args[1] });
          }
          else {
            r.switchPerms(plr, (String[])ArrayUtils.subarray(args, 2, args.length));
          }
        }

      }
      else if (ct == Utils.CommandType.DisplayName) {
        Clan c = Utils.clanTest(plr, cn, args.length == 1 ? null : Rank.PermType.Name);
        if (c == null)
          return;
        if (args.length == 1) {
          Utils.msg(plr, "displayname", new String[] { "<name>", c.displayname });
        }
        else {
          c.displayname = args[1].replaceAll("(?i)&([a-f0-9k-or])", "§$1");
          Utils.msg(plr, "displayname.set", new String[] { "<name>", c.displayname });
          Utils.adminMsg(plr, c, "displayname.set.player", new String[] { "<name>", c.displayname, "<player>", plr.getName() });
        }
      }
      else if (ct == Utils.CommandType.Rank) {
        Clan c = Utils.clanTest(plr, cn, args.length == 1 ? null : Rank.PermType.GiveRank);
        if (c == null)
          return;
        if (args.length == 1) {
          Rank r = c.getRank(plr);
          Utils.msg(plr, "rank.your", new String[] { "<rank>", r.name, "<perms>", r.getPermissions() });
        }
        else {
          if (!c.pls.contains(args[1])) {
            Utils.msg(plr, "noclan.other", new String[] { "<player>", args[1] });
            return;
          }
          Rank r = c.getPlayerRank(args[1]);
          if (args.length == 2) {
            Utils.msg(plr, "rank.player", new String[] { "<player>", args[1], "<rank>", r.name, "<perms>", r.getPermissions() });
          }
          else {
            Rank r2 = c.containsRank(args[2]);
            if (r == null) {
              Utils.msg(plr, "rank.notfound", new String[] { "<rank>", args[2] });
            } else if ((Utils.kf.getBoolean("owners.mustbeoneowner")) && (r.name.equals("owner")) && (r.players.size() == 1)) {
              Utils.msg(plr, "rank.lastowner", new String[] { "<name>", args[1] });
            }
            else {
              r.players.remove(args[1]);
              r2.players.add(args[1]);
              Utils.msg(plr, "rank.set", new String[] { "<name>", args[1], "<rank>", args[2] });
              Utils.adminMsg(plr, c, "rank.changed", new String[] { "<player>", plr.getName(), "<name>", args[1], "<rank>", args[2] });
            }
          }
        }
      }
      else if (ct == Utils.CommandType.Invite) {
        Clan c = Utils.clanTest(plr, cn, Rank.PermType.Invite);
        if (c == null)
          return;
        if (args.length == 1) {
          List inv = new ArrayList();
          for (Map.Entry e : Utils.invitations.entrySet()) {
            if (e.getValue() == c)
              inv.add((String)e.getKey());
          }
          Utils.msg(plr, "invitations", new String[] { "<invitations>", StringUtils.join(inv, ", ") });
        }
        else {
          MC_Player inv = KFA.srv.getOnlinePlayerByName(args[1]);
          if (inv == null) {
            Utils.msg(plr, "invite.playernotonline", new String[] { "<player>", args[1] });
            return;
          }
          Clan c2 = Clans.getClan(inv);
          if (c2 != null) {
            Utils.msg(plr, "inclan.other", new String[] { "<player>", args[1] });
            return;
          }
          c2 = (Clan)Utils.invitations.get(args[1]);
          if ((c2 != null) && (c2 != c)) {
            Utils.msg(plr, "invite.hasinvitation", new String[] { "<player>", args[1] });
            return;
          }
          if (c2 == c) {
            Utils.invitations.remove(args[1]);
            Utils.msg(inv, "invite.invcancelled", new String[] { "<player>", plr.getName(), "<name>", c.name });
            Utils.msg(plr, "invite.cancelled", new String[] { "<player>", args[1] });
          }
          else {
            Utils.invitations.put(args[1], c);
            Utils.msg(inv, "invite.invitation", new String[] { "<player>", plr.getName(), "<name>", c.name });
            Utils.msg(plr, "invite.invited", new String[] { "<player>", args[1] });
          }
        }
      }
      else if (ct == Utils.CommandType.SetSpawn) {
        Clan c = Utils.clanTest(plr, cn, Rank.PermType.SetSpawn);
        if (c == null)
          return;
        c.spawnloc = plr.getLocation();
        Utils.msg(plr, "setspawn", new String[] { "<x>", c.spawnloc.getBlockX(), 
          "<y>", c.spawnloc.getBlockY(), 
          "<z>", c.spawnloc.getBlockZ() });
        Utils.adminMsg(plr, c, "setspawn", new String[] { "<x>", c.spawnloc.getBlockX(), 
          "<y>", c.spawnloc.getBlockY(), 
          "<z>", c.spawnloc.getBlockZ() });
      }
      else if (ct == Utils.CommandType.Spawn) {
        Clan c = Utils.clanTest(plr, cn, null);
        if (c == null)
          return;
        plr.teleport(c.spawnloc == null ? KFA.srv.getWorld(plr.getLocation().dimension).getSpawnLocation() : c.spawnloc);
        Utils.msg(plr, "spawn", new String[0]);
      }
      else if (ct == Utils.CommandType.List) {
        List l = Lists.newArrayList(Utils.clans.keySet());
        Collections.sort(l);
        Utils.msg(plr, "list", new String[] { "<clans>", StringUtils.join(l, ", ") });
      }
      else if (ct == Utils.CommandType.Info) {
        boolean test = (args.length > 1) && (plr.hasPermission("clans.infoanyclan"));
        Clan c = test ? (Clan)Utils.clans.get(args[1]) : Utils.clanTest(plr, cn, null);
        if (c == null) {
          if (test)
            Utils.msg(plr, "notexists", new String[] { "<name>", args[1] });
          return;
        }
        String f = KFA.l(plr, "clans.info.playerlist");
        String pll = "";
        for (Rank r : c.ranks) {
          pll = pll + f.replace("<rank>", r.name).replace("<players>", StringUtils.join(r.players, ", "));
        }
        Utils.msg(plr, "info", new String[] { "<name>", c.name, "<prefix>", c.prefix, "<suffix>", c.suffix, 
          "<playerlist>", pll });
      }
      else if (ct == Utils.CommandType.MaxPlayers) {
        Clan c = Utils.clanTest(plr, cn, args.length == 1 ? null : Rank.PermType.MaxPlayer);
        if (c == null)
          return;
        if (args.length == 1) {
          Utils.msg(plr, "maxplr", new String[] { "<current>", c.pls.size(), "<maximum>", c.maxpls });
        }
        else {
          c.maxpls = Integer.valueOf(args[1]).intValue();
          Utils.msg(plr, "maxplr.set", new String[] { "<maximum>", c.maxpls });
          Utils.adminMsg(plr, c, "maxplr.set.other", new String[] { "<player>", plr.getName(), "<maximum>", c.maxpls });
        }
      }
      else if (ct == Utils.CommandType.Accept) {
        Clan c = (Clan)Utils.invitations.remove(pln);
        if (c == null) {
          Utils.msg(plr, "invite.noinvitation", new String[0]);
          return;
        }
        Utils.adminMsg(plr, c, "invite.accepted", new String[] { "<name>", pln });
        c.addPlayer(pln);
        Utils.msg(plr, "invite.join", new String[] { "<name>", c.name });
      }
      else if (ct == Utils.CommandType.Deny) {
        Clan c = (Clan)Utils.invitations.remove(pln);
        if (c == null) {
          Utils.msg(plr, "invite.noinvitation", new String[0]);
          return;
        }
        Utils.adminMsg(plr, c, "invite.rejected", new String[] { "<name>", pln });
        Utils.msg(plr, "invite.deny", new String[] { "<name>", c.name });
      }
      else if (ct == Utils.CommandType.Ally) {
        Clan c = Utils.clanTest(plr, cn, args.length == 1 ? null : Rank.PermType.Ally);
        if (c == null)
          return;
        if (args.length == 1) {
          Utils.msg(plr, "ally", new String[] { "<ally>", StringUtils.join(c.allys, ", "), 
            "<requests>", StringUtils.join(c.allyrequests, ", "), 
            "<pending>", StringUtils.join(c.pendingallys, ", ") });
        }
        else {
          Clan inv = (Clan)Utils.clans.get(args[1]);
          if (inv == null) {
            Utils.msg(plr, "notexists", new String[] { "<name>", args[1] });
          }
          else if (inv == c) {
            Utils.msg(plr, "ally.yourclan", new String[0]);
          }
          else if (c.allyrequests.contains(args[1])) {
            if ((args.length > 2) && (args[2].equals("-"))) {
              c.allyrequests.remove(args[1]);
              inv.pendingallys.remove(c.name);
              Utils.msg(plr, "ally.denied", new String[] { "<clan>", args[1] });
              Utils.adminMsg(plr, c, "ally.denied.player", new String[] { "<player>", plr.getName(), "<clan>", args[1] });
              Utils.adminMsg(plr, inv, "ally.deny", new String[] { "<clan>", c.name });
            }
            else {
              c.allyrequests.remove(args[1]);
              c.allys.add(args[1]);
              inv.pendingallys.remove(c.name);
              inv.allys.add(c.name);
              Utils.msg(plr, "ally.accepted", new String[] { "<clan>", args[1] });
              Utils.adminMsg(plr, c, "ally.accepted.player", new String[] { "<player>", plr.getName(), "<clan>", args[1] });
              Utils.adminMsg(plr, inv, "ally.accept", new String[] { "<clan>", c.name });
            }
          }
          else if (c.pendingallys.contains(args[1])) {
            c.pendingallys.remove(args[1]);
            inv.allyrequests.remove(c.name);
            Utils.msg(plr, "ally.cancelled", new String[] { "<clan>", args[1] });
            Utils.adminMsg(plr, c, "ally.cancelled.player", new String[] { "<player>", plr.getName(), "<clan>", args[1] });
            Utils.adminMsg(plr, inv, "ally.cancel", new String[] { "<clan>", c.name });
          }
          else if (c.allys.contains(args[1])) {
            c.allys.remove(args[1]);
            inv.allys.remove(c.name);
            Utils.msg(plr, "ally.broke", new String[] { "<clan>", args[1] });
            Utils.adminMsg(plr, c, "ally.broke.player", new String[] { "<player>", plr.getName(), "<clan>", args[1] });
            Utils.adminMsg(plr, inv, "ally.break", new String[] { "<clan>", c.name });
          }
          else {
            c.pendingallys.add(args[1]);
            inv.allyrequests.add(c.name);
            Utils.msg(plr, "ally.requested", new String[] { "<clan>", args[1] });
            Utils.adminMsg(plr, c, "ally.requested.player", new String[] { "<player>", plr.getName(), "<clan>", args[1] });
            Utils.adminMsg(plr, inv, "ally.request", new String[] { "<clan>", c.name });
          }
        }
      }
      else if (ct == Utils.CommandType.Reload) {
        Utils.load();
        Utils.msg(plr, "reloaded", new String[0]);
      }
      else if (ct == Utils.CommandType.Save) {
        Utils.saveclans();
        Utils.msg(plr, "saved", new String[0]);
      }
    }
  }

  public boolean hasPermissionToUse(MC_Player plr) {
    return plr.hasPermission("clans.use");
  }
}

/* Location:           C:\Users\Gyuri\Downloads\Clans.jar
 * Qualified Name:     Clans.Command
 * JD-Core Version:    0.6.2
 */