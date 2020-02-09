package palvelu_varaus;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Composite;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Text;

import ApuMetodeja.ApuMetodeja;
import laskutus.Lasku;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.events.VerifyEvent;

public class MuokkaaPalveluvarausGUI {

	protected Shell muokkaaPalveluvarausShl;
	private Text palveluvarausIdTxt;
	private Text palveluIdTxt;
	private Text asiakasIdTxt;
	private Label muokkaaPalveluvarausLbl;
	private Label palveluvarausIdLbl;
	private Label palveluIdLbl;
	private Label asiakasID;
	private Label aloituspvmLbl;
	private Label lopetuspvmLbl;
	private Label label;
	private DateTime aloituspvmDateTime;
	private DateTime lopetuspvmDateTime;
	private Connection conn;
	Palveluvaraus palveluvaraus = new Palveluvaraus();
	private int valittuPalveluvarausID;
	private int valittuAsiakasId;
	private int valittuPalveluId;
	private PalveluvarausEtusivuGUI palveluvarausEtusivuGui;
	private Button valitsePalveluBtn;
	private Button valitseAsiakasBtn;
	private ValitseAsiakasGUI_muokkaa valitseAsiakasGui;
	private ValitsePalveluGUI_muokkaa valitsePalveluGui;
	private MuokkaaPalveluvarausGUI muokkaaPalveluvarausGui;
	private int palveluvarausIdTalteen = 0;
	private int palveluIdTalteen = 0;
	private int asiakasIdTalteen = 0;
	private int aloituspvmPaivaTalteen;
	private int aloituspvmKuukausiTalteen;
	private int aloituspvmVuosiTalteen;
	private int lopetuspvmPaivaTalteen;
	private int lopetuspvmKuukausiTalteen;
	private int lopetuspvmVuosiTalteen;


