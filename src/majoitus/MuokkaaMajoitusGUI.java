package majoitus;

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

public class MuokkaaMajoitusGUI {

	protected Shell muokkaaMajoitusShl;
	private Text majoitusIdTxt;
	private Text nimiTxt;
	private Text toimipisteIdTxt;
	private Label muokkaaMajoitusLbl;
	private Label majoitusIdLbl;
	private Label nimiLbl;
	private Label toimipisteIdLbl;
	private Label label;
	private Connection conn;
	Majoitus majoitus = new Majoitus();
	private int valittuMajoitusID;
	private MajoitusEtusivuGUI majoitusEtusivuGui;
	private Label hintaLbl;
	private Text hintaTxt;
	private Button valitseToimipisteBtn;
	private ValitseToimipisteGUI_muokkaa valitseToimipisteGui_muokkaa;
	private int majoitusIdTalteen;
	private int toimipisteIdTalteen;
	private int hintaTalteen;
	private String nimiTalteen = "";
	private int valittuToimipisteId;
	private MuokkaaMajoitusGUI muokkaaMajoitusGui;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			MuokkaaMajoitusGUI window = new MuokkaaMajoitusGUI();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 * @throws Exception 
	 * @throws SQLException 
	 */
	public void open() throws SQLException, Exception {
		createContents();
		yhdista();
		// Avautuessa keskelle
		Display display = ApuMetodeja.centerWindow(muokkaaMajoitusShl);
		luoMajoitusTiedot();
		
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
		
		muokkaaMajoitusShl.open();
		muokkaaMajoitusShl.layout();
		while (!muokkaaMajoitusShl.isDisposed()) {
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
	
	
	private void luoMajoitusTiedot() throws SQLException {
		String sql = "SELECT MajoitusID, Nimi, Hinta, ToimipisteID "+
					"FROM majoitus WHERE MajoitusID = ?";
		ResultSet tulosjoukko = null;
		PreparedStatement lause = null;
		try {
			// luo PreparedStatement-olio sql-lauseelle
			lause = conn.prepareStatement(sql);
			
			lause.setInt(1, getValittuMajoitusID()); // asetetaan where ehtoon (?) arvo
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
	
			majoitusIdTxt.setText(Integer.toString(tulosjoukko.getInt("MajoitusID")));
			toimipisteIdTxt.setText(Integer.toString(tulosjoukko.getInt("ToimipisteID")));
			hintaTxt.setText(Integer.toString(tulosjoukko.getInt("Hinta")));
			nimiTxt.setText(tulosjoukko.getString("Nimi"));
		}
	}
	
	public void muokkaaMajoitus() throws SQLException, Exception {
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

		if (majoitusIdTxt.getText() == "") {
			System.out.println("Et syöttänyt Majoitus ID:tä, yritä uudelleen!!");
			muokkaaMajoitusShl.close();
		}
		else {
			majoitus.setMajoitusId(Integer.parseInt(majoitusIdTxt.getText()));
		}
		if (hintaTxt.getText() == "") {
			System.out.println("Et syöttänyt hintaa, yritä uudelleen!");
			muokkaaMajoitusShl.close();
		}
		else {
			majoitus.setHinta(Integer.parseInt(hintaTxt.getText()));
		}
		if (nimiTxt.getText() == "") {
			System.out.println("Et syöttänyt nimeä, yritä uudelleen!");
			muokkaaMajoitusShl.close();
		}
		else {
			majoitus.setNimi(nimiTxt.getText());
		}
		if (toimipisteIdTxt.getText() =="") {
			System.out.println("Et valinnut toimipistettä, yritä uudelleen!");
		}
		else {
			majoitus.setToimipisteId(Integer.parseInt(toimipisteIdTxt.getText()));
		}
		
		majoitus.setMajoitusId(Integer.parseInt(majoitusIdTxt.getText()));
		majoitus.setHinta(Integer.parseInt(hintaTxt.getText()));
		majoitus.setNimi(nimiTxt.getText());
		majoitus.setToimipisteId(Integer.parseInt(toimipisteIdTxt.getText()));
		
		majoitus.muokkaaMajoitus(conn);
	}


	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		muokkaaMajoitusShl = new Shell(SWT.CLOSE | SWT.MIN);
		
		
		// "Responsiivisuus - maaritelty tassa vain 1920x1080 resoluutiolle, pidemmalle kehitetyssa ohjelmassa pitaisi muillekin resoluutioille"
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		
		if (screenSize.getWidth() < 1600){
			muokkaaMajoitusShl.setSize((int)(screenSize.getWidth() / 3), (int) (screenSize.getHeight() / 1.95));
		}
		else if (screenSize.getWidth() > 1600){
			muokkaaMajoitusShl.setSize((int)(screenSize.getWidth() / 4.5), (int) (screenSize.getHeight() / 3.3));
		}
		else if (screenSize.getWidth() == 1600) {
			muokkaaMajoitusShl.setSize((int) (screenSize.getWidth() / 3.4), (int) (screenSize.getHeight() / 2.2));
		}
		
		muokkaaMajoitusShl.setText("Muokkaa majoitusta");
		muokkaaMajoitusShl.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Composite composite = new Composite(muokkaaMajoitusShl, SWT.NONE);
		composite.setLayout(new GridLayout(4, false));
		new Label(composite, SWT.NONE);
		
		// Otsikko
		muokkaaMajoitusLbl = new Label(composite, SWT.NONE);
		muokkaaMajoitusLbl.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 2, 2));
		muokkaaMajoitusLbl.setAlignment(SWT.CENTER);
		muokkaaMajoitusLbl.setText("Muokkaa alla majoituksen tietoja");
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
		
		
		// Ekan rivin label ja tekstikentta (majoitus ja majoitusid)
		majoitusIdLbl = new Label(composite, SWT.NONE);
		majoitusIdLbl.setText("Majoitus ID");
		
