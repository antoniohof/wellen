package de.hfkbremen.ton;

import controlP5.ControlP5;
import processing.core.PApplet;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static de.hfkbremen.ton.Ton.TONE_ENGINE_JSYN;
import static de.hfkbremen.ton.Ton.TONE_ENGINE_MIDI;
import static de.hfkbremen.ton.Ton.TONE_ENGINE_OSC;
import static de.hfkbremen.ton.Ton.TONE_ENGINE_SOFTWARE;

public abstract class ToneEngine {

    // @TODO(add javadoc to abstract classes)

    public static final int INSTRUMENT_EMPTY = 0;
    public static final int INSTRUMENT_WITH_OSCILLATOR = 1;
    public static final int INSTRUMENT_WITH_OSCILLATOR_ADSR = 2;
    public static final int INSTRUMENT_WITH_OSCILLATOR_ADSR_FILTER_LFO = 3;

    public static final int NUMBERS_OF_INSTRUMENTS = 16;
    public static final String INSTRUMENT_STR = "instrument";
    public static final int GUI_ATTACK = 0;
    public static final int GUI_DECAY = 1;
    public static final int GUI_SUSTAIN = 2;
    public static final int GUI_RELEASE = 3;
    public static final int GUI_OSC = 4;
    public static final int GUI_LFO_AMP = 5;
    public static final int GUI_LFO_FREQ = 6;
    public static final int GUI_FILTER_Q = 7;
    public static final int GUI_FILTER_FREQ = 8;
    private static final int GUI_NUMBER_OF_ELEMENTS = 9;
    private static final String[] INSTRUMENT_FIELDS = new String[GUI_NUMBER_OF_ELEMENTS];

    static {
        INSTRUMENT_FIELDS[GUI_ATTACK] = "attack";
        INSTRUMENT_FIELDS[GUI_DECAY] = "decay";
        INSTRUMENT_FIELDS[GUI_SUSTAIN] = "sustain";
        INSTRUMENT_FIELDS[GUI_RELEASE] = "release";
        INSTRUMENT_FIELDS[GUI_OSC] = "osc_type";
        INSTRUMENT_FIELDS[GUI_LFO_AMP] = "lfo_amp";
        INSTRUMENT_FIELDS[GUI_LFO_FREQ] = "lfo_freq";
        INSTRUMENT_FIELDS[GUI_FILTER_Q] = "filter_q";
        INSTRUMENT_FIELDS[GUI_FILTER_FREQ] = "filter_freq";
    }

    private final Timer mTimer;

    ToneEngine() {
        mTimer = new Timer();
    }

    public static ToneEngine createEngine() {
        return new ToneEngineSoftware();
    }

    public static ToneEngine createEngine(String... pName) {
        if (pName.length > 0) {
            if (pName[0].equalsIgnoreCase(TONE_ENGINE_SOFTWARE)) {
                return new ToneEngineSoftware();
            } else if (pName[0].equalsIgnoreCase("minim")) {
                return new ToneEngineMinim();
            } else if (pName[0].equalsIgnoreCase("jsyn-minimal")) {
                return new ToneEngineJSyn(INSTRUMENT_WITH_OSCILLATOR);
            } else if (pName[0].equalsIgnoreCase(TONE_ENGINE_JSYN)) {
                if (pName.length == 1) {
                    return new ToneEngineJSyn(INSTRUMENT_WITH_OSCILLATOR_ADSR);
                } else if (pName.length == 3) {
                    /* specify output device */
                    return new ToneEngineJSyn(INSTRUMENT_WITH_OSCILLATOR_ADSR,
                                              Integer.parseInt(pName[1]),
                                              Integer.parseInt(pName[2]));
                }
            } else if (pName[0].equalsIgnoreCase("jsyn-filter+lfo")) {
                return new ToneEngineJSyn(INSTRUMENT_WITH_OSCILLATOR_ADSR_FILTER_LFO);
            } else if (pName[0].equalsIgnoreCase(TONE_ENGINE_MIDI) && pName.length == 2) {
                return new ToneEngineMIDI(pName[1]);
            } else if (pName[0].equalsIgnoreCase(TONE_ENGINE_OSC) && pName.length >= 2) {
                if (pName.length == 2) {
                    return new ToneEngineOSC(pName[1]);
                } else if (pName.length == 3) {
                    try {
                        final String mIPTransmit = pName[1];
                        final int mPortTransmit = Integer.parseInt(pName[2]);
                        return new ToneEngineOSC(mIPTransmit, mPortTransmit);
                    } catch (NumberFormatException e) {
                        System.err.println("+++ could not parse ports");
                    }
                }
                return new ToneEngineOSC();
            }
            System.err.println("+++ could not find specified tone engine: " + pName[0]);
            System.err.println("+++ hint: check number of parameters");
        }
        return createEngine();
    }

