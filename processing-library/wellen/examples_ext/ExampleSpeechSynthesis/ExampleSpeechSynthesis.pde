import wellen.*; 
import netP5.*; 
import oscP5.*; 

SpeechSynthesis mSpeech;

String[] mWords;

int mBeatCount;

void settings() {
    size(640, 480);
}

void setup() {
    String mText = "I know not by what power I am made bold, Nor how it may concern my modesty, In such a " +
                   "presence here to plead" +
                   " my thoughts; But I beseech your grace that I may know The worst that may " + "befall me in " +
                   "this case, If I refuse to " +
                   "wed Demetrius.";
    mWords = split(mText, ' ');
    printArray(SpeechSynthesis.list());
    mSpeech = new SpeechSynthesis();
    mSpeech.blocking(false);
    Beat mBeat = new Beat(this, 140);
}

void draw() {
    background(255);
    noStroke();
    fill(0);
    float mScale = (mBeatCount % 32) * 0.025f + 0.25f;
    ellipse(width * 0.5f, height * 0.5f, width * mScale, width * mScale);
}

void beat(int pBeatCount) {
    mBeatCount = pBeatCount;
    int mWordIndex = pBeatCount % mWords.length;
    mSpeech.say("Daniel", mWords[mWordIndex]);
}
