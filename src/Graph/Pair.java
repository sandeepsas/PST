package Graph;

import java.io.Serializable;
import java.util.Objects;


public class Pair<L,R> implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 2088625713495844100L;
	private L l;
    private R r;
    public Pair(L l, R r){
        this.l = l;
        this.r = r;
    }
    public Pair() {
		// TODO Auto-generated constructor stub
    	this.l = null;
        this.r = null;
	}
	public L getL(){ return l; }
    public R getR(){ return r; }
    public void setL(L l){ this.l = l; }
    public void setR(R r){ this.r = r; }
    
    
	@Override 
	public int hashCode() { 
		return Objects.hash(l, r);
	  }
	@Override 
	public boolean equals(Object node) {
		if(node == null)
			return false;
		Pair<L, R> node_x = (Pair<L, R>) node;
        return ((node_x.l == this.l) && (node_x.r == this.r));
    }
	public String toString(){
		return Objects.toString(this.l)+"-"+Objects.toString(this.r);
	}
    
    
    
}