    public static ControlP5 createInstrumentsGUI(PApplet p, ToneEngine pToneEngine) {
        return createInstrumentsGUI(p, pToneEngine, NUMBERS_OF_INSTRUMENTS);
    }

    public static ControlP5 createInstrumentsGUI(PApplet p, ToneEngine pToneEngine, int... mInstruments) {
        ControlP5 cp5 = new ControlP5(p);
        if (pToneEngine instanceof ToneEngineJSyn || pToneEngine instanceof ToneEngineMinim) {
            for (int i = 0; i < mInstruments.length; i++) {
                final int mID = mInstruments[i];
                final String mInstrumentStr = INSTRUMENT_STR + mID;
                final Instrument mInstrument = pToneEngine.instrument(mID);
                cp5.addControllersFor(mInstrumentStr, mInstrument);
                updateGUI(cp5, mInstrument);
                cp5.setPosition(10, 10 + i * 60, mInstrument);
            }
        }
        return cp5;
    }

    public static void updateGUI(ControlP5 cp5, final Instrument mInstrument, final int pField) {
        final String mInstrumentStr = INSTRUMENT_STR + mInstrument.ID() + "/" + INSTRUMENT_FIELDS[pField];
        if (cp5.get(mInstrumentStr) != null) {
            switch (pField) {
                case GUI_ATTACK:
                    cp5.get(mInstrumentStr).setValue(mInstrument.get_attack());
                    break;
                case GUI_DECAY:
                    cp5.get(mInstrumentStr).setValue(mInstrument.get_decay());
                    break;
                case GUI_SUSTAIN:
                    cp5.get(mInstrumentStr).setValue(mInstrument.get_sustain());
                    break;
                case GUI_RELEASE:
                    cp5.get(mInstrumentStr).setValue(mInstrument.get_release());
                    break;
                case GUI_OSC:
                    cp5.get(mInstrumentStr).setValue(mInstrument.get_osc_type());
                    break;
                case GUI_LFO_AMP:
                    cp5.get(mInstrumentStr).setValue(mInstrument.get_lfo_amp());
                    break;
                case GUI_LFO_FREQ:
                    cp5.get(mInstrumentStr).setValue(mInstrument.get_lfo_freq());
                    break;
                case GUI_FILTER_Q:
                    cp5.get(mInstrumentStr).setValue(mInstrument.get_filter_q());
                    break;
                case GUI_FILTER_FREQ:
                    cp5.get(mInstrumentStr).setValue(mInstrument.get_filter_freq());
                    break;
            }
        }
    }

    public static void updateGUI(ControlP5 cp5, final Instrument mInstrument) {
        for (int i = 0; i < GUI_NUMBER_OF_ELEMENTS; i++) {
            updateGUI(cp5, mInstrument, i);
        }
    }

    /**
     * play a note
     *
     * @param note     pitch of note ranging from 0 to 127
     * @param velocity volume of note ranging from 0 to 127
     * @param duration duration in seconds before the note is turned off again
     */
    public final void note_on(int note, int velocity, float duration) {
        TimerTask mTask = new NoteOffTask(note, instrument().ID());
        mTimer.schedule(mTask, (long) (duration * 1000));
        note_on(note, velocity);
    }

    /**
     * play a note
     *
     * @param note     pitch of note ranging from 0 to 127
     * @param velocity volume of note ranging from 0 to 127
     */
    public abstract void note_on(int note, int velocity);

    /**
     * turn off a note
     *
     * @param note pitch of note to turn off
     */
    public abstract void note_off(int note);

    /**
     * turns off the last played note.
     */
    public abstract void note_off();

    public abstract void control_change(int pCC, int pValue);

    public abstract void pitch_bend(int pValue);

    public abstract boolean isPlaying();

    public abstract Instrument instrument(int pInstrumentID);

    public abstract Instrument instrument();

    public abstract ArrayList<? extends Instrument> instruments();

    public abstract void replace_instrument(Instrument pInstrument);

    private class NoteOffTask extends TimerTask {

        final int note;
        final int instrument;

        public NoteOffTask(int pNote, int pInstrument) {
            note = pNote;
            instrument = pInstrument;
        }

        public void run() {
            int mCurrentInstrument = instrument().ID();
            instrument(instrument);
            note_off(note);
            instrument(mCurrentInstrument);
        }
    }
}
