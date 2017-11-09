import java.util.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.Timer;


public class GameView extends JPanel implements Observer {
	
    private Model model;
    private MainView mainView;
    
    // Variables for layout
	private SpringLayout displayLayout;
	private JLabel scoreLabel;
	private JLabel fpsLabel;
	private JLabel speedLabel;
    private JButton helpButton;
    private JButton setFPSButton;
    private JButton setSpeedButton;
    
    // Timer variable for animation
    private Timer animationTimer;
    
    private int displayHeight;
    
    // Variables for painting
    private double blockSize;
    private int scrollSpeed;
    private int plyrDisplayPosnX;
    private int plyrDisplayPosnY;
    private int finishLineDisplayPosnX;
    private ArrayList<int[]> obstaclesDisplayPosn;
    
    // Variables for game logic
    private boolean gamePaused;
    private boolean gameWon;
    private boolean gameLost;
    private int gameScore;
    private boolean gameReset;
    
	public GameView(Model model, MainView mainView) {
        this.setSize(new Dimension(1000, 600));
        this.setFocusable(true);
        this.setBackground(Color.black);
		
        this.model = model;
        this.mainView = mainView;
        
        this.displayLayout = new SpringLayout();
        this.setLayout(this.displayLayout);
        this.scoreLabel = new JLabel("Score: " + this.model.getgameScore());
        this.scoreLabel.setFont(new Font("Arial", Font.BOLD, 18));
        this.scoreLabel.setForeground(Color.white);
        this.fpsLabel = new JLabel("FPS: " + this.model.getfps());
        this.fpsLabel.setFont(new Font("Arial", Font.BOLD, 18));
        this.fpsLabel.setForeground(Color.white);
        this.speedLabel = new JLabel("Speed: " + this.model.getspeed());
        this.speedLabel.setFont(new Font("Arial", Font.BOLD, 18));
        this.speedLabel.setForeground(Color.white);
		this.helpButton = new JButton("Help");
        this.setFPSButton = new JButton("FPS");
		this.setSpeedButton = new JButton("Speed");
		this.layoutView();
		
		this.registerControllers();
		
        this.model.addObserver(this);

        this.animationTimer = new Timer(1000/this.model.getfps(), event -> {
        	repaint();
        });
	}
	
	public void setupGameDisplay() {
        this.displayHeight = (this.getHeight() - (this.getHeight())/6);
        
        this.blockSize = this.displayHeight / this.model.getvertBlockNum();
        this.scrollSpeed = 0;
        this.plyrDisplayPosnX = (int) Math.round(this.model.getplyrPosnX() * this.blockSize);
        this.plyrDisplayPosnY = (int) Math.round(this.model.getplyrPosnY() * this.blockSize);
        this.finishLineDisplayPosnX = (int) Math.round(this.model.getfinishLinePosnX() * this.blockSize);
        this.obstaclesDisplayPosn = new ArrayList<int[]>();
        
        this.gamePaused = true;
        this.gameWon = false;
        this.gameLost = false;
        this.gameScore = 0;
        this.gameReset = false;
        
        this.initObstacleDisplayPosn();
	}
	
	private void layoutView() {
		this.displayLayout.putConstraint(SpringLayout.NORTH, this.scoreLabel, this.displayHeight, SpringLayout.NORTH, this);
        this.displayLayout.putConstraint(SpringLayout.WEST, this.scoreLabel, 0, SpringLayout.WEST, this);
        this.add(this.scoreLabel);
        
        this.displayLayout.putConstraint(SpringLayout.NORTH, this.fpsLabel, 0, SpringLayout.SOUTH, this.scoreLabel);
        this.displayLayout.putConstraint(SpringLayout.WEST, this.fpsLabel, 0, SpringLayout.WEST, this);
        this.add(this.fpsLabel);
		
        this.displayLayout.putConstraint(SpringLayout.NORTH, this.speedLabel, 0, SpringLayout.SOUTH, this.fpsLabel);
        this.displayLayout.putConstraint(SpringLayout.WEST, this.speedLabel, 0, SpringLayout.WEST, this);
        this.add(this.speedLabel);
        
        this.displayLayout.putConstraint(SpringLayout.NORTH, this.helpButton, this.displayHeight, SpringLayout.NORTH, this);
        this.displayLayout.putConstraint(SpringLayout.EAST, this.helpButton, 0, SpringLayout.EAST, this);
        this.add(this.helpButton);
		
        this.displayLayout.putConstraint(SpringLayout.NORTH, this.setFPSButton, this.displayHeight, SpringLayout.NORTH, this);
        this.displayLayout.putConstraint(SpringLayout.EAST, this.setFPSButton, 0, SpringLayout.WEST, this.helpButton);
		this.add(this.setFPSButton);
		
        this.displayLayout.putConstraint(SpringLayout.NORTH, this.setSpeedButton, this.displayHeight, SpringLayout.NORTH, this);
		this.displayLayout.putConstraint(SpringLayout.EAST, this.setSpeedButton, 0, SpringLayout.WEST, this.setFPSButton);
		this.add(this.setSpeedButton);
	}
	
