import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

public class TitleView extends JPanel implements Observer {
	
	private Model model;
    private MainView mainView;
    
    // list of buttons
    private JButton playButton;
    private JButton helpButton;
    private JButton quitButton;
    
    // list of images to be used
    private ImageIcon titleImage;
    private ImageIcon spaceshipImage;
    
    // list of sub-panels
    private JPanel titlePanel;
    private JPanel buttonPanel;
    private JPanel imagePanel;
    
    private boolean gameFileFound;
    
    /**
     * Create a new View.
     */
    public TitleView(Model model, MainView mainView) {
    	this.model = model;
    	this.mainView = mainView;
    	this.layoutView();
    	this.gameFileFound = false;
    	this.registerControllers();
    	this.model.addObserver(this);
    }
    
    private void layoutView() {
    	this.playButton = new JButton("Play");
    	this.helpButton = new JButton("Help");
    	this.quitButton = new JButton("Quit");
    	
    	this.titlePanel = new JPanel();
    	this.buttonPanel = new JPanel();
    	this.imagePanel = new JPanel();

    	try {
        	BufferedImage titleimg = ImageIO.read(new File("src/transparent_title.png"));
        	this.titleImage = new ImageIcon(titleimg);
        	BufferedImage spaceshipimg = ImageIO.read(new File("src/transparent_spaceship.png"));
        	this.spaceshipImage = new ImageIcon(spaceshipimg);
    	}
    	catch (IOException e) {
    		e.printStackTrace();
    	}

    	this.setLayout(new BorderLayout(0, 0));
    	
    	// layout for a title panel
        JLabel title = new JLabel(this.titleImage);
        this.titlePanel.setBackground(Color.black);
        this.titlePanel.add(title);
        this.add("North", this.titlePanel);
    	
    	// layout for a button panel
    	SpringLayout buttonLayout = new SpringLayout();
    	this.buttonPanel.setLayout(buttonLayout);
    	this.buttonPanel.setBackground(Color.black);
        this.add("Center", this.buttonPanel);
        
        buttonLayout.putConstraint(SpringLayout.NORTH, this.playButton, 5, SpringLayout.SOUTH, this.titlePanel);
        buttonLayout.putConstraint(SpringLayout.WEST, this.playButton, 5, SpringLayout.WEST, this);
        this.buttonPanel.add(this.playButton);
        
        buttonLayout.putConstraint(SpringLayout.NORTH, this.helpButton, 5, SpringLayout.SOUTH, this.playButton);
        buttonLayout.putConstraint(SpringLayout.WEST, this.helpButton, 5, SpringLayout.WEST, this);
        this.buttonPanel.add(this.helpButton);
        
        buttonLayout.putConstraint(SpringLayout.NORTH, this.quitButton, 5, SpringLayout.SOUTH, this.helpButton);
        buttonLayout.putConstraint(SpringLayout.WEST, this.quitButton, 5, SpringLayout.WEST, this);
        this.buttonPanel.add(this.quitButton);     
         
        // layout for a image panel
        JLabel image = new JLabel(this.spaceshipImage);
        this.imagePanel.setBackground(Color.black);
        this.imagePanel.add(image);
        this.add("East", this.imagePanel);
    }
    
    private void registerControllers() {
    	this.playButton.addMouseListener(new MouseAdapter() {
    		public void mouseClicked(MouseEvent e) {
    			model.startGameModel();
    		}
    	});
    	this.helpButton.addMouseListener(new MouseAdapter() {
    		public void mouseClicked(MouseEvent e) {
    			String aboutmsg = "WELCOME TO MOVE OUT!\n In this game you will need to avoid incoming blocks, otherwise you will crash into it and die.\n\n";
    			String controlmsg = "Controls are:\n" + "     W: Up\n" + "     S: Down\n" + "     D: Forward\n" + "     A: Backward\n" + "     Space: Pause\n\n";
    			String enhancemsg = "By the way, due to the gravity, you will be also pulled to the left side slower than other blocks.\n EXTRA PRECAUTION NEEDED!\n";
    			
    			String msg = aboutmsg + controlmsg + enhancemsg;
    			String title = "Help";
    			JOptionPane.showMessageDialog(null, msg, title, JOptionPane.INFORMATION_MESSAGE);
    		}
    	});
    	this.quitButton.addMouseListener(new MouseAdapter() {
    		public void mouseClicked(MouseEvent e) {
    			System.exit(0);
    		}
    	});
    }
    
    /**
     * Update with data from the model.
     */
    public void update(Object observable) {
        // XXX Fill this in with the logic for updating the view when the model
        // changes.
    	this.gameFileFound = this.model.getgameFileFound();
    	
    	if (this.gameFileFound == true) {
    		this.mainView.switchtogameView();
    	}
    }
}
