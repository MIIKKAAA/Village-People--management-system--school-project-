package majoitus_varaus;

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

public class MajoitusvarausEtusivuGUI {

	protected Shell majoitusvarausEtusivuShl;
	private Table majoitusvarausHallintaTbl;
	private Composite composite_1;
	private Button lisaaMajoitusvarausBtn;
	private Button muokkaaMajoitusvarausBtn;
	private Button poistaMajoitusvarausBtn;
	private TableColumn majoitusvarausId;
	private TableColumn asiakasId;
	private TableColumn majoitusAloituspvm;
	private Button majoitusvarausRaportointiBtn;
	private LisaaMajoitusvarausGUI lisaaMajoitusvarausGui;
	private MuokkaaMajoitusvarausGUI muokkaaMajoitusvarausGui;
	private NaytaMajoitusvarausTiedotGUI naytaMajoitusvarausTiedotGui;
	private MajoitusRaportointiGUI majoitusvarausRaportointiGui;
	private Label majoitusvarausListaLbl;
	private Button naytaTiedotBtn;
	private Connection conn;
	private int valittuMajoitusvarausID;
	private MajoitusvarausPoistoVarmistusDialog majoitusvarausPoistoVarmistusDialog;
	private MajoitusvarausEtusivuGUI majoitusvarausEtusivuGui;
	private Button takaisinBtn;


