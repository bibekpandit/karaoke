package karaoke.sound;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AbcBuilder Class to build Abc Music Object as you parse through the AbcBody in MusicLanguageParser Class
 * 
 * Specification Author: Marwa Abdulhai
 * Implementation Author: Marwa Abdulhai
 *
 */
public class AbcBuilder {
    private final List<Bar> totalMusic;

    private List<Music> barNotes;
    private List<Music> tupletNotes;
    private List<Note> chordNotes;
    private Map<Character,String> accidentals;
    
    private List<Bar> beginRepeat;
    private List<Bar> firstRepeat;
    private List<Bar> secondRepeat;

    private int repeatStatus;
    private String status;
    private boolean repeatType;
    
    private final int beginRepeatStatus = 1;
    private final int firstRepeatEndingStatus = 2;
    private final int secondRepeatEndingStatus = 3;

    
    // repeatMap provides information on when to repeat
    private final Map<Integer, List<Integer>> repeatMap = new HashMap<>();

    private double tupletDuration ;

    private List<String> lyrics;
    
    /*
     * AF(totalMusic, barNotes, tupletNotes, chordNotes, =  Builds an Abc Body into a list of Music objects in totalMusic
     * Accidentals, beginRepeat, firstRepeat, secondRepeat) 
     */
    
    /**
     * Create AbcBuilder object to Build Music containing Notes, Chords, Tuplets, 
     * with Repeats inside Bar, Bars inside Repeats and Concats
     */
    public AbcBuilder() {
        this.totalMusic = new ArrayList<Bar>();

        this.barNotes  = new ArrayList<Music>();
        this.tupletNotes = new ArrayList<Music>();
        this.chordNotes = new ArrayList<Note>();
        this.accidentals = new HashMap<Character,String>();
        
        this.beginRepeat = new ArrayList<Bar>();
        this.firstRepeat = new ArrayList<Bar>();
        this.secondRepeat = new ArrayList<Bar>();
        this.repeatStatus = 0;
        status = "";
    }

    /**
     * Add Music object to Bar
     * @param music to add
     */
    public void addToBar(Music music) {
        barNotes.add(music);

    }
    
    /**
     * Add Note object to Chord
     * @param note to add 
     */
    public void addToChord(Note note) {
        chordNotes.add(note);
    }
    
    /**
     * Add Music object to Tuplet
     * @param music to add
     */
    public void addToTuplet(Music music) {
        tupletNotes.add(music);
    }


    /**
     * Return current accumulation of notes in a Chord within a specific Bar
     * @return list of notes in the chord
     */
    public List<Note> getChordNotes() {
        return this.chordNotes;
    }

    /**
     * Get list of Music objects inside a Tuplet
     * @return list of music 
     */
    public List<Music> getTupletNotes() {
        return tupletNotes;
    }

    /**
     * Return current accumulation of the music objects
     * @return list of music gathered so far
     */
    public List<Bar> getTotalMusic() {
        return this.totalMusic;
    }



    /**
     * Adds Tuplet to a Bar
     * @param tuplet to add to Bar
     */
    public void addTuplet(Tuplet tuplet) {
        this.barNotes.add(tuplet);
        //this.tupletNotes = new ArrayList<Music>(); // is the following necessary after this one

    }

    /**
     * Reset Tuplet after adding to Bar
     */
    public void resetTuplet() {
        this.tupletNotes = new ArrayList<Music>();
        
    }

    /**
     * Reset Chord after adding to Bar
     */

    public void resetChord() {
        this.chordNotes = new ArrayList<Note>();
        
    }

    /**
     * Add Accidental found in the Bar
     * @param c that accidental is applied on
     */
    public void addAccidental(char c,String type) {
        this.accidentals.put(c, type);
        
    }


    /**
     * Set status of where you are in the Repeat 
     * @param status of repeat
     */
    public void setRepeatStatus(int status) {
        this.repeatStatus = status;
        
    }



    /**
     * Return status of where to add Music object to
     * @return string containing status of where to add Music object (Bar, Chord, or Tuplet)
     */
    public String getStatus() {
        return this.status;
    }
    
