import java.util.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.io.*;

public class Model {
    /** The observers that are watching this model for changes. */
    private ArrayList<Observer> observers;
    private ArrayList<double[]> obstaclesPosn;
    
    String filePathLoaded;
    String filePathSaved;

    private double horiBlockNum;
    private double vertBlockNum;
    
    private double plyrPosnX;
    private double plyrPosnY;
    
    private double finishLinePosnX;
    
    private int numObstacles;
     
    private int fps;
    private int speed;
    
    private int gameScore;
    private boolean gameFileFound; 
    private boolean gamePaused;
    private boolean gameLost;
    private boolean gameWon;
    private boolean gameReset;

    /**
     * Create a new model.
     */
    public Model() {
        this.observers = new ArrayList<Observer>();
        this.obstaclesPosn = new ArrayList<double[]>();
        
        this.filePathLoaded = "";
        this.filePathSaved = "";
        
    	this.horiBlockNum = 0;
    	this.vertBlockNum = 0;
    	
    	this.plyrPosnY = 0;
    	this.plyrPosnX = 0;
    	
    	this.finishLinePosnX = 0;
    	
    	this.numObstacles = 0;
    	
        this.fps = 0;
    	this.speed = 0;
    	
    	this.gameScore = 0;
    	this.gameFileFound = false;
    	this.gamePaused = true;
    	this.gameLost = false;
    	this.gameWon = false;
    	this.gameReset = false;
    }

    /**
     * Add an observer to be notified when this model changes.
     */
    public void addObserver(Observer observer) {
        this.observers.add(observer);
    }

    /**
     * Remove an observer from this model.
     */
    public void removeObserver(Observer observer) {
        this.observers.remove(observer);
    }

    /**
     * Notify all observers that the model has changed.
     */
    public void notifyObservers() {
        for (Observer observer: this.observers) {
            observer.update(this);
        }
    }
 
    public void startGameModel() {
    	JFileChooser jfc = new JFileChooser();
    	jfc.setCurrentDirectory(new File(System.getProperty("user.dir") + "/level"));
    	jfc.setFileFilter(new FileNameExtensionFilter("*.txt", "txt"));
    	jfc.setMultiSelectionEnabled(false);
    	int reply = jfc.showOpenDialog(null);
    	
    	if (reply == JFileChooser.APPROVE_OPTION) {
    		this.filePathLoaded = jfc.getSelectedFile().getAbsolutePath();
    		this.resetGameModel(); // for starting a new game after winnning
    		this.readGameModel(this.filePathLoaded);
    		this.gameFileFound = true;
    	}
    	else {
    		this.gameFileFound = false;
    	}
    	this.notifyObservers();
    }
    
    public boolean saveGameModel() {
    	JFileChooser jfc = new JFileChooser();
    	jfc.setCurrentDirectory(new File(System.getProperty("user.dir") + "/level"));
    	jfc.setFileFilter(new FileNameExtensionFilter("*.txt", "txt"));
    	jfc.setMultiSelectionEnabled(false);
    	int reply = jfc.showSaveDialog(null);
    	
    	if (reply == JFileChooser.APPROVE_OPTION) {
    		this.filePathSaved = jfc.getSelectedFile().getAbsolutePath();
    		
    		if (this.filePathSaved.endsWith(".txt")) {
    			return this.writeGameModel(this.filePathSaved);
    		}
    		else {
    			return this.writeGameModel(this.filePathSaved + ".txt");
    		}
    	}
    	else {
    		return false;
    	}
    }
    
    public void restartGameModel() {
    	this.resetGameModel();
    	this.readGameModel(this.filePathLoaded);
    }
    
    private void readGameModel(String fileDir) {
    	
    	try {
    		BufferedReader br = new BufferedReader(new FileReader(fileDir));
    		String readLine;
    		int count = 0;
    		
    		readLine = br.readLine();
    		while (readLine != null) {
    			if (readLine.charAt(0) != '#') {
    				String[] splited = readLine.split(" ");
    				if (count == 0) {
    					this.horiBlockNum = Double.parseDouble(splited[0]);
    					this.vertBlockNum = Double.parseDouble(splited[1]);
    				}
    				else if (count == 1) {
    					if (this.fps == 0 && this.speed == 0) {
    						this.fps = Integer.parseInt(splited[0]);
    						this.speed = Integer.parseInt(splited[1]);
    					}
    				}
    				else if (count == 2) {
    					this.plyrPosnX = Double.parseDouble(splited[0]);
    					this.plyrPosnY = Double.parseDouble(splited[1]);
    				}
    				else if (count == 3) {
    					this.finishLinePosnX = Double.parseDouble(splited[0]);
    				}
    				else {
    					double top_left_x = Double.parseDouble(splited[0]);
    					double top_left_y = Double.parseDouble(splited[1]);
    					double bottom_right_x = Double.parseDouble(splited[2]);
    					double bottom_right_y = Double.parseDouble(splited[3]);
    				
    					double[] dimensions = {top_left_x, top_left_y, bottom_right_x, bottom_right_y};
    					this.obstaclesPosn.add(dimensions);
    				
    				}
    				count++;
    			}
    			readLine = br.readLine();
    		}
    		br.close();
    	}
    	catch (IOException ioerror) {
    		ioerror.printStackTrace();
    		System.out.println("File cannot be opened. Terminating the program..");
    		System.exit(0);
    	}
    	catch (NumberFormatException nferror) {
    		System.out.println("Wrong file format. Terminating the program..");
    		System.exit(0);
    	}
    	// Initializes necessary variables
    	this.numObstacles = this.obstaclesPosn.size();
    }
    
