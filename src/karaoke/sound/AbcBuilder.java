package karaoke.sound;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A mutable AbcBuilder Class to build Abc Music Object as you parse through the AbcBody in MusicLanguageParser Class
 * 
 * Specification Author: Marwa Abdulhai
 * Implementation Author: Marwa Abdulhai
 *
 */
public class AbcBuilder {
    private List<Music> barNotes;
    private List<Music> tupletNotes;
    private List<Note> chordNotes;
    private Map<Character,String> accidentals;
    
    private String status;
    private boolean isLastLine;    
    private static final Map<String, List<String>> KEY_ACCIDENTALS;
    
    private final Map<String, VoiceBuilder> musicForVoice;
    
    private double tupletDuration ;
    private List<String> lyrics;
    private int lyricsCounter;

    private String currentSinger;

    private boolean inMusicParsing;
    static
    {
        KEY_ACCIDENTALS = new HashMap<String, List<String>> ();
        KEY_ACCIDENTALS.put("C", Arrays.asList("C","D","E","F","G","A","B"));
        KEY_ACCIDENTALS.put("Am", Arrays.asList("C","D","E","F","G","A","B"));
        KEY_ACCIDENTALS.put("G", Arrays.asList("C","D","E","F#","G","A","B"));
        KEY_ACCIDENTALS.put("Em", Arrays.asList("C","D","E","F#","G","A","B"));
        KEY_ACCIDENTALS.put("D", Arrays.asList("C#","D","E","F#","G","A","B"));
        KEY_ACCIDENTALS.put("Bm", Arrays.asList("C#","D","E","F#","G","A","B"));
        KEY_ACCIDENTALS.put("A", Arrays.asList("C#","D","E","F#","G#","A","B"));
        KEY_ACCIDENTALS.put("F#m", Arrays.asList("C#","D","E","F#","G#","A","B"));
        KEY_ACCIDENTALS.put("E", Arrays.asList("C#","D#","E","F#","G#","A","B"));
        KEY_ACCIDENTALS.put("C#m", Arrays.asList("C#","D#","E","F#","G#","A","B"));
        KEY_ACCIDENTALS.put("B", Arrays.asList("C#","D#","E","F#","G#","A#","B"));
        KEY_ACCIDENTALS.put("G#m", Arrays.asList("C#","D#","E","F#","G#","A#","B"));
        KEY_ACCIDENTALS.put("F#", Arrays.asList("C#","D#","E#","F#","G#","A#","B"));
        KEY_ACCIDENTALS.put("D#m", Arrays.asList("C#","D#","E#","F#","G#","A#","B"));
        KEY_ACCIDENTALS.put("C#", Arrays.asList("C#","D#","E#","F#","G#","A#","B#"));
        KEY_ACCIDENTALS.put("A#m", Arrays.asList("C#","D#","E#","F#","G#","A#","B#"));

        KEY_ACCIDENTALS.put("F", Arrays.asList("C","D","E","F","G","A","Bb"));
        KEY_ACCIDENTALS.put("Dm", Arrays.asList("C","D","E","F","G","A","Bb"));
        KEY_ACCIDENTALS.put("Bb", Arrays.asList("C","D","Eb","F","G","A","Bb"));
        KEY_ACCIDENTALS.put("Gm", Arrays.asList("C","D","Eb","F","G","A","Bb"));
        KEY_ACCIDENTALS.put("Eb", Arrays.asList("C","D","Eb","F","G","Ab","Bb"));
        KEY_ACCIDENTALS.put("Cm", Arrays.asList("C","D","Eb","F","G","Ab","Bb"));
        KEY_ACCIDENTALS.put("Ab", Arrays.asList("C","Db","Eb","F","G","Ab","Bb"));
        KEY_ACCIDENTALS.put("Fm", Arrays.asList("C","Db","Eb","F","G","Ab","Bb"));
        KEY_ACCIDENTALS.put("Db", Arrays.asList("C","Db","Eb","F","Gb","Ab","Bb"));
        KEY_ACCIDENTALS.put("Bbm", Arrays.asList("C","Db","Eb","F","Gb","Ab","Bb"));
        KEY_ACCIDENTALS.put("Gb", Arrays.asList("Cb","Db","Eb","F","Gb","Ab","Bb"));
        KEY_ACCIDENTALS.put("Ebm", Arrays.asList("Cb","Db","Eb","F","Gb","Ab","Bb"));
        KEY_ACCIDENTALS.put("Cb", Arrays.asList("Cb","Db","Eb","Fb","Gb","Ab","Bb"));
        KEY_ACCIDENTALS.put("Abm", Arrays.asList("Cb","Db","Eb","Fb","Gb","Ab","Bb"));

    }
    
    /*
     * AF(totalMusic, barNotes, tupletNotes, chordNotes, =  Builds an Abc Body into a list of Music objects in totalMusic
     * Accidentals, beginRepeat, firstRepeat, secondRepeat) 
     */
    
    /**
     * Create AbcBuilder object to Build Music containing Notes, Chords, Tuplets, 
     * with Repeats inside Bar, Bars inside Repeats and Concats
     */
    public AbcBuilder() {
        this.barNotes  = new ArrayList<Music>();
        this.tupletNotes = new ArrayList<Music>();
        this.chordNotes = new ArrayList<Note>();
        this.accidentals = new HashMap<Character,String>();
        this.lyricsCounter = 0;
        this.lyrics = new ArrayList<String>();
        this.status = "";
        this.currentSinger = "";
        this.musicForVoice = new HashMap<>();
        this.isLastLine = false;
    }
    
    /**
     * Resets the Lyrics 
     */
    public void resetLyricsCounter() {
        this.lyricsCounter=0;
    }
    
