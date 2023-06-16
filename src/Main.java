import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class Proceso implements Runnable {
    private String nombre;
    private int duracion;
    private int prioridad;

    public Proceso(String nombre, int duracion, int prioridad) {
        this.nombre = nombre;
        this.duracion = duracion;
        this.prioridad = prioridad;
    }

    public String getNombre() {
        return nombre;
    }

    public int getDuracion() {
        return duracion;
    }

    public int getPrioridad() {
        return prioridad;
    }

    public void run() {
        System.out.println("Ejecutando proceso " + nombre + " (duración = " + duracion
                + ", prioridad = " + prioridad + ")");

        while (duracion > 0) {
            duracion--;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Proceso " + nombre + " finalizado.");
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

class PlanificadorProcesos {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("--- Planificador de Procesos ---");
        System.out.println("Selecciona el algoritmo de planificación:");
        System.out.println("1. FCFS (en orden de llegada)");
        System.out.println("2. SJF (Planificación con selección del trabajo más corto)");
        System.out.println("3. Round Robin con FIFO");
        System.out.println("4. Round Robin con prioridad");
        System.out.println("5. SRTF (Shortest Remaining Time First)");
        System.out.print("Opción: ");
        int opcion = scanner.nextInt();

        ColaProcesos colaProcesos = crearColaProcesos();

        switch (opcion) {
            case 1:
                planificarFCFS(colaProcesos);
                break;
            case 2:
                planificarSJF(colaProcesos);
                break;
            case 3:
                System.out.print("Ingresa el valor del quantum: ");
                int quantum = scanner.nextInt();
                planificarRoundRobin(colaProcesos, quantum);
                break;
            case 4:
                System.out.print("Ingresa el valor del quantum: ");
                quantum = scanner.nextInt();
                planificarRoundRobinConPrioridad(colaProcesos, quantum);
                break;
            case 5:
                planificarSRTF(colaProcesos);
                break;
            default:
                System.out.println("Opción inválida.");
        }
    }

    public static ColaProcesos crearColaProcesos() {
        ColaProcesos colaProcesos = new ColaProcesos();
        colaProcesos.agregarProceso(new Proceso("P1", 5, 2));
        colaProcesos.agregarProceso(new Proceso("P2", 3, 1));
        colaProcesos.agregarProceso(new Proceso("P3", 8, 3));
        colaProcesos.agregarProceso(new Proceso("P4", 2, 4));
        colaProcesos.agregarProceso(new Proceso("P5", 6, 2));
        return colaProcesos;
    }

    public static void planificarFCFS(ColaProcesos colaProcesos) {
        while (!colaProcesos.estaVacia()) {
            Proceso proceso = colaProcesos.obtenerProceso();
            Thread thread = new Thread(proceso);
            thread.start();

            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            colaProcesos.remove(proceso);
        }
    }

    public static void planificarSJF(ColaProcesos colaProcesos) {
        colaProcesos.ordenarPorDuracion();

        while (!colaProcesos.estaVacia()) {
            Proceso proceso = colaProcesos.obtenerProceso();
            Thread thread = new Thread(proceso);
            thread.start();

            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            colaProcesos.remove(proceso);
        }
    }

    public static void planificarRoundRobin(ColaProcesos colaProcesos, int quantum) {
        while (!colaProcesos.estaVacia()) {
            Proceso proceso = colaProcesos.obtenerProceso();
            Thread thread = new Thread(proceso);
            thread.start();

            try {
                thread.join(quantum * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (proceso.getDuracion() > 0) {
                colaProcesos.remove(proceso);
                colaProcesos.agregarProceso(proceso);
            }
        }
    }

    public static void planificarRoundRobinConPrioridad(ColaProcesos colaProcesos, int quantum) {
        colaProcesos.ordenarPorPrioridad();

        while (!colaProcesos.estaVacia()) {
            Proceso proceso = colaProcesos.obtenerProceso();
            Thread thread = new Thread(proceso);
            thread.start();

            try {
                thread.join(quantum * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (proceso.getDuracion() > 0) {
                colaProcesos.remove(proceso);
                colaProcesos.agregarProceso(proceso);
            }
        }
    }

    public static void planificarSRTF(ColaProcesos colaProcesos) {
        while (!colaProcesos.estaVacia()) {
            Proceso proceso = colaProcesos.obtenerProcesoConMenorDuracion();
            Thread thread = new Thread(proceso);
            thread.start();

            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            colaProcesos.remove(proceso);
        }
    }
}
