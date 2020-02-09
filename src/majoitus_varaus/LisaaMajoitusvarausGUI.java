package majoitus_varaus;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
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

public class LisaaMajoitusvarausGUI {

	protected Shell lisaaMajoitusvarausShl;
	private Text majoitusvarausIdTxt;
	private Text majoitusIdTxt;
	private Text asiakasIdTxt;
	private Label lisaaMajoitusvarausMainLbl;
	private Label majoitusvarausIdLbl;
	private Label majoitusIdLbl;
	private Label asiakasID;
	private Label aloituspvmLbl;
	private Label lopetuspvmLbl;
	private Label label;
	private DateTime aloituspvmDateTime;
	private DateTime lopetuspvmDateTime;
	private int valittuMajoitusvarausID;
	private Connection conn;
	Majoitusvaraus majoitusvaraus = new Majoitusvaraus();
	private MajoitusvarausEtusivuGUI majoitusvarausEtusivuGui;
	private int valittuAsiakasId;
	private int valittuMajoitusId;
	private Button valitseAsiakasBtn;
	private ValitseAsiakasGUI_majoitusvaraus_lisaa valitseAsiakasGui;
	private ValitseMajoitusGUI_majoitusvaraus_lisaa valitseMajoitusGui;
	private Button valitseMajoitusBtn;
	private LisaaMajoitusvarausGUI lisaaMajoitusvarausGui;
	private int majoitusvarausIdTalteen = 0;
	private int majoitusIdTalteen = 0;
	private int asiakasIdTalteen = 0;
	private DateTime aloituspvmTalteen;
	private DateTime lopetuspvmTalteen;
	
