package wellen.examples_ext;

import processing.core.PApplet;
import wellen.Tone;

/**
 * this example demonstrates how to use the OSC tone engine to send OSC commands. the defined OSC address patterns can
 * be found in `ToneEngineOSC`
 */
public class ExampleExternal04OSCToneEngine extends PApplet {

    private int mNote;

    public void settings() {
        size(640, 480);
    }

    public void setup() {
        Tone.start("osc", "127.0.0.1", "7001");
    }

    public void draw() {
        background(Tone.is_playing() ? 255 : 0);
    }

    public void mousePressed() {
        Tone.instrument(mouseX < width / 2.0 ? 0 : 1);
        mNote = 45 + (int) random(0, 12);
        Tone.note_on(mNote, 127);
    }

    public void mouseReleased() {
        Tone.note_off(mNote);
    }

    public static void main(String[] args) {
        PApplet.main(ExampleExternal04OSCToneEngine.class.getName());
    }
}
