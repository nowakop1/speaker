package pl.tm.speaker;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;
import javax.swing.*;
import java.io.*;
//import com.sun.java.util.jar.pack.Package.File;

public class MicrophoneCapture extends JFrame{
	
	private static final long serialVersionUID = 1820537616017154755L;
	private AudioFormat audioformat;
	private TargetDataLine targetDataLine;
	final JButton captureBtn = new JButton("Capture");
	final JButton stopBtn = new JButton("Stop");
	
	final JPanel btnPanel = new JPanel();
	final ButtonGroup btnGroup = new ButtonGroup();
	final JRadioButton aifcBtn = new JRadioButton("AIFC");
	final JRadioButton aiffBtn = new JRadioButton("AIFF");
	final JRadioButton auBtn = new JRadioButton("AU", true);
	final JRadioButton sndBtn = new JRadioButton("SND");
	final JRadioButton waveBtn = new JRadioButton("WAVE");
	
	public static void main (String [] args){
		new MicrophoneCapture();
	}
	
	public MicrophoneCapture(){
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
					}
				}
			);
		
		getContentPane().add(captureBtn);
		getContentPane().add(stopBtn);
		btnGroup.add(aifcBtn);
		btnGroup.add(aiffBtn);
		btnGroup.add(auBtn);
		btnGroup.add(sndBtn);
		btnGroup.add(waveBtn);
		btnPanel.add(aifcBtn);
		btnPanel.add(aiffBtn);
		btnPanel.add(auBtn);
		btnPanel.add(sndBtn);
		btnPanel.add(waveBtn);
		getContentPane().add(btnPanel);
		getContentPane().setLayout(new FlowLayout());
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(300, 120);
		setVisible(true);
	}//end of constructor
	
	private void captureAudio(){
		try {
			audioformat = getAudioFormat();
			DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioformat);
			targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
			new CaptureThread().start();
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
		public void run(){
			AudioFileFormat.Type fileType = null;
			File audioFile = null;
			if (aifcBtn.isSelected()){
				fileType = AudioFileFormat.Type.AIFC;
				audioFile = new File("junk.aifc");
			}else if (aiffBtn.isSelected()){
				fileType = AudioFileFormat.Type.AIFF;
				audioFile = new File("junk.aiff");
			}else if (auBtn.isSelected()){
				fileType = AudioFileFormat.Type.AU;
				audioFile = new File("junk.au");
			}else if (sndBtn.isSelected()){
				fileType = AudioFileFormat.Type.SND;
				audioFile = new File("junk.snd");
			}else if (waveBtn.isSelected()){
				fileType = AudioFileFormat.Type.WAVE;
				audioFile = new File("junk.wave");
			}
			try {
				targetDataLine.open(audioformat);
				targetDataLine.start();
				AudioSystem.write(new AudioInputStream(targetDataLine), fileType, audioFile);
			}catch (Exception e){
				e.printStackTrace();
			}
		}
	}

}
