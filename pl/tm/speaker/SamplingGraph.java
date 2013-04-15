package pl.tm.speaker;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.Line2D;
import java.io.File;
import java.io.InputStream;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.Vector;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.swing.JPanel;

class SamplingGraph extends JPanel {

	private static final long serialVersionUID = -6981728433133015261L;
	
    private Font font12 = new Font("serif", Font.PLAIN, 12);
    Color jfcBlue = new Color(204, 204, 255);
    Vector lines = new Vector();
    
    
    private File file;
	private String fileName;
	private AudioInputStream audioInputStream;
	
	private AnimationPanel ap;
    
    double duration;   
    
    String errStr;

    public SamplingGraph(AnimationPanel ap) {
    	this.ap = ap;
    	setPreferredSize(new Dimension(400, 300));
        setBackground(new Color(20, 20, 20));
    }
    
    public void createAudioInputStream(File file, boolean updateComponents) {
        if (file != null && file.isFile()) {
            try {
                this.file = file;
                errStr = null;
                audioInputStream = AudioSystem.getAudioInputStream(file);
                fileName = file.getName();
                System.out.println(audioInputStream.getFormat().toString());
                long milliseconds = (long)((audioInputStream.getFrameLength() * 1000) / audioInputStream.getFormat().getFrameSize());
                duration = milliseconds / 1000.0;
                if (updateComponents) {
                    this.createWaveForm(null);
                }
            } catch (Exception ex) { 
                reportStatus(ex.toString());
            }
        } else {
            reportStatus("Audio file required.");
        }
    }
    
    public void createAudioInputStream( InputStream inputstream ) {
    	audioInputStream = new AudioInputStream(inputstream, new AudioFormat(8000.0F, 16, 1, true, false), 16000);//set another length)
    	
    }
    
    private void reportStatus(String msg) {
        if ((errStr = msg) != null) {
            System.out.println(errStr);
            this.repaint();
        }
    }

    public void createWaveForm(byte[] audioBytes) {
    	
    	double [] audioValues;

        lines.removeAllElements();  // clear the old vector

        AudioFormat format = audioInputStream.getFormat();
        if (audioBytes == null) {
            try {
                audioBytes = new byte[
                    (int) (audioInputStream.getFrameLength() 
                    * format.getFrameSize())];
                audioInputStream.read(audioBytes);
            } catch (Exception ex) { 
                reportStatus(ex.toString());
                return; 
            }
        }

        //Dimension d = getSize();
        int w = 400;
        int h = 300;
        
        int[] audioData = null;
        if (format.getSampleSizeInBits() == 16) {
             int nlengthInSamples = audioBytes.length / 2;
             audioData = new int[nlengthInSamples];
             if (format.isBigEndian()) {
                for (int i = 0; i < nlengthInSamples; i++) {
                     /* First byte is MSB (high order) */
                     int MSB = (int) audioBytes[2*i];
                     /* Second byte is LSB (low order) */
                     int LSB = (int) audioBytes[2*i+1];
                     audioData[i] = MSB << 8 | (255 & LSB);
                 }
             } else {
                 for (int i = 0; i < nlengthInSamples; i++) {
                     /* First byte is LSB (low order) */
                     int LSB = (int) audioBytes[2*i];
                     /* Second byte is MSB (high order) */
                     int MSB = (int) audioBytes[2*i+1];
                     audioData[i] = MSB << 8 | (255 & LSB);
                 }
             }
         } else if (format.getSampleSizeInBits() == 8) {
             int nlengthInSamples = audioBytes.length;
             audioData = new int[nlengthInSamples];
             if (format.getEncoding().toString().startsWith("PCM_SIGN")) {
                 for (int i = 0; i < audioBytes.length; i++) {
                     audioData[i] = audioBytes[i];
                 }
             } else {
                 for (int i = 0; i < audioBytes.length; i++) {
                     audioData[i] = audioBytes[i] - 128;
                 }
             }
        }
        
        audioValues = new double [w];
                           
        int frames_per_pixel = audioBytes.length / format.getFrameSize() / w;
        byte my_byte = 0;
        byte my_byte_last = 0;
        double y_last = 0;
        int numChannels = format.getChannels();
        int i = 0;
        for (double x = 0; x < w && audioData != null; x++) {
        	//System.out.println(x);
            int idx = (int) (frames_per_pixel * numChannels * x);
            if (format.getSampleSizeInBits() == 8) {
                 my_byte = (byte) audioData[idx];
            } else {
                 my_byte = (byte) (128 * audioData[idx] / 32768 );
            }
            //System.out.println(my_byte);
            double y_new = (double) (h * (128 - my_byte) / 256);
            lines.add(new Line2D.Double(x, y_last, x, y_new));
            audioValues[i] = Math.abs(my_byte - my_byte_last);
            y_last = y_new;
            my_byte_last = my_byte;
            i++;
        }
        
        ap.setAudioValues(audioValues);

        repaint();
    }


    public void paint(Graphics g) {

        Dimension d = getSize();
        int w = d.width;
        int h = d.height;
        int INFOPAD = 15;

        Graphics2D g2 = (Graphics2D) g;
        g2.setBackground(getBackground());
        g2.clearRect(0, 0, w, h);
        g2.setColor(Color.white);
        g2.fillRect(0, h-INFOPAD, w, INFOPAD);

        if (errStr != null) {
            g2.setColor(jfcBlue);
            g2.setFont(new Font("serif", Font.BOLD, 18));
            g2.drawString("ERROR", 5, 20);
            AttributedString as = new AttributedString(errStr);
            as.addAttribute(TextAttribute.FONT, font12, 0, errStr.length());
            AttributedCharacterIterator aci = as.getIterator();
            FontRenderContext frc = g2.getFontRenderContext();
            LineBreakMeasurer lbm = new LineBreakMeasurer(aci, frc);
            float x = 5, y = 25;
            lbm.setPosition(0);
            while (lbm.getPosition() < errStr.length()) {
                TextLayout tl = lbm.nextLayout(w-x-5);
                if (!tl.isLeftToRight()) {
                    x = w - tl.getAdvance();
                }
                tl.draw(g2, x, y += tl.getAscent());
                y += tl.getDescent() + tl.getLeading();
            }
        } else {
            g2.setColor(Color.black);
            g2.setFont(font12);
            g2.drawString("File: " + fileName + "  Length: " + String.valueOf(duration), 3, h-4);

            if (audioInputStream != null) {
                // .. render sampling graph ..
                g2.setColor(jfcBlue);
                for (int i = 1; i < lines.size(); i++) {
                    g2.draw((Line2D) lines.get(i));
                }
            }
        }
    }
}