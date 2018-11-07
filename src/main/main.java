package main;

import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer.Optimum;
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import util_1.NonLinearLeastSolver;
import util_1.TrilaterationFunction;
import util_2.ThreeDistanceToCenterLocation;

public class main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		double[][] positions = new double[][] { { 5.0, -6.0 }, { 13.0, -15.0 }, { 21.0, -3.0 }, { 12.4, -21.2 }, { 12.4, -1.2 } };
		double[] distances = new double[] { 8.06, 13.97, 23.32, 15.31, 24.26 };

		NonLinearLeastSolver solver = new NonLinearLeastSolver(new TrilaterationFunction(positions, distances), new LevenbergMarquardtOptimizer());
		Optimum optimum = solver.solve();

		// the answer
		double[] centroid = optimum.getPoint().toArray();
		
		System.out.println("centroid : { " + centroid[0] + ", " + centroid[1] + " } " );

		// error and geometry information; may throw SingularMatrixException depending the threshold argument provided
		RealVector standardDeviation = optimum.getSigma(0);
		System.out.println("Standard : " + standardDeviation.toString());
		RealMatrix covarianceMatrix = optimum.getCovariances(0);
		System.out.println("ConvarianceMatrix : " + covarianceMatrix.toString());
		
//		ThreeDistanceToCenterLocation.calc(37.45096, 127.12709, 37.450962, 127.127106, 37.450996, 127.12711, 3.06, 4.69, 3.52);
//		System.out.println("Location : x = " + ThreeDistanceToCenterLocation.lat + " / y = " + ThreeDistanceToCenterLocation.lon);
//		ThreeDistanceToCenterLocation.callTestMethod(37.45097, 127.12709);

	}

}
