package pl.tm.speaker;

import java.awt.BorderLayout;

import javax.swing.JFrame;

public class SpeakerAnimation {

	private JFrame frame;
	
	private SamplingGraph sg;
	private AnimationPanel ap = new AnimationPanel();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		SpeakerAnimation window = new SpeakerAnimation();
		window.frame.setVisible(true);
	}

	/**
	 * Create the application.
	 */
	public SpeakerAnimation() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(0, 0, 410, 400);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		sg = new SamplingGraph(ap);
		frame.getContentPane().add(BorderLayout.NORTH, new MenuPanel(sg));
		frame.getContentPane().add(BorderLayout.SOUTH, ap);
		//frame.getContentPane().add(BorderLayout.CENTER, sg);
		ap.start();
	}
}
