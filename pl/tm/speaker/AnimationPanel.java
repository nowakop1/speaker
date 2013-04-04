package pl.tm.speaker;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

public class AnimationPanel extends JPanel {

	private static final long serialVersionUID = -8934973488257733142L;

	/**
	 * Create the panel.
	 */
	public AnimationPanel() {
		setPreferredSize(new Dimension(200, 200));
	}
	
	@Override
	public void paintComponent(Graphics g) {
		g.setColor(Color.orange);
		
		g.fillOval(getWidth() / 2, getHeight() / 2, 100, 100);
	}

}
