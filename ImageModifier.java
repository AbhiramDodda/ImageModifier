import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import javax.imageio.*;
import java.io.*;
import java.lang.reflect.*;
import javax.swing.JFileChooser;

public class ImageModifier extends Frame implements ActionListener {
    Image img;
    PlugInFilter pif;
    Image fimg;
    Image curimg;
    LoadedImage limg;
    Label lab;
    Button reset;
    Button newImage;
    String[] filters = {"Grayscale", "Invert", "Contrast", "Blur", "Sharpen", "SideMirror", "BottomMirror"};
    ImageModifier()
    {
        Panel p = new Panel();
        add(p, BorderLayout.SOUTH);

        reset = new Button("Reset");
        reset.addActionListener(this);
        p.add(reset);

        newImage = new Button("NewImage");
        newImage.addActionListener(this);
        p.add(newImage);

        for(String fstr: filters)
        {
            Button b = new Button(fstr);
            b.addActionListener(this);
            p.add(b);
        }
        lab = new Label("");
        add(lab, BorderLayout.NORTH);
        JFileChooser file = new JFileChooser();
        int result = file.showOpenDialog(null);
        if(result != JFileChooser.APPROVE_OPTION)
        {
            result = -1;
        }
        try
        {
            File imgFile = file.getSelectedFile();
            //File imgfile = new File(result);
            img = ImageIO.read(imgFile);
        }
        catch(IOException ioe)
        {
            System.out.println("No file");
            System.exit(0);
        }
        limg = new LoadedImage(img);
        add(limg, BorderLayout.CENTER);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we)
            {
                System.exit(0);
            }
        });
    }

    public JFileChooser chooseFile()
    {
        JFileChooser file = new JFileChooser();
        int result = file.showOpenDialog(null);
        return file;
    }
    // abstract method from ActionListener interface so must be implemented
    // whenever a button added with actionlistener as here with addActionListener() it calls actionPerformed
    public void actionPerformed(ActionEvent ae)
    {
        String a ="";
        try
        {
            a = ae.getActionCommand();
            if(a.equals("Reset"))
            {
                limg.set(img); // repaint()
                lab.setText("Normal"); // at panel on top
            }
            else if(a.equals("NewImage"))
            {
                JFileChooser fileChoosen = chooseFile();
                File imgFile = fileChoosen.getSelectedFile();
                img = ImageIO.read(imgFile);
                limg.set(img);
            }
            else
            {
                pif = (PlugInFilter)(Class.forName(a)).getConstructor().newInstance();
                fimg = pif.filter(this, img);
                limg.set(fimg);
                lab.setText("Filtered:" + a);
            }
            repaint();
        }
        catch(ClassNotFoundException cnf)
        {
            lab.setText(a+"not found");
            limg.set(fimg);
            repaint();
        }
        catch(InstantiationException ie)
        {
            lab.setText("couldn't new" + a);
        }
        catch(IllegalAccessException iae)
        {
            lab.setText("no access" + a);
        }
        catch(NoSuchMethodException | InvocationTargetException nme)
        {
            lab.setText("filter creation error" + nme);
        }
        catch(IOException ie)
        {
            lab.setText("ioexception");
        }
    }
    public static void main(String[] args)
    {
        ImageModifier jm = new ImageModifier();

        jm.setSize(new Dimension(950,700));
        jm.setVisible(true);
    }
}
interface PlugInFilter
{
    java.awt.Image filter(java.awt.Frame f, java.awt.Image in);
}
class Invert extends RGBImageFilter implements PlugInFilter {
    public Invert(){}
    public Image filter(Frame f,Image i)
    {
        return f.createImage(new FilteredImageSource(i.getSource(), this));
    }
    public int filterRGB(int x,int y,int rgb)
    {
        int r = 0xff - (rgb >> 16) & 0xff;
        int g = 0xff - (rgb >> 8) & 0xff;
        int b = 0xff - rgb & 0xff;
        return (0xff000000 | r << 16 | g << 8 | b);
    }
}
class Contrast extends RGBImageFilter implements PlugInFilter
{
    public Contrast(){}
    public Image filter(Frame f,Image i)
    {
        return f.createImage(new FilteredImageSource(i.getSource(), this));
    }
    private int mulclamp(int i,double factor)
    {
        i = (int)(i*factor);
        return (i > 255) ? 255 : i;
    }
    double gain = 1.2;
    private int cont(int i)
    {
        return (i < 128) ? (int)(i/gain) : mulclamp(i,gain);
    }
    public int filterRGB(int x,int y,int rgb)
    {
        int r = cont((rgb >> 16) & 0xff);
        int g = cont((rgb >> 8) & 0xff);
        int b = cont(rgb & 0xff);
        return (0xff000000 | r << 16 | g << 8 | b);
    }
}
class Grayscale extends RGBImageFilter implements PlugInFilter
{
    public Grayscale(){}
    public Image filter(Frame f, Image i)
    {
        return f.createImage(new FilteredImageSource(i.getSource(), this));
    }
    public int filterRGB(int x,int y,int rgb)
    {
        int r = (rgb >> 16) & 0xff;
        int g = (rgb >> 8) & 0xff;
        int b = rgb & 0xff;
        int k = (int)(0.56 * g + 0.33 * r + 0.11 * b);
        return (0xff000000 | k << 16 | k << 8 | k);
    }
}
class Blur extends Convolver
{
    public Blur(){}
    public void convolve()
    {
        for(int i=1; i<height-1; i++)
        {
            for(int j=1; j<width-1;j++)
            {
                int rs = 0,gs = 0,bs = 0;
                for(int k=-1;k<=1;k++)
                {
                    for(int l=-1;l<=1;l++)
                    {
                        int rgb = imgpixels[(i+k)*width+j+l];
                        int r = (rgb >> 16) & 0xff;
                        int g = (rgb >> 8) & 0xff;
                        int b = rgb & 0xff;
                        rs += r;
                        gs += g;
                        bs += b;
                    }
                }
                rs /= 9;
                gs /= 9;
                bs /= 9;
                newimgpixels[i*width+j] = (0xff000000 | rs << 16 | gs << 8 | bs);
            }          
        }
    }
    public void setProperies(java.util.Hashtable<?,?> dummy){}
    public void setPixels(int x1,int y1,int w,int h,ColorModel model,byte pixels[],int off,int scansize){}
}
class Sharpen extends Convolver
{
    public Sharpen(){}
    private int clamp(int c)
    {
        return (c > 255 ? 255 : (c < 0 ? 0 : c));
    }
    public void convolve()
    {
        int r0 = 0,g0 = 0,b0 = 0;
        for(int i=1;i<height-1;i++)
        {
            for(int j=1;j<width-1;j++)
            {
                int rs=0,gs=0,bs=0;
                for(int k=-1;k<=1;k++)
                {
                    for(int l=-1;l<=1;l++)
                    {
                        int rgb = imgpixels[(i+k)*width+j+l];
                        int r = (rgb >> 16) & 0xff;
                        int g = (rgb >> 8) & 0xff;
                        int b = rgb & 0xff;
                        if(l==0 && k==0)
                        {
                            r0 = r;
                            g0 = g;
                            b0 = b;
                        }
                        else
                        {
                            rs += r;
                            gs += g;
                            bs += b;
                        }
                    }
                }
                rs >>= 3;
                gs >>= 3;
                bs >>= 3;
                newimgpixels[i*width+j] = (0xff000000 | clamp(2*r0 - rs) << 16 | clamp(2*g0 - gs) << 8 | clamp(2*b0 - bs));
            }
        }
    }
}
class SideMirror extends Convolver
{
    public SideMirror(){}
    public void convolve()
    {
        for(int i=0;i<height-1;i++)
        {
            for(int j=0;j<width-1;j++)
            {
                newimgpixels[i*width+j] = imgpixels[width-j+i*width];
            }
        }
    }
}
class BottomMirror extends Convolver
{
    public BottomMirror(){}
    public void convolve()
    {
        setImg2d();
        for(int i=0;i<height-1;i++)
        {
            for(int j=0;j<width-1;j++)
            {
                newimgpixels[i*width+j] = imgpixels2d[height-1-i][j];
            }
        }
    }
}
