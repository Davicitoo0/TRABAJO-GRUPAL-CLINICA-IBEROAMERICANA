import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.MaskFormatter;

// Clase principal del sistema
public class SistemaGestionHospitalaria {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new InterfazHospital().setVisible(true);
        });
    }
}

// Clase para la interfaz gráfica
class InterfazHospital extends JFrame {
    private SistemaAutenticacion auth;
    private GestorPacientes gestorPacientes;
    private GestorHistoriasClinicas gestorHistorias;
    private Usuario usuarioActual;

    // Paneles de la interfaz
    private JPanel panelLogin;
    private JPanel panelSistema;
    private JTabbedPane tabbedPane;

    // Componentes para el login
    private JTextField txtDniLogin;
    private JFormattedTextField txtFechaNacLogin;
    private JLabel lblErrorLogin;

    // Componentes para la gestión de pacientes
    private JTextField txtDniPaciente, txtNombrePaciente, txtApellidoPaciente, txtEdadPaciente, txtTelefonoPaciente, txtDireccionPaciente;
    private JFormattedTextField txtFechaNacPaciente;
    private JComboBox<String> cmbGeneroPaciente;
    private DefaultTableModel modeloPacientes;
    private JLabel lblErrorDni, lblErrorNombre, lblErrorApellido, lblErrorEdad, lblErrorTelefono;

    // Componentes para la gestión de historias clínicas
    private JTextField txtDniHistoria;
    private JFormattedTextField txtFechaHistoria;
    private JTextArea txtDiagnosticoHistoria, txtTratamientoHistoria, txtMedicamentosHistoria, txtResultadosAnalisisHistoria;
    private DefaultTableModel modeloHistorias;
    private JLabel lblErrorDniHistoria, lblErrorFechaHistoria;

    // Componente para reportes
    private JTextArea areaReportes;
    
    // VARIABLES PARA LA VISTA DEL PACIENTE
    private JList<String> listaCitas;
    private JPanel panelDetalleHistoria;
    private JLabel lblDiagnostico, lblTratamiento, lblMedicamentos, lblResultadosAnalisis;

    public InterfazHospital() {
        super("Sistema de Gestión Hospitalaria - Clínica Iberoamericana");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        
        gestorPacientes = new GestorPacientes();
        // Se pasa la instancia de gestorPacientes al constructor de SistemaAutenticacion
        auth = new SistemaAutenticacion(gestorPacientes);
        gestorHistorias = new GestorHistoriasClinicas();
        
        crearInterfazLogin();
        add(panelLogin);
    }
    
    private void crearInterfazLogin() {
        panelLogin = new JPanel(new GridBagLayout());
        panelLogin.setBackground(new Color(240, 245, 249));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(240, 245, 249));
        
