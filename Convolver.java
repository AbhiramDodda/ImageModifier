import java.awt.*;
import java.awt.image.*;
abstract class Convolver implements ImageConsumer, PlugInFilter {
    int width,height;
    int imgpixels[], newimgpixels[], imgpixels2d[][];
    boolean imageready = false;
    abstract void convolve();

    public Image filter(Frame f,Image i)
    {
        imageready = false;
        i.getSource().startProduction(this);
        waitForImage();
        newimgpixels = new int[width*height];
        try
        {
            convolve();
        }
        catch(Exception e)
        {
            System.out.println("Convolver failed " + e);
            e.printStackTrace();
        }
        return f.createImage(new MemoryImageSource(width, height, newimgpixels, 0, width));
    }
    synchronized void waitForImage()
    {
        try
        {
            while(!imageready)
            {
                wait();
            }
        }
        catch(Exception e)
        {
            System.out.println("Interrupted");
        }
    }
    public void setProperties(java.util.Hashtable<?,?> dummy){ }
    public void setColorModel(ColorModel dummy){}
    public void setHints(int dummy){}
    public synchronized void imageComplete(int dummy)
    {
        imageready = true;
        notifyAll();
    }
    public void setDimensions(int x,int y)
    {
        width = x;
        height = y;
        imgpixels = new int[x*y];
    }
    public void setPixels(int x1,int y1,int w,int h,ColorModel model,byte pixels[],int off,int scansize)
    {
        int pix,x,y,x2,y2,sx,sy;
        x2 = x1 + w;
        y2 = y1 + h;
        sy = off;
        for(y=y1;y<y2;y++)
        {
            sx = sy;
            for(x = x1;x < x2;x++)
            {
                pix = model.getRGB(pixels[sx++]);
                if((pix & 0xff000000) == 0) 
                {
                    pix = 0x00ffffff;
                }
                imgpixels[y*width + x] = pix;
            }
            sy += scansize;
        }
    }
    public void setPixels(int x1,int y1,int w,int h,ColorModel model,int pixels[],int off,int scansize)
    {
        int pix,x,y,x2,y2,sx,sy;
        x2 = x1 + w;
        y2 = y1 + h;
        sy = off;
        for(y=y1;y<y2;y++)
        {
            sx = sy;
            for(x = x1;x < x2;x++)
            {
                pix = model.getRGB(pixels[sx++]);
                if((pix & 0xff000000) == 0) 
                {
                    pix = 0x00ffffff;
                }
                imgpixels[y*width + x] = pix;
            }
            sy += scansize;
        }
    }
    public void setImg2d()
    {
        imgpixels2d = new int[height][width];
        for(int i=0;i<height;i++)
        {
            for(int j=0;j<width;j++)
            {
                imgpixels2d[i][j] = imgpixels[i*width + j];
            }
        }
    }
}
