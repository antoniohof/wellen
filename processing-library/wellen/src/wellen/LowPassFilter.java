package wellen;

/**
 * Low Pass Filter ( Moog Ladder )
 * <p>
 * Ported from soundpipe
 * <p>
 * Original author(s) : Victor Lazzarini, John ffitch (fast tanh), Bob Moog
 */
public class LowPassFilter implements DSPNodeProcess {

    private final int mSamplingRate;
    private final float mIstor;
    private final float[] mDelay = new float[6];
    private final float[] mTanhstg = new float[3];
    private float mResonance;
    private float mCutoffFrequency;
    private float mOldFreq;
    private float mOldRes;
    private float mOldAcr;
    private float mOldTune;

    public LowPassFilter(int sample_rate) {
        mSamplingRate = sample_rate;
        mIstor = 0.0f;
        mResonance = 0.4f;
        mCutoffFrequency = 1000.0f;

        for (int i = 0; i < 6; i++) {
            mDelay[i] = 0.0f;
            mTanhstg[i % 3] = 0.0f;
        }

        mOldFreq = 0.0f;
        mOldRes = -1.0f;
    }

    @Override
    public float process(float pSignal) {
        float freq = mCutoffFrequency;
        float res = mResonance;
        float res4;
        float[] stg = new float[4];
        float acr, tune;
        float THERMAL = 0.000025f;

        if (res < 0) {
            res = 0;
        }

        if (mOldFreq != freq || mOldRes != res) {
            float f, fc, fc2, fc3, fcr;
            mOldFreq = freq;
            fc = (freq / mSamplingRate);
            f = 0.5f * fc;
            fc2 = fc * fc;
            fc3 = fc2 * fc2;

            fcr = 1.8730f * fc3 + 0.4955f * fc2 - 0.6490f * fc + 0.9988f;
            acr = -3.9364f * fc2 + 1.8409f * fc + 0.9968f;
            tune = (float) ((1.0f - Math.exp(-((2 * Math.PI) * f * fcr))) / THERMAL);

            mOldRes = res;
            mOldAcr = acr;
            mOldTune = tune;
        } else {
//            res = mOldRes;
            acr = mOldAcr;
            tune = mOldTune;
        }

        res4 = 4.0f * res * acr;

        for (int j = 0; j < 2; j++) {
            pSignal -= res4 * mDelay[5];
            mDelay[0] = stg[0]
                    = mDelay[0] + tune * (my_tanh(pSignal * THERMAL) - mTanhstg[0]);
            for (int k = 1; k < 4; k++) {
                pSignal = stg[k - 1];
                stg[k] = mDelay[k]
                        + tune
                        * ((mTanhstg[k - 1] = my_tanh(pSignal * THERMAL))
                        - (k != 3 ? mTanhstg[k]
                        : my_tanh(mDelay[k] * THERMAL)));
                mDelay[k] = stg[k];
            }
            mDelay[5] = (stg[3] + mDelay[4]) * 0.5f;
            mDelay[4] = stg[3];
        }
        return mDelay[5];
    }

    public float get_frequency() {
        return mCutoffFrequency;
    }

    /**
     * @param pCutoffFrequency cutoff frequency in Hz
     */
    public void set_frequency(float pCutoffFrequency) {
        mCutoffFrequency = pCutoffFrequency;
    }

    public float get_resonance() {
        return mResonance;
    }

    /**
     * @param pResonance resonance factor [0.0, 1.0] ( becomes instable close to 1.0 )
     */
    public void set_resonance(float pResonance) {
        mResonance = pResonance;
    }

    private float my_tanh(float x) {
        int sign = 1;
        if (x < 0) {
            sign = -1;
            x = -x;
            return x * sign;
        } else if (x >= 4.0f) {
            return sign;
        } else if (x < 0.5f) {
            return x * sign;
        }
        return sign * (float) Math.tanh(x);
    }
}
