package survivor.ontology;

import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.BasicOntology;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.schema.AgentActionSchema;
import jade.content.schema.ConceptSchema;
import jade.content.schema.ObjectSchema;
import jade.content.schema.PredicateSchema;
import jade.content.schema.PrimitiveSchema;
import survivor.ontology.actions.Reproduce;
import survivor.ontology.actions.RequestFood;
import survivor.ontology.actions.RequestFoodResponse;
import survivor.ontology.actions.RequestResourceAmountOfLocation;
import survivor.ontology.actions.RequestResourceAmountOfLocationResponse;
import survivor.ontology.actions.RequestResourceOfLocation;
import survivor.ontology.actions.RequestResourceOfLocationResponse;
import survivor.ontology.actions.RequestSeenResources;
import survivor.ontology.actions.RequestSeenResourcesResponse;
import survivor.ontology.actions.SendAgentInfo;
import survivor.ontology.actions.SendBaseInfo;
import survivor.ontology.actions.SendFoundResources;
import survivor.ontology.actions.UpdateBaseResources;

public class SurvivorOntology extends Ontology implements SurvivorVocabulary {
	
	private static SurvivorOntology instance = new SurvivorOntology(BasicOntology.getInstance());
	
	private static Codec codec = new SLCodec(); 
	
	public static SurvivorOntology getInstance() {
		return instance;
	}
	
	public static Codec getCodec()
	{
		return codec;
	}
	
