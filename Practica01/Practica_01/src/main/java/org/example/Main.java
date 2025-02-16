import java.util.Random;
import java.util.Scanner;
import java.util.*;

public class Main {
    // Variable que almacena el nombre del usuario que ha iniciado sesión
    private static String usuario_logeado;

    // Constante que define la longitud del tablero.
    private static final int LONGITUD = 20;

    // Matriz bidimensional que representa el tablero del juego.
    public static char[][] tablero;

    // Arreglo de palabras que conforman el diccionario del juego.
    public static String[] diccionario;

    // con esto tomo los datos del teclado.
    public static Scanner entradaEstatica = new Scanner(System.in);

    // Mapa que almacena los puntajes de los usuarios, donde la clave es el nombre del usuario y el valor es su puntaje.
    private static Map<String, Integer> puntajes = new HashMap<>();

    private static int palabrasEncontradas = 0;

    private static int totalPalabras = 0;
    private static List<String> palabrasEncontradasLista = new ArrayList<>();

    //  private static LinkedList<String> palabrasEncontradasLista = new LinkedList<>(); // como alternativa a new ArrayList<>();

    private static int intentosFallidos = 0;  // Variable para contar los intentos fallidos
    private static final int OPORTUNIDADES_MAXIMAS = 4;  // Número máximo de oportunidades

    public static void main(String[] args) {
        Main inicio = new Main();
        inicio.InicioUsuario(); // Llamada al metodo InicioUsuario()
    }

    public void InicioUsuario() {
        while (true) {
            System.out.println("***BIENVENIDO A UNA NUEVA PARTIDA ***");
            System.out.print("Por Favor Cree su Usuario : ");
            usuario_logeado = entradaEstatica.nextLine().trim(); //.trim() → Elimina los espacios en blanco al inicio y al final de la cadena ingresada.

           /*

            Asegura que el usuario haya ingresado un nombre antes de continuar con la ejecución del programa.
            Si la cadena no está vacía, el programa procede a guardar el usuario y ejecutar las siguientes acciones.

            */

            if (!usuario_logeado.isEmpty()) {
                puntajes.putIfAbsent(usuario_logeado, 25);  // Inicia con 25 puntos
                inicializarJuego();
                MenuInicioSesion();
            }
        }
    }

    private void inicializarJuego() {
        // Inicializa el tablero como una matriz de caracteres de tamaño LONGITUD x LONGITUD.
        tablero = new char[LONGITUD][LONGITUD];
        // Crea un diccionario con capacidad para almacenar hasta 50 palabras.
        diccionario = new String[50];
    }
// Mandamos a imprimir el menu enciclado por si se coloca un numero que no corresponde.
    public static void MenuInicioSesion() {
        while (true) {
            System.out.println("***Menu de Opciones***");
            System.out.println("1. Ver Puntajes");
            System.out.println("2. Ingresar Palabras");
            System.out.println("3. Mostrar Tablero");
            System.out.println("4. Cerrar Sesión");
            System.out.print("Seleccione opción: ");

            if (!entradaEstatica.hasNextInt()) { // La expresión !entradaEstatica.hasNextInt() se usa para verificar si la siguiente entrada del usuario NO es un número entero.
                entradaEstatica.next();
                System.out.println("Opción no válida. Intente de nuevo.");
                continue;
            }
            int opcion = entradaEstatica.nextInt();
            entradaEstatica.nextLine();

            switch (opcion) {
                case 1:
                    mostrarPuntajes();
                    break;
                case 2:
                    agregarPalabras();
                    break;
                case 3:
                    buscarPalabrasEnTablero();
                    break;
                case 4:
                    cerrarSesion();
                    return;
                default:
                    System.out.println("Opción inválida. Intente de nuevo.");
            }
        }
    }

    public static void cerrarSesion() {
        System.out.printf("Presione (s) para salir o cualquier otra tecla para un nuevo juego.....");
        String respuesta = entradaEstatica.nextLine().trim().toLowerCase();// trim elimina los espacios en blanco y toLoverCase(); convierte a mayusculas.
        if (respuesta.equals("s")) {  //compara el contenido de dos objetos para determinar si son iguales.
            System.out.println("**GRACIAS POR JUGAR **");
            System.out.print("Saliendo");

            try {  // codigo que puede generar una excepcion
                for (int i = 0; i < 4; i++) {
                    Thread.sleep(500); // Pausa de 500 milisegundos entre cada punto
                    System.out.print(".");
                }
            } catch (InterruptedException e) { // codigo que se ejecuta si sucede la excepsion
                Thread.currentThread().interrupt();  // Sirve para interrumpir el hilo actual, marcándolo como interrumpido. Sin embargo, no detiene el hilo inmediatamente, solo le indica que ha sido interrumpido para que pueda manejar la interrupción de forma adecuada
            }

            System.out.println(); // Salto de línea al final
            System.exit(0);
        } else {
            System.out.println("Regresando al inicio de sesión...");
        }
    }

