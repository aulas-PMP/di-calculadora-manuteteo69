import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;

/**
 * clase que representa una calculadora grafica implementada
 */
public class CalculadoraGaucho extends JFrame {

    private final JTextField pantalla; // cuadro de texto que muestra los numeros y resultados
    private final JTextField pantallaDatoAlmacenado; // cuadro de texto que muestra el dato almacenado
    private boolean reiniciarPantalla; // borra el resultado de la operacion anterior solo si se pone un numero, si se pone un operador nuevo continua con la operacion
    private ModoEntrada modoEntrada; // referencia al modo de entrada activo
    private String datoAlmacenado; // almacena un dato en la memoria

    /**
     * constructor que inicializa la interfaz grafica de la calculadora
     * tamano y posicion de la calculadora y tb los botnes
     */
    public CalculadoraGaucho() {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension tamano = toolkit.getScreenSize(); // para q se adapte al tamano d la pantalla
        int altura = tamano.height;
        int anchura = tamano.width;

        setSize(anchura / 2, altura / 3); // define el tamaño del marco
        setLocation(anchura / 4, altura / 3); // la anchura entre 4 para que quede en el medio no se muy bien pq
        setTitle("CalculadoraGauchoOptimista - ManuAbaloRietz"); // name de la calculator
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // diseño principal
        setLayout(new BorderLayout());

        // inicializa dato almacenado
        datoAlmacenado = "0";

        // inicializa pantalla del dato almacenado
        pantallaDatoAlmacenado = new JTextField("Dato almacenado: " + datoAlmacenado);
        pantallaDatoAlmacenado.setFont(new Font("Arial", Font.PLAIN, 18));
        pantallaDatoAlmacenado.setHorizontalAlignment(SwingConstants.RIGHT);
        pantallaDatoAlmacenado.setEditable(false);
        add(pantallaDatoAlmacenado, BorderLayout.NORTH);

        // inicializa pantalla principal
        pantalla = new JTextField("0"); // el texto de la calculadora empieza en 0
        pantalla.setFont(new Font("Arial", Font.BOLD, 36)); // estilo y tamano de la fuente q si no se ve ultrapeqeno
        pantalla.setHorizontalAlignment(SwingConstants.RIGHT); // propiedad para alinear el textico a la derecha
        pantalla.setEditable(false);
        add(pantalla, BorderLayout.CENTER);

        // panel principal para dividir botones de numeros y operadores
        JPanel panelPrincipal = new JPanel(new GridLayout(1, 2, 10, 10));

        // crea el panel de numeros
        JPanel panelNumeros = new JPanel();
        panelNumeros.setLayout(new GridLayout(4, 3, 5, 5)); // 4 filas, 3 columnas

        // define los textos de los botones de numeros
        String[] numeros = {
                "7", "8", "9",
                "4", "5", "6",
                "1", "2", "3",
                "0", ","
        };

        // agrega los botones numericos al panel de numeros
        for (String texto : numeros) {
            JButton boton = new JButton(texto);
            boton.setFont(new Font("Arial", Font.BOLD, 20));
            boton.setFocusPainted(false);
            boton.setBackground(Color.WHITE);
            boton.setForeground(Color.BLACK);

            // asigna la logica para cada boton
            boton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (!(modoEntrada instanceof ModoNumpad)) { // bloquea entrada por raton si esta en modo numpad
                        procesarEntrada(e.getActionCommand());
                    }
                    pantalla.requestFocusInWindow(); // asegura que el teclado sigue activo
                }
            });
            panelNumeros.add(boton);
        }

        // crea el panel de operadores
        JPanel panelOperadores = new JPanel();
        panelOperadores.setLayout(new GridLayout(4, 2, 5, 5)); // 4 filas, 2 columnas

        // define los textos de los botones de operadores
        String[] operadores = {
                "AC", "C",
                "/", "*",
                "-", "+",
                "="//, "AC"
                /*esta movida tuve q hacerla pa q al hacer una opercion
                 y tengamos el resultado, al darle a un operador 
                no se borre el resultado
                y pueda por ejemplo, multiplicarse */
        };

        // agrega los botones de operadores al panel de operadores
        for (String texto : operadores) {
            JButton boton = new JButton(texto);
            boton.setFont(new Font("Arial", Font.BOLD, 20));
            boton.setFocusPainted(false);
            boton.setBackground(Color.BLUE);
            boton.setForeground(Color.WHITE);

            // asigna la logica para cada boton
            boton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (!(modoEntrada instanceof ModoNumpad)) { // bloquea entrada por raton si esta en modo numpad
                        procesarEntrada(e.getActionCommand());
                    }
                    pantalla.requestFocusInWindow(); // asegura que el teclado sigue activo
                }
            });
            panelOperadores.add(boton);
        }

        // agrega los paneles al panel principal
        panelPrincipal.add(panelNumeros);
        panelPrincipal.add(panelOperadores);

        // agrega el panel principal al centro del layout
        add(panelPrincipal, BorderLayout.SOUTH);

        // asigna el listener del teclado
        pantalla.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (modoEntrada != null) {
                    modoEntrada.procesarEntradaTeclado(e); // delega el procesamiento al modo de entrada
                }
            }
        });

        // inicializa el selector de modos
        inicializarSelectorDeModos();
    }

    /**
     * inicializa los botones del selector de modos al final de la calculadora
     */
    private void inicializarSelectorDeModos() {
        JPanel selectorModos = new JPanel();
        selectorModos.setLayout(new FlowLayout());

        JRadioButton modoRaton = new JRadioButton("raton");
        JRadioButton modoNumpad = new JRadioButton("numpad");
        JRadioButton modoLibre = new JRadioButton("libre");

        ButtonGroup grupo = new ButtonGroup();
        grupo.add(modoRaton);
        grupo.add(modoNumpad);
        grupo.add(modoLibre);

        // inicializa con modo numpad por defecto
        modoNumpad.setSelected(true);
        cambiarModo(new ModoNumpad(this));

        // agrega listeners a los botones
        modoRaton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cambiarModo(new ModoRaton());
            }
        });
        modoNumpad.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cambiarModo(new ModoNumpad(CalculadoraGaucho.this));
            }
        });
        modoLibre.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cambiarModo(new ModoLibre(CalculadoraGaucho.this));
            }
        });

        selectorModos.add(modoRaton);
        selectorModos.add(modoNumpad);
        selectorModos.add(modoLibre);

        add(selectorModos, BorderLayout.NORTH);
    }

    /**
     * cambia el modo de entrada de datos de la calculadora
     */
    private void cambiarModo(ModoEntrada nuevoModo) {
        this.modoEntrada = nuevoModo;

        // actualiza los listeners segun el modo
        pantalla.removeKeyListener(pantalla.getKeyListeners()[0]); // quita el listener anterior
        pantalla.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (modoEntrada != null) {
                    modoEntrada.procesarEntradaTeclado(e); // delega el procesamiento al modo de entrada
                }
            }
        });
    }

    /**
     * procesa las entradas del raton y actualiza la pantalla
     */
    void procesarEntrada(String comando) {
        String textoActual = pantalla.getText();
    
        if (reiniciarPantalla) {
            textoActual = "0";
            reiniciarPantalla = false;
        }
    
        switch (comando) {
            case "AC":
                pantalla.setText("0");
                pantalla.setForeground(Color.BLACK); // resetear color a negro
                break;
            case "C":
                if (textoActual.length() > 1) {
                    pantalla.setText(textoActual.substring(0, textoActual.length() - 1));
                } else {
                    pantalla.setText("0");
                }
                break;
            case "=":
                try {
                    double resultado = evaluarExpresion(textoActual.replace(',', '.'));
                    pantalla.setText(String.valueOf(resultado).replace('.', ','));
    
                    // 
                    if (resultado < 0) {
                        pantalla.setForeground(Color.RED); // rojo si es negativo
                    } else {
                        pantalla.setForeground(Color.BLACK); // negro si es positivo o cero
                    }
    
                    reiniciarPantalla = true;
                } catch (Exception ex) {
                    pantalla.setText("error");
                    pantalla.setForeground(Color.BLACK); // negro por defecto para errores
                    reiniciarPantalla = true;
                }
                break;
            default:
                if (textoActual.equals("0")) {
                    pantalla.setText(comando);
                } else {
                    pantalla.setText(textoActual + comando);
                }
                break;
        }
    }
    

    /**
     * actualiza el dato mostrado en la pantalla del dato almacenado
     */
    private void actualizarPantallaDatoAlmacenado() {
        pantallaDatoAlmacenado.setText("Dato almacenado: " + datoAlmacenado);
    }

    /**
     * evalua una expresion matematica en formato string
     */
    private double evaluarExpresion(String expresion) throws Exception {
        ArrayList<Double> numeros = new ArrayList<>();
        ArrayList<Character> operadores = new ArrayList<>();
        String numeroActual = "";

        for (int i = 0; i < expresion.length(); i++) {
            char c = expresion.charAt(i);

            if (Character.isDigit(c) || c == '.') {
                numeroActual += c;
            } else if (esOperador(c)) {
                if (!numeroActual.isEmpty()) {
                    numeros.add(Double.parseDouble(numeroActual));
                    numeroActual = "";
                }
                while (!operadores.isEmpty() && prioridad(operadores.get(operadores.size() - 1)) >= prioridad(c)) {
                    procesarOperacion(numeros, operadores);
                }
                operadores.add(c);
            }
        }

        if (!numeroActual.isEmpty()) {
            numeros.add(Double.parseDouble(numeroActual));
        }

        while (!operadores.isEmpty()) {
            procesarOperacion(numeros, operadores);
        }

        if (numeros.size() != 1) {
            throw new Exception("error en la expresion");
        }

        return numeros.get(0);
    }

    private boolean esOperador(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }

    private int prioridad(char operador) {
        if (operador == '+' || operador == '-') return 1;
        if (operador == '*' || operador == '/') return 2;
        return 0;
    }

    private void procesarOperacion(ArrayList<Double> numeros, ArrayList<Character> operadores) throws Exception {
        if (numeros.size() < 2) throw new Exception("expresion invalida");

        double b = numeros.remove(numeros.size() - 1);
        double a = numeros.remove(numeros.size() - 1);
        char operador = operadores.remove(operadores.size() - 1);

        switch (operador) {
            case '+':
                numeros.add(a + b);
                break;
            case '-':
                numeros.add(a - b);
                break;
            case '*':
                numeros.add(a * b);
                break;
            case '/':
                if (b == 0) throw new Exception("division por cero");
                numeros.add(a / b);
                break;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                CalculadoraGaucho marco = new CalculadoraGaucho();
                marco.setVisible(true);
            }
        });
    }
    
       /**
 * procesa las entradas del teclado 
 */
