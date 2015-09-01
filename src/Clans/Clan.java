package Clans;

import PluginReference.MC_Location;
import PluginReference.MC_Player;
import PluginReference.MC_Server;
import WrapperObjects.Entities.PlayerWrapper;
import gyurix.konfigfajl.ConfigFile;
import gyurix.konfigfajl.KFA;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import joebkt.EntityPlayer;
import joebkt.Packet_Teams;
import joebkt.PlayerConnection;

public class Clan
{
  MC_Location spawnloc = null;
  List<Rank> ranks = new ArrayList();
  List<String> pls = new ArrayList();
  List<String> allys = new ArrayList();
  List<String> allyrequests = new ArrayList();
  List<String> pendingallys = new ArrayList();
  List<String> enemies = new ArrayList();
  public String prefix;
  public String suffix = "§r";
  public String displayname;
  public String name;
  int maxpls;

  public Clan(String n)
  {
    this.name = n;
    this.displayname = n;
  }
  public Clan(MC_Player plr, String n) {
    if (Utils.kf.getBoolean("clancreationspawn"))
      this.spawnloc = plr.getLocation();
    this.name = n;
    this.displayname = n;
    this.ranks.add(new Rank("owner", plr));
    this.ranks.add(new Rank(plr, ("member " + Utils.kf.get("memberperms")).split("\\ ")));
    this.pls.add(plr.getName());
    this.prefix = RandomPrefix.getNewPrefix();
    if (Utils.kf.getBoolean("scoreboard"))
      sendPacket(null, 0);
    this.maxpls = ((int)Utils.kf.getLong("maxplayer.default", 16L));
    if (Utils.kf.getBoolean("clancreationspawn"))
      this.spawnloc = plr.getLocation();
    Utils.clans.put(n, this);
  }
  public void remove() {
    Utils.clans.remove(this.name);
    for (Clan c : Utils.clans.values()) {
      c.allyrequests.remove(this.name);
      c.pendingallys.remove(this.name);
      c.allys.remove(this.name);
      c.enemies.remove(this.name);
    }
    this.ranks = null;
    for (String pln : this.pls) {
      Utils.chats.remove(pln);
    }
    this.pls = null;
    if (Utils.kf.getBoolean("scoreboard"))
      sendPacket(null, 1); 
  }

  public void removePlayer(String pln) { Rank r = getPlayerRank(pln);
    if (this.pls.remove(pln)) {
      r.players.remove(pln);
      Utils.chats.remove(pln);
      if (Utils.kf.getBoolean("scoreboard"))
        sendPacketPlayer(pln, 4);
    } }

  public void addPlayer(String pln) {
    containsRank("member").players.add(pln);
    this.pls.add(pln);
    if (Utils.kf.getBoolean("chat.clans.autoenable"))
      Utils.chats.put(pln, this);
    if (Utils.kf.getBoolean("scoreboard"))
      sendPacketPlayer(pln, 3);
  }

  public void sendPacket(MC_Player plr, int m) {
    Packet_Teams p1 = new Packet_Teams();
    p1.a = (this.name.length() < 17 ? this.name : this.name.substring(0, 16));
    p1.h = m;
    if ((m == 0) || (m == 2)) {
      p1.b = p1.a;
      p1.c_prefix = this.prefix;
      p1.d_suffix = this.suffix;
    }
    p1.g = this.pls;
    if (plr == null) {
      for (MC_Player p : KFA.srv.getPlayers())
        ((PlayerWrapper)p).plr.plrConnection.sendPacket(p1);
    }
    else
      ((PlayerWrapper)plr).plr.plrConnection.sendPacket(p1);
  }

  public void sendPacketPlayer(String pln, int m)
  {
    Packet_Teams p1 = new Packet_Teams();
    p1.a = (this.name.length() < 17 ? this.name : this.name.substring(0, 16));
    p1.h = m;
    p1.g.add(pln);
    for (MC_Player p : KFA.srv.getPlayers())
      ((PlayerWrapper)p).plr.plrConnection.sendPacket(p1);
  }

  public Rank getRank(MC_Player plr) {
    return getPlayerRank(plr.getName());
  }
  public Rank getPlayerRank(String pln) {
    for (Rank r : this.ranks) {
      if (r.players.contains(pln)) {
        return r;
      }
    }
    return null;
  }
  public boolean hasPerm(MC_Player plr, Rank.PermType perm) {
    if (perm == null)
      return true;
    Rank r = getRank(plr);
    if (r == null) {
      return false;
    }
    return r.hasPerm(perm);
  }
  public boolean removeRank(String name) {
    for (Rank r : this.ranks)
      if (r.name.equals(name)) {
        this.ranks.remove(r);
        containsRank("member").players.addAll(r.players);
        return true;
      }
    return false;
  }
  public Rank containsRank(String name) {
    for (Rank r : this.ranks)
      if (r.name.equals(name))
        return r;
    return null;
  }
}

/* Location:           C:\Users\Gyuri\Downloads\Clans.jar
 * Qualified Name:     Clans.Clan
 * JD-Core Version:    0.6.2
 */