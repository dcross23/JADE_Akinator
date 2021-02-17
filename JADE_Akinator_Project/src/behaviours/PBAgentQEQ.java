package behaviours;

import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.ThreadedBehaviourFactory;
import weka.classifiers.trees.J48;

public class PBAgentQEQ extends ParallelBehaviour {

	private static final long serialVersionUID = 1L;

	public PBAgentQEQ(J48 j48) throws Exception {
		super();
		
		//Acepta hasta 5 clientes, y por tanto, habr� 5 subBehaviours
		ThreadedBehaviourFactory tbf = new ThreadedBehaviourFactory();
		for(int i=0; i<5; i++) 
			this.addSubBehaviour(tbf.wrap(new CBAgenteQEQ(j48)));
		
		//Imprime el �rbol
		System.out.println(j48.graph());
	}
}
