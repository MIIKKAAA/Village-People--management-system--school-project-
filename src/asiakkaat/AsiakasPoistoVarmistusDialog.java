package asiakkaat;

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

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.DateTime;

public class AsiakasPoistoVarmistusDialog {
	
	protected Shell asiakasnPoistoShl;
	private Label poistoLbl;
	private Label label;
	private int valittuAsiakasID;
	private Connection conn;
	private AsiakkaatEtusivuGUI asiakkaatEtusivuGui;
	private int valittuAsiakasId;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			AsiakasPoistoVarmistusDialog window = new AsiakasPoistoVarmistusDialog();
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
		Display display = ApuMetodeja.centerWindow(asiakasnPoistoShl);

		asiakasnPoistoShl.open();
		asiakasnPoistoShl.layout();
		while (!asiakasnPoistoShl.isDisposed()) {
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
	
	private void poistaAsiakas(int asiakasID) {
		String sql = "DELETE FROM asiakas WHERE AsiakasID = ?";
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
	}
	
	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		asiakasnPoistoShl = new Shell(SWT.CLOSE | SWT.MIN);
		
		
		// "Responsiivisuus - maaritelty tassa vain 1920x1080 resoluutiolle, pidemmalle kehitetyssa ohjelmassa pitaisi muillekin resoluutioille"
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		if (screenSize.getWidth() < 1600){
			asiakasnPoistoShl.setSize((int)(screenSize.getWidth() / 3), (int) (screenSize.getHeight() / 1.95));
		}
		else if (screenSize.getWidth() > 1600){
			asiakasnPoistoShl.setSize((int)(screenSize.getWidth() / 7), (int) (screenSize.getHeight() / 7));
		}
		else if (screenSize.getWidth() == 1600) {
			asiakasnPoistoShl.setSize((int) (screenSize.getWidth() / 3.4), (int) (screenSize.getHeight() / 2.2));
		}
		
				asiakasnPoistoShl.setText("Asiakkaan poisto");
		asiakasnPoistoShl.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Composite composite = new Composite(asiakasnPoistoShl, SWT.NONE);
		composite.setLayout(new GridLayout(4, false));
		new Label(composite, SWT.NONE);
		
		// Otsikko
		poistoLbl = new Label(composite, SWT.NONE);
		poistoLbl.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 2, 2));
		poistoLbl.setAlignment(SWT.CENTER);
		poistoLbl.setText("Haluatko varmasti poistaa valitun asiakkaan?");
		new Label(composite, SWT.NONE);
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
				asiakasnPoistoShl.close();
			}
		});
		
		Button poistoHyvaksyBtn = new Button(composite, SWT.NONE);
		poistoHyvaksyBtn.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		poistoHyvaksyBtn.setText("Hyväksy");
		poistoHyvaksyBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				AsiakkaatEtusivuGUI asiakasEtusivuOlio = getAsiakkaatEtusivuGui();
				setValittuAsiakasID(asiakasEtusivuOlio.getValittuAsiakasId());
				poistaAsiakas(getValittuAsiakasID());
				asiakasEtusivuOlio.getEtusivuAsiakasTbl().removeAll();
				asiakasEtusivuOlio.haeTiedot();
				asiakasnPoistoShl.close();

			}
		});
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		
		label = new Label(composite, SWT.NONE);
	}

	public int getValittuAsiakasID() {
		return valittuAsiakasID;
	}

	public void setValittuAsiakasID(int valittuAsiakasID) {
		this.valittuAsiakasID = valittuAsiakasID;
	}

	public AsiakkaatEtusivuGUI getAsiakkaatEtusivuGui() {
		return asiakkaatEtusivuGui;
	}

	public void setAsiakkaatEtusivuGui(AsiakkaatEtusivuGUI asiakkaatEtusivuGui) {
		this.asiakkaatEtusivuGui = asiakkaatEtusivuGui;
	}

	public int getValittuAsiakasId() {
		return valittuAsiakasId;
	}

	public void setValittuAsiakasId(int valittuAsiakasId) {
		this.valittuAsiakasId = valittuAsiakasId;
	}
	
	
}
