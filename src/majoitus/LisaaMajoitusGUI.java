package majoitus;

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
import laskutus.Lasku;
import majoitus_varaus.ValitseAsiakasGUI_majoitusvaraus_lisaa;

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

public class LisaaMajoitusGUI {

	protected Shell lisaaMajoitusShl;
	private Text majoitusIdTxt;
	private Text nimiTxt;
	private Text hintaTxt;
	private Label lisaaMajoitusMainLbl;
	private Label majoitusIdLbl;
	private Label nimiLbl;
	private Label hintaLbl;
	private Label label;
	private Connection conn;
	Majoitus majoitus = new Majoitus();
	private MajoitusEtusivuGUI majoitusEtusivuGui;
	private Label toimipisteIdLbl;
	private Text toimipisteIdTxt;
	private Button valitseToimipisteBtn;
	private ValitseToimipisteGUI valitseToimipisteGui;
	private int majoitusIdTalteen;
	private int toimipisteIdTalteen;
	private int hintaTalteen;
	private String nimiTalteen = "";
	private LisaaMajoitusGUI lisaaMajoitusGui;
	private int valittuToimipisteId;

	/**
	 * Open the window.
	 * @throws Exception 
	 * @throws SQLException 
	 */
	public void open() throws SQLException, Exception {
		createContents();
		yhdista();

		// Avautuessa keskelle
		Display display = ApuMetodeja.centerWindow(lisaaMajoitusShl);
		
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
		
		if (getHintaTalteen() > 0) {
			hintaTxt.setText(Integer.toString(getHintaTalteen()));
		}
		if (getNimiTalteen() != "") {
			nimiTxt.setText(getNimiTalteen());
		}
		if (getMajoitusIdTalteen() > 0) {
			majoitusIdTxt.setText(Integer.toString(getMajoitusIdTalteen()));
		}

		lisaaMajoitusShl.open();
		lisaaMajoitusShl.layout();
		while (!lisaaMajoitusShl.isDisposed()) {
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

public void lisaaMajoitusTietokantaan() throws SQLException, Exception {
		
		boolean majoitus_lisatty = true;
		majoitus = null;
		
		try {
			majoitus = Majoitus.haeMajoitus(Integer.parseInt(majoitusIdTxt.getText()), conn);
		}catch (SQLException e) {
			majoitus_lisatty = false;
			e.printStackTrace();
		} catch (Exception e) {
			majoitus_lisatty = false;
			e.printStackTrace();
		}
		
		majoitus.setMajoitusId(Integer.parseInt(majoitusIdTxt.getText()));
		majoitus.setHinta(Integer.parseInt(hintaTxt.getText()));
		majoitus.setNimi(nimiTxt.getText());
		majoitus.setToimipisteId(Integer.parseInt(toimipisteIdTxt.getText()));
		majoitus.lisaaMajoitus(conn);
	}
	/**
	 * Create contents of the window.
	 * @wbp.parser.entryPoint
	 */
	protected void createContents() {
		lisaaMajoitusShl = new Shell(SWT.CLOSE | SWT.MIN);
		
		
		// "Responsiivisuus - maaritelty tassa vain 1920x1080 resoluutiolle, pidemmalle kehitetyssa ohjelmassa pitaisi muillekin resoluutioille"
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		if (screenSize.getWidth() < 1600){
			lisaaMajoitusShl.setSize((int)(screenSize.getWidth() / 3), (int) (screenSize.getHeight() / 1.95));
		}
		else if (screenSize.getWidth() > 1600){
			lisaaMajoitusShl.setSize((int)(screenSize.getWidth() / 4.5), (int) (screenSize.getHeight() / 3.3));
		}
		else if (screenSize.getWidth() == 1600) {
			lisaaMajoitusShl.setSize((int) (screenSize.getWidth() / 3.4), (int) (screenSize.getHeight() / 2.2));
		}
		
				lisaaMajoitusShl.setText("Lisää majoitus");
		lisaaMajoitusShl.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Composite composite = new Composite(lisaaMajoitusShl, SWT.NONE);
		composite.setLayout(new GridLayout(4, false));
		new Label(composite, SWT.NONE);
		
		// Otsikko
		lisaaMajoitusMainLbl = new Label(composite, SWT.NONE);
		lisaaMajoitusMainLbl.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 2, 2));
		lisaaMajoitusMainLbl.setAlignment(SWT.CENTER);
		lisaaMajoitusMainLbl.setText("Syötä alle majoituksen tiedot");
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
		
		
		// Ekan rivin label ja tekstikentta (majoitus ja majoitus ID)
		majoitusIdLbl = new Label(composite, SWT.NONE);
		majoitusIdLbl.setText("Majoitus ID");
		
		nimiLbl = new Label(composite, SWT.NONE);
		nimiLbl.setText("Nimi");
		
		label = new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		
		majoitusIdTxt = new Text(composite, SWT.BORDER);
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
				// TODO MAJOITUS VARAUS KUUNTELIJA TOIMINNALLISUUS TARVITTAESSA
			}
		});
		majoitusIdTxt.setToolTipText("Majoitus ID");
		
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
		
		toimipisteIdLbl = new Label(composite, SWT.NONE);
		toimipisteIdLbl.setText("Toimipiste ID");
		
		
		// Toisen rivin label ja tekstikentta (asiakasid ja toimipiste id)
		hintaLbl = new Label(composite, SWT.NONE);
		hintaLbl.setText("Hinta");
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		
		toimipisteIdTxt = new Text(composite, SWT.BORDER);
		toimipisteIdTxt.setEditable(false);
		toimipisteIdTxt.addVerifyListener(new VerifyListener() {
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
		toimipisteIdTxt.setToolTipText("Toimipiste ID");
		toimipisteIdTxt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		hintaTxt = new Text(composite, SWT.BORDER);
		hintaTxt.addVerifyListener(new VerifyListener() {
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
		hintaTxt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		hintaTxt.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				// TODO ASIAKAS ID KUUNTELIJA TOIMINNALLISUUS TARVITTAESSA
			}
		});
		hintaTxt.setToolTipText("Hinta");
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		
		valitseToimipisteBtn = new Button(composite, SWT.NONE);
		valitseToimipisteBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
	
				valitseToimipisteGui = new ValitseToimipisteGUI();
				if(toimipisteIdTxt.getText() != "") {
					valitseToimipisteGui.setToimipisteIdTalteen(Integer.parseInt(toimipisteIdTxt.getText()));
				}
				if(majoitusIdTxt.getText() != "") {
					valitseToimipisteGui.setMajoitusIdTalteen(Integer.parseInt(majoitusIdTxt.getText()));
				}
				if (nimiTxt.getText() != "") {
					valitseToimipisteGui.setNimiTalteen(nimiTxt.getText());
				}
				if (hintaTxt.getText() != "") {
					valitseToimipisteGui.setHintaTalteen(Integer.parseInt(hintaTxt.getText()));
				}
				valitseToimipisteGui.setLisaaMajoitusGui(getLisaaMajoitusGui());
				lisaaMajoitusShl.close();
				try {
					valitseToimipisteGui.open();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		valitseToimipisteBtn.setText("Valitse toimipiste");
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
				lisaaMajoitusShl.close();
				majoitusEtusivuGui = getMajoitusEtusivuGui();
				try {
					majoitusEtusivuGui.open();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		Button majoitusLisaaBtn = new Button(composite, SWT.NONE);
		majoitusLisaaBtn.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		majoitusLisaaBtn.setText("Hyväksy");
		majoitusLisaaBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO HYVAKSY NAPILLE KUUNTELIJA
				try {
					lisaaMajoitusTietokantaan();
					majoitusEtusivuGui = getMajoitusEtusivuGui();
					majoitusEtusivuGui.open();
					lisaaMajoitusShl.close();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		new Label(composite, SWT.NONE);
	}


	public int getMajoitusIdTalteen() {
		return majoitusIdTalteen;
	}

	public void setMajoitusIdTalteen(int majoitusIdTalteen) {
		this.majoitusIdTalteen = majoitusIdTalteen;
	}

	public int getToimipisteIdTalteen() {
		return toimipisteIdTalteen;
	}

	public void setToimipisteIdTalteen(int toimipisteIdTalteen) {
		this.toimipisteIdTalteen = toimipisteIdTalteen;
	}

	public int getHintaTalteen() {
		return hintaTalteen;
	}

	public void setHintaTalteen(int hintaTalteen) {
		this.hintaTalteen = hintaTalteen;
	}

	public String getNimiTalteen() {
		return nimiTalteen;
	}

	public void setNimiTalteen(String nimiTalteen) {
		this.nimiTalteen = nimiTalteen;
	}

	public LisaaMajoitusGUI getLisaaMajoitusGui() {
		return lisaaMajoitusGui;
	}

	public void setLisaaMajoitusGui(LisaaMajoitusGUI lisaaMajoitusGui) {
		this.lisaaMajoitusGui = lisaaMajoitusGui;
	}

	public MajoitusEtusivuGUI getMajoitusEtusivuGui() {
		return majoitusEtusivuGui;
	}

	public void setMajoitusEtusivuGui(MajoitusEtusivuGUI majoitusEtusivuGui) {
		this.majoitusEtusivuGui = majoitusEtusivuGui;
	}

	public int getValittuToimipisteId() {
		return valittuToimipisteId;
	}

	public void setValittuToimipisteId(int valittuToimipisteId) {
		this.valittuToimipisteId = valittuToimipisteId;
	}
	
	
	
}
