
public class Identifier {
	private int prim; //primary id (the id of the item composite)
	private char supp; //supplementary id; the component of the item that this is
	private String comb;
	public boolean equals(Object o) {
		Identifier i = (Identifier)o;
		return this.comb.equals(i.comb);
	}
	public int hashCode() {
		return comb.hashCode();
	}
	Identifier(int first, char second){
		prim = first;
		supp = second;
		comb = (""+first)+(""+second);
	}
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