protected void procesarTeclado(KeyEvent e) {
    int keyCode = e.getKeyCode();
    String textoActual = pantalla.getText();

    if (reiniciarPantalla) {
        textoActual = "0";
        reiniciarPantalla = false;
    }

    // Manejo de numeros
    if (keyCode >= KeyEvent.VK_NUMPAD0 && keyCode <= KeyEvent.VK_NUMPAD9) {
        int numero = keyCode - KeyEvent.VK_NUMPAD0;
        pantalla.setText(textoActual.equals("0") ? String.valueOf(numero) : textoActual + numero);
    }
    // Manejo de operadores
    else if (keyCode == KeyEvent.VK_ADD) {
        pantalla.setText(textoActual + "+");
    } else if (keyCode == KeyEvent.VK_SUBTRACT) {
        pantalla.setText(textoActual + "-");
    } else if (keyCode == KeyEvent.VK_MULTIPLY) {
        pantalla.setText(textoActual + "*");
    } else if (keyCode == KeyEvent.VK_DIVIDE) {
        pantalla.setText(textoActual + "/");
    } else if (keyCode == KeyEvent.VK_DECIMAL) {
        pantalla.setText(textoActual + ","); // agrega coma en lugar de punto
    }
    // Manejo del enter y borrar
    else if (keyCode == KeyEvent.VK_ENTER) {
        procesarEntrada("=");
    } else if (keyCode == KeyEvent.VK_BACK_SPACE) {
        procesarEntrada("C");
    }
}

    }
    
    /**
     * interfaz para definir modos de entrada
     */
    interface ModoEntrada {
        void procesarEntradaTeclado(KeyEvent e);
    }
    
    /**
     * modo que permite entrada solo por raton
     */
    class ModoRaton implements ModoEntrada {
        public ModoRaton() {
            // este modo no procesa entradas de teclado, por eso no necesita nada aqui
        }
    
        @Override
        public void procesarEntradaTeclado(KeyEvent e) {
            // no hace nada porque es solo para raton
        }
    }
    
    /**
     * modo que permite entrada solo por teclado numerico
     */
    class ModoNumpad implements ModoEntrada {
        private final CalculadoraGaucho calculadora;
    
        public ModoNumpad(CalculadoraGaucho calculadora) {
            this.calculadora = calculadora;
        }
    
        @Override
        public void procesarEntradaTeclado(KeyEvent e) {
            int keyCode = e.getKeyCode();
    
            // permite solo entradas de teclado numerico y operadores
            switch (keyCode) {
                case KeyEvent.VK_NUMPAD0, KeyEvent.VK_NUMPAD1, KeyEvent.VK_NUMPAD2, KeyEvent.VK_NUMPAD3,
                        KeyEvent.VK_NUMPAD4, KeyEvent.VK_NUMPAD5, KeyEvent.VK_NUMPAD6, KeyEvent.VK_NUMPAD7,
                        KeyEvent.VK_NUMPAD8, KeyEvent.VK_NUMPAD9:
                    int numero = keyCode - KeyEvent.VK_NUMPAD0;
                    calculadora.procesarEntrada(String.valueOf(numero));
                    break;
    
                case KeyEvent.VK_DECIMAL: // tecla del punto decimal
                    calculadora.procesarEntrada(",");
                    break;
    
                case KeyEvent.VK_ADD: // tecla +
                    calculadora.procesarEntrada("+");
                    break;
    
                case KeyEvent.VK_SUBTRACT: // tecla -
                    calculadora.procesarEntrada("-");
                    break;
    
                case KeyEvent.VK_MULTIPLY: // tecla *
                    calculadora.procesarEntrada("*");
                    break;
    
                case KeyEvent.VK_DIVIDE: // tecla /
                    calculadora.procesarEntrada("/");
                    break;
    
                case KeyEvent.VK_ENTER: // tecla Enter
                    calculadora.procesarEntrada("=");
                    break;
    
                case KeyEvent.VK_BACK_SPACE: // tecla Backspace
                    calculadora.procesarEntrada("C");
                    break;
    
                default:
                    // ignora cualquier otra tecla
                    break;
            }
        }
    }
    
    /**
     * modo que permite entrada tanto por raton como por teclado
     */
    class ModoLibre implements ModoEntrada {
        private final CalculadoraGaucho calculadora;
    
        public ModoLibre(CalculadoraGaucho calculadora) {
            this.calculadora = calculadora;
        }
    
        @Override
        public void procesarEntradaTeclado(KeyEvent e) {
            calculadora.procesarTeclado(e);
    }
}
