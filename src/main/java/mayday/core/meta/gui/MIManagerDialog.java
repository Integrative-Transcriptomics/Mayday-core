package mayday.core.meta.gui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;

import mayday.core.DataSetEvent;
import mayday.core.DataSetListener;
import mayday.core.gui.MaydayDialog;
import mayday.core.meta.MIManager;

@SuppressWarnings("serial")
public class MIManagerDialog extends MaydayDialog implements WindowListener, DataSetListener{
		
	private MIManager mimanager;
	private MIGroupSelectionPanel selPanel;
	private MIMenu mema;
	
	public MIManagerDialog(MIManager miManager) {
		mimanager = miManager;
		selPanel = new MIGroupSelectionPanel(mimanager);
		mimanager.getDataSet().addDataSetListener(this);
		init();
	}
	
	private void init() {
		this.setLayout(new BorderLayout());
		
		this.setJMenuBar(getMenuBar());
		
		this.add(selPanel, BorderLayout.CENTER);
		
		selPanel.addInternalMouseListener(new MouseInputAdapter() {
			public void mouseClicked( MouseEvent e ) {       
				if ( e.getButton() == MouseEvent.BUTTON3 ) 
					if ( e.getClickCount() == 1 ) { 
						mema.getPopupMenu().show( MIManagerDialog.this, e.getX(), e.getY() );
					}
			}
		});

		// Add close button with fancy border for seperation
		JButton closeBtn;
		{
			JPanel BottomPanel = new JPanel();
			BoxLayout BottomPanelLayout = new BoxLayout(BottomPanel, javax.swing.BoxLayout.Y_AXIS);
			BottomPanel.setLayout(BottomPanelLayout);
			add(BottomPanel, BorderLayout.SOUTH);
			BottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			{
				JSeparator jSeparator2 = new JSeparator();
				BottomPanel.add(jSeparator2);
				JPanel ButtonPanel = new JPanel();
				BottomPanel.add(ButtonPanel);
				FlowLayout ButtonPanelLayout = new FlowLayout();
				ButtonPanelLayout.setAlignment(FlowLayout.RIGHT);
				ButtonPanel.setLayout(ButtonPanelLayout);
				{
					closeBtn = new JButton();
					ButtonPanel.add(closeBtn);
					closeBtn.setText("Close");
				}
			}
		}
		// make that button actually close the window
		closeBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
			}
		});
		
		this.setTitle("MI Manager for \""+mimanager.getDataSet().getName()+"\"");
		this.addWindowListener(this);
		
		setMinimumSize(new Dimension(600,500));
		pack();
	}
	
	
	private JMenuBar getMenuBar() {
		JMenuBar mb = new JMenuBar();
		
//		JMenu FileMenu = new JMenu("File");
//		FileMenu.add(new MetaInfoImportAction());
//		FileMenu.add(new JSeparator());
//		FileMenu.add(new AbstractAction("Close") {
//			public void actionPerformed(ActionEvent e) {
//				dispose();
//			}
//		});
//				

//		mb.add(FileMenu);
		mema = new MIMenu(selPanel, mimanager, this);
		
		mb.add(mema.getMenu());	
		
		return mb;
	}
	
	
	
	
//
//	public class MetaInfoImportAction extends AbstractAction {
//		public MetaInfoImportAction() {
//			super("Import...");			
//		}
//		public void actionPerformed(ActionEvent a) {
//			PluginInfo pli = PluginManager.getInstance().getPluginFromID("PAS.mio.import.csv");
//			((MetaInfoPlugin)pli.getInstance()).run(null, mimanager);
//		}
//	}
	/*
	public class MetaInfoExportAction extends AbstractAction {
		public MetaInfoExportAction() {
			super("Export...");
		}
		public void actionPerformed(ActionEvent a) {
			PluginInfo pli = PluginManager.getInstance().getPluginFromID("PAS.mio.export.csv");			
			((MetaInfoPlugin)pli.getInstance()).run(selPanel.getSelection(), mimanager);
		}
	}*/
	

	
	public void setVisible(boolean vis) {
		if (selPanel.getSelectableCount()>0 || !vis)  //hide always, show only when helpful
			super.setVisible(vis);
	}
	
	public void windowActivated(WindowEvent arg0) {
	
	}

	public void windowClosed(WindowEvent arg0) {
		selPanel.removeNotify();
		mimanager.getDataSet().removeDataSetListener(this);
	}

	public void windowClosing(WindowEvent arg0) {
	}

	public void windowDeactivated(WindowEvent arg0) {
	}

	public void windowDeiconified(WindowEvent arg0) {
	}

	public void windowIconified(WindowEvent arg0) {
	}

	public void windowOpened(WindowEvent arg0) {
	}

	public void dataSetChanged(DataSetEvent event) {
		if (event.getChange()==DataSetEvent.CLOSING_CHANGE)
		  this.dispose();		
	}
	
}
