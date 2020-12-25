package com.lukflug.hartree;

import java.util.ArrayList;
import java.util.List;

public class Atom {
	private final double dr;
	private final int z;
	private double a,zInit,zEff;
	private List<Electron> electrons=new ArrayList<Electron>();
	
	public Atom (double dr, double a, int z, double zInit) {
		this.dr=dr;
		this.a=a;
		this.z=z;
		this.zInit=zInit;
		this.zEff=zInit;
	}
	
	public void addElectron (int size, int n, int l) {
		electrons.add(new Electron(size,dr,n,l));
	}
	
	public String updateElectrons (double step) {
		for (Electron e: electrons) {
			synchronized (this) {
				if (!e.calcEnergy(new Potential(e),a,zEff)) {
					return "No convergence for n="+e.getPrincipalNumber()+" and l="+e.getAzimuthalNumber()+"!";
				}
			}
		}
		if (zInit<z) {
			zEff+=step;
			if (zEff>z) zEff=z;
		} else {
			zEff-=step;
			if (zEff<z) zEff=z;
		}
		return null;
	}
	
	public List<Electron> getElectrons() {
		return electrons;
	}
	
	public Field getElectronDensity() {
		return new Density();
	}
	
	public Field getElectronPotential() {
		return new Potential(null);
	}
	
	public double getTotalEnergy() {
		double energy=0;
		for (Electron e: electrons) {
			energy+=e.getEnergy();
			Potential pot=new Potential(e);
			for (int i=1;i<e.getSize();i++) {
				energy-=.5*e.getValue(i)*e.getValue(i)*(pot.getValue(i)+z/dr/i)*dr;
			}
		}
		return energy;
	}
	
	public synchronized Atom createCopy() {
		Atom copy=new Atom(dr,a,z,zEff);
		copy.zEff=zEff;
		for (Electron e: electrons) {
			copy.getElectrons().add(e.createCopy());
		}
		return copy;
	}
	
	
	private class Potential implements Field {
		private final Electron e1;
		
		public Potential (Electron e1) {
			this.e1=e1;
		}
		
		@Override
		public double getValue(double r) {
			double value=-zEff/r;
			for (Electron e: electrons) {
				if (e!=e1) value+=e.getPotential().getValue(r);
			}
			return value;
		}
		
		@Override
		public double getValue (int i) {
			double value=-zEff/dr/i;
			for (Electron e: electrons) {
				if (e!=e1) value+=e.getPotential().getValue(i);
			}
			return value;
		}
		
		@Override
		public int getSize() {
			return e1.getSize();
		}
	}
	
	private class Density implements Field {
		@Override
		public double getValue(double r) {
			double value=0;
			for (Electron e: electrons) {
				value+=e.getValue(r)*e.getValue(r);
			}
			return value;
		}
		
		@Override
		public double getValue (int i) {
			double value=0;
			for (Electron e: electrons) {
				value+=e.getValue(i)*e.getValue(i);
			}
			return value;
		}
		
		@Override
		public int getSize() {
			return 0;
		}
	}
}
