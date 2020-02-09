package palvelu_varaus;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.FillLayout;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.RowLayout;
import swing2swt.layout.BorderLayout;
import org.eclipse.swt.widgets.TableItem;

import ApuMetodeja.ApuMetodeja;
import laskutus.LaskuPoistoVarmistusDialog;
import laskutus.LuoLaskuGUI;
import laskutus.MuokkaaLaskuGUI;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;

public class PalveluvarausEtusivuGUI {

	protected Shell palveluvarausEtusivuShl;
	private Table palveluvarausHallintaTbl;
	private Composite composite_1;
	private Button lisaaPalveluvarausBtn;
	private Button muokkaaPalveluvarausBtn;
	private Button poistaPalveluvarausBtn;
	private TableColumn palveluvarausId;
	private TableColumn asiakasId;
	private TableColumn palveluAloituspvm;
	private Button palveluvarausRaportointiBtn;
	private LisaaPalveluvarausGUI lisaaPalveluvarausGui;
	private MuokkaaPalveluvarausGUI muokkaaPalveluvarausGui;
	private NaytaPalveluvarausTiedotGUI naytaPalveluvarausTiedotGui;
	private PalveluvarausRaportointiGUI palveluvarausRaportointiGui;
	private Label palveluvarausListaLbl;
	private Button naytaTiedotBtn;
	private Connection conn;
	private int valittuPalveluvarausID;
	private PalveluvarausPoistoVarmistusDialog palveluvarausPoistoVarmistusDialog;
	private PalveluvarausEtusivuGUI palveluvarausEtusivuGui;
	private Button takaisinBtn;


