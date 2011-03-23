package survivor.ontology;

public interface SurvivorVocabulary {
	public static final String ONTOLOGY_NAME = "SurvivalOntology";
	
	//Concepts
	public static final String LOCATION = "LOCATION";
	public static final String RESOURCE = "RESOURCE";
	public static final String FOOD = "FOOD";
	public static final String STONE = "STONE";
	public static final String WOOD = "WOOD";
	public static final String PERSON = "PERSON";
	public static final String BASE = "BASEINFO";
	
	//Slots
	public static final String XPOS = "posx";
	public static final String YPOS = "posy";
	public static final String AMOUNT = "amount";
	public static final String REMAINING = "remaining";
	public static final String SEEN_RESOURCES = "seenResources";
	public static final String ENERGY = "energy";
	public static final String PERSON_LOCATION = "personLocation";
	public static final String WORKLOAD = "workload";
	public static final String TYPE = "type";
	
	
	//Predicates
	public static final String RESOURCE_IS_LOCATED = "RESOURCE_IS_LOCATED";
	public static final String IS_LOCATED_RESOURCE = "resource";
	public static final String IS_LOCATED_LOCATION = "location";
	
	//Actions
	public static final String REQUEST_FOOD = "REQUEST_FOOD";
	public static final String REQUEST_FOOD_RESPONSE = "REQUEST_FOOD_RESPONSE";
	public static final String REQUEST_SEEN_RESOURCES = "REQUEST_SEEN_RESOURCES";
	public static final String REQUEST_SEEN_RESOURCES_RESPONSE = "REQUEST_SEEN_RESOURCES_RESPONSE";
	public static final String REQUEST_RESOURCE_OF_LOCATION = "REQUEST_RESOURCE_OF_LOCATION";
	public static final String REQUEST_RESOURCE_OF_LOCATION_RESPONSE = "REQUEST_RESOURCE_OF_LOCATION_RESPONSE";
	public static final String SEND_AGENT_INFO = "SEND_AGENT_INFO";
	public static final String SEND_BASE_INFO = "SEND_BASE_INFO";
	public static final String SEND_FOUND_RESOURCES = "SEND_FOUND_RESOURCES";
	public static final String REQUEST_RESOURCE_AMOUNT_OF_LOCATION = "REQUEST_RESOURCE_AMOUNT_OF_LOCATION";
	public static final String REQUEST_RESOURCE_AMOUNT_OF_LOCATION_RESPONSE = "REQUEST_RESOURCE_AMOUNT_OF_LOCATION_RESPONSE";
	public static final String UPDATE_BASE_RESOURCES = "UPDATE_BASE_RESOURCES";
	public static final String REPRODUCE = "REPRODUCE";
	
}
