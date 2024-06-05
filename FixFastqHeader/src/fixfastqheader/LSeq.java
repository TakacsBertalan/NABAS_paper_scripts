/*............................................................................................................
 ..##....##..######....######.....########.##.....##.########..##........#######..########..########.########.
 ..###...##.##....##..##....##....##........##...##..##.....##.##.......##.....##.##.....##.##.......##.....##
 ..####..##.##........##..........##.........##.##...##.....##.##.......##.....##.##.....##.##.......##.....##
 ..##.##.##.##...####..######.....######......###....########..##.......##.....##.########..######...########.
 ..##..####.##....##........##....##.........##.##...##........##.......##.....##.##...##...##.......##...##..
 ..##...###.##....##..##....##....##........##...##..##........##.......##.....##.##....##..##.......##....##.
 ..##....##..######....######.....########.##.....##.##........########..#######..##.....##.########.##.....## 
 .............................................................................................................
 ................................. NGS eXplorer by Gábor Jaksa ...............................................
 .............................................................................................................
 */
package fixfastqheader;

/**
 * Interface for large sequences
 *
 * @author leo
 *
 */
public interface LSeq {

    //public long lookup(int index);
    public long lookup(long index);

    public long textSize();

    public void set(long index, long value);
    //public void set(int index, long value);

    public long increment(long index, long value);
    //public long increment(int index, long value);

}