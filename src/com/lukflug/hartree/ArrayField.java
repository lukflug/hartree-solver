package com.lukflug.hartree;

public class ArrayField implements Field {
	protected double array[];
	protected final double dr;

	public ArrayField (int size, double dr) {
		array=new double[size];
		this.dr=dr;
	}

	@Override
	public double getValue(double r) {
		double index=r/dr;
		if (index>=array.length-1) return 0;
		int i=(int)Math.floor(index);
		double a=index-i;
		return (1-a)*array[i]+a*array[i+1];
	}
	
	@Override
	public double getValue(int i) {
		if (i>=array.length) return 0;
		return array[i];
	}
	
	@Override
	public int getSize() {
		return array.length;
	}
}
