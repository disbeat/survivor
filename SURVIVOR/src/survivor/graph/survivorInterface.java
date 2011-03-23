/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package survivor.graph;

import java.util.Hashtable;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

import survivor.SurvivorProperties;
import survivor.ontology.BaseInfo;
import survivor.ontology.Food;
import survivor.ontology.Location;
import survivor.ontology.Person;
import survivor.ontology.ResourceIsLocated;
import survivor.ontology.Stone;
import survivor.ontology.Wood;

/**
 *
 * @author lopes
 */
public class survivorInterface extends BasicGame implements Runnable{

	private int x, y, pace = 25;
	private Image woodLogo, stoneLogo, foodLogo, grass, hq, log, logo, food, rocks,
		lumberjack, miner, hunter, seeker;
	int woodQt, stoneQt, foodQt, hqPosX, hqPosY;
	private int HEIGHT, WIDTH, posXOffset, posYOffset;
	private Hashtable<Location, ResourceIsLocated> resourcesLocations = null;
	private int pX, pY, cellSize = 50; //in pixels~
	private BaseInfo base;
	Hashtable<String, Person> agents;
	
	public survivorInterface()
	{
		super("Survivor");
		x = 101;
		y = 101;
		hqPosX=x/2;
		hqPosY=y/2;
	}

