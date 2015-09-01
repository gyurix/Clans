package Clans;

import PluginReference.MC_Player;
import gyurix.konfigfajl.KFA;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;

public class Rank
{
  BitSet perms = new BitSet(13);
  List<String> players = new ArrayList();
  String name;

  public Rank(String n)
  {
    this.name = n;
  }
  public Rank(String n, MC_Player plr) {
    this.name = n;
    this.players.add(plr.getName());
    this.perms.set(0, 13);
  }
  public boolean hasPerm(PermType perm) {
    return perm == null ? true : this.perms.get(perm.id);
  }
  public String getPermissions() {
    String out = "";
    for (PermType perm : PermType.values()) {
      if (this.perms.get(perm.id))
        out = out + ", " + perm.name();
    }
    return out.length() == 0 ? "" : out.substring(2);
  }
  public Rank(MC_Player plr, String[] args) {
    this.name = args[0];
    for (String s : (String[])ArrayUtils.subarray(args, 1, args.length))
      try {
        this.perms.set(PermType.valueOf(s).id);
      }
      catch (Throwable e) {
        plr.sendMessage(KFA.l(plr, "clans.editranks.invalidperm")
          .replace("<perm>", s));
      }
  }

  public void switchPerms(MC_Player plr, String[] perm) {
    for (String s : perm)
      try {
        PermType t = PermType.valueOf(s);
        this.perms.flip(t.id);
        Utils.msg(plr, "editranks." + (this.perms.get(t.id) ? "add" : "remove") + "perm", new String[] { 
          "<perm>", s, "<rank>", this.name });
      }
      catch (Throwable e) {
        plr.sendMessage(KFA.l(plr, "clans.editranks.invalidperm")
          .replace("<perm>", s));
      }
  }

  public void setPerms(String permlist) {
    for (String s : permlist.split("\\ "))
      try {
        PermType t = PermType.valueOf(s);
        this.perms.set(t.id);
      }
      catch (Throwable e) {
        Utils.msg(null, "editranks.invalidperm", new String[] { "<perm>", s });
      }
  }

  public static enum PermType
  {
    Invite(0), Kick(1), Name(2), MaxPlayer(3), Prefix(4), Suffix(5), GiveRank(6), EditRanks(7), SetSpawn(8), Remove(9), Ally(10), Enemy(11), AdminMessages(12);

    private int id;

    private PermType(int i) { this.id = i; }

  }
}

/* Location:           C:\Users\Gyuri\Downloads\Clans.jar
 * Qualified Name:     Clans.Rank
 * JD-Core Version:    0.6.2
 */