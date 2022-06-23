//defines an identifier for an item
//Roni Shae
//June 22, 2022
public class Identifier {
	private int prim; //primary id (the id of the item composite)
	private char supp; //supplementary id; the component of the item that this is
	private String comb;
	
	@Override
	public boolean equals(Object o) {
		Identifier i = (Identifier)o;
		return this.comb.equals(i.comb);
	}
	public int hashCode() {
		return comb.hashCode();
	}
	
	//constructor
	Identifier(int first, char second){
		prim = first;
		supp = second;
		comb = (""+first)+(""+second);
	}
	
	//getters & setters
	public int getPrim() {
		return prim;
	}
	public char getSupp() {
		return supp;
	}
	public String toString() {
		return comb;
	}
}
