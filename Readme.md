android Ray Picking gluUnProject sample
=======================================

This is an android port of the Ray Picking gluUnProject sample described in the excellent [Blog Post](http://blog.nova-box.com/2010/05/iphone-ray-picking-glunproject-sample.html) and sample code by [Nova Box](http://www.nova-box.com/).

If you would like to understand the concept behind recognizing touch events on OpenGL objects using Ray Picking I can only recommend the mentioned [Blog Post](http://blog.nova-box.com/2010/05/iphone-ray-picking-glunproject-sample.html).

This code has been tested on Android 4.0.3 and 2.3.7 and is provided without any warranties as is.

For convenience reasons I included a Fragment hosting the GLSurfaceView so you need to include the Support Library (android-support-v4.jar).


Known Bugs and Limitations:
===========================
- Portrait Mode - I have not yet figured out what causes the Portrait mode to fail so the Activity is forced to Landscape


Working with the Sample:
========================
most likely you would like to add other objects so feel free to change the code. a good starting point to add custom objects are the initObjects and drawModels Methods in the RayPickerRenderer class.
