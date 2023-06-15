import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

class Proceso {
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

    public void setDuracion(int duracion) {
        this.duracion = duracion;
    }

    public int getPrioridad() {
        return prioridad;
    }
}

class ColaProcesos implements Iterable<Proceso> {
    private List<Proceso> procesos;

    public ColaProcesos() {
        procesos = new ArrayList<>();
    }

    public void agregarProceso(Proceso proceso) {
        procesos.add(proceso);
    }

    public void mostrarProcesos() {
        for (Proceso proceso : procesos) {
            System.out.println("Proceso: " + proceso.getNombre() + " Duración: " + proceso.getDuracion()
                    + " Prioridad: " + proceso.getPrioridad());
        }
    }

    public void ordenarPorDuracion() {
        procesos.sort(Comparator.comparingInt(Proceso::getDuracion));
    }

    public void ordenarPorPrioridad() {
        procesos.sort(Comparator.comparingInt(Proceso::getPrioridad));
    }

    public boolean isEmpty() {
        return procesos.isEmpty();
    }

    public Proceso get(int index) {
        return procesos.get(index);
    }

    public void remove(Proceso proceso) {
        procesos.remove(proceso);
    }

    public void add(Proceso proceso) {
        procesos.add(proceso);
    }

    public int size() {
        return procesos.size();
    }

    @Override
    public Iterator<Proceso> iterator() {
        return procesos.iterator();
    }
}

