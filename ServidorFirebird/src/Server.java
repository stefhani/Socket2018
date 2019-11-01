import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import paqueteCliente.Client;
import paqueteSwitch.ClassSwitch;


public class Server  {

	public static void main(String[] args) {
		Server s = new Server(); 
		s.conexion(5400); //esto es para que conecte, lo que no me funciona
       //System.out.println(Consulta("Select * from tbl_libros"));//aca tengo la funcion que me da un string, que quiero mandar como msj y no me deja
	}
	
	// FIREBIRD
	public static String Consulta(String consultaSQL) {
		String respuesta = "";
		String url = "jdbc:firebirdsql:localhost/3050:C:/Users/Majo/Documents/Eclipse workbench/ServidorFirebird/EJEMPLO.FDB"; /*en mi caso lo tengo en esta dirección, si no cambian el puerto, el default es 3050*/
        String usuario = "SYSDBA";
        String  pass = "Majo1908";
    
        String driver = "org.firebirdsql.jdbc.FBDriver";
        
        Connection conexion;
        Statement consulta;
        ResultSet resultado;
        
        
        
        try{
       	 Class.forName(driver);
       	 conexion = DriverManager.getConnection(url,usuario,pass);
       	 
       	 consulta = conexion.createStatement();
       	 resultado = consulta.executeQuery(consultaSQL);
       	 
       	 String tabla = consultaSQL.substring(18, consultaSQL.length());
       	 
       	 switch (tabla) {
       	 
       	    case "usuario":
       	     while (resultado.next()){ /*mientras encuentre un "siguiente"_ muestra todo lo que tengo en la tabla*/
           		 String id = resultado.getString("IDU");
           		 String user = resultado.getString("usuario");
           		 String pss = resultado.getString("condicion");
            	        		 	
           		respuesta+="ID:"+ id+"\n"+"TÍTULO:" + user+"\n"+"ESTADO:" + pss+"\n"+"\n";
       	     }
       	    break;
       	    	
       	    	
       	    case "libros":
       	       while (resultado.next()){ /*mientras encuentre un "siguiente"_ muestra todo lo que tengo en la tabla*/
           		 String id = resultado.getString("IDL");
           		 String tit = resultado.getString("titulo");
           		 String es = resultado.getString("estado");
           		 
           		respuesta+="ID:"+ id+"\n"+"TÍTULO:" + tit+"\n"+"ESTADO:" + es+"\n"+"\n";
           		
           	   }

       	    break;
       	    	
       	   
       	 }
       	 
       	 
        }
        catch(Exception e){
       	    System.out.println("ERROR ENCONTRADO: " + e.getMessage());
        }
        return(respuesta);
        
	}
	
	

	//SERVIDOR
	private Socket miServicio; 
    private ServerSocket socketServicio;
    
    private OutputStream outputStream;
    private InputStream inputStream;

    private DataOutputStream salidaDatos;
    private DataInputStream entradaDatos;

    private boolean opcion = true;
    private Scanner scanner;
    private String esctribir;

    //APERTURA DE SOCKET
    public void conexion(int numeroPuerto) {
        try {
            socketServicio = new ServerSocket(numeroPuerto);
            System.out.println("El servidor firebird está escuchando en el puerto: " + numeroPuerto);
            miServicio = socketServicio.accept();
            
            recibirDatos();
           // Consulta("select * from tbl_libros ",this);
            
            while (opcion) {
                scanner = new Scanner(System.in);
                esctribir = Consulta("Select * from tbl_libros");
                if (!esctribir.equals("fin")) {
                    enviarDatos(esctribir);
                } else {
                    opcion = false;
                    enviarDatos("FIN DE LA CONEXION");
                    socketServicio.close();
                    
                }
            }
            //cerrarTodo();
        } catch (Exception ex) {
            System.out.println("NO HAY CONEXIÓN CON EL CLIENTE");
        }
    }

    
    public void enviarDatos(String datos) {
        try {
            outputStream = miServicio.getOutputStream();
            salidaDatos = new DataOutputStream(outputStream);
            salidaDatos.writeUTF(datos);
            salidaDatos.flush();
        } catch (IOException ex) {
            Logger.getLogger(ClassSwitch.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void recibirDatos() {
        try {
            inputStream = miServicio.getInputStream();
            entradaDatos = new DataInputStream(inputStream);
            System.out.println("Mensaje del Cliente: "+entradaDatos.readUTF());
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void cerrarTodo() {
        try {
            salidaDatos.close();
            entradaDatos.close();
            socketServicio.close();
            miServicio.close();

        } catch (IOException ex) {
            Logger.getLogger(ClassSwitch.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
	  

}
