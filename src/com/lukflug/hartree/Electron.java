package com.lukflug.hartree;

public class Electron extends ArrayField {
	private final int n,l;
	private double e=0;
	private ArrayField pot;
	private boolean inited=false;
	private static final double F=1;
	private static final double M=1822.888486209;
	
	public Electron (int size, double dr, int n, int l) {
		super(size,dr);
		this.n=n;
		this.l=l;
		pot=new ArrayField(size,dr) {
			@Override
			public double getValue(double r) {
				if (r>array.length*dr) return 1/r;
				return super.getValue(r);
			}
		};
	}
	
	public void calcEnergy (Field v, double a, double z) {
		int nr=n-l;
		double last=0;
		double start=-z*z;
		int count=1;
		while (true) {
			update(v,start/count,a);
			if (last*array[array.length-1]<0) {
				nr--;
				if (nr==0) break;
			}
			count++;
			last=array[array.length-1];
		}
		double max=start/count;
		double min=start/(count-1);
		do {
			update(v,(min+max)/2,a);
			if (last*array[array.length-1]<0) max=e;
			else min=e;
		} while (Math.abs(array[array.length-1])>.0001);
	}
	
	private void update (Field v, double e, double a) {
		double mass=M/(1/a+M);
		double copy[]=new double[array.length];
		for (int i=0;i<array.length;i++) {
			copy[i]=array[i];
		}
		array[0]=0;
		array[1]=dr;
		double prob=array[1]*array[1]*dr;
		for (int i=2;i<array.length;i++) {
			double r=(i-1)*dr;
			array[i]=2*array[i-1]-array[i-2]+(2*mass*(v.getValue(i-1)-e)+l*(l+1)/r/r)*array[i-1]*dr*dr;
			prob+=array[i]*array[i]*dr;
		}
		double q=-1;
		pot.array[array.length-1]=-q/(array.length-1)/dr;
		for (int i=array.length-2;i>0;i--) {
			array[i]/=Math.sqrt(prob);
			double eff=F*array[i+1]+Math.sqrt(1-F*F)*copy[i+1];
			if (!inited) eff=array[i+1];
			q+=eff*eff*dr;
			pot.array[i]=pot.array[i+1]-q/dr*(1.0/i-1.0/(i+1));
		}
		this.e=e;
		inited=true;
	}
	
	public double getEnergy() {
		return e;
	}
	
	public Field getPotential() {
		return pot;
	}
}
