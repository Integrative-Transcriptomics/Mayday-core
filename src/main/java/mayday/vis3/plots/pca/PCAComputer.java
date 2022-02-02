package mayday.vis3.plots.pca;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.concurrent.Callable;

import javax.swing.SwingUtilities;

import mayday.core.MasterTable;
import mayday.core.Probe;
import mayday.core.math.JamaSubset.Matrix;
import mayday.core.math.JamaSubset.PCA;
import mayday.core.math.Statistics;
import mayday.core.tasks.AbstractTask;
import mayday.vis3.model.manipulators.Centering;

public class PCAComputer extends AbstractTask {

	public PCAPlotComponent callBack;
	
	public PCAComputer(PCAPlotComponent pcc) {
		super("Computing PCA");
		callBack = pcc;
		start();
	}
	
	public void doWork() {
		try {
			Matrix pca_input = prepareData(callBack.viewModel.getProbes());
			PCA pca = new PCA(pca_input.getArray());
			callBack.PCAData = pca.getResult();
			callBack.EigenValues = pca.getEigenValues();
			/*Object[] temp = doPCA(callBack.viewModel.getProbes(), callBack.viewModel.getDataSet().getMasterTable());
			callBack.PCAData=(Matrix)temp[0];
			callBack.EigenValues=(double[])temp[1];*/
		} catch (Throwable e) {
			callBack.PCAData=null;
			throw new RuntimeException("PCA Computation failed. The reason was a "+e.getClass().getCanonicalName(),e);
		}
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				callBack.updateWithPCAResult(PCAComputer.this);					
			}
		});
	}


	public Object[] doPCA (Collection<Probe> probeList, MasterTable masterTable)
	{
		int n = probeList.size();
		int m = masterTable.getNumberOfExperiments();


		double[][] indat;
		if (callBack.transpose_first)
			indat = new double[m][n];
		else 
			indat = new double[n][m];

		try {
			if (callBack.transpose_first) {
				int i=0;
				for (Probe tmp : probeList) { 
					for (int j=0; j!=m; ++j) {
						indat[j][i] = tmp.getValue(j);
					}
					++i;
				}
			} else { 
				int i=0;
				for (Probe tmp : probeList) { 
					for (int j=0; j!=m; ++j) {
						indat[i][j] = tmp.getValue(j);
					}
					++i;
				}
			}

		} catch (NullPointerException e){
			throw new RuntimeException("Cannot work on Probes containing missing values");
		}
		Matrix indat_matrix = new Matrix(m,n);
		if(callBack.normalize_first){
			indat_matrix = standardize(new Matrix(indat));
		}
		
		PCA pca = new PCA(indat_matrix.getArray());
		
		return new Object[]{pca.getResult(),pca.getEigenValues()};

	}

	/**
	 * prepares a matrix for PCA computation according to user input (transpose and normalize)
	 * @param probeList Set of all probes
	 * @return ready matrix as input-data for PCA
	 */
	private Matrix prepareData (Collection<Probe> probeList){

		int  nrow = probeList.size();
		int ncol = probeList.iterator().next().getValues().length;;
		Matrix pca_indat = new Matrix(nrow,ncol);

		try{
			//put all probes into a matrix (transposed or not according to user input)
			int i=0;
			for (Probe tmp : probeList) {

				//if(!callBack.transpose_first){
					pca_indat.setMatrix(i,i,0,ncol-1,new Matrix(tmp.getValues(),1));
				/*}else{
					// transpose matrix if user wants to
					pca_indat.setMatrix(0,ncol-1,i,i,new Matrix(tmp.getValues(),ncol));
				}*/
				++i;
			}
			// transpose matrix if user wants to
			if(callBack.transpose_first){
				pca_indat = pca_indat.transpose();
			}
			//normalize data if user wants to
			if(!callBack.standardize_first.equals("use original data")){
				pca_indat = standardize(pca_indat);
			}
		} catch (NullPointerException e){
			throw new RuntimeException("Cannot work on Probes containing missing values");
		}

		return pca_indat;
	}


	@Override
	protected void initialize() {
	}

	private Matrix standardize(Matrix A){
		Matrix sdt_Matrix = new Matrix(A.getRowDimension(),A.getColumnDimension());
		for(int i = 0; i < A.getColumnDimension(); i++){
			double[] columns = A.getMatrix(0,A.getRowDimension() - 1,i,i).getColumnPackedCopy();

			if(this.callBack.standardize_first.equals("only center data")){
				columns=new Centering().manipulate(columns);
			}else if (this.callBack.standardize_first.equals("normalize data")){
				Statistics.normalize(columns);
			}
			Matrix std_column = new Matrix(columns,A.getRowDimension());
			sdt_Matrix.setMatrix(0,A.getRowDimension() -1,i,i,std_column);
		}
		sdt_Matrix.timesEquals(1/Math.sqrt(A.getRowDimension()));
		return sdt_Matrix;
	}

	
}
