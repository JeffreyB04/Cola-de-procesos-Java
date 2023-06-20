import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

class Proceso implements Runnable {
    private String nombre;
    private int duracion;
    private int prioridad;
    private JTable table;

    public Proceso(String nombre, int duracion, int prioridad, JTable table) {
        this.nombre = nombre;
        this.duracion = duracion;
        this.prioridad = prioridad;
        this.table = table;
    }

    public String getNombre() {
        return nombre;
    }

    public int getDuracion() {
        return duracion;
    }
    public void setDuracion(int duracion) {
        this.duracion = duracion;
    }


    public int getPrioridad() {
        return prioridad;
    }

    public void run() {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        int rowIndex = Integer.parseInt(Thread.currentThread().getName());

        while (duracion > 0) {
            model.setValueAt(duracion, rowIndex, 2);
            duracion--;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        model.setValueAt("Finalizado", rowIndex, 3);
    }
}

class ColaProcesos {
    private List<Proceso> procesos;

    public ColaProcesos() {
        procesos = new ArrayList<>();
    }

    public void agregarProceso(Proceso proceso) {
        procesos.add(proceso);
    }

    public boolean estaVacia() {
        return procesos.isEmpty();
    }

    public Proceso obtenerProceso() {
        return procesos.get(0);
    }

    public Proceso obtenerProcesoConMayorPrioridad() {
        Proceso procesoMayorPrioridad = procesos.get(0);
        for (Proceso proceso : procesos) {
            if (proceso.getPrioridad() > procesoMayorPrioridad.getPrioridad()) {
                procesoMayorPrioridad = proceso;
            }
        }
        return procesoMayorPrioridad;
    }

    public Proceso obtenerProcesoConMenorDuracion() {
        Proceso procesoMenorDuracion = procesos.get(0);
        for (Proceso proceso : procesos) {
            if (proceso.getDuracion() < procesoMenorDuracion.getDuracion()) {
                procesoMenorDuracion = proceso;
            }
        }
        return procesoMenorDuracion;
    }

    public void ordenarPorDuracion() {
        procesos.sort((p1, p2) -> p1.getDuracion() - p2.getDuracion());
    }

    public void ordenarPorPrioridad() {
        procesos.sort((p1, p2) -> p2.getPrioridad() - p1.getPrioridad());
    }

    public void remove(Proceso proceso) {
        procesos.remove(proceso);
    }

    public int size() {
        return procesos.size();
    }

    public Proceso get(int index) {
        return procesos.get(index);
    }
}

class PlanificadorProcesosGUI extends JFrame {
    private JComboBox<String> algoritmoComboBox;
    private JTextField cantidadProcesosTextField;
    private JButton iniciarButton;
    private JTable procesosTable;
    private DefaultTableModel tableModel;
    private ColaProcesos colaProcesos;

    public PlanificadorProcesosGUI() {
        setTitle("Planificador de Procesos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        initComponents();
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initComponents() {
        JPanel optionsPanel = new JPanel(new FlowLayout());
        algoritmoComboBox = new JComboBox<>(new String[] {
                "FCFS (en orden de llegada)",
                "SJF (Planificación con selección del trabajo más corto)",
                "Round Robin con FIFO",
                "Round Robin con prioridad",
                "SRTF (Shortest Remaining Time First)"
        });
        algoritmoComboBox.setPreferredSize(new Dimension(300, 25));
        cantidadProcesosTextField = new JTextField(10);
        iniciarButton = new JButton("Iniciar");
        optionsPanel.add(new JLabel("Algoritmo:"));
        optionsPanel.add(algoritmoComboBox);
        optionsPanel.add(new JLabel("Cantidad de Procesos:"));
        optionsPanel.add(cantidadProcesosTextField);
        optionsPanel.add(iniciarButton);

        tableModel = new DefaultTableModel(new Object[][]{}, new String[] {
                "Proceso", "Duración", "Estado"
        });
        procesosTable = new JTable(tableModel);
        procesosTable.setRowHeight(30);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        procesosTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        procesosTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        procesosTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        JScrollPane scrollPane = new JScrollPane(procesosTable);

        add(optionsPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        iniciarButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                iniciarButton.setEnabled(false);
                algoritmoComboBox.setEnabled(false);
                cantidadProcesosTextField.setEnabled(false);

                String algoritmo = algoritmoComboBox.getSelectedItem().toString();
                int cantidadProcesos = Integer.parseInt(cantidadProcesosTextField.getText());

                if (cantidadProcesos <= 0) {
                    JOptionPane.showMessageDialog(PlanificadorProcesosGUI.this,
                            "La cantidad de procesos debe ser mayor a cero.", "Error", JOptionPane.ERROR_MESSAGE);
                    iniciarButton.setEnabled(true);
                    algoritmoComboBox.setEnabled(true);
                    cantidadProcesosTextField.setEnabled(true);
                    return;
                }

                colaProcesos = crearColaProcesos(cantidadProcesos);

                switch (algoritmo) {
                    case "FCFS (en orden de llegada)":
                        planificarFCFS();
                        break;
                    case "SJF (Planificación con selección del trabajo más corto)":
                        planificarSJF();
                        break;
                    case "Round Robin con FIFO":
                        String quantumStr = JOptionPane.showInputDialog(PlanificadorProcesosGUI.this,
                                "Ingresa el valor del quantum:", "Quantum", JOptionPane.QUESTION_MESSAGE);
                        int quantum = Integer.parseInt(quantumStr);
                        planificarRoundRobinFIFO(quantum);
                        break;
                    case "Round Robin con prioridad":
                        String quantumStr2 = JOptionPane.showInputDialog(PlanificadorProcesosGUI.this,
                                "Ingresa el valor del quantum:", "Quantum", JOptionPane.QUESTION_MESSAGE);
                        int quantum2 = Integer.parseInt(quantumStr2);
                        planificarRoundRobinPrioridad(quantum2);
                        break;
                    case "SRTF (Shortest Remaining Time First)":
                        planificarSRTF();
                        break;
                }
            }
        });
    }

