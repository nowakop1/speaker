package pl.tm.speaker;

import java.io.File;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;


public class Playback implements Runnable {
	
	private AnimationPanel ap;
	
	private AudioFormat audioFormat;
	private AudioInputStream audioInputStream;
	private SourceDataLine sourceDataLine;
	
	private byte playBuffer[];;
	
	private int bytesPerSecond;
	private int frameRate;
	
	private double avg;
	private double prev_avg;
	
	Thread runner;
	
	public Playback(AnimationPanel ap) {
		this.ap = ap;
	}
	
	public void playAudio() {			
		try {
			File soundFile = new File("junk.wav");			
			audioInputStream = AudioSystem.getAudioInputStream(soundFile);
			audioFormat = audioInputStream.getFormat();
			
			int frameLength = (int) audioInputStream.getFrameLength();
			int frameSize = (int) audioInputStream.getFormat().getFrameSize();
			
			frameRate = (int)audioFormat.getFrameRate();
			bytesPerSecond = frameLength * frameSize;

		    System.out.println(audioFormat);
		    
		    DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, audioFormat);
		    sourceDataLine = (SourceDataLine)AudioSystem.getLine(dataLineInfo);
		    
		    start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void start() {
		if (runner == null) {
			runner = new Thread(this);
			runner.start();
		}
	}
	
	public void stop() {
		if (runner != null) {
			runner = null;
		}
	}
	
	public void run() {		
		playBuffer = new byte[bytesPerSecond / 500];
		
		try {
			sourceDataLine.open(audioFormat);
			sourceDataLine.start();
		      
			int cnt;
			avg = 0;
			prev_avg = 0;
			int nlengthInSamples = playBuffer.length / 2;
			int a;
				
			while((cnt = audioInputStream.read(playBuffer, 0, playBuffer.length)) != -1) {
				if(cnt > 0){
					sourceDataLine.write(playBuffer, 0, cnt);
					prev_avg = avg;
					
					for (int i = 0; i < nlengthInSamples; i++) {
						/* First byte is MSB (high order) */
						int MSB = (int) playBuffer[2*i];
						/* Second byte is LSB (low order) */
						int LSB = (int) playBuffer[2*i+1];
						a = MSB << 8 | (255 & LSB);
						a = 128 * a / 32768;
						avg += a;
					}
					
					avg = avg / playBuffer.length;
					avg = avg * 10;
					if(Math.abs(avg - prev_avg) > 100)
						avg = prev_avg;
					ap.setAudioValues(Math.abs(avg - prev_avg));
				}
			}
			
			ap.setAudioValues(0);
				
			sourceDataLine.drain();
			sourceDataLine.close();
			
			stop();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public double getAvg() {
		return avg;
	}
}
