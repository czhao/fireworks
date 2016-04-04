### Inspiration
Firework were invented in ancient China in the 12th century. When I was wondering how to generate an amazing firework show on the mobile device, [canvas-fireworks](https://github.com/Automattic/canvas-fireworks) showcases the range of possibilities with HTML5 canvas. Why not creating an experiement with Android?

### Physics behind the scene
The key to simulate fireworks is to establish the 3D space behind the explosion. While simulating the physics in 3D, I map the 3D space into 2D canvas with corresponding normalized gravity, air resistance and wind effect. Most of physical explaination can be found from [this paper](http://theochem.mercer.edu/pipermail/csc415/attachments/20110507/6aab91e2/fireworks_final-0001.pdf).

### Technology in use
Since this is an Android experiment, I aim to achieve the reality via "Android approach", without any third party graphic library or openGL knowledge.

- support Android 4.4+
- surface & canvas drawing

### Project Structure

- an Android Studio project 



