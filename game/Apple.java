package game;

import java.awt.*;
import java.util.Random;

public class Apple extends Entity implements Drawable {
    private Color color;
    private Random rand = new Random();
    private int type; // 0 = normal, 1 = golden, 2 = speed, 3 = bonus
    private int effectDuration;

    // Konstruktor untuk membuat apel dengan warna tertentu
    public Apple(Color color) {
        super(0, 0); // Memanggil konstruktor parent class (Entity)
        this.color = color;
        this.type = 0; // Default ke apel normal
        spawn(); // Memanggil method spawn untuk posisi awal
    }

    // Method untuk menempatkan apel di posisi random dan menentukan jenisnya
    public void spawn() {
        x = 50 + rand.nextInt(20) * 25;
        y = 50 + rand.nextInt(20) * 25;
        
        // Probabilitas jenis apel: 
        // 70% normal, 10% golden, 10% speed, 10% bonus
        int chance = rand.nextInt(100);
        if (chance < 70) {
            type = 0; // Apel normal (merah)
            color = new Color(220, 50, 50); // Red
        } else if (chance < 80) {
            type = 1; // Apel emas
            color = new Color(255, 215, 0); // Gold
        } else if (chance < 90) {
            type = 2; // Apel biru (speed boost)
            color = new Color(70, 130, 180); // Blue
        } else {
            type = 3; // Apel hijau (bonus)
            color = new Color(50, 180, 50); // Green
        }
    }

    // Getter untuk jenis apel
    public int getType() {
        return type;
    }

    // Getter untuk durasi efek
    public int getEffectDuration() {
        return effectDuration;
    }

    // Setter untuk warna apel
    public void setColor(Color color) {
        this.color = color;
    }

    // Method untuk menggambar apel
    @Override
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Menggambar apel dengan gradient untuk efek visual
        GradientPaint gradient = new GradientPaint(
            x, y, color.brighter(),
            x + 20, y + 20, color.darker()
        );
        g2d.setPaint(gradient);
        g2d.fillOval(x, y, 20, 20);
        
        // Menambahkan highlight pada apel
        g2d.setColor(new Color(255, 255, 255, 100));
        g2d.fillOval(x + 3, y + 3, 8, 8);
        
        /// Menambahkan tangkai untuk apel normal
        if (type == 0) {
            g2d.setColor(new Color(100, 70, 30)); // Warna coklat untuk tangkai
            g2d.setStroke(new BasicStroke(2));
            g2d.drawLine(x + 10, y - 3, x + 10, y + 2);
        }
        
        // Memberikan tanda khusus untuk apel spesial
        if (type != 0) {
            g2d.setColor(Color.WHITE);
            g2d.setStroke(new BasicStroke(1.5f));
            g2d.drawOval(x, y, 20, 20);
            
            if (type == 1) { // Efek kilau untuk apel emas
                g2d.setColor(new Color(255, 255, 150));
                g2d.fillOval(x + 5, y + 5, 10, 10);
            }
        }
    }
}