import java.sql.*;
import java.util.Scanner;

/**
 * <p>Title: Plataforma_educacion_a_Distancia</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Danielle Ruben, Garcia Ayelen, Urricelqui Irina. 
 * @version 1.0
 */

public class PlataformaEducacionADistancia {


  public static void main(String[] args) {

    try {
    	String driver = "org.postgresql.Driver";
	    String url = "jdbc:postgresql://localhost:5432/proyecto_Urricelqui_Danielle_Garcia";
	    String username = "postgres";
	    String password = "root";

	    // Load database driver if not already loaded.
	    Class.forName(driver);
	    // Establish network connection to database.
	    Connection connection =
	    		DriverManager.getConnection(url, username, password);
	    
	    System.out.println("ingrese la operacion a realizar \n");
	    
	    System.out.println("-------------------------");
	    System.out.println("Operaciones disponibles: ");
	    System.out.println("-------------------------\n");
	    
	    System.out.println("1 - INGRESAR ACTIVIDAD");
	    System.out.println("2 - ELIMINAR UNA MATERIA");
	    System.out.println("3 - CONSULTAR ALUMNOS DE UNA MATERIA \n");
	    int eleccion;
	    
	    while(true) {
		    Scanner s = new Scanner(System.in);
		    if(s.hasNextInt()) {
		    	eleccion = s.nextInt();
		    	if(eleccion >= 1 && eleccion <= 3) {
		    		break;
		    	}
		    }
		    System.out.println("Solo se permiten los valores 1, 2 ó 3. Ingrese nuevamente:");
	    }
	    connection.setAutoCommit(false); 
	    OperacionDeBaseDeDatos op = new OperacionDeBaseDeDatos(eleccion, connection);
	    if (eleccion == 3) {
	    	ResultSet resultSet = op.getResults();
	    	int counter = 0;
	    	// Print results.
		    while(resultSet.next()) {
		       System.out.print(" Numero alumno: "+ resultSet.getString("nro_alumno") + "\n");
		       System.out.print(" DNI: " + resultSet.getString("dni") + "\n");
		       System.out.print(" Nombre: " + resultSet.getString("nombre") + "\n");
		       System.out.print(" Apellido: " + resultSet.getString("apellido") + "\n");
		       System.out.print(" Direccion: " + resultSet.getString("direccion"));
		       System.out.print("\n");
		       System.out.print("\n");
		       counter++;
		    } 
		    if(counter == 0) {
		    	System.out.println("No hay alumnos cursando esta materia");
		    }
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