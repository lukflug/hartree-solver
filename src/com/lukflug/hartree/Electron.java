package com.lukflug.hartree;

public class Electron extends ArrayField {
	private final int n,l;
	private double e=0;
	private ArrayField pot;
	private boolean inited=false;
	public static double f=.5,tol=.0001,maxCount=10000,maxAtt=10000;
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
		// Cache hamiltonian
		double h[]=new double[array.length];
		double mass=M/(1/a+M);
		for (int i=1;i<array.length;i++) {
			double r=i*dr;
			h[i]=2*mass*v.getValue(i)+l*(l+1)/r/r;
		}
		// Allocate temporary wavefunction
		double temp[]=new double[array.length];
		// Find energy
		int nr=n-l;
		double last=0;
		double start=-z*z;
		int count=1;
		while (true) {
			update(h,start/count,a,temp);
			if (last*temp[temp.length-1]<0) {
				nr--;
				if (nr==0) break;
			}
			count++;
			last=temp[temp.length-1];
			if (count>maxCount) {
				System.out.println("Energy level not found!");
				return false;
			}
		}
		double max=start/count;
		double min=start/(count-1);
		int attempts=0;
		do {
			update(h,(min+max)/2,a,temp);
			if (last*temp[temp.length-1]<0) max=e;
			else min=e;
			attempts++;
			if (attempts>maxAtt) return false;
		} while (Math.abs(temp[temp.length-1])>tol);
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
			pot.array[i]=pot.array[i+1]-q/dr*(1.0/i-1.0/(i+1));
		}
		inited=true;
		return true;
	}
	
	private void update (double h[], double e, double a, double temp[]) {
		double shift=2*M/(1/a+M)*e;
		temp[0]=0;
		double vel=1;
		temp[1]=vel*dr;
		double prob=temp[1]*temp[1]*dr;
		for (int i=1;i<temp.length-1;i++) {
			vel+=(h[i]-shift)*temp[i]*dr;
			temp[i+1]=temp[i]+vel*dr;
			prob+=temp[i+1]*temp[i+1]*dr;
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
