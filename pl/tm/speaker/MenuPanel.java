package pl.tm.speaker;

import javax.swing.JButton;
import javax.swing.JPanel;

public class MenuPanel extends JPanel {

	private static final long serialVersionUID = 282504801246092114L;

	/**
	 * Create the panel.
	 */
	public MenuPanel() {		
		JButton startButton = new JButton("start");
		JButton exitButton = new JButton("wyjscie");
		
		add(startButton);
		add(exitButton);
	}

}
