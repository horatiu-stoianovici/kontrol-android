package com.stoianovici.horatiu.kontrol.views;

import com.stoianovici.horatiu.kontrol.utils.RandomStringGenerator;

public class TouchPoint {
	private static RandomStringGenerator stringGenerator = new RandomStringGenerator(5);
	private float x, y;
	private int id;
	private String uid;
	
	public TouchPoint(float x, float y, int id)
	{
		this.x = x;
		this.y = y;
		this.id = id;
		this.uid = stringGenerator.nextString();
	}
	
	public TouchPoint(float x, float y)
	{
		this(x, y, 0);
	}
	
	public TouchPoint()
	{
		this(0, 0, 0);
	}
	
	public void setX(float x){
		this.x = x;
	}
	
	public void setY(float y){
		this.y = y;
	}
	
	public void setId(int id){
		this.id = id;
	}
	
	public float getX(){
		return this.x;
	}
	
	public float getY(){
		return this.y;
	}
	
	public int getId(){
		return this.id;
	}
	
	public String getUid(){
		return uid;
	}

	public void refreshUid() {
		this.uid = stringGenerator.nextString();
	}
}
