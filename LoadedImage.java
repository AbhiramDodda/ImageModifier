/*************************************************************************
                       Loaded Image
 *    To provide paint that can diplay either image or error message
 *************************************************************************/
import java.awt.*;
public class LoadedImage extends Canvas
{
    Image img;
    public LoadedImage(Image i)
    {
        set(i);
    }
    public void set(Image i)
    {
        img = i;
        repaint();
    }
    public void paint(Graphics g)
    {
        if(img == null)
        {
            // no file loaded or wrong format
            g.drawString("no image",10,40);
        }
        else
        {
            g.drawImage(img,0,0,this);
        }
    }
    public Dimension getPreferredSize()
    {
        return new Dimension(img.getWidth(this),img.getHeight(this));
    }
    public Dimension getMinimumSize()
    {
        return getPreferredSize();
    }
}