	public SurvivorOntology(Ontology base) {
		super(ONTOLOGY_NAME, base);
		
		try
		{
			// primitives
			PrimitiveSchema integerSchema = (PrimitiveSchema)getSchema(BasicOntology.INTEGER);
		
			// location
			ConceptSchema locationSchema = new ConceptSchema(SurvivorVocabulary.LOCATION);
			locationSchema.add(SurvivorVocabulary.XPOS, integerSchema, ObjectSchema.MANDATORY);
			locationSchema.add(SurvivorVocabulary.YPOS, integerSchema, ObjectSchema.MANDATORY);
			
			// resources
			ConceptSchema resourceSchema = new ConceptSchema(SurvivorVocabulary.RESOURCE);
			resourceSchema.add(SurvivorVocabulary.AMOUNT, integerSchema);
			resourceSchema.add(SurvivorVocabulary.TYPE, integerSchema);
			resourceSchema.add(SurvivorVocabulary.LOCATION, locationSchema, ObjectSchema.OPTIONAL);
			
			
			ConceptSchema foodSchema = new ConceptSchema(SurvivorVocabulary.FOOD);
			foodSchema.addSuperSchema(resourceSchema);
			
			ConceptSchema woodSchema = new ConceptSchema(SurvivorVocabulary.WOOD);
			woodSchema.addSuperSchema(resourceSchema);
			
			ConceptSchema stoneSchema = new ConceptSchema(SurvivorVocabulary.STONE);
			woodSchema.addSuperSchema(resourceSchema);
			
			ConceptSchema personSchema = new ConceptSchema(SurvivorVocabulary.PERSON);
			personSchema.add(SurvivorVocabulary.PERSON_LOCATION, locationSchema, ObjectSchema.MANDATORY);
			personSchema.add(SurvivorVocabulary.ENERGY, integerSchema, ObjectSchema.MANDATORY);
			personSchema.add(SurvivorVocabulary.WORKLOAD, integerSchema, ObjectSchema.OPTIONAL);
			personSchema.add(SurvivorVocabulary.TYPE, integerSchema, ObjectSchema.MANDATORY);
			
			ConceptSchema baseSchema = new ConceptSchema(SurvivorVocabulary.BASE);
			baseSchema.add(SurvivorVocabulary.LOCATION, locationSchema, ObjectSchema.MANDATORY);
			baseSchema.add(SurvivorVocabulary.WOOD, woodSchema, ObjectSchema.MANDATORY);
			baseSchema.add(SurvivorVocabulary.STONE, woodSchema, ObjectSchema.MANDATORY);
			baseSchema.add(SurvivorVocabulary.FOOD, woodSchema, ObjectSchema.MANDATORY);
			
			
			
			// resource location
			PredicateSchema resourceIsLocatedSchema = new PredicateSchema(SurvivorVocabulary.RESOURCE_IS_LOCATED);
			resourceIsLocatedSchema.add(SurvivorVocabulary.RESOURCE, resourceSchema, ObjectSchema.MANDATORY);
			resourceIsLocatedSchema.add(SurvivorVocabulary.IS_LOCATED_LOCATION, locationSchema, ObjectSchema.MANDATORY);
			
			
			// actions
			AgentActionSchema requestFoodSchema = new AgentActionSchema(SurvivorVocabulary.REQUEST_FOOD);
			requestFoodSchema.add(SurvivorVocabulary.AMOUNT, integerSchema);
			
			AgentActionSchema requestFoodResponseSchema = new AgentActionSchema(SurvivorVocabulary.REQUEST_FOOD_RESPONSE);
			requestFoodResponseSchema.add(SurvivorVocabulary.AMOUNT, integerSchema);
			
			AgentActionSchema requestSeenResourcesSchema = new AgentActionSchema(SurvivorVocabulary.REQUEST_SEEN_RESOURCES);
			
			AgentActionSchema requestSeenResourcesResponseSchema = new AgentActionSchema(SurvivorVocabulary.REQUEST_SEEN_RESOURCES_RESPONSE);
			requestSeenResourcesResponseSchema.add(SurvivorVocabulary.RESOURCE, resourceSchema, 0, ObjectSchema.UNLIMITED);
			
			AgentActionSchema requestResourceOfLocationSchema = new AgentActionSchema(SurvivorVocabulary.REQUEST_RESOURCE_OF_LOCATION);
			requestResourceOfLocationSchema.add(SurvivorVocabulary.LOCATION, locationSchema);
			
			AgentActionSchema requestResourceOfLocationResponseSchema = new AgentActionSchema(SurvivorVocabulary.REQUEST_RESOURCE_OF_LOCATION_RESPONSE);
			requestResourceOfLocationResponseSchema.add(SurvivorVocabulary.RESOURCE, resourceSchema, ObjectSchema.OPTIONAL);
			
			AgentActionSchema sendAgentInfoSchema = new AgentActionSchema(SurvivorVocabulary.SEND_AGENT_INFO);
			sendAgentInfoSchema.add(SurvivorVocabulary.PERSON, personSchema, ObjectSchema.MANDATORY);
			
			AgentActionSchema sendBaseInfoSchema = new AgentActionSchema(SurvivorVocabulary.SEND_BASE_INFO);
			sendBaseInfoSchema.add(SurvivorVocabulary.BASE, baseSchema, ObjectSchema.MANDATORY);
			
			AgentActionSchema sendFoundResourcesSchema = new AgentActionSchema(SurvivorVocabulary.SEND_FOUND_RESOURCES);
			sendFoundResourcesSchema.add(SurvivorVocabulary.RESOURCE, resourceSchema, 0, ObjectSchema.UNLIMITED);
			
			AgentActionSchema requestResourceAmountOfLocationSchema = new AgentActionSchema(SurvivorVocabulary.REQUEST_RESOURCE_AMOUNT_OF_LOCATION);
			requestResourceAmountOfLocationSchema.add(SurvivorVocabulary.LOCATION, locationSchema, ObjectSchema.MANDATORY);
			requestResourceAmountOfLocationSchema.add(SurvivorVocabulary.AMOUNT, integerSchema, ObjectSchema.MANDATORY);
			
			AgentActionSchema requestResourceAmountOfLocationResponseSchema = new AgentActionSchema(SurvivorVocabulary.REQUEST_RESOURCE_AMOUNT_OF_LOCATION_RESPONSE);
			requestResourceAmountOfLocationResponseSchema.add(SurvivorVocabulary.AMOUNT, integerSchema, ObjectSchema.MANDATORY);
			
			AgentActionSchema updateBaseResourcesSchema = new AgentActionSchema(SurvivorVocabulary.UPDATE_BASE_RESOURCES);
			updateBaseResourcesSchema.add(SurvivorVocabulary.AMOUNT, integerSchema, ObjectSchema.MANDATORY);
			updateBaseResourcesSchema.add(SurvivorVocabulary.TYPE, integerSchema, ObjectSchema.MANDATORY);
			updateBaseResourcesSchema.add(SurvivorVocabulary.LOCATION, locationSchema, ObjectSchema.MANDATORY);
			
			AgentActionSchema reproduceSchema = new AgentActionSchema(SurvivorVocabulary.REPRODUCE);
			
			// define schema classes
			add(locationSchema, Location.class);
			add(resourceSchema, Resource.class);
			add(stoneSchema, Stone.class);
			add(woodSchema, Wood.class);
			add(foodSchema, Food.class);
			add(personSchema, Person.class);
			add(baseSchema, BaseInfo.class);
			add(resourceIsLocatedSchema, ResourceIsLocated.class);
			add(requestFoodSchema, RequestFood.class);
			add(requestFoodResponseSchema, RequestFoodResponse.class);
			add(requestSeenResourcesSchema, RequestSeenResources.class);
			add(requestSeenResourcesResponseSchema, RequestSeenResourcesResponse.class);
			add(requestResourceOfLocationSchema, RequestResourceOfLocation.class);
			add(requestResourceOfLocationResponseSchema, RequestResourceOfLocationResponse.class);
			add(sendAgentInfoSchema, SendAgentInfo.class);
			add(sendBaseInfoSchema, SendBaseInfo.class);
			add(sendFoundResourcesSchema, SendFoundResources.class);
			add(requestResourceAmountOfLocationSchema, RequestResourceAmountOfLocation.class);
			add(requestResourceAmountOfLocationResponseSchema, RequestResourceAmountOfLocationResponse.class);
			add(updateBaseResourcesSchema, UpdateBaseResources.class);
			add(reproduceSchema, Reproduce.class);
		
		} catch(OntologyException oe) { 
			oe.printStackTrace(); 
		}
	}
}
