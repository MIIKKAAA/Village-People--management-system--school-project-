package laskutus;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import swing2swt.layout.BorderLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import ApuMetodeja.ApuMetodeja;

import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;

public class LaskuPohjaEmail {

	protected Shell laskuPohjaShl;
	private Label label;
	private Label titleLbl;
	private Label laskuLbl;
	private Label asiakkaanTiedotLbl;
	private Table laskuTiedotTbl;
	private TableColumn laskuIdColumn;
	private TableColumn paivamaaraColumn;
	private TableColumn erapaivaColumn;
	private TableColumn viitenumeroColumn;
	private Label yhteensaMaksettavaaLbl;
	private Label label_1;
	private Label nimiLbl;
	private Label osoiteLbl;
	private Label postinumeroLbl;
	private Label postitoimipaikkaLbl;
	private Label hintaLbl;
	private Label label_2;
	private Table palveluTbl;
	private TableColumn palveluNimiColumn;
	private TableColumn palveluAloituspvmColumn;
	private TableColumn palveluLopetuspvmColumn;
	private Label lblNewLabel;
	private Label veloitettavatLbl;
	private TableColumn palveluHintaColumn;
	private Table majoitusTbl;
	private TableColumn majoitusNimiColumn;
	private TableColumn tblclmnAloituspivmr;
	private TableColumn majoitusLopetuspvmColumn;
	private TableColumn majoitusHintaColumn;
	private int valittuLaskuID;
	private int valittuAsiakasID;
	private int valittuPalveluvarausID;
	private int valittuMajoitusvarausID;
	private int haettuPalveluHinta;
	private int haettuMajoitusHinta;
	Connection conn;
	private TableColumn tilinumeroColumn;
	String valittuKansio;
	private Button btnPeruuta;
	private Button tulostaBtn;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			LaskuPohjaEmail window = new LaskuPohjaEmail();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
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
	/**
	 * Open the window.
	 * @throws Exception 
	 * @throws SQLException 
	 */
	public void open() throws SQLException, Exception {
		yhdista();;
		createContents();
		// Avautuessa keskelle
		Display display = ApuMetodeja.centerWindow(laskuPohjaShl);
		populateMajoitusTiedotTbl();
		populatePalveluTiedotTbl();
		populateLaskuTiedotTbl();
		populateAsiakkaanTiedot();
		asetaYhteisHinta();
		laskuPohjaShl.open();
		laskuPohjaShl.layout();
		while (!laskuPohjaShl.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
	private void asetaYhteisHinta() {
		hintaLbl.setText(Integer.toString(getHaettuPalveluHinta()+getHaettuMajoitusHinta())+"€");
	}
	
	private void populateAsiakkaanTiedot() throws SQLException{
		String sql = "SELECT Etunimi, Sukunimi, Postinumero, Postiosoite, Postitoimipaikka FROM asiakas WHERE AsiakasID = ?";
		ResultSet tulosjoukko = null;
		PreparedStatement lause2 = null;
		try {
			// luo PreparedStatement-olio sql-lauseelle
			lause2 = conn.prepareStatement(sql);
			lause2.setInt(1, getValittuAsiakasID()); // asetetaan where ehtoon (?) arvo
			// suorita sql-lause
			tulosjoukko = lause2.executeQuery();	
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
        
        while (tulosjoukko.next()) {
	        String koko_nimi = tulosjoukko.getString("Etunimi")+" "+tulosjoukko.getString("Sukunimi");
	        nimiLbl.setText(koko_nimi);
	        osoiteLbl.setText(tulosjoukko.getString("Postiosoite"));
	        postinumeroLbl.setText(tulosjoukko.getString("Postinumero"));
	        postitoimipaikkaLbl.setText(tulosjoukko.getString("Postitoimipaikka"));
        }
        
	}
	
	private void populateLaskuTiedotTbl() throws SQLException{

		String sql = "SELECT LaskuID, Laskunluontipvm, Eräpvm, Viitenumero, Tilinumero FROM laskujen_hallinta_ja_seuranta WHERE LaskuID = ?";
		ResultSet tulosjoukko = null;
		PreparedStatement lause2 = null;
		try {
			// luo PreparedStatement-olio sql-lauseelle
			lause2 = conn.prepareStatement(sql);
			lause2.setInt(1, getValittuLaskuID()); // asetetaan where ehtoon (?) arvo
			// suorita sql-lause
			tulosjoukko = lause2.executeQuery();	
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
	            item = new TableItem(laskuTiedotTbl, SWT.NONE);
	            item.setText(0, tulosjoukko.getString("LaskuID"));
	            item.setText(1, tulosjoukko.getString("Tilinumero"));
		        item.setText(2, tulosjoukko.getString("Viitenumero"));
		        item.setText(3, tulosjoukko.getString("Laskunluontipvm"));
		        item.setText(4, tulosjoukko.getString("Eräpvm"));
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	private void populateMajoitusTiedotTbl() throws SQLException {
		int majoitusId = 0;
		String sql = "SELECT MajoitusID From majoitusvaraus WHERE MajoitusvarausID = ?";
		ResultSet majoitusIdJoukko = null;
		PreparedStatement lause1 = null;
		try {
			// luo PreparedStatement-olio sql-lauseelle
			lause1 = conn.prepareStatement(sql);
			lause1.setInt(1, getValittuMajoitusvarausID()); // asetetaan where ehtoon (?) arvo
			// suorita sql-lause
			majoitusIdJoukko = lause1.executeQuery();	
			if (majoitusIdJoukko == null) { // tulosjoukkoon odotetaan ainoastaan max. yhtä riviä
				System.out.println("...");
			}
		} catch (SQLException se) {
            // SQL virheet
            se.printStackTrace();
        } catch (Exception e) {
            // JDBC virheet
            e.printStackTrace();
		}
		while (majoitusIdJoukko.next()) {
			majoitusId = majoitusIdJoukko.getInt("MajoitusID");
		}
		System.out.println("majId: "+majoitusId);

		sql = "SELECT aloituspvm, lopetuspvm, Hinta, Nimi FROM majoitusvaraus INNER JOIN "+
				"majoitus ON majoitusvaraus.MajoitusID=majoitus.MajoitusID WHERE majoitusvaraus.MajoitusID = ? AND AsiakasID = ?";
		ResultSet tulosjoukko = null;
		PreparedStatement lause2 = null;
		try {
			// luo PreparedStatement-olio sql-lauseelle
			lause2 = conn.prepareStatement(sql);
			lause2.setInt(1, majoitusId); // asetetaan where ehtoon (?) arvo
			lause2.setInt(2, getValittuAsiakasID());
			// suorita sql-lause
			tulosjoukko = lause2.executeQuery();	
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
	            item = new TableItem(majoitusTbl, SWT.NONE);
	            item.setText(0, tulosjoukko.getString("Nimi"));
		        item.setText(1, tulosjoukko.getString("aloituspvm"));
		        item.setText(2, tulosjoukko.getString("lopetuspvm"));
	            item.setText(3, tulosjoukko.getString("Hinta"));
	            setHaettuMajoitusHinta(haettuMajoitusHinta+Integer.parseInt(tulosjoukko.getString("Hinta")));
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	private void populatePalveluTiedotTbl() throws SQLException {
		int palveluId = 0;
		String sql = "SELECT PalveluID From palvelun_varaus WHERE PalveluvarausID = ?";
		ResultSet palveluIdJoukko = null;
		PreparedStatement lause1 = null;
		try {
			// luo PreparedStatement-olio sql-lauseelle
			lause1 = conn.prepareStatement(sql);
			lause1.setInt(1, getValittuPalveluvarausID()); // asetetaan where ehtoon (?) arvo
			// suorita sql-lause
			palveluIdJoukko = lause1.executeQuery();	
			if (palveluIdJoukko == null) { // tulosjoukkoon odotetaan ainoastaan max. yhtä riviä
				System.out.println("...");
			}
		} catch (SQLException se) {
            // SQL virheet
            se.printStackTrace();
        } catch (Exception e) {
            // JDBC virheet
            e.printStackTrace();
		}
		while (palveluIdJoukko.next()) {
			palveluId = palveluIdJoukko.getInt("PalveluID");
		}
		System.out.println("PalvID: "+palveluId);

		sql = "SELECT aloituspvm, lopetuspvm, Palvelun_hinta, Palvelun_nimi FROM palvelun_varaus INNER JOIN "+
		"palvelu ON palvelun_varaus.PalveluID=palvelu.PalveluID WHERE palvelun_varaus.PalveluID = ?";
		ResultSet tulosjoukko = null;
		PreparedStatement lause2 = null;
		try {
			// luo PreparedStatement-olio sql-lauseelle
			lause2 = conn.prepareStatement(sql);
			lause2.setInt(1, palveluId); // asetetaan where ehtoon (?) arvo
			// suorita sql-lause
			tulosjoukko = lause2.executeQuery();	
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
	            item = new TableItem(palveluTbl, SWT.NONE);
	            item.setText(0, tulosjoukko.getString("Palvelun_nimi"));
		        item.setText(1, tulosjoukko.getString("aloituspvm"));
		        item.setText(2, tulosjoukko.getString("lopetuspvm"));
	            item.setText(3, tulosjoukko.getString("Palvelun_hinta"));
	            setHaettuPalveluHinta(Integer.parseInt(tulosjoukko.getString("Palvelun_hinta")));
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
		laskuPohjaShl = new Shell(SWT.NONE);
		laskuPohjaShl.setSize(556, 683);
		laskuPohjaShl.setText("SWT Application");
		laskuPohjaShl.setLayout(new BorderLayout(0, 0));
		
		Composite composite = new Composite(laskuPohjaShl, SWT.BORDER);
		composite.setLayoutData(BorderLayout.CENTER);
		Display display = Display.getCurrent();
		composite.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		GridLayout gl_composite = new GridLayout(1, false);
		composite.setLayout(gl_composite);
		
		label = new Label(composite, SWT.NONE);
		
		titleLbl = new Label(composite, SWT.NONE);
		titleLbl.setFont(SWTResourceManager.getFont("Segoe UI", 18, SWT.BOLD));
		titleLbl.setText("VILLAGE PEOPLE");
		titleLbl.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		
		laskuLbl = new Label(composite, SWT.NONE);
		laskuLbl.setText("Lasku");
		laskuLbl.setFont(SWTResourceManager.getFont("Segoe UI", 14, SWT.NORMAL));
		laskuLbl.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		
		asiakkaanTiedotLbl = new Label(composite, SWT.NONE);
		asiakkaanTiedotLbl.setText("Asiakkaan tiedot");
		asiakkaanTiedotLbl.setFont(SWTResourceManager.getFont("Segoe UI", 14, SWT.NORMAL));
		asiakkaanTiedotLbl.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		
		nimiLbl = new Label(composite, SWT.NONE);
		nimiLbl.setText("NIMI TÄHÄN");
		nimiLbl.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		
		osoiteLbl = new Label(composite, SWT.NONE);
		osoiteLbl.setText("OSOITE TÄHÄN");
		osoiteLbl.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		
		postinumeroLbl = new Label(composite, SWT.NONE);
		postinumeroLbl.setText("POSTINUMERO TAHAN");
		postinumeroLbl.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		
		postitoimipaikkaLbl = new Label(composite, SWT.NONE);
		postitoimipaikkaLbl.setText("POSTITOIMIPAIKKA TÄHÄN");
		postitoimipaikkaLbl.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		
		label_1 = new Label(composite, SWT.NONE);
		
		laskuTiedotTbl = new Table(composite, SWT.BORDER | SWT.READ_ONLY);
		laskuTiedotTbl.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				laskuTiedotTbl.deselectAll();
			}
		});
		laskuTiedotTbl.setHeaderVisible(true);
		laskuTiedotTbl.setLinesVisible(true);
		GridData gd_laskuTiedotTbl = new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1);
		gd_laskuTiedotTbl.heightHint = 30;
		gd_laskuTiedotTbl.widthHint = 500;
		laskuTiedotTbl.setLayoutData(gd_laskuTiedotTbl);
		
		laskuIdColumn = new TableColumn(laskuTiedotTbl, SWT.NONE);
		laskuIdColumn.setResizable(false);
		laskuIdColumn.setWidth(82);
		laskuIdColumn.setText("Lasku ID");
		
		tilinumeroColumn = new TableColumn(laskuTiedotTbl, SWT.NONE);
		tilinumeroColumn.setWidth(100);
		tilinumeroColumn.setText("Tilinumero");
		
		viitenumeroColumn = new TableColumn(laskuTiedotTbl, SWT.NONE);
		viitenumeroColumn.setResizable(false);
		viitenumeroColumn.setWidth(120);
		viitenumeroColumn.setText("Viitenumero");
		
		paivamaaraColumn = new TableColumn(laskuTiedotTbl, SWT.NONE);
		paivamaaraColumn.setResizable(false);
		paivamaaraColumn.setWidth(100);
		paivamaaraColumn.setText("Päivämäärä");
		
		erapaivaColumn = new TableColumn(laskuTiedotTbl, SWT.NONE);
		erapaivaColumn.setResizable(false);
		erapaivaColumn.setWidth(100);
		erapaivaColumn.setText("Eräpäivä");
		
		lblNewLabel = new Label(composite, SWT.NONE);
		
		veloitettavatLbl = new Label(composite, SWT.NONE);
		veloitettavatLbl.setText("Veloitettavat majoitukset ja palvelut");
		veloitettavatLbl.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		
		palveluTbl = new Table(composite, SWT.BORDER);
		palveluTbl.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				palveluTbl.deselectAll();
			}
		});
		GridData gd_palveluTbl = new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1);
		gd_palveluTbl.widthHint = 513;
		gd_palveluTbl.heightHint = 45;
		palveluTbl.setLayoutData(gd_palveluTbl);
		palveluTbl.setHeaderVisible(true);
		palveluTbl.setLinesVisible(true);
		
		palveluNimiColumn = new TableColumn(palveluTbl, SWT.NONE);
		palveluNimiColumn.setResizable(false);
		palveluNimiColumn.setWidth(140);
		palveluNimiColumn.setText("Palvelu");
		
		palveluAloituspvmColumn = new TableColumn(palveluTbl, SWT.NONE);
		palveluAloituspvmColumn.setResizable(false);
		palveluAloituspvmColumn.setWidth(130);
		palveluAloituspvmColumn.setText("Aloituspäivämäärä");
		
		palveluLopetuspvmColumn = new TableColumn(palveluTbl, SWT.NONE);
		palveluLopetuspvmColumn.setResizable(false);
		palveluLopetuspvmColumn.setWidth(160);
		palveluLopetuspvmColumn.setText("Lopetuspäivämäärä");
		
		palveluHintaColumn = new TableColumn(palveluTbl, SWT.NONE);
		palveluHintaColumn.setResizable(false);
		palveluHintaColumn.setWidth(100);
		palveluHintaColumn.setText("Hinta");
		
		majoitusTbl = new Table(composite, SWT.BORDER | SWT.FULL_SELECTION);
		majoitusTbl.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				majoitusTbl.deselectAll();
			}
		});
		GridData gd_majoitusTbl = new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1);
		gd_majoitusTbl.heightHint = 45;
		gd_majoitusTbl.widthHint = 513;
		majoitusTbl.setLayoutData(gd_majoitusTbl);
		majoitusTbl.setHeaderVisible(true);
		majoitusTbl.setLinesVisible(true);
		
		majoitusNimiColumn = new TableColumn(majoitusTbl, SWT.NONE);
		majoitusNimiColumn.setResizable(false);
		majoitusNimiColumn.setWidth(140);
		majoitusNimiColumn.setText("Majoitus");
		
		tblclmnAloituspivmr = new TableColumn(majoitusTbl, SWT.NONE);
		tblclmnAloituspivmr.setResizable(false);
		tblclmnAloituspivmr.setWidth(130);
		tblclmnAloituspivmr.setText("Aloituspäivämäärä");
		
		majoitusLopetuspvmColumn = new TableColumn(majoitusTbl, SWT.NONE);
		majoitusLopetuspvmColumn.setResizable(false);
		majoitusLopetuspvmColumn.setWidth(160);
		majoitusLopetuspvmColumn.setText("Lopetuspäivämäärä");
		
		majoitusHintaColumn = new TableColumn(majoitusTbl, SWT.LEFT);
		majoitusHintaColumn.setResizable(false);
		majoitusHintaColumn.setWidth(100);
		majoitusHintaColumn.setText("Hinta");
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		
		label_2 = new Label(composite, SWT.NONE);
		
		yhteensaMaksettavaaLbl = new Label(composite, SWT.NONE);
		yhteensaMaksettavaaLbl.setFont(SWTResourceManager.getFont("Segoe UI", 14, SWT.NORMAL));
		yhteensaMaksettavaaLbl.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		yhteensaMaksettavaaLbl.setText("Yhteensä maksettavaa");
		yhteensaMaksettavaaLbl.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		
		hintaLbl = new Label(composite, SWT.NONE);
		hintaLbl.setFont(SWTResourceManager.getFont("Segoe UI", 14, SWT.NORMAL));
		hintaLbl.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		hintaLbl.setText("HINTA TÄHÄN");
		hintaLbl.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		
		Composite composite_1 = new Composite(laskuPohjaShl, SWT.NONE);
		composite_1.setLayoutData(BorderLayout.SOUTH);
		composite_1.setLayout(new GridLayout(3, false));
		
		btnPeruuta = new Button(composite_1, SWT.NONE);
		btnPeruuta.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		btnPeruuta.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				laskuPohjaShl.close();
			}
		});
		btnPeruuta.setText("Peruuta");
		
		tulostaBtn = new Button(composite_1, SWT.NONE);
		tulostaBtn.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		tulostaBtn.setText("Tulosta");
		
		Button luoLiiteBtn = new Button(composite_1, SWT.NONE);
		luoLiiteBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				palveluTbl.deselectAll();
				majoitusTbl.deselectAll();
				laskuTiedotTbl.deselectAll();
				 Image image = new Image(display, composite.getBounds().width, composite.getBounds().height);
				 ImageLoader loader = new ImageLoader();
				
				 GC gc = new GC(image);
				 composite.print(gc);
				 gc.dispose();
				 
				 try {
					DirectoryDialog dialog = new DirectoryDialog(laskuPohjaShl);

					 String kansio = dialog.open();
					 kansio.replace("\\", "\\\\");
   
					 loader.data = new ImageData[]{image.getImageData()};
					 loader.save(kansio+"\\laskuliite.png", SWT.IMAGE_PNG);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		luoLiiteBtn.setLayoutData(new GridData(SWT.RIGHT, SWT.BOTTOM, false, false, 1, 1));
		luoLiiteBtn.setText("Luo liite");

	}

	public int getValittuLaskuID() {
		return valittuLaskuID;
	}

	public void setValittuLaskuID(int valittuLaskuID) {
		this.valittuLaskuID = valittuLaskuID;
	}

	public int getValittuAsiakasID() {
		return valittuAsiakasID;
	}

	public void setValittuAsiakasID(int valittuAsiakasID) {
		this.valittuAsiakasID = valittuAsiakasID;
	}

	public int getValittuPalveluvarausID() {
		return valittuPalveluvarausID;
	}

	public void setValittuPalveluvarausID(int valittuPalveluvarausID) {
		this.valittuPalveluvarausID = valittuPalveluvarausID;
	}

	public int getValittuMajoitusvarausID() {
		return valittuMajoitusvarausID;
	}

	public void setValittuMajoitusvarausID(int valittuMajoitusvarausID) {
		this.valittuMajoitusvarausID = valittuMajoitusvarausID;
	}
	public int getHaettuPalveluHinta() {
		return haettuPalveluHinta;
	}
	public void setHaettuPalveluHinta(int haettuPalveluHinta) {
		this.haettuPalveluHinta = haettuPalveluHinta;
	}
	public int getHaettuMajoitusHinta() {
		return haettuMajoitusHinta;
	}
	public void setHaettuMajoitusHinta(int haettuMajoitusHinta) {
		this.haettuMajoitusHinta = haettuMajoitusHinta;
	}


	
	
}
