package Clans;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class RandomPrefix
{
  static final Character[] colors = { Character.valueOf('0'), Character.valueOf('1'), Character.valueOf('2'), Character.valueOf('3'), Character.valueOf('4'), Character.valueOf('5'), Character.valueOf('6'), Character.valueOf('7'), Character.valueOf('8'), Character.valueOf('9'), Character.valueOf('a'), Character.valueOf('b'), Character.valueOf('c'), Character.valueOf('d'), Character.valueOf('e'), Character.valueOf('f') };
  static final String[] formatters = { "", "§o", "§n", "§n§o", "§l", "§l§o", "§l§o§n" };

  public static String getNewPrefix() { List forms = Arrays.asList(formatters);
    Collections.shuffle(forms);
    List col = Arrays.asList(colors);
    Collections.shuffle(col);
    List prefixes = new ArrayList();
    for (Clan c : Utils.clans.values())
      if (!c.prefix.isEmpty())
        prefixes.add(c.prefix);
    Iterator localIterator2;
    for (??? = forms.iterator(); ???.hasNext(); 
      localIterator2.hasNext())
    {
      String f = (String)???.next();
      localIterator2 = col.iterator(); continue; char c = ((Character)localIterator2.next()).charValue();
      if (!prefixes.contains("§" + c + f)) {
        return "§" + c + f;
      }
    }

    return null;
  }
}

/* Location:           C:\Users\Gyuri\Downloads\Clans.jar
 * Qualified Name:     Clans.RandomPrefix
 * JD-Core Version:    0.6.2
 */