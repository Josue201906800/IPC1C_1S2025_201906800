import java.util.Random;
import java.util.Scanner;
import java.util.*;

public class Main {
    // Variable para almacenar el nombre del usuario que está logueado en la partida.
    private static String usuario_logeado;
    // Constante que define el tamaño del tablero, en este caso será un tablero de 20x20.
    private static final int LONGITUD = 20;
    // Array bidimensional (matriz) que representará el tablero de juego. Cada casilla del tablero
// contendrá un carácter, que en este caso corresponderá a una letra.
    public static char[][] tablero;
    // Array unidimensional que almacenará el diccionario de palabras que el usuario debe encontrar.
// El diccionario se llena con palabras que el usuario ingresa durante el juego.
    public static String[] diccionario;
    // aquí empezamos a tomar los datos del teclado.
    public static Scanner entradaEstatica = new Scanner(System.in);
    private static Map<String, Integer[]> puntajes = new HashMap<>(); // Cambié a Integer[] para almacenar puntos y jugadas
    private static int palabrasEncontradas = 0;
    private static int totalPalabras = 0;
    // aquí no utilizar Arraylist
    private static String[] palabrasEncontradasLista = new String[50];
    private static int intentosFallidos = 0;
    private static final int OPORTUNIDADES_MAXIMAS = 4;

    public static void main(String[] args) {
        // Llamar al metodo estático desde el main
        InicioUsuario();
    }

    public static void InicioUsuario() {
        while (true) {
            System.out.println("***BIENVENIDO A UNA NUEVA PARTIDA ***");
            System.out.print("   Por Favor Cree su Usuario : ");
            usuario_logeado = entradaEstatica.nextLine().trim(); // omite los espacios en blanco.
            if (!usuario_logeado.isEmpty()) { //verifica si la variable usuario_logeado no está vacía.
                puntajes.putIfAbsent(usuario_logeado, new Integer[]{25, 0}); // 25 puntos iniciales, 0 juegos jugados
                inicializarJuego();
                MenuInicioSesion();
            }
        }
    }

    private static void inicializarJuego() {
        tablero = new char[LONGITUD][LONGITUD]; // crea un array bidimensional de tipo char llamado tablero
        diccionario = new String[50];
        llenarTableroAleatorio();
    }

    public static void MenuInicioSesion() {
        // imprimimos el menu en un ciclo para entrar en un submenu
        while (true) {
            System.out.println("***Menu de Opciones***");
            System.out.println("1. Ver Puntajes");
            System.out.println("2. Ingresar Palabras");
            System.out.println("3. Mostrar Tablero (Jugar)");
            System.out.println("4. Reportes");
            System.out.println("5. Cerrar Sesión");
            System.out.print("Seleccione opción: ");

            if (!entradaEstatica.hasNextInt()) { // se utiliza para verificar si la entrada proporcionada por el usuario no es un número entero válido.
                entradaEstatica.next();
                System.out.println("Opción no válida. Intente de nuevo Por Favor.");
                continue;
            }
            int opcion = entradaEstatica.nextInt();
            entradaEstatica.nextLine();

            // para el submenu

            switch (opcion) {
                case 1:
                    mostrarPuntajes();
                    break;
                case 2:
                    agregarPalabras();
                    break;
                case 3:
                    buscarTodasLasPalabras();
                    break;
                case 4:
                    mostrarReportes();
                    break;
                case 5:
                    cerrarSesion();
                    return;
                default:
                    System.out.println("Opción inválida. Intente de nuevo.");
            }
        }
    }

    public static void agregarPalabras() {
        System.out.print("Cuantas palabras desea ingresar: ");
        if (!entradaEstatica.hasNextInt()) {
            entradaEstatica.next();
            System.out.println("Número inválido. Intente de nuevo.");
            return;
        }
        totalPalabras = entradaEstatica.nextInt();
        entradaEstatica.nextLine();
        palabrasEncontradas = 0;
        Arrays.fill(palabrasEncontradasLista, null);

        for (int i = 0; i < totalPalabras; i++) {
            System.out.print("Ingrese palabra (5-12 caracteres): ");
            String palabra = entradaEstatica.nextLine().trim().toUpperCase();
            if (palabra.length() >= 5 && palabra.length() <= 12 && !palabra.contains(" ")) { // Verifica que no haya espacios
                for (int j = 0; j < diccionario.length; j++) {
                    if (diccionario[j] == null) {
                        diccionario[j] = palabra;
                        colocarPalabraEnTablero(palabra);
                        break;
                    }
                }
            } else {
                System.out.println("Palabra fuera de rango o contiene espacios.");
            }
        }
    }

