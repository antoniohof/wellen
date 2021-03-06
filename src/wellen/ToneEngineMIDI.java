package wellen;

import java.util.ArrayList;
import java.util.Timer;

import static processing.core.PApplet.constrain;

public class ToneEngineMIDI extends ToneEngine {

    // @TODO(add `InstrumentMIDI`)

    public static final int CC_MODULATION = 1;
    public final MidiOut mMidiOut;
    private final Timer mTimer;
    private int mLastPlayedNote = -1;
    private int mChannel;

    public ToneEngineMIDI(String pMidiOutputDeviceName) {
        mTimer = new Timer();
        mMidiOut = new MidiOut(get_proper_device_name(pMidiOutputDeviceName));
        prepareExitHandler();
    }

    public ToneEngineMIDI(int pMidiOutputDeviceID) {
        mTimer = new Timer();
        mMidiOut = new MidiOut(pMidiOutputDeviceID);
        prepareExitHandler();
    }

    public void note_on(int note, int velocity) {
        mMidiOut.sendNoteOn(mChannel, note, velocity);
        mLastPlayedNote = note;
    }

    public void note_off(int note) {
        mMidiOut.sendNoteOff(mChannel, note, 0);
        mLastPlayedNote = -1;
    }

    public void note_off() {
        note_off(mLastPlayedNote);
    }

    public void control_change(int pCC, int pValue) {
        mMidiOut.sendControllerChange(mChannel, pCC, pValue);
    }

    public void pitch_bend(int pValue) {
        final int mValue = constrain(pValue, 0, 16383);
        final int LSB_MASK = 0b00000001111111;
        final int MSB_MASK = 0b11111110000000;
        final int msb = (mValue & MSB_MASK) / 128;
        final int lsb = mValue & LSB_MASK;
        mMidiOut.sendPitchBend(mChannel, lsb, msb);
    }

    public boolean is_playing() {
        return (mLastPlayedNote != -1);
    }

    public Instrument instrument(int pInstrumentID) {
        mChannel = pInstrumentID;
        return null;
    }

    public Instrument instrument() {
        return null;
    }

    public ArrayList<? extends Instrument> instruments() {
        return null;
    }

    public void replace_instrument(Instrument pInstrument) {
    }

    private void prepareExitHandler() {
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                for (int i = 0; i < 127; i++) {
                    mMidiOut.sendNoteOff(mChannel, i, 0);
                    mLastPlayedNote = -1;
                }
                mMidiOut.close();
            }
        }));
    }

    public static String get_proper_device_name(String pMidiOutputDeviceName) {
        String[] mDevices = MidiOut.availableOutputs();
        for (String mDevice : mDevices) {
            if (mDevice.startsWith(pMidiOutputDeviceName)) {
                return mDevice;
            }
        }
        System.err.println("+++ @" + ToneEngineMIDI.class.getSimpleName() + " / couldn't find MIDI device: " + pMidiOutputDeviceName);
        return null;
    }
}
