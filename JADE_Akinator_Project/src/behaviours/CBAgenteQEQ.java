package behaviours;

import java.io.IOException;
import java.io.Serializable;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import utils.Pregunta;
import weka.classifiers.trees.J48;

public class CBAgenteQEQ extends CyclicBehaviour {

	private static final long serialVersionUID = 1L;
	private J48 j48;
	private Pregunta pregunta;
	
	
	public CBAgenteQEQ(J48 j48) throws Exception {
		this.j48 = j48;
	}
	
	
	@Override
	public void action() {
		//Nueva partida
		try {
			pregunta = new Pregunta(j48);
		} catch (Exception e) {
			e.printStackTrace();
		}	
		
		//Espera el aviso del cliente para la empezar la siguiente partida
		ACLMessage msg = this.myAgent.blockingReceive( MessageTemplate.and(
     					 MessageTemplate.MatchPerformative(ACLMessage.REQUEST), 
     					 MessageTemplate.MatchOntology("ontologia"))
					);
		
		 		
		//Partida
		while(true) {
			//Obtener siguiente pregunta 
			String p = "PREGUNTA " + pregunta.obtenerPreguntaNodo();
			
			//Enviar siguiente pregunta
			try {
				msg = msg.createReply();                       
				msg.setPerformative(ACLMessage.INFORM);             
				msg.setContentObject((Serializable) p);			//El contenido es la siguiente pregunta
				this.myAgent.send(msg);							//Enviamos la siguiente pregunta
				
			} catch (IOException e1) {
				e1.printStackTrace();
			} 
		
		
			//Obtiene la respuesta
			msg = this.myAgent.blockingReceive(MessageTemplate.and(
					       MessageTemplate.and(
	     					  MessageTemplate.MatchPerformative(ACLMessage.INFORM), 
	     					  MessageTemplate.MatchOntology("ontologia"))
						    ,MessageTemplate.MatchConversationId(msg.getConversationId())
						));
			
			String respuesta = null;
			try {
				respuesta = "" + msg.getContentObject(); //Recibe la respuesta del cliente

			} catch (UnreadableException e1) {
				e1.printStackTrace();
			}
		
			
			//Avanza en el árbol con esa respuesta
			pregunta.navegarNodoRespuesta(respuesta);
			
			
			//Siguiente respuesta o última respuesta si ya sabe la solución
			msg = msg.createReply();		
			
			String sigRespuesta = null;
			if(pregunta.esNodoFinal()) {
				msg.setPerformative(ACLMessage.PROPOSE); 
				sigRespuesta = "ES "+ pregunta.obtenerPreguntaNodo();
			
			}else {
				msg.setPerformative(ACLMessage.INFORM);
				sigRespuesta = "Hay mas preguntas";
			}
			
			
			//Envia la respuesta
			try {
				msg.setContentObject((Serializable) sigRespuesta);		//Contenido de la respuesta
				this.myAgent.send(msg);									//Enviar respuesta
				
			} catch (IOException e1) {
				e1.printStackTrace();
			} 
			
			
			//Si la respuesta era la respuesta final, acaba la partida
			if(msg.getPerformative() == ACLMessage.PROPOSE) {
				//Fin del bucle de la partida
				break;
			}	
			
			//Si hay más preguntas, espera a que el cliente esté listo para la siguiente pregunta
			msg = this.myAgent.blockingReceive(MessageTemplate.and(
					 MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM), 
	     					             MessageTemplate.MatchOntology("ontologia")
	     					             ),
						                 MessageTemplate.MatchConversationId(msg.getConversationId())
						));
		}
		
	}
}
