import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.util.Random;

public class SnakeGame extends JPanel implements ActionListener {
	
	private static final int SCREEN_WIDTH = 1300;
	private static final int SCREEN_HEIGHT = 750;
	private static final int UNIT_SIZE = 50;
	private static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / (UNIT_SIZE*UNIT_SIZE);
	private static final int DELAY = 165;
	private final int defaultParts = 3;
	private static boolean isRunning = false;
	private static boolean gameOn = false;
	private static boolean isRainbow = false; 
	private static boolean eatenRainbow = false;
	private int x[] = new int[GAME_UNITS];
	private int y[] = new int[GAME_UNITS];
	private int bodyParts = defaultParts;
	private int turns;
	private int applesEaten;
	private int appleX;
	private int appleY;
	private char direction = 'R';
	private static Timer timer;
	private static Random random;
	
	public SnakeGame(){
		random = new Random();
		this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
		this.setBackground(Color.BLACK);
		this.setFocusable(true);
		this.addKeyListener(new MyKeyAdapter());
		start();
	}
	public void start () {
		newApple();
		isRunning = true;
		timer = new Timer(DELAY, this);
		timer.start();
	}
	public void pause() {
		gameOn = true;
		timer.stop();
	}

	public void resume() {
		gameOn = false;
		timer.start();
	}
	public void reset() {
		applesEaten = 0;
		bodyParts = defaultParts;
		x = new int[GAME_UNITS];
		y = new int[GAME_UNITS];
		isRainbow = false;
		eatenRainbow = false;
		direction = 'R';
		turns = 0;
		repaint();
		start();
	}
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		draw(g);
	}
	public void draw(Graphics e) {
		if(isRunning) {
			for(int i = 0; i < SCREEN_HEIGHT / UNIT_SIZE; i++) {
				e.drawLine(i*UNIT_SIZE, 0, i*UNIT_SIZE, SCREEN_HEIGHT);
				e.drawLine(0, i*UNIT_SIZE, SCREEN_WIDTH, i * UNIT_SIZE);
			}
			e.setColor(Color.RED);
			e.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);
			try {
				URL appleIO;
				Image test;
				if(!isRainbow) {
					appleIO = new URL("https://i.imgur.com/Imu8yIM.png");
					test = ImageIO.read(appleIO);
					e.drawImage(test, appleX, appleY, UNIT_SIZE, UNIT_SIZE, getFocusCycleRootAncestor());
				} else {
					appleIO = new URL("https://i.imgur.com/sQ0qbpg.png");
					test = ImageIO.read(appleIO);
					e.drawImage(test, appleX, appleY, UNIT_SIZE, UNIT_SIZE, getFocusCycleRootAncestor());
				}
				} catch (MalformedURLException ed) {
				ed.printStackTrace();
			} catch (IOException ed) {
				ed.printStackTrace();
			}
			
			for(int i = 0; i< bodyParts;i++) {
				if (!eatenRainbow) {
					e.setColor(Color.green);
					e.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
				} else {
					//e.setColor(new Color(45,180,0));
					e.setColor(new Color(random.nextInt(255),random.nextInt(255),random.nextInt(255)));
					e.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
				}			
			}
			
			e.setColor(Color.YELLOW);
			e.setFont(new Font("MV Boli", Font.BOLD, 25));
			e.drawString("Snake Game. Press SPACE to pause", (SCREEN_WIDTH - getFontMetrics(e.getFont()).stringWidth("Snake Game. Press SPACE to pause"))/2, e.getFont().getSize());
			
			
			e.setColor(Color.red);
			e.setFont(new Font("MV Boli", Font.BOLD, 40));
			e.drawString("Score: "+ applesEaten, (SCREEN_WIDTH - getFontMetrics(e.getFont()).stringWidth("Score: "+applesEaten)), e.getFont().getSize());
		} else{
			gameOver(e);
		}
	}
	
	public void newApple() {
		appleX = random.nextInt((int)(SCREEN_WIDTH/UNIT_SIZE))*UNIT_SIZE;
		appleY = random.nextInt((int)(SCREEN_HEIGHT/UNIT_SIZE))*UNIT_SIZE;
		
		isRainbow = ((Math.random()* 601) >= 560) ? (true) : (false);
		
	}
	public void move(){
		for(int i = bodyParts; i > 0; i--) {
			x[i] = x[i-1];
			y[i] = y[i-1];
		}
		
		switch(direction) {
		case 'U':
			y[0] = y[0] - UNIT_SIZE;
			break;
		case 'D':
			y[0] = y[0] + UNIT_SIZE;
			break;
		case 'L':
			x[0] = x[0] - UNIT_SIZE;
			break;
		case 'R':
			x[0] = x[0] + UNIT_SIZE;
			break;
		case KeyEvent.VK_SPACE:
			if(gameOn) {
				resume();
			} else {
				pause();
			}
			break;
		}
		
	}
	public void checkApple() {
		if((x[0] == appleX && y[0] == appleY) && !isRainbow) {
			bodyParts++;
			applesEaten++;
			if(turns != 0) {
				turns--;
			}
			newApple();
		} else if((x[0] == appleX && y[0] == appleY) && isRainbow) {
			bodyParts++;
			applesEaten++;
			eatenRainbow = true;
			timer.setDelay(DELAY - 50);
			turns += 4;
			newApple();
		}
		
		if(turns == 0) {
			eatenRainbow = (turns == 0) ? (false): (true);
			timer.setDelay(DELAY);
		}
	}
	public void checkCollision() {
		//checks if head collides with body
		for(int i = bodyParts;i>0;i--) {
			if((x[0] == x[i])&& (y[0] == y[i])) {
				isRunning = false;
			}
		}
		//check if head touches left border
		if(x[0] < 0) {
			isRunning = false;
		}
		//check if head touches right border
		if(x[0] > SCREEN_WIDTH) {
			isRunning = false;
		}
		//check if head touches top border
		if(y[0] < 0) {
			isRunning = false;
		}
		//check if head touches bottom border
		if(y[0] > SCREEN_HEIGHT) {
			isRunning = false;
		}
		
		if(!isRunning) {
			timer.stop();
		}
	}
	public void gameOver(Graphics g) {
		//Score
		g.setColor(Color.red);
		g.setFont( new Font("Ink Free",Font.BOLD, 40));
		FontMetrics metrics1 = getFontMetrics(g.getFont());
		g.drawString("Score: "+ applesEaten, (SCREEN_WIDTH - metrics1.stringWidth("Score: "+applesEaten))/2, g.getFont().getSize());
		//Game Over text
		g.setColor(Color.red);
		g.setFont( new Font("Ink Free",Font.BOLD, 75));
		FontMetrics metrics2 = getFontMetrics(g.getFont());
		g.drawString("Game Over", (SCREEN_WIDTH - metrics2.stringWidth("Game Over"))/2, SCREEN_HEIGHT/2);
		
		g.setColor(Color.red);
		g.setFont( new Font("Ink Free",Font.BOLD, 75));
		FontMetrics metrics3 = getFontMetrics(g.getFont());
		g.drawString("Press ENTER to replay", (SCREEN_WIDTH - metrics3.stringWidth("Press ENTER to replay"))/2, SCREEN_HEIGHT/3);
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(isRunning) {
			move();
			checkApple();
			checkCollision();
		}
		repaint();
		
	}
	public class MyKeyAdapter extends KeyAdapter{
		@Override
		public void keyPressed(KeyEvent e) {
			switch(e.getKeyCode()) {
			case KeyEvent.VK_LEFT:
				direction = (direction != 'R') ? (direction = 'L') : direction;
				break;
			case KeyEvent.VK_RIGHT:
				direction = (direction != 'L') ? (direction = 'R') : direction;
				break;
			case KeyEvent.VK_UP:
				direction = (direction != 'D') ? (direction = 'U') : direction;
				break;
			case KeyEvent.VK_DOWN:
				direction = (direction != 'U') ? (direction = 'D') : direction;
				break;
			case KeyEvent.VK_SPACE:
				if(gameOn) {
					resume();
				} else {
					pause();
				}
				break;
			case KeyEvent.VK_ENTER:
				if(!isRunning) {
					reset();
				}
			}
		}
	}
	
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		
		try {
			URL url = new URL("https://publicdomainvectors.org/photos/snake-cobra-publicdomain.jpg");
			Image image = ImageIO.read(url);
			frame.setIconImage(image);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		frame.add(new SnakeGame());
		frame.setTitle("Snake Game");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.pack();
		frame.setVisible(true);
		frame.setLocationRelativeTo(null);
	}
}
