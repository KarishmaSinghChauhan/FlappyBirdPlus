package com.karishma.flappybirdplus;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Collections;
import java.util.PriorityQueue;
import java.util.Random;

public class FlappyBirdPlus extends ApplicationAdapter {

	SpriteBatch batch;
	Texture background;
	Texture gameover;
	Texture extraBird;
	Circle birdCircle;
	Circle extraBirdCircle;
	Rectangle tubeRectangle[];
//	ShapeRenderer shapeRenderer;

	Texture birds[];
	Texture topTube;
	Texture bottomTube;
	int flappyState = 1;
	int gameState = 0;
	float birdY;
	float gravity = 3;
	float velocity = 0;

	float distanceBtwTubes; // distance along y axis
	int numberOfTubes = 3;
	float tubeX[] = new float[numberOfTubes];
	float tubeY[] = new float[numberOfTubes];
	float gap = 400; // the constant distance in height between the lower end of topTube and upper end of bottomTube
	float tubeVelocity = 4;
	Random randomGenerator;

	int score = 0;
	int presentTube = 0;
	int bonus = 0;
	int bonusPointsOrig = 2;
	int bonusPoints = bonusPointsOrig;
	BitmapFont font;
	BitmapFont font2;
	BitmapFont font3;

	float extraBirdY;
	float extraBirdX;
	float extraVelocity = 6;
	float extraMultiply = 50;

	@SuppressWarnings("NewApi")
	PriorityQueue<Integer> highScore = new PriorityQueue<Integer>(Collections.reverseOrder());

	@Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture("bg.png");
		gameover = new Texture("gameover.png");
		birds = new Texture[2];
		birds[0] = new Texture("bird.png");
		birds[1] = new Texture("bird2.png");
		topTube = new Texture("toptube.png");
		bottomTube = new Texture("bottomtube.png");
		birdY = Gdx.graphics.getHeight()/2-birds[flappyState].getHeight()/2;
		distanceBtwTubes = Gdx.graphics.getWidth()*3/4;
		randomGenerator = new Random();
		tubeRectangle = new Rectangle[2*numberOfTubes];

		for(int i = 0; i<= numberOfTubes-1; i++)
		{
			tubeX[i] = Gdx.graphics.getWidth() / 2 - topTube.getWidth() / 2 + (i * distanceBtwTubes) + Gdx.graphics.getWidth()/2;
			tubeY[i] = (randomGenerator.nextFloat()-0.5f)*(Gdx.graphics.getHeight()-gap-200);

			int j = i*2;
			tubeRectangle[j] = new Rectangle();
			tubeRectangle[j+1] = new Rectangle();
		}

//		shapeRenderer = new ShapeRenderer();
		birdCircle = new Circle();
		extraBirdCircle = new Circle();

		font = new BitmapFont();
		font2 = new BitmapFont();
		font3 = new BitmapFont();

