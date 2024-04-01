package com.example;

public class Nvect{
	int dims;
	double[] coords;
	
	public String toString(){
		String retval = "{";
		for(int i = 0; i < dims; i++){
			if(i != 0) retval += ", ";
			retval += coords[i];
		}
		retval += "}";
		return retval;
	}
	
	private static double det(double[][] matrix){
		int N = matrix.length;
		if(N == 1) return matrix[0][0];
		double retval = 0;
		for(int i = 0; i < matrix.length; i++){
			double[][] minor = new double[N-1][N-1];
			for(int j = 1; j < N; j++){
				for(int k = 0; k < N; k++){
					if ( k == i) continue;
					minor[j-1][k < i ? k: k-1] = matrix[j][k];
				}
			}
			retval += matrix[0][i] * (i%2 == 0? 1 : -1) * det(minor);  
		}
		return retval;
	}
	
	public Nvect(){
		dims = 0;
		for(int i = 0; i < dims; i++){
			this.coords[i] = 0;
		}
	}
	
	public Nvect(double ... coords){
		dims = coords.length;
		this.coords = new double[dims];
		for(int i = 0; i < dims; i++){
			this.coords[i] = coords[i];
		}	
	}
	
	public Nvect(int N, Nvect v){
		dims = N;
		this.coords = new double[dims];
		for(int i = 0; i < (dims < v.dims ? dims : v.dims); i++){
			this.coords[i] = v.coords[i];
		}
		for(int i = v.dims; i < dims; i++){
			this.coords[i] = 0;
		}
	}
	
	public Nvect(Nvect v){
		dims = v.dims;
		coords = new double[dims];
		for(int i = 0; i < dims; i++){
			this.coords[i] = v.coords[i];
		}	
	}
	
	public Nvect mul(double x){
		Nvect retval = new Nvect(this);
		for(int i = 0; i < dims; i++){
			retval.coords[i] *= x;
		}	
		return retval;
	}
	
	public static Nvect mul(Nvect ... v){
		Nvect retval = new Nvect();
		retval.dims = v[0].dims;
		retval.coords = new double[retval.dims];
		if( v.length < retval.dims - 1) throw new RuntimeException("not enough vects");
		if(v.length > retval.dims) throw new RuntimeException("too much vects");
		for(int i = 0; i < v.length; i++){
			if(v[i].dims != retval.dims) throw new RuntimeException("incorrect dimensions");	
		}
		for(int i = 0; i < retval.dims; i++){
			double[][] minor = new double[retval.dims-1][retval.dims-1];
			for(int j = 0; j < v.length; j++){
				for(int k = 0; k < retval.dims; k++){
					if (k == i) continue;
					minor[j][k < i ? k: k-1] = v[j].coords[k];
				}
			}
			retval.coords[i] = (i%2 == 0? 1 : -1) * det(minor);
		}
		return(retval);		
	}
	
	public double surface(Nvect v){
		double retval = Nvect.mul(this, v).len();
		return retval>0 ? retval : -retval;
	}
	
	public double smul(Nvect v){
		if(v.dims != this.dims) throw new RuntimeException("incorrect dimensions");
		double retval = 0;
		for (int i = 0; i < dims; i++){
			retval += this.coords[i] * v.coords[i];
		}
		return retval;
	}
	
	public Nvect add(Nvect v){
		if(v.dims != this.dims) throw new RuntimeException("incorrect dimensions");
		Nvect retval = new Nvect(this);
		for (int i = 0; i < dims; i++){
			retval.coords[i] += v.coords[i];
		}
		return retval;
	}
	
	public Nvect sub(Nvect v){
		if(v.dims != this.dims) throw new RuntimeException("incorrect dimensions");
		Nvect retval = new Nvect(this);
		for (int i = 0; i < dims; i++){
			retval.coords[i] -= v.coords[i];
		}
		return retval;
	}
	
	public double len(){
		double retval = 0;
		for (int i = 0; i < dims; i++){
			retval += this.coords[i] * this.coords[i];
		}
		return Math.sqrt(retval);
	}	
	
	public double cos(Nvect v){
		return this.smul(v) / this.len() / v.len();	
	}
	
	public double alpha(Nvect v){
		return Math.acos(this.cos(v));
	}
	
	public static double cos(Nvect a, Nvect b){
		return a.smul(b) / a.len() / b.len();	
	}
	
	public static double alpha(Nvect a, Nvect b){
		return a.alpha(b);
	}
}
