/*
 * Created on Dec 8, 2004
 *
 */
package mayday.statistics.probefeature;

import java.util.HashMap;
import java.util.List;

import mayday.core.MasterTable;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.meta.MIGroup;
import mayday.core.meta.types.DoubleMIO;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.ProbelistPlugin;
import mayday.core.structures.linalg.vector.DoubleVector;

/**
 * @author gehlenbo
 *
 */
public class Median
extends AbstractProbeFeaturePlugin
implements ProbelistPlugin
{

	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli= new PluginInfo(
				this.getClass(),
				"PAS.statistics.median",
				new String[0], 
				Constants.MC_PROBELIST,
				(HashMap<String,Object>)null,
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Compute median of expression value across all experiments.",
				"Probe Median");
		pli.addCategory(MC);
		return pli;
	}


	public List<ProbeList> run( List<ProbeList> probeLists, MasterTable masterTable )
	{
		List<Probe> pl = ProbeList.mergeProbeLists(probeLists, masterTable);

		// create MIO group
		MIGroup l_mioGroup = masterTable.getDataSet().getMIManager().newGroup("PAS.MIO.Double", 
				"Median expression","/Probe Statistic/");

		// compute mean of all probes in the probe lists and attach the mean as
		// an mio to each probe
		DoubleVector av = new DoubleVector(masterTable.getNumberOfExperiments());
		
		for (Probe pb : pl) {
			av.set(pb.getValues());
			Double val = av.median();
			if ( val != null ) {
				DoubleMIO l_mio = new DoubleMIO( val );
				l_mioGroup.add( pb, l_mio );
			}
		}

		return null;
	}


	@Override
	public void init() {
	}
}