    private ColaProcesos crearColaProcesos(int cantidadProcesos) {
        ColaProcesos colaProcesos = new ColaProcesos();
        for (int i = 1; i <= cantidadProcesos; i++) {
            String nombre = "P" + i;
            int duracion = (int) (Math.random() * 10) + 1;
            int prioridad = (int) (Math.random() * 5) + 1;
            colaProcesos.agregarProceso(new Proceso(nombre, duracion, prioridad, procesosTable));
        }
        return colaProcesos;
    }

    private void planificarFCFS() {
        while (!colaProcesos.estaVacia()) {
            Proceso proceso = colaProcesos.obtenerProceso();
            Thread thread = new Thread(proceso);
            thread.setName(String.valueOf(colaProcesos.size() - 1));
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            colaProcesos.remove(proceso);
        }
        mostrarMensaje("Todos los procesos han finalizado.");
        reiniciar();
    }

    private void planificarSJF() {
        colaProcesos.ordenarPorDuracion();

        while (!colaProcesos.estaVacia()) {
            Proceso proceso = colaProcesos.obtenerProceso();
            Thread thread = new Thread(proceso);
            thread.setName(String.valueOf(colaProcesos.size() - 1));
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            colaProcesos.remove(proceso);
        }
        mostrarMensaje("Todos los procesos han finalizado.");
        reiniciar();
    }
    private void planificarRoundRobinFIFO(int quantum) {
        while (!colaProcesos.estaVacia()) {
            Proceso proceso = colaProcesos.obtenerProceso();
            Thread thread = new Thread(proceso);
            thread.setName(String.valueOf(colaProcesos.size() - 1));
            thread.start();

            try {
                Thread.sleep(quantum * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (proceso.getDuracion() > 0) {
                proceso.setDuracion(proceso.getDuracion() - quantum);
                colaProcesos.remove(proceso);
                colaProcesos.agregarProceso(proceso);
            } else {
                colaProcesos.remove(proceso);
            }
        }
        mostrarMensaje("Todos los procesos han finalizado.");
        reiniciar();
    }

    private void planificarRoundRobinPrioridad(int quantum) {
        colaProcesos.ordenarPorPrioridad();

        while (!colaProcesos.estaVacia()) {
            Proceso proceso = colaProcesos.obtenerProceso();
            Thread thread = new Thread(proceso);
            thread.setName(String.valueOf(colaProcesos.size() - 1));
            thread.start();

            try {
                Thread.sleep(quantum * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (proceso.getDuracion() > 0) {
                proceso.setDuracion(proceso.getDuracion() - quantum);
                colaProcesos.remove(proceso);
                colaProcesos.agregarProceso(proceso);
            } else {
                colaProcesos.remove(proceso);
            }
        }
        mostrarMensaje("Todos los procesos han finalizado.");
        reiniciar();
    }


    private void planificarSRTF() {
        while (!colaProcesos.estaVacia()) {
            Proceso proceso = colaProcesos.obtenerProcesoConMenorDuracion();
            Thread thread = new Thread(proceso);
            thread.setName(String.valueOf(colaProcesos.size() - 1));
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            colaProcesos.remove(proceso);
        }
        mostrarMensaje("Todos los procesos han finalizado.");
        reiniciar();
    }

    private void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(PlanificadorProcesosGUI.this, mensaje, "Información", JOptionPane.INFORMATION_MESSAGE);
    }

    private void reiniciar() {
        iniciarButton.setEnabled(true);
        algoritmoComboBox.setEnabled(true);
        cantidadProcesosTextField.setEnabled(true);
        tableModel.setRowCount(0);
        colaProcesos = null;
    }
}

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new PlanificadorProcesosGUI();
            }
        });
    }
}
