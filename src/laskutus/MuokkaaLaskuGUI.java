package laskutus;

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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import ApuMetodeja.ApuMetodeja;

import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.events.VerifyEvent;

public class MuokkaaLaskuGUI {

	protected Shell muokkaaLaskuShl;
	private Composite composite_1;
	private Table laskutettavaMajoitusvarausTbl;
	private Composite composite_2;
	private Label laskutettavaPalveluLbl;
	private Table laskutettavatPalveluvarauksetTbl;
	private TableColumn palveluvarausId;
	private TableColumn palveluId;
	private Label laskuID;
	private Text laskuIdTxt;
	private Composite composite_4;
	private Label tilinumeroLbl;
	private Text tilinumeroTxt;
	private Label saajanNimiLbl;
	private Text saajanNimiTxt;
	private Label viitenumeroLbl;
	private Text viitenumeroTxt;
	private Label maksuMaaraLbl;
	private Text maksuMaaraTxt;
	private Label laskunTyyppiLbl;
	private Combo laskuTyyppiCombo;
	private Label laskuLuontiPvmLbl;
	private Label laskunErapvmLbl;
	private DateTime laskunLuontiPvmDateTime;
	private DateTime laskunEraPvmDateTime;
	private Button muokkaaLaskuBtn;
	private Button laskuPeruutaBtn;
	private Label asiakasIdLbl;
	private Text asiakasIdTxt;
	private int valittuMajoitusvarausID;
	private int valittuPalveluVarausID;
	private int valittuAsiakasID;
	private int valittuLaskuID;
	private Connection conn;
	private int hintaPalveluId;
	private int hintaMajoitusId;
	Lasku lasku = new Lasku();
	private ValittuVarausVaroitusDialog valittuVarausVaroitusDialog;
	private LaskutusEtusivuGUI laskutusEtusivuGui;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			MuokkaaLaskuGUI window = new MuokkaaLaskuGUI();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 * @throws SQLException 
	 */
	public void open() throws SQLException {
		try {
			yhdista();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		createContents();
		// Avautuessa keskelle
		Display display = ApuMetodeja.centerWindow(muokkaaLaskuShl);
		populateMajoitusvarausTbl(laskutettavaMajoitusvarausTbl);
		populatePalveluvarausTbl(laskutettavatPalveluvarauksetTbl);
		luoLaskuTiedot();
		lisaaTiedotLaskuOlioon();
		
		muokkaaLaskuShl.open();
		muokkaaLaskuShl.layout();
		while (!muokkaaLaskuShl.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
	
	/*Avataan tietokantayhteys
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
	
	public void lisaaTiedotLaskuOlioon() {
		lasku.setLasku_id(Integer.parseInt(laskuIdTxt.getText()));
		lasku.setAsiakas_id(Integer.parseInt(asiakasIdTxt.getText()));
		lasku.setErapvm(dateTimeToSqlDate(laskunEraPvmDateTime));
		lasku.setLaskunluontipvm(dateTimeToSqlDate(laskunLuontiPvmDateTime));
		
		lasku.setLaskun_tyyppi(laskuTyyppiCombo.getText());
		lasku.setTilinumero(Integer.parseInt(tilinumeroTxt.getText()));
		lasku.setSaajan_nimi(saajanNimiTxt.getText());
		lasku.setViitenumero(Integer.parseInt(viitenumeroTxt.getText()));
		lasku.setMaksun_maara(Integer.parseInt(maksuMaaraTxt.getText())); //TODO
	}
	public void populateMajoitusvarausTbl(Table majoitusvarausTbl) throws SQLException {
		String sql = "SELECT MajoitusvarausID, MajoitusID FROM majoitusvaraus WHERE AsiakasID = ?";
		ResultSet tulosjoukko = null;
		PreparedStatement lause = null;
		try {
			// luo PreparedStatement-olio sql-lauseelle
			lause = conn.prepareStatement(sql);
			lause.setInt(1, getValittuAsiakasID()); // asetetaan where ehtoon (?) arvo
			// suorita sql-lause
			tulosjoukko = lause.executeQuery();	
			if (tulosjoukko == null) { // tulosjoukkoon odotetaan ainoastaan max. yhtä riviä
				System.out.println("...");
			}
		} catch (SQLException se) {
            // SQL virheet
            se.printStackTrace();
        } catch (Exception e) {
            // JDBC virheet
            e.printStackTrace();
		}

        java.sql.ResultSetMetaData rsmd = tulosjoukko.getMetaData();
        int columnsNumber = rsmd.getColumnCount();
        
		try {
			TableItem item;
			
			while (tulosjoukko.next ()){
	            item = new TableItem(majoitusvarausTbl, SWT.NONE);
	            for (int i = 1; i <= columnsNumber; i++) {
	                item.setText(i - 1, tulosjoukko.getString(i));
			}
		}
		}catch (SQLException e) {
			e.printStackTrace();
		}catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	
	public void populatePalveluvarausTbl(Table palveluvarausTbl) throws SQLException {
			
		String sql = "SELECT PalveluvarausID, PalveluID FROM palvelun_varaus WHERE AsiakasID = ?";
		ResultSet tulosjoukko = null;
		PreparedStatement lause = null;
		try {
			// luo PreparedStatement-olio sql-lauseelle
			lause = conn.prepareStatement(sql);
			
			lause.setInt(1, getValittuAsiakasID()); // asetetaan where ehtoon (?) arvo
			// suorita sql-lause
			tulosjoukko = lause.executeQuery();	
			if (tulosjoukko == null) {
				System.out.println("");
			}
		} catch (SQLException se) {
            // SQL virheet
            se.printStackTrace();
        } catch (Exception e) {
            // JDBC virheet
            e.printStackTrace();
		}
		

        java.sql.ResultSetMetaData rsmd = tulosjoukko.getMetaData();
        int columnsNumber = rsmd.getColumnCount();
        
		try {
			TableItem item;
			while (tulosjoukko.next ()){
	            item = new TableItem(palveluvarausTbl, SWT.NONE);
	            for (int i = 1; i <= columnsNumber; i++) {
	                item.setText(i - 1, tulosjoukko.getString(i));
			}
		}
		}catch (SQLException e) {
			e.printStackTrace();
		}catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	

	private void luoLaskuTiedot() throws SQLException {
		String sql = "SELECT LaskuID, PalveluvarausID, AsiakasID, MajoitusvarausID, Laskun_tyyppi, Tilinumero, Saajan_nimi, Viitenumero, Maksun_määrä, Laskunluontipvm, Eräpvm "+
					"FROM laskujen_hallinta_ja_seuranta WHERE AsiakasID = ?";
		ResultSet tulosjoukko = null;
		PreparedStatement lause = null;
		try {
			// luo PreparedStatement-olio sql-lauseelle
			lause = conn.prepareStatement(sql);
			
			lause.setInt(1, getValittuAsiakasID()); // asetetaan where ehtoon (?) arvo
			// suorita sql-lause
			tulosjoukko = lause.executeQuery();	
			if (tulosjoukko == null) {
				System.out.println("");
			}
		} catch (SQLException se) {
            // SQL virheet
            se.printStackTrace();
        } catch (Exception e) {
            // JDBC virheet
            e.printStackTrace();
		}
        
		if (tulosjoukko.next()) {
			laskuIdTxt.setText(Integer.toString(tulosjoukko.getInt("LaskuID")));
			asiakasIdTxt.setText(Integer.toString(tulosjoukko.getInt("AsiakasID")));
			tilinumeroTxt.setText(Integer.toString(tulosjoukko.getInt("Tilinumero")));
			saajanNimiTxt.setText(tulosjoukko.getString("Saajan_nimi"));
			viitenumeroTxt.setText(Integer.toString(tulosjoukko.getInt("Viitenumero")));
			laskunLuontiPvmDateTime.setDay(Integer.parseInt(getDayFromStringDate(tulosjoukko, "Laskunluontipvm")));
			laskunLuontiPvmDateTime.setMonth(Integer.parseInt(getMonthFromStringDate(tulosjoukko, "Laskunluontipvm")));
			laskunLuontiPvmDateTime.setYear(Integer.parseInt(getYearFromStringDate(tulosjoukko, "Laskunluontipvm")));
			laskunEraPvmDateTime.setDay(Integer.parseInt(getDayFromStringDate(tulosjoukko, "Eräpvm")));
			laskunEraPvmDateTime.setMonth(Integer.parseInt(getMonthFromStringDate(tulosjoukko, "Eräpvm")));
			laskunEraPvmDateTime.setYear(Integer.parseInt(getYearFromStringDate(tulosjoukko, "Eräpvm")));
			laskuTyyppiCombo.setText(tulosjoukko.getString("Laskun_tyyppi"));
			maksuMaaraTxt.setText(tulosjoukko.getString("Maksun_määrä"));
		}
	}
	
public void muokkaaLasku() throws SQLException, Exception {
		
		boolean lasku_lisatty = true;
		lasku = null;
		
		try {
			lasku = Lasku.haeLasku(Integer.parseInt(laskuIdTxt.getText()), conn);
		}catch (SQLException e) {
			lasku_lisatty = false;
			e.printStackTrace();
		} catch (Exception e) {
			lasku_lisatty = false;
			e.printStackTrace();
		}

		lasku.setLasku_id(Integer.parseInt(laskuIdTxt.getText()));
		lasku.setAsiakas_id(Integer.parseInt(asiakasIdTxt.getText()));
		lasku.setErapvm(dateTimeToSqlDate(laskunEraPvmDateTime));
		lasku.setLaskunluontipvm(dateTimeToSqlDate(laskunLuontiPvmDateTime));
		
		TableItem valitutPalveluvarauksetTbl[] = laskutettavatPalveluvarauksetTbl.getSelection();
		if (valitutPalveluvarauksetTbl.length < 1) {
			lasku.setPalveluvaraus_id(0);
		}
		else {
			lasku.setPalveluvaraus_id(Integer.parseInt(valitutPalveluvarauksetTbl[0].getText(0)));	
		}
		
		TableItem valittuMajoitusvarausTbl[] = laskutettavaMajoitusvarausTbl.getSelection();
		if (valittuMajoitusvarausTbl.length <1) {
			lasku.setMajoitusvaraus_id(0);
		}
		else {
			lasku.setMajoitusvaraus_id(Integer.parseInt(valittuMajoitusvarausTbl[0].getText(0)));
		}
	
		lasku.setLaskun_tyyppi(laskuTyyppiCombo.getText());
		lasku.setTilinumero(Integer.parseInt(tilinumeroTxt.getText()));
		lasku.setSaajan_nimi(saajanNimiTxt.getText());
		lasku.setViitenumero(Integer.parseInt(viitenumeroTxt.getText()));

		lasku.setMaksun_maara(Integer.parseInt(maksuMaaraTxt.getText())); //TODO
		lasku.muokkaaLasku(conn);
	}
	
	public String dateTimeToSqlDate(DateTime date) {
		
		int vuosi = date.getYear();
		int kuukausi = date.getMonth()+1;
		int paiva = date.getDay();
		
		String sqlDateTime = vuosi+"-"+kuukausi+"-"+paiva;
		
		return sqlDateTime;
	}
	public int laskePalveluHinta() throws SQLException {
		String sql = "SELECT Palvelun_hinta FROM palvelu WHERE PalveluID = ?";
		ResultSet tulosjoukko = null;
		PreparedStatement lause = null;
	
		try {
			// luo PreparedStatement-olio sql-lauseelle
			lause = conn.prepareStatement(sql);
			
			lause.setInt(1, getHintaPalveluId()); // asetetaan where ehtoon (?) arvo
			// suorita sql-lause
			tulosjoukko = lause.executeQuery();	
			if (tulosjoukko == null) {
				System.out.println("");
			}
		} catch (SQLException se) {
	        // SQL virheet
	        se.printStackTrace();
	    } catch (Exception e) {
	        // JDBC virheet
	        e.printStackTrace();
		}
		
	
	    java.sql.ResultSetMetaData rsmd = tulosjoukko.getMetaData();
	    int columnsNumber = rsmd.getColumnCount();
	    int palveluHinta = 0;
		try {
			while (tulosjoukko.next ()){
	            for (int i = 1; i <= columnsNumber; i++) {
	               palveluHinta = Integer.parseInt(tulosjoukko.getString(i));

			}
		}
	
		}catch (SQLException e) {
			e.printStackTrace();
		}catch (Exception e) {
	        e.printStackTrace();
	    }
		return palveluHinta;
	}
	
	public int laskeMajoitusHinta() throws SQLException {
		String sql = "SELECT Hinta FROM majoitus WHERE MajoitusID = ?";
		ResultSet tulosjoukko = null;
		PreparedStatement lause = null;
	
		try {
			// luo PreparedStatement-olio sql-lauseelle
			lause = conn.prepareStatement(sql);
			
			lause.setInt(1, getHintaMajoitusId()); // asetetaan where ehtoon (?) arvo
			// suorita sql-lause
			tulosjoukko = lause.executeQuery();	
			if (tulosjoukko == null) {
				System.out.println("");
			}
		} catch (SQLException se) {
	        // SQL virheet
	        se.printStackTrace();
	    } catch (Exception e) {
	        // JDBC virheet
	        e.printStackTrace();
		}
		
	
	    java.sql.ResultSetMetaData rsmd = tulosjoukko.getMetaData();
	    int columnsNumber = rsmd.getColumnCount();
	    int majoitusHinta = 0;
		try {
			while (tulosjoukko.next ()){
	            for (int i = 1; i <= columnsNumber; i++) {
	               	majoitusHinta = Integer.parseInt(tulosjoukko.getString(i));
	
			}
		}
	
		}catch (SQLException e) {
			e.printStackTrace();
		}catch (Exception e) {
	        e.printStackTrace();
	    }
		return majoitusHinta;
	}
	
	public int laskeYhteishinta() throws SQLException {
		int yhteishinta = laskePalveluHinta()+laskeMajoitusHinta();
		return yhteishinta;
	}
	
	public int [] getIndexes(String date) throws SQLException {
		int eka = date.indexOf("-");
		int toka = date.indexOf("-", eka+1);
		int[] indeksit = new int[2];
		indeksit[0] = eka;
		indeksit[1] = toka;
		return indeksit;
	}
	
	public String getYearFromStringDate(ResultSet set, String sarake) throws SQLException {
		String date = set.getString(sarake);
		int [] idxTbl  = getIndexes(date);
		String vuosi = date.substring(0, idxTbl[0]);
		
		return vuosi;
	}
	
	public String getMonthFromStringDate(ResultSet set, String sarake) throws SQLException {
		String date = set.getString(sarake);
		int [] idxTbl  = getIndexes(date);
		String kuukausi = date.substring(idxTbl[0], idxTbl[1]);
		
		return kuukausi;
	}
	
	public String getDayFromStringDate(ResultSet set, String sarake) throws SQLException {
		String date = set.getString(sarake);
		int [] idxTbl  = getIndexes(date);
		String paiva = date.substring(idxTbl[1], date.length());
		
		return paiva;
	}
	
	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		muokkaaLaskuShl = new Shell(SWT.CLOSE | SWT.MIN);
		muokkaaLaskuShl.setSize(980, 584);
		
		
		// "Responsiivisuus - maaritelty tassa vain 1920x1080 resoluutiolle, pidemmalle kehitetyssa ohjelmassa pitaisi muillekin resoluutioille"
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		if (screenSize.getWidth() < 1600){
			muokkaaLaskuShl.setSize((int)(screenSize.getWidth() / 1.25), (int) (screenSize.getHeight() / 1.2));
		}
		else if (screenSize.getWidth() > 1600){
			muokkaaLaskuShl.setSize((int)(screenSize.getWidth() / 2.08), (int) (screenSize.getHeight() / 2));
		}
		else if (screenSize.getWidth() == 1600) {
			muokkaaLaskuShl.setSize((int) (screenSize.getWidth() / 3.4), (int) (screenSize.getHeight() / 2.2));
		}
		
		
		muokkaaLaskuShl.setText("Muokkaa laskua");
		GridLayout gl_muokkaaLaskuShl = new GridLayout(3, false);
		muokkaaLaskuShl.setLayout(gl_muokkaaLaskuShl);
		
		composite_1 = new Composite(muokkaaLaskuShl, SWT.H_SCROLL | SWT.V_SCROLL);
		composite_1.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		composite_1.setLayout(new GridLayout(1, false));
		
		Label laskutettavaMajoitusvarausLbl = new Label(composite_1, SWT.NONE);
		laskutettavaMajoitusvarausLbl.setText("Valitse laskutettavat majoitukset");
		
		
		// Laskutettavat majoitukset taulu
		laskutettavaMajoitusvarausTbl = new Table(composite_1, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI | SWT.TOGGLE);
		laskutettavaMajoitusvarausTbl.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				
				TableItem item =  laskutettavaMajoitusvarausTbl.getItem(new Point(e.x, e.y));

			    if (item == null) {
			    	laskutettavaMajoitusvarausTbl.deselectAll();
			    	setHintaMajoitusId(0);
			    }
				TableItem valittuMajoitusTbl[] = laskutettavaMajoitusvarausTbl.getSelection();
				if (valittuMajoitusTbl.length > 0) {
					setHintaMajoitusId(Integer.parseInt(valittuMajoitusTbl[0].getText(1)));
				}
				try {
					maksuMaaraTxt.setText(Integer.toString(laskeYhteishinta()));
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		
		GridData gd_laskutettavaMajoitusvarausTbl = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gd_laskutettavaMajoitusvarausTbl.heightHint = 313;
		gd_laskutettavaMajoitusvarausTbl.widthHint = 245;
		laskutettavaMajoitusvarausTbl.setLayoutData(gd_laskutettavaMajoitusvarausTbl);
		laskutettavaMajoitusvarausTbl.setSize(529, 433);
		laskutettavaMajoitusvarausTbl.setHeaderVisible(true);
		laskutettavaMajoitusvarausTbl.setLinesVisible(true);
		
		
		// Taulun kentat
		TableColumn majoitusvarausID = new TableColumn(laskutettavaMajoitusvarausTbl, SWT.NONE);
		majoitusvarausID.setWidth(110);
		majoitusvarausID.setText("Majoitusvaraus ID");
		
		TableColumn majoitusId = new TableColumn(laskutettavaMajoitusvarausTbl, SWT.NONE);
		majoitusId.setToolTipText("");
		majoitusId.setWidth(100);
		majoitusId.setText("MajoitusID");
		
		composite_2 = new Composite(muokkaaLaskuShl, SWT.H_SCROLL | SWT.V_SCROLL);
		composite_2.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		composite_2.setLayout(new GridLayout(1, false));
		
		laskutettavaPalveluLbl = new Label(composite_2, SWT.NONE);
		laskutettavaPalveluLbl.setText("Valitse laskutettavat palvelut");
		
		
		// Laskutettavat palvelut taulu
		laskutettavatPalveluvarauksetTbl = new Table(composite_2, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI | SWT.TOGGLE);
		laskutettavatPalveluvarauksetTbl.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {

			    TableItem item =  laskutettavatPalveluvarauksetTbl.getItem(new Point(e.x, e.y));

			    if (item == null) {
			    	laskutettavatPalveluvarauksetTbl.deselectAll();
			    	setHintaPalveluId(0);
			    }
				TableItem valittuPalveluTbl[] = laskutettavatPalveluvarauksetTbl.getSelection();
				if (valittuPalveluTbl.length > 0) {
					setHintaPalveluId(Integer.parseInt(valittuPalveluTbl[0].getText(1)));
				}
				try {
					maksuMaaraTxt.setText(Integer.toString(laskePalveluHinta()+laskeMajoitusHinta()));
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		GridData gd_laskutettavatPalveluvarauksetTbl = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_laskutettavatPalveluvarauksetTbl.heightHint = 313;
		laskutettavatPalveluvarauksetTbl.setLayoutData(gd_laskutettavatPalveluvarauksetTbl);
		laskutettavatPalveluvarauksetTbl.setHeaderVisible(true);
		laskutettavatPalveluvarauksetTbl.setLinesVisible(true);
		
		
		// Taulun kentat
		palveluvarausId = new TableColumn(laskutettavatPalveluvarauksetTbl, SWT.NONE);
		palveluvarausId.setWidth(100);
		palveluvarausId.setText("Palveluvaraus ID");
		
		palveluId = new TableColumn(laskutettavatPalveluvarauksetTbl, SWT.NONE);
		palveluId.setWidth(100);
		palveluId.setText("PalveluID");
		
		composite_4 = new Composite(muokkaaLaskuShl, SWT.NONE);
		GridData gd_composite_4 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_composite_4.widthHint = 330;
		composite_4.setLayoutData(gd_composite_4);
		composite_4.setLayout(new GridLayout(1, false));
		
		
		// LaskuID label ja tekstikentta
		laskuID = new Label(composite_4, SWT.NONE);
		laskuID.setSize(51, 15);
		laskuID.setText("Laskun ID");
		
		laskuIdTxt = new Text(composite_4, SWT.BORDER);
		laskuIdTxt.setEditable(false);
		laskuIdTxt.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				// TODO LASKU ID KUUNTELIJA
			}
		});
		laskuIdTxt.setToolTipText("Laskun ID");
		GridData gd_laskuIdTxt = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_laskuIdTxt.widthHint = 182;
		laskuIdTxt.setLayoutData(gd_laskuIdTxt);
		laskuIdTxt.setSize(76, 21);
		
		asiakasIdLbl = new Label(composite_4, SWT.NONE);
		asiakasIdLbl.setText("Asiakkaan ID");
		
		asiakasIdTxt = new Text(composite_4, SWT.BORDER);
		asiakasIdTxt.setEditable(false);
		asiakasIdTxt.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				// TODO ASIAKAS ID KUUNTELIJA
			}
		});
		asiakasIdTxt.setToolTipText("Asiakkaan ID");
		asiakasIdTxt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		
		// Tilinumero label ja tekstikentta
		tilinumeroLbl = new Label(composite_4, SWT.NONE);
		tilinumeroLbl.setText("Tilinumero");
		
		tilinumeroTxt = new Text(composite_4, SWT.BORDER);
		tilinumeroTxt.addVerifyListener(new VerifyListener() {
			 @Override
			 public void verifyText(VerifyEvent e) {

				 String string = e.text;
			      char[] chars = new char[string.length()];
			      string.getChars(0, chars.length, chars, 0);
			      for (int i = 0; i < chars.length; i++) {
			         if (!('0' <= chars[i] && chars[i] <= '9')) {
			            e.doit = false;
			            return;
			         }
			      }
	        }
		});
		tilinumeroTxt.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				// TODO TILINUMERO KUUNTELIJA
			}
		});
		tilinumeroTxt.setToolTipText("Tilinumero");
		tilinumeroTxt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		
		// Saajan nimi label ja tekstikentta
		saajanNimiLbl = new Label(composite_4, SWT.NONE);
		saajanNimiLbl.setToolTipText("");
		saajanNimiLbl.setText("Saajan nimi");
		
		saajanNimiTxt = new Text(composite_4, SWT.BORDER);
		saajanNimiTxt.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				// TODO SAAJA NIMI KUUNTELIJA
			}
		});
		saajanNimiTxt.setToolTipText("Maksun saajan nimi");
		saajanNimiTxt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		
		// Viitenumero label ja tekstikentta
		viitenumeroLbl = new Label(composite_4, SWT.NONE);
		viitenumeroLbl.setText("Viitenumero");
		
		viitenumeroTxt = new Text(composite_4, SWT.BORDER);
		viitenumeroTxt.addVerifyListener(new VerifyListener() {
			 @Override
			 public void verifyText(VerifyEvent e) {

				 String string = e.text;
			      char[] chars = new char[string.length()];
			      string.getChars(0, chars.length, chars, 0);
			      for (int i = 0; i < chars.length; i++) {
			         if (!('0' <= chars[i] && chars[i] <= '9')) {
			            e.doit = false;
			            return;
			         }
			      }
	        }
		});
		viitenumeroTxt.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				// TODO VIITENUMERO KUUNTELIJA
			}
		});
		viitenumeroTxt.setToolTipText("Viitenumero");
		viitenumeroTxt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		
		// Maksun maara label ja tekstikentta
		maksuMaaraLbl = new Label(composite_4, SWT.NONE);
		maksuMaaraLbl.setText("Maksun määrä");
		
		maksuMaaraTxt = new Text(composite_4, SWT.BORDER);
		maksuMaaraTxt.addVerifyListener(new VerifyListener() {
			 @Override
			 public void verifyText(VerifyEvent e) {

				 String string = e.text;
			      char[] chars = new char[string.length()];
			      string.getChars(0, chars.length, chars, 0);
			      for (int i = 0; i < chars.length; i++) {
			         if (!('0' <= chars[i] && chars[i] <= '9')) {
			            e.doit = false;
			            return;
			         }
			      }
	        }
		});
		maksuMaaraTxt.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				// TODO MAKSUMAARA KUUNTELIJA
			}
		});
		maksuMaaraTxt.setToolTipText("Maksun määrä");
		maksuMaaraTxt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		
		// Laskun luontipvm lbl ja aikavalinta
		laskuLuontiPvmLbl = new Label(composite_4, SWT.NONE);
		laskuLuontiPvmLbl.setText("Laskun luontipäivämäärä");
		
		laskunLuontiPvmDateTime = new DateTime(composite_4, SWT.BORDER);
		laskunLuontiPvmDateTime.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//TODO LASKUN LUONTIPVM KUUNTELIJA

			}
		});
		
		
		// Laskun erapvm label ja aikavalinta
		laskunErapvmLbl = new Label(composite_4, SWT.NONE);
		laskunErapvmLbl.setText("Laskun eräpäivämäärä");
		
		laskunEraPvmDateTime = new DateTime(composite_4, SWT.BORDER);
		laskunEraPvmDateTime.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO LASKUN ERAPVM KUUNTELIJA
			}
		});
		
		
		// Laskutyyppi label ja pudotusvalikko
		laskunTyyppiLbl = new Label(composite_4, SWT.NONE);
		laskunTyyppiLbl.setText("Valitse laskun tyyppi");
		
		laskuTyyppiCombo = new Combo(composite_4, SWT.NONE);
		laskuTyyppiCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//TODO LASKU TYYPPI KUUNTELIJA
				/*
				String valinta = laskuTyyppiCombo.getText();
				System.out.println(valinta);
				*/
			}
		});
		laskuTyyppiCombo.setItems(new String[] {"Paperilasku", "Sähköpostilasku"});
		
