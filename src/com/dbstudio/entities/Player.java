package com.dbstudio.entities;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.dbstudio.graficos.Spritesheet;
import com.dbstudio.main.Game;
import com.dbstudio.world.Camera;
import com.dbstudio.world.World;

public class Player extends Entity {

	//Declarando as direções.
	public int right_dir = 0, left_dir = 1;
	public int dir = right_dir;
	public static boolean right;
	public static boolean up;
	public static boolean left;
	public static boolean down;
	public double speed = 1.4;
	
	//Colocando animações.
	private int frames = 1, maxFrames = 5, index = 0, maxIndex = 3;
	private boolean moved = false;
	private BufferedImage[] rightPlayer;
	private BufferedImage[] leftPlayer;
	
	private BufferedImage playerDamage;
	
	private boolean arma = false;
	
	public boolean isDamaged = false;
	private int damageFrames = 0;
	
	public boolean shoot = false, mouseShoot = false;
	
	public int ammo = 0;
	
	public double life = 100,maxlife = 100;
	
	public int mx,my;
	
	/*Dando propiedades ao player para a movimentações*/
	public Player(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, sprite);
		
		rightPlayer = new BufferedImage[4];
		leftPlayer = new BufferedImage[4];
		playerDamage = Game.spritesheet.getSprite(0, 16, 16, 16);
		for(int i=0; i < 4; i++){
		rightPlayer[i] = Game.spritesheet.getSprite(32 + (i*16), 0, 16, 16);
		}
		for(int i=0; i < 4; i++){
			leftPlayer[i] = Game.spritesheet.getSprite(32 + (i*16), 16, 16, 16);
			}
	}

	//Movimentação do player a cada tick, colocando a movimentação false se não estiver pressionando a tecla.
	public void tick(){
		moved = false;
		if(right && World.isFree((int)(x+speed),this.getY())) {
			moved = true;
			dir = right_dir;
			x+=speed;
		}
		else if(left && World.isFree((int)(x-speed),this.getY())) {
			moved = true;
			dir = left_dir;
			x-=speed;
		}
		if(up && World.isFree(this.getX(),(int)(y-speed))) {
			moved = true;
			y-=speed;
		}
		else if(down && World.isFree(this.getX(),(int)(y+speed))) {
			moved = true;
			y+=speed;
		}
		
		//Mudando a imagem do player, animação/tempo a cada 4 frames muda de imagem do player.
		if(moved) {
			frames++;
			if(frames == maxFrames) {
				frames = 0;
				index++;
				if(index > maxIndex)
					index = 0;
			}
		}
		
		checkCollisionLifePack();
		checkCollisionAmmo();
		checkCollisionArma();
		
		//Garante que sem fica Piscando
		if(isDamaged) {
			this.damageFrames++;
			if(this.damageFrames == 8) {
				this.damageFrames = 0;
				isDamaged = false;
			}
			
			
		}
		//Condição para que se tiver arma+bullet = atirar.
		if(shoot && arma && ammo > 0) {
			ammo--;
			//Criar Bala e atirar
			shoot = false;
			int dx = 0;
			int px = 0;
			int py = 7;
			if(dir == right_dir) {
				 px = 18;
				 dx = 1;
			}else {
				 px = -8;
				 dx = -1; 
			}
			
			BulletShoot bullet = new BulletShoot(this.getX() + px, this.getY() + py,3,3,null, dx, 0);
			Game.bullets.add(bullet);
		}
		
			if(mouseShoot) {
			
			mouseShoot = false;
			
			
			if(arma && ammo > 0) {
			ammo--;
			//Criar bala e atirar!

			int px = 0,py = 8;
			double angle = 0;
			if(dir == right_dir) {
				px = 18;
				angle = Math.atan2(my - (this.getY()+py - Camera.y),mx - (this.getX()+px - Camera.x));
			}else {
				px = -8;
				angle = Math.atan2(my - (this.getY()+py - Camera.y),mx - (this.getX()+px - Camera.x));
			}
			
			double dx = Math.cos(angle);
			double dy = Math.sin(angle);
			
			BulletShoot bullet = new BulletShoot(this.getX()+px,this.getY()+py,3,3,null,dx,dy);
			Game.bullets.add(bullet);
			}
		}
		
		
		if(life <= 0) {
			//Game OVER
			life = 0;
			Game.gameState = "GAME_OVER";
		}
		
		Camera.x = Camera.clamp(this.getX() - (Game.WIDTH/2), 0, World.WIDTH*16 - Game.WIDTH);
		Camera.y = Camera.clamp(this.getY() - (Game.HEIGHT/2), 0, World.HEIGHT*16 - Game.HEIGHT);
		
		
	}
	
	//Metodo para pegar a vida
	public void checkCollisionLifePack() {
		for(int i = 0; i < Game.entities.size(); i++) {
			Entity atual = Game.entities.get(i);
			if(atual instanceof Lifepack) {
				if(Entity.isColidding(this, atual)) {
					life+=8;
					if(life >= 100)
						life = 100;
					Game.entities.remove(atual);
					return;
				}
			}
		}
		
	}

	//Metodo para pegar a Bala
	public void checkCollisionAmmo() {
		for(int i = 0; i < Game.entities.size(); i++) {
			Entity atual = Game.entities.get(i);
			if(atual instanceof Bullet) {
				if(Entity.isColidding(this, atual)) {
					ammo+=20;
					Game.entities.remove(atual);
					
				}
			}
		}
	}
	//Metodo para pegar arma
	public void checkCollisionArma() {
		for(int i = 0; i < Game.entities.size(); i++) {
			Entity atual = Game.entities.get(i);
			if(atual instanceof Weapon) {
				if(Entity.isColidding(this, atual)) {
					arma=true;
					//System.out.println("Pegou a arma");
					Game.entities.remove(atual);
					
				}
			}
		}
	}
	
	
	
	//Atribuindo direita e esquerda +dir = direção. 
	public void render(Graphics g){
		if(!isDamaged) {
		if(dir == right_dir) {
		g.drawImage(rightPlayer[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
		if(arma){
			//Desenhar arma para direita.
			g.drawImage(Entity.Gun_RIGHT, this.getX()+8 - Camera.x,this.getY() - Camera.y,null);
		}
		}else if(dir == left_dir) {
			g.drawImage(leftPlayer[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
			if(arma){
				//Desenhar arma para esquerda.
				g.drawImage(Entity.Gun_LEFT, this.getX()-8 - Camera.x,this.getY() - Camera.y,null);
			}
		}
		}else{
			g.drawImage(playerDamage, this.getX() - Camera.x,this.getY() - Camera.y, null);
		}
	}
	
	
}