class ManejoProcesos {
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        boolean salir = false;
        while (!salir) {
            System.out.println("\n--- Menú ---");
            System.out.println("1. FCFS (en orden de llegada)");
            System.out.println("2. SJF (Planificación con selección del trabajo más corto)");
            System.out.println("3. Round Robin con FIFO");
            System.out.println("4. Round Robin con prioridad");
            System.out.println("5. Salir");
            System.out.print("Elige una opción: ");
            int opcion = scanner.nextInt();
            switch (opcion) {
            /*
                case 1:
                    Thread fcfsThread = new Thread(() -> ejecutarFCFS());
                    fcfsThread.start();
                    break;
                case 2:
                    Thread sjfThread = new Thread(() -> ejecutarSJF());
                    sjfThread.start();
                    break;
                case 3:
                    Thread roundRobinFifoThread = new Thread(() -> ejecutarRoundRobinFIFO());
                    roundRobinFifoThread.start();
                    break;
                case 4:
                    Thread roundRobinPrioridadThread = new Thread(() -> ejecutarRoundRobinPrioridad());
                    roundRobinPrioridadThread.start();
                    break;
                case 5:
                    salir = true;
                    break;


                    */
                    
                case 1:
                    ejecutarFCFS();
                    break;
                case 2:
                    ejecutarSJF();
                    break;
                case 3:
                    ejecutarRoundRobinFIFO();
                    break;
                case 4:
                    ejecutarRoundRobinPrioridad();
                    break;
                case 5:
                    ejecutarSRTF();
                    break;
                case 6:
                    salir = true;
                    break;
                default:
                    System.out.println("Opción inválida.");
                    break;
            }
        }
    }

    private static void ejecutarFCFS() {
        ColaProcesos cola = crearColaProcesos();
        System.out.println("\nEjecutando FCFS (en orden de llegada)");
        System.out.println("Diagrama de Gantt:");
        int tiempo = 0;
        for (Proceso proceso : cola) {
            System.out.print(tiempo + " - " + (tiempo + proceso.getDuracion()) + ": " + proceso.getNombre() + " ");
            tiempo += proceso.getDuracion();
        }
        double tiempoMedioEspera = calcularTiempoMedioEspera(cola);
        System.out.println("\nTiempo medio de espera: " + tiempoMedioEspera);
    }

    private static void ejecutarSJF() {
        ColaProcesos cola = crearColaProcesos();
        cola.ordenarPorDuracion();
        System.out.println("\nEjecutando SJF (Planificación con selección del trabajo más corto)");
        System.out.println("Diagrama de Gantt:");
        int tiempo = 0;
        for (Proceso proceso : cola) {
            System.out.print(tiempo + " - " + (tiempo + proceso.getDuracion()) + ": " + proceso.getNombre() + " ");
            tiempo += proceso.getDuracion();
        }
        double tiempoMedioEspera = calcularTiempoMedioEspera(cola);
        System.out.println("\nTiempo medio de espera: " + tiempoMedioEspera);
    }

    private static void ejecutarRoundRobinFIFO() {
        ColaProcesos cola = crearColaProcesos();
        System.out.print("Ingrese el quantum para Round Robin: ");
        int quantum = scanner.nextInt();
        System.out.println("\nEjecutando Round Robin con FIFO");
        System.out.println("Diagrama de Gantt:");
        int tiempo = 0;
        while (!cola.isEmpty()) {
            Proceso proceso = cola.get(0);
            if (proceso.getDuracion() <= quantum) {
                System.out.print(tiempo + " - " + (tiempo + proceso.getDuracion()) + ": " + proceso.getNombre() + " ");
                tiempo += proceso.getDuracion();
                cola.remove(proceso);
            } else {
                System.out.print(tiempo + " - " + (tiempo + quantum) + ": " + proceso.getNombre() + " ");
                tiempo += quantum;
                proceso.setDuracion(proceso.getDuracion() - quantum);
                cola.add(proceso);
                cola.remove(proceso);
            }
        }
        double tiempoMedioEspera = calcularTiempoMedioEspera(cola);
        System.out.println("\nTiempo medio de espera: " + tiempoMedioEspera);
    }

 private static void ejecutarSRTF() {
        ColaProcesos cola = crearColaProcesos();
        System.out.println("\nEjecutando SRTF (Shortest Remaining Time First)");
        System.out.println("Diagrama de Gantt:");
        int tiempo = 0;
        while (!cola.isEmpty()) {
            Proceso procesoActual = obtenerProcesoMasCorto(cola, tiempo);
            if (procesoActual != null) {
                System.out.print(tiempo + " - " + (tiempo + 1) + ": " + procesoActual.getNombre() + " ");
                tiempo += 1;
                procesoActual.setDuracion(procesoActual.getDuracion() - 1);
                if (procesoActual.getDuracion() == 0) {
                    cola.remove(procesoActual);
                }
            } else {
                tiempo += 1;
            }
        }
        double tiempoMedioEspera = calcularTiempoMedioEspera(cola);
        System.out.println("\nTiempo medio de espera: " + tiempoMedioEspera);
    }
     private static Proceso obtenerProcesoMasCorto(ColaProcesos cola, int tiempo) {
        Proceso procesoMasCorto = null;
        for (Proceso proceso : cola) {
            if (proceso.getDuracion() > 0) {
                if (procesoMasCorto == null) {
                    procesoMasCorto = proceso;
                } else if (proceso.getDuracion() < procesoMasCorto.getDuracion()) {
                    procesoMasCorto = proceso;
                }
            }
        }
        return procesoMasCorto;
    }

    private static void ejecutarRoundRobinPrioridad() {
        ColaProcesos cola = crearColaProcesos();
        cola.ordenarPorPrioridad();
        System.out.print("Ingrese el quantum para Round Robin: ");
        int quantum = scanner.nextInt();
        System.out.println("\nEjecutando Round Robin con prioridad");
        System.out.println("Diagrama de Gantt:");
        int tiempo = 0;
        while (!cola.isEmpty()) {
            Proceso proceso = cola.get(0);
            if (proceso.getDuracion() <= quantum) {
                System.out.print(tiempo + " - " + (tiempo + proceso.getDuracion()) + ": " + proceso.getNombre() + " ");
                tiempo += proceso.getDuracion();
                cola.remove(proceso);
            } else {
                System.out.print(tiempo + " - " + (tiempo + quantum) + ": " + proceso.getNombre() + " ");
                tiempo += quantum;
                proceso.setDuracion(proceso.getDuracion() - quantum);
                cola.add(proceso);
                cola.remove(proceso);
                cola.ordenarPorPrioridad(); // Ordenar nuevamente por prioridad después de agregar el proceso al final de la cola
            }
        }
        double tiempoMedioEspera = calcularTiempoMedioEspera(cola);
        System.out.println("\nTiempo medio de espera: " + tiempoMedioEspera);
    }

    private static ColaProcesos crearColaProcesos() {
        ColaProcesos cola = new ColaProcesos();
        System.out.print("Ingrese la cantidad de procesos: ");
        int cantidadProcesos = scanner.nextInt();
        for (int i = 1; i <= cantidadProcesos; i++) {
            System.out.print("Ingrese el nombre del proceso " + i + ": ");
            String nombre = scanner.next();
            System.out.print("Ingrese la duración del proceso " + i + ": ");
            int duracion = scanner.nextInt();
            System.out.print("Ingrese la prioridad del proceso " + i + ": ");
            int prioridad = scanner.nextInt();
            Proceso proceso = new Proceso(nombre, duracion, prioridad);
            cola.agregarProceso(proceso);
        }
        return cola;
    }

    private static double calcularTiempoMedioEspera(ColaProcesos cola) {
        double tiempoTotalEspera = 0;
        int cantidadProcesos = cola.size();
        int tiempoEspera = 0;
        for (Proceso proceso : cola) {
            tiempoTotalEspera += tiempoEspera;
            tiempoEspera += proceso.getDuracion();
        }
        return tiempoTotalEspera / cantidadProcesos;
    }
}
