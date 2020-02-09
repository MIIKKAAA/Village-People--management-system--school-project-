package asiakkaat;

import java.sql.*;
public class Asiakas {
	
	// attribuutit, vastaavat tietokantataulun sarakkeita
    private int m_asiakas_id;
	private String m_etunimi;
	private String m_sukunimi;
	private String m_lahiosoite;
	private String m_postitoimipaikka;
	private int m_postinro;
	private String m_email;
	private int m_puhelinnro;
	private String m_syntymaaika;

    public Asiakas(){
 
    }
	// getterit ja setterit
	public int getAsiakasId()
	{
		return m_asiakas_id;
	}
	public String getEtunimi() {
		return m_etunimi;
	}
	public String getSukunimi() {
		return m_sukunimi;
	}
	public String getLahiosoite() {
		return m_lahiosoite;
	}
	public String getPostitoimipaikka() {
		return m_postitoimipaikka;
	}
	public int getPostinro() {
		return m_postinro;
	}
	public String getEmail() {
		return m_email;
	}
	public int getPuhelinnro() {
		return m_puhelinnro;
	}
	public String getSyntymaaika() {
		return m_syntymaaika;
	}
	public void setAsiakasId (int id)
	{
		m_asiakas_id = id;
	}
	public void setEtunimi (String ni) {
		m_etunimi = ni;
	}
	public void setSukunimi (String ni) {
		m_sukunimi = ni;
	}
	public void setLahiosoite (String os) {
		m_lahiosoite = os;
	}
	public void setPostitoimipaikka (String ptp) {
		m_postitoimipaikka = ptp;
	}
	public void setPostinro (int pno) {
		m_postinro = pno;
	}
	public void setEmail (String mail) {
		m_email = mail;
	}
	public void setPuhelinnro (int puhveli) {
		m_puhelinnro = puhveli;
	}
	public void setSyntymaaika (String aika) {
		m_syntymaaika = aika;
	}
    @Override
    public String toString(){
        return (m_asiakas_id + " " + m_etunimi + " " + m_sukunimi);
    }
	/*
	Haetaan asiakkaan tiedot ja palautetaan asiakasolio kutsujalle.
	Staattinen metodi, ei vaadi fyysisen olion olemassaoloa.
	*/
	public static Asiakas haeAsiakas (Connection connection, int id) throws SQLException, Exception { // tietokantayhteys välitetään parametrina
		// haetaan tietokannasta asiakasta, jonka asiakas_id = id 
		String sql = "SELECT AsiakasID, Etunimi, Sukunimi, Postiosoite, Postitoimipaikka, Postinumero, Sahkoposti, Puhelinnumero, Syntymaaika " 
					+ " FROM asiakas WHERE AsiakasID = ?"; // ehdon arvo asetetaan jäljempänä
		ResultSet tulosjoukko = null;
		PreparedStatement lause = null;
		try {
			// luo PreparedStatement-olio sql-lauseelle
			lause = connection.prepareStatement(sql);
			lause.setInt( 1, id); // asetetaan where ehtoon (?) arvo
			// suorita sql-lause
			tulosjoukko = lause.executeQuery();	
			if (tulosjoukko == null) {
				throw new Exception("Asiakasta ei loydy");
			}
		} catch (SQLException se) {
            // SQL virheet
                        throw se;
                } catch (Exception e) {
            // JDBC virheet
                        throw e;
		}
		// käsitellään resultset - laitetaan tiedot asiakasoliolle
		Asiakas asiakasOlio = new Asiakas ();
		
		try {
			if (tulosjoukko.next () == true){
				//asiakas_id, etunimi, sukunimi, lahiosoite, postitoimipaikka, postinro, email, puhelinnro, syntymaaika
				asiakasOlio.setAsiakasId (tulosjoukko.getInt("AsiakasID"));
				asiakasOlio.setEtunimi (tulosjoukko.getString("Etunimi"));
				asiakasOlio.setSukunimi(tulosjoukko.getString("Sukunimi"));
				asiakasOlio.setLahiosoite (tulosjoukko.getString("Postiosoite"));
				asiakasOlio.setPostitoimipaikka (tulosjoukko.getString("Postitoimipaikka"));
				asiakasOlio.setPostinro (tulosjoukko.getInt("Postinumero"));
				asiakasOlio.setEmail (tulosjoukko.getString("Sahkoposti"));
				asiakasOlio.setPuhelinnro (tulosjoukko.getInt("Puhelinnumero"));
				asiakasOlio.setSyntymaaika (tulosjoukko.getString("Syntymaaika"));
			}
			
		}catch (SQLException e) {
			throw e;
		}
		// palautetaan asiakasolio
		
		return asiakasOlio;
	}
	/*
	Lisätään asiakkaan tiedot tietokantaan.
	Metodissa "asiakasolio kirjoittaa tietonsa tietokantaan".
	*/
     public int lisaaAsiakas (Connection connection) throws SQLException, Exception { // tietokantayhteys välitetään parametrina
		// haetaan tietokannasta asiakasta, jonka asiakas_id = olion id -> ei voi lisätä, jos on jo kannassa
		String sql = "SELECT AsiakasID" 
					+ " FROM asiakas WHERE AsiakasID = ?"; // ehdon arvo asetetaan jäljempänä
		ResultSet tulosjoukko = null;
		PreparedStatement lause = null; 
		
		try {
			// luo PreparedStatement-olio sql-lauseelle
			lause = connection.prepareStatement(sql);
			lause.setInt( 1, getAsiakasId()); // asetetaan where ehtoon (?) arvo, olion asiakasid
			// suorita sql-lause
			tulosjoukko = lause.executeQuery();	
			if (tulosjoukko.next () == true) { // asiakas loytyi
				throw new Exception("Asiakas on jo olemassa");
			}
		} catch (SQLException se) {
            // SQL virheet
                    throw se;
                } catch (Exception e) {
            // JDBC virheet
                    throw e;
		}
		// parsitaan INSERT
		sql = "INSERT INTO asiakas "
		+ "(AsiakasID, Etunimi, Sukunimi, Postiosoite, Postitoimipaikka, Postinumero, Sahkoposti, Puhelinnumero, Syntymaaika) "
		+ " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
		// System.out.println("Lisataan " + sql);
		lause = null;
		try {
			// luo PreparedStatement-olio sql-lauseelle
			lause = connection.prepareStatement(sql);
			// laitetaan arvot INSERTtiin
			lause.setInt( 1, getAsiakasId());
			lause.setString(2, getEtunimi()); 
			lause.setString(3, getSukunimi()); 
			lause.setString(4, getLahiosoite());
			lause.setString(5, getPostitoimipaikka ());
			lause.setInt(6, getPostinro ());
			lause.setString(7, getEmail ());
			lause.setInt(8, getPuhelinnro ());
			lause.setString(9, getSyntymaaika ());
			// suorita sql-lause
			int lkm = lause.executeUpdate();	
		//	System.out.println("lkm " + lkm);
			if (lkm == 0) {
				throw new Exception("Asiakkaan lisaaminen ei onnistu");
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
	Muutetaan asiakkaan tiedot tietokantaan id-tietoa (avain) lukuunottamatta. 
	Metodissa "asiakasolio muuttaa tietonsa tietokantaan".
	*/
    public int muutaAsiakas (Connection connection) throws SQLException, Exception { // tietokantayhteys välitetään parametrina
		// haetaan tietokannasta asiakasta, jonka asiakas_id = olion id, virhe, jos ei löydy
		String sql = "SELECT AsiakasID" 
					+ " FROM asiakas WHERE AsiakasID = ?"; // ehdon arvo asetetaan jäljempänä
		ResultSet tulosjoukko = null;
		PreparedStatement lause = null;
		try {
			// luo PreparedStatement-olio sql-lauseelle
			lause = connection.prepareStatement(sql);
			lause.setInt( 1, getAsiakasId()); // asetetaan where ehtoon (?) arvo
			// suorita sql-lause
			tulosjoukko = lause.executeQuery();	
			if (tulosjoukko.next () == false) { // asiakasta ei löytynyt
				throw new Exception("Asiakasta ei loydy tietokannasta");
			}
		} catch (SQLException se) {
            // SQL virheet
                    throw se;
                } catch (Exception e) {
            // JDBC virheet
                    throw e;
		}
		// parsitaan Update, päiviteään tiedot lukuunottamatta avainta
		sql = "UPDATE  asiakas "
		+ "SET Etunimi = ?, Sukunimi = ?, Postiosoite = ?, Postitoimipaikka = ?, Postinumero = ?, Sahkoposti = ?, Puhelinnumero = ?, Syntymaaika = ? "
		+ " WHERE AsiakasID = ?";
		
		lause = null;
		try {
			// luo PreparedStatement-olio sql-lauseelle
			lause = connection.prepareStatement(sql);
			
			// laitetaan olion attribuuttien arvot UPDATEen
			
			lause.setString(1, getEtunimi()); 
			lause.setString(2, getSukunimi()); 
			lause.setString(3, getLahiosoite());
			lause.setString(4, getPostitoimipaikka ());
			lause.setInt(5, getPostinro ());
			lause.setString(6, getEmail ());
			lause.setInt(7, getPuhelinnro ());
			lause.setString(8, getSyntymaaika ());
			// where-ehdon arvo
            lause.setInt( 9, getAsiakasId());
			// suorita sql-lause
			int lkm = lause.executeUpdate();	
			if (lkm == 0) {
				throw new Exception("Asiakkaan muuttaminen ei onnistu");
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
	Poistetaan asiakkaan tiedot tietokannasta. 
	Metodissa "asiakasolio poistaa tietonsa tietokannasta".
	*/
	public int poistaAsiakas (Connection connection) throws SQLException, Exception { // tietokantayhteys välitetään parametrina
		
		// parsitaan DELETE
		String sql = "DELETE FROM  asiakas WHERE AsiakasID = ?";
		PreparedStatement lause = null;
		try {
			// luo PreparedStatement-olio sql-lauseelle
			lause = connection.prepareStatement(sql);
			// laitetaan arvot DELETEn WHERE-ehtoon
			lause.setInt( 1, getAsiakasId());
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
		return 0; // toiminto ok
	}
}