	private void initObstacleDisplayPosn() {
        for (int i = 0; i < this.model.getnumObstacles(); i++) {
        	int top_left_x = (int) Math.round(this.model.gettopleftX(i) * this.blockSize);
        	int top_left_y = (int) Math.round(this.model.gettopleftY(i) * this.blockSize);
        	int bottom_right_x = (int) Math.round(this.model.getbottomrightX(i) * this.blockSize);
        	int bottom_right_y = (int) Math.round(this.model.getbottomrightY(i) * this.blockSize);
        	
        	int[] dimensions = {top_left_x, top_left_y, bottom_right_x, bottom_right_y};
        	this.obstaclesDisplayPosn.add(dimensions);
        }
	} 

	@Override
	public void paintComponent(Graphics gph) {
		super.paintComponent(gph);;
		
		//int horiBlockNumDisplay = (int) Math.round(this.getWidth() / this.blockSize);
		int width = 0;
		int height = 0;
		
		Graphics2D gph2 = (Graphics2D) gph;
		gph2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		/* Drawing grids for debug
		for (int i = 0; i < horiBlockNumDisplay; i++) {
			for (int j = 0; j < this.model.getvertBlockNum(); j++) {
				gph2.drawRect((int) Math.round(i*blockSize),(int) Math.round(j*blockSize), (int) Math.round(blockSize), (int) Math.round(blockSize));
			}
		}
		*/

		for (int i = 0; i < this.model.getnumObstacles(); i++) {
			
			this.obstaclesDisplayPosn.get(i)[0] -= this.scrollSpeed;
			this.obstaclesDisplayPosn.get(i)[2] -= this.scrollSpeed;
			
			width = (int) Math.round(this.obstaclesDisplayPosn.get(i)[2] - this.obstaclesDisplayPosn.get(i)[0] + this.blockSize);
			height = (int) Math.round(this.obstaclesDisplayPosn.get(i)[3] - this.obstaclesDisplayPosn.get(i)[1] + this.blockSize);
			
			if (this.obstaclesDisplayPosn.get(i)[0] <= this.getWidth()) {
				gph2.setColor(Color.gray);
				gph2.fillRect(this.obstaclesDisplayPosn.get(i)[0], this.obstaclesDisplayPosn.get(i)[1], width, height);
			}
		}
		
		width = (int) Math.round(this.blockSize/4);
		height = (int) Math.round(this.model.getvertBlockNum() * this.blockSize);
		
		this.finishLineDisplayPosnX -= this.scrollSpeed;
		
		gph2.setColor(Color.yellow);
		gph2.fillRect(this.finishLineDisplayPosnX, 0, width, height);
		
		width = (int) Math.round(this.blockSize);
		height = width;
		
		this.plyrDisplayPosnX -= this.scrollSpeed/2;
		
		gph2.setColor(Color.blue);
		gph2.fillRect(this.plyrDisplayPosnX, this.plyrDisplayPosnY, width, height);
		
		this.checkBoundary();
		this.checkCollisions();
		this.checkReached();
		this.updateScore();
		this.setPlayerPosn();
		this.setFinishLinePosnX();
		this.setObstaclesPosnX();
		this.checkCollisions();
		this.adjustSize();
		this.adjustLayout();
	 } 
	
