import wellen.*; 
import netP5.*; 
import oscP5.*; 

Slider mSliderAttack;

Slider mSliderDecay;

Slider mSliderSustain;

Slider mSliderRelease;

int mNote;

void settings() {
    size(640, 480);
}

void setup() {
    hint(DISABLE_KEY_REPEAT);
    mSliderAttack = new Slider();
    mSliderDecay = new Slider();
    mSliderSustain = new Slider();
    mSliderSustain.horizontal = false;
    mSliderRelease = new Slider();
    updateADSR();
    println(ADSR.ADSR_DIAGRAM);
}

void draw() {
    background(Tone.is_playing() ? 0 : 255);
    final float mXOffset = (width - Slider.size * 4) * 0.5f;
    final float mYOffset = height * 0.5f;
    translate(mXOffset, mYOffset);
    scale(1, -1);
    updateDiagram(mXOffset, mYOffset);
    drawDiagram();
}

void mousePressed() {
    mNote = Scale.get_note(Scale.MAJOR_CHORD_7, Note.NOTE_A2, (int) random(0, 10));
    Tone.note_on(mNote, 100);
}

void mouseReleased() {
    Tone.note_off();
}

void mouseDragged() {
    updateADSR();
}

void drawDiagram() {
    int mColor = Tone.is_playing() ? 255 : 0;
    stroke(mColor, 15);
    line(0, 0, Slider.size * 4, 0);
    /* GUI */
    noStroke();
    fill(mColor);
    ellipse(0, 0, Slider.radius, Slider.radius);
    mSliderAttack.draw(g, mColor);
    mSliderDecay.draw(g, mColor);
    mSliderSustain.draw(g, mColor);
    mSliderRelease.draw(g, mColor);
    /* envelope */
    strokeWeight(3);
    stroke(mColor);
    line(0, 0,
         mSliderAttack.current_position_x(),
         mSliderAttack.current_position_y());
    line(mSliderAttack.current_position_x(),
         mSliderAttack.current_position_y(),
         mSliderDecay.current_position_x(),
         mSliderDecay.current_position_y());
    line(mSliderDecay.current_position_x(),
         mSliderDecay.current_position_y(),
         mSliderSustain.current_position_x(),
         mSliderSustain.current_position_y());
    line(mSliderSustain.current_position_x(),
         mSliderSustain.current_position_y(),
         mSliderRelease.current_position_x(),
         mSliderRelease.current_position_y());
    strokeWeight(1);
}

void updateDiagram(float mXOffset, float mYOffset) {
    float mX = mouseX - mXOffset;
    float mY = -mouseY + mYOffset;
    mSliderAttack.update(mX, mY, mousePressed);
    mSliderAttack.x = 0;
    mSliderAttack.y = Slider.size;
    mSliderDecay.update(mX, mY, mousePressed);
    mSliderDecay.x = mSliderAttack.current_position_x();
    mSliderDecay.y = mSliderSustain.current_position_y();
    mSliderSustain.update(mX, mY, mousePressed);
    mSliderSustain.x = mSliderDecay.current_position_x() + Slider.size;
    mSliderSustain.y = 0;
    mSliderRelease.update(mX, mY, mousePressed);
    mSliderRelease.x = mSliderSustain.x;
    mSliderRelease.y = 0;
}

void updateADSR() {
    Tone.instrument().set_attack(mSliderAttack.value);
    Tone.instrument().set_decay(mSliderDecay.value);
    Tone.instrument().set_sustain(mSliderSustain.value);
    Tone.instrument().set_release(mSliderRelease.value);
}

static class Slider {
    
static final float size = 120;
    
static final float radius = 8;
    float x;
    float y;
    float value;
    boolean horizontal;
    boolean hoover;
    boolean drag;
    
Slider() {
        x = 0;
        y = 0;
        value = 0.5f;
        horizontal = true;
        hoover = false;
        drag = false;
    }
    void draw(PGraphics g, int pColor) {
        g.stroke(pColor);
        g.line(x, y, x + (horizontal ? size : 0), y + (horizontal ? 0 : size));
        final float mEdgeDiameter = 5;
        g.noStroke();
        g.fill(pColor);
        g.ellipse(x, y, mEdgeDiameter, mEdgeDiameter);
        g.ellipse(x + (horizontal ? size : 0), y + (horizontal ? 0 : size), mEdgeDiameter, mEdgeDiameter);
        g.noStroke();
        g.fill(pColor);
        g.ellipse(current_position_x(), current_position_y(), radius * (hoover ? 2 : 1), radius * (hoover ? 2 : 1));
    }
    void update_value(float pX, float pY) {
        value = (horizontal ? (pX - x) : (pY - y)) / size;
        value = min(1, max(0, value));
    }
    boolean hit(float pX, float pY) {
        final float mDistance = PVector.dist(new PVector().set(pX, pY),
                                             new PVector().set(current_position_x(), current_position_y()));
        return mDistance < radius * 2;
    }
    float current_position_x() {
        return horizontal ? x + size * value : x;
    }
    float current_position_y() {
        return horizontal ? y : y + size * value;
    }
    void update(float mouse_x, float mouse_y, boolean mouse_pressed) {
        if (hit(mouse_x, mouse_y)) {
            if (mouse_pressed) {
                update_value(mouse_x, mouse_y);
                drag = true;
            } else {
                drag = false;
            }
            hoover = true;
        } else {
            hoover = false;
            drag = false;
        }
    }
}
