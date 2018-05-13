package karaoke.sound;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Bibek Kumar Pandit
 *
 * Concat represents an immutable datatype that concatenates multiple pieces of Music (type Bar) 
 * to return concatenated a Music. 
 */
public class Concat implements Music{

    
    //fields
    private final List<Bar> music;
    private final Map<Integer, List<Integer>> mapOfRepeats;
    private final List<Bar> musicToPlay;
    private final double durationEachBar;
    
    /**
     * Constructor of Concat
     * @param music list of bars to play which doesn't encapsulate repeats yet
     * @param map map carrying information of repeat positions and music to repeat
     */
    public Concat(List<Bar> music, Map<Integer, List<Integer>> map) {
        
        this.music = Collections.synchronizedList(Collections.unmodifiableList(new ArrayList<Bar>(music)));
        this.mapOfRepeats = Collections.synchronizedMap(Collections.unmodifiableMap(new HashMap<Integer, List<Integer>>(map)));
        this.musicToPlay = this.encapsulateRepeat();
        
        if (this.musicToPlay.isEmpty()) {
            this.durationEachBar = 0.0;
        }
        else {
            this.durationEachBar = this.musicToPlay.get(0).getDuration();
        }
        
        checkRep();
    }
    
    private void checkRep() {
//        assert music != null;
//        assert mapOfRepeats != null;
//        assert musicToPlay != null;
//        for (Bar bar: musicToPlay) {
//            assert bar.getDuration() == durationEachBar;
//        }
    }
    
    private List<Bar> encapsulateRepeat(){
        List<Bar> listCapturingRepeat = new ArrayList<>();
        for(int i = 0; i < music.size(); i++) {
            if (mapOfRepeats.containsKey(i)) {
                int startIndex = mapOfRepeats.get(i).get(0);
                int endIndex = mapOfRepeats.get(i).get(1);
                listCapturingRepeat.addAll(music.subList(startIndex, endIndex));
            }
            listCapturingRepeat.add(music.get(i));
        }
        return listCapturingRepeat;
    }
    
    @Override
    public double getDuration() {
        checkRep();
        return durationEachBar*musicToPlay.size();
    }
    
    @Override
    public void play(SequencePlayer player, double atBeat) {
        double offsetDuration = 0;
        for (Bar bar : musicToPlay) {
            bar.play(player, atBeat+offsetDuration);
            offsetDuration += bar.getDuration();
        }
    }
    
    /**
     * @return list of bars that form the music (repeat factored)
     */
    public List<Bar> getMusic() {
        return new ArrayList<Bar>(this.musicToPlay);
    }

    /**
     * @return map conveying information on repeat.
     */
    public Map<Integer, List<Integer>> getHashMap() {
        return new HashMap<Integer, List<Integer>>(mapOfRepeats);
    }
   
    @Override
    public String toString() {
        String concat = "";
        for(Music m: music) {
            concat += m.toString();
        }
        return concat;
        
    }
}
