import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.*;

public class NumericKeypadApp {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Interacción con el teclado numérico");
        JTextArea textArea = new JTextArea("Presiona las teclas del teclado numérico...");
        textArea.setEditable(false); // Solo para mostrar resultados

        // Agregar KeyListener para capturar eventos del teclado
        textArea.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                // No se necesita
            }

            @Override
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();
                String output = "";

                // Capturar números del teclado numérico
                if (keyCode >= KeyEvent.VK_NUMPAD0 && keyCode <= KeyEvent.VK_NUMPAD9) {
                    int number = keyCode - KeyEvent.VK_NUMPAD0;
                    output = "Número presionado: " + number;
                } 
                // Capturar operaciones matemáticas
                else if (keyCode == KeyEvent.VK_ADD) {
                    output = "Operación presionada: + (Suma)";
                } else if (keyCode == KeyEvent.VK_SUBTRACT) {
                    output = "Operación presionada: - (Resta)";
                } else if (keyCode == KeyEvent.VK_MULTIPLY) {
                    output = "Operación presionada: * (Multiplicación)";
                } else if (keyCode == KeyEvent.VK_DIVIDE) {
                    output = "Operación presionada: / (División)";
                } else if (keyCode == KeyEvent.VK_DECIMAL) {
                    output = "Operación presionada: . (Punto Decimal)";
                }

                if (!output.isEmpty()) {
                    textArea.append("\n" + output);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                // No se necesita
            }
        });

        frame.add(textArea);
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // Asegurarse de que el foco esté en el área de texto para capturar teclas
        textArea.requestFocusInWindow();
    }
}
