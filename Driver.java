package nKnightsCSP;

public class Driver {
	
	public static void main(String[] args){
		KnightSolverCSP ks = new KnightSolverCSP(Integer.parseInt(args[0]));
		ks.solve();
		ks.printState();
		//ks.testMethod();
	}
}