	/**
	 * Open the window.
	 * @throws Exception 
	 */
	public void open() throws Exception {
		yhdista();
		createContents();
		// Avautuessa keskelle
		Display display = ApuMetodeja.centerWindow(palveluvarausEtusivuShl);
		populateEtusivuTbl(palveluvarausHallintaTbl);
		
		takaisinBtn = new Button(palveluvarausEtusivuShl, SWT.NONE);
		takaisinBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				palveluvarausEtusivuShl.close();
			}
		});
		takaisinBtn.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		takaisinBtn.setText("Takaisin");
		new Label(palveluvarausEtusivuShl, SWT.NONE);
		palveluvarausEtusivuShl.open();
		palveluvarausEtusivuShl.layout();
		while (!palveluvarausEtusivuShl.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	public void yhdista() throws SQLException, Exception {
		conn = null;
		String url = "jdbc:mariadb://localhost:3306/vp"; // palvelin = localhost, :portti annettu asennettaessa, tietokannan nimi
		try {
			// ota yhteys kantaan, kayttaja = root, salasana = root
			conn=DriverManager.getConnection(url,"root","root");
		}
		catch (SQLException e) { // tietokantaan ei saada yhteyttä
			conn = null;
			throw e;
		}
		catch (Exception e ) { // JDBC ajuria ei löydy
			throw e;
		}
	}
	/*
	Suljetaan tietokantayhteys
	*/
	public void sulje_kanta() throws SQLException, Exception {
		// suljetaan		
		try {
			// sulje yhteys kantaan
			conn.close ();
		}
		catch (SQLException e) { // tietokantavirhe
			throw e;
		}
		catch (Exception e ) { // muu virhe tapahtui
			throw e;
		}
	}

	public void populateEtusivuTbl(Table etusivuLaskutusTbl) throws SQLException {
		
		// SQL-kysely ja tulosjoukon valmistelu
		String sql = "SELECT PalveluvarausID, AsiakasID, aloituspvm FROM palvelun_varaus";
		ResultSet tulosjoukko = null;
		PreparedStatement lause = null;
		
		try {
			// luo PreparedStatement-olio sql-lauseelle
			lause = conn.prepareStatement(sql);
			tulosjoukko = lause.executeQuery();	
			if (tulosjoukko == null) {
				System.out.println("Tyhjä??");
			}
		} catch (SQLException se) {
            // SQL virheet, TODO
            se.printStackTrace();
        } catch (Exception e) {
            // JDBC virheet, TODO
            e.printStackTrace();
		}
		
		// sarakkeiden maaran haku alempaa taulun tayttamista varten iteroinnilla
        java.sql.ResultSetMetaData rsmd = tulosjoukko.getMetaData();
        int columnsNumber = rsmd.getColumnCount();
        
        // iteroidaan tulosjoukkoa lapi niin kauan kun riittaa, lisataan uudet TableItem:t ja niiden sisalto
		try {
			TableItem item;
			while (tulosjoukko.next ()){
	            item = new TableItem(etusivuLaskutusTbl, SWT.NONE);
	            for (int i = 1; i <= columnsNumber; i++) {
	            	if (tulosjoukko.getString(i) == null) {
		                item.setText(i - 1, "");
	            	}
	            	else {
	            		item.setText(i - 1, tulosjoukko.getString(i));

	            	}
			}
		}
		}catch (SQLException e) {
			e.printStackTrace();
		}catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	/**
	 * @wbp.parser.entryPoint
	 */
	protected void createContents() {
		palveluvarausEtusivuShl = new Shell(SWT.CLOSE | SWT.MIN);
		palveluvarausEtusivuShl.setSize(600, 446);
		
		
		// "Responsiivisuus - maaritelty tassa vain 1920x1080 resoluutiolle, pidemmalle kehitetyssa ohjelmassa pitaisi muillekin resoluutioille"
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		if (screenSize.getWidth() < 1600){
			palveluvarausEtusivuShl.setSize((int)(screenSize.getWidth() / 2.15), (int) (screenSize.getHeight() / 1.7));
		}
		else if (screenSize.getWidth() > 1600){
			palveluvarausEtusivuShl.setSize((int)(screenSize.getWidth() / 3.2), (int) (screenSize.getHeight() / 2.5));
		}
		else if (screenSize.getWidth() == 1600) {
			palveluvarausEtusivuShl.setSize((int) (screenSize.getWidth() / 3.4), (int) (screenSize.getHeight() / 2.2));
		}
		
		palveluvarausEtusivuShl.setText("Palveluvarausten hallinta");
		palveluvarausEtusivuShl.setLayout(new GridLayout(2, false));
		
		Composite composite = new Composite(palveluvarausEtusivuShl, SWT.V_SCROLL);
		composite.setLayout(new GridLayout(1, false));
		composite.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, true, 1, 1));
		
		
		// Palvelutaulu
		palveluvarausListaLbl = new Label(composite, SWT.NONE);
		palveluvarausListaLbl.setText("Alla on lista luoduista palveluvarauksista");
		
		palveluvarausHallintaTbl = new Table(composite, SWT.BORDER | SWT.FULL_SELECTION);
		GridData gd_palveluvarausHallintaTbl = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_palveluvarausHallintaTbl.heightHint = 311;
		palveluvarausHallintaTbl.setLayoutData(gd_palveluvarausHallintaTbl);
		palveluvarausHallintaTbl.setHeaderVisible(true);
		palveluvarausHallintaTbl.setLinesVisible(true);
		
		
		// Taulun kentat
		palveluvarausId = new TableColumn(palveluvarausHallintaTbl, SWT.NONE);
		palveluvarausId.setWidth(137);
		palveluvarausId.setText("Palveluvaraus ID");
		
		asiakasId = new TableColumn(palveluvarausHallintaTbl, SWT.NONE);
		asiakasId.setWidth(128);
		asiakasId.setText("Asiakas ID");
		
		palveluAloituspvm = new TableColumn(palveluvarausHallintaTbl, SWT.NONE);
		palveluAloituspvm.setWidth(117);
		palveluAloituspvm.setText("Aloituspäivämäärä");
		
		composite_1 = new Composite(palveluvarausEtusivuShl, SWT.NONE);
		composite_1.setLayout(new GridLayout(1, false));
		composite_1.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		new Label(composite_1, SWT.NONE);
		
		
		// Lisaa palvelu nappi
		lisaaPalveluvarausBtn = new Button(composite_1, SWT.NONE);
		GridData gd_lisaaPalveluvarausBtn = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lisaaPalveluvarausBtn.widthHint = 120;
		lisaaPalveluvarausBtn.setLayoutData(gd_lisaaPalveluvarausBtn);
		lisaaPalveluvarausBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				lisaaPalveluvarausGui = new LisaaPalveluvarausGUI();
				lisaaPalveluvarausGui.setPalveluvarausEtusivuGui(palveluvarausEtusivuGui);
				lisaaPalveluvarausGui.setLisaaPalveluvarausGui(lisaaPalveluvarausGui);
				palveluvarausEtusivuShl.close();
				try {
					lisaaPalveluvarausGui.open();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		lisaaPalveluvarausBtn.setText("Lisää uusi varaus");
		
		
		// Muokkaa palveluta nappi
		muokkaaPalveluvarausBtn = new Button(composite_1, SWT.NONE);
		GridData gd_muokkaaPalveluvarausBtn = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_muokkaaPalveluvarausBtn.widthHint = 120;
		muokkaaPalveluvarausBtn.setLayoutData(gd_muokkaaPalveluvarausBtn);
		muokkaaPalveluvarausBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				muokkaaPalveluvarausGui = new MuokkaaPalveluvarausGUI();
				muokkaaPalveluvarausGui.setMuokkaaPalveluvarausGui(muokkaaPalveluvarausGui);
				muokkaaPalveluvarausGui.setPalveluvarausEtusivuGui(palveluvarausEtusivuGui);
				
				TableItem valittuPalveluvarausTbl[] = palveluvarausHallintaTbl.getSelection();
				if (valittuPalveluvarausTbl.length <1) {
					System.out.println("Valitse yksi");
				}
				else {
					muokkaaPalveluvarausGui.setValittuPalveluvarausID(Integer.parseInt(valittuPalveluvarausTbl[0].getText(0)));

					
					try {
						palveluvarausEtusivuShl.close();
						muokkaaPalveluvarausGui.open();
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
			
		});
		muokkaaPalveluvarausBtn.setText("Muokkaa varausta");
		
		
		// Nayta tiedot nappi
		naytaTiedotBtn = new Button(composite_1, SWT.NONE);
		naytaTiedotBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//TODO MUOKKAA PALVELU NAPPI KUUNTELIJA
				// Valinnat talteen ja valitetaan uudelle ikkunalle (luokkaoliolle)
				naytaPalveluvarausTiedotGui = new NaytaPalveluvarausTiedotGUI();
				TableItem valittuPalveluvarausTbl[] = palveluvarausHallintaTbl.getSelection();
				if (valittuPalveluvarausTbl.length <1) {
					System.out.println("Valitse yksi");
				}
				else {
					naytaPalveluvarausTiedotGui.setPalveluvarausEtusivuGui(getPalveluvarausEtusivuGui());
					naytaPalveluvarausTiedotGui.setValittuPalveluvarausID(Integer.parseInt(valittuPalveluvarausTbl[0].getText(0)));
					
					try {
						palveluvarausEtusivuShl.close();
						naytaPalveluvarausTiedotGui.open();
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
		GridData gd_naytaTiedotBtn = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_naytaTiedotBtn.widthHint = 120;
		naytaTiedotBtn.setLayoutData(gd_naytaTiedotBtn);
		naytaTiedotBtn.setText("Näytä tiedot");
		new Label(composite_1, SWT.NONE);
		
		
		// Poista palvelu nappi
		poistaPalveluvarausBtn = new Button(composite_1, SWT.NONE);
		GridData gd_poistaPalveluvarausBtn = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_poistaPalveluvarausBtn.widthHint = 120;
		poistaPalveluvarausBtn.setLayoutData(gd_poistaPalveluvarausBtn);
		poistaPalveluvarausBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// Valinnat talteen ja valitetaan uudelle ikkunalle (luokkaoliolle)
				TableItem valittuPalveluvarausTbl[] = palveluvarausHallintaTbl.getSelection();
				if (valittuPalveluvarausTbl.length <1) {
					System.out.println("Valitse yksi");
				}
				else {
					setValittuPalveluvarausID(Integer.parseInt(valittuPalveluvarausTbl[0].getText(0)));
					palveluvarausPoistoVarmistusDialog = new PalveluvarausPoistoVarmistusDialog();
					palveluvarausPoistoVarmistusDialog.setPalveluvarausEtusivuGui(getPalveluvarausEtusivuGui());
					try {
						palveluvarausPoistoVarmistusDialog.open();
					} catch (Exception e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
				}
			}
		});
		poistaPalveluvarausBtn.setText("Poista varaus");
		new Label(composite_1, SWT.NONE);
		
		
		// Raportointi nappi
		palveluvarausRaportointiBtn = new Button(composite_1, SWT.NONE);
		GridData gd_palveluvarausRaportointiBtn = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_palveluvarausRaportointiBtn.widthHint = 120;
		palveluvarausRaportointiBtn.setLayoutData(gd_palveluvarausRaportointiBtn);
		palveluvarausRaportointiBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO LISAA RAPORTOINTI NAPPI KUUNTELIJA
				palveluvarausRaportointiGui = new PalveluvarausRaportointiGUI();
				try {
					palveluvarausRaportointiGui.open();
					
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		palveluvarausRaportointiBtn.setText("Raportointi");

	}

	public int getValittuPalveluvarausID() {
		return valittuPalveluvarausID;
	}

	public void setValittuPalveluvarausID(int valittuPalveluvarausID) {
		this.valittuPalveluvarausID = valittuPalveluvarausID;
	}

	public PalveluvarausEtusivuGUI getPalveluvarausEtusivuGui() {
		return palveluvarausEtusivuGui;
	}

	public void setPalveluvarausEtusivuGui(PalveluvarausEtusivuGUI palveluvarausEtusivuGui) {
		this.palveluvarausEtusivuGui = palveluvarausEtusivuGui;
	}

	public Table getPalveluvarausHallintaTbl() {
		return palveluvarausHallintaTbl;
	}

	public void setPalveluvarausHallintaTbl(Table palveluvarausHallintaTbl) {
		this.palveluvarausHallintaTbl = palveluvarausHallintaTbl;
	}
	

}
