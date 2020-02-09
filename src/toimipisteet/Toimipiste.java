package toimipisteet;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.eclipse.swt.widgets.DateTime;

public class Toimipiste {
	
	private int toimipisteId;
	private String paikkakunta;
	private String nimi;
	private String osoite;
	private int postinumero;
	
	
	public Toimipiste() {
	}
	

	public int getToimipisteId() {
		return toimipisteId;
	}






	public void setToimipisteId(int toimipisteId) {
		this.toimipisteId = toimipisteId;
	}






	public String getPaikkakunta() {
		return paikkakunta;
	}






	public void setPaikkakunta(String paikkakunta) {
		this.paikkakunta = paikkakunta;
	}






	public String getNimi() {
		return nimi;
	}






	public void setNimi(String nimi) {
		this.nimi = nimi;
	}






	public String getOsoite() {
		return osoite;
	}






	public void setOsoite(String osoite) {
		this.osoite = osoite;
	}






	public int getPostinumero() {
		return postinumero;
	}






	public void setPostinumero(int postinumero) {
		this.postinumero = postinumero;
	}






	@Override
    public String toString(){
        return ("Id: "+toimipisteId);
    }
	
	
	public static Toimipiste haeToimipiste(int id, Connection connection) throws SQLException, Exception {
		
		String sql = "SELECT * FROM toimipisteet WHERE ToimipisteID = ?";
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
		Toimipiste toimipisteOlio = new Toimipiste();
		try {
			while (tulosjoukko.next () == true){ // tulosjoukossa oletettavasti useampia rivejä
		
				toimipisteOlio.setNimi(tulosjoukko.getString("Nimi"));
				toimipisteOlio.setOsoite(tulosjoukko.getString("Osoite"));
				toimipisteOlio.setPaikkakunta(tulosjoukko.getString("Paikkakunta"));
				toimipisteOlio.setPostinumero(Integer.parseInt(tulosjoukko.getString("Postinumero")));
				toimipisteOlio.setToimipisteId(Integer.parseInt(tulosjoukko.getString("ToimipisteID")));
			
				
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}

		return toimipisteOlio;
	}
	
	public int lisaaToimipiste(Connection connection) throws SQLException, Exception {
		
		String sql = "SELECT ToimipisteID FROM toimipisteet WHERE ToimipisteID = ?";
		ResultSet tulosjoukko = null;
		PreparedStatement lause = null;
		
		try {
			// luo PreparedStatement-olio sql-lauseelle
			lause = connection.prepareStatement(sql);
			lause.setInt(1, getToimipisteId()); // asetetaan where ehtoon (?) arvo
			// suorita sql-lause
			tulosjoukko = lause.executeQuery();	
			if (tulosjoukko.next()) {
				throw new Exception("Toimipiste on jo olemassa");
			}
		} catch (SQLException se) {
            // SQL virheet
            se.printStackTrace();
        } catch (Exception e) {
            // JDBC virheet
            e.printStackTrace();
		}
		
		sql = "INSERT INTO toimipisteet (ToimipisteID, Paikkakunta, Nimi, Osoite, Postinumero) VALUES (?, ?, ?, ?, ?)";
		lause = null;

		try {
			lause = connection.prepareStatement(sql);

			lause.setInt(1, getToimipisteId());
			lause.setString(2, getPaikkakunta());
			lause.setString(3, getNimi());
			lause.setString(4, getOsoite());
			lause.setInt(5, getPostinumero());
			
			int lkm = lause.executeUpdate();
			if (lkm == 0) {
				throw new Exception("Majoituksen lisääminen ei onnistunut");
			}
		}catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return 0;
	}
	
	
	public int muokkaaToimipiste(Connection connection) throws SQLException, Exception {
		
		String sql = "SELECT ToimipisteID FROM toimipisteet WHERE ToimipisteID = ?";
		
		ResultSet tulosjoukko = null;
		PreparedStatement lause = null;
		
		try {
			// luo PreparedStatement-olio sql-lauseelle
			lause = connection.prepareStatement(sql);
			lause.setInt(1, getToimipisteId()); // asetetaan where ehtoon (?) arvo
			// suorita sql-lause
			tulosjoukko = lause.executeQuery();	
			if (tulosjoukko.next() == false) {
				throw new Exception("Toimipistettä ei löydy.");
			}
		} catch (SQLException se) {
            // SQL virheet
            se.printStackTrace();
        } catch (Exception e) {
            // JDBC virheet
            e.printStackTrace();
		}
	
		sql = "UPDATE toimipisteet SET ToimipisteID = ?, Paikkakunta = ?, Nimi = ?, Osoite = ?, Postinumero = ? WHERE ToimipisteID = ?"; 
		lause = null;

		try {
			lause = connection.prepareStatement(sql);
			
			lause.setInt(1, getToimipisteId());
			lause.setString(2, getPaikkakunta());
			lause.setString(3, getNimi());
			lause.setString(4, getOsoite());
			lause.setInt(5, getPostinumero());
			lause.setInt(6, getToimipisteId());
			
			int lkm = lause.executeUpdate();
			if (lkm == 0) {
				throw new Exception("Majoituksen muuttaminen ei onnistunut");
			}
		}catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return 0;
	}
	

}


