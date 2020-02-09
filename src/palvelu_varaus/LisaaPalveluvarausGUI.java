package palvelu_varaus;

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
import java.sql.SQLIntegrityConstraintViolationException;

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

public class LisaaPalveluvarausGUI {

	protected Shell lisaaPalveluvarausShl;
	private Text palveluvarausIdTxt;
	private Text palveluIdTxt;
	private Text asiakasIdTxt;
	private Label lisaaPalveluvarausMainLbl;
	private Label palveluvarausIdLbl;
	private Label palveluIdLbl;
	private Label asiakasID;
	private Label aloituspvmLbl;
	private Label lopetuspvmLbl;
	private Label label;
	private DateTime aloituspvmDateTime;
	private DateTime lopetuspvmDateTime;
	private int valittuPalveluvarausID;
	private Connection conn;
	Palveluvaraus palveluvaraus = new Palveluvaraus();
	private PalveluvarausEtusivuGUI palveluvarausEtusivuGui;
	private int valittuAsiakasId;
	private int valittuPalveluId;
	private LisaaPalveluvarausGUI lisaaPalveluvarausGui;
	private Button valitsePalveluBtn;
	private Button valitseAsiakasBtn;
	private ValitseAsiakasGUI valitseAsiakasGui;
	private ValitsePalveluGUI valitsePalveluGui;
	private int palveluvarausIdTalteen = 0;
	private int palveluIdTalteen = 0;
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
		Display display = ApuMetodeja.centerWindow(lisaaPalveluvarausShl);

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
		
		if (getPalveluvarausIdTalteen() > 0) {
			palveluvarausIdTxt.setText(Integer.toString(getPalveluvarausIdTalteen()));
		}
		else {
			palveluvarausIdTxt.setText("");
		}
		
		lisaaPalveluvarausShl.open();
		lisaaPalveluvarausShl.layout();
		while (!lisaaPalveluvarausShl.isDisposed()) {
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

public void lisaaPalveluvarausTietokantaan() throws SQLException, SQLIntegrityConstraintViolationException, Exception {
		
		boolean palveluvaraus_lisatty = true;
		palveluvaraus = null;
		if (palveluvarausIdTxt.getText() == "") {
			System.out.println("Et syöttäny Palveluvaraus ID:tä, yritä uudelleen!");
			lisaaPalveluvarausShl.close();
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

		palveluvaraus.setAloituspvm(dateTimeToSqlDate(aloituspvmDateTime));
		palveluvaraus.setLopetuspvm(dateTimeToSqlDate(lopetuspvmDateTime));
		if (asiakasIdTxt.getText() == "") {
			System.out.println("Et syöttänyt Asiakas ID:tä, yritä uudelleen!!");
			lisaaPalveluvarausShl.close();
		}
		else {
			palveluvaraus.setAsiakasId(Integer.parseInt(asiakasIdTxt.getText()));
		}
		if (palveluIdTxt.getText() == "") {
			System.out.println("Et valinnut palvelua, yritä uudelleen!");
			lisaaPalveluvarausShl.close();
		}
		else {
			palveluvaraus.setPalveluId(Integer.parseInt(palveluIdTxt.getText()));
		}
		if (palveluvarausIdTxt.getText() == "") {
			System.out.println("Et syöttänyt Palveluvaraus ID:tä, yritä uudelleen!");
			lisaaPalveluvarausShl.close();
		}
		else {
			palveluvaraus.setPalveluvarausId(Integer.parseInt(palveluvarausIdTxt.getText()));
		}
		palveluvaraus.lisaaPalveluvaraus(conn);
	}
	/**
	 * Create contents of the window.
	 * @wbp.parser.entryPoint
	 */
	protected void createContents() {
		lisaaPalveluvarausShl = new Shell(SWT.CLOSE | SWT.MIN);
		
		
		// "Responsiivisuus - maaritelty tassa vain 1920x1080 resoluutiolle, pidemmalle kehitetyssa ohjelmassa pitaisi muillekin resoluutioille"
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		if (screenSize.getWidth() < 1600){
			lisaaPalveluvarausShl.setSize((int)(screenSize.getWidth() / 3), (int) (screenSize.getHeight() / 1.95));
		}
		else if (screenSize.getWidth() > 1600){
			lisaaPalveluvarausShl.setSize((int)(screenSize.getWidth() / 4.5), (int) (screenSize.getHeight() / 3));
		}
		else if (screenSize.getWidth() == 1600) {
			lisaaPalveluvarausShl.setSize((int) (screenSize.getWidth() / 3.4), (int) (screenSize.getHeight() / 2.2));
		}
		
				lisaaPalveluvarausShl.setText("Lisää varaus");
		lisaaPalveluvarausShl.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Composite composite = new Composite(lisaaPalveluvarausShl, SWT.NONE);
		composite.setLayout(new GridLayout(4, false));
		new Label(composite, SWT.NONE);
		
		// Otsikko
		lisaaPalveluvarausMainLbl = new Label(composite, SWT.NONE);
		lisaaPalveluvarausMainLbl.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 2, 2));
		lisaaPalveluvarausMainLbl.setAlignment(SWT.CENTER);
		lisaaPalveluvarausMainLbl.setText("Syötä alle palveluvarauksen tiedot");
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
		
		palveluIdLbl = new Label(composite, SWT.NONE);
		palveluIdLbl.setText("Palvelu ID");
		
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
		
		palveluIdTxt = new Text(composite, SWT.BORDER);
		palveluIdTxt.setEditable(false);
		palveluIdTxt.addVerifyListener(new VerifyListener() {
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
		palveluIdTxt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		palveluIdTxt.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				// TODO PALVELU ID KUUNTELIJA TOIMINNALLISUUS TARVITTAESSA
			}
		});
		palveluIdTxt.setToolTipText("Palvelu ID");
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		
		valitseAsiakasBtn = new Button(composite, SWT.NONE);
		valitseAsiakasBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
	
