package pl.tm.speaker;

import java.awt.BorderLayout;

import javax.swing.JFrame;

public class SpeakerAnimation {

	private JFrame frame;

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
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.getContentPane().add(BorderLayout.WEST, new AnimationPanel());
		frame.getContentPane().add(BorderLayout.EAST, new MenuPanel());
	}
}