	private void checkBoundary() {
		if (this.plyrDisplayPosnY <= 0) {
			if (this.plyrDisplayPosnX >= (this.getWidth() - this.blockSize)) {
				this.plyrDisplayPosnX = (int) Math.round(this.getWidth() - this.blockSize);
			}
			this.plyrDisplayPosnY = 0;
		}
		else if (this.plyrDisplayPosnY >= (this.displayHeight - this.blockSize)) {
			if (this.plyrDisplayPosnX >= (this.getWidth() - this.blockSize)) {
				this.plyrDisplayPosnX = (int) Math.round(this.getWidth() - this.blockSize);
			}
			this.plyrDisplayPosnY = (int) Math.round(this.displayHeight - this.blockSize);
		}
		else if (this.plyrDisplayPosnX >= (this.getWidth() - this.blockSize)) {
			this.plyrDisplayPosnX = (int) Math.round(this.getWidth() - this.blockSize);
		}
		else if (this.plyrDisplayPosnX <= this.blockSize * -1) {
			this.model.gamePausedOn();
			this.model.gameLostOn();
		}
	}
	
	private void checkCollisions() {
		boolean collided = false;
		int width = 0;
		int height = 0;
		
		width = (int) Math.round(this.blockSize);
		height = width;
		Rectangle plyrBound = new Rectangle(this.plyrDisplayPosnX, this.plyrDisplayPosnY, width, height);
		
		for (int i = 0; i < this.model.getnumObstacles(); i++) {
			width = (int) Math.round(this.obstaclesDisplayPosn.get(i)[2] - this.obstaclesDisplayPosn.get(i)[0] + this.blockSize);
			height = (int) Math.round(this.obstaclesDisplayPosn.get(i)[3] - this.obstaclesDisplayPosn.get(i)[1] + this.blockSize);
			Rectangle obstacleBound = new Rectangle(this.obstaclesDisplayPosn.get(i)[0], this.obstaclesDisplayPosn.get(i)[1], width, height);
			
			if (plyrBound.intersects(obstacleBound) == true) {
				collided = true;
				break;
			}
		}
		
		if (collided == true) {
			this.model.gamePausedOn();
			this.model.gameLostOn();
		}
	}
	
	private void checkReached() {
		int width = 0;
		int height = 0;
		
		width = (int) Math.round(this.blockSize);
		height = width;
		Rectangle plyrBound = new Rectangle(this.plyrDisplayPosnX, this.plyrDisplayPosnY, width, height);
		
		width = (int) Math.round(this.blockSize/4);
		height = (int) Math.round(this.model.getvertBlockNum() * this.blockSize);
		Rectangle finishLineBound = new Rectangle(this.finishLineDisplayPosnX, 0, width, height);
		
		if (plyrBound.intersects(finishLineBound) == true) {
			this.model.gamePausedOn();
			this.model.gameWonOn();
		}
	}
	
	private void updateScore() {
		if (gameScore < this.model.getnumObstacles() && this.plyrDisplayPosnX >= this.obstaclesDisplayPosn.get(gameScore)[2]) {
			this.model.addgameScore();	
		}
 	}
	
// ======================= Controllers =======================
	private void moveUp() {
		this.plyrDisplayPosnY -= this.scrollSpeed;
	}
	private void moveDown() {
		this.plyrDisplayPosnY += this.scrollSpeed;
	}
	private void moveForward() {
		this.plyrDisplayPosnX += this.scrollSpeed * 2;
	}
	private void moveBackward() {
		this.plyrDisplayPosnX -= this.scrollSpeed * 2;
	}
		
	private void registerControllers() {
		this.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				int keyCode = e.getKeyCode();
				
				switch (keyCode) {
				case KeyEvent.VK_W:
					moveUp();
					break;
				case KeyEvent.VK_S:
					moveDown();
					break;
				case KeyEvent.VK_A:
					moveBackward();
					break;
				case KeyEvent.VK_D:
					moveForward();
					break;
				case KeyEvent.VK_SPACE:
					model.gamePausedOn();
		    		openPausedDialog();
					break;
				}
			}		
		});
		
		this.helpButton.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				model.gamePausedOn();
				openHelpDialog();
			}
		});
		
		this.setFPSButton.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				model.gamePausedOn();
				openSetFPSDialog();
			}
		});
		
		this.setSpeedButton.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				model.gamePausedOn();
				openSetSpeedDialog();
			}
		});
	}