	/**
	 * Open the window.
	 * @throws Exception 
	 * @throws SQLException 
	 */
	public void open() throws SQLException, Exception {
		createContents();
		yhdista();

		luoPalveluvarausTiedot();
		// Avautuessa keskelle
		Display display = ApuMetodeja.centerWindow(muokkaaPalveluvarausShl);
		
		if (getValittuAsiakasId() == 0 && getAsiakasIdTalteen() == 0) {
			asiakasIdTxt.setText("");
		}
		else if (getAsiakasIdTalteen() > 0 && getValittuAsiakasId() == 0) {
			asiakasIdTxt.setText(Integer.toString(getAsiakasIdTalteen()));
		}
		else if (getAsiakasIdTalteen() > 0 && getValittuAsiakasId() > 0) {
			asiakasIdTxt.setText(Integer.toString(getValittuAsiakasId()));
		}
		else if (getAsiakasIdTalteen() == 0 && getValittuAsiakasId() > 0) {
			asiakasIdTxt.setText(Integer.toString(getValittuAsiakasId()));
		}
			
		if (getValittuPalveluId() == 0 && getPalveluIdTalteen() == 0) {
			palveluIdTxt.setText("");
		}
		else if (getPalveluIdTalteen() > 0 && getValittuPalveluId() == 0) {
			palveluIdTxt.setText(Integer.toString(getPalveluIdTalteen()));
		}
		else if (getPalveluIdTalteen() > 0 && getValittuPalveluId() > 0) {
			palveluIdTxt.setText(Integer.toString(getValittuPalveluId()));
		}
		else if (getPalveluIdTalteen() == 0 && getValittuPalveluId() > 0) {
			palveluIdTxt.setText(Integer.toString(getValittuPalveluId()));
		}
		
		if (getValittuPalveluvarausID() == 0 && getPalveluvarausIdTalteen() == 0) {
			palveluvarausIdTxt.setText("");
		}
		else if (getPalveluvarausIdTalteen() > 0 && getValittuPalveluvarausID() == 0) {
			palveluvarausIdTxt.setText(Integer.toString(getPalveluvarausIdTalteen()));
		}
		else if (getPalveluvarausIdTalteen() > 0 && getValittuPalveluvarausID() > 0) {
			palveluvarausIdTxt.setText(Integer.toString(getValittuPalveluvarausID()));
		}
		else if (getPalveluvarausIdTalteen() == 0 && getValittuPalveluvarausID() > 0) {
			palveluvarausIdTxt.setText(Integer.toString(getValittuPalveluvarausID()));
		}
		
		muokkaaPalveluvarausShl.open();
		muokkaaPalveluvarausShl.layout();
		while (!muokkaaPalveluvarausShl.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
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
	
	
	private void luoPalveluvarausTiedot() throws SQLException {
		String sql = "SELECT PalveluvarausID, PalveluID, AsiakasID, aloituspvm, lopetuspvm "+
					"FROM palvelun_varaus WHERE PalveluvarausID = ?";
		ResultSet tulosjoukko = null;
		PreparedStatement lause = null;
		try {
			// luo PreparedStatement-olio sql-lauseelle
			lause = conn.prepareStatement(sql);
			
			lause.setInt(1, getValittuPalveluvarausID()); // asetetaan where ehtoon (?) arvo
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
	
			palveluvarausIdTxt.setText(Integer.toString(tulosjoukko.getInt("PalveluvarausID")));
			asiakasIdTxt.setText(Integer.toString(tulosjoukko.getInt("AsiakasID")));
			palveluIdTxt.setText(Integer.toString(tulosjoukko.getInt("PalveluID")));
			aloituspvmDateTime.setDay(Integer.parseInt(getDayFromStringDate(tulosjoukko, "aloituspvm")));
			aloituspvmDateTime.setMonth(Integer.parseInt(getMonthFromStringDate(tulosjoukko, "aloituspvm"))-1);
			aloituspvmDateTime.setYear(Integer.parseInt(getYearFromStringDate(tulosjoukko, "aloituspvm")));
			lopetuspvmDateTime.setDay(Integer.parseInt(getDayFromStringDate(tulosjoukko, "lopetuspvm")));
			lopetuspvmDateTime.setMonth(Integer.parseInt(getMonthFromStringDate(tulosjoukko, "lopetuspvm"))-1);
			lopetuspvmDateTime.setYear(Integer.parseInt(getYearFromStringDate(tulosjoukko, "lopetuspvm")));
		}
	}
	
	public void muokkaaPalveluvaraus() throws SQLException, Exception {
		boolean palveluvaraus_lisatty = true;
		palveluvaraus = null;
		
		if (palveluvarausIdTxt.getText() == "") {
			System.out.println("Et syöttäny Palveluvaraus ID:tä, yritä uudelleen!");
			muokkaaPalveluvarausShl.close();
		}
		
		try {
			palveluvaraus = Palveluvaraus.haePalveluvaraus(Integer.parseInt(palveluvarausIdTxt.getText()), conn);
		}catch (SQLException e) {
			palveluvaraus_lisatty = false;
			e.printStackTrace();
		} catch (Exception e) {
			palveluvaraus_lisatty = false;
			e.printStackTrace();
		}
		if (asiakasIdTxt.getText() == "") {
			System.out.println("Et syöttänyt Asiakas ID:tä, yritä uudelleen!!");
			muokkaaPalveluvarausShl.close();
		}
		else {
			palveluvaraus.setAsiakasId(Integer.parseInt(asiakasIdTxt.getText()));
		}
		if (palveluIdTxt.getText() == "") {
			System.out.println("Et valinnut palveluta, yritä uudelleen!");
			muokkaaPalveluvarausShl.close();
		}
		else {
			palveluvaraus.setPalveluId(Integer.parseInt(palveluIdTxt.getText()));
		}
		if (palveluvarausIdTxt.getText() == "") {
			System.out.println("Et syöttänyt Palveluvaraus ID:tä, yritä uudelleen!");
			muokkaaPalveluvarausShl.close();
		}
		else {
			palveluvaraus.setPalveluvarausId(Integer.parseInt(palveluvarausIdTxt.getText()));
		}

		palveluvaraus.setAsiakasId(Integer.parseInt(asiakasIdTxt.getText()));
		palveluvaraus.setPalveluId(Integer.parseInt(palveluIdTxt.getText()));
		palveluvaraus.setPalveluvarausId(Integer.parseInt(palveluvarausIdTxt.getText()));
		palveluvaraus.setLopetuspvm(dateTimeToSqlDate(lopetuspvmDateTime));
		palveluvaraus.setAloituspvm(dateTimeToSqlDate(aloituspvmDateTime));
		
		palveluvaraus.muokkaaPalveluvaraus(conn);
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
		String kuukausi = date.substring(idxTbl[0]+1, idxTbl[1]);
		
		return kuukausi;
	}
	
	public String getDayFromStringDate(ResultSet set, String sarake) throws SQLException {
		String date = set.getString(sarake);
		int [] idxTbl  = getIndexes(date);
		String paiva = date.substring(idxTbl[1]+1, date.length());
		
		return paiva;
	}
	
	public String dateTimeToSqlDate(DateTime date) {
		
		int vuosi = date.getYear();
		int kuukausi = date.getMonth()+1;
		int paiva = date.getDay();
		
		String sqlDateTime = vuosi+"-"+kuukausi+"-"+paiva;
		
		return sqlDateTime;
	}
	
	public DateTime createDateTime(DateTime date) {
		int paiva = date.getDay();
		int kuukausi = date.getMonth()+1;
		int vuosi = date.getYear();
		DateTime pvm = null;
		pvm.setDay(paiva);
		pvm.setMonth(kuukausi);
		pvm.setYear(vuosi);
		
		return pvm;
	}
	


	/**
	 * Create contents of the window.
	 * @wbp.parser.entryPoint
	 */
	protected void createContents() {
		muokkaaPalveluvarausShl = new Shell(SWT.CLOSE | SWT.MIN);
		muokkaaPalveluvarausShl.setSize(450, 519);
		
		
		// "Responsiivisuus - maaritelty tassa vain 1920x1080 resoluutiolle, pidemmalle kehitetyssa ohjelmassa pitaisi muillekin resoluutioille"
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		
		if (screenSize.getWidth() < 1600){
			muokkaaPalveluvarausShl.setSize((int)(screenSize.getWidth() / 3), (int) (screenSize.getHeight() / 1.95));
		}
		else if (screenSize.getWidth() > 1600){
			muokkaaPalveluvarausShl.setSize((int)(screenSize.getWidth() / 4.5), (int) (screenSize.getHeight() / 3));
		}
		else if (screenSize.getWidth() == 1600) {
			muokkaaPalveluvarausShl.setSize((int) (screenSize.getWidth() / 3.4), (int) (screenSize.getHeight() / 2.2));
		}
		
				muokkaaPalveluvarausShl.setText("Muokkaa varausta");
		muokkaaPalveluvarausShl.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Composite composite = new Composite(muokkaaPalveluvarausShl, SWT.NONE);
		composite.setLayout(new GridLayout(4, false));
		new Label(composite, SWT.NONE);
		
		// Otsikko
		muokkaaPalveluvarausLbl = new Label(composite, SWT.NONE);
		muokkaaPalveluvarausLbl.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 2, 2));
		muokkaaPalveluvarausLbl.setAlignment(SWT.CENTER);
		muokkaaPalveluvarausLbl.setText("Muokkaa alla varauksen tietoja");
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		
		
		// Toisen rivin label ja tekstikentta (asiakas id ja toimipiste id)
		asiakasID = new Label(composite, SWT.NONE);
		asiakasID.setText("Asiakas ID");
		
		palveluIdLbl = new Label(composite, SWT.NONE);
		palveluIdLbl.setText("Palvelu ID");
		
		label = new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		
		asiakasIdTxt = new Text(composite, SWT.BORDER);
		asiakasIdTxt.setEditable(false);
		asiakasIdTxt.addVerifyListener(new VerifyListener() {
			 @Override
			 public void verifyText(VerifyEvent e) {

	            Text text = (Text)e.getSource();

	            final String oldS = text.getText();
	            String newS = oldS.substring(0, e.start) + e.text + oldS.substring(e.end);

	            boolean isFloat = true;
	            try
	            {
	                Float.parseFloat(newS);
	            }
	            catch(NumberFormatException ex)
	            {
	                isFloat = false;
	            }

	            if(!isFloat)
	                e.doit = false;
	        }
		});
		asiakasIdTxt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		asiakasIdTxt.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				// TODO ASIAKASID KUUNTELIJA TOIMINNALLISUUS TARVITTAESSA
			}
		});
		asiakasIdTxt.setToolTipText("Asiakas ID");
		
		palveluIdTxt = new Text(composite, SWT.BORDER);
		palveluIdTxt.setEditable(false);
		palveluIdTxt.addVerifyListener(new VerifyListener() {
			 @Override
			 public void verifyText(VerifyEvent e) {

	            Text text = (Text)e.getSource();

	            final String oldS = text.getText();
	            String newS = oldS.substring(0, e.start) + e.text + oldS.substring(e.end);

	            boolean isFloat = true;
	            try
	            {
	                Float.parseFloat(newS);
	            }
	            catch(NumberFormatException ex)
	            {
	                isFloat = false;
	            }


	            if(!isFloat)
	                e.doit = false;
	        }
		});
		palveluIdTxt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		palveluIdTxt.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				// TODO MAJOITUS ID KUUNTELIJA TOIMINNALLISUUS TARVITTAESSA
			}
		});
		palveluIdTxt.setToolTipText("Palvelu ID");
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		
		valitseAsiakasBtn = new Button(composite, SWT.NONE);
		valitseAsiakasBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
	
				valitseAsiakasGui = new ValitseAsiakasGUI_muokkaa();
				if(asiakasIdTxt.getText() != "") {
					valitseAsiakasGui.setAsiakasIdTalteen(Integer.parseInt(asiakasIdTxt.getText()));
				}
				
				if(palveluIdTxt.getText() != "") {
					valitseAsiakasGui.setPalveluIdTalteen(Integer.parseInt(palveluIdTxt.getText()));
				}
				if (palveluvarausIdTxt.getText() != "") {
					valitseAsiakasGui.setPalveluvarausIdTalteen(Integer.parseInt(palveluvarausIdTxt.getText()));
				}
				if (aloituspvmDateTime.toString() != "") {
					valitseAsiakasGui.setAloituspvmPaivaTalteen(aloituspvmDateTime.getDay());
					valitseAsiakasGui.setAloituspvmKuukausiTalteen(aloituspvmDateTime.getMonth());
					valitseAsiakasGui.setAloituspvmVuosiTalteen(aloituspvmDateTime.getYear());
				}
				
				if (lopetuspvmDateTime.toString() !="") {
					valitseAsiakasGui.setLopetuspvmPaivaTalteen(lopetuspvmDateTime.getDay());
					valitseAsiakasGui.setLopetuspvmKuukausiTalteen(lopetuspvmDateTime.getMonth());
					valitseAsiakasGui.setLopetuspvmVuosiTalteen(lopetuspvmDateTime.getYear());;
				}
				valitseAsiakasGui.setMuokkaaPalveluvarausGui(getMuokkaaPalveluvarausGui());
				muokkaaPalveluvarausShl.close();
				try {
					valitseAsiakasGui.open();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		valitseAsiakasBtn.setText("Valitse asiakas");
		
		valitsePalveluBtn = new Button(composite, SWT.NONE);
		valitsePalveluBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				valitsePalveluGui = new ValitsePalveluGUI_muokkaa();
				if(asiakasIdTxt.getText() != "") {
					valitsePalveluGui.setAsiakasIdTalteen(Integer.parseInt(asiakasIdTxt.getText()));
				}
				if (palveluIdTxt.getText() !="") {
					valitsePalveluGui.setPalveluIdTalteen(Integer.parseInt(palveluIdTxt.getText()));
				}
				if (palveluvarausIdTxt.getText() != "") {
					valitsePalveluGui.setPalveluvarausIdTalteen(Integer.parseInt(palveluvarausIdTxt.getText()));
				}
				if (aloituspvmDateTime.toString() != "") {
					valitsePalveluGui.setAloituspvmPaivaTalteen(aloituspvmDateTime.getDay());
					valitsePalveluGui.setAloituspvmKuukausiTalteen(aloituspvmDateTime.getMonth());
					valitsePalveluGui.setAloituspvmVuosiTalteen(aloituspvmDateTime.getYear());
				}
				
				if (lopetuspvmDateTime.toString() !="") {
					valitsePalveluGui.setLopetuspvmPaivaTalteen(lopetuspvmDateTime.getDay());
					valitsePalveluGui.setLopetuspvmKuukausiTalteen(lopetuspvmDateTime.getMonth());
					valitsePalveluGui.setLopetuspvmVuosiTalteen(lopetuspvmDateTime.getYear());;
				}

				valitsePalveluGui.setMuokkaaPalveluvarausGui(getMuokkaaPalveluvarausGui());
				muokkaaPalveluvarausShl.close();
				try {
					valitsePalveluGui.open();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
		});
		valitsePalveluBtn.setText("Valitse palvelu");
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		
		
		// Ekan rivin label ja tekstikentta (palveluvaraus ja palveluid)
		palveluvarausIdLbl = new Label(composite, SWT.NONE);
		palveluvarausIdLbl.setText("Palveluvaraus ID");
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		
		palveluvarausIdTxt = new Text(composite, SWT.BORDER);
		palveluvarausIdTxt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		palveluvarausIdTxt.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				// TODO MAJOITUSVARAUS ID KUUNTELIJA TOIMINNALLISUUS TARVITTAESSA
			
			}
		});
		palveluvarausIdTxt.setToolTipText("Palvelu ID");
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		
		
		// Neljannen rivin label ja tekstikentta (aloituspvm ja lopetuspvm)
		aloituspvmLbl = new Label(composite, SWT.NONE);
		aloituspvmLbl.setText("Aloituspäivämäärä");
		
		lopetuspvmLbl = new Label(composite, SWT.NONE);
		lopetuspvmLbl.setText("Lopetuspäivämäärä");
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		
		aloituspvmDateTime = new DateTime(composite, SWT.BORDER);
		
		lopetuspvmDateTime = new DateTime(composite, SWT.BORDER);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		
		
		// Napit
		Button peruutaBtn = new Button(composite, SWT.NONE);
		peruutaBtn.setText("Peruuta");
		peruutaBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO PERUUTA NAPILLE KUUNTELIJA
				muokkaaPalveluvarausShl.close();
				palveluvarausEtusivuGui = getPalveluvarausEtusivuGui();
				try {
					palveluvarausEtusivuGui.open();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			
			}
		});
		
		Button palveluvarausMuokkaaBtn = new Button(composite, SWT.NONE);
		palveluvarausMuokkaaBtn.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		palveluvarausMuokkaaBtn.setText("Hyväksy");
		palveluvarausMuokkaaBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO HYVAKSY NAPILLE KUUNTELIJA
				try {
					muokkaaPalveluvaraus();
					palveluvarausEtusivuGui = getPalveluvarausEtusivuGui();
					muokkaaPalveluvarausShl.close();
					palveluvarausEtusivuGui.open();
					
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		new Label(composite, SWT.NONE);

	}

	public int getValittuAsiakasId() {
		return valittuAsiakasId;
	}

	public void setValittuAsiakasId(int valittuAsiakasId) {
		this.valittuAsiakasId = valittuAsiakasId;
	}

	public int getValittuPalveluId() {
		return valittuPalveluId;
	}

	public void setValittuPalveluId(int valittuPalveluId) {
		this.valittuPalveluId = valittuPalveluId;
	}

	public MuokkaaPalveluvarausGUI getMuokkaaPalveluvarausGui() {
		return muokkaaPalveluvarausGui;
	}

	public void setMuokkaaPalveluvarausGui(MuokkaaPalveluvarausGUI muokkaaPalveluvarausGui) {
		this.muokkaaPalveluvarausGui = muokkaaPalveluvarausGui;
	}

	public PalveluvarausEtusivuGUI getPalveluvarausEtusivuGui() {
		return palveluvarausEtusivuGui;
	}

	public void setPalveluvarausEtusivuGui(PalveluvarausEtusivuGUI palveluvarausEtusivuGui) {
		this.palveluvarausEtusivuGui = palveluvarausEtusivuGui;
	}

	public int getPalveluvarausIdTalteen() {
		return palveluvarausIdTalteen;
	}

	public void setPalveluvarausIdTalteen(int palveluvarausIdTalteen) {
		this.palveluvarausIdTalteen = palveluvarausIdTalteen;
	}

	public int getPalveluIdTalteen() {
		return palveluIdTalteen;
	}

	public void setPalveluIdTalteen(int palveluIdTalteen) {
		this.palveluIdTalteen = palveluIdTalteen;
	}

	public int getAsiakasIdTalteen() {
		return asiakasIdTalteen;
	}

	public void setAsiakasIdTalteen(int asiakasIdTalteen) {
		this.asiakasIdTalteen = asiakasIdTalteen;
	}

	
	public int getValittuPalveluvarausID() {
		return valittuPalveluvarausID;
	}

	public void setValittuPalveluvarausID(int valittuPalveluvarausID) {
		this.valittuPalveluvarausID = valittuPalveluvarausID;
	}
	public int getAloituspvmPaivaTalteen() {
		return aloituspvmPaivaTalteen;
	}
	public void setAloituspvmPaivaTalteen(int aloituspvmPaivaTalteen) {
		this.aloituspvmPaivaTalteen = aloituspvmPaivaTalteen;
	}
	public int getAloituspvmKuukausiTalteen() {
		return aloituspvmKuukausiTalteen;
	}
	public void setAloituspvmKuukausiTalteen(int aloituspvmKuukausiTalteen) {
		this.aloituspvmKuukausiTalteen = aloituspvmKuukausiTalteen;
	}
	public int getAloituspvmVuosiTalteen() {
		return aloituspvmVuosiTalteen;
	}
	public void setAloituspvmVuosiTalteen(int aloituspvmVuosiTalteen) {
		this.aloituspvmVuosiTalteen = aloituspvmVuosiTalteen;
	}
	public int getLopetuspvmPaivaTalteen() {
		return lopetuspvmPaivaTalteen;
	}
	public void setLopetuspvmPaivaTalteen(int lopetuspvmPaivaTalteen) {
		this.lopetuspvmPaivaTalteen = lopetuspvmPaivaTalteen;
	}
	public int getLopetuspvmKuukausiTalteen() {
		return lopetuspvmKuukausiTalteen;
	}
	public void setLopetuspvmKuukausiTalteen(int lopetuspvmKuukausiTalteen) {
		this.lopetuspvmKuukausiTalteen = lopetuspvmKuukausiTalteen;
	}
	public int getLopetuspvmVuosiTalteen() {
		return lopetuspvmVuosiTalteen;
	}
	public void setLopetuspvmVuosiTalteen(int lopetuspvmVuosiTalteen) {
		this.lopetuspvmVuosiTalteen = lopetuspvmVuosiTalteen;
	}

	
	
}