		nimiLbl = new Label(composite, SWT.NONE);
		nimiLbl.setText("Nimi");
		
		label = new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		
		majoitusIdTxt = new Text(composite, SWT.BORDER);
		majoitusIdTxt.setEditable(false);
		majoitusIdTxt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		majoitusIdTxt.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				// TODO MAJOITUSVARAUS ID KUUNTELIJA TOIMINNALLISUUS TARVITTAESSA
			
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
		
		
		// Toisen rivin label ja tekstikentta (asiakas id ja toimipiste id)
		toimipisteIdLbl = new Label(composite, SWT.NONE);
		toimipisteIdLbl.setText("Toimipiste ID");
		
		hintaLbl = new Label(composite, SWT.NONE);
		hintaLbl.setText("Hinta");
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		
		toimipisteIdTxt = new Text(composite, SWT.BORDER);
		toimipisteIdTxt.setEditable(false);
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
				// TODO ASIAKASID KUUNTELIJA TOIMINNALLISUUS TARVITTAESSA
			}
		});
		toimipisteIdTxt.setToolTipText("Toimipiste ID");
		
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
		hintaTxt.setToolTipText("Hinta");
		hintaTxt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		
		valitseToimipisteBtn = new Button(composite, SWT.NONE);
		valitseToimipisteBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
	
				valitseToimipisteGui_muokkaa = new ValitseToimipisteGUI_muokkaa();
				if(toimipisteIdTxt.getText() != "") {
					valitseToimipisteGui_muokkaa.setToimipisteIdTalteen(Integer.parseInt(toimipisteIdTxt.getText()));
				}
				if(majoitusIdTxt.getText() != "") {
					valitseToimipisteGui_muokkaa.setMajoitusIdTalteen(Integer.parseInt(majoitusIdTxt.getText()));
				}
				if (nimiTxt.getText() != "") {
					valitseToimipisteGui_muokkaa.setNimiTalteen(nimiTxt.getText());
				}
				if (hintaTxt.getText() != "") {
					valitseToimipisteGui_muokkaa.setHintaTalteen(Integer.parseInt(hintaTxt.getText()));
				}
				valitseToimipisteGui_muokkaa.setMuokkaaMajoitusGui(getMuokkaaMajoitusGui());
				muokkaaMajoitusShl.close();
				try {
					valitseToimipisteGui_muokkaa.open();
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
				muokkaaMajoitusShl.close();
				majoitusEtusivuGui = getMajoitusEtusivuGui();
				try {
					majoitusEtusivuGui.open();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			
			}
		});
		
		Button majoitusMuokkaaBtn = new Button(composite, SWT.NONE);
		majoitusMuokkaaBtn.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		majoitusMuokkaaBtn.setText("Hyväksy");
		majoitusMuokkaaBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO HYVAKSY NAPILLE KUUNTELIJA
				try {
					muokkaaMajoitus();
					majoitusEtusivuGui = getMajoitusEtusivuGui();
					muokkaaMajoitusShl.close();
					majoitusEtusivuGui.open();
					
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		new Label(composite, SWT.NONE);
	}

	public MajoitusEtusivuGUI getMajoitusEtusivuGui() {
		return majoitusEtusivuGui;
	}

	public void setMajoitusEtusivuGui(MajoitusEtusivuGUI majoitusEtusivuGui) {
		this.majoitusEtusivuGui = majoitusEtusivuGui;
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

	public int getValittuToimipisteId() {
		return valittuToimipisteId;
	}

	public void setValittuToimipisteId(int valittuToimipisteId) {
		this.valittuToimipisteId = valittuToimipisteId;
	}

	public MuokkaaMajoitusGUI getMuokkaaMajoitusGui() {
		return muokkaaMajoitusGui;
	}

	public void setMuokkaaMajoitusGui(MuokkaaMajoitusGUI muokkaaMajoitusGui) {
		this.muokkaaMajoitusGui = muokkaaMajoitusGui;
	}
	public int getValittuMajoitusID() {
		return valittuMajoitusID;
	}

	public void setValittuMajoitusID(int valittuMajoitusID) {
		this.valittuMajoitusID = valittuMajoitusID;
	}
}
