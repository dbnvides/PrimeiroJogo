package com.dbstudio.main;

	import java.awt.Canvas;
	import java.awt.Color;
	import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.awt.image.DataBufferInt;
import javax.swing.JFrame;

import com.dbstudio.entities.BulletShoot;
import com.dbstudio.entities.Enemy;
import com.dbstudio.entities.Entity;
import com.dbstudio.entities.Player;
import com.dbstudio.graficos.Spritesheet;
import com.dbstudio.graficos.UI;
import com.dbstudio.world.Camera;
import com.dbstudio.world.World;

	public class Game extends Canvas implements Runnable,KeyListener, MouseListener, MouseMotionListener {

		
		private static final long serialVersionUID = 1L;
		public static JFrame frame;
		private Thread thread;
		private boolean isRunning = true;
		public static final int WIDTH = 320;
		public static final int HEIGHT = 240;
		public static final int SCALE = 3;
		
		private int CUR_LEVEL = 1, MAX_LEVEL = 2;
		private BufferedImage image;
		
		public static List<Entity> entities;
		public static List<Enemy> enemies;
		public static List<BulletShoot> bullets;
		public static Spritesheet spritesheet;

		public static World world;
		
		public static Player player;
		
		public static Random rand;
		
		public UI ui;
		
		//public InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream("pixelfont.ttf");
		//public Font  newfont;
		
		public static String gameState = "MENU"; 
		private boolean showMessageGameOver = true;
		private int framesGameOver = 0;
		private boolean restartGame = false;
		
		public Menu menu;
		
		public boolean saveGame = false;
		
		public int mx, my;
		
		public int[] pixels;
		
		public int xx,yy;
		
		public Game() {
			//Sound.musicBackground.loop();
			rand = new Random();
			addKeyListener(this);
			addMouseListener(this);
			addMouseMotionListener(this);
			setPreferredSize(new Dimension(WIDTH*SCALE,HEIGHT*SCALE));
			initFrame();
			
			//Inicializando objetos.
			ui = new UI();
			image = new BufferedImage(WIDTH,HEIGHT, BufferedImage.TYPE_INT_RGB);
			pixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();
			entities = new ArrayList<Entity>();
			enemies = new ArrayList<Enemy>();
			bullets = new ArrayList<BulletShoot>();
			
			
			spritesheet = new Spritesheet("/spritesheet.png");
			player = new Player(0,0,16,16,spritesheet.getSprite(32, 0, 16, 16));
			entities.add(player);
			world = new World("/level1.png");
			
			/*try {
				newfont = Font.createFont(Font.TRUETYPE_FONT, stream).deriveFont(50f);
			} catch (FontFormatException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}*/
			menu = new Menu();
			
		}
		
		public void initFrame() {
			frame = new JFrame("Game #1");
			frame.add(this);
			frame.pack();
			frame.setResizable(false);
			frame.setLocationRelativeTo(null);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setVisible(true);	
			return;
		}
			
		
		public synchronized void start(){
			thread = new Thread(this);
			isRunning = true;
			thread.start();
			
		}
		
		public synchronized void stop(){
			isRunning = false;
		}

		public static void main(String[] args) {
			Game game = new Game();
			game.start();
		}
		
		public void tick(){
			if(gameState == "NORMAL") {
				xx++;
				if(this.saveGame) {
					this.saveGame = false;
					String[] opt1 = {"level", "vida"};
					int[] opt2 = {this.CUR_LEVEL,(int) player.life};
					Menu.saveGame(opt1,opt2,10);
					System.out.println("O mundo foi salvo");
				}
				this.restartGame = false;
			for(int i = 0; i < entities.size(); i++) {
				Entity e = entities.get(i);
				e.tick();
				
			}
			
			for(int i = 0; i < bullets.size(); i++) {
				bullets.get(i).tick();
			}
			
			if(enemies.size() == 0) {
				//proximo level
				CUR_LEVEL++;
				if(CUR_LEVEL > MAX_LEVEL) {
					CUR_LEVEL = 1;
				}
				String newWorld = "level"+CUR_LEVEL+".png";
				World.restarGame(newWorld);
			}
			
			}else if(gameState == "GAME OVER") {
				this.framesGameOver++;
				if(this.framesGameOver == 30) {
					this.framesGameOver = 0;
					if(this.showMessageGameOver)
						this.showMessageGameOver = false;
						else
							this.showMessageGameOver = true;
			
			}
			}
			if(restartGame) {
				this.restartGame = false;
				this.gameState = "NORMAL";
				CUR_LEVEL = 1;
				String newWorld = "level"+CUR_LEVEL+".png";
				World.restarGame(newWorld);
				}
			else if(gameState =="MENU"){
				//
				menu.tick();
			}
			
		}
		
		public void drawRectangleExemple(int xoff, int yoff) {
			for(int xx = 0; xx < 32; xx++){
				for(int yy = 0; yy < 32; yy++){
					int xOff = xx + xoff;
					int yOff = yy + yoff;
					if(xOff < 0 || yOff < 0 || xOff >= WIDTH || yOff >= WIDTH)
						continue;
					pixels[xOff + (yOff*WIDTH)] = 0xff0000;
				}
			}
		}
		
		public void render(){
			BufferStrategy bs = this.getBufferStrategy();
			if(bs == null) {
				this.createBufferStrategy(3);
				return;
			}
			Graphics g = image.getGraphics();
			g.setColor(new Color(0,0,0));
			g.fillRect(0, 0, WIDTH,HEIGHT);
			
			//Renderização do jogo
			world.render(g);
			for(int i = 0; i < entities.size(); i++) {
				Entity e = entities.get(i);
				e.render(g);
			}
			for(int i = 0; i < bullets.size(); i++) {
				bullets.get(i).render(g);
			}
			ui.render(g);
			g.dispose();
			g = bs.getDrawGraphics();
			drawRectangleExemple(xx,yy);
			g.drawImage(image, 0, 0, WIDTH*SCALE, HEIGHT*SCALE, null);
			g.setFont(new Font("arial", Font.BOLD,20));
			g.setColor(Color.white);
			g.drawString("Munição: "+ player.ammo , 600,20);
			if(gameState == "GAME_OVER") {
				Graphics2D g2 = (Graphics2D) g;
				g2.setColor(new Color(0,0,0,100));
				g2.fillRect(0, 0, WIDTH*SCALE, HEIGHT*SCALE);
				g.setFont(new Font("arial", Font.BOLD,50));
				g.setColor(Color.white);
				g.drawString("Game Over ", (WIDTH*SCALE)  / 2 - 140,(HEIGHT*SCALE) / 2 + 30);
				g.setFont(new Font("arial", Font.BOLD,20));
				g.setColor(Color.white);
				if(showMessageGameOver) 
				g.drawString("> Pressione ENTER para reiniciar < ", (WIDTH*SCALE)  / 2 - 171,(HEIGHT*SCALE) / 2 + 100);
				
			}else if(gameState == "MENU"){
				menu.render(g);
			}
			//Seguindo a direcção em que o mouse esta.
			/*Graphics2D g2 = (Graphics2D) g;
			double angleMouse = Math.atan2(200+25 - my  , 200+25 - mx);
			g2.rotate(angleMouse, 200+25, 200+25);
			g.setColor(Color.red);
			g.fillRect(200, 200, 50, 50);
			
			/*g.setFont(newfont);
			g.setColor(Color.red);
			g.drawString("Testando a fonte", 90, 50);
			*/
			
			bs.show();
			
		}
		
		
		public void run() {
			long lastTime = System.nanoTime();
			double amountOfTicks = 60.0;
			double ns = 1000000000 / amountOfTicks;
			double delta = 0;
			int frames = 0;
			double timer = System.currentTimeMillis();
			requestFocus();
			while(isRunning) {
				long now = System.nanoTime();
				delta+= (now - lastTime) / ns;
				lastTime = now;
				if(delta >= 1) {
					tick();
					render();
					frames++;
					delta--;
				}
				
				if(System.currentTimeMillis() - timer >= 1000) {
					System.out.println("FPS: "+ frames);
					frames = 0;
					timer+=1000;
				}
			}
			
			stop();
		}

		//Executa a ação de andar 
		public void keyPressed(KeyEvent e) {
			if(e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D){
				Player.right = true;
			}else if(e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A){
				Player.left = true;
			}
			if(e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W){
				Player.up = true;
				if(gameState == "MENU") {
					menu.up = true;
				}
			}
			else if(e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S){
				Player.down = true;
				if(gameState == "MENU") {
					menu.down = true;
				}
			}
			if(e.getKeyCode() == KeyEvent.VK_SPACE) {
				player.shoot = true;
			}
			if(e.getKeyCode() == KeyEvent.VK_ENTER) {
				this.restartGame = true;
				if(gameState == "MENU") {
					menu.enter = true;
				}if(gameState == "MENU") {
					this.restartGame = false;
				}
			}
			if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
				gameState = "MENU";
				menu.pause = true;
			}
			
			if(e.getKeyCode() == KeyEvent.VK_E) {
				this.saveGame = true;
				if(gameState == "NORMAL")
					this.saveGame = true;
			}
			
			
		}

		@Override
		public void keyReleased(KeyEvent e) {
			if(e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D){
				Player.right = false;
			}else if(e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A){
				Player.left = false;
			}
			if(e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W){
				Player.up = false;
			}
			else if(e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S){
				Player.down = false;
			}
			if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
				player.shoot = false;
			}
			if(e.getKeyCode() == KeyEvent.VK_ENTER){
				this.restartGame = false;
				
			}
			
			
			
			
			
		}

		@Override
		public void keyTyped(KeyEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseClicked(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseExited(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mousePressed(MouseEvent e) {
			player.mouseShoot = true;
			player.mx = (e.getX() /3);
			player.my = (e.getY() /3);
			
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseDragged(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			this.mx = e.getX();
			this.my = e.getY();
			
		}

		
		
		
	}



