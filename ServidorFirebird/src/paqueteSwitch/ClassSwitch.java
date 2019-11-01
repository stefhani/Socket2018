package paqueteSwitch;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import paqueteCliente.Client;


public class ClassSwitch {

    private Socket miServicio; //SWITCH COMO SERVIDOR
    private ServerSocket socketServicio;
    
    private Socket switchCliente; //SWITCH COMO CLIENTE

    private OutputStream outputStream;
    private InputStream inputStream;

    private DataOutputStream salidaDatos;
    private DataInputStream entradaDatos;

    private boolean opcion = true;
    private Scanner scanner;
    private String esctribir;

    //APERTURA DE SOCKET
    public void conexionSERV(int numeroPuerto) {
        try {
            socketServicio = new ServerSocket(numeroPuerto);
            System.out.println("El switch se está escuchando en el puerto: " + numeroPuerto);
            miServicio = socketServicio.accept();
            
            Thread hilo = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (opcion) {
                        recibirDatos();

                    }
                }
            });
            hilo.start();
            while (opcion) {
                scanner = new Scanner(System.in);
                esctribir = scanner.nextLine();
                if (!esctribir.equals("fin")) {
                    enviarDatos(esctribir);
                } else {
                    opcion = false;
                    enviarDatos("FIN DE LA CONEXION");
                    socketServicio.close();
                    
                }
            }
            cerrarTodo();
        } catch (Exception ex) {
            System.out.println("NO HAY CONEXIÓN CON EL CLIENTE");
        }
    }

    public void conexionBD(int numeroPuerto, String ipMaquina) {
        try {
            switchCliente = new Socket(ipMaquina, numeroPuerto);
            Thread hilo1 = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (opcion) {
                        escucharDatos(switchCliente);
                        
                    }
                }
            });
            hilo1.start();
            while (opcion) {
                scanner = new Scanner(System.in);
                esctribir = scanner.nextLine();
                if (!esctribir.equals("fin")) {
                    enviarDatos(esctribir);
                } else {
                    
                    enviarDatos("FIN DE LA CONEXION");
                    System.out.println("FIN DE LA CONEXIÓN");
                    opcion = false;
                    switchCliente.close();
                    
                }
            }
           
        } catch (Exception ex) {
            System.out.println("ERROR AL ABRIR LOS SOCKETS CLIENTE " + ex.getMessage());
        }
    }
    
    
    public void escucharDatos(Socket socket) {
        try {
            inputStream = socket.getInputStream();
            entradaDatos = new DataInputStream(inputStream);
            System.out.println("Mensaje del Servidor: " +entradaDatos.readUTF());
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
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

    public static void main(String[] args) {
        ClassSwitch serv = new ClassSwitch();
        //serv.conexionSERV(5555);
        serv.conexionBD(5400, "localhost");
    }

}