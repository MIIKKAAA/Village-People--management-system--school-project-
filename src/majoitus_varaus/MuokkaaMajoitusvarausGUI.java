package majoitus_varaus;

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

public class MuokkaaMajoitusvarausGUI {

	protected Shell muokkaaMajoitusvarausShl;
	private Text majoitusvarausIdTxt;
	private Text majoitusIdTxt;
	private Text asiakasIdTxt;
	private Label muokkaaMajoitusvarausLbl;
	private Label majoitusvarausIdLbl;
	private Label majoitusIdLbl;
	private Label asiakasID;
	private Label aloituspvmLbl;
	private Label lopetuspvmLbl;
	private Label label;
	private DateTime aloituspvmDateTime;
	private DateTime lopetuspvmDateTime;
	private Connection conn;
	Majoitusvaraus majoitusvaraus = new Majoitusvaraus();
	private int valittuMajoitusvarausID;
	private int valittuAsiakasId;
	private int valittuMajoitusId;
	private MajoitusvarausEtusivuGUI majoitusvarausEtusivuGui;
	private Button valitseMajoitusBtn;
	private Button valitseAsiakasBtn;
	private ValitseAsiakasGUI_majoitusvaraus_muokkaa valitseAsiakasGui;
	private ValitseMajoitusGUI_majoitusvaraus_muokkaa valitseMajoitusGui;
	private MuokkaaMajoitusvarausGUI muokkaaMajoitusvarausGui;
	private LisaaMajoitusvarausGUI lisaaMajoitusvarausGui;
	private int majoitusvarausIdTalteen = 0;
	private int majoitusIdTalteen = 0;
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

		luoMajoitusvarausTiedot();
		// Avautuessa keskelle
		Display display = ApuMetodeja.centerWindow(muokkaaMajoitusvarausShl);
		
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
			
		if (getValittuMajoitusId() == 0 && getMajoitusIdTalteen() == 0) {
			majoitusIdTxt.setText("");
		}
		else if (getMajoitusIdTalteen() > 0 && getValittuMajoitusId() == 0) {
			majoitusIdTxt.setText(Integer.toString(getMajoitusIdTalteen()));
		}
		else if (getMajoitusIdTalteen() > 0 && getValittuMajoitusId() > 0) {
			majoitusIdTxt.setText(Integer.toString(getValittuMajoitusId()));
		}
		else if (getMajoitusIdTalteen() == 0 && getValittuMajoitusId() > 0) {
			majoitusIdTxt.setText(Integer.toString(getValittuMajoitusId()));
		}
		
		if (getValittuMajoitusvarausID() == 0 && getMajoitusvarausIdTalteen() == 0) {
			majoitusvarausIdTxt.setText("");
		}
		else if (getMajoitusvarausIdTalteen() > 0 && getValittuMajoitusvarausID() == 0) {
			majoitusvarausIdTxt.setText(Integer.toString(getMajoitusvarausIdTalteen()));
		}
		else if (getMajoitusvarausIdTalteen() > 0 && getValittuMajoitusvarausID() > 0) {
			majoitusvarausIdTxt.setText(Integer.toString(getValittuMajoitusvarausID()));
		}
		else if (getMajoitusvarausIdTalteen() == 0 && getValittuMajoitusvarausID() > 0) {
			majoitusvarausIdTxt.setText(Integer.toString(getValittuMajoitusvarausID()));
		}
		
