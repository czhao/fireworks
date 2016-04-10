**This is an Android experiment submission**

# Inspiration
Firework were invented in ancient China in the 12th century. When I was wondering how to generate an amazing firework show on the mobile device, [canvas-fireworks](https://github.com/Automattic/canvas-fireworks) showcases the range of possibilities with HTML5 canvas. Why not creating an experiement with Android? 

Besides seeing the world through our eyes, we sense the world via sound as well. In this experiment, I would like to demo how a simple audio processing techniques can make the phone more useful. 

# How it works

The app utilizes the microphone and analyses the real-time audio input. It will build up an audio profile about the environment and use the profile to identify the boost of audio power (volume). Whenever the app detects the audio level beyond expected range, it will fire a firework into the sky. 

It recommends to start the app in a relatively quite environment and use earphone as audio output.  

# References

## Graphics
The key to simulate fireworks is to establish the 3D space behind the explosion. While simulating the physics in 3D, I map the 3D space into 2D canvas with corresponding normalized gravity, air resistance and wind effect. Most of physical explaination can be found from [Firework Simulation](http://theochem.mercer.edu/pipermail/csc415/attachments/20110507/6aab91e2/fireworks_final-0001.pdf) by Bell el.

## Audio
The app collects the audio in the form of normalized values and perform a simple analysis called 'Normalized Power Sequences'. It works in five steps
- Estimation of the signal power, for each consecutive non-overlapping block of audio sample
- Windowing of the obtained power sequence to consider only its more recent elements
- Normalization of the windowed power sequence
- Determination of the variance of the resulting normalizing sequence
- Application of a threshold on the variance for making the detection decsion

More information can be found via [Detection and Recognition of Impulsive Sound Signals](http://lpm.epfl.ch/webdav/site/lpm/users/175547/public/Sound_recognition.pdf) by Alain Dufaux. It is possible to apply more techniques discussed in the paper, i.e. use FFT to identify different sound pattern, but the complexity is beyond this experiment. I include the FFT implementation as a sub module as it is quite handy to use. More example about the FFT analysis can be found via [this repo](https://github.com/sommukhopadhyay/FFTBasedSpectrumAnalyzer).

The audio sample comes from [freesound.org](https://www.freesound.org/), a Creative Commons Licensed audio database. 

# Technology in use
Since this is an Android experiment, I aim to achieve the reality via "Android approach", without any third party graphic library or openGL knowledge.

- support Android 4.4+
- audio capture via microphone
- surface & canvas drawing

# Project Structure

- `app` an Android Studio project 
- `spectrum-analyse` include a FFT implementation for feature analysis





