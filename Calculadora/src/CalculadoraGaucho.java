import java.awt.*;
import javax.swing.*;

/**
 * clase que representa una calculadora grafica implementada
 */
public class CalculadoraGaucho extends JFrame {

    private JTextField pantalla; // cuadro de texto que muestra los numeros y resultados

    /**
     * constructor que inicializa la interfaz grafica de la calculadora
     * tamano y posicion de la calculadora y tb los botnes
     */
    public CalculadoraGaucho() {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension tamano = toolkit.getScreenSize();
        int altura = tamano.height;
        int anchura = tamano.width;

        setSize(anchura / 2, altura / 3); // define el tamaño del marco
        setLocation(anchura / 4, altura / 3); // la anchura entre 4 para que quede en el medio no se muy bien pq 
        setTitle("CalculadoraGaucho - ManuAbaloRietz");//name de la calculator
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // diseno principal
        setLayout(new BorderLayout());

        // inicializa pantalla
        pantalla = new JTextField("0");
        pantalla.setFont(new Font("Arial", Font.BOLD, 36));// estilo y tamano de la fuente q si no se ve ultrapeqeno
        pantalla.setHorizontalAlignment(SwingConstants.RIGHT);
        pantalla.setEditable(false);
        add(pantalla, BorderLayout.NORTH);

        // crea el panel de botones
        JPanel panelBotones = new JPanel();
        panelBotones.setLayout(new GridLayout(5, 4, 5, 5));

        // define los textos de los botones
        String[] botones = {
                "AC", "÷", "×", "DEL", // AC borra todo y DEL borra solo el ultimo num
                "7", "8", "9", "-",
                "4", "5", "6", "+",
                "1", "2", "3", "=",
                "0", ".", "(", ")"
        };

        // agrega los botones al panel
        for (String texto : botones) {
            JButton boton = new JButton(texto);
            boton.setFont(new Font("Arial", Font.BOLD, 20));
            boton.setFocusPainted(false);

            // aplica estilos
            if (texto.equals("AC") || texto.equals("=")) {
                boton.setBackground(Color.RED);
                boton.setForeground(Color.WHITE);
            } else {
                boton.setBackground(Color.LIGHT_GRAY);
                boton.setForeground(Color.BLACK);
            }

            panelBotones.add(boton);
        }

        add(panelBotones, BorderLayout.CENTER);
    }

    /**
     * metodo principal que inicia la calculator y la muestra en panralla
     *
     * @param args 
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CalculadoraGaucho marco = new CalculadoraGaucho();
            marco.setVisible(true);
        });
    }
}
