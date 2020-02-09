package toimipisteet;

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

public class ToimipisteEtusivuGUI {

	protected Shell toimipisteEtusivuShl;
	private Table toimipisteHallintaTbl;
	private Composite composite_1;
	private Button lisaaToimipisteBtn;
	private Button muokkaaToimipisteBtn;
	private Button poistaToimipisteBtn;
	private TableColumn toimipisteId;
	private TableColumn nimi;
	private TableColumn paikkakunta;
	private LisaaToimipisteGUI lisaaToimipisteGui;
	private MuokkaaToimipisteGUI muokkaaToimipisteGui;
	private NaytaToimipisteTiedotGUI naytaToimipisteTiedotGui;
	private Label toimipisteListaLbl;
	private Button naytaTiedotBtn;
	private Connection conn;
	private int valittuToimipisteID;
	private ToimipistePoistoVarmistusDialog toimipistePoistoVarmistusDialog;
	private ToimipisteEtusivuGUI toimipisteEtusivuGui;
	private Button takaisinBtn;


	/**
	 * Open the window.
	 * @throws Exception 
	 */
	public void open() throws Exception {
		yhdista();
		createContents();
		// Avautuessa keskelle
		Display display = ApuMetodeja.centerWindow(toimipisteEtusivuShl);
		populateEtusivuTbl(toimipisteHallintaTbl);
		toimipisteEtusivuShl.open();
		toimipisteEtusivuShl.layout();
		while (!toimipisteEtusivuShl.isDisposed()) {
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
		String sql = "SELECT ToimipisteID, Nimi, Paikkakunta FROM toimipisteet";
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
		toimipisteEtusivuShl = new Shell(SWT.CLOSE | SWT.MIN);
		toimipisteEtusivuShl.setSize(600, 446);
		
		
		// "Responsiivisuus - maaritelty tassa vain 1920x1080 resoluutiolle, pidemmalle kehitetyssa ohjelmassa pitaisi muillekin resoluutioille"
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		if (screenSize.getWidth() < 1600){
			toimipisteEtusivuShl.setSize((int)(screenSize.getWidth() / 2.15), (int) (screenSize.getHeight() / 1.7));
		}
		else if (screenSize.getWidth() > 1600){
			toimipisteEtusivuShl.setSize((int)(screenSize.getWidth() / 3.2), (int) (screenSize.getHeight() / 2.5));
		}
		else if (screenSize.getWidth() == 1600) {
			toimipisteEtusivuShl.setSize((int) (screenSize.getWidth() / 3.4), (int) (screenSize.getHeight() / 2.2));
		}
		
		toimipisteEtusivuShl.setText("Toimipisteiden hallinta");
		toimipisteEtusivuShl.setLayout(new GridLayout(2, false));
		
		Composite composite = new Composite(toimipisteEtusivuShl, SWT.V_SCROLL);
		composite.setLayout(new GridLayout(1, false));
		composite.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, true, 1, 1));
		
		
		// Toimipistetaulu
		toimipisteListaLbl = new Label(composite, SWT.NONE);
		toimipisteListaLbl.setText("Alla on lista luoduista toimipisteistä");
		
		toimipisteHallintaTbl = new Table(composite, SWT.BORDER | SWT.FULL_SELECTION);
		GridData gd_toimipisteHallintaTbl = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_toimipisteHallintaTbl.heightHint = 311;
		toimipisteHallintaTbl.setLayoutData(gd_toimipisteHallintaTbl);
		toimipisteHallintaTbl.setHeaderVisible(true);
		toimipisteHallintaTbl.setLinesVisible(true);
		
		
		// Taulun kentat
		toimipisteId = new TableColumn(toimipisteHallintaTbl, SWT.NONE);
		toimipisteId.setWidth(137);
		toimipisteId.setText("Toimipiste ID");
		
		nimi = new TableColumn(toimipisteHallintaTbl, SWT.NONE);
		nimi.setWidth(128);
		nimi.setText("Nimi");
		
		paikkakunta = new TableColumn(toimipisteHallintaTbl, SWT.NONE);
		paikkakunta.setWidth(117);
		paikkakunta.setText("Paikkakunta");
		
		composite_1 = new Composite(toimipisteEtusivuShl, SWT.NONE);
		composite_1.setLayout(new GridLayout(1, false));
		composite_1.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		new Label(composite_1, SWT.NONE);
		
		
		// Lisaa toimipiste nappi
		lisaaToimipisteBtn = new Button(composite_1, SWT.NONE);
		GridData gd_lisaaToimipisteBtn = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lisaaToimipisteBtn.widthHint = 120;
		lisaaToimipisteBtn.setLayoutData(gd_lisaaToimipisteBtn);
		lisaaToimipisteBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
		