		muokkaaMajoitusvarausShl.open();
		muokkaaMajoitusvarausShl.layout();
		while (!muokkaaMajoitusvarausShl.isDisposed()) {
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
	
	
	private void luoMajoitusvarausTiedot() throws SQLException {
		String sql = "SELECT MajoitusvarausID, MajoitusID, AsiakasID, aloituspvm, lopetuspvm "+
					"FROM majoitusvaraus WHERE MajoitusvarausID = ?";
		ResultSet tulosjoukko = null;
		PreparedStatement lause = null;
		try {
			// luo PreparedStatement-olio sql-lauseelle
			lause = conn.prepareStatement(sql);
			
			lause.setInt(1, getValittuMajoitusvarausID()); // asetetaan where ehtoon (?) arvo
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
	
			majoitusvarausIdTxt.setText(Integer.toString(tulosjoukko.getInt("MajoitusvarausID")));
			asiakasIdTxt.setText(Integer.toString(tulosjoukko.getInt("AsiakasID")));
			majoitusIdTxt.setText(Integer.toString(tulosjoukko.getInt("MajoitusID")));
			aloituspvmDateTime.setDay(Integer.parseInt(getDayFromStringDate(tulosjoukko, "aloituspvm")));
			aloituspvmDateTime.setMonth(Integer.parseInt(getMonthFromStringDate(tulosjoukko, "aloituspvm"))-1);
			aloituspvmDateTime.setYear(Integer.parseInt(getYearFromStringDate(tulosjoukko, "aloituspvm")));
			lopetuspvmDateTime.setDay(Integer.parseInt(getDayFromStringDate(tulosjoukko, "lopetuspvm")));
			lopetuspvmDateTime.setMonth(Integer.parseInt(getMonthFromStringDate(tulosjoukko, "lopetuspvm"))-1);
			lopetuspvmDateTime.setYear(Integer.parseInt(getYearFromStringDate(tulosjoukko, "lopetuspvm")));
		}
	}
	
	public void muokkaaMajoitusvaraus() throws SQLException, Exception {
		boolean majoitusvaraus_lisatty = true;
		majoitusvaraus = null;
		
		if (majoitusvarausIdTxt.getText() == "") {
			System.out.println("Et syöttäny Majoitusvaraus ID:tä, yritä uudelleen!");
			muokkaaMajoitusvarausShl.close();
		}
		
		try {
			majoitusvaraus = Majoitusvaraus.haeMajoitusvaraus(Integer.parseInt(majoitusvarausIdTxt.getText()), conn);
		}catch (SQLException e) {
			majoitusvaraus_lisatty = false;
			e.printStackTrace();
		} catch (Exception e) {
			majoitusvaraus_lisatty = false;
			e.printStackTrace();
		}
		
		if (asiakasIdTxt.getText() == "") {
			System.out.println("Et syöttänyt Asiakas ID:tä, yritä uudelleen!!");
			muokkaaMajoitusvarausShl.close();
		}
		else {
			majoitusvaraus.setAsiakasId(Integer.parseInt(asiakasIdTxt.getText()));
		}
		if (majoitusIdTxt.getText() == "") {
			System.out.println("Et valinnut majoitusta, yritä uudelleen!");
			muokkaaMajoitusvarausShl.close();
		}
		else {
			majoitusvaraus.setMajoitusId(Integer.parseInt(majoitusIdTxt.getText()));
		}
		if (majoitusvarausIdTxt.getText() == "") {
			System.out.println("Et syöttänyt Majoitusvaraus ID:tä, yritä uudelleen!");
			muokkaaMajoitusvarausShl.close();
		}
		else {
			majoitusvaraus.setMajoitusvarausId(Integer.parseInt(majoitusvarausIdTxt.getText()));
		}

		majoitusvaraus.setAsiakasId(Integer.parseInt(asiakasIdTxt.getText()));
		majoitusvaraus.setMajoitusId(Integer.parseInt(majoitusIdTxt.getText()));
		majoitusvaraus.setMajoitusvarausId(Integer.parseInt(majoitusvarausIdTxt.getText()));
		majoitusvaraus.setLopetuspvm(dateTimeToSqlDate(lopetuspvmDateTime));
		majoitusvaraus.setAloituspvm(dateTimeToSqlDate(aloituspvmDateTime));
		
		majoitusvaraus.muokkaaMajoitusvaraus(conn);
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
		muokkaaMajoitusvarausShl = new Shell(SWT.CLOSE | SWT.MIN);
		muokkaaMajoitusvarausShl.setSize(450, 519);
		
		
		// "Responsiivisuus - maaritelty tassa vain 1920x1080 resoluutiolle, pidemmalle kehitetyssa ohjelmassa pitaisi muillekin resoluutioille"
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		
		if (screenSize.getWidth() < 1600){
			muokkaaMajoitusvarausShl.setSize((int)(screenSize.getWidth() / 3), (int) (screenSize.getHeight() / 1.95));
		}
		else if (screenSize.getWidth() > 1600){
			muokkaaMajoitusvarausShl.setSize((int)(screenSize.getWidth() / 4.5), (int) (screenSize.getHeight() / 3));
		}
		else if (screenSize.getWidth() == 1600) {
			muokkaaMajoitusvarausShl.setSize((int) (screenSize.getWidth() / 3.4), (int) (screenSize.getHeight() / 2.2));
		}
		
				muokkaaMajoitusvarausShl.setText("Muokkaa varausta");
		muokkaaMajoitusvarausShl.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Composite composite = new Composite(muokkaaMajoitusvarausShl, SWT.NONE);
		composite.setLayout(new GridLayout(4, false));
		new Label(composite, SWT.NONE);
		
		// Otsikko
		muokkaaMajoitusvarausLbl = new Label(composite, SWT.NONE);
		muokkaaMajoitusvarausLbl.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 2, 2));
		muokkaaMajoitusvarausLbl.setAlignment(SWT.CENTER);
		muokkaaMajoitusvarausLbl.setText("Muokkaa alla varauksen tietoja");
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
		