    public static void agregarPalabras() {
        System.out.print("Cuantas palabras desea ingresar: ");

        if (!entradaEstatica.hasNextInt()) { // Niega el resultado, es decir, devuelve true si la entrada no es un número entero.
            entradaEstatica.next();
            System.out.println("Número inválido. Intente de nuevo.");
            return;
        }
        totalPalabras = entradaEstatica.nextInt();
        entradaEstatica.nextLine();
        palabrasEncontradas = 0;
        palabrasEncontradasLista.clear();

        boolean esHorizontal = true; // Empezamos colocando la primera palabra de forma horizontal

        for (int i = 0; i < totalPalabras; i++) {
            String palabra = "";
            boolean palabraValida = false;

            // Bucle para asegurarse de que la palabra ingresada sea válida
            while (!palabraValida) {
                System.out.print("Ingrese palabra (5-12 caracteres): ");
                palabra = entradaEstatica.nextLine().trim().toUpperCase();

                // Validar que no contenga espacios
                if (palabra.contains(" ")) {
                    System.out.println("La palabra no debe contener espacios en blanco. Intente de nuevo.");
                    continue;  // Regresa al inicio del ciclo para pedir la palabra nuevamente
                }

                if (palabra.length() >= 5 && palabra.length() <= 12) {
                    palabraValida = true;
                    // Guardar la palabra en el diccionario
                    for (int j = 0; j < diccionario.length; j++) {
                        if (diccionario[j] == null) {
                            diccionario[j] = palabra;
                            colocarPalabraEnTablero(palabra, esHorizontal);
                            break;
                        }
                    }

                    // Alternamos entre horizontal y vertical
                    esHorizontal = !esHorizontal;
                } else {
                    System.out.println("Palabra fuera de rango. Debe tener entre 5 y 12 caracteres.");
                }
            }
        }
        llenarTablero(); // Rellena el tablero con letras aleatorias después de colocar las palabras
    }

    public static void colocarPalabraEnTablero(String palabra, boolean esHorizontal) {
        Random random = new Random(); // crea una nueva instancia de la clase Random. Este objeto, random, luego se puede usar para generar números aleatorios de diferentes tipos.
        boolean colocada = false;

        while (!colocada) {
            // Elegir una dirección (horizontal o vertical)
            int direccion = esHorizontal ? 0 : 1;
            int fila = random.nextInt(LONGITUD);
            int col = random.nextInt(LONGITUD);

            if (direccion == 0) { // Horizontal
                if (col + palabra.length() <= LONGITUD) { // Verificar que cabe horizontalmente
                    boolean espacioLibre = true;
                    for (int i = 0; i < palabra.length(); i++) {
                        if (tablero[fila][col + i] != '\0') {
                            espacioLibre = false;
                            break;
                        }
                    }
                    if (espacioLibre) {
                        for (int i = 0; i < palabra.length(); i++) {
                            tablero[fila][col + i] = palabra.charAt(i);
                        }
                        colocada = true;
                    }
                }
            } else { // Vertical
                if (fila + palabra.length() <= LONGITUD) { // Verificar que cabe verticalmente
                    boolean espacioLibre = true;
                    for (int i = 0; i < palabra.length(); i++) {
                        if (tablero[fila + i][col] != '\0') {
                            espacioLibre = false;
                            break;
                        }
                    }
                    if (espacioLibre) {
                        for (int i = 0; i < palabra.length(); i++) {
                            tablero[fila + i][col] = palabra.charAt(i);
                        }
                        colocada = true;
                    }
                }
            }
        }
    }