// ======================= Adjust graphics positions =======================	
	private void setPlayerPosn() {
		double plyrPosnX = this.plyrDisplayPosnX / this.blockSize;
		double plyrPosnY = this.plyrDisplayPosnY / this.blockSize;
		this.model.setplyrPosnX(plyrPosnX);
		this.model.setplyrPosnY(plyrPosnY);
	}
	
	private void adjustPlayerPosn() {
		this.plyrDisplayPosnX = (int) Math.round(this.model.getplyrPosnX() * this.blockSize);
		this.plyrDisplayPosnY = (int) Math.round(this.model.getplyrPosnY() * this.blockSize);
	}
	
	private void setFinishLinePosnX() {
		double finishLinePosnX = this.finishLineDisplayPosnX / this.blockSize;
		this.model.setfinishLinePosnX(finishLinePosnX);
	}
	
	private void adjustFinishLinePosnX() {
		this.finishLineDisplayPosnX = (int) Math.round(this.model.getfinishLinePosnX() * this.blockSize);
	}
	
	private void setObstaclesPosnX() {
		for (int i = 0; i < this.model.getnumObstacles(); i++) {
			double top_left_x = this.obstaclesDisplayPosn.get(i)[0] / this.blockSize;
			double bottom_right_y = this.obstaclesDisplayPosn.get(i)[2] / this.blockSize;
			this.model.settopleftX(i, top_left_x);
			this.model.setbottomrightX(i, bottom_right_y);
		}
	}
	
	private void adjustObstaclesPosn() {
		for (int i = 0; i < this.model.getnumObstacles(); i++) {
			this.obstaclesDisplayPosn.get(i)[0] = (int) Math.round(this.model.gettopleftX(i) * this.blockSize);
 			this.obstaclesDisplayPosn.get(i)[1] = (int) Math.round(this.model.gettopleftY(i) * this.blockSize);
 			this.obstaclesDisplayPosn.get(i)[2] = (int) Math.round(this.model.getbottomrightX(i) * this.blockSize);
			this.obstaclesDisplayPosn.get(i)[3] = (int) Math.round(this.model.getbottomrightY(i) * this.blockSize);
		}
	}
	
	private void adjustSize() {
		this.displayHeight = (this.getHeight() - (this.getHeight())/6);
		this.blockSize = this.displayHeight / this.model.getvertBlockNum();
		this.scrollSpeed = (int) Math.round(this.blockSize/this.model.getfps()) * this.model.getspeed();
		this.adjustPlayerPosn();
		this.adjustFinishLinePosnX();
		this.adjustObstaclesPosn();
	}
	
	private void adjustLayout() { 
        this.setLayout(this.displayLayout);
        
        this.scoreLabel.setText("Score: " + this.model.getgameScore());
        this.displayLayout.putConstraint(SpringLayout.NORTH, this.scoreLabel, this.displayHeight, SpringLayout.NORTH, this);
        this.displayLayout.putConstraint(SpringLayout.WEST, this.scoreLabel, 0, SpringLayout.WEST, this);
        
        this.fpsLabel.setText("FPS: " + this.model.getfps());
        this.displayLayout.putConstraint(SpringLayout.NORTH, this.fpsLabel, 0, SpringLayout.SOUTH, this.scoreLabel);
        this.displayLayout.putConstraint(SpringLayout.WEST, this.fpsLabel, 0, SpringLayout.WEST, this);
        
        this.speedLabel.setText("Speed: " + this.model.getspeed());
        this.displayLayout.putConstraint(SpringLayout.NORTH, this.speedLabel, 0, SpringLayout.SOUTH, this.fpsLabel);
        this.displayLayout.putConstraint(SpringLayout.WEST, this.speedLabel, 0, SpringLayout.WEST, this);
        
        this.displayLayout.putConstraint(SpringLayout.NORTH, this.helpButton, this.displayHeight, SpringLayout.NORTH, this);
        this.displayLayout.putConstraint(SpringLayout.EAST, this.helpButton, 0, SpringLayout.EAST, this);
        
        this.displayLayout.putConstraint(SpringLayout.NORTH, this.setFPSButton, this.displayHeight, SpringLayout.NORTH, this);
        this.displayLayout.putConstraint(SpringLayout.EAST, this.setFPSButton, 0, SpringLayout.WEST, this.helpButton);
		
		this.displayLayout.putConstraint(SpringLayout.NORTH, this.setSpeedButton, this.displayHeight, SpringLayout.NORTH, this);
		this.displayLayout.putConstraint(SpringLayout.EAST, this.setSpeedButton, 0, SpringLayout.WEST, this.setFPSButton);
	}
	
