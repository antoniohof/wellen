package welle.examples_ext;

import processing.core.PApplet;
import welle.Tone;
import welle.Welle;

/**
 * this example demonstrates how to use a *Low-Pass Filter* (LPF) on a sawtooth oscillator in DSP.
 * <p>
 * note that this functionality is not implemented for MIDI and OSC.
 */
public class ExampleInstruments05LPF extends PApplet {

    public void settings() {
        size(640, 480);
    }

    public void setup() {
        Tone.instrument().enable_LPF(true);
        Tone.instrument().set_oscillator_type(Welle.OSC_SAWTOOTH);
    }

    public void draw() {
        background(255);
        fill(0);
        ellipse(width * 0.5f, height * 0.5f, Tone.is_playing() ? 100 : 5, Tone.is_playing() ? 100 : 5);
    }

    public void mousePressed() {
        int mNote = 45 + (int) random(0, 12);
        Tone.note_on(mNote, 100);
    }

    public void mouseReleased() {
        Tone.note_off();
    }

    public void mouseDragged() {
        Tone.instrument().set_filter_resonance(map(mouseY, 0, height, 0.0f, 0.95f));
        Tone.instrument().set_filter_frequency(map(mouseX, 0, width, 0.0f, 2000.0f));
    }

    public static void main(String[] args) {
        PApplet.main(ExampleInstruments05LPF.class.getName());
    }
}