	/**
	 * Open the window.
	 * @throws Exception 
	 */
	public void open() throws Exception {
		yhdista();
		createContents();
		// Avautuessa keskelle
		Display display = ApuMetodeja.centerWindow(majoitusvarausEtusivuShl);
		populateEtusivuTbl(majoitusvarausHallintaTbl);
		
		takaisinBtn = new Button(majoitusvarausEtusivuShl, SWT.NONE);
		takaisinBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				majoitusvarausEtusivuShl.close();
			}
		});
		takaisinBtn.setText("Takaisin");
		new Label(majoitusvarausEtusivuShl, SWT.NONE);
		majoitusvarausEtusivuShl.open();
		majoitusvarausEtusivuShl.layout();
		while (!majoitusvarausEtusivuShl.isDisposed()) {
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
		String sql = "SELECT MajoitusvarausID, AsiakasID, aloituspvm FROM majoitusvaraus";
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
		majoitusvarausEtusivuShl = new Shell(SWT.CLOSE | SWT.MIN);
		majoitusvarausEtusivuShl.setSize(600, 446);
		
		
		// "Responsiivisuus - maaritelty tassa vain 1920x1080 resoluutiolle, pidemmalle kehitetyssa ohjelmassa pitaisi muillekin resoluutioille"
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		if (screenSize.getWidth() < 1600){
			majoitusvarausEtusivuShl.setSize((int)(screenSize.getWidth() / 2.15), (int) (screenSize.getHeight() / 1.7));
		}
		else if (screenSize.getWidth() > 1600){
			majoitusvarausEtusivuShl.setSize((int)(screenSize.getWidth() / 3.2), (int) (screenSize.getHeight() / 2.5));
		}
		else if (screenSize.getWidth() == 1600) {
			majoitusvarausEtusivuShl.setSize((int) (screenSize.getWidth() / 3.4), (int) (screenSize.getHeight() / 2.2));
		}
		
		majoitusvarausEtusivuShl.setText("Majoitusvarausten hallinta");
		majoitusvarausEtusivuShl.setLayout(new GridLayout(2, false));
		
		Composite composite = new Composite(majoitusvarausEtusivuShl, SWT.V_SCROLL);
		composite.setLayout(new GridLayout(1, false));
		composite.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, true, 1, 1));
		
		
		// Majoitustaulu
		majoitusvarausListaLbl = new Label(composite, SWT.NONE);
		majoitusvarausListaLbl.setText("Alla on lista luoduista majoitusvarauksista");
		
		majoitusvarausHallintaTbl = new Table(composite, SWT.BORDER | SWT.FULL_SELECTION);
		GridData gd_majoitusvarausHallintaTbl = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_majoitusvarausHallintaTbl.heightHint = 311;
		majoitusvarausHallintaTbl.setLayoutData(gd_majoitusvarausHallintaTbl);
		majoitusvarausHallintaTbl.setHeaderVisible(true);
		majoitusvarausHallintaTbl.setLinesVisible(true);
		
		
		// Taulun kentat
		majoitusvarausId = new TableColumn(majoitusvarausHallintaTbl, SWT.NONE);
		majoitusvarausId.setWidth(137);
		majoitusvarausId.setText("Majoitusvaraus ID");
		
		asiakasId = new TableColumn(majoitusvarausHallintaTbl, SWT.NONE);
		asiakasId.setWidth(128);
		asiakasId.setText("Asiakas ID");
		
		majoitusAloituspvm = new TableColumn(majoitusvarausHallintaTbl, SWT.NONE);
		majoitusAloituspvm.setWidth(117);
		majoitusAloituspvm.setText("Aloituspäivämäärä");
		
		composite_1 = new Composite(majoitusvarausEtusivuShl, SWT.NONE);
		composite_1.setLayout(new GridLayout(1, false));
		composite_1.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		new Label(composite_1, SWT.NONE);
		
		
		// Lisaa majoitus nappi
		lisaaMajoitusvarausBtn = new Button(composite_1, SWT.NONE);
		GridData gd_lisaaMajoitusvarausBtn = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lisaaMajoitusvarausBtn.widthHint = 120;
		lisaaMajoitusvarausBtn.setLayoutData(gd_lisaaMajoitusvarausBtn);
		lisaaMajoitusvarausBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				lisaaMajoitusvarausGui = new LisaaMajoitusvarausGUI();
				lisaaMajoitusvarausGui.setLisaaMajoitusvarausGui(lisaaMajoitusvarausGui);
				lisaaMajoitusvarausGui.setMajoitusvarausEtusivuGui(majoitusvarausEtusivuGui);
				majoitusvarausEtusivuShl.close();
				try {
					lisaaMajoitusvarausGui.open();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		lisaaMajoitusvarausBtn.setText("Lisää uusi varaus");
		
		
		// Muokkaa majoitusta nappi
		muokkaaMajoitusvarausBtn = new Button(composite_1, SWT.NONE);
		GridData gd_muokkaaMajoitusvarausBtn = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_muokkaaMajoitusvarausBtn.widthHint = 120;
		muokkaaMajoitusvarausBtn.setLayoutData(gd_muokkaaMajoitusvarausBtn);
		muokkaaMajoitusvarausBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				muokkaaMajoitusvarausGui = new MuokkaaMajoitusvarausGUI();
				muokkaaMajoitusvarausGui.setMuokkaaMajoitusvarausGui(muokkaaMajoitusvarausGui);
				muokkaaMajoitusvarausGui.setMajoitusvarausEtusivuGui(majoitusvarausEtusivuGui);
				
				TableItem valittuMajoitusvarausTbl[] = majoitusvarausHallintaTbl.getSelection();
				if (valittuMajoitusvarausTbl.length <1) {
					System.out.println("Valitse yksi");
				}
				else {
					muokkaaMajoitusvarausGui.setValittuMajoitusvarausID(Integer.parseInt(valittuMajoitusvarausTbl[0].getText(0)));
					
					try {
						majoitusvarausEtusivuShl.close();
						muokkaaMajoitusvarausGui.open();
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
		muokkaaMajoitusvarausBtn.setText("Muokkaa varausta");
		
		
		// Nayta tiedot nappi
		naytaTiedotBtn = new Button(composite_1, SWT.NONE);
		naytaTiedotBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//TODO MUOKKAA MAJOITUS NAPPI KUUNTELIJA
				// Valinnat talteen ja valitetaan uudelle ikkunalle (luokkaoliolle)
				naytaMajoitusvarausTiedotGui = new NaytaMajoitusvarausTiedotGUI();
				naytaMajoitusvarausTiedotGui.setMajoitusvarausEtusivuGui(majoitusvarausEtusivuGui);
				TableItem valittuMajoitusvarausTbl[] = majoitusvarausHallintaTbl.getSelection();
				if (valittuMajoitusvarausTbl.length <1) {
					System.out.println("Valitse yksi");
				}
				else {
	
					naytaMajoitusvarausTiedotGui.setValittuMajoitusvarausID(Integer.parseInt(valittuMajoitusvarausTbl[0].getText(0)));
					
					try {
						majoitusvarausEtusivuShl.close();
						naytaMajoitusvarausTiedotGui.open();
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
		
		
		// Poista majoitus nappi
		poistaMajoitusvarausBtn = new Button(composite_1, SWT.NONE);
		GridData gd_poistaMajoitusvarausBtn = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_poistaMajoitusvarausBtn.widthHint = 120;
		poistaMajoitusvarausBtn.setLayoutData(gd_poistaMajoitusvarausBtn);
		poistaMajoitusvarausBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// Valinnat talteen ja valitetaan uudelle ikkunalle (luokkaoliolle)
				TableItem valittuMajoitusvarausTbl[] = majoitusvarausHallintaTbl.getSelection();
				if (valittuMajoitusvarausTbl.length <1) {
					System.out.println("Valitse yksi");
				}
				else {
					setValittuMajoitusvarausID(Integer.parseInt(valittuMajoitusvarausTbl[0].getText(0)));
					majoitusvarausPoistoVarmistusDialog = new MajoitusvarausPoistoVarmistusDialog();
					majoitusvarausPoistoVarmistusDialog.setMajoitusvarausEtusivuGui(getMajoitusvarausEtusivuGui());
					try {
						majoitusvarausPoistoVarmistusDialog.open();
					} catch (Exception e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
				}
			}
		});
		poistaMajoitusvarausBtn.setText("Poista varaus");
		new Label(composite_1, SWT.NONE);
		
		
		// Raportointi nappi
		majoitusvarausRaportointiBtn = new Button(composite_1, SWT.NONE);
		GridData gd_majoitusvarausRaportointiBtn = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_majoitusvarausRaportointiBtn.widthHint = 120;
		majoitusvarausRaportointiBtn.setLayoutData(gd_majoitusvarausRaportointiBtn);
		majoitusvarausRaportointiBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO LISAA RAPORTOINTI NAPPI KUUNTELIJA
				majoitusvarausRaportointiGui = new MajoitusRaportointiGUI();
				try {
					majoitusvarausRaportointiGui.open();
					
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		majoitusvarausRaportointiBtn.setText("Raportointi");

	}

	public int getValittuMajoitusvarausID() {
		return valittuMajoitusvarausID;
	}

	public void setValittuMajoitusvarausID(int valittuMajoitusvarausID) {
		this.valittuMajoitusvarausID = valittuMajoitusvarausID;
	}

	public MajoitusvarausEtusivuGUI getMajoitusvarausEtusivuGui() {
		return majoitusvarausEtusivuGui;
	}

	public void setMajoitusvarausEtusivuGui(MajoitusvarausEtusivuGUI majoitusvarausEtusivuGui) {
		this.majoitusvarausEtusivuGui = majoitusvarausEtusivuGui;
	}

	public Table getMajoitusvarausHallintaTbl() {
		return majoitusvarausHallintaTbl;
	}

	public void setMajoitusvarausHallintaTbl(Table majoitusvarausHallintaTbl) {
		this.majoitusvarausHallintaTbl = majoitusvarausHallintaTbl;
	}
	

}
