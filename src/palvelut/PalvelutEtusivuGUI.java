package palvelut;

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
import asiakkaat.AsiakasPoistoVarmistusDialog;
import asiakkaat.AsiakkaatEtusivuGUI;
import asiakkaat.NaytaAsiakasTiedotGUI;
import palvelu_varaus.PalveluvarausRaportointiGUI;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;

public class PalvelutEtusivuGUI {

	protected Shell palvelutEtusivuShl;
	private Table etusivuPalveluTbl;
	private Composite composite_1;
	private Button lisaaPalveluBtn;
	private Button muokkaaPalveluBtn;
	private Button poistaPalveluBtn;
	private TableColumn palveluID;
	private TableColumn palveluNimi;
	private TableColumn palveluToimipiste;
	private LisaaPalveluGUI lisaaPalveluGui;
	private MuokkaaPalveluGUI muokkaaPalveluGui;
	private NaytaPalveluTiedotGUI naytaPalveluTiedotGui;
	private PalveluvarausRaportointiGUI palveluRaportointiGui;
	private Label palveluListaLbl;
	private Button naytaTiedotBtn;
	private Connection conn;
	private ResultSet palvelut;
	private TableItem palvelu;
	private int PalveluID;
	private Button takaisinBtn;
	private PalvelutEtusivuGUI palvelutEtusivuGui;
	private int valittuPalveluId;
	private PalveluPoistoVarmistusDialog palveluPoistoVarmistusDialog;



	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			PalvelutEtusivuGUI window = new PalvelutEtusivuGUI();
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
		haeTiedot();
		// Avautuessa keskelle
		Display display = ApuMetodeja.centerWindow(palvelutEtusivuShl);
		