        JLabel lblTitulo = new JLabel("Clínica Iberoamericana", JLabel.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitulo.setForeground(new Color(44, 62, 80));
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(lblTitulo);
        
        contentPanel.add(Box.createVerticalStrut(20));
        
        JPanel loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBorder(BorderFactory.createTitledBorder("Inicio de Sesión"));
        loginPanel.setBackground(Color.WHITE);
        loginPanel.setPreferredSize(new Dimension(400, 300));
        loginPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        GridBagConstraints loginGbc = new GridBagConstraints();
        loginGbc.insets = new Insets(10, 10, 10, 10);
        
        loginGbc.gridx = 0; loginGbc.gridy = 0; loginGbc.anchor = GridBagConstraints.WEST;
        loginPanel.add(new JLabel("DNI:"), loginGbc);
        
        loginGbc.gridy = 1;
        loginPanel.add(new JLabel("Fecha de Nacimiento (DDMMYY):"), loginGbc);
        
        loginGbc.gridx = 1; loginGbc.gridy = 0; loginGbc.fill = GridBagConstraints.HORIZONTAL;
        loginGbc.weightx = 1.0;
        txtDniLogin = new JTextField(15);
        loginPanel.add(txtDniLogin, loginGbc);
        
        txtDniLogin.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { }
            public void removeUpdate(DocumentEvent e) { }
            public void insertUpdate(DocumentEvent e) {
                SwingUtilities.invokeLater(() -> {
                    String text = txtDniLogin.getText();
                    if (!text.matches("\\d*")) {
                        String filteredText = text.replaceAll("[^\\d]", "");
                        txtDniLogin.setText(filteredText);
                        lblErrorLogin.setText("El DNI solo debe contener números.");
                    } else {
                        lblErrorLogin.setText(" ");
                    }
                });
            }
        });
        
        loginGbc.gridy = 1;
        try {
            MaskFormatter formatter = new MaskFormatter("##/##/##");
            formatter.setPlaceholderCharacter(' ');
            txtFechaNacLogin = new JFormattedTextField(formatter);
        } catch (java.text.ParseException e) {
            txtFechaNacLogin = new JFormattedTextField();
            e.printStackTrace();
        }
        loginPanel.add(txtFechaNacLogin, loginGbc);
        
        loginGbc.gridx = 0; loginGbc.gridy = 2; loginGbc.gridwidth = 2; loginGbc.fill = GridBagConstraints.NONE;
        lblErrorLogin = new JLabel(" ");
        lblErrorLogin.setForeground(Color.RED);
        loginPanel.add(lblErrorLogin, loginGbc);
        
        loginGbc.gridx = 0; loginGbc.gridy = 3; loginGbc.gridwidth = 2; loginGbc.fill = GridBagConstraints.NONE;
        JButton btnLogin = new JButton("Iniciar Sesión");
        btnLogin.setBackground(new Color(52, 152, 219));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.addActionListener(e -> autenticarUsuario());
        loginPanel.add(btnLogin, loginGbc);
        
        contentPanel.add(loginPanel);
        
        gbc.gridy = 0; gbc.weighty = 1.0; gbc.anchor = GridBagConstraints.CENTER;
        panelLogin.add(contentPanel, gbc);
    }
    
    private void autenticarUsuario() {
        String dni = txtDniLogin.getText().trim();
        String fechaNacimiento = txtFechaNacLogin.getText().replace("/", "").trim();
        
        if (dni.isEmpty() || fechaNacimiento.isEmpty()) {
            lblErrorLogin.setText("DNI y Fecha de Nacimiento son obligatorios.");
            return;
        }

        usuarioActual = auth.autenticarUsuario(dni, fechaNacimiento);

        if (usuarioActual != null) {
            remove(panelLogin);
            crearInterfazSistema(usuarioActual.getRol());
            add(panelSistema);
            revalidate();
            repaint();
            
            if (usuarioActual.getRol().equals("RECEPCION") || usuarioActual.getRol().equals("ADMIN")) {
                cargarDatosPacientes();
            }
            if (usuarioActual.getRol().equals("MEDICO") || usuarioActual.getRol().equals("ADMIN")) {
                cargarDatosHistorias();
            }
            if (usuarioActual.getRol().equals("PACIENTE")) {
                 cargarListaCitas(usuarioActual.getDni());
            }

        } else {
            lblErrorLogin.setText("Credenciales incorrectas.");
        }
    }

    private void crearInterfazSistema(String rol) {
        panelSistema = new JPanel(new BorderLayout());
        JMenuBar menuBar = new JMenuBar();
        JMenu menuArchivo = new JMenu("Archivo");
        JMenuItem itemCerrarSesion = new JMenuItem("Cerrar Sesión");
        itemCerrarSesion.addActionListener(e -> cerrarSesion());
        menuArchivo.add(itemCerrarSesion);
        JMenuItem itemSalir = new JMenuItem("Salir");
        itemSalir.addActionListener(e -> System.exit(0));
        menuArchivo.add(itemSalir);
        menuBar.add(menuArchivo);
        setJMenuBar(menuBar);
        
        tabbedPane = new JTabbedPane();

        if (rol.equals("RECEPCION") || rol.equals("ADMIN")) {
            JPanel panelPacientes = crearPanelPacientes();
            tabbedPane.addTab("Gestión de Pacientes", panelPacientes);
        }
        if (rol.equals("MEDICO") || rol.equals("ADMIN")) {
            JPanel panelHistorias = crearPanelHistorias();
            tabbedPane.addTab("Historias Clínicas", panelHistorias);
        }
        if (rol.equals("ADMIN") || rol.equals("MEDICO")) {
            JPanel panelReportes = crearPanelReportes();
            tabbedPane.addTab("Reportes", panelReportes);
        }
        if (rol.equals("PACIENTE")) {
            JPanel panelVistaPaciente = crearPanelVistaPaciente();
            tabbedPane.addTab("Mi Historial Clínico", panelVistaPaciente);
        }

        panelSistema.add(tabbedPane, BorderLayout.CENTER);
    }
    
    private void cerrarSesion() {
        remove(panelSistema);
        add(panelLogin);
        txtDniLogin.setText("");
        txtFechaNacLogin.setText("");
        lblErrorLogin.setText(" ");
        setJMenuBar(null);
        revalidate();
        repaint();
    }
    
    private JPanel crearPanelPacientes() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Registro de Pacientes"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(new JLabel("DNI:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        txtDniPaciente = new JTextField(10);
        formPanel.add(txtDniPaciente, gbc);
        gbc.gridy = 1; gbc.gridx = 1; gbc.weightx = 1.0; gbc.gridwidth = 1;
        lblErrorDni = new JLabel(" ");
        lblErrorDni.setForeground(Color.RED);
        formPanel.add(lblErrorDni, gbc);
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 1.0;
        txtNombrePaciente = new JTextField(10);
        formPanel.add(txtNombrePaciente, gbc);
        gbc.gridy = 3; gbc.gridx = 1;
        lblErrorNombre = new JLabel(" ");
        lblErrorNombre.setForeground(Color.RED);
        formPanel.add(lblErrorNombre, gbc);
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(new JLabel("Apellido:"), gbc);
        gbc.gridx = 1; gbc.gridy = 4; gbc.weightx = 1.0;
        txtApellidoPaciente = new JTextField(10);
        formPanel.add(txtApellidoPaciente, gbc);
        gbc.gridy = 5; gbc.gridx = 1;
        lblErrorApellido = new JLabel(" ");
        lblErrorApellido.setForeground(Color.RED);
        formPanel.add(lblErrorApellido, gbc);
        gbc.gridx = 0; gbc.gridy = 6; gbc.weightx = 0; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(new JLabel("Edad:"), gbc);
        gbc.gridx = 1; gbc.gridy = 6; gbc.weightx = 1.0;
        txtEdadPaciente = new JTextField(5);
        formPanel.add(txtEdadPaciente, gbc);
        gbc.gridy = 7; gbc.gridx = 1;
        lblErrorEdad = new JLabel(" ");
        lblErrorEdad.setForeground(Color.RED);
        formPanel.add(lblErrorEdad, gbc);
        gbc.gridx = 0; gbc.gridy = 8; gbc.weightx = 0; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(new JLabel("Género:"), gbc);
        gbc.gridx = 1; gbc.gridy = 8; gbc.weightx = 1.0;
        cmbGeneroPaciente = new JComboBox<>(new String[]{"Masculino", "Femenino", "Otro"});
        formPanel.add(cmbGeneroPaciente, gbc);
        gbc.gridx = 0; gbc.gridy = 9; gbc.weightx = 0; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(new JLabel("Teléfono:"), gbc);
        gbc.gridx = 1; gbc.gridy = 9; gbc.weightx = 1.0;
        txtTelefonoPaciente = new JTextField(10);
        formPanel.add(txtTelefonoPaciente, gbc);
        gbc.gridy = 10; gbc.gridx = 1;
        lblErrorTelefono = new JLabel(" ");
        lblErrorTelefono.setForeground(Color.RED);
        formPanel.add(lblErrorTelefono, gbc);
        gbc.gridx = 0; gbc.gridy = 11; gbc.weightx = 0; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(new JLabel("Dirección:"), gbc);
        gbc.gridx = 1; gbc.gridy = 11; gbc.weightx = 1.0;
        txtDireccionPaciente = new JTextField(10);
        formPanel.add(txtDireccionPaciente, gbc);
        gbc.gridx = 0; gbc.gridy = 12; gbc.weightx = 0; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(new JLabel("Fecha de Nacimiento (DDMMYY):"), gbc);
        gbc.gridx = 1; gbc.gridy = 12; gbc.weightx = 1.0;
        try {
            MaskFormatter formatter = new MaskFormatter("##/##/##");
            formatter.setPlaceholderCharacter(' ');
            txtFechaNacPaciente = new JFormattedTextField(formatter);
        } catch (java.text.ParseException e) {
            txtFechaNacPaciente = new JFormattedTextField();
            e.printStackTrace();
        }
        formPanel.add(txtFechaNacPaciente, gbc);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        JButton btnGuardar = new JButton("Guardar Paciente");
        btnGuardar.addActionListener(e -> guardarPaciente());
        JButton btnLimpiar = new JButton("Limpiar");
        btnLimpiar.addActionListener(e -> limpiarCamposPaciente());
        buttonPanel.add(btnGuardar);
        buttonPanel.add(btnLimpiar);
        panel.add(formPanel, BorderLayout.NORTH);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        String[] columnas = {"DNI", "Nombre", "Apellido", "Edad", "Género", "Teléfono"};
        modeloPacientes = new DefaultTableModel(null, columnas);
        JTable tablaPacientes = new JTable(modeloPacientes);
        JScrollPane scrollPane = new JScrollPane(tablaPacientes);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private void guardarPaciente() {
        limpiarMensajesError();
        String dni = txtDniPaciente.getText().trim();
        String nombre = txtNombrePaciente.getText().trim();
        String apellido = txtApellidoPaciente.getText().trim();
        String edadStr = txtEdadPaciente.getText().trim();
        String genero = (String) cmbGeneroPaciente.getSelectedItem();
        String telefono = txtTelefonoPaciente.getText().trim();
        String direccion = txtDireccionPaciente.getText().trim();
        String fechaNacimiento = txtFechaNacPaciente.getText().replace("/", "").trim();
        boolean esValido = true;
        int edad = 0; 
        
        if (fechaNacimiento.isEmpty() || fechaNacimiento.contains(" ")) {
            JOptionPane.showMessageDialog(this, "La fecha de nacimiento es obligatoria y debe estar completa.", "Error", JOptionPane.ERROR_MESSAGE);
            esValido = false;
        }
        if (dni.length() != 8 || !dni.matches("\\d{8}")) {
            lblErrorDni.setText("El DNI debe tener 8 dígitos numéricos.");
            esValido = false;
        }
        if (nombre.isEmpty() || !nombre.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]{2,50}$")) {
            lblErrorNombre.setText("El nombre debe contener solo letras (2-50 caracteres).");
            esValido = false;
        }
        if (apellido.isEmpty() || !apellido.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]{2,50}$")) {
            lblErrorApellido.setText("El apellido debe contener solo letras (2-50 caracteres).");
            esValido = false;
        }
        if (!edadStr.isEmpty()) {
            try {
                if (edadStr.length() > 3) {
                    lblErrorEdad.setText("La edad no debe exceder los 3 dígitos.");
                    esValido = false;
                }
                edad = Integer.parseInt(edadStr);
                if (edad <= 0 || edad > 150) {
                    lblErrorEdad.setText("La edad debe ser un número válido.");
                    esValido = false;
                }
            } catch (NumberFormatException ex) {
                lblErrorEdad.setText("La edad debe ser un número.");
                esValido = false;
            }
        } else {
            lblErrorEdad.setText("La edad es obligatoria.");
            esValido = false;
        }
        if (!telefono.matches("\\d{9}")) {
            lblErrorTelefono.setText("El teléfono debe tener exactamente 9 dígitos.");
            esValido = false;
        }
        if (!esValido) {
            return;
        }
        Paciente nuevoPaciente = new Paciente(dni, nombre, apellido, edad, genero, telefono, direccion, fechaNacimiento);
        boolean guardado = gestorPacientes.agregarPaciente(nuevoPaciente);
        if (guardado) {
            JOptionPane.showMessageDialog(this, "Paciente guardado con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            cargarDatosPacientes();
            limpiarCamposPaciente();
        } else {
            JOptionPane.showMessageDialog(this, "El DNI del paciente ya existe.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void limpiarMensajesError() {
        lblErrorDni.setText(" ");
        lblErrorNombre.setText(" ");
        lblErrorApellido.setText(" ");
        lblErrorEdad.setText(" ");
        lblErrorTelefono.setText(" ");
    }
    
    private void limpiarCamposPaciente() {
        txtDniPaciente.setText("");
        txtNombrePaciente.setText("");
        txtApellidoPaciente.setText("");
        txtEdadPaciente.setText("");
        txtTelefonoPaciente.setText("");
        txtDireccionPaciente.setText("");
        txtFechaNacPaciente.setText("");
        cmbGeneroPaciente.setSelectedIndex(0);
    }
    
    private void cargarDatosPacientes() {
        modeloPacientes.setRowCount(0);
        List<Paciente> pacientes = gestorPacientes.listarPacientes();
        for (Paciente p : pacientes) {
            Object[] row = {p.getDni(), p.getNombre(), p.getApellido(), p.getEdad(), p.getGenero(), p.getTelefono()};
            modeloPacientes.addRow(row);
        }
    }
    
    private JPanel crearPanelHistorias() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JPanel formPanel = new JPanel(new BorderLayout(10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Registro de Historia Clínica"));
        JPanel topPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        topPanel.add(new JLabel("DNI del Paciente:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        txtDniHistoria = new JTextField();
        topPanel.add(txtDniHistoria, gbc);
        gbc.gridy = 1; gbc.gridx = 1; gbc.weightx = 1.0; gbc.gridwidth = 1;
        lblErrorDniHistoria = new JLabel(" ");
        lblErrorDniHistoria.setForeground(Color.RED);
        topPanel.add(lblErrorDniHistoria, gbc);
        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.WEST;
        topPanel.add(new JLabel("Fecha:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 1.0;
        try {
            MaskFormatter formatter = new MaskFormatter("##/##/####");
            formatter.setPlaceholderCharacter(' ');
            txtFechaHistoria = new JFormattedTextField(formatter);
        } catch (java.text.ParseException e) {
            txtFechaHistoria = new JFormattedTextField();
            e.printStackTrace();
        }
        topPanel.add(txtFechaHistoria, gbc);
        gbc.gridy = 3; gbc.gridx = 1; gbc.weightx = 1.0; gbc.gridwidth = 1;
        lblErrorFechaHistoria = new JLabel(" ");
        lblErrorFechaHistoria.setForeground(Color.RED);
        topPanel.add(lblErrorFechaHistoria, gbc);
        formPanel.add(topPanel, BorderLayout.NORTH);
        JPanel centerPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        centerPanel.add(new JLabel("Diagnóstico:"));
        txtDiagnosticoHistoria = new JTextArea(3, 20);
        centerPanel.add(new JScrollPane(txtDiagnosticoHistoria));
        centerPanel.add(new JLabel("Tratamiento:"));
        txtTratamientoHistoria = new JTextArea(3, 20);
        centerPanel.add(new JScrollPane(txtTratamientoHistoria));
        centerPanel.add(new JLabel("Medicamentos:"));
        txtMedicamentosHistoria = new JTextArea(3, 20);
        centerPanel.add(new JScrollPane(txtMedicamentosHistoria));
        centerPanel.add(new JLabel("Resultados de Análisis:"));
        txtResultadosAnalisisHistoria = new JTextArea(3, 20);
        centerPanel.add(new JScrollPane(txtResultadosAnalisisHistoria));
        formPanel.add(centerPanel, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        JButton btnGuardar = new JButton("Guardar Historia Clínica");
        btnGuardar.addActionListener(e -> guardarHistoriaClinica());
        JButton btnLimpiar = new JButton("Limpiar");
        btnLimpiar.addActionListener(e -> limpiarCamposHistoria());
        buttonPanel.add(btnGuardar);
        buttonPanel.add(btnLimpiar);
        formPanel.add(buttonPanel, BorderLayout.SOUTH);
        panel.add(formPanel, BorderLayout.NORTH);
        String[] columnas = {"ID", "DNI Paciente", "Fecha", "Diagnóstico", "Tratamiento"};
        modeloHistorias = new DefaultTableModel(null, columnas);
        JTable tablaHistorias = new JTable(modeloHistorias);
        JScrollPane scrollPane = new JScrollPane(tablaHistorias);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private void guardarHistoriaClinica() {
        lblErrorDniHistoria.setText(" ");
        lblErrorFechaHistoria.setText(" ");
        String dniPaciente = txtDniHistoria.getText().trim();
        String fecha = txtFechaHistoria.getText().trim();
        String diagnostico = txtDiagnosticoHistoria.getText();
        String tratamiento = txtTratamientoHistoria.getText();
        String medicamentos = txtMedicamentosHistoria.getText();
        String resultadosAnalisis = txtResultadosAnalisisHistoria.getText();
        boolean esValido = true;
        if (dniPaciente.length() != 8 || !dniPaciente.matches("\\d{8}")) {
            lblErrorDniHistoria.setText("El DNI debe tener 8 dígitos numéricos.");
            esValido = false;
        }
        if (fecha.isEmpty() || fecha.contains(" ")) {
            lblErrorFechaHistoria.setText("Formato de fecha incompleto.");
            esValido = false;
        }
        if (dniPaciente.isEmpty() || diagnostico.isEmpty()) {
            JOptionPane.showMessageDialog(this, "DNI, fecha y diagnóstico son obligatorios.", "Error", JOptionPane.ERROR_MESSAGE);
            esValido = false;
        }
        if (!esValido) {
            return;
        }
        if (gestorPacientes.buscarPaciente(dniPaciente) == null) {
            JOptionPane.showMessageDialog(this, "El paciente con ese DNI no existe.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        HistoriaClinica nuevaHistoria = new HistoriaClinica(0, dniPaciente, fecha, diagnostico, tratamiento, medicamentos, resultadosAnalisis);
        gestorHistorias.agregarHistoriaClinica(nuevaHistoria);
        JOptionPane.showMessageDialog(this, "Historia clínica guardada con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        cargarDatosHistorias();
        limpiarCamposHistoria();
    }
    
    private void limpiarCamposHistoria() {
        txtDniHistoria.setText("");
        txtFechaHistoria.setText("");
        txtDiagnosticoHistoria.setText("");
        txtTratamientoHistoria.setText("");
        txtMedicamentosHistoria.setText("");
        txtResultadosAnalisisHistoria.setText("");
    }
    
    private void cargarDatosHistorias() {
        modeloHistorias.setRowCount(0);
        List<HistoriaClinica> historias = gestorHistorias.listarHistorias();
        for (HistoriaClinica hc : historias) {
            Object[] row = {hc.getId(), hc.getDniPaciente(), hc.getFecha(), hc.getDiagnostico(), hc.getTratamiento()};
            modeloHistorias.addRow(row);
        }
    }
    
    private JPanel crearPanelReportes() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        areaReportes = new JTextArea();
        areaReportes.setEditable(false);
        areaReportes.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(areaReportes);
        panel.add(scrollPane, BorderLayout.CENTER);
        JButton btnGenerarReporte = new JButton("Generar Reporte");
        btnGenerarReporte.addActionListener(e -> generarReporte());
        panel.add(btnGenerarReporte, BorderLayout.SOUTH);
        generarReporte();
        return panel;
    }
    
    private void generarReporte() {
        StringBuilder reporte = new StringBuilder();
        reporte.append("REPORTE DEL SISTEMA\n");
        reporte.append("====================\n\n");
        int totalPacientes = gestorPacientes.listarPacientes().size();
        reporte.append("Total de pacientes registrados: ").append(totalPacientes).append("\n");
        int totalHistorias = gestorHistorias.listarHistorias().size();
        reporte.append("Total de historias clínicas: ").append(totalHistorias).append("\n\n");
        Map<String, Long> pacientesPorGenero = new HashMap<>();
        gestorPacientes.listarPacientes().forEach(p ->
            pacientesPorGenero.merge(p.getGenero(), 1L, Long::sum)
        );
        reporte.append("Pacientes por género:\n");
        pacientesPorGenero.forEach((genero, count) ->
            reporte.append("   - ").append(genero).append(": ").append(count).append("\n")
        );
        reporte.append("\n");
        Map<String, Long> diagnosticos = new HashMap<>();
        gestorHistorias.listarHistorias().forEach(h ->
            h.getDiagnostico().lines().forEach(line ->
                diagnosticos.merge(line.trim(), 1L, Long::sum)
            )
        );
        reporte.append("Diagnósticos más comunes:\n");
        if (diagnosticos.isEmpty()) {
            reporte.append("   - No hay datos disponibles.\n");
        } else {
            diagnosticos.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(5)
                .forEach(entry ->
                    reporte.append("   - ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n")
                );
        }
        areaReportes.setText(reporte.toString());
    }
    
    private JPanel crearPanelVistaPaciente() {
        JPanel panel = new JPanel(new BorderLayout(20, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel panelIzquierdo = new JPanel(new BorderLayout());
        panelIzquierdo.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "Citas por Fecha"));
        
        listaCitas = new JList<>();
        listaCitas.setFixedCellHeight(30);
        listaCitas.setFont(new Font("Arial", Font.PLAIN, 14));
        listaCitas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        listaCitas.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int index = listaCitas.getSelectedIndex();
                if (index != -1) {
                    cargarDetalleHistoria(index);
                }
            }
        });
        
        panelIzquierdo.add(new JScrollPane(listaCitas), BorderLayout.CENTER);
        
        panelDetalleHistoria = new JPanel(new GridBagLayout());
        panelDetalleHistoria.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "Detalle de Historia Clínica"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST; gbc.gridwidth = 2;
        panelDetalleHistoria.add(new JLabel("Diagnóstico:"), gbc);
        gbc.gridy = 1;
        lblDiagnostico = new JLabel();
        panelDetalleHistoria.add(lblDiagnostico, gbc);
        
        gbc.gridy = 2;
        panelDetalleHistoria.add(new JLabel("Tratamiento:"), gbc);
        gbc.gridy = 3;
        lblTratamiento = new JLabel();
        panelDetalleHistoria.add(lblTratamiento, gbc);
        
        gbc.gridy = 4;
        panelDetalleHistoria.add(new JLabel("Medicamentos:"), gbc);
        gbc.gridy = 5;
        lblMedicamentos = new JLabel();
        panelDetalleHistoria.add(lblMedicamentos, gbc);
        
        gbc.gridy = 6;
        panelDetalleHistoria.add(new JLabel("Resultados de Análisis:"), gbc);
        gbc.gridy = 7;
        lblResultadosAnalisis = new JLabel();
        panelDetalleHistoria.add(lblResultadosAnalisis, gbc);

        JButton btnVolver = new JButton("Volver a Citas");
        btnVolver.addActionListener(e -> limpiarDetalleHistoria());
        gbc.gridx = 0; gbc.gridy = 8; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        panelDetalleHistoria.add(btnVolver, gbc);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelIzquierdo, panelDetalleHistoria);
        splitPane.setDividerLocation(300);

        panel.add(splitPane, BorderLayout.CENTER);
        
        return panel;
    }

    private void cargarListaCitas(String dni) {
        DefaultListModel<String> modeloLista = new DefaultListModel<>();
        List<HistoriaClinica> historias = gestorHistorias.buscarHistoriasPorPaciente(dni);
        
        if (historias.isEmpty()) {
            modeloLista.addElement("No hay historial clínico disponible.");
        } else {
            for (HistoriaClinica hc : historias) {
                modeloLista.addElement("Cita del " + hc.getFecha());
            }
        }
        
        listaCitas.setModel(modeloLista);
        limpiarDetalleHistoria();
    }

    private void cargarDetalleHistoria(int index) {
        List<HistoriaClinica> historias = gestorHistorias.buscarHistoriasPorPaciente(usuarioActual.getDni());
        
        if (index >= 0 && index < historias.size()) {
            HistoriaClinica historiaSeleccionada = historias.get(index);
            
            lblDiagnostico.setText("<html>" + historiaSeleccionada.getDiagnostico().replaceAll("\\n", "<br>") + "</html>");
            lblTratamiento.setText("<html>" + historiaSeleccionada.getTratamiento().replaceAll("\\n", "<br>") + "</html>");
            lblMedicamentos.setText("<html>" + historiaSeleccionada.getMedicamentos().replaceAll("\\n", "<br>") + "</html>");
            lblResultadosAnalisis.setText("<html>" + historiaSeleccionada.getResultadosAnalisis().replaceAll("\\n", "<br>") + "</html>");
        }
    }
    
    private void limpiarDetalleHistoria() {
        lblDiagnostico.setText("");
        lblTratamiento.setText("");
        lblMedicamentos.setText("");
        lblResultadosAnalisis.setText("");
        listaCitas.clearSelection();
    }
}

// Clase de modelo Paciente
class Paciente {
    private String dni;
    private String nombre;
    private String apellido;
    private int edad;
    private String genero;
    private String telefono;
    private String direccion;
    private String fechaNacimiento;
    public Paciente(String dni, String nombre, String apellido, int edad, String genero, String telefono, String direccion, String fechaNacimiento) {
        this.dni = dni;
        this.nombre = nombre;
        this.apellido = apellido;
        this.edad = edad;
        this.genero = genero;
        this.telefono = telefono;
        this.direccion = direccion;
        this.fechaNacimiento = fechaNacimiento;
    }
    public String getDni() { return dni; }
    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public int getEdad() { return edad; }
    public String getGenero() { return genero; }
    public String getTelefono() { return telefono; }
    public String getDireccion() { return direccion; }
    public String getFechaNacimiento() { return fechaNacimiento; }
}

// Clase de modelo HistoriaClinica
class HistoriaClinica {
    private int id;
    private String dniPaciente;
    private String fecha;
    private String diagnostico;
    private String tratamiento;
    private String medicamentos;
    private String resultadosAnalisis;
    public HistoriaClinica(int id, String dniPaciente, String fecha, String diagnostico, String tratamiento, String medicamentos, String resultadosAnalisis) {
        this.id = id;
        this.dniPaciente = dniPaciente;
        this.fecha = fecha;
        this.diagnostico = diagnostico;
        this.tratamiento = tratamiento;
        this.medicamentos = medicamentos;
        this.resultadosAnalisis = resultadosAnalisis;
    }
    public int getId() { return id; }
    public String getDniPaciente() { return dniPaciente; }
    public String getFecha() { return fecha; }
    public String getDiagnostico() { return diagnostico; }
    public String getTratamiento() { return tratamiento; }
    public String getMedicamentos() { return medicamentos; }
    public String getResultadosAnalisis() { return resultadosAnalisis; }
}

// Clase de modelo Usuario
class Usuario {
    private String dni;
    private String password;
    private String rol;
    public Usuario(String dni, String password, String rol) {
        this.dni = dni;
        this.password = password;
        this.rol = rol;
    }
    public String getDni() { return dni; }
    public String getPassword() { return password; }
    public String getRol() { return rol; }
}

// Clase GestorPacientes
class GestorPacientes {
    private List<Paciente> pacientes;
    public GestorPacientes() {
        this.pacientes = new ArrayList<>();
    }
    public boolean agregarPaciente(Paciente paciente) {
        if (buscarPaciente(paciente.getDni()) != null) {
            return false;
        }
        return pacientes.add(paciente);
    }
    public Paciente buscarPaciente(String dni) {
        for (Paciente p : pacientes) {
            if (p.getDni().equals(dni)) {
                return p;
            }
        }
        return null;
    }
    public List<Paciente> listarPacientes() {
        return new ArrayList<>(pacientes);
    }
}

// Clase GestorHistoriasClinicas
class GestorHistoriasClinicas {
    private List<HistoriaClinica> historias;
    private int nextId = 1;
    public GestorHistoriasClinicas() {
        this.historias = new ArrayList<>();
    }
    public void agregarHistoriaClinica(HistoriaClinica historia) {
        historia = new HistoriaClinica(nextId++, historia.getDniPaciente(), historia.getFecha(), historia.getDiagnostico(), historia.getTratamiento(), historia.getMedicamentos(), historia.getResultadosAnalisis());
        historias.add(historia);
    }
    public List<HistoriaClinica> listarHistorias() {
        return new ArrayList<>(historias);
    }
    public List<HistoriaClinica> buscarHistoriasPorPaciente(String dni) {
        List<HistoriaClinica> resultados = new ArrayList<>();
        for (HistoriaClinica hc : historias) {
            if (hc.getDniPaciente().equals(dni)) {
                resultados.add(hc);
            }
        }
        return resultados;
    }
}

// Clase SistemaAutenticacion
class SistemaAutenticacion {
    private List<Usuario> usuariosSistema;
    private GestorPacientes gestorPacientes;

    // Se pasa la instancia de GestorPacientes para poder buscar a los pacientes
    public SistemaAutenticacion(GestorPacientes gestorPacientes) {
        this.gestorPacientes = gestorPacientes;
        this.usuariosSistema = new ArrayList<>();
        // Usuarios del sistema predefinidos (no son pacientes)
        usuariosSistema.add(new Usuario("69752454", "220697", "ADMIN"));
        usuariosSistema.add(new Usuario("58963214", "150385", "MEDICO"));
        usuariosSistema.add(new Usuario("47125896", "081190", "RECEPCION"));
    }
    public Usuario autenticarUsuario(String dni, String password) {
        // 1. Intentar autenticar como usuario del sistema (ADMIN, MEDICO, RECEPCION)
        for (Usuario u : usuariosSistema) {
            if (u.getDni().equals(dni) && u.getPassword().equals(password)) {
                return u;
            }
        }
        // 2. Si no es un usuario del sistema, intentar autenticar como paciente
        Paciente paciente = gestorPacientes.buscarPaciente(dni);
        if (paciente != null && paciente.getFechaNacimiento().equals(password)) {
            return new Usuario(paciente.getDni(), paciente.getFechaNacimiento(), "PACIENTE");
        }
        return null;
    }
}