	/**
	 * Open the window.
	 * @throws Exception 
	 * @throws SQLException 
	 */
	public void open() throws SQLException, Exception {
		createContents();
		yhdista();
		// Avautuessa keskelle
		Display display = ApuMetodeja.centerWindow(lisaaMajoitusvarausShl);

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
		
		if (getMajoitusvarausIdTalteen() > 0) {
			majoitusvarausIdTxt.setText(Integer.toString(getMajoitusvarausIdTalteen()));
		}
		else {
			majoitusvarausIdTxt.setText("");
		}
		
		lisaaMajoitusvarausShl.open();
		lisaaMajoitusvarausShl.layout();
		while (!lisaaMajoitusvarausShl.isDisposed()) {
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
	
	public String dateTimeToSqlDate(DateTime date) {
		
		int vuosi = date.getYear();
		int kuukausi = date.getMonth()+1;
		int paiva = date.getDay();
		
		String sqlDateTime = vuosi+"-"+kuukausi+"-"+paiva;
		
		return sqlDateTime;
	}

public void lisaaMajoitusvarausTietokantaan() throws SQLException, Exception {
		
		boolean majoitusvaraus_lisatty = true;
		majoitusvaraus = null;
		if (majoitusvarausIdTxt.getText() == "") {
			System.out.println("Et syöttäny Majoitusvaraus ID:tä, yritä uudelleen!");
			lisaaMajoitusvarausShl.close();
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
			lisaaMajoitusvarausShl.close();
		}
		else {
			majoitusvaraus.setAsiakasId(Integer.parseInt(asiakasIdTxt.getText()));
		}
		if (majoitusIdTxt.getText() == "") {
			System.out.println("Et valinnut majoitusta, yritä uudelleen!");
			lisaaMajoitusvarausShl.close();
		}
		else {
			majoitusvaraus.setMajoitusId(Integer.parseInt(majoitusIdTxt.getText()));
		}
		if (majoitusvarausIdTxt.getText() == "") {
			System.out.println("Et syöttänyt Majoitusvaraus ID:tä, yritä uudelleen!");
			lisaaMajoitusvarausShl.close();
		}
		else {
			majoitusvaraus.setMajoitusvarausId(Integer.parseInt(majoitusvarausIdTxt.getText()));
		}
		majoitusvaraus.setAloituspvm(dateTimeToSqlDate(aloituspvmDateTime));
		majoitusvaraus.setLopetuspvm(dateTimeToSqlDate(lopetuspvmDateTime));
		majoitusvaraus.setAsiakasId(Integer.parseInt(asiakasIdTxt.getText()));
		majoitusvaraus.setMajoitusId(Integer.parseInt(majoitusIdTxt.getText()));
		majoitusvaraus.setMajoitusvarausId(Integer.parseInt(majoitusvarausIdTxt.getText()));
		majoitusvaraus.lisaaMajoitusvaraus(conn);
	}
	/**
	 * Create contents of the window.
	 * @wbp.parser.entryPoint
	 */
	protected void createContents() {
		lisaaMajoitusvarausShl = new Shell(SWT.CLOSE | SWT.MIN);
		lisaaMajoitusvarausShl.setSize(469, 557);
		
		
		// "Responsiivisuus - maaritelty tassa vain 1920x1080 resoluutiolle, pidemmalle kehitetyssa ohjelmassa pitaisi muillekin resoluutioille"
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		if (screenSize.getWidth() < 1600){
			lisaaMajoitusvarausShl.setSize((int)(screenSize.getWidth() / 3), (int) (screenSize.getHeight() / 1.95));
		}
		else if (screenSize.getWidth() > 1600){
			lisaaMajoitusvarausShl.setSize((int)(screenSize.getWidth() / 4.5), (int) (screenSize.getHeight() / 3));
		}
		else if (screenSize.getWidth() == 1600) {
			lisaaMajoitusvarausShl.setSize((int) (screenSize.getWidth() / 3.4), (int) (screenSize.getHeight() / 2.2));
		}
		
				lisaaMajoitusvarausShl.setText("Lisää varaus");
		lisaaMajoitusvarausShl.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Composite composite = new Composite(lisaaMajoitusvarausShl, SWT.NONE);
		composite.setLayout(new GridLayout(4, false));
		new Label(composite, SWT.NONE);
		
		// Otsikko
		lisaaMajoitusvarausMainLbl = new Label(composite, SWT.NONE);
		lisaaMajoitusvarausMainLbl.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 2, 2));
		lisaaMajoitusvarausMainLbl.setAlignment(SWT.CENTER);
		lisaaMajoitusvarausMainLbl.setText("Syötä alle majoitusvarauksen tiedot");
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
		
		
		// Toisen rivin label ja tekstikentta (asiakasid ja toimipiste id)
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
		asiakasIdTxt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		asiakasIdTxt.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				// TODO ASIAKAS ID KUUNTELIJA TOIMINNALLISUUS TARVITTAESSA
			}
		});
		asiakasIdTxt.setToolTipText("Asiakas ID");
		
		majoitusIdTxt = new Text(composite, SWT.BORDER);
		majoitusIdTxt.setEditable(false);
		majoitusIdTxt.addVerifyListener(new VerifyListener() {
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
	
				valitseAsiakasGui = new ValitseAsiakasGUI_majoitusvaraus_lisaa();
				if(asiakasIdTxt.getText() != "") {
					valitseAsiakasGui.setAsiakasIdTalteen(Integer.parseInt(asiakasIdTxt.getText()));
				}
				
				if(majoitusIdTxt.getText() != "") {
					valitseAsiakasGui.setMajoitusIdTalteen(Integer.parseInt(majoitusIdTxt.getText()));
				}
				if (majoitusvarausIdTxt.getText() != "") {
					valitseAsiakasGui.setMajoitusvarausIdTalteen(Integer.parseInt(majoitusvarausIdTxt.getText()));
				}
				valitseAsiakasGui.setLisaaMajoitusvarausGui(getLisaaMajoitusvarausGui());
				lisaaMajoitusvarausShl.close();
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
				
				valitseMajoitusGui = new ValitseMajoitusGUI_majoitusvaraus_lisaa();
				if(asiakasIdTxt.getText() != "") {
					valitseMajoitusGui.setAsiakasIdTalteen(Integer.parseInt(asiakasIdTxt.getText()));
				}
				if (majoitusIdTxt.getText() !="") {
					valitseMajoitusGui.setMajoitusIdTalteen(Integer.parseInt(majoitusIdTxt.getText()));
				}
				if (majoitusvarausIdTxt.getText() != "") {
					valitseMajoitusGui.setMajoitusvarausIdTalteen(Integer.parseInt(majoitusvarausIdTxt.getText()));
				}

				valitseMajoitusGui.setLisaaMajoitusvarausGui(getLisaaMajoitusvarausGui());
				lisaaMajoitusvarausShl.close();
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
		
		
		// Ekan rivin label ja tekstikentta (majoitusvaraus ja majoitus ID)
		majoitusvarausIdLbl = new Label(composite, SWT.NONE);
		majoitusvarausIdLbl.setText("Majoitusvaraus ID");
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		
		majoitusvarausIdTxt = new Text(composite, SWT.BORDER);
		majoitusvarausIdTxt.addVerifyListener(new VerifyListener() {
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
		majoitusvarausIdTxt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		majoitusvarausIdTxt.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				// TODO MAJOITUS VARAUS KUUNTELIJA TOIMINNALLISUUS TARVITTAESSA

			}
		});
		majoitusvarausIdTxt.setToolTipText("Majoitusvaraus ID");
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
		// KUUNTELIJA TARVITTAESSA
		
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
				lisaaMajoitusvarausShl.close();
				majoitusvarausEtusivuGui = getMajoitusvarausEtusivuGui();
				try {
					majoitusvarausEtusivuGui.open();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		Button majoitusvarausLisaaBtn = new Button(composite, SWT.NONE);
		majoitusvarausLisaaBtn.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		majoitusvarausLisaaBtn.setText("Hyväksy");
		majoitusvarausLisaaBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO HYVAKSY NAPILLE KUUNTELIJA
				try {
					lisaaMajoitusvarausTietokantaan();
					majoitusvarausEtusivuGui = getMajoitusvarausEtusivuGui();
					lisaaMajoitusvarausShl.close();
					majoitusvarausEtusivuGui.open();

				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		// KUUNTELIJA TARVITTAESSA
		
		
		new Label(composite, SWT.NONE);
	}

	public int getValittuMajoitusvarausID() {
		return valittuMajoitusvarausID;
	}

	public void setValittuMajoitusvarausID(int valittuMajoitusvarausID) {
		this.valittuMajoitusvarausID = valittuMajoitusvarausID;
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

	public LisaaMajoitusvarausGUI getLisaaMajoitusvarausGui() {
		return lisaaMajoitusvarausGui;
	}

	public void setLisaaMajoitusvarausGui(LisaaMajoitusvarausGUI lisaaMajoitusvarausGui) {
		this.lisaaMajoitusvarausGui = lisaaMajoitusvarausGui;
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

	public DateTime getAloituspvmTalteen() {
		return aloituspvmTalteen;
	}

	public void setAloituspvmTalteen(DateTime aloituspvmTalteen) {
		this.aloituspvmTalteen = aloituspvmTalteen;
	}

	public DateTime getLopetuspvmTalteen() {
		return lopetuspvmTalteen;
	}

	public void setLopetuspvmTalteen(DateTime lopetuspvmTalteen) {
		this.lopetuspvmTalteen = lopetuspvmTalteen;
	}

	public MajoitusvarausEtusivuGUI getMajoitusvarausEtusivuGui() {
		return majoitusvarausEtusivuGui;
	}

	public void setMajoitusvarausEtusivuGui(MajoitusvarausEtusivuGUI majoitusvarausEtusivuGui) {
		this.majoitusvarausEtusivuGui = majoitusvarausEtusivuGui;
	}
	
	
	
}
