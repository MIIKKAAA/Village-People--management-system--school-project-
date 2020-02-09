package majoitus;

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

public class MajoitusEtusivuGUI {

	protected Shell majoitusEtusivuShl;
	private Table majoitusHallintaTbl;
	private Composite composite_1;
	private Button lisaaMajoitusBtn;
	private Button muokkaaMajoitusBtn;
	private Button poistaMajoitusBtn;
	private TableColumn majoitusId;
	private TableColumn nimi;
	private TableColumn toimipisteId;
	private LisaaMajoitusGUI lisaaMajoitusGui;
	private MuokkaaMajoitusGUI muokkaaMajoitusGui;
	private NaytaMajoitusTiedotGUI naytaMajoitusTiedotGui;
	private Label majoitusListaLbl;
	private Button naytaTiedotBtn;
	private Connection conn;
	private int valittuMajoitusID;
	private MajoitusPoistoVarmistusDialog majoitusPoistoVarmistusDialog;
	private MajoitusEtusivuGUI majoitusEtusivuGui;
	private Button takaisinBtn;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			MajoitusEtusivuGUI window = new MajoitusEtusivuGUI();
			window.setMajoitusEtusivuGui(window);
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 * @throws Exception 
	 */
	public void open() throws Exception {
		yhdista();
		createContents();
		// Avautuessa keskelle
		Display display = ApuMetodeja.centerWindow(majoitusEtusivuShl);
		populateEtusivuTbl(majoitusHallintaTbl);
		
		takaisinBtn = new Button(majoitusEtusivuShl, SWT.NONE);
		takaisinBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				majoitusEtusivuShl.close();
			}
		});
		takaisinBtn.setText("Takaisin");
		new Label(majoitusEtusivuShl, SWT.NONE);
		majoitusEtusivuShl.open();
		majoitusEtusivuShl.layout();
		while (!majoitusEtusivuShl.isDisposed()) {
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
		String sql = "SELECT MajoitusID, Nimi, ToimipisteID FROM majoitus";
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
	protected void createContents() {
		majoitusEtusivuShl = new Shell(SWT.CLOSE | SWT.MIN);
		majoitusEtusivuShl.setSize(600, 561);
		
		
		// "Responsiivisuus - maaritelty tassa vain 1920x1080 resoluutiolle, pidemmalle kehitetyssa ohjelmassa pitaisi muillekin resoluutioille"
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		if (screenSize.getWidth() < 1600){
			majoitusEtusivuShl.setSize((int)(screenSize.getWidth() / 2.15), (int) (screenSize.getHeight() / 1.7));
		}
		else if (screenSize.getWidth() > 1600){
			majoitusEtusivuShl.setSize((int)(screenSize.getWidth() / 3.2), (int) (screenSize.getHeight() / 2.4));
		}
		else if (screenSize.getWidth() == 1600) {
			majoitusEtusivuShl.setSize((int) (screenSize.getWidth() / 3.4), (int) (screenSize.getHeight() / 2.2));
		}
		
		majoitusEtusivuShl.setText("Majoitusten hallinta");
		majoitusEtusivuShl.setLayout(new GridLayout(2, false));
		
		Composite composite = new Composite(majoitusEtusivuShl, SWT.V_SCROLL);
		composite.setLayout(new GridLayout(1, false));
		composite.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		
		
		// Majoitustaulu
		majoitusListaLbl = new Label(composite, SWT.NONE);
		majoitusListaLbl.setText("Alla on lista luoduista majoituksista");
		
		majoitusHallintaTbl = new Table(composite, SWT.BORDER | SWT.FULL_SELECTION);
		GridData gd_majoitusHallintaTbl = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_majoitusHallintaTbl.heightHint = 311;
		majoitusHallintaTbl.setLayoutData(gd_majoitusHallintaTbl);
		majoitusHallintaTbl.setHeaderVisible(true);
		majoitusHallintaTbl.setLinesVisible(true);
		
		
		// Taulun kentat
		majoitusId = new TableColumn(majoitusHallintaTbl, SWT.NONE);
		majoitusId.setWidth(137);
		majoitusId.setText("Majoitus ID");
		
		nimi = new TableColumn(majoitusHallintaTbl, SWT.NONE);
		nimi.setWidth(128);
		nimi.setText("Nimi");
		
		toimipisteId = new TableColumn(majoitusHallintaTbl, SWT.NONE);
		toimipisteId.setWidth(117);
		toimipisteId.setText("Toimipiste ID");
		
		composite_1 = new Composite(majoitusEtusivuShl, SWT.NONE);
		composite_1.setLayout(new GridLayout(1, false));
		composite_1.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		new Label(composite_1, SWT.NONE);
		
		
		// Lisaa majoitus nappi
		lisaaMajoitusBtn = new Button(composite_1, SWT.NONE);
		GridData gd_lisaaMajoitusBtn = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lisaaMajoitusBtn.widthHint = 120;
		lisaaMajoitusBtn.setLayoutData(gd_lisaaMajoitusBtn);
		lisaaMajoitusBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
		
				lisaaMajoitusGui = new LisaaMajoitusGUI();
				lisaaMajoitusGui.setMajoitusEtusivuGui(majoitusEtusivuGui);
				lisaaMajoitusGui.setLisaaMajoitusGui(lisaaMajoitusGui);
				majoitusEtusivuShl.close();
				try {
					lisaaMajoitusGui.open();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		lisaaMajoitusBtn.setText("Lisää uusi majoitus");
		
		
		// Muokkaa majoitusta nappi
		muokkaaMajoitusBtn = new Button(composite_1, SWT.NONE);
		GridData gd_muokkaaMajoitusBtn = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_muokkaaMajoitusBtn.widthHint = 120;
		muokkaaMajoitusBtn.setLayoutData(gd_muokkaaMajoitusBtn);
		muokkaaMajoitusBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//TODO MUOKKAA MAJOITUS NAPPI KUUNTELIJA
				// Valinnat talteen ja valitetaan uudelle ikkunalle (luokkaoliolle)
				muokkaaMajoitusGui = new MuokkaaMajoitusGUI();
				muokkaaMajoitusGui.setMuokkaaMajoitusGui(muokkaaMajoitusGui);
				muokkaaMajoitusGui.setMajoitusEtusivuGui(majoitusEtusivuGui);
				TableItem valittuMajoitusTbl[] = majoitusHallintaTbl.getSelection();
				if (valittuMajoitusTbl.length <1) {
					System.out.println("Valitse yksi");
				}
				else {
	
					muokkaaMajoitusGui.setValittuMajoitusID(Integer.parseInt(valittuMajoitusTbl[0].getText(0)));
					
					try {
						majoitusEtusivuShl.close();
						muokkaaMajoitusGui.open();
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
		muokkaaMajoitusBtn.setText("Muokkaa majoitusta");
		
		
		// Nayta tiedot nappi
		naytaTiedotBtn = new Button(composite_1, SWT.NONE);
		naytaTiedotBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//TODO MUOKKAA MAJOITUS NAPPI KUUNTELIJA
				// Valinnat talteen ja valitetaan uudelle ikkunalle (luokkaoliolle)
				naytaMajoitusTiedotGui = new NaytaMajoitusTiedotGUI();
				naytaMajoitusTiedotGui.setMajoitusEtusivuGui(majoitusEtusivuGui);
				TableItem valittuMajoitusTbl[] = majoitusHallintaTbl.getSelection();
				if (valittuMajoitusTbl.length <1) {
					System.out.println("Valitse yksi");
				}
				else {
	
					naytaMajoitusTiedotGui.setValittuMajoitusID(Integer.parseInt(valittuMajoitusTbl[0].getText(0)));
					
					try {
						majoitusEtusivuShl.close();
						naytaMajoitusTiedotGui.open();
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
		poistaMajoitusBtn = new Button(composite_1, SWT.NONE);
		GridData gd_poistaMajoitusBtn = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_poistaMajoitusBtn.widthHint = 120;
		poistaMajoitusBtn.setLayoutData(gd_poistaMajoitusBtn);
		poistaMajoitusBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// Valinnat talteen ja valitetaan uudelle ikkunalle (luokkaoliolle)
				TableItem valittuMajoitusTbl[] = majoitusHallintaTbl.getSelection();
				if (valittuMajoitusTbl.length <1) {
					System.out.println("Valitse yksi");
				}
				else {
					setValittuMajoitusID(Integer.parseInt(valittuMajoitusTbl[0].getText(0)));
					majoitusPoistoVarmistusDialog = new MajoitusPoistoVarmistusDialog();
					majoitusPoistoVarmistusDialog.setMajoitusEtusivuGui(getMajoitusEtusivuGui());
					try {
						majoitusPoistoVarmistusDialog.open();
					} catch (Exception e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
				}
			}
		});
		poistaMajoitusBtn.setText("Poista majoitus");

	}

	public int getValittuMajoitusID() {
		return valittuMajoitusID;
	}

	public void setValittuMajoitusID(int valittuMajoitusID) {
		this.valittuMajoitusID = valittuMajoitusID;
	}

	public MajoitusEtusivuGUI getMajoitusEtusivuGui() {
		return majoitusEtusivuGui;
	}

	public void setMajoitusEtusivuGui(MajoitusEtusivuGUI majoitusEtusivuGui) {
		this.majoitusEtusivuGui = majoitusEtusivuGui;
	}

	public Table getMajoitusHallintaTbl() {
		return majoitusHallintaTbl;
	}

	public void setMajoitusHallintaTbl(Table majoitusHallintaTbl) {
		this.majoitusHallintaTbl = majoitusHallintaTbl;
	}
	

}
