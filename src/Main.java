import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
class Proceso implements Runnable {
    private String nombre;
    private int duracion;
    private int prioridad;
    private JTextArea outputTextArea;

    public Proceso(String nombre, int duracion, int prioridad, JTextArea outputTextArea) {
        this.nombre = nombre;
        this.duracion = duracion;
        this.prioridad = prioridad;
        this.outputTextArea = outputTextArea;
    }

    public String getNombre() {
        return nombre;
    }

    public void setDuracion(int duracion) {
        this.duracion = duracion;
    }

    public int getDuracion() {
        return duracion;
    }

    public int getPrioridad() {
        return prioridad;
    }

    public void run() {
        outputTextArea.append("Ejecutando proceso " + nombre + " (duración = " + duracion
                + ", prioridad = " + prioridad + ")\n");

        while (duracion > 0) {
            outputTextArea.append("Proceso " + nombre + ": " + duracion + " segundos restantes.\n");
            duracion--;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        outputTextArea.append("Proceso " + nombre + " finalizado.\n");
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

    public void eliminarProceso(Proceso proceso) {
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
    private JLabel numProcesosLabel;
    private JTextField numProcesosTextField;
    private JButton crearColaButton;
    private JLabel algoritmoLabel;
    private JComboBox<String> algoritmoComboBox;
    private JLabel quantumLabel;
    private JTextField quantumTextField;
    private JButton planificarButton;
    private JTextArea outputTextArea;

    private ColaProcesos colaProcesos;

    public PlanificadorProcesosGUI() {
        setTitle("Planificador de Procesos");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(6, 2, 10, 10));

        numProcesosLabel = new JLabel("Número de procesos:");
        numProcesosTextField = new JTextField();
        crearColaButton = new JButton("Crear cola");
        algoritmoLabel = new JLabel("Algoritmo de planificación:");
        algoritmoComboBox = new JComboBox<>(new String[]{"FCFS", "SJF", "Round Robin", "Round Robin con prioridad", "SRTF"});
        quantumLabel = new JLabel("Quantum:");
        quantumTextField = new JTextField();
        planificarButton = new JButton("Planificar");
        outputTextArea = new JTextArea();

        outputTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputTextArea);

        crearColaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String numProcesosText = numProcesosTextField.getText();
                int numProcesos = Integer.parseInt(numProcesosText);
                colaProcesos = crearColaProcesos(numProcesos);
                outputTextArea.append("Cola de procesos creada.\n");
            }
        });

        planificarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (colaProcesos == null) {
                    outputTextArea.append("Primero debes crear una cola de procesos.\n");
                    return;
                }

                String algoritmo = algoritmoComboBox.getSelectedItem().toString();
                if (algoritmo.equals("FCFS")) {
                    planificarFCFS(colaProcesos);
                } else if (algoritmo.equals("SJF")) {
                    planificarSJF(colaProcesos);
                } else if (algoritmo.equals("Round Robin")) {
                    String quantumText = quantumTextField.getText();
                    int quantum = Integer.parseInt(quantumText);
                    planificarRoundRobin(colaProcesos, quantum);
                } else if (algoritmo.equals("Round Robin con prioridad")) {
                    String quantumText = quantumTextField.getText();
                    int quantum = Integer.parseInt(quantumText);
                    planificarRoundRobinConPrioridad(colaProcesos, quantum);
                } else if (algoritmo.equals("SRTF")) {
                    planificarSRTF(colaProcesos);
                }
            }
        });

        add(numProcesosLabel);
        add(numProcesosTextField);
        add(crearColaButton);
        add(new JLabel()); // Placeholder
        add(algoritmoLabel);
        add(algoritmoComboBox);
        add(quantumLabel);
        add(quantumTextField);
        add(planificarButton);
        add(new JLabel()); // Placeholder
        add(scrollPane);
    }

    public ColaProcesos crearColaProcesos(int numProcesos) {
        ColaProcesos colaProcesos = new ColaProcesos();

        for (int i = 1; i <= numProcesos; i++) {
            String nombre = JOptionPane.showInputDialog("Nombre del proceso " + i + ":");
            String duracionText = JOptionPane.showInputDialog("Duración del proceso " + i + ":");
            int duracion = Integer.parseInt(duracionText);
            String prioridadText = JOptionPane.showInputDialog("Prioridad del proceso " + i + ":");
            int prioridad = Integer.parseInt(prioridadText);

            Proceso proceso = new Proceso(nombre, duracion, prioridad, outputTextArea);
            colaProcesos.agregarProceso(proceso);
        }

        return colaProcesos;
    }

    public void planificarFCFS(ColaProcesos colaProcesos) {
        ExecutorService executorService = Executors.newFixedThreadPool(colaProcesos.size());

        for (int i = 0; i < colaProcesos.size(); i++) {
            Proceso proceso = colaProcesos.get(i);
            executorService.execute(proceso);
        }

        executorService.shutdown();
    }

    public void planificarSJF(ColaProcesos colaProcesos) {
        colaProcesos.ordenarPorDuracion();
        ExecutorService executorService = Executors.newFixedThreadPool(colaProcesos.size());

        for (int i = 0; i < colaProcesos.size(); i++) {
            Proceso proceso = colaProcesos.get(i);
            executorService.execute(proceso);
        }

        executorService.shutdown();
    }

    public void planificarRoundRobin(ColaProcesos colaProcesos, int quantum) {
        ExecutorService executorService = Executors.newFixedThreadPool(colaProcesos.size());
        int i = 0;

        while (!colaProcesos.estaVacia()) {
            Proceso proceso = colaProcesos.obtenerProceso();
            if (proceso.getDuracion() > quantum) {
                proceso.setDuracion(proceso.getDuracion() - quantum);
                executorService.execute(proceso);
                i = (i + 1) % colaProcesos.size();
            } else {
                executorService.execute(proceso);
                colaProcesos.eliminarProceso(proceso);
            }
        }

        executorService.shutdown();
    }

    public void planificarRoundRobinConPrioridad(ColaProcesos colaProcesos, int quantum) {
        colaProcesos.ordenarPorPrioridad();
        ExecutorService executorService = Executors.newFixedThreadPool(colaProcesos.size());
        int i = 0;

        while (!colaProcesos.estaVacia()) {
            Proceso proceso = colaProcesos.obtenerProceso();
            if (proceso.getDuracion() > quantum) {
                proceso.setDuracion(proceso.getDuracion() - quantum);
                executorService.execute(proceso);
                i = (i + 1) % colaProcesos.size();
            } else {
                executorService.execute(proceso);
                colaProcesos.eliminarProceso(proceso);
            }
        }

        executorService.shutdown();
    }

    public void planificarSRTF(ColaProcesos colaProcesos) {
        ExecutorService executorService = Executors.newFixedThreadPool(colaProcesos.size());

        while (!colaProcesos.estaVacia()) {
            Proceso proceso = colaProcesos.obtenerProcesoConMenorDuracion();
            executorService.execute(proceso);
            colaProcesos.eliminarProceso(proceso);
        }

        executorService.shutdown();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                PlanificadorProcesosGUI gui = new PlanificadorProcesosGUI();
                gui.setVisible(true);
            }
        });
    }
}