    /**
     * Returns the music for voice
     * @return the music map
     */
    public Map<String, VoiceBuilder> getMusicForVoice(){
        return this.musicForVoice;
    }
    
    /**
     * Returns if we are at the last line in the music
     * @return true if at the last line
     */
    public boolean isLastLine() {
        return this.isLastLine;
    }
    
    /**
     * Set to true if at the last line
     * @param val true if at the last line
     */
    public void setLastLine(boolean val) {
        this.isLastLine = val;
    }
    
    /**
     * @return VoiceBuilder associated with current singer(or voice)
     */
    public VoiceBuilder getCurrentVoiceBuilder() {
        if (this.getSinger().equals("OneVoice")) {
            return this.musicForVoice.get("OneVoice");
        }
        return this.musicForVoice.get(currentSinger);
    }
    
    /**
     * @param singers different voices in the music
     */
    public void addSingers(List<String> singers) {
        assert this.musicForVoice.keySet().isEmpty();
        for (String singer : singers) {
            this.musicForVoice.put(singer, new VoiceBuilder(singer));
        }
    }
    
    /**
     * Add voice when only default voice in music
     */
    public void addSingers() {
        assert this.musicForVoice.keySet().isEmpty();
        this.musicForVoice.put("OneVoice", new VoiceBuilder("OneVoice"));
    }
    
    private String getLyrics(int count) {
        String s = "";
        for(int i =0; i < this.lyrics.size(); i++) {
            if (count == i) {
                s += "*" + this.lyrics.get(count) + "*";
            }
            else if(this.lyrics.get(i).equals("_")){
                continue; //skip
            }
            else {
                s += this.lyrics.get(i);
            }
        }
        return s;
    }
    
    /** 
     * @return lyric at lyricsCounter if there are lyrics and increment counter.
     *         if no lyrics is present, returns "-1"
     */
    public String getLyricOnCount() {
        System.out.println("Counter " + lyricsCounter);
        try {
            if(lyrics.isEmpty()) {
                return "No Lyrics";
            }
            if (lyrics.get(lyricsCounter).equals(" ")) { //won't work if multiple spaces in the lyrics
                lyricsCounter++;
            }
            if (lyrics.get(lyricsCounter).equals("")) {
                lyricsCounter++;
                return getLyrics(-1);
            }
            if (lyrics.get(lyricsCounter).equals("_")) {
                int dummyCount = lyricsCounter - 1;
                lyricsCounter++;
                while (true) {
                    if (lyrics.get(dummyCount).equals("_")) {dummyCount = dummyCount -1;}
                    else {return getLyrics(dummyCount);}
                }
            }
            
            return getLyrics(lyricsCounter++);
        }
        catch(IndexOutOfBoundsException exp) {
            return "\n";
        }
        
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
     * Adds Tuplet to a Bar
     * @param tuplet to add to Bar
     */
    public void addTuplet(Tuplet tuplet) {
        this.barNotes.add(tuplet);

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
     * @param type of the accidental
     */
    public void addAccidental(char c,String type) {
        this.accidentals.put(c, type);
        
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
        if (!barNotes.isEmpty()) {
            Bar bar = new Bar(this.barNotes);
            this.getCurrentVoiceBuilder().addBar(bar); // add bar to currentVoiceBuilder
        }

        
        this.barNotes = new ArrayList<Music>();
        this.chordNotes = new ArrayList<Note>();
        this.tupletNotes = new ArrayList<Music>();
        this.accidentals = new HashMap<Character,String>();
        
    }

    /**
     * Apply key accidental to notes 
     * @param pitchChar the note
     * @param keyAccidental the key accidental for the music
     * @return new pitch with applied accidental
     */
    public Pitch applyKeyAccidental(Character pitchChar, String keyAccidental) {
        Pitch pitch = new Pitch(pitchChar);
        List<String> accidentalList = this.KEY_ACCIDENTALS.get(keyAccidental);
        for(String accidental: accidentalList) {
            if(accidental.indexOf(pitchChar)!=-1) {
                if(accidental.contains("#")) {
                    pitch = pitch.transpose(1);
                    
                }
                else if(accidental.contains("b")) {
                    pitch = pitch.transpose(-1);
                }
            }
        }
        return pitch;
            
    }

    /**
     * Get the size of the notes in a Bar
     * @return the size
     */
    public int getBarNotesSize() {
        return this.barNotes.size();
    }


    /**
     * Set the duration of the Tuplet
     * @param duration of the tuplet
     */
    public void setTupletDuration(double duration) {
       this.tupletDuration = duration;
        
    }
    
    /**
     * Get the tuplet duration
     * @return duration of the tuplet
     */
    public double getTupletDuration() {
        return this.tupletDuration;         
     }

    /**
     * Set the lyrics 
     * @param lyrics to set
     */
    public void setLyrics(List<String> lyrics) {
        this.lyrics = new ArrayList<String>(lyrics);
        
    }
    
    /**
     * Get the lyrics
     * @return the lyrics
     */
    public List<String> getLyrics() {
        return this.lyrics;
        
    }
    
    /**
     * Get the singer
     * @return the singer
     */
    public String getSinger() {
        if(this.currentSinger.length()==0) {
            return "OneVoice";
        }
        return this.currentSinger;
    }

    /**
     * Set the singer
     * @param singer to set
     */
    public void setSinger(String singer) {
        this.currentSinger = singer;
    }

    /**
     * Check to see if we are parsing music
     * @return true or false
     */
    public boolean inMusic() {
        return this.inMusicParsing;
    }

    /**
     * Set true if we are in music
     * @param b true if in Music, false otherwise
     */
    public void setInMusic(boolean b) {
        this.inMusicParsing = b;
        
    }

}

