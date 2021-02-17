package agents;

import behaviours.CBCliente;
import jade.core.Agent;

public class AgentCliente extends Agent {

	private static final long serialVersionUID = 1L;

	@Override
	public void setup() {
		//El cliente no tiene servicios
				
		//Comportamientos agente cliente
		this.addBehaviour(new CBCliente(this.getName()));
	}
}