				valitseAsiakasGui = new ValitseAsiakasGUI();
				if(asiakasIdTxt.getText() != "") {
					valitseAsiakasGui.setAsiakasIdTalteen(Integer.parseInt(asiakasIdTxt.getText()));
				}
				
				if(palveluIdTxt.getText() != "") {
					valitseAsiakasGui.setPalveluIdTalteen(Integer.parseInt(palveluIdTxt.getText()));
				}
				if (palveluvarausIdTxt.getText() != "") {
					valitseAsiakasGui.setPalveluvarausIdTalteen(Integer.parseInt(palveluvarausIdTxt.getText()));
				}
				valitseAsiakasGui.setLisaaPalveluvarausGui(getLisaaPalveluvarausGui());
				lisaaPalveluvarausShl.close();
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
	
				valitsePalveluGui = new ValitsePalveluGUI();
				if(asiakasIdTxt.getText() != "") {
					valitsePalveluGui.setAsiakasIdTalteen(Integer.parseInt(asiakasIdTxt.getText()));
				}
				if (palveluIdTxt.getText() !="") {
					valitsePalveluGui.setPalveluIdTalteen(Integer.parseInt(palveluIdTxt.getText()));
				}
				if (palveluvarausIdTxt.getText() != "") {
					valitsePalveluGui.setPalveluvarausIdTalteen(Integer.parseInt(palveluvarausIdTxt.getText()));
				}
				valitsePalveluGui.setLisaaPalveluvarausGui(getLisaaPalveluvarausGui());
				lisaaPalveluvarausShl.close();
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
		
		
		// Ekan rivin label ja tekstikentta (palveluvaraus ja palvelu ID)
		palveluvarausIdLbl = new Label(composite, SWT.NONE);
		palveluvarausIdLbl.setText("Palveluvaraus ID");
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		
		palveluvarausIdTxt = new Text(composite, SWT.BORDER);
		palveluvarausIdTxt.addVerifyListener(new VerifyListener() {
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
		palveluvarausIdTxt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		palveluvarausIdTxt.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				// TODO PALVELU VARAUS KUUNTELIJA TOIMINNALLISUUS TARVITTAESSA
			}
		});
		palveluvarausIdTxt.setToolTipText("Palveluvaraus ID");
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
		// KUUNTELIJA TARVITTAESSA
		
		
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
				lisaaPalveluvarausShl.close();
				palveluvarausEtusivuGui = getPalveluvarausEtusivuGui();
				try {
					palveluvarausEtusivuGui.open();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		Button palveluvarausLisaaBtn = new Button(composite, SWT.NONE);
		palveluvarausLisaaBtn.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		palveluvarausLisaaBtn.setText("Hyväksy");
		palveluvarausLisaaBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO HYVAKSY NAPILLE KUUNTELIJA
				try {
					lisaaPalveluvarausTietokantaan();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					if (e1 instanceof SQLIntegrityConstraintViolationException) {
						e1.printStackTrace();
						return;
					}
					else {
						e1.printStackTrace();
					}
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				palveluvarausEtusivuGui = getPalveluvarausEtusivuGui();
				try {
					palveluvarausEtusivuGui.open();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				lisaaPalveluvarausShl.close();
			}
		});
		new Label(composite, SWT.NONE);
	}

	public int getValittuPalveluvarausID() {
		return valittuPalveluvarausID;
	}

	public void setValittuPalveluvarausID(int valittuPalveluvarausID) {
		this.valittuPalveluvarausID = valittuPalveluvarausID;
	}

	public LisaaPalveluvarausGUI getLisaaPalveluvarausGui() {
		return lisaaPalveluvarausGui;
	}

	public void setLisaaPalveluvarausGui(LisaaPalveluvarausGUI lisaaPalveluvarausGui) {
		this.lisaaPalveluvarausGui = lisaaPalveluvarausGui;
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
	
	
	
}
