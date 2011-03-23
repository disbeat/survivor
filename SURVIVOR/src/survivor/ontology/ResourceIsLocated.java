package survivor.ontology;

import jade.content.Predicate;

public class ResourceIsLocated implements Predicate {

	Resource resource;
	Location location;
	
		
	public Resource getResource() {
		return resource;
	}
	
	public void setResource(Resource resource) {
		this.resource = resource;
	}
	
	public Location getLocation() {
		return location;
	}
	
	public void setLocation(Location location) {
		this.location = location;
	}
}
