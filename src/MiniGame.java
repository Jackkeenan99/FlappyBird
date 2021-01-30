import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.Timer;

public class MiniGame implements ActionListener, MouseListener {

	public static MiniGame game;
	public final int WIDTH = 1500, HEIGHT=800;
	public Renderer renderer;
	public Rectangle bird;
	public int flaps, yMotion, score;
	public ArrayList<Rectangle> columns;
	public Random r;
	public boolean gameOver = false;
	public boolean started = false;

	public MiniGame() {
		JFrame jf = new JFrame();
		renderer = new Renderer();
		r = new Random();
		Timer timer = new Timer(20, this);
		jf.add(renderer);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setResizable(false); // makes JFrame non resizeable
		jf.addMouseListener(this);
		jf.setSize(WIDTH, HEIGHT);
		jf.setVisible(true);
		bird = new Rectangle(WIDTH/2-10, HEIGHT/2-10, 20,20);
		columns = new ArrayList<Rectangle>();
		addColumn(true);		// starting columns
		addColumn(true);
		addColumn(true);
		addColumn(true);
		timer.start();
	}

	public void addColumn(boolean b) {
		int space= 300;
		int width= 100;
		int height = 50 + r.nextInt(300);  // min height is 50 and max 300
		if(b) {	// preset cols
			columns.add(new Rectangle(WIDTH+width+columns.size()*300,HEIGHT - height - 150, width, height));
			columns.add(new Rectangle((WIDTH + width + (columns.size()-1)*300), 0, width, HEIGHT-height-space));
		}
		else {		//randomly generated columns
			columns.add(new Rectangle(columns.get(columns.size()-1).x + 600, HEIGHT - height - 150, width, height));
			columns.add(new Rectangle(columns.get(columns.size()-1).x, 0, width, HEIGHT-height-space));
		}
	}


	public void paint(Graphics g, Rectangle column) {
		g.setColor(Color.green.darker());
		g.fillRect(column.x, column.y, column.width, column.height);
	}
	public void repaint(Graphics g) {
		g.setColor(Color.cyan);				//background
		g.fillRect(0,0,WIDTH,HEIGHT);
		g.setColor(Color.green);			// grass
		g.fillRect(0,HEIGHT-150,WIDTH,150);
		g.setColor(Color.red);				// if game started paint the bird
		if(started) g.fillRect(bird.x,bird.y,bird.width, bird.height);

		for(Rectangle column : columns) {
			paint(g, column);
		}
		g.setColor(Color.white);
		g.setFont(new Font("Arial",1,100));
		if(gameOver) {
			g.drawString("Game Over!",(WIDTH/2)/2 + 50, HEIGHT/2);
		}
		if(!started) {
			g.drawString("Click to start",(WIDTH/2)/2, HEIGHT/2);
		}
		g.drawString(String.valueOf(score),(WIDTH/2)-100, HEIGHT/4);

	}


	public static void main(String[] args) {
		game = new MiniGame();

	}
	public void higher() {  // if click on a game over reset game
		if(gameOver) {
			bird = new Rectangle(WIDTH/2-10, HEIGHT/2-10, 20,20);
			columns = new ArrayList<Rectangle>();
			yMotion = 0;
			score = 0;
			addColumn(true);
			addColumn(true);
			addColumn(true);
			addColumn(true);
			gameOver = false;
		}
		if(!started) {
			started = true;
		}
		if(!gameOver) {
			if(yMotion > 0) {
				yMotion = 0;
			}
			yMotion -=10;

		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		int speed=10;

		if(started) {
			for(int i=0; i<columns.size(); i++) { 	// move columns
				Rectangle rect = columns.get(i);
				rect.x -= speed;
			}
			for(int i=0; i<columns.size(); i++) { // remove column when gone of screen
				Rectangle rect = columns.get(i);
				if(rect.x + rect.width < 0) {
					columns.remove(rect);
					if(rect.y==0) {  // only want one col as they come in pairs
						addColumn(false);
					}
				}
			}
			flaps ++;
			if (flaps%2 == 0 && yMotion < 15) { // gravity of bird
				yMotion = yMotion + 2;
			}
			bird.y += yMotion;

			for( Rectangle rect: columns) { 
				if(rect.y == 0 && bird.x > rect.x + (rect.width) - speed && bird.x < (rect.x + rect.width) + speed)
				{ // add to score when bird is past tube -- take into account speed 
					score++;
					System.out.print(score);
				}
				if(rect.intersects(bird)) {
					gameOver = true;  // if a bird intersects a column it dies
					bird.x = rect.x - bird.width;
				}
			}
			if(bird.y > HEIGHT - 120 || bird.y < 0 ) { // check if bird is out of bounds
				gameOver = true;
			}
			if(bird.y + yMotion>=HEIGHT - 150) {
				bird.y = HEIGHT - 150 - bird.height;
			}
		}
		renderer.repaint();

	}

	@Override
	public void mouseClicked(MouseEvent e) {}
	@Override
	public void mousePressed(MouseEvent e) {higher();}
	@Override
	public void mouseReleased(MouseEvent e) {}
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
}
