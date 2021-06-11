import java.sql.*;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Danielle Ruben, Garcia Ayelen, Urricelqui Irina. 
 * @version 1.0
 */

public class PlataformaEducacionADistancia {


  public static void main(String[] args) {

    try {
    //  String driver = "org.gjt.mm.mysql.Driver";
    	String driver = "org.postgresql.Driver";
	    String url = "jdbc:postgresql://localhost:5432/proyecto?searchpath=plataforma_ed_a dist";
	    String username = "postgres";
	    String password = "postgres";

      // Load database driver if not already loaded.
      Class.forName(driver);
      // Establish network connection to database.
      Connection connection =
        DriverManager.getConnection(url, username, password);

 

      String query = "SELECT * FROM alumno ";
      PreparedStatement statement = connection.prepareStatement(query);
//      statement.setString(1, "2");
      ResultSet resultSet = statement.executeQuery();

      
      // Send query to database and store results.



      // Print results.
      while(resultSet.next()) 
      {
       System.out.print(" DNI: " + resultSet.getString("dni_alumno"));
       System.out.print("; Numero alumno: "+resultSet.getString("nro_alumno"));
       System.out.print("\n   ");
       System.out.print("\n   ");
      } 
      
    } catch(ClassNotFoundException cnfe) {
      System.err.println("Error loading driver: " + cnfe);
      cnfe.printStackTrace();
    } catch(SQLException sqle) {
    	sqle.printStackTrace();
      System.err.println("Error connecting: " + sqle);
    } catch(Exception sqle) {
  	sqle.printStackTrace();
    System.err.println("Error connecting: " + sqle);
  }


  }

}