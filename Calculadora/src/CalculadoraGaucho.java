import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;

/**
 * clase que representa una calculadora grafica implementada
 */
public class CalculadoraGaucho extends JFrame {

    private final JTextField pantalla; // cuadro de texto que muestra los numeros y resultados

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
                "0", ".", "(", ")" // los parentesis los anadi para que me cuadrase
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
            boton.addActionListener(e -> procesarEntrada(e.getActionCommand()));
            panelBotones.add(boton);
        }

        add(panelBotones, BorderLayout.CENTER);

        // asigna el listener del teclado (teoricamente pq no va)
        pantalla.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                procesarTeclado(e);
            }
        });
    }

    /**
     * procesa las entradas del raton y actualiza la pantalla
     */
    private void procesarEntrada(String comando) {
        String textoActual = pantalla.getText();

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
                    double resultado = evaluarExpresion(textoActual);
                    pantalla.setText(String.valueOf(resultado));
                } catch (Exception ex) {
                    pantalla.setText("Error");
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
     * procesa las entradas del teclado y actualiza la pantalla (sigue sin ir la entrada de datos por teclado numerico xd)
     */
    private void procesarTeclado(KeyEvent e) {
        int keyCode = e.getKeyCode(); 
        String textoActual = pantalla.getText();

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

    private int prioridad(char operador) { // jerarquia de operadores matematicos no se si esta bien xd
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
        SwingUtilities.invokeLater(() -> {
            CalculadoraGaucho marco = new CalculadoraGaucho();
            marco.setVisible(true);
        });
    }
}