// ======================= Dialog functions =======================
	private void openHelpDialog() {
		String controlmsg = "Controls are:\n" + "     W: Up\n" + "     S: Down\n" + "     D: Forward\n" + "     A: Backward\n" + "     Space: Pause\n\n";
		
		String msg = controlmsg;
		String title = "Help";
		JOptionPane.showMessageDialog(null, msg, title, JOptionPane.INFORMATION_MESSAGE);
		this.model.gamePausedOff();
	}
	private void openSetFPSDialog() {
		String msg = "Input new FPS (1 - 60)";
		String title = "Set FPS";
		
		String reply = JOptionPane.showInputDialog(null, msg, title, JOptionPane.QUESTION_MESSAGE);
		
		try {
			int replyInt = Integer.parseInt(reply);
			if (replyInt >= 1 && replyInt <= 60) {
				this.model.setfps(replyInt);
				this.model.gamePausedOff();
			}
			else {
				this.openSetFPSDialog();
			}
		}
		catch(NumberFormatException nferror) {
			if (reply == null || reply == Integer.toString(JOptionPane.CLOSED_OPTION)) { 
				this.model.gamePausedOff();
			}
			else {
				this.openSetFPSDialog();
			}
		}
	}
	private void openSetSpeedDialog() {
		String msg = "Input new speed (1 - 4)";
		String title = "Set Speed";
		
		String reply = JOptionPane.showInputDialog(null, msg, title, JOptionPane.QUESTION_MESSAGE);
		
		try {
			int replyInt = Integer.parseInt(reply);
			if (replyInt >= 1 && replyInt <= 4) {
				this.model.setspeed(replyInt);
				this.model.gamePausedOff();
			}
			else {
				this.openSetSpeedDialog();
			}
		}
		catch(NumberFormatException nferror) {
			if (reply == null || reply == Integer.toString(JOptionPane.CLOSED_OPTION)) { 
				this.model.gamePausedOff();
			}
			else {
				this.openSetSpeedDialog();
			}
		}
	}
	private void openQuitDialog() {
		String msg = "You are quitting the current game.\n Do you want to save your game?";
		String title = "Save Game?";
		
		int reply = JOptionPane.showOptionDialog(null, msg, title, JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, null, null);
		
		if (reply == 0) {
			if (this.model.saveGameModel() == true) {
				this.model.gameResetOn();
			}
			else  {
				this.openQuitDialog();
			}
		}
		else if (reply == 1) {
			this.model.gameResetOn();
		}
		else {
			this.openPausedDialog();
		} 
	}
	private void openPausedDialog() {
		String msg = "Game is paused. What is your option?";
		String title = "Game Paused";
		String[] options = {"Resume", "Quit"};
		
		int reply = JOptionPane.showOptionDialog(null, msg, title, JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, null);
		
		if (reply == 0 || reply == JOptionPane.CLOSED_OPTION) {
			this.model.gamePausedOff();
		}
		else {
			this.openQuitDialog();
		}
	}
	private void openLostDialog() {
		String msg = "The spaceship is destroyed!\n" +  "Do you want to restart?";
		String title = "You Lost!";
		
		int reply = JOptionPane.showOptionDialog(null, msg, title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
		
		if (reply == 0) {
			this.model.restartGameModel();
			this.setupGameDisplay();
			this.model.gamePausedOff();
		}
		else {
			this.model.gameResetOn();
		}
	}
	private void openWonDialog() {
		String msg = "Congratulations!\n" +  "You reached the end of the world!\n" + "Do you want to head to another world?";
		String title = "You Won!";
		
		int reply = JOptionPane.showOptionDialog(null, msg, title, JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
		
		if (reply == 0) {
			this.model.startGameModel();
			this.setupGameDisplay();
			this.model.gamePausedOff();
		}
		else {
			this.model.gameResetOn();
		}
	}
	
// ======================= Update functions =======================
    public void update(Object observable) {
    	this.gameScore = this.model.getgameScore();
    	this.gamePaused = this.model.getgamePaused();
    	this.gameLost = this.model.getgameLost();
    	this.gameWon = this.model.getgameWon();
    	this.gameReset = this.model.getgameReset();
    	
    	if (this.gamePaused == true) {
    		this.animationTimer.stop();
    	}
    	else {
    		this.animationTimer.start();
    		this.requestFocusInWindow();
    	}
    	
    	if (this.gameReset == true) {
    		this.model.gameResetOff();
			this.mainView.switchtotitleView();
    	}
    	
    	if (this.gameLost == true) {
    		this.openLostDialog();
    	}
    	else if (this.gameWon == true) {
    		this.openWonDialog();
    	}
    }
}
