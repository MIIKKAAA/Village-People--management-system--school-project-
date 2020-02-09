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

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.DateTime;

public class MajoitusPoistoVarmistusDialog {

	protected Shell majoitusvarauksenPoistoShl;
	private Label poistoLbl;
	private Label label;
	private int valittuMajoitusID;
	private Connection conn;
	private MajoitusEtusivuGUI majoitusEtusivuGui;

	/**
	 * Open the window.
	 * @throws Exception 
	 * @throws SQLException 
	 */
	public void open() throws SQLException, Exception {
		createContents();
		yhdista();
		// Avautuessa keskelle
		Display display = ApuMetodeja.centerWindow(majoitusvarauksenPoistoShl);

		majoitusvarauksenPoistoShl.open();
		majoitusvarauksenPoistoShl.layout();
		while (!majoitusvarauksenPoistoShl.isDisposed()) {
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
	
	private void poistaLasku(int laskuID) {
		// Jos majoitusvarauksista loytyy majoitusID, asetetaan NULL:ksi - tahan pitaisi toteuttaa koko majoitusvarauksen poisto
		// (koska majoitusvarausta ei luonnollisesti voi olla ellei ole majoituspaikkaa), mutta se jaakoon tekematta nyt
		String sql = "UPDATE majoitusvaraus SET MajoitusID = NULL WHERE MajoitusID = ?";
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
	
		sql = "DELETE FROM majoitus WHERE MajoitusID = ?";
		ResultSet tulosjoukko1 = null;
		PreparedStatement lause1 = null;
		try {
			// luo PreparedStatement-olio sql-lauseelle
			lause1 = conn.prepareStatement(sql);
			
			lause1.setInt(1, getValittuMajoitusID()); // asetetaan where ehtoon (?) arvo
			// suorita sql-lause
			tulosjoukko1 = lause1.executeQuery();	
			if (tulosjoukko1 == null) {
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
		majoitusvarauksenPoistoShl = new Shell(SWT.CLOSE | SWT.MIN);
		
		
		// "Responsiivisuus - maaritelty tassa vain 1920x1080 resoluutiolle, pidemmalle kehitetyssa ohjelmassa pitaisi muillekin resoluutioille"
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		if (screenSize.getWidth() < 1600){
			majoitusvarauksenPoistoShl.setSize((int)(screenSize.getWidth() / 3), (int) (screenSize.getHeight() / 1.95));
		}
		else if (screenSize.getWidth() > 1600){
			majoitusvarauksenPoistoShl.setSize((int)(screenSize.getWidth() / 6), (int) (screenSize.getHeight() / 7));
		}
		else if (screenSize.getWidth() == 1600) {
			majoitusvarauksenPoistoShl.setSize((int) (screenSize.getWidth() / 3.4), (int) (screenSize.getHeight() / 2.2));
		}
		
				majoitusvarauksenPoistoShl.setText("Majoitusvarauksen poisto");
		majoitusvarauksenPoistoShl.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Composite composite = new Composite(majoitusvarauksenPoistoShl, SWT.NONE);
		composite.setLayout(new GridLayout(4, false));
		new Label(composite, SWT.NONE);
		
		// Otsikko
		poistoLbl = new Label(composite, SWT.NONE);
		poistoLbl.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 2, 2));
		poistoLbl.setAlignment(SWT.CENTER);
		poistoLbl.setText("Haluatko varmasti poistaa valitun majoitusvarauksen?");
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
				majoitusvarauksenPoistoShl.close();
			}
		});
		
		Button poistoHyvaksyBtn = new Button(composite, SWT.NONE);
		poistoHyvaksyBtn.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		poistoHyvaksyBtn.setText("Hyväksy");
		poistoHyvaksyBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MajoitusEtusivuGUI majoitusEtusivuOlio = getMajoitusEtusivuGui();
				setValittuMajoitusID(majoitusEtusivuOlio.getValittuMajoitusID());
				poistaLasku(getValittuMajoitusID());
				try {
					majoitusEtusivuOlio.getMajoitusHallintaTbl().removeAll();
					majoitusEtusivuOlio.populateEtusivuTbl(majoitusEtusivuOlio.getMajoitusHallintaTbl());
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				majoitusvarauksenPoistoShl.close();

			}
		});
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		
		label = new Label(composite, SWT.NONE);
	}

	public int getValittuMajoitusID() {
		return valittuMajoitusID;
	}

	public void setValittuMajoitusID(int valittuMajoitusID) {
		this.valittuMajoitusID = valittuMajoitusID;
	}

	public MajoitusEtusivuGUI getMajoitusEtusivuGui() {
		return majoitusEtusivuGui;
	}

	public void setMajoitusEtusivuGui(MajoitusEtusivuGUI majoitusEtusivuGui) {
		this.majoitusEtusivuGui = majoitusEtusivuGui;
	}

	
	
	
}
