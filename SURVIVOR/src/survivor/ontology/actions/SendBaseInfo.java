package survivor.ontology.actions;

import jade.content.AgentAction;
import survivor.ontology.BaseInfo;

public class SendBaseInfo implements AgentAction {

	BaseInfo baseInfo;
	
	public BaseInfo getBaseInfo() {
		return baseInfo;
	}
	
	public void setBaseInfo(BaseInfo baseInfo) {
		this.baseInfo = baseInfo;
	}
	
}
