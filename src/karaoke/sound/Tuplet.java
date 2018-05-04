package karaoke.sound;

import java.util.List;

/**
 * An immutable data type representing a tuplet 
 * A Tuplet is a consecutive group of notes that are to be played for a duration
 *  that is either greater or less than the sum of the individual notes within that group
 * @author Myra
 *
 */
public class Tuplet implements Music {
    private final List<Note> notes;
    private final double duration;
    /**
     * Creates a Tuplet consisting of notes fitted to the proper duration
     * @param notes
     * @param duration
     */
    public Tuplet(List<Note> notes, double duration) {
        this.notes = notes;
        this.duration = duration;
    }
    
    // TODO checkRep A tuplet may not contain rests, but it may contain chords.

    @Override
    public double getDuration() {
        return this.duration;
    }
    
    @Override
    public void play(SequencePlayer player, double atBeat) {
        // TODO Auto-generated method stub
        
    }
    
    @Override 
    public String toString() {
        String ans = "(";
        ans += this.duration;
        for (Note note: notes) {
            ans += note;
        }
        return ans;   
    }
    

}