		extraBird = new Texture("magic ball.jpg");
		extraBirdY = randomGenerator.nextFloat() * (Gdx.graphics.getHeight() - extraBird.getHeight());
		extraBirdX = Gdx.graphics.getWidth();
	}


	@Override
	public void render () {

		batch.begin();
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(),Gdx.graphics.getHeight());

		if(gameState == 1 || gameState == 3)
		{
			for(int i = 0; i<= numberOfTubes -1; i++)
			{
				batch.draw(topTube, tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeY[i]);
				batch.draw(bottomTube, tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeY[i]);

				int j = i*2;
				tubeRectangle[j] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeY[i] , topTube.getWidth(), topTube.getHeight());
				tubeRectangle[j+1] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeY[i], bottomTube.getWidth(), bottomTube.getHeight());

				tubeX[i] = tubeX[i] - tubeVelocity;

				if(Intersector.overlaps(birdCircle, tubeRectangle[j]) || Intersector.overlaps(birdCircle, tubeRectangle[j+1]) )
				{
					// Gdx.app.log("Collision", "Collided!");

					gameState = 2;
					highScore.add(score+bonus);
				}

				if(tubeX[i] < - bottomTube.getWidth())
				{
					tubeX[i] = distanceBtwTubes * numberOfTubes + (Gdx.graphics.getWidth()* 1/4); //added the last term as a modification for my Bonus round flying birds.
					tubeY[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);
				}

				else if(tubeX[presentTube] < Gdx.graphics.getWidth()/2 - bottomTube.getWidth()/2)
				{
					score += 1;
//					Gdx.app.log("Score", Integer.toString(score));

					if(presentTube <= numberOfTubes-2)
					{
						presentTube += 1;
					}
					else
					{
						presentTube = 0;
						gameState = 3;
					}
				}
			}

			if(Gdx.input.justTouched())
			{
				velocity = -30;
			}

			if(birdY > 0 )
			{
				velocity = velocity + gravity;
				birdY = birdY - velocity;

				if(birdY <= 0 || birdY > Gdx.graphics.getHeight()- birds[flappyState].getHeight())
				{
					gameState = 2;
					highScore.add(score+bonus);
				}
			}
		}

		else if (gameState == 0)
		{
			if(Gdx.input.justTouched())
			{
				gameState = 1;
			}
		}

		if(gameState == 3)
		{
			batch.draw(extraBird, extraBirdX, extraBirdY, 140, 140);
			extraBirdCircle.set(extraBirdX + 140/2, extraBirdY + 140/2, 140/2 );
			extraBirdX = extraBirdX - extraVelocity;

			while(true)
			{
				float n = (randomGenerator.nextFloat() - 0.5f) * extraMultiply;
				if(extraBirdY+n > 0 && extraBirdY +n < Gdx.graphics.getHeight()- extraBird.getHeight())
				{
					extraBirdY += n;
					break;
				}
			}

			if(extraBirdX < - extraBird.getWidth() || Intersector.overlaps(extraBirdCircle, birdCircle))
			{
				if(Intersector.overlaps(extraBirdCircle, birdCircle))
				{
//					Gdx.app.log("Bonus", "Scored!");
					bonus += bonusPoints;
					bonusPoints += 2;
				}
				else
				{
					bonusPoints = bonusPointsOrig;
				}

				gameState = 1;
				extraMultiply *= 1.2;
				tubeVelocity += 1;
				extraVelocity += 1;
				extraBirdY = randomGenerator.nextFloat() * (Gdx.graphics.getHeight() - extraBird.getHeight());
				extraBirdX = Gdx.graphics.getWidth() ;
			}
		}

		if(gameState == 2)
		{
			batch.draw(gameover, Gdx.graphics.getWidth()/2 - gameover.getWidth()/2 -450, Gdx.graphics.getHeight()/2 - gameover.getHeight()/2 , Gdx.graphics.getWidth()*3/4, 300);

			int highest = highScore.peek();

			font.setColor(Color.RED);
			font.getData().setScale(5);
			font.draw(batch, "Final Score : "+ Integer.toString(score+bonus), 850, 960);

			font.setColor(Color.RED);
			font.getData().setScale(5);
			font.draw(batch, "High Score : "+ Integer.toString(highest), 850, 860);

			while(birdY > 0)
			{
				velocity = velocity + gravity;
				birdY = birdY - velocity;
			}

			if(Gdx.input.justTouched())
			{
				gameState = 1;
				score = 0;
				presentTube = 0;
				bonus = 0;
				velocity = 0;
				bonusPoints = bonusPointsOrig;
				extraVelocity = 6;
				extraMultiply = 50;
				tubeVelocity = 4;
				birdY = Gdx.graphics.getHeight()/2-birds[flappyState].getHeight()/2;

				for(int i = 0; i<= numberOfTubes-1; i++)
				{
					tubeX[i] = Gdx.graphics.getWidth() / 2 - topTube.getWidth() / 2 + (i * distanceBtwTubes) + Gdx.graphics.getWidth()/2;
					tubeY[i] = (randomGenerator.nextFloat()-0.5f)*(Gdx.graphics.getHeight()-gap-200);

					int j = i*2;
					tubeRectangle[j] = new Rectangle();
					tubeRectangle[j+1] = new Rectangle();
				}
			}
		}

		if(gameState != 2)
		{
			if (flappyState == 1)
			{
				flappyState = 0;
			}
			else
			{
				flappyState = 1;
			}
		}

		batch.draw(birds[flappyState], Gdx.graphics.getWidth()/2-birds[flappyState].getWidth()/2, birdY);
		font.setColor(Color.BLACK);
		font.getData().setScale(5);
		font.draw(batch, "Score : "+ Integer.toString(score), 80, 200);
		font.setColor(Color.BLACK);
		font.getData().setScale(5);
		font.draw(batch, "Bonus : "+ Integer.toString(bonus), 80, 120);
		batch.end();
		birdCircle.set(Gdx.graphics.getWidth()/2, birdY + birds[flappyState].getHeight()/2, birds[flappyState].getHeight()/2 );
	}


	@Override
	public void dispose () {
		batch.dispose();
	}
}
