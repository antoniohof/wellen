package wellen;

import netP5.NetAddress;
import oscP5.OscMessage;
import oscP5.OscP5;

import java.util.ArrayList;
import java.util.Timer;

public class ToneEngineOSC extends ToneEngine {

    // @TODO(add `InstrumentOSC`)

    public static final String OSC_ADDR_PATTERN_NOTE_ON = "/note_on";
    public static final String OSC_ADDR_PATTERN_NOTE_OFF = "/note_off";
    public static final String OSC_ADDR_PATTERN_CONTROLCHANGE = "/controlchange";
    public static final String OSC_ADDR_PATTERN_PITCHBAND = "/pitchbend";
    public static final String OSC_ADDR_PATTERN_PROGRAMCHANGE = "/programchange";
    private static final int DEFAULT_TRANSMIT_PORT = 7001;
    private final OscP5 mOscP5;
    private final NetAddress mRemoteLocation;
    private final Timer mTimer;
    private int mChannel;
    private boolean mIsPlaying = false;

    public ToneEngineOSC(String pTransmitIP, int pPortTransmit) {
        final int pPortReceive = 0; // @TODO do we need to supply a *listening port* although it is never used?
        mOscP5 = new OscP5(this, pPortReceive);
        mRemoteLocation = new NetAddress(pTransmitIP, pPortTransmit);
        mTimer = new Timer();
    }

    public ToneEngineOSC(String pHostIP) {
        this(pHostIP, DEFAULT_TRANSMIT_PORT);
    }

    public ToneEngineOSC() {
        this("127.0.0.1");
    }

    public void note_on(int note, int velocity) {
        mIsPlaying = true;
        OscMessage m = new OscMessage(OSC_ADDR_PATTERN_NOTE_ON);
        m.add(mChannel);
        m.add(note);
        m.add(velocity);
        mOscP5.send(m, mRemoteLocation);
    }

    public void note_off(int note) {
        mIsPlaying = false;
        OscMessage m = new OscMessage(OSC_ADDR_PATTERN_NOTE_OFF);
        m.add(mChannel);
        m.add(note);
        mOscP5.send(m, mRemoteLocation);
    }

    public void note_off() {
        note_off(-1);
    }

    public void control_change(int pCC, int pValue) {
        OscMessage m = new OscMessage(OSC_ADDR_PATTERN_CONTROLCHANGE);
        m.add(mChannel);
        m.add(pCC);
        m.add(pValue);
        mOscP5.send(m, mRemoteLocation);
    }

    public void pitch_bend(int pValue) {
        OscMessage m = new OscMessage(OSC_ADDR_PATTERN_PITCHBAND);
        m.add(mChannel);
        m.add(pValue);
        mOscP5.send(m, mRemoteLocation);
    }

    public boolean is_playing() {
        return mIsPlaying;
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

    @Override
    public void replace_instrument(Instrument pInstrument) {

    }
}
