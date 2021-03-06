# wellen

*wellen* is a framework for exploring and teaching generative music making and algorithmic compositions. it facilitates simple ways of playing musical notes, facilitates easy access to low-level digital signal processing (DSP) and supplies rhythm and timing as well as some *standard* muscial mechanics. the library acts as an adapter to various sound in- and outputs like MIDI, OSC, or digital/analog audio. the library is hosted on github [wellen](https://github.com/dennisppaul/wellen).

## installation

the library can be installed as a [Processing library](https://processing.org/reference/libraries/) by unpacking the `wellen.zip` archive in the Processing library folder. a step-by-step introduction to the library can be found under `examples` and extended applications of the libray can be found in `examples_ext` and `applications`.

### dependencies

*wellen* makes use of the [oscP5](http://sojamo.de/code/) library to communicate over network with OSC ( see e.g `ExampleExternal04OSCToneEngine` ). the library can be installed via the processing library installer.

## concepts

watch the screencast series [Wellen](https://www.youtube.com/playlist?list=PLXJNr6N-Bu4NzkP4UJ5m-9721MdaZ6v-q) as an introduction by coding examples to the *wellen* library.

below is a text-based explanation of the core concepts of the *wellen* library:

### `Tone`

muscial notes can be played with a single call to `Tone.note_on(int, int)` and ended with `Tone.note_off(int)` ( see `ExampleBasics01Notes` ). each node is characterized by two parameters `pitch` and `velocity`. the value range conforms to MIDI standards.

by default a simple software-based synthesizer is used as a *tone engine* to produce the sound. however, there are quite a lot of options ( i.e an oscillator with different waveshapes, a low-pass filter (LPF), an attack-decay-sustain-release envelope (ADSR), and two low-frequency oscillators (LFOs) one for frequency and one for amplitude modulation ) to change the sound characteristics ( see `ExampleBasics05Instruments` + `ExampleInstruments01ADSR` ff. ). 

note that the default *tone engine* is monophonic i.e a single *instrument* can play only one note at a time. however, there are 16 instruments available which can be combined into a polyphonic setup with 16 voices.

although `Tone` is designed to play musical notes ( arranged in half-tone steps ) trigger by the `note_on`+`note_off` paradigm, it can also be used to controll the frequency and amplitude of the generated sounds directly ( see `ExampleInstruments03FrequencyAndAmplitude` ).

*wellen* comes with mechanisms to send messages to other applications or machines via MIDI ( see `ExampleBasics06MIDI` ) or OSC ( see `ExampleInstruments07OSCToneEngine` ). likewise *wellen* can also receive events from other applications or machines with `EventReceiverMIDI` via MIDI and `EventReceiverOSC` via OSC ( see `ExampleExternal01ReceiveMIDIandOSC` ).

### `DSP`

*wellen* facilitates a mechanism for digital ( audio ) signal processing (DSP).

in the simplest setup the method `DSP.start(Object)` starts the signal processing pipeline which then continuously calls the method `audioblock(float[])`. the `float[]` array must be filled with samples that are then played back by the underlying audio infrastructure.

see `ExampleBasics04DSP` for a simple implementation of a *sine wave oscillator* as well as `ExampleDSP03Echo` for an implementations of slightly more advanced concept in DSP.

additionally DSP can also be started with different parameter sets to either run with stereo output ( see `ExampleDSP01StereoOutput` ), mono in- and output ( see `ExampleDSP02PassThrough` ) or stereo in- and output.

the default *tone engine* is designed to be optionally interfaced with `DSP`. this mechanism can e.g be used to apply an *effect* to played notes ( see `ExampleDSP09ToneEngineInternalWithDSP` ).

*wellen* comes with a series of handy classes to facilitate some fundamental DSP techniques. `Sampler` can be used to play back chunks of ( recorded ) memory at varying speed and direction ( see `ExampleDSP07Sampler` + `ExampleDSP10SampleRecorder` ). `Wavetable` is similar but designed to facilitate the emulation of oscillators with different wave shapes ( see `ExampleDSP05Wavetable` + `ExampleDSP06LFO` ). `ADSR` supplies an envelope to controll the amplitude of a signal over time. `LowPassFilter` allows the filtering of a signal with a resonance filter ( see `ExampleDSP04LPF` ). `Trigger` observes a continous signal and fires events whenever a rising or falling edge is detected; in conjunction with an oscillator it can be used to generate reoccuring events ( see `ExampleDSP08Trigger` ).

### `Beat`

*wellen* has a mechanism to trigger a continous beat. the method `Beat.start(Object, int)` starts a beat at a specified *beats per minute* (BPM) ( see `ExampleBasics03Beat` ). 

a beat can also be triggered by an external MIDI clock ( see `ExampleExternal02MIDIClock` ) to synchronize with other applications.

### other *muscial* techniques

with `Scale` values can be transformed into intervals based on musical scales ( see `ExampleBasics02Scales` ).

the `Sequencer` supplies a simple structure to facilitate the recording and recalling of note sequences ( see `ExampleTechnique01Sequencer` ). 

`Arpeggiator` works in a similar way but schedules a series of notes based on a predefined pattern and a base note.
