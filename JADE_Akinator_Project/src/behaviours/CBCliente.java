package behaviours;

import java.io.IOException;
import java.io.Serializable;
import java.util.Scanner;
import java.util.UUID;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import utils.Utils;


public class CBCliente extends CyclicBehaviour{
	private static final long serialVersionUID = 1L;
	private String conversationID = "";
	private String name;
	private Scanner sc;

	public CBCliente(String name) {
		super();
		this.name = name;
		sc = new Scanner(System.in);
	}
	
	@Override
	public void action() {
		//Nueva partida, nuevo conversationID
		conversationID = name + UUID.randomUUID().toString() + System.currentTimeMillis();
		
		//Avisar al adivino
		Utils.enviarMensaje(this.myAgent, "adivinar", "EMPEZAR PARTIDA", conversationID);
		
		//Partida
		System.out.println("\n****************  AKINATOR  *****************");
		  System.out.println("**************     DAVID      ***************");
		  
		while(true) {
			//Recibe la pregunta y la imprime por pantalla
			ACLMessage msg = this.myAgent.blockingReceive( MessageTemplate.and(
					 MessageTemplate.MatchPerformative(ACLMessage.INFORM), 
					 MessageTemplate.MatchOntology("ontologia"))
					);
			
			String pregunta = null;
			try {
				pregunta = "" + msg.getContentObject();
				System.out.println(pregunta);
			} catch (UnreadableException e) {
				e.printStackTrace();
			}
			
			
			//Espero la respuesta por consola
			String respuesta;
			do {
				respuesta = sc.nextLine().toLowerCase();
			}while(!respuesta.equals("s") && !respuesta.equals("n"));
			
			
			//Envia la respuesta
			try {
				msg = msg.createReply();                            
				msg.setPerformative(ACLMessage.INFORM);             
				msg.setContentObject((Serializable) respuesta);	    //El contenido es la respuesta a la pregunta
				this.myAgent.send(msg);								//Envia la respuesta
			
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			
			//Espera a que el agente quien es quien avance y diga la respuesta final(PROPOSE) 
			//  o se prepare para enviar la siguiente pregunta (INFORM)
			msg = this.myAgent.blockingReceive( MessageTemplate.and( 
						 MessageTemplate.or( MessageTemplate.MatchPerformative(ACLMessage.INFORM),
								 			 MessageTemplate.MatchPerformative(ACLMessage.PROPOSE) ), 
					 	 MessageTemplate.MatchOntology("ontologia"))
					);
			
			
			String respuestaTrasAvance = null;
			try {
				respuestaTrasAvance = "" + msg.getContentObject();
			} catch (UnreadableException e) {
				e.printStackTrace();
			}
			
			
			//Si la respuesta es la solución, la imprime por pantalla y empieza una nueva partida
			if(msg.getPerformative() == ACLMessage.PROPOSE) {
				System.out.println(respuestaTrasAvance);
				
				System.out.println("**************** FIN PARTIDA ****************\n\n");
				break;
			}
			
			
			//Si no era la solución, avisa al agente Quien es quien para la siguiente pregunta
			try {
				msg = msg.createReply();                            	
				msg.setPerformative(ACLMessage.INFORM);             
				msg.setContentObject((Serializable) "Estoy preparado"); //El contenido simplemente es un aviso (no vale para nada)
				this.myAgent.send(msg);									//Envia el aviso
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		} //Fin while
		//Fin partida
	}	

}