	public survivorInterface(BaseInfo base, Hashtable<Location, ResourceIsLocated> worldResources, int worldSize, Hashtable<String, Person> agents) {
		super("Survivor");
		resourcesLocations = worldResources;
		x = worldSize;
		y = worldSize;
		posYOffset = (int) Math.ceil(y/2.0);
		posXOffset = (int) Math.ceil(x/2.0);
		hqPosX = base.getLocation().getPosx()+posXOffset;
		hqPosY = base.getLocation().getPosy()+posYOffset;
		this.base = base;
		this.agents = agents;
	}

	
	public void run() {
		try {
			AppGameContainer container = new AppGameContainer(this);
			container.setDisplayMode(1024,600,false);
			container.start();
			
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
	
	public void init(GameContainer container) throws SlickException {
		//PARAMETERS OF RENDER
		container.setShowFPS(false);
		container.setTargetFrameRate(60);
		
		//INITIALIZING VARIABLES
		this.HEIGHT = container.getHeight();
		this.WIDTH = container.getWidth();

		this.pX = (hqPosX)*cellSize-WIDTH/2+cellSize/2;
		this.pY = (hqPosY)*cellSize-HEIGHT/2+cellSize/2;
		
		//LOAD IMAGES
		woodLogo = new Image("images/wood.png");
		stoneLogo = new Image("images/stone.gif");
		foodLogo = new Image("images/steak.png");
		logo = new Image("images/logo.png");
		
		//BASIC ELEMENTS
		grass = new Image("images/grass.jpg");
		hq = new Image("images/hq.gif");
		log = new Image("images/tree.png");
		rocks = new Image("images/rocks.gif");
		food = new Image("images/sheep.png");
		
		//AGENTS
		miner = new Image("images/miner.png");
		hunter = new Image("images/hunter.png");
		lumberjack = new Image("images/lumberjack.png");
		seeker = new Image("images/seeker.png");
		
		//INITIAL QUANTITIES OF RESOURCES
		woodQt = base.getWood().getAmount();
		stoneQt = base.getStone().getAmount();
		foodQt = base.getFood().getAmount();
	}

	public void update(GameContainer container, int delta) {
		woodQt = base.getWood().getAmount();
		stoneQt = base.getStone().getAmount();
		foodQt = base.getFood().getAmount();
		if (container.getInput().isKeyDown(Input.KEY_ESCAPE))
			container.exit();
		if (container.getInput().isKeyDown(Input.KEY_UP))
			this.pY -= pace;
		if (container.getInput().isKeyDown(Input.KEY_DOWN))
			this.pY += pace;
		if (container.getInput().isKeyDown(Input.KEY_RIGHT))
			this.pX += pace;
		if (container.getInput().isKeyDown(Input.KEY_LEFT))
			this.pX -= pace;
		if (container.getInput().isKeyDown(Input.KEY_SPACE))
		{
			this.pX = (hqPosX)*cellSize-WIDTH/2+cellSize/2;
			this.pY = (hqPosY)*cellSize-HEIGHT/2+cellSize/2;
		}
	}

	public void render(GameContainer container, Graphics g) {
		
		int i, j;

		for (i=0;i<x;i++)
			for (j=0;j<y;j++)
			{
				grass.draw((cellSize*i-pX),(cellSize*j-pY), cellSize, cellSize);
			}

		hq.draw((hqPosX*cellSize-pX),(hqPosY*cellSize-pY),(float)(cellSize*2),(float)(cellSize*2));
		
		Location l;
		for (ResourceIsLocated ptr : resourcesLocations.values())
		{
			l = ptr.getLocation();
			if (ptr.getResource() instanceof Wood)
			{
				log.draw(((l.getPosx()+posXOffset)*cellSize-pX),((l.getPosy()+posYOffset)*cellSize-pY),(float)(cellSize*2),(float)(cellSize*2));
			}
			else if (ptr.getResource() instanceof Stone)
			{
				rocks.draw(((l.getPosx()+posXOffset)*cellSize-pX),((l.getPosy()+posYOffset)*cellSize-pY),cellSize,cellSize);
			}
			else if (ptr.getResource() instanceof Food)
			{
				food.draw(((l.getPosx()+posXOffset)*cellSize-pX),((l.getPosy()+posYOffset)*cellSize-pY),(float)(cellSize*0.7),(float)(cellSize*0.7));
			}
		}
		float per;
		int sC=0, mC=0, lC=0, hC=0;
		for (Person p : agents.values())
		{
			l = p.getPersonLocation();
			
			per = (float) (p.getEnergy()/SurvivorProperties.MAX_ENERGY);
			g.setColor(new Color((float) (1.0 - per),(float) (0.0 + per),0));
			g.fillRect(((l.getPosx()+posXOffset)*cellSize-pX),((l.getPosy()+posYOffset)*cellSize-pY-10),(float)(cellSize-cellSize*(1-per)),3);
			if (p.getType()==Person.MINER)
			{
				miner.draw(((l.getPosx()+posXOffset)*cellSize-pX),((l.getPosy()+posYOffset)*cellSize-pY),cellSize,cellSize);
				mC++;
			}
			else if (p.getType()==Person.HUNTER)
			{
				hunter.draw(((l.getPosx()+posXOffset)*cellSize-pX),((l.getPosy()+posYOffset)*cellSize-pY),cellSize,cellSize);
				hC++;
			}
			else if (p.getType()==Person.LUMBERJACK)
			{
				lumberjack.draw(((l.getPosx()+posXOffset)*cellSize-pX),((l.getPosy()+posYOffset)*cellSize-pY),cellSize,cellSize);
				lC++;
			}
			else if (p.getType()==Person.SEEKER)
			{
				seeker.draw(((l.getPosx()+posXOffset)*cellSize-pX),((l.getPosy()+posYOffset)*cellSize-pY),cellSize,cellSize);
				sC++;
			}
		}
		
		
		Color grayAlph = new Color(Color.lightGray);
		grayAlph.a = (float) 0.5;
		
		g.setColor(grayAlph);
		g.fillRoundRect(25, HEIGHT-70, WIDTH-50, 50, 15);
		
		g.setColor(Color.black);
		logo.draw(60, HEIGHT-60, 90, 30);
		
		woodLogo.draw(170, HEIGHT-60, 30, 30);
		g.drawString(woodQt+"", 210, HEIGHT-50);

		stoneLogo.draw(260, HEIGHT-60, 30, 30);
		g.drawString(stoneQt+"", 300, HEIGHT-50);

		foodLogo.draw(350, HEIGHT-60, 30, 30);
		g.drawString(foodQt+"", 390, HEIGHT-50);
		
		
		seeker.draw(600, HEIGHT-60, 30, 30);
		g.drawString(sC+"", 640, HEIGHT-50);

		hunter.draw(690, HEIGHT-60, 30, 30);
		g.drawString(hC+"", 730, HEIGHT-50);

		lumberjack.draw(780, HEIGHT-60, 30, 30);
		g.drawString(lC+"", 820, HEIGHT-50);
		
		miner.draw(870, HEIGHT-60, 30, 30);
		g.drawString(mC+"", 910, HEIGHT-50);
		
		
		
	}

	public static void main(String args[]) {
		try {
			AppGameContainer container = new AppGameContainer(new survivorInterface());
			container.setDisplayMode(1024,600,false);
			container.start();
			
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}

}
