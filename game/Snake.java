package game;

import java.awt.*;
import java.util.ArrayList;

public class Snake extends Entity implements Drawable { 
    private ArrayList<Point> body = new ArrayList<>(); 
    private int size = 1;
    private Color color;
    private Color effectColor = null;
    private char direction = 'R'; 

     // Konstruktor
    public Snake(int x, int y, Color color) {
        super(x, y);
        this.color = color;
        body.add(new Point(x, y));
    }

    public Snake(int x, int y, Color color, int length) {
        super(x, y);
        this.color = color;
        this.size = length;
        for (int i = 0; i < length; i++) {
            body.add(new Point(x - i * 25, y)); // arah ke kiri
        }
    } 

    // Method untuk menambah panjang ular
    public void grow() {
        size++;
    }

     // Method untuk menggerakkan ular
    public void move(int dx, int dy) {
        x += dx; // Update posisi x
        y += dy; // Update posisi y
        body.add(0, new Point(x, y));
        while (body.size() > size) body.remove(body.size() - 1);
        
        // Update arah berdasarkan gerakan
        if (dx > 0) direction = 'R';
        else if (dx < 0) direction = 'L';
        else if (dy < 0) direction = 'U';
        else if (dy > 0) direction = 'D';
    }

    // Method untuk mendeteksi tabrakan dengan tubuh sendiri
    public boolean isColliding() {
        // Cek apakah kepala bertabrakan dengan segmen tubuh lain
        for (int i = 1; i < body.size(); i++) {
            if (body.get(i).equals(body.get(0))) return true;
        }
        return false;
    }

    //Setter untuk warna ular
    public void setColor(Color color) {
        this.color = color;
    }

    //Setter untuk warna efek khusus
    public void setEffectColor(Color effectColor) {
        this.effectColor = effectColor;
    }

    //Getter untuk panjang ular
     public int getLength() {
        return size;
    }
    /**
     * Method untuk menggambar ular
     * @param g Graphics context
     */
    @Override
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Gambar setiap segmen tubuh
        for (int i = 0; i < body.size(); i++) {
            Point p = body.get(i);
            
            // Tentukan gradient warna
            GradientPaint gradient;
            if (i == 0 && effectColor != null) {
                // Jika kepala dan ada efek khusus
                gradient = new GradientPaint(
                    p.x, p.y, effectColor.brighter(),
                    p.x + 20, p.y + 20, effectColor.darker()
                );
            } else {
                gradient = new GradientPaint(
                    p.x, p.y, color.brighter(),
                    p.x + 20, p.y + 20, color.darker()
                );
            }
            
            g2d.setPaint(gradient);
            g2d.fillRoundRect(p.x, p.y, 20, 20, 5, 5);
            
            //border
            g2d.setColor(color.darker().darker());
            g2d.setStroke(new BasicStroke(1.5f));
            g2d.drawRoundRect(p.x, p.y, 20, 20, 5, 5);
            
            // Gambar fitur kepala (hanya untuk segmen pertama)
            if (i == 0) {
                // Mata
                g2d.setColor(Color.WHITE);
                g2d.fillOval(p.x + 4, p.y + 5, 6, 6);
                g2d.fillOval(p.x + 10, p.y + 5, 6, 6);
                g2d.setColor(Color.BLACK);
                g2d.fillOval(p.x + 5, p.y + 6, 3, 3); //pupil
                g2d.fillOval(p.x + 11, p.y + 6, 3, 3);
                
                // Lidah (hanya saat bergerak horizontal)
                if (direction == 'L' || direction == 'R') {
                    g2d.setColor(new Color(220, 50, 50));
                    g2d.fillRoundRect(
                        direction == 'R' ? p.x + 20 : p.x - 5, 
                        p.y + 8, 
                        5, 
                        4, 
                        2, 
                        2
                    );
                }
            }
        }
    }
}