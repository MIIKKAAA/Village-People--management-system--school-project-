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
import laskutus.MuokkaaLaskuGUI;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.DateTime;

public class PalveluvarausRaportointiGUI {

	protected Shell palveluvarausRaportointiShl;
	private Table palveluRaportointiTbl;
	private Composite composite_1;
	private TableColumn toimipisteId;
	private TableColumn toimipisteNimi;
	private Label toimipisteValintaLbl;
	private DateTime aloituspvmDateTime;
	private Label aloituspvmLbl;
	private Label lopetuspvmLbl;
	private DateTime lopetuspvmDateTime;
	private Button luoRaporttiBtn;
	private Connection conn;
	RaporttiPohja raportti;
	private int valittuToimipisteId;
	private String valittuAloituspvm;
	private String valittulopetuspvm;
	private TableColumn toimipisteOsoite;
	private TableColumn paikkakuntaColumn;
	private Button peruutaBtn;
	private Composite composite_2;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			PalveluvarausRaportointiGUI window = new PalveluvarausRaportointiGUI();
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
		Display display = ApuMetodeja.centerWindow(palveluvarausRaportointiShl);
		populateToimipisteTbl(palveluRaportointiTbl);
		palveluvarausRaportointiShl.open();
		palveluvarausRaportointiShl.layout();
		while (!palveluvarausRaportointiShl.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
	
	
	/*
	Avataan tietokantayhteys
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
	
	public String dateTimeToSqlDate(DateTime date) {
		
		int vuosi = date.getYear();
		int kuukausi = date.getMonth()+1;
		int paiva = date.getDay();
		
		String sqlDateTime = vuosi+"-"+kuukausi+"-"+paiva;
		
		return sqlDateTime;
	}

	/**
	 * Create contents of the window.
	 */
	public void populateToimipisteTbl(Table tbl) throws SQLException {
		
		// SQL-kysely ja tulosjoukon valmistelu
		String sql = "SELECT ToimipisteID, Nimi, Paikkakunta, Osoite FROM toimipisteet";
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
	            item = new TableItem(tbl, SWT.NONE);
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
	 * Create contents of the window.
	 */
	protected void createContents() {
		palveluvarausRaportointiShl = new Shell(SWT.CLOSE | SWT.MIN);
		palveluvarausRaportointiShl.setSize(623, 522);
		
		
		// "Responsiivisuus - maaritelty tassa vain 1920x1080 resoluutiolle, pidemmalle kehitetyssa ohjelmassa pitaisi muillekin resoluutioille"
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		
		if (screenSize.getWidth() < 1600){
			palveluvarausRaportointiShl.setSize((int)(screenSize.getWidth() / 2.55), (int) (screenSize.getHeight() / 1.7));
		}
		else if (screenSize.getWidth() > 1600){
			palveluvarausRaportointiShl.setSize((int)(screenSize.getWidth() / 3), (int) (screenSize.getHeight() / 2.3));
		}
		else if (screenSize.getWidth() == 1600) {
			palveluvarausRaportointiShl.setSize((int) (screenSize.getWidth() / 3.4), (int) (screenSize.getHeight() / 2.2));
		}
		
				palveluvarausRaportointiShl.setText("Palveluvarausten raportointi");
		palveluvarausRaportointiShl.setLayout(new GridLayout(2, false));
		
		Composite composite = new Composite(palveluvarausRaportointiShl, SWT.V_SCROLL);
		composite.setLayout(new GridLayout(1, false));
		composite.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, true, 1, 1));
		
		toimipisteValintaLbl = new Label(composite, SWT.NONE);
		toimipisteValintaLbl.setText("Valitse alta haluamasi raportissa käytettävät toimipisteet");
		
		
		
		// Toimipisteen valinta taulu
		palveluRaportointiTbl = new Table(composite, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
		GridData gd_palveluRaportointiTbl = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_palveluRaportointiTbl.heightHint = 323;
		palveluRaportointiTbl.setLayoutData(gd_palveluRaportointiTbl);
		palveluRaportointiTbl.setHeaderVisible(true);
		palveluRaportointiTbl.setLinesVisible(true);
		
		
		// Taulun kentat
		toimipisteId = new TableColumn(palveluRaportointiTbl, SWT.NONE);
		toimipisteId.setWidth(87);
		toimipisteId.setText("Toimipiste ID");
		
		toimipisteNimi = new TableColumn(palveluRaportointiTbl, SWT.NONE);
		toimipisteNimi.setWidth(118);
		toimipisteNimi.setText("Toimipisteen nimi");
		
		paikkakuntaColumn = new TableColumn(palveluRaportointiTbl, SWT.NONE);
		paikkakuntaColumn.setWidth(100);
		paikkakuntaColumn.setText("Paikkakunta");
		
		toimipisteOsoite = new TableColumn(palveluRaportointiTbl, SWT.NONE);
		toimipisteOsoite.setWidth(100);
		toimipisteOsoite.setText("Osoite");
		
		composite_2 = new Composite(composite, SWT.NONE);
		composite_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		composite_2.setLayout(new GridLayout(2, true));
		
		peruutaBtn = new Button(composite_2, SWT.NONE);
		peruutaBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				palveluvarausRaportointiShl.close();
			}
		});
		peruutaBtn.setText("Peruuta");
		
		
		// Luo raportti nappi
		luoRaporttiBtn = new Button(composite_2, SWT.NONE);
		luoRaporttiBtn.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		luoRaporttiBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// Valinnat talteen ja valitetaan uudelle ikkunalle (luokkaoliolle)
				raportti = new RaporttiPohja();
				TableItem valittuToimipisteTbl[] = palveluRaportointiTbl.getSelection();
				if (valittuToimipisteTbl.length <1) {
					System.out.println("Valitse yksi");
				}
				else {
					raportti.setValittuToimipisteID(Integer.parseInt(valittuToimipisteTbl[0].getText(0)));
					int a_kuukausi = aloituspvmDateTime.getMonth()+1;
					int a_paiva = aloituspvmDateTime.getDay();
					int a_vuosi = aloituspvmDateTime.getYear();
					String a_pvm = a_vuosi+"-"+a_kuukausi+"-"+a_paiva;
					
					int l_kuukausi = lopetuspvmDateTime.getMonth()+1;
					int l_paiva = lopetuspvmDateTime.getDay();
					int l_vuosi = lopetuspvmDateTime.getYear();
					String l_pvm = l_vuosi+"-"+l_kuukausi+"-"+l_paiva;
					raportti.setValittuAloituspvm(a_pvm);
					raportti.setValittuLopetuspvm(l_pvm);
					try {
						raportti.open();
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
		luoRaporttiBtn.setText("Luo raportti varauksista");
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		
		composite_1 = new Composite(palveluvarausRaportointiShl, SWT.NONE);
		composite_1.setLayout(new GridLayout(1, false));
		composite_1.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		
		
		// Aloituspvm label ja aikavalinta
		aloituspvmLbl = new Label(composite_1, SWT.NONE);
		aloituspvmLbl.setText("Valitse aloituspäivämäärä");
		
		aloituspvmDateTime = new DateTime(composite_1, SWT.BORDER);
		aloituspvmDateTime.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//TODO LISAA ALOITUSPVM NAPPI KUUNTELIJA
			}
		});
		
		
		// Lopetuspvm label ja aikavalinta
		lopetuspvmLbl = new Label(composite_1, SWT.NONE);
		lopetuspvmLbl.setText("Valitse lopetuspäivämäärä");
		
		lopetuspvmDateTime = new DateTime(composite_1, SWT.BORDER);
		lopetuspvmDateTime.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO LISAA LOPETUSPVM NAPPI KUUNTELIJA
			}
		});

	}

	public int getValittuToimipisteId() {
		return valittuToimipisteId;
	}

	public void setValittuToimipisteId(int valittuToimipisteId) {
		this.valittuToimipisteId = valittuToimipisteId;
	}

	public String getValittuAloituspvm() {
		return valittuAloituspvm;
	}

	public void setValittuAloituspvm(String valittuAloituspvm) {
		this.valittuAloituspvm = valittuAloituspvm;
	}

	public String getValittulopetuspvm() {
		return valittulopetuspvm;
	}

	public void setValittulopetuspvm(String valittulopetuspvm) {
		this.valittulopetuspvm = valittulopetuspvm;
	}

}
