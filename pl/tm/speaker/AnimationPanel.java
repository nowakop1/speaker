package pl.tm.speaker;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class AnimationPanel extends JPanel implements Runnable{

	private static final long serialVersionUID = -8934973488257733142L;
	
	//private BufferedImage img;
	private List<BufferedImage> imgList = new ArrayList<BufferedImage>();
	private double[] imgValues = {10, 50, 100, 150, 200, 250, 300, 350, 400, 450, 1000};
	private double audioValues;
	
	int totalPictures = 11;
	int current = 0;
	int position = 0;
	double value = 0.0;
	
	Thread runner;
	int pause = 50;

	/**
	 * Create the panel.
	 */
	public AnimationPanel() {
		try {
			for(int i = 0; i < totalPictures; i++)
				imgList.add(ImageIO.read(new File("img/" + i + ".png")));
		} catch (IOException e) {
			System.out.println(e);
		}
		
		Dimension dimension = new Dimension(100, 100);
		setPreferredSize(dimension);
		//setPreferredSize(new Dimension(200, 200));
	}
	
	@Override
	public void paintComponent(Graphics g) {
		int x = getWidth();
    	int y = getHeight();
    	int imgWidth;
    	int imgHeight;
    	
    	g.setColor(new Color(0, 139, 69));
        g.fillRect(0,0,x,y);
        
		Graphics2D g2d = (Graphics2D) g;
		
		imgWidth = imgList.get(current).getWidth();
		imgHeight = imgList.get(current).getHeight();
		
//		System.out.println(current);
		g2d.drawImage(imgList.get(current), (x - imgWidth) / 2, (y - imgHeight) / 2 , this);
	}
	
	public void start() {
		if (runner == null) {
			runner = new Thread(this);
			runner.start();
		}
	}
	
	@Override
	public void run() {
		Thread thisThread = Thread.currentThread();
		while (runner == thisThread) {
			repaint();
			
			if(audioValues != 0) {			
				//System.out.println(audioValues[position] + " " + audioValues.length);
				
				if(audioValues == 0) {
					stop();
				} else {
					//System.out.println(audioValues[position] + " " + imgValues[current]);
//					if(audioValues > imgValues[current])
						System.out.println("TAK " + audioValues);
					while(audioValues > imgValues[current]) {
						current++;
						//System.out.println(audioValues + " " + imgValues[current]);
					}
					
					try {
						Thread.sleep(pause);
					}
					catch (InterruptedException e) { }
				}
				position += 10;
				//System.out.println(position);
				current = 0;
			}
		}		
	}
	
	public void stop() {
		if (runner != null) {
			runner = null;
			position = 0;
		}
	}

	public void setAudioValues(double audioValues) {
		this.audioValues = audioValues;
	}

}
