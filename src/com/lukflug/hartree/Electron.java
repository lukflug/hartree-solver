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
				if (r>=(array.length-1)*dr && inited) return 1/r;
				return super.getValue(r);
			}
			
			@Override
			public double getValue(int i) {
				if (i>=array.length && inited) return 1/dr/i;
				return super.getValue(i);
			}
		};
	}
	
	public boolean calcEnergy (Field v, double a, double z) {
		int nr=n-l;
		double last=0;
		double start=-z*z;
		int count=1;
		double temp[]=new double[array.length];
		while (true) {
			update(v,start/count,a,temp);
			if (last*temp[temp.length-1]<0) {
				nr--;
				if (nr==0) break;
			}
			count++;
			last=temp[temp.length-1];
			if (count>1000) return false;
		}
		double max=start/count;
		double min=start/(count-1);
		int attempts=0;
		do {
			update(v,(min+max)/2,a,temp);
			if (last*temp[temp.length-1]<0) max=e;
			else min=e;
			attempts++;
			if (attempts>1000) return false;
		} while (Math.abs(temp[temp.length-1])>.01);
		double q=-1;
		pot.array[array.length-1]=-q/(array.length-1)/dr;
		for (int i=array.length-2;i>0;i--) {
			if (inited) array[i]=F*temp[i]+Math.sqrt(1-F*F)*array[i];
			else array[i]=temp[i];
			q+=array[i+1]*array[i+1]*dr;
			pot.array[i]=pot.array[i+1]-q/dr*(1.0/i-1.0/(i+1));
		}
		inited=true;
		return true;
	}
	
	private void update (Field v, double e, double a, double temp[]) {
		double mass=M/(1/a+M);
		temp[0]=0;
		temp[1]=dr;
		double prob=temp[1]*temp[1]*dr;
		for (int i=2;i<temp.length;i++) {
			double r=(i-1)*dr;
			temp[i]=2*temp[i-1]-temp[i-2]+(2*mass*(v.getValue(i-1)-e)+l*(l+1)/r/r)*temp[i-1]*dr*dr;
			prob+=temp[i]*temp[i]*dr;
		}
		for (int i=0;i<temp.length;i++) {
			temp[i]/=Math.sqrt(prob);
		}
		this.e=e;
	}
	
	public int getPrincipalNumber() {
		return n;
	}
	
	public int getAzimuthalNumber() {
		return l;
	}
	
	public double getEnergy() {
		return e;
	}
	
	public Field getPotential() {
		return pot;
	}
	
	public Electron createCopy() {
		Electron copy=new Electron(array.length,dr,n,l);
		for (int i=0;i<array.length;i++) {
			copy.array[i]=array[i];
			copy.pot.array[i]=pot.array[i];
		}
		copy.e=e;
		copy.inited=inited;
		return copy;
	}
}
