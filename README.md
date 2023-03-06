# ImageModifier

## A user interface that allows predefined image modifications.


## ImageModifier 
The main flow control is stated in ImageModifier. No Argument constructor ImageModifier displays the frame. Initially user is asked to choose an image file from the present system through JFileChooser of swings. The file is then loaded through imageio and displayed in the window. User gets to choose from nine options provided on the bottom panel. By clicking the buttons event is generated and with ActionListener interface of java.awt.event package actionPerformed is implemented to handle the generated events.

#### Reset
Reset sets back the original image chose by user. The image is initially stored in an Image variable which always holds the original image. When Reset is clicked even when no filters are applied to the image there is no effect on the image.

#### NewImage
By clicking on NewImage button user can choose a new image discarding the present image and any filters applied on it. NewImage works in the same way as initial image choosing through JFileChooser.

#### GrayScale
GrayScale filter is subclass of RGBImageFilter also implementing PlugInFilter. It overrides filterRGB method to change incoming color values. It takes red, green and blue values and computes the brightness of the pixel using NTSC(National Television Standards Committee) color-to-brightness conversion factor. It then returns a gray pixel that has the same brightness as the color source.RGBimageFilter is found in java.awt.image.ImageFilter package.

#### Invert
Invert filter is subclass of RGBImageFilter also implementing PlugInFilter. It takes apart red, green and blue values and then inverts them by subtracting the values from 255.

#### Contrast
Contrast filter algorithm takes red, green and blue values separately and boosts them 1.2 times if they are already brighter than 128, if they are below 128 then they are divided by 1.2. The boosted values are properly clamped at 255 by multclamp() method.

#### Blur
Blur filter is a subclass of Convolver. The algorithm runs through every pixel in the source image array, imgpixels, and computes average of 3x3 box surrounding the pixel. The corresponding output pixels are stored in newimgpixels.

#### Sharpen
Sharpen filter is a subclass of Convolver. The algorithm runs through every pixel in the image source array, imgpixels, and computes the average of 3x3 box surrounding the pixel not considering the centre pixel. The algorithm makes bright parts brighter and dark parts darker bringing in the variation in the pixels of the part of image. The algorithm works on pixels as, if the pixel is 30 brighter than it's surroundings another 30 is added and if it is 30 darker than its surrounding then another 30 is deducted.

#### SideMirror 
SideMirror is not a filter, it does not modify pixels instead it alters the arrangement of pixels. As the name suggests the image displayed by SideMirror is the image that would be visible in mirror placed by the side. In the algorithm pixels from one dimensional array are copied into a new array. In the new array the pixels at the beginning of each row are the end pixels of each row in the original array.

#### BottomMirror
Like SideMirror BottomMirror is also a subclass of Convolver which alters the positions of pixels without applying any modifications to pixels. In the algorithm instead of one dimensional array a two dimensional matrix computed in Convolver is used that inverts positions of rows in the image to form new image.


## Convolver

Convolver creates a one dimensional array out of the loaded image. Convolver also provides a matrix of image so that it is easy to work on features like applying mirror effects.

## LoadedImage

LoadedImage is a subclass of Canvas that is used to set and reset the image display.
#### getPreferredSize
The method is used to get the dimensions of the image.
#### paint
Method of Canvas is overridden, used to display error message in case of no image loaded or wrong file loaded and displays image in the window if the file is correct. The constructor LoadedImage is called whenever a new image is chosen by user.
#### set
The method set is used to display the filtered images instead of calling the constructor all the times the functionality is separated from the constructor.

### Packages and classes used
1. java.awt
2. java.awt.image
3. java.awt.event
4. java.io
5. javax.imageio
6. java.lang.reflect

### References 
Java The Complete Reference(Eleventh Edition) - Herbert Schildt, Oracle Press
The main idea was taken from the Images chapter of the book.
