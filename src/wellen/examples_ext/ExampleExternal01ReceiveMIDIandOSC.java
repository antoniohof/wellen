package wellen.examples_ext;

import processing.core.PApplet;
import wellen.EventReceiverMIDI;
import wellen.EventReceiverOSC;
import wellen.Wellen;

/**
 * this example demonstrates how to receive MIDI and OSC events in the `event_receive` method.
 * <p>
 * note that two arguments are received. the first specifies the event type ( see `Event` for a list of events ) and the
 * second contains the payload ( i.e the actual event related data ).
 * <p>
 * note that each event type implies a number of data points e.g `EVENT_NOTE_ON` always has 3 data points: `CHANNEL`,
 * `NOTE` and `VELOCITY` ( see `ExampleEvent03MIDIExternalKeyboard` for an example of an implementation ).
 */
public class ExampleExternal01ReceiveMIDIandOSC extends PApplet {

    //@TODO(parse received events and play them as musical notes)
    private String mEventReceived = "EVENTS\n---\n";
    private int mEventCounter = 2;

    public void settings() {
        size(640, 480);
    }

    public void setup() {
        textFont(createFont("Roboto Mono", 11));
        EventReceiverOSC.start(this);
        Wellen.dumpMidiInputDevices();
        EventReceiverMIDI.start(this, "Bus 1");
    }

    public void draw() {
        background(255);
        noStroke();
        fill(0);
        text(mEventReceived, 11, 22);
    }

    public void event_receive(int pEvent, float[] pData) {
        String mEventReceivedStr = "[" + nf(mEventCounter, 2) + "] ";
        mEventReceivedStr += "EVENT ( ";
        mEventReceivedStr += "TYPE: " + pEvent;
        mEventReceivedStr += " DATA: ";
        for (float pDatum : pData) {
            mEventReceivedStr += pDatum;
            mEventReceivedStr += " ";
        }
        mEventReceivedStr += ")";
        mEventReceived += mEventReceivedStr + "\n";
        println(mEventReceivedStr);

        mEventCounter++;
        if (mEventCounter > 23) {
            mEventCounter = 0;
            mEventReceived = "";
        }
    }

    public static void main(String[] args) {
        PApplet.main(ExampleExternal01ReceiveMIDIandOSC.class.getName());
    }
}
