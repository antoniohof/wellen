package wellen.examples_ext;

import processing.core.PApplet;
import wellen.Beat;
import wellen.Note;
import wellen.Scale;
import wellen.Sequencer;
import wellen.Tone;

/**
 * this example demonstrates how to use `Sequencer` to repeatedly play a predefined pattern of notes.
 */
public class ExampleTechnique01Sequencer extends PApplet {

    private static final int OFF = -1;

    private final Sequencer<Integer> mSequence = new Sequencer<Integer>(
            0, OFF, 12, OFF,
            0, OFF, 12, OFF,
            0, OFF, 12, OFF,
            0, OFF, 12, OFF,
            3, 3, 15, 15,
            3, 3, 15, 15,
            5, 5, 17, 17,
            5, 5, 17, 17
    );

    public void settings() {
        size(640, 480);
    }

    public void setup() {
        Beat.start(this, 120 * 4);
    }

    public void draw() {
        background(255);
        noStroke();
        fill(0);
        if (mSequence.get_current() != OFF) {
            float mNote = (mSequence.get_current() - 18) / 36.0f + 0.1f;
            ellipse(width * 0.5f, height * 0.5f, width * mNote, width * mNote);
        }
    }

    public void beat(int pBeat) {
        int mStep = mSequence.step();
        if (mStep != OFF) {
            int mNote = Scale.get_note(Scale.HALF_TONE, Note.NOTE_C4, mStep);
            Tone.note_on(mNote, 100);
        } else {
            Tone.note_off();
        }
    }

    public static void main(String[] args) {
        PApplet.main(ExampleTechnique01Sequencer.class.getName());
    }
}
