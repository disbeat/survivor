package survivor.ontology;

import jade.content.Concept;

public class Location implements Concept{
	
	int posx, posy;

	public int getPosx() {
		return posx;
	}

	public void setPosx(int posx) {
		this.posx = posx;
	}

	public int getPosy() {
		return posy;
	}

	public void setPosy(int posy) {
		this.posy = posy;
	}

	public boolean equals(Location location)
	{
		if (location.getPosx()==this.getPosx() && location.getPosy()==this.getPosy())
			return true;
		return false;
	}
	
	public int distance(Location location)
	{
		return (Math.abs(this.getPosx()-location.getPosx()))+(Math.abs(this.getPosy()-location.getPosy()));
	}
	
	public Location clone()
	{
		Location l = new Location();
		l.setPosx(this.getPosx());
		l.setPosy(this.getPosy());
		return l;
	}
	
	@Override
	public String toString()
	{
		return this.getPosx()+", "+this.posy;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		return ( this.posx == ((Location)obj).getPosx() && this.posy == ((Location)obj).getPosy() );
	}
}
