/*
 * Created on Dec 8, 2004
 *
 */
package mayday.statistics.probefeature;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JOptionPane;

import mayday.core.MasterTable;
import mayday.core.MaydayDefaults;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.meta.MIGroup;
import mayday.core.meta.types.DoubleMIO;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.ProbelistPlugin;

/**
 * @author gehlenbo
 *
 */
public class PearsonCorrelation
extends AbstractProbeFeaturePlugin
implements ProbelistPlugin
{

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {	
		PluginInfo pli= new PluginInfo(
				(Class)this.getClass(),
				"PAS.statistics.pcc",
				new String[0], 
				Constants.MC_PROBELIST,
				(HashMap<String,Object>)null,
				"Nils Gehlenborg",
				"neil@mangojelly.org",
				"Compute Pearson correlation coefficient with respect to a target probe.",
				"Pearson Correlation Coefficient");
		pli.addCategory(MC);
		return pli;
	}


	@SuppressWarnings("unchecked")
	public List<ProbeList> run( List<ProbeList>probeLists, MasterTable masterTable )
	{
		LinkedList<ProbeList> result = new LinkedList<ProbeList>();
		// get unique probes first
		ProbeList l_uniqueProbeList = new ProbeList( masterTable.getDataSet(), false );

		// this list is sorted according to the sorting of the layers, top layer at the top of the list
		java.util.List l_uniqueProbes = new ArrayList();

		// create a unique subset of the input probe lists
		for ( int i = 0; i < probeLists.size(); ++i )
		{
			ProbeList l_newProbeList = new ProbeList( masterTable.getDataSet(), false );    
			ProbeList l_probeList = (ProbeList)probeLists.get( i );

			// extract new probes
			l_newProbeList.setOperation( l_uniqueProbeList.invert( false ), l_probeList, ProbeList.AND_MODE );

			// store new probes
			l_uniqueProbeList.setOperation( l_uniqueProbeList, l_probeList, ProbeList.OR_MODE );

			l_uniqueProbes.addAll( 0, l_newProbeList.toCollection() );
		}

		// get target probe
		Probe l_targetProbe = null;

		String l_message = "Enter a non-empty name for this column.";

		Collections.sort( l_uniqueProbes );

		l_targetProbe = (Probe)JOptionPane.showInputDialog( null,
				l_message,
				MaydayDefaults.Messages.INFORMATION_TITLE,
				JOptionPane.INFORMATION_MESSAGE,
				null,
				l_uniqueProbes.toArray(),
				null );

		// quit if the user pressed cancel
		if ( l_targetProbe == null )
		{
			return ( result );
		}

		// create MIO group
		MIGroup l_mioGroup = masterTable.getDataSet().getMIManager().newGroup("PAS.MIO.Double", 
				"Pearson correlation with \"" + l_targetProbe.getName() + "\"."
				,"/Probe Statistic/");

		// compute mean of all probes in the probe lists and attach the mean as
		// an mio to each probe
		for ( int i = 0; i < l_uniqueProbes.size(); ++i )
		{
			Probe l_probe = (Probe)l_uniqueProbes.get( i );

			Double l_pcc = new Double( l_targetProbe.getPearsonCorrelationCoefficient( l_probe, 0 ) );

			if ( l_pcc != null )
			{
				DoubleMIO l_mio = new DoubleMIO( l_pcc );
				l_mioGroup.add( l_probe, l_mio );
			}      
		}

		return ( result );
	}


	@Override
	public void init() {
	}
}
