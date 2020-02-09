package Aloitussivu;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.sql.SQLException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import swing2swt.layout.BorderLayout;
import toimipisteet.ToimipisteEtusivuGUI;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Text;

import asiakkaat.AsiakkaatEtusivuGUI;
import laskutus.LaskutusEtusivuGUI;
import majoitus.MajoitusEtusivuGUI;
import majoitus_varaus.MajoitusvarausEtusivuGUI;
import majoitus_varaus.MajoitusvarausEtusivuGUI;
import palvelu_varaus.PalveluvarausEtusivuGUI;
import palvelut.PalvelutEtusivuGUI;

import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.wb.swt.SWTResourceManager;

import ApuMetodeja.ApuMetodeja;

import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

public class AloitussivuGUI {

	protected Shell aloitussivuShl;
	private ToimipisteEtusivuGUI toimipisteEtusivuGui;
	private PalvelutEtusivuGUI palvelutEtusivuGui;
	private AsiakkaatEtusivuGUI asiakkaatEtusivuGui;
	private LaskutusEtusivuGUI laskutusEtusivuGui;
	private PalveluvarausEtusivuGUI palveluvarausEtusivuGui;
	private MajoitusvarausEtusivuGUI majoitusvarausEtusivuGui;
	private MajoitusEtusivuGUI majoitusEtusivuGui;
	
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			AloitussivuGUI window = new AloitussivuGUI();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		createContents();
		// Avattaessa asetellaan keskelle
		Display display = ApuMetodeja.centerWindow(aloitussivuShl);
		aloitussivuShl.open();
		aloitussivuShl.layout();
		while (!aloitussivuShl.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		aloitussivuShl = new Shell(SWT.CLOSE | SWT.MIN);
		aloitussivuShl.setSize(452, 537);
		
		// "Responsiivisuus - maaritelty tassa vain 1920x1080 resoluutiolle, pidemmalle kehitetyssa ohjelmassa pitaisi muillekin resoluutioille"
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		
		if (screenSize.getWidth() < 1600){
			aloitussivuShl.setSize((int)(screenSize.getWidth() / 2.7), (int) (screenSize.getHeight() / 1.6));
		}
		else if (screenSize.getWidth() > 1600){
			aloitussivuShl.setSize((int)(screenSize.getWidth() / 4), (int) (screenSize.getHeight() / 1.9));
		}
		else if (screenSize.getWidth() == 1600) {
			aloitussivuShl.setSize((int) (screenSize.getWidth() / 3.4), (int) (screenSize.getHeight() / 2.2));
		}
		
		aloitussivuShl.setText("Village People Oy hallintajärjestelmä");
		aloitussivuShl.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Composite composite = new Composite(aloitussivuShl, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		
		new Label(composite, SWT.NONE);

		Label tervetuloaLbl = new Label(composite, SWT.CENTER);
		tervetuloaLbl.setFont(SWTResourceManager.getFont("Segoe UI", 13, SWT.NORMAL));
		tervetuloaLbl.setAlignment(SWT.CENTER);
		tervetuloaLbl.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1));
		tervetuloaLbl.setText("Tervetuloa Village People Oy hallintajärjestelmään");
		new Label(composite, SWT.NONE);
		
		Label lblValitseAltaHaluamasi = new Label(composite, SWT.NONE);
		lblValitseAltaHaluamasi.setFont(SWTResourceManager.getFont("Segoe UI", 11, SWT.NORMAL));
		lblValitseAltaHaluamasi.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		lblValitseAltaHaluamasi.setText("Valitse alta haluamasi toiminto");
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		
		Button toimipisteHallintaBtn = new Button(composite, SWT.NONE);
		toimipisteHallintaBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO TOIMIPISTE NAPPI KUUNTELIJA
				toimipisteEtusivuGui = new ToimipisteEtusivuGUI();
				toimipisteEtusivuGui.setToimipisteEtusivuGui(toimipisteEtusivuGui);
				try {
					toimipisteEtusivuGui.open();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		GridData gd_toimipisteHallintaBtn = new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1);
		gd_toimipisteHallintaBtn.widthHint = 150;
		gd_toimipisteHallintaBtn.minimumWidth = 150;
		toimipisteHallintaBtn.setLayoutData(gd_toimipisteHallintaBtn);
		toimipisteHallintaBtn.setText("Toimipisteiden hallinta");
		new Label(composite, SWT.NONE);
		
