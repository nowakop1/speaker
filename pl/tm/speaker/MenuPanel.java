package pl.tm.speaker;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import pl.tm.speaker.MicrophoneCapture.CaptureThread;

public class MenuPanel extends JPanel {

	private static final long serialVersionUID = 282504801246092114L;
	
	private AudioFormat audioformat;
	private TargetDataLine targetDataLine;
	final JButton captureBtn = new JButton("Capture");
	final JButton stopBtn = new JButton("Stop");
	
	private AnimationPanel ap;
		
	//private SamplingGraph sg;
	
	/**
	 * Create the panel.
	 */
	public MenuPanel(final AnimationPanel ap) {
		//this.sg = sg;
		this.ap = ap;
		
		final Playback playback = new Playback(ap);
		
		captureBtn.setEnabled(true);
		stopBtn.setEnabled(false);
		
		captureBtn.addActionListener(
				new ActionListener(){
					public void actionPerformed (ActionEvent e){
						captureBtn.setEnabled(false);
						stopBtn.setEnabled(true);
						captureAudio();
					}
				}
			);
			
		stopBtn.addActionListener(
				new ActionListener(){
					public void actionPerformed (ActionEvent e){
						captureBtn.setEnabled(true);
						stopBtn.setEnabled(false);
						targetDataLine.stop();
						targetDataLine.close();
						
						//sg.createAudioInputStream(new File("junk.wav"), true);
						playback.playAudio();
					}
				}
			);
			
		add(captureBtn);
		add(stopBtn);
	}
	
	private void captureAudio(){
		try {
			audioformat = getAudioFormat();
			DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioformat);
			targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
			new CaptureThread().start();
			//new DrawThread().start();
		}catch (Exception e){
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	private AudioFormat getAudioFormat(){
		float sampleRate = 8000.0F; //8000,11025,16000,22050,44100
		int sampleSizeInBits = 16; //8, 16
		int channels = 1; //1, 2
		boolean signed = true;
		boolean bigEndian = false;
		return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
	}
	
	class CaptureThread extends Thread {
		
		public void run(){//OutputStream rawOut){
			//DataOutputStream out = new DataOutputStream(new BufferedOutputStream(rawOut));
			AudioFileFormat.Type fileType = null;
			File audioFile = null;
			fileType = AudioFileFormat.Type.WAVE;
			audioFile = new File("junk.wav");
				
			try {
				targetDataLine.open(audioformat);
				targetDataLine.start();
				AudioSystem.write(new AudioInputStream(targetDataLine), fileType, audioFile);
				/*AudioSystem.write(new AudioInputStream(targetDataLine), fileType, out);
				out.flush();
				out.close();*/
			}catch (Exception e){
				e.printStackTrace();
			}
		}
	}
	
//	class DrawThread extends Thread {
//		
//		public void run(InputStream rawIn){
//			sg.createAudioInputStream( rawIn );
//		}
//	}
}
