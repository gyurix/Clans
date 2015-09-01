package Clans;

import PluginReference.PluginBase;
import PluginReference.PluginInfo;

public class MyPlugin extends PluginBase
{
  PluginInfo info;
  public static PluginBase pl;

  public PluginInfo getPluginInfo()
  {
    PluginInfo inf = new PluginInfo();
    inf.eventSortOrder = -20.0D;
    inf.description = "Clan management plugin!";
    pl = this; inf.ref = this;
    return this.info = inf;
  }

  public void onServerFullyLoaded() {
    this.info.ref = new Clans();
  }
}

/* Location:           C:\Users\Gyuri\Downloads\Clans.jar
 * Qualified Name:     Clans.MyPlugin
 * JD-Core Version:    0.6.2
 */