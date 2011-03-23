package survivor.ontology;

import jade.content.Concept;

public class BaseInfo implements Concept{
	
	Food food;
	Wood wood;
	Stone stone;
	Location location;

	public Food getFood() {
		return food;
	}
	
	public void setFood(Food food) {
		this.food = food;
	}
	
	public Stone getStone() {
		return stone;
	}
	
	public void setStone(Stone stone) {
		this.stone = stone;
	}
	
	public Wood getWood() {
		return wood;
	}
	
	public void setWood(Wood wood) {
		this.wood = wood;
	}
	
	public Location getLocation() {
		return location;
	}
	
	public void setLocation(Location location) {
		this.location = location;
	}
}
