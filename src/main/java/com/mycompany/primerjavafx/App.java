package com.mycompany.primerjavafx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import java.sql.*;

public class App extends Application {

    private static Scene scene;

    private final String URL = "jdbc:mysql://localhost:3306/empresa_construccion";
    private final String USER = "root";
    private final String PASS = "";

    private TextArea txtResultado;
    
    @Override
    public void start(Stage stage) {
        // Inicializamos los componentes
        txtResultado = new TextArea();
        txtResultado.setEditable(false);
        txtResultado.setPrefHeight(400);

        Button btnA = new Button("A. Vehículos más nuevos");
        Button btnB = new Button("B. Técnicos por contacto");
        Button btnC = new Button("C. Reporte de Revisiones");

        // Estilo rápido para los botones
        btnA.setMaxWidth(Double.MAX_VALUE);
        btnB.setMaxWidth(Double.MAX_VALUE);
        btnC.setMaxWidth(Double.MAX_VALUE);

        // Asignamos las funciones a los botones
        btnA.setOnAction(e -> mostrarVehiculosNuevos());
        btnB.setOnAction(e -> mostrarTecnicosDesc());
        btnC.setOnAction(e -> mostrarReporteRevisiones());

        // Layout
        VBox root = new VBox(10, btnA, btnB, btnC, txtResultado);
        root.setStyle("-fx-padding: 20;");

        Scene scene = new Scene(root, 600, 550);
        stage.setTitle("Sistema Empresa de Construcción");
        stage.setScene(scene);
        stage.show();
    }

    // --- MÉTODOS DE LÓGICA SQL ---

    public void mostrarVehiculosNuevos() {
        txtResultado.clear();
        
        // En MySQL, la consulta es idéntica
        String sql = "SELECT * FROM vehiculo ORDER BY odometro ASC LIMIT 3";
        
        try {
            // Cargar el driver de MySQL (Opcional en versiones nuevas, pero recomendado)
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            try (Connection con = DriverManager.getConnection(URL, USER, PASS);
                 PreparedStatement ps = con.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                
                txtResultado.appendText("--- 3 VEHÍCULOS MÁS NUEVOS (MySQL) ---\n");
                while (rs.next()) {
                    txtResultado.appendText(String.format("Placa: %s | Modelo: %s | Odómetro: %s\n",
                            rs.getString("placa"), rs.getString("modelo"), rs.getString("odometro")));
                }
            }
        } catch (ClassNotFoundException e) {
            txtResultado.setText("Error: No se encontró el Driver de MySQL.");
        } catch (SQLException e) {
            txtResultado.setText("Error de Conexión MySQL: " + e.getMessage());
        }
    }

    public void mostrarTecnicosDesc() {
        txtResultado.clear();
        String sql = "SELECT * FROM tecnico ORDER BY contacto DESC";
        try (Connection con = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            txtResultado.appendText("--- TÉCNICOS POR CONTACTO (DESC) ---\n");
            while (rs.next()) {
                txtResultado.appendText(String.format("Nombre: %s %s | Contacto: %d\n",
                        rs.getString("nombre"), rs.getString("apellido"), rs.getInt("contacto")));
            }
        } catch (SQLException e) {
            txtResultado.setText("Error: " + e.getMessage());
        }
    }

    public void mostrarReporteRevisiones() {
        txtResultado.clear();
        String sql = "SELECT r.id, (t.nombre || ' ' || t.apellido) as tecnico, v.placa, v.tipo, " +
                     "(o.nombre || ' ' || o.apellido) as operador, r.fecha_revision " +
                     "FROM revision r " +
                     "JOIN tecnico t ON r.encargado = t.id " +
                     "JOIN vehiculo v ON r.vehiculo = v.placa " +
                     "JOIN operador o ON r.chofer = o.id";
        
        try (Connection con = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            txtResultado.appendText("--- REPORTE DETALLADO DE REVISIONES ---\n");
            while (rs.next()) {
                txtResultado.appendText(String.format(
                    "ID: %d | Técnico: %s | Vehículo: %s (%s) | Chofer: %s | Fecha: %s\n",
                    rs.getInt("id"), rs.getString("tecnico"), rs.getString("placa"), 
                    rs.getString("tipo"), rs.getString("operador"), rs.getDate("fecha_revision")
                ));
            }
        } catch (SQLException e) {
            txtResultado.setText("Error en Reporte: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch();
    }

}