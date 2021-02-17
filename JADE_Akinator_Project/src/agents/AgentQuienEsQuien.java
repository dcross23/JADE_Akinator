package agents;

import behaviours.PBAgentQEQ;
import jade.content.lang.sl.SLCodec;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class AgentQuienEsQuien extends Agent {

	private static final long serialVersionUID = 1L;
	protected PBAgentQEQ pb;
	private J48 j48 = null;

	@Override
	public void setup() {
		try {
			this.j48 = createTree("famosos.csv");
		} catch (Exception e) {
			e.printStackTrace(); 
		}
		
		
		//Servicios agente quien es quien
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		
		ServiceDescription sd = new ServiceDescription();
		sd.setName("QuienEsQuien");
		sd.setType("adivinar");
		sd.addOntologies("ontologia");
		sd.addLanguages(new SLCodec().getName());
		
		dfd.addServices(sd);		
		try{
			DFService.register(this, dfd);
		}
		catch(FIPAException e){
			System.err.println("Agente "+getLocalName()+":"+e.getMessage());
		}
		
		
		//Comportamientos agente quien es quien
		try {
			pb = new PBAgentQEQ(j48);
			addBehaviour(pb);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Crea el árbol j48
	 * @param file
	 * @return
	 * @throws Exception
	 */
	private J48 createTree(String file) throws Exception{	
		 DataSource source = new DataSource(file);
		 Instances data = source.getDataSet();
		 
		 //indicar el atributo con la categoría a clasificar
		 if (data.classIndex() == -1)
			 data.setClassIndex(0);
	
		 
		 J48 j48 = new J48();
		 j48.setOptions(new String[] {"-C", "0.25", "-M", "1"});
		 j48.setUnpruned(true);
		 j48.buildClassifier(data);
		 
		 return j48;
	}
	
}