    private boolean writeGameModel(String fileDir) {
    	try {
    		BufferedWriter bw = new BufferedWriter(new FileWriter(fileDir));
    		
    		bw.write(this.horiBlockNum + " " + this.vertBlockNum);
    		bw.newLine();
    		bw.write(this.fps + " " + this.speed);
    		bw.newLine();
    		bw.write(this.plyrPosnX + " " + this.plyrPosnY);
    		bw.newLine();
    		bw.write(this.finishLinePosnX + "");
    		bw.newLine();
    		
    		for (int i = 0; i < this.numObstacles - 1; i++) {
    			bw.write(this.gettopleftX(i) + " " +  this.gettopleftY(i) + " " + this.getbottomrightX(i) + " " + this.getbottomrightY(i));
    			bw.newLine();
    		}
    		bw.write(this.gettopleftX(this.numObstacles - 1) + " " +  this.gettopleftY(this.numObstacles - 1) + " " + this.getbottomrightX(this.numObstacles - 1) + " " + this.getbottomrightY(this.numObstacles - 1));
    		
    		bw.close();
    		return true;
    	}
    	catch (IOException ioerror) {
    		ioerror.printStackTrace();
    		System.out.println("File cannot be written. Reloading dialog..");
    		return false;
    	}
    }
    
    private void resetGameModel() {
    	this.obstaclesPosn.clear();
    	this.horiBlockNum = 0;
    	this.vertBlockNum = 0;
    	this.plyrPosnY = 0;
    	this.plyrPosnX = 0;
        this.numObstacles = 0;
    	this.gameScore = 0;
    	this.gamePaused = true;
    	this.gameLost = false;
    	this.gameWon = false;
    	
    	if (this.gameReset == true) {
    		this.fps = 0;
    		this.speed = 0;
    		this.gameFileFound = false;
    	}
    	else {
    		this.gameReset = false;
    	}
    }
    
    // list of accessor functions for GameView
    public double gettopleftX(int index) {
    	return this.obstaclesPosn.get(index)[0];
    }
    public double gettopleftY(int index) {
    	return this.obstaclesPosn.get(index)[1];
    }
    public double getbottomrightX(int index) {
    	return this.obstaclesPosn.get(index)[2];
    }
    public double getbottomrightY(int index) {
    	return this.obstaclesPosn.get(index)[3];
    }
    public double gethoriBlockNum() {
    	return this.horiBlockNum;
    }
    public double getvertBlockNum() {
    	return this.vertBlockNum;
    }
    public double getplyrPosnX() {
    	return this.plyrPosnX;
    } 
    public double getplyrPosnY() {
    	return this.plyrPosnY;
    } 
    public double getfinishLinePosnX() {
    	return this.finishLinePosnX;
    }
    public int getnumObstacles() {
    	return this.numObstacles;
    }
    public int getfps() {
    	return this.fps;
    }
    public int getspeed() {
    	return this.speed;
    }
    public int getgameScore() {
    	return this.gameScore;
    }
    public boolean getgameFileFound() {
    	return this.gameFileFound;
    }
    public boolean getgamePaused() {
    	return this.gamePaused;
    }
    public boolean getgameLost() {
    	return this.gameLost;
    }
    public boolean getgameWon() {
    	return this.gameWon;
    }
    public boolean getgameReset() {
    	return this.gameReset;
    }
    
    // list of mutator functions for GameView
    public void settopleftX(int index, double value) {
    	this.obstaclesPosn.get(index)[0] = value;
    }
    public void settopleftY(int index, double value) {
    	this.obstaclesPosn.get(index)[1] = value;
    }
    public void setbottomrightX(int index, double value) {
    	this.obstaclesPosn.get(index)[2] = value;
    }
    public void setbottomrightY(int index, double value) {
    	this.obstaclesPosn.get(index)[3] = value;
    }
    public void setplyrPosnX(double value) {
    	this.plyrPosnX = value;
    }
    public void setplyrPosnY(double value) {
    	this.plyrPosnY = value;
    }
    public void setfinishLinePosnX(double value) {
    	this.finishLinePosnX = value;
    }
    public void setfps(int value) {
    	this.fps = value;
    }
    public void setspeed(int value) {
    	this.speed = value;
    }
    public void addgameScore() {
    	this.gameScore++;
    	this.notifyObservers();
    }
    public void gamePausedOn() {
    	this.gamePaused = true;
    	this.notifyObservers();
    }
    public void gamePausedOff() {
    	this.gamePaused = false;
    	this.notifyObservers();
    }
    public void gameLostOn() {
    	this.gameLost = true;
    	this.notifyObservers();
    }
    public void gameWonOn() {
    	this.gameWon = true;
    	this.notifyObservers();
    }
    public void gameResetOn() {
    	this.gameReset = true;
    	this.resetGameModel();
    	this.notifyObservers();
    }
    public void gameResetOff() {
    	this.gameReset = false;
    }
 }