    public static void mostrarPuntajes() {
        System.out.println("***Puntajes***:");
        for (Map.Entry<String, Integer[]> entry : puntajes.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue()[0] + " puntos");
        }
        mostrarGanador();
    }

    private static void mostrarGanador() {
        int maxPuntos = Integer.MIN_VALUE;
        String ganador = "";
        boolean empate = false;

        for (Map.Entry<String, Integer[]> entry : puntajes.entrySet()) {
            int puntos = entry.getValue()[0];
            if (puntos > maxPuntos) {
                maxPuntos = puntos;
                ganador = entry.getKey();
                empate = false;
            } else if (puntos == maxPuntos) {
                empate = true;
            }
        }

        if (empate) {
            System.out.println("¡Hay un empate!");
        } else {
            System.out.println("El ganador es: " + ganador);
        }
    }

    public static void mostrarReportes() {
        System.out.println("***Reportes***:");
        for (Map.Entry<String, Integer[]> entry : puntajes.entrySet()) {
            System.out.println(entry.getKey() + " ha jugado " + entry.getValue()[1] + " veces.");
            // Agregar intentos fallidos y exitosos
            int intentosExitosos = entry.getValue()[1] - intentosFallidos;
            System.out.println(entry.getKey() + " tuvo " + intentosFallidos + " intentos fallidos y " + intentosExitosos + " intentos exitosos.");
        }
    }

    public static void buscarTodasLasPalabras() {
        while (palabrasEncontradas < totalPalabras) {
            System.out.print("Ingrese la palabra a buscar: ");
            String palabraBuscar = entradaEstatica.nextLine().trim().toUpperCase();
            if (palabraBuscar.contains(" ")) {  // Evitar que la palabra contenga espacios
                System.out.println("La palabra no debe contener espacios.");
                continue;
            }

            boolean encontrada = false;

            for (int i = 0; i < LONGITUD; i++) {
                for (int j = 0; j < LONGITUD; j++) {
                    if (buscarPalabraEnPosicion(i, j, palabraBuscar)) {
                        reemplazarPalabraEnTablero(i, j, palabraBuscar);
                        encontrada = true;
                        palabrasEncontradas++;
                        // Sumar puntos: 1 punto por cada letra encontrada
                        puntajes.get(usuario_logeado)[0] += palabraBuscar.length();
                        break;
                    }
                }
                if (encontrada) break;
            }

            if (!encontrada) {
                // Restar puntos: 5 puntos por cada palabra no encontrada
                puntajes.get(usuario_logeado)[0] -= 5;
                System.out.println("La palabra no fue encontrada en el tablero.");
                intentosFallidos++; // Aumentar intentos fallidos
                System.out.println("Te quedan " + (OPORTUNIDADES_MAXIMAS - intentosFallidos) + " intentos.");
                if (intentosFallidos == OPORTUNIDADES_MAXIMAS) {
                    System.out.println("¡Perdiste! Has alcanzado el límite de intentos fallidos.");
                    MenuInicioSesion(); // Regresar al menú de opciones
                    return;
                }
            } else {
                System.out.println("Palabra encontrada y tachada en el tablero.");
                // Mostrar cuántas palabras faltan por encontrar
                int palabrasRestantes = totalPalabras - palabrasEncontradas;
                System.out.println("Faltan " + palabrasRestantes + " palabras por encontrar.");
                System.out.println("¡Todas las palabras han sido encontradas!");
            }

            System.out.println("*** Tablero de Juego ***");
            for (int i = 0; i < LONGITUD; i++) {
                for (int j = 0; j < LONGITUD; j++) {
                    System.out.print(tablero[i][j] + " ");
                }
                System.out.println();
            }
        }

        // Aumentar la cantidad de veces que el usuario ha jugado
        puntajes.get(usuario_logeado)[1]++;
    }

    private static boolean buscarPalabraEnPosicion(int fila, int columna, String palabra) {
        if (columna + palabra.length() > LONGITUD) {
            return false;
        }
        for (int k = 0; k < palabra.length(); k++) {
            if (tablero[fila][columna + k] != palabra.charAt(k)) {
                return false;
            }
        }
        return true;
    }

    private static void reemplazarPalabraEnTablero(int fila, int columna, String palabra) {
        for (int k = 0; k < palabra.length(); k++) {
            tablero[fila][columna + k] = '#';
        }
    }

    private static void colocarPalabraEnTablero(String palabra) {
        Random rand = new Random();
        int fila = rand.nextInt(LONGITUD);
        int columna = rand.nextInt(LONGITUD - palabra.length());
        for (int k = 0; k < palabra.length(); k++) {
            tablero[fila][columna + k] = palabra.charAt(k);
        }
    }

    // Metodo que llena el tablero con letras aleatorias
    private static void llenarTableroAleatorio() {
        // Crea un objeto Random para generar números aleatorios
        Random rand = new Random();
        // Recorre cada fila del tablero
        for (int i = 0; i < LONGITUD; i++) {
            // Recorre cada columna de la fila
            for (int j = 0; j < LONGITUD; j++) {

                // Genera una letra aleatoria entre 'A' y 'Z'
                // rand.nextInt(26) genera un número aleatorio entre 0 y 25
                // 'A' + rand.nextInt(26) suma ese número al valor de 'A' (es decir, entre 0 y 25)
                // Esto da un número que corresponde a una letra entre 'A' y 'Z'
                tablero[i][j] = (char) ('A' + rand.nextInt(26));
            }
        }
    }

    public static void cerrarSesion() {
        System.out.println("Cerrando sesión de " + usuario_logeado);
        System.out.print("Para Salir presione S , para iniciar una nueva partida presione I (INICIAR/SALIR): ");
        String respuesta = entradaEstatica.nextLine().trim().toUpperCase();

        if (respuesta.equals("INICIAR") || respuesta.equals("I")) {
            InicioUsuario(); // Volver al inicio de sesión
        } else if (respuesta.equals("SALIR") || respuesta.equals("S")) {
            System.out.println("Gracias por su Juego");
            try {
                // Mostrar los tres puntos con una pequeña pausa
                for (int i = 0; i < 3; i++) {
                    System.out.print(".");
                    Thread.sleep(500);
                }
            } catch (InterruptedException e) {
                System.out.println("Error al pausar.");
            }

            System.exit(0); // Termina el programa correctamente
        } else {
            System.out.println("Opción no válida. El programa no se cerrará.");
        }
    }
}