				lisaaToimipisteGui = new LisaaToimipisteGUI();
				lisaaToimipisteGui.setLisaaToimipisteGui(lisaaToimipisteGui);
				lisaaToimipisteGui.setToimipisteEtusivuGui(getToimipisteEtusivuGui());
				toimipisteEtusivuShl.close();
				try {
					lisaaToimipisteGui.open();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		lisaaToimipisteBtn.setText("Lisää uusi toimipiste");
		
		
		// Muokkaa toimipisteta nappi
		muokkaaToimipisteBtn = new Button(composite_1, SWT.NONE);
		GridData gd_muokkaaToimipisteBtn = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_muokkaaToimipisteBtn.widthHint = 120;
		muokkaaToimipisteBtn.setLayoutData(gd_muokkaaToimipisteBtn);
		muokkaaToimipisteBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//TODO MUOKKAA MAJOITUS NAPPI KUUNTELIJA
				// Valinnat talteen ja valitetaan uudelle ikkunalle (luokkaoliolle)
				muokkaaToimipisteGui = new MuokkaaToimipisteGUI();
				muokkaaToimipisteGui.setMuokkaaToimipisteGui(muokkaaToimipisteGui);
				muokkaaToimipisteGui.setToimipisteEtusivuGui(toimipisteEtusivuGui);
				TableItem valittuToimipisteTbl[] = toimipisteHallintaTbl.getSelection();
				if (valittuToimipisteTbl.length <1) {
					System.out.println("Valitse yksi");
				}
				else {
	
					muokkaaToimipisteGui.setValittuToimipisteID(Integer.parseInt(valittuToimipisteTbl[0].getText(0)));
					
					try {
						toimipisteEtusivuShl.close();
						muokkaaToimipisteGui.open();
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
		muokkaaToimipisteBtn.setText("Muokkaa toimipisteta");
		
		
		// Nayta tiedot nappi
		naytaTiedotBtn = new Button(composite_1, SWT.NONE);
		naytaTiedotBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//TODO MUOKKAA MAJOITUS NAPPI KUUNTELIJA
				// Valinnat talteen ja valitetaan uudelle ikkunalle (luokkaoliolle)
				naytaToimipisteTiedotGui = new NaytaToimipisteTiedotGUI();
				naytaToimipisteTiedotGui.setNaytaToimipisteTiedotGui(naytaToimipisteTiedotGui);
				naytaToimipisteTiedotGui.setToimipisteEtusivuGui(toimipisteEtusivuGui);
				TableItem valittuToimipisteTbl[] = toimipisteHallintaTbl.getSelection();
				if (valittuToimipisteTbl.length <1) {
					System.out.println("Valitse yksi");
				}
				else {
	
					naytaToimipisteTiedotGui.setValittuToimipisteID(Integer.parseInt(valittuToimipisteTbl[0].getText(0)));
					
					try {
						toimipisteEtusivuShl.close();
						naytaToimipisteTiedotGui.open();
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
		
		
		// Poista toimipiste nappi
		poistaToimipisteBtn = new Button(composite_1, SWT.NONE);
		GridData gd_poistaToimipisteBtn = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_poistaToimipisteBtn.widthHint = 120;
		poistaToimipisteBtn.setLayoutData(gd_poistaToimipisteBtn);
		poistaToimipisteBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// Valinnat talteen ja valitetaan uudelle ikkunalle (luokkaoliolle)
				TableItem valittuToimipisteTbl[] = toimipisteHallintaTbl.getSelection();
				if (valittuToimipisteTbl.length <1) {
					System.out.println("Valitse yksi");
				}
				else {
					setValittuToimipisteID(Integer.parseInt(valittuToimipisteTbl[0].getText(0)));
					System.out.println(Integer.parseInt(valittuToimipisteTbl[0].getText(0)));
					toimipistePoistoVarmistusDialog = new ToimipistePoistoVarmistusDialog();
					toimipistePoistoVarmistusDialog.setToimipisteEtusivuGui(getToimipisteEtusivuGui());
					try {
						toimipistePoistoVarmistusDialog.open();
					} catch (Exception e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
				}
			}
		});
		poistaToimipisteBtn.setText("Poista toimipiste");
		
		takaisinBtn = new Button(toimipisteEtusivuShl, SWT.NONE);
		takaisinBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				toimipisteEtusivuShl.close();
			}

		});
		takaisinBtn.setText("Takaisin");
		new Label(toimipisteEtusivuShl, SWT.NONE);

	}

	public int getValittuToimipisteID() {
		return valittuToimipisteID;
	}

	public void setValittuToimipisteID(int valittuToimipisteID) {
		this.valittuToimipisteID = valittuToimipisteID;
	}

	public ToimipisteEtusivuGUI getToimipisteEtusivuGui() {
		return toimipisteEtusivuGui;
	}

	public void setToimipisteEtusivuGui(ToimipisteEtusivuGUI toimipisteEtusivuGui) {
		this.toimipisteEtusivuGui = toimipisteEtusivuGui;
	}

	public Table getToimipisteHallintaTbl() {
		return toimipisteHallintaTbl;
	}

	public void setToimipisteHallintaTbl(Table toimipisteHallintaTbl) {
		this.toimipisteHallintaTbl = toimipisteHallintaTbl;
	}
	

}
