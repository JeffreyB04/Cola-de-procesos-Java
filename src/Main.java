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
        System.out.println(" Ejecutando proceso " + nombre + " (duración = " + duracion
                + ", prioridad = " + prioridad + ")");

        while (duracion > 0) {
            System.out.println("\t Proceso " + nombre + ": " + duracion + " segundos restantes.");
            duracion--;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("\t Proceso " + nombre + " finalizado.");
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

class PlanificadorProcesos {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean continuar = true;

        while (continuar) {

            System.out.println("Selecciona el algoritmo de planificación:");
            System.out.println("1. FCFS (en orden de llegada)");
            System.out.println("2. SJF (Planificación con selección del trabajo más corto)");
            System.out.println("3. Round Robin con FIFO");
            System.out.println("4. Round Robin con prioridad");
            System.out.println("5. SRTF (Shortest Remaining Time First)");
            System.out.println("6. Salir");
            System.out.print("Opción: ");
            int opcion = scanner.nextInt();

            ColaProcesos colaProcesos= null;
            if(opcion!=6){
                System.out.println("--- Planificador de Procesos ---");
                System.out.print("Ingresa el número de procesos: ");
                int numProcesos = scanner.nextInt();
                colaProcesos = crearColaProcesos(numProcesos);
            }

            switch (opcion) {
                case 1:

                    System.out.println("|------|------|------|------|");
                    tiemposFCFS(colaProcesos);
                    tiempoEjecucionFCFS(colaProcesos);
                    System.out.println();
                    System.out.println("Simulacion de los procesos: ");
                    planificarFCFS(colaProcesos);
                    System.out.println("|------|------|------|------|");
                    break;
                case 2:
                    System.out.println("|------|------|------|------|");
                    tiemposSJF(colaProcesos);
                    tiempoEjecucionSJF(colaProcesos);
                    System.out.println();
                    System.out.println("Simulacion de los procesos: ");
                    planificarSJF(colaProcesos);
                    System.out.println("|------|------|------|------|");
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
                case 6:
                    continuar = false;
                    System.out.println("Saliendo del programa...");
                    break;
                default:
                    System.out.println("Opción inválida.");
            }
        }
        scanner.close();
    }

    public static ColaProcesos crearColaProcesos(int numProcesos) {
        ColaProcesos colaProcesos = new ColaProcesos();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Datos de los Procesos2");
        for (int i = 1; i <= numProcesos; i++) {
            System.out.println("\t Proceso #" + i);
            System.out.print("\t \t Nombre: ");
            String nombre = scanner.next();
            System.out.print("\t \t Duración: ");
            int duracion = scanner.nextInt();
            System.out.print("\t \t Prioridad: ");
            int prioridad = scanner.nextInt();
            scanner.nextLine(); // Consumir nueva línea

            colaProcesos.agregarProceso(new Proceso(nombre, duracion, prioridad));
        }

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

            colaProcesos.eliminarProceso(proceso);
        }
    }
    public static void tiempoEjecucionFCFS(ColaProcesos procesos) {
        int sum = 0;
        System.out.print("Tiempo total de ejecución: ");
        for (int i = 0; i < procesos.size(); i++) {
            Proceso proceso = procesos.get(i);
            if (i == 0) {
                System.out.print(proceso.getDuracion());
            } else {
                System.out.print("+" + proceso.getDuracion());
            }
            sum += proceso.getDuracion();
        }
        System.out.print(" = " + sum+ "\n");
    }
    public static void tiemposFCFS(ColaProcesos procesos) {
        int tiempoInicio = 0;
        int tiempoFinalizacion = 0;
        int tiempoEspera = 0;
        int[] gantt = new int[procesos.size() + 1]; // Aumentamos en 1 el tamaño del vector
        gantt[0] = 0;
        for (int i = 0; i < procesos.size(); i++) {
            Proceso proceso = procesos.get(i);
            if (i != 0) {
                tiempoInicio = tiempoFinalizacion;
            }
            tiempoFinalizacion += proceso.getDuracion();
            gantt[i + 1] = tiempoFinalizacion;

            System.out.println("Proceso " + proceso.getNombre() + ":");
            System.out.println("\t Tiempo de inicio: " + tiempoInicio);
            System.out.println("\t Tiempo de finalización: " + tiempoFinalizacion);
            System.out.println("\t Tiempo de espera: " + tiempoEspera);

            tiempoEspera = tiempoFinalizacion - tiempoInicio;

        }

        gantt[procesos.size()] = tiempoFinalizacion;
        System.out.println();
        System.out.print("Diagrama de Gantt: | ");
        for (int i = 0; i <= procesos.size(); i++) {
            System.out.print(gantt[i]+ " | ");
        }
        System.out.println();
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

            colaProcesos.eliminarProceso(proceso);
        }
    }
    public static void tiempoEjecucionSJF(ColaProcesos procesos) {
        procesos.ordenarPorDuracion();
        int sum = 0;
        System.out.print("Tiempo total de ejecución: ");
        for (int i = 0; i < procesos.size(); i++) {
            Proceso proceso = procesos.get(i);
            if (i == 0) {
                System.out.print(proceso.getDuracion());
            } else {
                System.out.print("+" + proceso.getDuracion());
            }
            sum += proceso.getDuracion();
        }
        System.out.print(" = " + sum+ "\n");
    }
    public static void tiemposSJF(ColaProcesos procesos) {
        procesos.ordenarPorDuracion();
        int tiempoInicio = 0;
        int tiempoFinalizacion = 0;
        int tiempoEspera = 0;
        int[] gantt = new int[procesos.size() + 1]; // Aumentamos en 1 el tamaño del vector
        gantt[0] = 0;
        for (int i = 0; i < procesos.size(); i++) {
            Proceso proceso = procesos.get(i);
            if (i != 0) {
                tiempoInicio = tiempoFinalizacion;
            }
            tiempoFinalizacion += proceso.getDuracion();
            gantt[i + 1] = tiempoFinalizacion;

            System.out.println("Proceso " + proceso.getNombre() + ":");
            System.out.println("\t Tiempo de inicio: " + tiempoInicio);
            System.out.println("\t Tiempo de finalización: " + tiempoFinalizacion);
            System.out.println("\t Tiempo de espera: " + tiempoEspera);

            tiempoEspera = tiempoFinalizacion - tiempoInicio;

        }

        gantt[procesos.size()] = tiempoFinalizacion;
        System.out.println();
        System.out.print("Diagrama de Gantt: | ");
        for (int i = 0; i <= procesos.size(); i++) {
            System.out.print(gantt[i]+ " | ");
        }
        System.out.println();
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
               proceso.setDuracion(proceso.getDuracion() - quantum); // Restar el quantum utilizado
                colaProcesos.agregarProceso(proceso);
            }
            colaProcesos.eliminarProceso(proceso);
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
                proceso.setDuracion(proceso.getDuracion() - quantum); // Restar el quantum utilizado
                colaProcesos.agregarProceso(proceso);
            }
            colaProcesos.eliminarProceso(proceso);
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

            colaProcesos.eliminarProceso(proceso);
        }
    }
}