    public static void llenarTablero() {
        Random random = new Random();
        for (int i = 0; i < LONGITUD; i++) {
            for (int j = 0; j < LONGITUD; j++) {
                if (tablero[i][j] == '\0') { // compara el valor almacenado en la posición [i][j] de la matriz (o arreglo bidimensional) tablero con el carácter nulo '\0'.
                    tablero[i][j] = (char) ('A' + random.nextInt(26)); // 'A' + random.nextInt(26): Aquí, el número aleatorio generado por nextInt(26) se suma al valor numérico de 'A'. Esto da como resultado un número entre 65 (para 'A') y 90 (para 'Z'), que son los valores numéricos de las letras mayúsculas del alfabeto.
                }
            }
        }
    }

    public static void buscarPalabrasEnTablero() {
        while (palabrasEncontradas < totalPalabras) {
            imprimirTablero();
            System.out.print("Ingrese una palabra para buscar en el tablero: ");
            String palabraBuscada = entradaEstatica.nextLine().trim().toUpperCase();

            // Validar que no contenga espacios
            if (palabraBuscada.contains(" ")) {
                System.out.println("La palabra no debe contener espacios en blanco. Intente de nuevo.");
                continue;  // Regresa al inicio del ciclo para pedir la palabra nuevamente
            }

            if (buscarPalabraEnTablero(palabraBuscada)) {
                System.out.println("Palabra encontrada. Mostrando tablero con palabra tachada:");
                tacharPalabraEnTablero(palabraBuscada);
                palabrasEncontradas++;
                palabrasEncontradasLista.add(palabraBuscada);
                int puntosPalabra = palabraBuscada.length(); // La palabra vale tantos puntos como su longitud
                puntajes.put(usuario_logeado, puntajes.get(usuario_logeado) + puntosPalabra);
            } else {
                System.out.println("Palabra no encontrada en el tablero.");
                puntajes.put(usuario_logeado, puntajes.get(usuario_logeado) - 5); // Restar 5 puntos por palabra incorrecta
                intentosFallidos++; // Incrementar los intentos fallidos
                int oportunidadesRestantes = OPORTUNIDADES_MAXIMAS - intentosFallidos;
                System.out.println("Te quedan " + oportunidadesRestantes + " oportunidades.");

                if (intentosFallidos == OPORTUNIDADES_MAXIMAS) {
                    System.out.println("**Game Over**. Has fallado 4 veces.");
                    // Regresa al inicio de sesión después de perder
                    Main inicio = new Main();
                    inicio.InicioUsuario();  // Llamar a InicioUsuario desde una nueva instancia
                    return;
                }
            }
        }
        System.out.println("*** ¡Felicidades! Has encontrado todas las palabras. ***");
        System.out.println("Palabras encontradas: " + palabrasEncontradasLista);
        imprimirTablero(); // Muestra el tablero final con todas las palabras tachadas
    }

    public static boolean buscarPalabraEnTablero(String palabra) {
        for (String palabraDic : diccionario) {
            if (palabraDic != null && palabraDic.equals(palabra)) {
                return true;
            }
        }
        return false;
    }

    public static void tacharPalabraEnTablero(String palabra) {
        for (int i = 0; i < LONGITUD; i++) {
            for (int j = 0; j < LONGITUD; j++) {
                if (tablero[i][j] == palabra.charAt(0)) {
                    for (int k = 0; k < palabra.length(); k++) {
                        if (j + k < LONGITUD) {
                            tablero[i][j + k] = '#';
                        }
                    }
                    return;
                }
            }
        }
    }

    public static void imprimirTablero() {
        System.out.println("***_Tablero_****:");
        for (int i = 0; i < LONGITUD; i++) {
            for (int j = 0; j < LONGITUD; j++) {
                System.out.print(tablero[i][j] + " ");
            }
            System.out.println();
        }
    }

    public static void mostrarPuntajes() {
        System.out.println("***Puntajes***:");
        String ganador = "";
        int maxPuntaje = Integer.MIN_VALUE;
        boolean empate = false;

        // Encuentra el puntaje más alto
        for (Map.Entry<String, Integer> entry : puntajes.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue() + " puntos");
            if (entry.getValue() > maxPuntaje) {
                maxPuntaje = entry.getValue();
                ganador = entry.getKey();
                empate = false;
            } else if (entry.getValue() == maxPuntaje && !ganador.isEmpty()) {
                empate = true; // Si hay otro jugador con el mismo puntaje, es empate
            }
        }

        if (empate) {
            System.out.println("Es un empate.");
        } else {
            System.out.println("El ganador es: " + ganador + " con " + maxPuntaje + " puntos.");
        }
    }
}