		majoitusIdLbl = new Label(composite, SWT.NONE);
		majoitusIdLbl.setText("Majoitus ID");
		
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
		
		majoitusIdTxt = new Text(composite, SWT.BORDER);
		majoitusIdTxt.setEditable(false);
		majoitusIdTxt.addVerifyListener(new VerifyListener() {
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
		majoitusIdTxt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		majoitusIdTxt.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				// TODO MAJOITUS ID KUUNTELIJA TOIMINNALLISUUS TARVITTAESSA
			}
		});
		majoitusIdTxt.setToolTipText("Majoitus ID");
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		
		valitseAsiakasBtn = new Button(composite, SWT.NONE);
		valitseAsiakasBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
	
				valitseAsiakasGui = new ValitseAsiakasGUI_majoitusvaraus_muokkaa();
				if(asiakasIdTxt.getText() != "") {
					valitseAsiakasGui.setAsiakasIdTalteen(Integer.parseInt(asiakasIdTxt.getText()));
				}
				
				if(majoitusIdTxt.getText() != "") {
					valitseAsiakasGui.setMajoitusIdTalteen(Integer.parseInt(majoitusIdTxt.getText()));
				}
				if (majoitusvarausIdTxt.getText() != "") {
					valitseAsiakasGui.setMajoitusvarausIdTalteen(Integer.parseInt(majoitusvarausIdTxt.getText()));
				}
				if (aloituspvmDateTime.toString() != "") {
					valitseAsiakasGui.setAloituspvmPaivaTalteen(aloituspvmDateTime.getDay());
					valitseAsiakasGui.setAloituspvmKuukausiTalteen(aloituspvmDateTime.getMonth());
					valitseAsiakasGui.setAloituspvmVuosiTalteen(aloituspvmDateTime.getYear());
				}
				
				if (lopetuspvmDateTime.toString() != "") {
					valitseAsiakasGui.setLopetuspvmPaivaTalteen(lopetuspvmDateTime.getDay());
					valitseAsiakasGui.setLopetuspvmKuukausiTalteen(lopetuspvmDateTime.getMonth());
					valitseAsiakasGui.setLopetuspvmVuosiTalteen(lopetuspvmDateTime.getYear());;
				}
				
				valitseAsiakasGui.setMuokkaaMajoitusvarausGui(getMuokkaaMajoitusvarausGui());
				muokkaaMajoitusvarausShl.close();
				try {
					valitseAsiakasGui.open();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
		});
		valitseAsiakasBtn.setText("Valitse asiakas");
		
		valitseMajoitusBtn = new Button(composite, SWT.NONE);
		valitseMajoitusBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				valitseMajoitusGui = new ValitseMajoitusGUI_majoitusvaraus_muokkaa();
				if(asiakasIdTxt.getText() != "") {
					valitseMajoitusGui.setAsiakasIdTalteen(Integer.parseInt(asiakasIdTxt.getText()));
				}
				if (majoitusIdTxt.getText() !="") {
					valitseMajoitusGui.setMajoitusIdTalteen(Integer.parseInt(majoitusIdTxt.getText()));
				}
				if (majoitusvarausIdTxt.getText() != "") {
					valitseMajoitusGui.setMajoitusvarausIdTalteen(Integer.parseInt(majoitusvarausIdTxt.getText()));
				}
				if (aloituspvmDateTime.toString() != "") {
					valitseMajoitusGui.setAloituspvmPaivaTalteen(aloituspvmDateTime.getDay());
					valitseMajoitusGui.setAloituspvmKuukausiTalteen(aloituspvmDateTime.getMonth());
					valitseMajoitusGui.setAloituspvmVuosiTalteen(aloituspvmDateTime.getYear());
				}
				
				if (lopetuspvmDateTime.toString() !="") {
					valitseMajoitusGui.setLopetuspvmPaivaTalteen(lopetuspvmDateTime.getDay());
					valitseMajoitusGui.setLopetuspvmKuukausiTalteen(lopetuspvmDateTime.getMonth());
					valitseMajoitusGui.setLopetuspvmVuosiTalteen(lopetuspvmDateTime.getYear());;
				}
				valitseMajoitusGui.setMuokkaaMajoitusvarausGui(getMuokkaaMajoitusvarausGui());
				muokkaaMajoitusvarausShl.close();
				try {
					valitseMajoitusGui.open();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
		});
		valitseMajoitusBtn.setText("Valitse majoitus");
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		
		
		// Ekan rivin label ja tekstikentta (majoitusvaraus ja majoitusid)
		majoitusvarausIdLbl = new Label(composite, SWT.NONE);
		majoitusvarausIdLbl.setText("Majoitusvaraus ID");
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		
		majoitusvarausIdTxt = new Text(composite, SWT.BORDER);
		majoitusvarausIdTxt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		majoitusvarausIdTxt.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				// TODO MAJOITUSVARAUS ID KUUNTELIJA TOIMINNALLISUUS TARVITTAESSA
			
			}
		});
		majoitusvarausIdTxt.setToolTipText("Majoitus ID");
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
				muokkaaMajoitusvarausShl.close();
				majoitusvarausEtusivuGui = getMajoitusvarausEtusivuGui();
				try {
					majoitusvarausEtusivuGui.open();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			
			}
		});
		
		Button majoitusvarausMuokkaaBtn = new Button(composite, SWT.NONE);
		majoitusvarausMuokkaaBtn.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		majoitusvarausMuokkaaBtn.setText("Hyväksy");
		majoitusvarausMuokkaaBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO HYVAKSY NAPILLE KUUNTELIJA
				try {
					muokkaaMajoitusvaraus();
					majoitusvarausEtusivuGui = getMajoitusvarausEtusivuGui();
					muokkaaMajoitusvarausShl.close();
					majoitusvarausEtusivuGui.open();
					
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

	public int getValittuMajoitusId() {
		return valittuMajoitusId;
	}

	public void setValittuMajoitusId(int valittuMajoitusId) {
		this.valittuMajoitusId = valittuMajoitusId;
	}

	public MuokkaaMajoitusvarausGUI getMuokkaaMajoitusvarausGui() {
		return muokkaaMajoitusvarausGui;
	}

	public void setMuokkaaMajoitusvarausGui(MuokkaaMajoitusvarausGUI muokkaaMajoitusvarausGui) {
		this.muokkaaMajoitusvarausGui = muokkaaMajoitusvarausGui;
	}

	public MajoitusvarausEtusivuGUI getMajoitusvarausEtusivuGui() {
		return majoitusvarausEtusivuGui;
	}

	public void setMajoitusvarausEtusivuGui(MajoitusvarausEtusivuGUI majoitusvarausEtusivuGui) {
		this.majoitusvarausEtusivuGui = majoitusvarausEtusivuGui;
	}

	public int getMajoitusvarausIdTalteen() {
		return majoitusvarausIdTalteen;
	}

	public void setMajoitusvarausIdTalteen(int majoitusvarausIdTalteen) {
		this.majoitusvarausIdTalteen = majoitusvarausIdTalteen;
	}

	public int getMajoitusIdTalteen() {
		return majoitusIdTalteen;
	}

	public void setMajoitusIdTalteen(int majoitusIdTalteen) {
		this.majoitusIdTalteen = majoitusIdTalteen;
	}

	public int getAsiakasIdTalteen() {
		return asiakasIdTalteen;
	}

	public void setAsiakasIdTalteen(int asiakasIdTalteen) {
		this.asiakasIdTalteen = asiakasIdTalteen;
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
	public int getValittuMajoitusvarausID() {
		return valittuMajoitusvarausID;
	}

	public void setValittuMajoitusvarausID(int valittuMajoitusvarausID) {
		this.valittuMajoitusvarausID = valittuMajoitusvarausID;
	}
	
	
}
