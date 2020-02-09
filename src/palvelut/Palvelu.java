package palvelut;

import java.sql.*;
public class Palvelu {
	
	// attribuutit, vastaavat tietokantataulun sarakkeita
    private int m_palvelu_id;
	private String m_nimi;
	private int m_hinta;
	private int m_toimipiste_id;

    public Palvelu(){
 
    }
	// getterit ja setterit
	public int getPalveluId()
	{
		return m_palvelu_id;
	}
	public String getNimi() {
		return m_nimi;
	}
	public int getHinta() {
		return m_hinta;
	}
	public int getToimipisteId() {
		return m_toimipiste_id;
	}
	public void setPalveluId (int id)
	{
		m_palvelu_id = id;
	}
	public void setNimi (String ni) {
		m_nimi = ni;
	}
	public void setHinta (int hi) {
		m_hinta = hi;
	}
	public void setToimipisteId (int id) {
		m_toimipiste_id = id;
	}
    @Override
    public String toString(){
        return (m_palvelu_id + " " + m_nimi);
    }
	/*
	Haetaan palvelun tiedot ja palautetaan palveluolio kutsujalle.
	Staattinen metodi, ei vaadi fyysisen olion olemassaoloa.
	*/
	public static Palvelu haePalvelu (Connection connection, int id) throws SQLException, Exception { // tietokantayhteys välitetään parametrina
		// haetaan tietokannasta palvelua, jonka palvelu_id = id 
		String sql = "SELECT PalveluID, Palvelun_nimi, Palvelun_hinta, ToimipisteID " 
					+ " FROM palvelu WHERE PalveluID = ?"; // ehdon arvo asetetaan jäljempänä
		ResultSet tulosjoukko = null;
		PreparedStatement lause = null;
		try {
			// luo PreparedStatement-olio sql-lauseelle
			lause = connection.prepareStatement(sql);
			lause.setInt( 1, id); // asetetaan where ehtoon (?) arvo
			// suorita sql-lause
			tulosjoukko = lause.executeQuery();	
			if (tulosjoukko == null) {
				throw new Exception("Palvelua ei loydy");
			}
		} catch (SQLException se) {
            // SQL virheet
                        throw se;
                } catch (Exception e) {
            // JDBC virheet
                        throw e;
		}
		// käsitellään resultset - laitetaan tiedot asiakasoliolle
		Palvelu palveluOlio = new Palvelu ();
		
		try {
			if (tulosjoukko.next () == true){
				//palvelu_id, nimi, hinta, toimipiste_id
				palveluOlio.setPalveluId (tulosjoukko.getInt("PalveluID"));
				palveluOlio.setNimi (tulosjoukko.getString("Palvelun_nimi"));
				palveluOlio.setHinta (tulosjoukko.getInt("Palvelun_hinta"));
				palveluOlio.setToimipisteId (tulosjoukko.getInt("ToimipisteID"));
			}
			
		}catch (SQLException e) {
			throw e;
		}
		// palautetaan palveluolio
		
		return palveluOlio;
	}
	/*
	Lisätään palvelun tiedot tietokantaan.
	Metodissa "palveluolio kirjoittaa tietonsa tietokantaan".
	*/
     public int lisaaPalvelu (Connection connection) throws SQLException, Exception { // tietokantayhteys välitetään parametrina
		// haetaan tietokannasta asiakasta, jonka palvelu_id = olion id -> ei voi lisätä, jos on jo kannassa
		String sql = "SELECT PalveluID" 
					+ " FROM palvelu WHERE PalveluID = ?"; // ehdon arvo asetetaan jäljempänä
		ResultSet tulosjoukko = null;
		PreparedStatement lause = null; 
		
		try {
			// luo PreparedStatement-olio sql-lauseelle
			lause = connection.prepareStatement(sql);
			lause.setInt( 1, getPalveluId()); // asetetaan where ehtoon (?) arvo, olion asiakasid
			// suorita sql-lause
			tulosjoukko = lause.executeQuery();	
			if (tulosjoukko.next () == true) { // asiakas loytyi
				throw new Exception("Palvelu on jo olemassa");
			}
		} catch (SQLException se) {
            // SQL virheet
                    throw se;
                } catch (Exception e) {
            // JDBC virheet
                    throw e;
		}
		// parsitaan INSERT
		sql = "INSERT INTO palvelu "
		+ "(PalveluID, Palvelun_nimi, Palvelun_hinta, ToimipisteID) "
		+ " VALUES (?, ?, ?, ?)";
		// System.out.println("Lisataan " + sql);
		lause = null;
		try {
			// luo PreparedStatement-olio sql-lauseelle
			lause = connection.prepareStatement(sql);
			// laitetaan arvot INSERTtiin
			lause.setInt( 1, getPalveluId());
			lause.setString(2, getNimi()); 
			lause.setInt(3, getHinta ());
			lause.setInt(4, getToimipisteId ());
			// suorita sql-lause
			int lkm = lause.executeUpdate();	
		//	System.out.println("lkm " + lkm);
			if (lkm == 0) {
				throw new Exception("Palvelun lisaaminen ei onnistu");
			}
		} catch (SQLException se) {
            // SQL virheet
            throw se;
        } catch (Exception e) {
            // JDBC ym. virheet
            throw e;
		}
		return 0;
	}
	/*
	Muutetaan palvelun tiedot tietokantaan id-tietoa (avain) lukuunottamatta. 
	Metodissa "palveluolio muuttaa tietonsa tietokantaan".
	*/
    public int muutaPalvelu (Connection connection) throws SQLException, Exception { // tietokantayhteys välitetään parametrina
		// haetaan tietokannasta palvelua, jonka palvelu_id = olion id, virhe, jos ei löydy
		String sql = "SELECT PalveluID" 
					+ " FROM palvelu WHERE PalveluID = ?"; // ehdon arvo asetetaan jäljempänä
		ResultSet tulosjoukko = null;
		PreparedStatement lause = null;
		try {
			// luo PreparedStatement-olio sql-lauseelle
			lause = connection.prepareStatement(sql);
			lause.setInt( 1, getPalveluId()); // asetetaan where ehtoon (?) arvo
			// suorita sql-lause
			tulosjoukko = lause.executeQuery();	
			if (tulosjoukko.next () == false) { // asiakasta ei löytynyt
				throw new Exception("Palvelua ei loydy tietokannasta");
			}
		} catch (SQLException se) {
            // SQL virheet
                    throw se;
                } catch (Exception e) {
            // JDBC virheet
                    throw e;
		}
		// parsitaan Update, päiviteään tiedot lukuunottamatta avainta
		sql = "UPDATE  palvelu "
		+ "SET Palvelun_nimi = ?, Palvelun_hinta = ?, ToimipisteID = ? "
		+ " WHERE PalveluID = ?";
		
		lause = null;
		try {
			// luo PreparedStatement-olio sql-lauseelle
			lause = connection.prepareStatement(sql);
			
			// laitetaan olion attribuuttien arvot UPDATEen
			
			lause.setString(1, getNimi()); 
			lause.setInt(2, getHinta ());
			lause.setInt(3, getToimipisteId ());
			// where-ehdon arvo
            lause.setInt(4, getPalveluId());
			// suorita sql-lause
			int lkm = lause.executeUpdate();	
			if (lkm == 0) {
				throw new Exception("Palvelun muuttaminen ei onnistu");
			}
		} catch (SQLException se) {
            // SQL virheet
                throw se;
        } catch (Exception e) {
            // JDBC ym. virheet
                throw e;
		}
		return 0; // toiminto ok
	}
	/*
	Poistetaan palvelun tiedot tietokannasta. 
	Metodissa "palveluolio poistaa tietonsa tietokannasta".
	*/
	public int poistaPalvelu (Connection connection) throws SQLException, Exception { // tietokantayhteys välitetään parametrina
		String sql = "UPDATE laskujen_hallinta_ja_seuranta SET PalveluID = NULL WHERE PalveluID =?";
		PreparedStatement lause = null;
		try {
			// luo PreparedStatement-olio sql-lauseelle
			lause = connection.prepareStatement(sql);
			// laitetaan arvot DELETEn WHERE-ehtoon
			lause.setInt( 1, getPalveluId());
			// suorita sql-lause
			int lkm = lause.executeUpdate();	
			if (lkm == 0) {
				throw new Exception("Asiakkaan poistaminen ei onnistu");
			}
			} catch (SQLException se) {
            // SQL virheet
                throw se;
             } catch (Exception e) {
            // JDBC virheet
                throw e;
		}
		// parsitaan DELETE
		sql = "DELETE FROM  palvelu WHERE PalveluID = ?";
		PreparedStatement lause2 = null;
		try {
			// luo PreparedStatement-olio sql-lauseelle
			lause2 = connection.prepareStatement(sql);
			// laitetaan arvot DELETEn WHERE-ehtoon
			lause2.setInt( 1, getPalveluId());
			// suorita sql-lause
			int lkm = lause2.executeUpdate();	
			if (lkm == 0) {
				throw new Exception("Palvelun poistaminen ei onnistu");
			}
			} catch (SQLException se) {
            // SQL virheet
                throw se;
             } catch (Exception e) {
            // JDBC virheet
                throw e;
		}
		return 0; // toiminto ok
	}
}
