import java.sql.*;
import java.util.Scanner;

public class OperacionDeBaseDeDatos {
	private ResultSet resultadoDeOp;
	
	public OperacionDeBaseDeDatos(int op, Connection connection) throws Exception {
		switch(op) {
			case 1:
				System.out.println("------------------");
				System.out.println("INSERTAR ACTIVIDAD");
				System.out.println("------------------\n");
				System.out.println("Ingrese el codigo de su nueva actividad: ");
				Scanner scanCodAct = new Scanner(System.in);
				String codAct = scanCodAct.nextLine();
				System.out.println("Ingrese el codigo de la materia a la que su nueva actividad pertenece: ");
				Scanner scanCodMatPert = new Scanner(System.in);
				String codMatPert = scanCodMatPert.nextLine();
				System.out.println("Ingrese la descripcion de su nueva actividad: ");
				Scanner scanDescripcion = new Scanner(System.in);
				String descripcion = scanDescripcion.nextLine();
				insertarActividad(codAct, codMatPert, descripcion, connection);
				break;
				
			case 2:
				System.out.println("----------------");
				System.out.println("ELIMINAR MATERIA");
				System.out.println("----------------\n");
				System.out.println("Ingrese el codigo de la materia que desea eliminar: ");
				Scanner scanCodMateria = new Scanner(System.in);
				String codMateria = scanCodMateria.nextLine();
				eliminarMateria(codMateria, connection);
				break;
				
			case 3:
				System.out.println("---------------------------");
				System.out.println("ALUMNOS DE UNA MATERIA DADA");
				System.out.println("---------------------------\n");
				System.out.println("Ingrese el codigo de la materia sobre la que quiere consultar los alumnos: ");
				Scanner scanMatDada = new Scanner(System.in);
				String materiaDada = scanMatDada.nextLine();
				
				try {
					resultadoDeOp = getAlumnosDeMateria(materiaDada, connection);
				} catch(SQLException sqle) {
					manejarExcepciones(sqle, connection);
				}
				break;
				
			default:
				throw new UnsupportedOperationException("operacion inexistente");
		}
	}
	
	/**
	 * Método que devuelve el resultado que dió realizar una de las consultas de esta clase.
	 * @return el resultado de una consulta.
	 */
	public ResultSet getResults() {
		return resultadoDeOp;
	}
	
	/**
	 * Método que, dado el código de una materia cargada en la base de datos, devuelve los datos de todos
	 * los alumnos que la cursan
	 * @param materiaDada el código de la materia que se desea consultar
	 * @param connection la conección a la base de datos, necesaria para la ejecución de
	 * la consulta
	 * @return los alumnos que están cursando la materia dada
	 * @throws SQLException
	 */
	private ResultSet getAlumnosDeMateria(String materiaDada, Connection connection) throws SQLException{
			String query = "SELECT nro_alumno, dni, nombre, apellido, direccion "
					+ "FROM plataforma_ed_a_dist.alumno INNER JOIN "
					+ "(SELECT * FROM plataforma_ed_a_dist.realiza INNER JOIN "
					+ "plataforma_ed_a_dist.persona ON(dni_alumno_cursa = dni)) AS persEnMats "
					+ "ON(dni_alumno = persEnMats.dni) "
					+ "WHERE cod_materia_cursada = " + materiaDada;
					
			PreparedStatement statement = connection.prepareStatement(query);
			return statement.executeQuery();
	}
	
	/**
	 * Método que elimina una materia, dado su código.
	 * @param codMateria el código de la materia que se desea eliminar.
	 * @param connection la conección a la base de datos, necesaria para la ejecución de
	 * la consulta
	 */
	private void eliminarMateria(String codMateria, Connection connection) {
		try {
			String query = "DELETE FROM plataforma_ed_a_dist.materia WHERE cod_materia = " + codMateria;
			PreparedStatement statement = connection.prepareStatement(query);
			statement.executeUpdate();
			connection.commit();
			System.out.println("Listo!");
		} catch(SQLException sqle) {
			manejarExcepciones(sqle, connection);
		}
	}
	
	/**
	 * Método que realiza la operación de insertar una nueva actividad a la tabla de 
	 * actividades, dados su código, el código de la materia a la que pertenece y su
	 * descripción.

	 * @param codAct el código de la nueva actividad que se desea ingresar
	 * @param codMatPert el código de la materia a la que la nueva actividad pertenece
	 * @param descripcion información de la nueva actividad
	 * @param connection la conección a la base de datos, necesaria para la ejecución de
	 * la consulta
	 */
	private void insertarActividad(String codAct, String codMatPert, String descripcion, Connection connection) {
		try {
			String query = "INSERT INTO plataforma_ed_a_dist.actividad VALUES "
					+ "(" + codAct + ", " + "'" + descripcion + "'" + ", " + codMatPert + ")";
			
			PreparedStatement statement = connection.prepareStatement(query);
			statement.executeUpdate();
			connection.commit();
			System.out.println("Listo!");
		} catch (SQLException sqle){
			manejarExcepciones(sqle, connection);
		}
	}

	/**
	 * Método utilizado para el manejo de Excepciones en las operaciones.
	 * @param sqle la excepción que se produjo al realizar alguna operación sobre la base de datos.
	 * @param connection la conección a la base de datos.
	 */
	private void manejarExcepciones(SQLException sqle, Connection connection) {
		try	{
			// como se produjo una excepcion en el acceso a la base de datos se debe hacer el rollback	 	
			System.err.println("antes del rollback: " + sqle);
			connection.rollback();
			System.err.println("Error Se produjo una Excepcion accediendo a la base de datos: " + sqle);
			sqle.printStackTrace();
        } catch(Exception e) {
            System.err.println("Error Ejecutando el rollback de la transaccion: " + e.getMessage());
            e.printStackTrace();
        }
	}
	
	
	
}
