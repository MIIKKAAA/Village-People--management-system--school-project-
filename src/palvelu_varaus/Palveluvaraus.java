package palvelu_varaus;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

import org.eclipse.swt.widgets.DateTime;

public class Palveluvaraus {
	
	private int palveluvarausId;
	private int palveluId;
	private int asiakasId;
	private String aloituspvm;
	private String lopetuspvm;
	
	
	public Palveluvaraus() {
	}
	
	
	
    public int getPalveluvarausId() {
		return palveluvarausId;
	}



	public void setPalveluvarausId(int palveluvarausId) {
		this.palveluvarausId = palveluvarausId;
	}



	public int getPalveluId() {
		return palveluId;
	}



	public void setPalveluId(int palveluId) {
		this.palveluId = palveluId;
	}



	public int getAsiakasId() {
		return asiakasId;
	}



	public void setAsiakasId(int asiakasId) {
		this.asiakasId = asiakasId;
	}


	public String getAloituspvm() {
		return aloituspvm;
	}



	public void setAloituspvm(String aloituspvm) {
		this.aloituspvm = aloituspvm;
	}



	public String getLopetuspvm() {
		return lopetuspvm;
	}



	public void setLopetuspvm(String lopetuspvm) {
		this.lopetuspvm = lopetuspvm;
	}



	@Override
    public String toString(){
        return ("Id: "+palveluvarausId);
    }
	
	
	public static Palveluvaraus haePalveluvaraus(int id, Connection connection) throws SQLException, Exception {
		
		String sql = "SELECT * FROM palvelun_varaus WHERE PalveluvarausID = ?";
		ResultSet tulosjoukko = null;
		PreparedStatement lause = null;
		try {
			// luo PreparedStatement-olio sql-lauseelle
			lause = connection.prepareStatement(sql);
			lause.setInt(1, id); // asetetaan where ehtoon (?) arvo
			// suorita sql-lause
			tulosjoukko = lause.executeQuery();	
			if (tulosjoukko == null) { // tulosjoukkoon odotetaan ainoastaan max. yhtä riviä
				System.out.println("");
			}
		} catch (SQLException se) {
            // SQL virheet
            se.printStackTrace();
        } catch (Exception e) {
            // JDBC virheet
            e.printStackTrace();
		}
		// käsitellään resultset - laitetaan tiedot asiakasoliolle
		Palveluvaraus palveluvarausOlio = new Palveluvaraus();
		try {
			while (tulosjoukko.next () == true){ // tulosjoukossa oletettavasti useampia rivejä
				
				palveluvarausOlio.setAsiakasId(Integer.parseInt(tulosjoukko.getString("AsiakasID")));
				palveluvarausOlio.setPalveluId(Integer.parseInt(tulosjoukko.getString("PalveluID")));
				palveluvarausOlio.setPalveluvarausId(Integer.parseInt(tulosjoukko.getString("PalveluvarausID")));
				palveluvarausOlio.setLopetuspvm(tulosjoukko.getString("lopetuspvm"));
				palveluvarausOlio.setAloituspvm(tulosjoukko.getString("aloituspvm"));
			
				
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}

		return palveluvarausOlio;
	}
	
	public int lisaaPalveluvaraus(Connection connection) throws SQLException, SQLIntegrityConstraintViolationException, Exception {
		
		String sql = "SELECT PalveluvarausID FROM palvelun_varaus WHERE PalveluvarausID = ?";
		ResultSet tulosjoukko = null;
		PreparedStatement lause = null;
		
		try {
			// luo PreparedStatement-olio sql-lauseelle
			lause = connection.prepareStatement(sql);
			lause.setInt(1, getPalveluvarausId()); // asetetaan where ehtoon (?) arvo
			// suorita sql-lause
			tulosjoukko = lause.executeQuery();	
			if (tulosjoukko.next()) {
				throw new Exception("Varaus on jo olemassa");
			}
		} catch (SQLException se) {
            // SQL virheet
            se.printStackTrace();
        } catch (Exception e) {
            // JDBC virheet
            e.printStackTrace();
            return -1;
		}
		
		sql = "INSERT INTO palvelun_varaus (PalveluvarausID, PalveluID, AsiakasID, aloituspvm, lopetuspvm) VALUES (?, ?, ?, ?, ?)";
		lause = null;

		try {
			lause = connection.prepareStatement(sql);

			lause.setInt(1, getPalveluvarausId());
			lause.setInt(2, getPalveluId());
			lause.setInt(3, getAsiakasId());
			lause.setString(4,  getAloituspvm());
			lause.setString(5,  getLopetuspvm());
			
			int lkm = lause.executeUpdate();
			if (lkm == 0) {
				throw new Exception("Palveluvarauksen lisääminen ei onnistunut");
			}
		}catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return 0;
	}
	
	
	public int muokkaaPalveluvaraus(Connection connection) throws SQLException, Exception {
		
		String sql = "SELECT PalveluvarausID FROM palvelun_varaus WHERE PalveluvarausID = ?";
		
		ResultSet tulosjoukko = null;
		PreparedStatement lause = null;
		
		try {
			// luo PreparedStatement-olio sql-lauseelle
			lause = connection.prepareStatement(sql);
			lause.setInt(1, getPalveluvarausId()); // asetetaan where ehtoon (?) arvo
			// suorita sql-lause
			tulosjoukko = lause.executeQuery();	
			if (tulosjoukko.next() == false) {
				throw new Exception("Palveluvarausta ei löydy.");
			}
		} catch (SQLException se) {
            // SQL virheet
            se.printStackTrace();
        } catch (Exception e) {
            // JDBC virheet
            e.printStackTrace();
		}
	
		sql = "UPDATE palvelun_varaus SET PalveluvarausID = ?, PalveluID = ?, AsiakasID = ?, aloituspvm = ?, lopetuspvm = ? WHERE PalveluvarausID = ?"; 
		lause = null;

		try {
			lause = connection.prepareStatement(sql);
			
			lause.setInt(1, getPalveluvarausId());
			lause.setInt(2, getPalveluId());
			lause.setInt(3, getAsiakasId());
			lause.setString(4,  getAloituspvm());
			lause.setString(5,  getLopetuspvm());
			lause.setInt(6, getPalveluvarausId());
			
			int lkm = lause.executeUpdate();
			if (lkm == 0) {
				throw new Exception("Palveluvarauksen muuttaminen ei onnistunut");
			}
		}catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return 0;
	}
	

}