		takaisinBtn = new Button(palvelutEtusivuShl, SWT.NONE);
		takaisinBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				palvelutEtusivuShl.close();
			}
		});
		takaisinBtn.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		takaisinBtn.setText("Takaisin");
		new Label(palvelutEtusivuShl, SWT.NONE);

		palvelutEtusivuShl.open();
		palvelutEtusivuShl.layout();
		while (!palvelutEtusivuShl.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		palvelutEtusivuShl = new Shell(SWT.CLOSE | SWT.MIN);
		palvelutEtusivuShl.setSize(532, 438);
		
		
		// "Responsiivisuus - maaritelty tassa vain 1920x1080 resoluutiolle, pidemmalle kehitetyssa ohjelmassa pitaisi muillekin resoluutioille"
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		
		if (screenSize.getWidth() < 1600){
			palvelutEtusivuShl.setSize((int)(screenSize.getWidth() / 2.2), (int) (screenSize.getHeight() / 1.7));
		}
		else if (screenSize.getWidth() > 1600){
			palvelutEtusivuShl.setSize((int)(screenSize.getWidth() / 3.3), (int) (screenSize.getHeight() / 2.5));
		}
		else if (screenSize.getWidth() == 1600) {
			palvelutEtusivuShl.setSize((int) (screenSize.getWidth() / 3.4), (int) (screenSize.getHeight() / 2.2));
		}
		
				palvelutEtusivuShl.setText("Palveluiden hallinta");
		palvelutEtusivuShl.setLayout(new GridLayout(2, false));
		
		Composite composite = new Composite(palvelutEtusivuShl, SWT.V_SCROLL);
		composite.setLayout(new GridLayout(1, false));
		composite.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, true, 1, 1));
		
		palveluListaLbl = new Label(composite, SWT.NONE);
		palveluListaLbl.setText("Alla on lista luoduista palveluista");
		
		
		// Palvelun valinta taulu
		etusivuPalveluTbl = new Table(composite, SWT.BORDER | SWT.FULL_SELECTION);
		GridData gd_etusivuPalveluTbl = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_etusivuPalveluTbl.heightHint = 310;
		etusivuPalveluTbl.setLayoutData(gd_etusivuPalveluTbl);
		etusivuPalveluTbl.setHeaderVisible(true);
		etusivuPalveluTbl.setLinesVisible(true);
		
		
		// Taulun kentat
		palveluID = new TableColumn(etusivuPalveluTbl, SWT.NONE);
		palveluID.setWidth(137);
		palveluID.setText("Palvelu ID");
		
		palveluNimi = new TableColumn(etusivuPalveluTbl, SWT.NONE);
		palveluNimi.setWidth(128);
		palveluNimi.setText("Palvelun nimi");
		
		palveluToimipiste = new TableColumn(etusivuPalveluTbl, SWT.NONE);
		palveluToimipiste.setWidth(117);
		palveluToimipiste.setText("Toimipiste");
		
		composite_1 = new Composite(palvelutEtusivuShl, SWT.NONE);
		composite_1.setLayout(new GridLayout(1, false));
		composite_1.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		new Label(composite_1, SWT.NONE);
		
		
		// Lisaa palvelu nappi
		lisaaPalveluBtn = new Button(composite_1, SWT.NONE);
		GridData gd_lisaaPalveluBtn = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lisaaPalveluBtn.widthHint = 110;
		lisaaPalveluBtn.setLayoutData(gd_lisaaPalveluBtn);
		lisaaPalveluBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//TODO LISAA PALVELU NAPPI KUUNTELIJA
				lisaaPalveluGui = new LisaaPalveluGUI();
				lisaaPalveluGui.setPalvelutEtusivuGui(palvelutEtusivuGui);
				try {
					palvelutEtusivuShl.close();
					lisaaPalveluGui.open();
	
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		lisaaPalveluBtn.setText("Lisää uusi palvelu");
		
		
		// Muokkaa nappi
		muokkaaPalveluBtn = new Button(composite_1, SWT.NONE);
		GridData gd_muokkaaPalveluBtn = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_muokkaaPalveluBtn.widthHint = 110;
		muokkaaPalveluBtn.setLayoutData(gd_muokkaaPalveluBtn);
		muokkaaPalveluBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TableItem valittuPalveluTbl[] = etusivuPalveluTbl.getSelection();
				muokkaaPalveluGui = new MuokkaaPalveluGUI();
				muokkaaPalveluGui.setPalvelutEtusivuGui(palvelutEtusivuGui);
				if (valittuPalveluTbl.length < 1) {
					System.out.println("Valitse palvelu");
				}
				else {
					String palvelu = valittuPalveluTbl[0].getText();
					palvelutEtusivuShl.close();
					muokkaaPalveluGui.open(palvelu);
				}
			}
		});
		muokkaaPalveluBtn.setText("Muokkaa palvelua");
		
		naytaTiedotBtn = new Button(composite_1, SWT.NONE);
		naytaTiedotBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TableItem valittuPalveluTbl[] = etusivuPalveluTbl.getSelection();
				naytaPalveluTiedotGui = new NaytaPalveluTiedotGUI();
				naytaPalveluTiedotGui.setPalvelutEtusivuGui(palvelutEtusivuGui);
				if (valittuPalveluTbl.length < 1) {
					System.out.println("Valitse palvelu");
				}
				else {
					String valinta = valittuPalveluTbl[0].getText();
					palvelutEtusivuShl.close();
					naytaPalveluTiedotGui.open(valinta);
				}
			}
		});
		GridData gd_naytaTiedotBtn = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_naytaTiedotBtn.widthHint = 110;
		naytaTiedotBtn.setLayoutData(gd_naytaTiedotBtn);
		naytaTiedotBtn.setText("Näytä tiedot");
		new Label(composite_1, SWT.NONE);
		
		
		// Poista palvelu nappi
		poistaPalveluBtn = new Button(composite_1, SWT.NONE);
		GridData gd_poistaPalveluBtn = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_poistaPalveluBtn.widthHint = 110;
		poistaPalveluBtn.setLayoutData(gd_poistaPalveluBtn);
		poistaPalveluBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TableItem valittuPalveluTbl[] = etusivuPalveluTbl.getSelection();
				if (valittuPalveluTbl.length < 1) {
					System.out.println("Valitse palvelu");
				}
				else {
					setValittuPalveluId(Integer.parseInt(valittuPalveluTbl[0].getText()));
					palveluPoistoVarmistusDialog = new PalveluPoistoVarmistusDialog();
					palveluPoistoVarmistusDialog.setPalvelutEtusivuGui(getPalvelutEtusivuGui());
					try {
						palveluPoistoVarmistusDialog.open();
					} catch (Exception e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
//					poistaPalvelu(Integer.parseInt(valittuPalveluTbl[0].getText()));
//					etusivuPalveluTbl.removeAll();
//					haeTiedot();
				}
			}
		});
		poistaPalveluBtn.setText("Poista palvelu");

	}
	/*
	 * Haetaan luettelo palveluista tietokannasta
	 */
	public void haeTiedot() {
		palvelut = null;
		String sql = "SELECT PalveluID, Palvelun_nimi, palvelu.ToimipisteID, Paikkakunta FROM palvelu, toimipisteet WHERE palvelu.ToimipisteID = toimipisteet.ToimipisteID ORDER BY PalveluID";
		// avataan tietokanta
		try {
			yhdista();
			palvelut = conn.prepareStatement(sql).executeQuery();
			while (palvelut.next() == true) {
				palvelu = new TableItem(etusivuPalveluTbl, SWT.NONE);
				palvelu.setText(new String[] {Integer.toString(palvelut.getInt(1)), palvelut.getString(2), Integer.toString(palvelut.getInt(3)) + " (" + palvelut.getString(4) + ")"});
			}
			System.out.println("Haettiin palvelut tietokannasta");
		 } catch (SQLException se) {
            // SQL virheet
			se.printStackTrace();
			//JOptionPane.showMessageDialog(null, "Tapahtui virhe tietokantaa avattaessa.", "Tietokantavirhe", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            // JDBC virheet
        	e.printStackTrace();
            //JOptionPane.showMessageDialog(null, "Tapahtui JDBCvirhe tietokantaa avattaessa.", "Tietokantavirhe", JOptionPane.ERROR_MESSAGE);
		}
	}
	/*
	 * Poistetaan asiakas tietokannasta
	 */
	public void poistaPalvelu(int id) {
		// Lisatty, jos palveluvarauksessa esiintyy poistettava palveluid niin asetetaan sen nulliksi (vierasavaimen takia)
		// tahan pitaisi lisata koko palveluvarauksen poisto, mutta se jaakoon nyt tekematta -Miikka
		String sql = "UPDATE palvelun_varaus SET PalveluID = NULL WHERE PalveluID = ?";
		PreparedStatement lause = null;
		try {
			yhdista();
			// luo PreparedStatement-olio sql-lauseelle
			lause = conn.prepareStatement(sql);
			// laitetaan arvot DELETEn WHERE-ehtoon
			lause.setInt( 1, id);
			// suorita sql-lause
			int lkm = lause.executeUpdate();	
			if (lkm == 0) {
				throw new Exception("Palvelun poistaminen ei onnistu");
			}
		} catch (SQLException se) {
			// SQL virheet
			System.out.println("SQL-virhe");
		} catch (Exception e) {
			// JDBC virheet
        	System.out.println("JDBC-virhe");
		}
		
		sql = "DELETE FROM palvelun_varaus WHERE PalveluID = ?";
		PreparedStatement lause3 = null;
		try {
			// luo PreparedStatement-olio sql-lauseelle
			lause3 = conn.prepareStatement(sql);
			// laitetaan arvot DELETEn WHERE-ehtoon
			lause3.setInt( 1, id);
			// suorita sql-lause
			int lkm = lause3.executeUpdate();	
			if (lkm == 0) {
				throw new Exception("Palvelun poistaminen ei onnistu");
			}
		} catch (SQLException se) {
			// SQL virheet
			se.printStackTrace();
			System.out.println("SQL-virhe");
		} catch (Exception e) {
			// JDBC virheet
        	System.out.println("JDBC-virhe");
		}
		
		sql = "DELETE FROM palvelu WHERE PalveluID = ?";
		PreparedStatement lause2 = null;
		try {
			// luo PreparedStatement-olio sql-lauseelle
			lause2 = conn.prepareStatement(sql);
			// laitetaan arvot DELETEn WHERE-ehtoon
			lause2.setInt( 1, id);
			// suorita sql-lause
			int lkm = lause2.executeUpdate();	
			if (lkm == 0) {
				throw new Exception("Palvelun poistaminen ei onnistu");
			}
		} catch (SQLException se) {
			// SQL virheet
			se.printStackTrace();
			System.out.println("SQL-virhe");
		} catch (Exception e) {
			// JDBC virheet
        	System.out.println("JDBC-virhe");
		}
	}
	/*
	Avataan tietokantayhteys
	*/
	public void yhdista() throws SQLException, Exception {
		conn = null;
		String url = "jdbc:mariadb://localhost:3306/vp"; // palvelin = localhost, :portti annettu asennettaessa, tietokannan nimi
		try {
			// ota yhteys kantaan, kayttaja = root, salasana = mariadb
			conn=DriverManager.getConnection(url,"root","root");
		}
		catch (SQLException e) { // tietokantaan ei saada yhteytt�
			conn = null;
			throw e;
		}
		catch (Exception e ) { // JDBC ajuria ei l�ydy
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

	public Table getEtusivuPalveluTbl() {
		return etusivuPalveluTbl;
	}

	public void setEtusivuPalveluTbl(Table etusivuPalveluTbl) {
		this.etusivuPalveluTbl = etusivuPalveluTbl;
	}

	public PalvelutEtusivuGUI getPalvelutEtusivuGui() {
		return palvelutEtusivuGui;
	}

	public void setPalvelutEtusivuGui(PalvelutEtusivuGUI palvelutEtusivuGui) {
		this.palvelutEtusivuGui = palvelutEtusivuGui;
	}

	public int getValittuPalveluId() {
		return valittuPalveluId;
	}

	public void setValittuPalveluId(int valittuPalveluId) {
		this.valittuPalveluId = valittuPalveluId;
	}

}
