package com.dbstudio.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import com.dbstudio.main.Game;
import com.dbstudio.main.Sound;
import com.dbstudio.world.Camera;
import com.dbstudio.world.World;

public class Enemy2 extends Entity {

	
	private double speed = 0.4;
	
	private int maskx = 8, masky = 8, maskw = 10, maskh = 10;
	
	private int frames = 0, maxFrames = 20, index = 0, maxIndex = 3;
	
	private BufferedImage[] sprites;
	
	private int life = 20;
 	
	private boolean isDamaged = false;
	private int damageFrames = 10,damageCurrent = 0;
	
	public Enemy2(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, null);
		sprites = new BufferedImage[4];
		sprites[0] = Game.spritesheet.getSprite(80, 32, 16, 16);
		sprites[1] = Game.spritesheet.getSprite(80+16, 32, 16, 16);
		sprites[2] = Game.spritesheet.getSprite(113, 32, 16, 16);
		sprites[3] = Game.spritesheet.getSprite(128, 32, 16, 16);
		
	}

	public void tick(){
		if(this.calculateDistance(this.getX(), this.getY(),Game.player.getX(),Game.player.getY()) < 80){
		
		if(this.isColiddingWithPlayer()== false) {
		
		if((int)x < Game.player.getX()&& World.isFree((int)(x+speed), this.getY())
				&& !isColidding((int)(x+speed), this.getY())){
			x+=speed;
		}
		else if((int)x > Game.player.getX()&& World.isFree((int)(x-speed), this.getY())
				&& !isColidding((int)(x-speed), this.getY())){
			x-=speed;
		}
		if((int)y < Game.player.getY()&& World.isFree(this.getX(), (int)(y+speed))
				&& !isColidding(this.getX(), (int)(y+speed))){
			y+=speed;
		}
		else if((int)y > Game.player.getY()&& World.isFree(this.getX(), (int)(y-speed))
				&& !isColidding(this.getX(), (int)(y-speed))){
			y-=speed;
		}
		}else {
			//Estamos colidindo
			if(Game.rand.nextInt(100) < 20){
				Sound.hurtEffect.play();
			   Game.player.life-=Game.rand.nextInt(3);
			   Game.player.isDamaged = true;
			if(Game.player.life <= -1) {
				
				//GAME OVER!
				//System.exit(1);
			}   
			   System.out.println("Vida: "+ Game.player.life);
			}
			
		}
		frames++;
		if(frames == maxFrames) {
			frames = 0;
			index++;
			if(index > maxIndex) {
				index = 0;
			}
		}
		
		collidingBullet();
		
		if(life<=0) {
			destroySelf();
			return;
		}
		
		if(isDamaged) {
			this.damageCurrent++;
			if(this.damageCurrent == this.damageFrames) {
				this.damageCurrent = 0;
				this.isDamaged = false;
			}
		}
	}
	}
	
	
	public void destroySelf() {
		Game.enemies.remove(this);
		Game.entities.remove(this);
	}
	
	public void collidingBullet(){
		for(int i = 0; i < Game.bullets.size(); i++) {
			Entity e = Game.bullets.get(i);
		if(e instanceof BulletShoot) {
			if(Entity.isColidding(this, e)) {
				isDamaged = true;
				life--;
				Game.bullets.remove(i);
				return;
			}
		   }
		}
	}

	public boolean isColiddingWithPlayer(){
		Rectangle enemy2Current = new Rectangle(this.getX() + maskx,this.getY() + masky, maskw, maskh);
		Rectangle player = new Rectangle(Game.player.getX(),Game.player.getY(),16,16);
		
		return enemy2Current.intersects(player);
	}
	
	public boolean isColidding(int xnext,int ynext){
		Rectangle enemy2Currente = new Rectangle(xnext + maskx,ynext + masky, maskw, maskh);
		for(int i = 0; i < Game.enemies.size(); i++) {
			Entity e = Game.enemies.get(i);
			if(e == this)
				continue;
			Rectangle targetEnemy2 = new Rectangle(e.getX()+ maskx,e.getY()+ masky,maskw, maskh);
			if(enemy2Currente.intersects(targetEnemy2)) {
				return true;
			}
		}
		
		return false;
	}
	public void render(Graphics g) {
		if(!isDamaged)
		g.drawImage(sprites[index],this.getX() - Camera.x, this.getY() - Camera.y, null);
		else
			g.drawImage(Entity.ENEMY2_FEEDBACK,this.getX() - Camera.x, this.getY() - Camera.y, null);
	}

	
	
}
