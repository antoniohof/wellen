package wellen.examples_ext;

import processing.core.PApplet;
import wellen.DSP;
import wellen.Wellen;

/**
 * this example demonstrates how to implement a basic echo effect in DSP.
 */
public class ExampleDSP03Echo extends PApplet {

    float[] mDelayBuffer = new float[4096];
    int mDelayID = 0;
    int mDelayOffset = 512;
    float mDecay = 0.9f;
    float mMix = 0.25f;

    public void settings() {
        size(640, 480);
    }

    public void setup() {
        Wellen.dumpAudioInputAndOutputDevices();
        DSP.start(this, 1, 1);
    }

    public void draw() {
        background(255);
        stroke(0);
        final int mBufferSize = DSP.get_buffer_size();
        if (DSP.get_buffer() != null) {
            for (int i = 0; i < mBufferSize; i++) {
                final float x = map(i, 0, mBufferSize, 0, width);
                point(x, map(DSP.get_buffer()[i], -1, 1, 0, height));
            }
        }
    }

    public void mouseMoved() {
        mMix = map(mouseX, 0, width, 0.2f, 0.95f);
        mDelayOffset = (int) map(mouseY, 0, height, 1, mDelayBuffer.length);
    }

    public void audioblock(float[] pOutputSamples, float[] pInputSamples) {
        for (int i = 0; i < pInputSamples.length; i++) {
            mDelayID++;
            mDelayID %= mDelayBuffer.length;
            int mOffsetID = mDelayID + mDelayOffset;
            mOffsetID %= mDelayBuffer.length;
            pOutputSamples[i] = pInputSamples[i] * (1.0f - mMix) + mDelayBuffer[mOffsetID] * mMix;
            mDelayBuffer[mDelayID] = pOutputSamples[i] * mDecay;
        }
    }

    public static void main(String[] args) {
        PApplet.main(ExampleDSP03Echo.class.getName());
    }
}
