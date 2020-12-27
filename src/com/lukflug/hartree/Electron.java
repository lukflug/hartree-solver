package com.lukflug.hartree;

public class Electron extends ArrayField {
	private final int n,l;
	private double e=0;
	private ArrayField pot;
	private boolean inited=false;
	private int roots;
	public static double f=1,tol=.0001,maxAtt=1000;
	private static final double M=1822.888486209;
	
	public Electron (int size, double dr, int n, int l) {
		super(size,dr);
		this.n=n;
		this.l=l;
		pot=new ArrayField(size,dr) {
			@Override
			public double getValue(double r) {
				if (r/dr>=array.length-1 && inited) return 1/r;
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
		// Cache hamiltonian
		double h[]=new double[array.length];
		double mass=M/(1/a+M);
		for (int i=1;i<array.length;i++) {
			double r=i*dr;
			h[i]=2*mass*v.getValue(i)+l*(l+1)/r/r;
		}
		// Find energy
		double temp[]=new double[array.length];
		int nr=n-l-1;
		if (inited && findEnergy(nr,2*e,0,h,a,temp));
		else if (findEnergy(nr,-z*z,0,h,a,temp));
		else if (findEnergy(nr+1,-z*z,0,h,a,temp));
		else return false;
		// Update wavefunction and normalize
		double prob=0;
		for (int i=0;i<array.length;i++) {
			if (inited) array[i]=f*temp[i]+(1-f)*array[i];
			else array[i]=temp[i];
			prob+=array[i]*array[i]*dr;
		}
		for (int i=0;i<array.length;i++) {
			array[i]/=Math.sqrt(prob);
		}
		// Calculate potential
		double q=-1;
		pot.array[array.length-1]=-q/(array.length-1)/dr;
		for (int i=array.length-2;i>0;i--) {
			q+=array[i+1]*array[i+1]*dr;
			pot.array[i]=pot.array[i+1]+q/dr*(1.0/(i+1)-1.0/i);
		}
		inited=true;
		return true;
	}
	
	private boolean findEnergy (int nr, double min, double max, double h[], double a, double temp[]) {
		int attempts=0;
		while (true) {
			update(h,(min+max)/2,a,temp);
			if (roots>nr) max=e;
			else min=e;
			double last=temp[0];
			if (Math.abs(last)<=tol && ((roots==nr && last*Math.pow(-1,nr)>=0) || (roots==nr+1 && last*Math.pow(-1,nr)<=0))) {
				break;
			}
			attempts++;
			if (attempts>maxAtt) return false;
		}
		return true;
	}
	
	private void update (double h[], double e, double a, double temp[]) {
		roots=0;
		double shift=2*M/(1/a+M)*e;
		temp[temp.length-1]=dr;
		double vel=-Math.sqrt(-2*e)*temp[temp.length-1];
		double prob=temp[temp.length-1]*temp[temp.length-1]*dr;
		for (int i=temp.length-1;i>=1;i--) {
			vel-=(h[i]-shift)*temp[i]*dr;
			temp[i-1]=temp[i]-vel*dr;
			prob+=temp[i-1]*temp[i-1]*dr;
			if (temp[i-1]*temp[i]<0) roots++;
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
