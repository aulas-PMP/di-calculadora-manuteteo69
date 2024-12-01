import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;

/**
 * clase que representa una calculadora grafica implementada
 */
public class CalculadoraGaucho extends JFrame {

    private final JTextField pantalla; // cuadro de texto que muestra los numeros y resultados
    private boolean reiniciarPantalla; // borra el resultado de la operacion anterior solo si se pone un numero, si se pone un operador nuevo continua con la operacion
    private ModoEntrada modoEntrada; // referencia al modo de entrada activo

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
        setTitle("CalculadoraGaucho - ManuAbaloRietz"); // name de la calculator
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // diseño principal
        setLayout(new BorderLayout());

        // inicializa pantalla
        pantalla = new JTextField("0"); // el texto de la calculadora empieza en 0
        pantalla.setFont(new Font("Arial", Font.BOLD, 36)); // estilo y tamano de la fuente q si no se ve ultrapeqeno
        pantalla.setHorizontalAlignment(SwingConstants.RIGHT); // propiedad para alinear el textico a la derecha
        pantalla.setEditable(false);
        add(pantalla, BorderLayout.NORTH); // cuadrin de la pantalla

        // crea el panel de botones
        JPanel panelBotones = new JPanel();
        panelBotones.setLayout(new GridLayout(5, 4, 5, 5));

        // define los textos de los botones
        String[] botones = {
                "AC", "/", "*", "DEL", // AC borra todo y DEL borra solo el ultimo num
                "7", "8", "9", "-",
                "4", "5", "6", "+",
                "1", "2", "3", "=",
                "0", ",", "(", ")" // los parentesis los anadi para que me cuadrase
        };

        // agrega los botones al panel
        for (String texto : botones) {
            JButton boton = new JButton(texto);
            boton.setFont(new Font("Arial", Font.BOLD, 20));
            boton.setFocusPainted(false);

            // aplica estilos
            if (texto.equals("AC") || texto.equals("=") || texto.equals("+") || texto.equals("-") || texto.equals("*") || texto.equals("/") || texto.equals("DEL")) {
                boton.setBackground(Color.BLUE);
                boton.setForeground(Color.WHITE);
            } else {
                boton.setBackground(Color.WHITE);
                boton.setForeground(Color.BLACK);
            }

            // asigna la logica para cada boton
            boton.addActionListener(e -> {
                procesarEntrada(e.getActionCommand());
                if (modoEntrada instanceof ModoLibre) {
                    pantalla.requestFocusInWindow(); // asegura que el teclado sigue activo
                }
            });
            panelBotones.add(boton);
        }

        add(panelBotones, BorderLayout.CENTER);

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
        modoRaton.addActionListener(e -> cambiarModo(new ModoRaton()));
        modoNumpad.addActionListener(e -> cambiarModo(new ModoNumpad(this)));
        modoLibre.addActionListener(e -> cambiarModo(new ModoLibre(this)));

        selectorModos.add(modoRaton);
        selectorModos.add(modoNumpad);
        selectorModos.add(modoLibre);

        add(selectorModos, BorderLayout.SOUTH);
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

        // desactiva botones si es modo numpad
        for (Component componente : getComponentsEnPanelBotones()) {
            if (componente instanceof JButton) {
                componente.setEnabled(!(modoEntrada instanceof ModoNumpad)); // desactiva los botones en modo numpad
            }
        }
    }

    /**
     * metodo para obtener todos los componentes del panel de botones
     */
    private Component[] getComponentsEnPanelBotones() {
        for (Component componente : getContentPane().getComponents()) {
            if (componente instanceof JPanel) {
                JPanel panel = (JPanel) componente;
                if (panel.getLayout() instanceof GridLayout) {
                    return panel.getComponents(); // devuelve todos los botones
                }
            }
        }
        return new Component[0];
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
                    break;
                case "DEL":
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
                        reiniciarPantalla = true;
                    } catch (Exception ex) {
                        pantalla.setText("error");
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
         * procesa las entradas del teclado y actualiza la pantalla
         */
        protected void procesarTeclado(KeyEvent e) {
            int keyCode = e.getKeyCode(); 
            String textoActual = pantalla.getText();
    
            if (reiniciarPantalla) {
                textoActual = "0";
                reiniciarPantalla = false; 
            }
    
            if (keyCode >= KeyEvent.VK_NUMPAD0 && keyCode <= KeyEvent.VK_NUMPAD9) {
                int numero = keyCode - KeyEvent.VK_NUMPAD0;
                pantalla.setText(textoActual.equals("0") ? String.valueOf(numero) : textoActual + numero);
            } else if (keyCode == KeyEvent.VK_ADD) {
                pantalla.setText(textoActual + "+");
            } else if (keyCode == KeyEvent.VK_SUBTRACT) {
                pantalla.setText(textoActual + "-");
            } else if (keyCode == KeyEvent.VK_MULTIPLY) {
                pantalla.setText(textoActual + "*");
            } else if (keyCode == KeyEvent.VK_DIVIDE) {
                pantalla.setText(textoActual + "/");
            } else if (keyCode == KeyEvent.VK_DECIMAL) {
                pantalla.setText(textoActual + ","); // agrega coma en lugar de punto
            } else if (keyCode == KeyEvent.VK_ENTER) {
                procesarEntrada("=");
            } else if (keyCode == KeyEvent.VK_BACK_SPACE) {
                procesarEntrada("DEL");
            }
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
                case '+': numeros.add(a + b); break;
                case '-': numeros.add(a - b); break;
                case '*': numeros.add(a * b); break;
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
                calculadora.procesarEntrada("DEL");
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