		Button majoitusHallintaBtn = new Button(composite, SWT.NONE);
		majoitusHallintaBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO MAJOITUS NAPPI KUUNTELIJA
				majoitusEtusivuGui = new MajoitusEtusivuGUI();
				majoitusEtusivuGui.setMajoitusEtusivuGui(majoitusEtusivuGui);
				try {
					majoitusEtusivuGui.open();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		GridData gd_majoitusHallintaBtn = new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1);
		gd_majoitusHallintaBtn.widthHint = 150;
		gd_majoitusHallintaBtn.minimumWidth = 150;
		majoitusHallintaBtn.setLayoutData(gd_majoitusHallintaBtn);
		majoitusHallintaBtn.setText("Majoitusten hallinta");
		new Label(composite, SWT.NONE);
		
		Button palveluHallintaBtn = new Button(composite, SWT.NONE);
		palveluHallintaBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO PALVELU NAPPI KUUNTELIJA
				palvelutEtusivuGui = new PalvelutEtusivuGUI();
				palvelutEtusivuGui.setPalvelutEtusivuGui(palvelutEtusivuGui);
				palvelutEtusivuGui.open();
				
			}
		});
		GridData gd_palveluHallintaBtn = new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1);
		gd_palveluHallintaBtn.widthHint = 150;
		gd_palveluHallintaBtn.minimumWidth = 150;
		palveluHallintaBtn.setLayoutData(gd_palveluHallintaBtn);
		palveluHallintaBtn.setText("Palveluiden hallinta");
		new Label(composite, SWT.NONE);
		
		Button asiakasHallintaBtn = new Button(composite, SWT.NONE);
		asiakasHallintaBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO ASIAKAS NAPPI KUUNTELIJA
				asiakkaatEtusivuGui = new AsiakkaatEtusivuGUI();
				asiakkaatEtusivuGui.setAsiakkaatEtusivuGui(asiakkaatEtusivuGui);
				asiakkaatEtusivuGui.open();
			}
		});
		GridData gd_asiakasHallintaBtn = new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1);
		gd_asiakasHallintaBtn.minimumWidth = 1;
		gd_asiakasHallintaBtn.widthHint = 150;
		asiakasHallintaBtn.setLayoutData(gd_asiakasHallintaBtn);
		asiakasHallintaBtn.setText("Asiakkaiden hallinta");
		new Label(composite, SWT.NONE);
		
		Button palveluvarausBtn = new Button(composite, SWT.NONE);
		palveluvarausBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				palveluvarausEtusivuGui = new PalveluvarausEtusivuGUI();
				palveluvarausEtusivuGui.setPalveluvarausEtusivuGui(palveluvarausEtusivuGui);
				try {
					palveluvarausEtusivuGui.open();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
		});
		GridData gd_palveluvarausBtn = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
		gd_palveluvarausBtn.widthHint = 150;
		palveluvarausBtn.setLayoutData(gd_palveluvarausBtn);
		palveluvarausBtn.setText("Palveluvarausten hallinta");
		new Label(composite, SWT.NONE);
		
		Button majoitusvarausBtn = new Button(composite, SWT.NONE);
		majoitusvarausBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				majoitusvarausEtusivuGui = new MajoitusvarausEtusivuGUI();
				try {
					majoitusvarausEtusivuGui.setMajoitusvarausEtusivuGui(majoitusvarausEtusivuGui);
					majoitusvarausEtusivuGui.open();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		GridData gd_majoitusvarausBtn = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
		gd_majoitusvarausBtn.widthHint = 150;
		majoitusvarausBtn.setLayoutData(gd_majoitusvarausBtn);
		majoitusvarausBtn.setText("Majoitusvarausten hallinta");
		new Label(composite, SWT.NONE);
		
		Button laskutusBtn = new Button(composite, SWT.NONE);
		laskutusBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO LASKUTUS NAPPI KUUNTELIJA
				laskutusEtusivuGui = new LaskutusEtusivuGUI();
				try {
					laskutusEtusivuGui.setLaskutusEtusivuGui(laskutusEtusivuGui);
					laskutusEtusivuGui.open();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			
			}
		});
		GridData gd_laskutusBtn = new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1);
		gd_laskutusBtn.minimumWidth = 150;
		laskutusBtn.setLayoutData(gd_laskutusBtn);
		laskutusBtn.setText("Laskutus");

	}
}
