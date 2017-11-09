import java.awt.*;
import javax.swing.*;

public class MainView extends JFrame {
	private Model model;
	private TitleView titleDisplay;
	private GameView gameDisplay;
	
	public MainView(Model model) {
		this.model = model;
        this.setTitle("Move Out!");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.settitleView();
        this.setVisible(true);
	}
	
	private void settitleView() {
		this.titleDisplay = new TitleView(this.model, this);
		this.getContentPane().add(titleDisplay);
		this.setPreferredSize(new Dimension(1010, 833));
		this.setResizable(false);
		this.pack();
	}
	
	private void setgameView() {
		this.gameDisplay = new GameView(this.model, this);
		this.getContentPane().add(gameDisplay);
		this.setResizable(false);
        this.setMinimumSize(new Dimension(518, 347));
        this.setPreferredSize(new Dimension(1018, 647)); 
        this.pack();
	}
	
	public void switchtotitleView() {
		this.model.removeObserver(this.gameDisplay);
		this.getContentPane().remove(this.gameDisplay);
		this.settitleView();
		this.revalidate();
		this.repaint();
	}
	
	public void switchtogameView() {
		this.model.removeObserver(this.titleDisplay);
		this.getContentPane().remove(this.titleDisplay);
		this.setgameView();
		this.gameDisplay.setupGameDisplay();
		this.revalidate();
		this.repaint();
		this.model.gamePausedOff();
	}
}