		// Gridin asettelua
		new Label(muokkaaLaskuShl, SWT.NONE);
		new Label(muokkaaLaskuShl, SWT.NONE);
		new Label(muokkaaLaskuShl, SWT.NONE);
		
		
		// Alanapit
		laskuPeruutaBtn = new Button(muokkaaLaskuShl, SWT.NONE);
		laskuPeruutaBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				muokkaaLaskuShl.close();
				laskutusEtusivuGui = new LaskutusEtusivuGUI();
				try {
					laskutusEtusivuGui.open();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		laskuPeruutaBtn.setText("Peruuta");
		new Label(muokkaaLaskuShl, SWT.NONE);
		
		muokkaaLaskuBtn = new Button(muokkaaLaskuShl, SWT.NONE);
		muokkaaLaskuBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					TableItem valitutPalveluvarauksetTbl[] = laskutettavatPalveluvarauksetTbl.getSelection();	
					TableItem valittuMajoitusvarausTbl[] = laskutettavaMajoitusvarausTbl.getSelection();
					
					if (valitutPalveluvarauksetTbl.length <1 && valittuMajoitusvarausTbl.length <1) {
						valittuVarausVaroitusDialog = new ValittuVarausVaroitusDialog();
						valittuVarausVaroitusDialog.open();
					}
					else {
						muokkaaLasku();
						muokkaaLaskuShl.close();
						laskutusEtusivuGui = new LaskutusEtusivuGUI();
						laskutusEtusivuGui.open();
					}
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		});
		muokkaaLaskuBtn.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		muokkaaLaskuBtn.setText("Hyväksy");
	}

	public int getValittuMajoitusvarausID() {
		return valittuMajoitusvarausID;
	}

	public void setValittuMajoitusvarausID(int valittuMajoitusvarausID) {
		this.valittuMajoitusvarausID = valittuMajoitusvarausID;
	}

	public int getValittuPalveluVarausID() {
		return valittuPalveluVarausID;
	}

	public void setValittuPalveluVarausID(int valittuPalveluVarausID) {
		this.valittuPalveluVarausID = valittuPalveluVarausID;
	}

	public int getValittuAsiakasID() {
		return valittuAsiakasID;
	}

	public void setValittuAsiakasID(int valittuAsiakasID) {
		this.valittuAsiakasID = valittuAsiakasID;
	}

	public int getValittuLaskuID() {
		return valittuLaskuID;
	}

	public void setValittuLaskuID(int valittuLaskuID) {
		this.valittuLaskuID = valittuLaskuID;
	}

	public int getHintaPalveluId() {
		return hintaPalveluId;
	}

	public void setHintaPalveluId(int hintaPalveluId) {
		this.hintaPalveluId = hintaPalveluId;
	}

	public int getHintaMajoitusId() {
		return hintaMajoitusId;
	}

	public void setHintaMajoitusId(int hintaMajoitusId) {
		this.hintaMajoitusId = hintaMajoitusId;
	}
	
	
}
