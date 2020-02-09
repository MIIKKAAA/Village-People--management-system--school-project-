package toimipisteet;

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
import majoitus.ValitseToimipisteGUI;
import palvelu_varaus.ValitseAsiakasGUI;

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

public class LisaaToimipisteGUI {

	protected Shell lisaaToimipisteShl;
	private Text toimipisteIdTxt;
	private Text osoiteTxt;
	private Text nimiTxt;
	private Text paikkakuntaTxt;
	private Label lisaaToimipisteMainLbl;
	private Label toimipisteIdLbl;
	private Label nimiLbl;
	private Label paikkakuntaLbl;
	private Label label;
	private int valittuToimipisteID;
	private Connection conn;
	Toimipiste toimipiste = new Toimipiste();
	private ToimipisteEtusivuGUI toimipisteEtusivuGui;
	private Label osoiteLbl;
	private Label postinumeroLbl;
	private Text postinumeroTxt;
	private LisaaToimipisteGUI lisaaToimipisteGui;
	private int toimipisteIdTalteen;
	private int asiakasIdTalteen;
	private int valittuAsiakasId;
	private int valittuToimipisteId;
	private ValitseToimipisteGUI valitseToimipisteGui;


	/**
	 * Open the window.
	 * @throws Exception 
	 * @throws SQLException 
	 */
	public void open() throws SQLException, Exception {
		createContents();
		yhdista();
		// Avautuessa keskelle
		Display display = ApuMetodeja.centerWindow(lisaaToimipisteShl);
			
		if (getValittuToimipisteId() == 0 && getToimipisteIdTalteen() == 0) {
			toimipisteIdTxt.setText("");
		}
		else if (getToimipisteIdTalteen() > 0 && getValittuToimipisteId() == 0) {
			toimipisteIdTxt.setText(Integer.toString(getToimipisteIdTalteen()));
		}
		else if (getToimipisteIdTalteen() > 0 && getValittuToimipisteId() > 0) {
			toimipisteIdTxt.setText(Integer.toString(getValittuToimipisteId()));
		}
		else if (getToimipisteIdTalteen() == 0 && getValittuToimipisteId() > 0) {
			toimipisteIdTxt.setText(Integer.toString(getValittuToimipisteId()));
		}
		
		if (getToimipisteIdTalteen() > 0) {
			toimipisteIdTxt.setText(Integer.toString(getToimipisteIdTalteen()));
		}
		else {
			toimipisteIdTxt.setText("");
		}
		
		lisaaToimipisteShl.open();
		lisaaToimipisteShl.layout();
		while (!lisaaToimipisteShl.isDisposed()) {
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

public void lisaaToimipisteTietokantaan() throws SQLException, Exception {
		
		boolean toimipiste_lisatty = true;
		toimipiste = null;
		
		try {
			toimipiste = Toimipiste.haeToimipiste(Integer.parseInt(toimipisteIdTxt.getText()), conn);
		}catch (SQLException e) {
			toimipiste_lisatty = false;
			e.printStackTrace();
		} catch (Exception e) {
			toimipiste_lisatty = false;
			e.printStackTrace();
		}
		toimipiste.setNimi(nimiTxt.getText());
		toimipiste.setOsoite(osoiteTxt.getText());
		toimipiste.setPaikkakunta(paikkakuntaTxt.getText());
		toimipiste.setPostinumero(Integer.parseInt(postinumeroTxt.getText()));
		toimipiste.setToimipisteId(Integer.parseInt(toimipisteIdTxt.getText()));
		toimipiste.lisaaToimipiste(conn);
	}
	/**
	 * Create contents of the window.
	 * @wbp.parser.entryPoint
	 */
	protected void createContents() {
		lisaaToimipisteShl = new Shell(SWT.CLOSE | SWT.MIN);
		lisaaToimipisteShl.setSize(450, 354);
		
		
		// "Responsiivisuus - maaritelty tassa vain 1920x1080 resoluutiolle, pidemmalle kehitetyssa ohjelmassa pitaisi muillekin resoluutioille"
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		if (screenSize.getWidth() < 1600){
			lisaaToimipisteShl.setSize((int)(screenSize.getWidth() / 3), (int) (screenSize.getHeight() / 1.95));
		}
		else if (screenSize.getWidth() > 1600){
			lisaaToimipisteShl.setSize((int)(screenSize.getWidth() / 4.5), (int) (screenSize.getHeight() / 3));
		}
		else if (screenSize.getWidth() == 1600) {
			lisaaToimipisteShl.setSize((int) (screenSize.getWidth() / 3.4), (int) (screenSize.getHeight() / 2.2));
		}
		
				lisaaToimipisteShl.setText("Lisää toimipiste");
		lisaaToimipisteShl.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Composite composite = new Composite(lisaaToimipisteShl, SWT.NONE);
		composite.setLayout(new GridLayout(4, false));
		new Label(composite, SWT.NONE);
		
		// Otsikko
		lisaaToimipisteMainLbl = new Label(composite, SWT.NONE);
		lisaaToimipisteMainLbl.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 2, 2));
		lisaaToimipisteMainLbl.setAlignment(SWT.CENTER);
		lisaaToimipisteMainLbl.setText("Syötä alle toimipisteen tiedot");
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
		
		
		// Ekan rivin label ja tekstikentta (toimipiste ja toimipiste ID)
		toimipisteIdLbl = new Label(composite, SWT.NONE);
		toimipisteIdLbl.setText("Toimipiste ID");
		
		nimiLbl = new Label(composite, SWT.NONE);
		nimiLbl.setText("Nimi");
		
		label = new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		
		toimipisteIdTxt = new Text(composite, SWT.BORDER);
		toimipisteIdTxt.addVerifyListener(new VerifyListener() {
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

	            System.out.println(newS);

	            if(!isFloat)
	                e.doit = false;
	        }
		});
		toimipisteIdTxt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		toimipisteIdTxt.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				// TODO MAJOITUS VARAUS KUUNTELIJA TOIMINNALLISUUS TARVITTAESSA
				System.out.println("TOIMII");
			}
		});
		toimipisteIdTxt.setToolTipText("Toimipiste ID");
		
		nimiTxt = new Text(composite, SWT.BORDER);
		nimiTxt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		nimiTxt.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				// TODO MAJOITUS ID KUUNTELIJA TOIMINNALLISUUS TARVITTAESSA
			}
		});
		nimiTxt.setToolTipText("Nimi");
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		
		osoiteLbl = new Label(composite, SWT.NONE);
		osoiteLbl.setText("Osoite");
		
		
		// Toisen rivin label ja tekstikentta (asiakasid ja toimipiste id)
		paikkakuntaLbl = new Label(composite, SWT.NONE);
		paikkakuntaLbl.setText("Paikkakunta");
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		
		osoiteTxt = new Text(composite, SWT.BORDER);
		osoiteTxt.setToolTipText("Toimipiste ID");
		osoiteTxt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		paikkakuntaTxt = new Text(composite, SWT.BORDER);
		paikkakuntaTxt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		paikkakuntaTxt.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				// TODO ASIAKAS ID KUUNTELIJA TOIMINNALLISUUS TARVITTAESSA
			}
		});
		paikkakuntaTxt.setToolTipText("Paikkakunta");
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		
		postinumeroLbl = new Label(composite, SWT.NONE);
		postinumeroLbl.setText("Postinumero");
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		
		postinumeroTxt = new Text(composite, SWT.BORDER);
		postinumeroTxt.addVerifyListener(new VerifyListener() {
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
		postinumeroTxt.setToolTipText("Postinumero");
		postinumeroTxt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(composite, SWT.NONE);
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
				lisaaToimipisteShl.close();
				toimipisteEtusivuGui = new ToimipisteEtusivuGUI();
				try {
					toimipisteEtusivuGui.open();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		Button toimipisteLisaaBtn = new Button(composite, SWT.NONE);
		toimipisteLisaaBtn.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		toimipisteLisaaBtn.setText("Hyväksy");
		toimipisteLisaaBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO HYVAKSY NAPILLE KUUNTELIJA
				try {
					lisaaToimipisteTietokantaan();
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
				toimipisteEtusivuGui = getToimipisteEtusivuGui();
				try {
					toimipisteEtusivuGui.open();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				lisaaToimipisteShl.close();
			}
		});
		new Label(composite, SWT.NONE);
	}

	public int getValittuToimipisteID() {
		return valittuToimipisteID;
	}

	public void setValittuToimipisteID(int valittuToimipisteID) {
		this.valittuToimipisteID = valittuToimipisteID;
	}

	public LisaaToimipisteGUI getLisaaToimipisteGui() {
		return lisaaToimipisteGui;
	}

	public void setLisaaToimipisteGui(LisaaToimipisteGUI lisaaToimipisteGui) {
		this.lisaaToimipisteGui = lisaaToimipisteGui;
	}

	public ToimipisteEtusivuGUI getToimipisteEtusivuGui() {
		return toimipisteEtusivuGui;
	}

	public void setToimipisteEtusivuGui(ToimipisteEtusivuGUI toimipisteEtusivuGui) {
		this.toimipisteEtusivuGui = toimipisteEtusivuGui;
	}

	public int getToimipisteIdTalteen() {
		return toimipisteIdTalteen;
	}

	public void setToimipisteIdTalteen(int toimipisteIdTalteen) {
		this.toimipisteIdTalteen = toimipisteIdTalteen;
	}

	public int getAsiakasIdTalteen() {
		return asiakasIdTalteen;
	}

	public void setAsiakasIdTalteen(int asiakasIdTalteen) {
		this.asiakasIdTalteen = asiakasIdTalteen;
	}

	public int getValittuAsiakasId() {
		return valittuAsiakasId;
	}

	public void setValittuAsiakasId(int valittuAsiakasId) {
		this.valittuAsiakasId = valittuAsiakasId;
	}

	public int getValittuToimipisteId() {
		return valittuToimipisteId;
	}

	public void setValittuToimipisteId(int valittuToimipisteId) {
		this.valittuToimipisteId = valittuToimipisteId;
	}
	
	
	
}
