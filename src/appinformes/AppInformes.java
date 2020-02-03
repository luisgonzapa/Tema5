/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package appinformes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javax.swing.JOptionPane;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author Luis
 */
public class AppInformes extends Application {

    public static Connection conexion = null;

    @Override
    public void start(Stage primaryStage) {
        //Establecemos la conexión con la BD
        conectaBD();

        //Creamos la escena. Un Menubar.
        MenuBar menu = new MenuBar();

        //Añadimos las opciones
        Menu menuOpciones = new Menu("Informes");
        //Añadimos la lista de opciones desplegables.
        MenuItem opcion1 = new MenuItem("Listado Facturas");
        MenuItem opcion2 = new MenuItem("Ventas totales");
        MenuItem opcion3 = new MenuItem("Facturas por Cliente");
        MenuItem opcion4 = new MenuItem("Subinforme Listado Facturas");
        //Añadimos los Items al Menu.
        menuOpciones.getItems().add(opcion1);
        menuOpciones.getItems().add(opcion2);
        menuOpciones.getItems().add(opcion3);
        menuOpciones.getItems().add(opcion4);

        //Añadimos el Menu(menuOpciones) al MenuBar(menu)
        menu.getMenus().add(menuOpciones);

        //Atributos para la opcion3.
        Label labelProducto = new Label("Introduzca código de cliente:");
        TextField idCliente = new TextField();
        Button btn = new Button();
        btn.setText("Generar Informe");

        VBox root = new VBox();

        root.getChildren().add(menu);

        //Manejadores para opciones.
        //opcion1
        opcion1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    generaInforme1();
                } catch (JRException ex) {
                    Logger.getLogger(AppInformes.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        //opcion2
        opcion2.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    generaInforme2();
                } catch (JRException ex) {
                    Logger.getLogger(AppInformes.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        //opcion3
        opcion3.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //Carga en pantalla los componentes necesarios...
                root.getChildren().addAll(labelProducto,idCliente, btn);
            }
        });
        
        //opcion4
        opcion4.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    generaInforme4();
                } catch (JRException ex) {
                    System.out.println("Error al abrir el reporte");
                    Logger.getLogger(AppInformes.class.getName()).log(Level.SEVERE, null, ex);

                }
            }
        });
        
        //Evento botón "generar informe" opcion3
        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //El manejador del botón desencadena el generaInforme3(Textfield)
                String numeroCliente = idCliente.getText();
                int nCliente = Integer.parseInt(numeroCliente);
                generaInforme3(nCliente);
            }
        });

        Scene scene = new Scene(root, 300, 250);
        primaryStage.setTitle("Generador de Informes");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        try {
            //Apaga la base de datos.(Cierra conexión)
            DriverManager.getConnection("jdbc:hsqldb:hsql://localhost;shutdown=true");
        } catch (Exception ex) {
            System.out.println("No se pudo cerrar la conexion a la BD");
        }
    }

    public void conectaBD() {
        //Establecemos conexión con la BD
        String baseDatos = "jdbc:hsqldb:hsql://localhost:9001/xdb";
        String usuario = "sa";
        String clave = "";
        try {
            Class.forName("org.hsqldb.jdbcDriver").newInstance();
            conexion = DriverManager.getConnection(baseDatos, usuario, clave);
        } catch (ClassNotFoundException cnfe) {
            System.err.println("Fallo al cargar JDBC");
            System.exit(1);
        } catch (SQLException sqle) {
            System.err.println("No se pudo conectar a BD");
            System.exit(1);
        } catch (java.lang.InstantiationException sqlex) {
            System.err.println("Imposible Conectar");
            System.exit(1);
        } catch (Exception ex) {
            System.err.println("Imposible Conectar");
            System.exit(1);
        }
    }
    
    public void generaInforme1() throws JRException {
        JasperReport jr = (JasperReport) JRLoader.loadObject(AppInformes.class.getResource("facturas.jasper"));
        JasperPrint jp = (JasperPrint) JasperFillManager.fillReport(jr, null, conexion);
        JasperViewer.viewReport(jp);
    }
    
    public void generaInforme2() throws JRException {
        JasperReport jr = (JasperReport) JRLoader.loadObject(AppInformes.class.getResource("Ventas_Totales.jasper"));
        JasperPrint jp = (JasperPrint) JasperFillManager.fillReport(jr, null, conexion);
        JasperViewer.viewReport(jp);
    }

    public void generaInforme3(int nCliente) {

        try {
            JasperReport jr = (JasperReport) JRLoader.loadObject(AppInformes.class.getResource("FacturasPorCliente.jasper"));
            
            System.out.println("Estoy en generaInforme3");
            //Map de parámetros
            Map parametros = new HashMap();
            parametros.put("idCliente", nCliente);
            JasperPrint jp = (JasperPrint) JasperFillManager.fillReport(jr, parametros, conexion);
            JasperViewer.viewReport(jp);
            
        } catch (JRException ex) {
            System.out.println("Error al recuperar el jasper");
            JOptionPane.showMessageDialog(null, ex);
        }
    }
    
    public void generaInforme4() throws JRException {
        JasperReport jr = (JasperReport) JRLoader.loadObject(AppInformes.class.getResource("SubinformeListadoFactura.jasper"));
        JasperPrint jp = (JasperPrint) JasperFillManager.fillReport(jr, null, conexion);
        JasperViewer.viewReport(jp);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
