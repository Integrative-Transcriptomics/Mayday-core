/*
 * File Gaussian.java
 * Created on 08.03.2004
 *As part of package MathObjects.Functions
 *By Janko Dietzsch
 *
 */
package mayday.core.math.functions;

/**
 * This class gives back the value of the Gaussian kernel. It is designed to expect only 
 * a distance as input parameter Distance.
 * 
 * @author  Janko Dietzsch
 * @version 0.1
 * 
 * 
 */
public class GaussianKernel implements IRotationalKernelFunction {
	private double sigma; 
	
	public GaussianKernel(double Sigma){
		this.sigma = Math.abs(Sigma);		
	}
	
	
	
	/* (non-Javadoc)
	 * @see MathObjects.Functions.IOneDimFunctions#getFunctionValueOf(double)
	 */
	 /**
	  * This method returns the Gaussian kernel value for the distance given by the 
	  * input parameter.
	  * @param Distance gives the distance to the center of the Gaussian kernel
	  */
	public double getFunctionValueOf(double Distance) {
		double Expo =  - Math.pow( Distance, 2.0) / (2.0 * Math.pow( sigma, 2.0));
		return Math.exp( Expo );
	}
	/**
	 * @return
	 */
	public double getSigma() {
		return this.sigma;
	}

	/**
	 * @param Sigma
	 */
	public void setSigma(double Sigma) {
		this.sigma = Math.abs(Sigma);
	}
    
    /**
     * Get the defining parameter of this kernel
     * @return
     */
    public double getRadius() {
        return this.sigma;
    }

    /**
     * Set the defining parameter of this kernel
     * @param Radius
     */
    public void setRadius(double Radius) {
        this.sigma = Math.abs(Radius);
    }

}