    /**
     * Setting status of where to add music objects to 
     * @param status string representing status of where to add music object
     *        Can take values of "Bar", "Chord" or "Tuplet"
     */
    public void setStatus(String status) {  //assert statement here
        this.status = status;
    }

    /**
     * Adds Music objects in a single Bar to the Concatenation of Entire Music
     * Applies Accidentals to Music Notes in the Bar
     */
    public void resetBar() {
        Bar bar = new Bar(this.barNotes);
        if(repeatStatus == 0) {
            this.totalMusic.add(bar);
        }
        else if(repeatStatus == beginRepeatStatus) {
            this.beginRepeat.add(bar);

        }
        else if(repeatStatus == firstRepeatEndingStatus) {
            this.firstRepeat.add(bar);
        }
        else if(this.repeatType && repeatStatus == secondRepeatEndingStatus) { //initial value of repeat type?
            this.firstRepeat.add(bar);
            // putting (key, value) into the repeat map. key holds the position of where repeat has to start
            // value specifies the range of sub-list of bars that has to be repeated at position key.
            // key = L + l_0
            // value = [L, L+ l_0]
            int key = this.totalMusic.size() + this.firstRepeat.size();
            List<Integer> value = Arrays.asList(this.totalMusic.size(),
                                                this.totalMusic.size()+this.firstRepeat.size());
            repeatMap.put(key,value);
            
            for(Bar b: this.firstRepeat) {
                totalMusic.add(b);
            }
            this.beginRepeat = new ArrayList<Bar>();
            this.firstRepeat = new ArrayList<Bar>();
            this.secondRepeat = new ArrayList<Bar>();
            this.repeatStatus = 0;
            this.repeatType = false;

        }
        else if(repeatStatus == secondRepeatEndingStatus) {
            this.secondRepeat.add(bar);
            // key = L + l_0 + l_1
            // value = [L, L+ l_0]
            int key = this.totalMusic.size()+this.beginRepeat.size()+this.firstRepeat.size();
            List<Integer> value = Arrays.asList(this.totalMusic.size(),
                                                this.totalMusic.size()+this.beginRepeat.size());
            repeatMap.put(key, value);
            totalMusic.addAll(this.beginRepeat);
            totalMusic.addAll(this.firstRepeat);
            totalMusic.addAll(this.secondRepeat);

            this.beginRepeat = new ArrayList<Bar>();
            this.firstRepeat = new ArrayList<Bar>();
            this.secondRepeat = new ArrayList<Bar>();
            this.repeatStatus = 0;
            
        }
        this.barNotes = new ArrayList<Music>();
        this.chordNotes = new ArrayList<Note>();
        this.tupletNotes = new ArrayList<Music>();
        this.accidentals = new HashMap<Character,String>();
        
    }

    
    /**
     * Returns to String representation of Music accumulated so far by the AbcBuilder
     * @return string of the AbcBuilder Music objects
     */
    @Override
    public String toString() {
        String total = "";
        for(Music music: this.totalMusic) {
            total+=music.toString();
            
        }
        return total;
    }

    public List<Bar> getMusicLine() {
        return this.totalMusic;
    }

    public Pitch applyAccidental(Character pitchChar) {
        Pitch pitch = new Pitch(pitchChar);
        if(this.accidentals.containsKey(pitchChar)) { //how is accidental being handled
            String type = this.accidentals.get(pitchChar);
            if(type.indexOf("^")!=-1) {
                for(int i = 0; i<type.length();i++) {
                    pitch.transpose(1);
                }
            }
            else if(type.indexOf("_")!=-1) {
                for(int i = 0; i<type.length();i++) {
                    pitch.transpose(-1);
                }
            }
        }
        return pitch;
    }


    public void flagSimpleRepeat(boolean b) {
        this.repeatType = b;
    }

    public int getBarNotesSize() {
        return this.barNotes.size();
    }

    public Map<Integer, List<Integer>> getHashMap() {
        return this.repeatMap;
    }

    public void setTupletDuration(double duration) {
       this.tupletDuration = duration;
        
    }
    public double getTupletDuration() {
        return this.tupletDuration;
         
     }


    public void setLyrics(List<String> lyrics) {
        this.lyrics = lyrics;
        
    }
    public List<String> getLyrics() {
        return this.lyrics;
        
    }

}

