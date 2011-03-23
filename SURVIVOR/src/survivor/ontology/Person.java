package survivor.ontology;

import jade.content.Concept;

public class Person implements Concept{
	
	public static final int SEEKER = 0;
	public static final int MINER = 3;
	public static final int LUMBERJACK = 1;
	public static final int HUNTER = 2;
	
	int energy;
	int workload;
	Location personLocation;
	int type;
	
	public int getEnergy() {
		return energy;
	}
	
	public void setEnergy(int energy) {
		this.energy = energy;
	}

	public Location getPersonLocation() {
		return personLocation;
	}
	
	public void setPersonLocation(Location personLocation) {
		this.personLocation = personLocation;
	}
	
	public int getWorkload() {
		return workload;
	}
	
	public void setWorkload(int workload) {
		this.workload = workload;
	}
	
	public int getType() {
		return type;
	}
	
	public void setType(int type) {
		this.type = type;
	}
}
