package game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;

// Kelas utama yang mengatur logika permainan dan tampilan
public class GamePanel extends JPanel implements ActionListener {
    // Objek permainan
    private Snake snake; // Objek ular
    private Apple apple; // Objek apel
    
    // Timers
    private Timer gameTimer;  // Timer utama permainan
    private Timer respawnTimer; // Timer untuk respawn setelah mati
    private Timer effectTimer; // Timer untuk efek khusus
    
    // Game state
    private int score = 0; // Skor pemain
    private int lives = 3; // Nyawa pemain
    private int level = 1; // Level saat ini
    private int multiplier = 1; // Multiplier skor
    private char direction = 'R'; // Arah gerakan ular (U=atas, D=bawah, L=kiri, R=kanan)
    private boolean isRunning = false; // Status apakah permainan sedang berjalan
    private boolean isPaused = false; // Status apakah permainan sedang dijeda
    private boolean isRespawning = false;
    private boolean isSpeedBoosted = false;
    private boolean isInvincible = false;
    
    // Theme colors
    private Color bgColor = new Color(240, 240, 240); // Warna background
    private Color snakeColor = new Color(50, 120, 50); // Warna ular
    private Color appleColor = new Color(220, 50, 50); // Warna apel
    private Color textColor = new Color(60, 60, 60); // Warna teks
    private Color panelColor = new Color(250, 250, 250); // Warna panel
    private Color borderColor = new Color(200, 200, 200); // Warna border

    // UI Components
    private JButton playButton; // Tombol mulai permainan
    private JButton themeButton; // Tombol pilih tema
    private JButton exitButton;// Tombol keluar
    private JButton[] themeButtons; // Array tombol pilihan tema
    
    // Statistics
    private Map<Integer, Integer> levelHighScores = new HashMap<>(); // Highscore per level
    private int applesEaten = 0; // Total apel normal yang dimakan
    private int specialApplesEaten = 0; // Total apel spesial yang dimakan

    private String levelUpText = "";
    private long levelUpStartTime = 0;


    //Konstruktor untuk GamePanel
    //Mengatur ukuran, background, layout, dan inisialisasi komponen
    public GamePanel() {
        setPreferredSize(new Dimension(600, 650)); // Ukuran panel
        setBackground(bgColor); // Set warna background
        setLayout(new BorderLayout()); // Menggunakan BorderLayout
        setFocusable(true); // Agar bisa menerima input keyboard
        addKeyListener(new KeyHandler()); // Menambahkan listener keyboard

        createStartMenu(); // Membuat menu awal
        initializeGame(); // Inisialisasi objek permainan
    }

    //Method untuk inisialisasi objek-objek permainan
    private void initializeGame() {
        // Membuat ular di posisi awal (125,125) dengan warna snakeColor
        snake = new Snake(125, 125, snakeColor);
        // Membuat apel pertama dengan warna appleColor
        apple = new Apple(appleColor);
        // Timer utama dengan delay 150ms yang memanggil actionPerformed
        gameTimer = new Timer(150, this);
        // Timer untuk respawn setelah mati (1000ms = 1 detik)
        respawnTimer = new Timer(1000, e -> {
            isRespawning = false; // Reset status respawn
            respawnTimer.stop(); // Hentikan timer
            gameTimer.start(); // Mulai kembali timer utama
        });
        
        // Timer untuk efek khusus (5000ms = 5 detik)
        effectTimer = new Timer(5000, e -> {
            isSpeedBoosted = false;
            isInvincible = false;
            multiplier = 1;
            snake.setEffectColor(null); // Hapus efek warna pada ular
            effectTimer.stop(); // Hentikan timer efek
        });
    }

    //Method untuk membuat menu awal permainan
    private void createStartMenu() {
        removeAll(); // Hapus semua komponen yang ada
        
        // Panel untuk menampung komponen menu
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS)); // Layout vertikal
        menuPanel.setBackground(panelColor); // Warna background panel
        menuPanel.setBorder(BorderFactory.createEmptyBorder(100, 0, 50, 0)); // Padding
        menuPanel.setAlignmentX(CENTER_ALIGNMENT); // Posisi tengah

        // Label judul game
        JLabel titleLabel = new JLabel("SNAKE GAME BY MEGA");
        titleLabel.setFont(new Font("Courier", Font.BOLD, 48)); // Font Courier bold 48px
        titleLabel.setForeground(new Color(255, 153, 153));  // Warna teks
        titleLabel.setAlignmentX(CENTER_ALIGNMENT); // Posisi tengah
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0)); // Margin bawah

        menuPanel.add(titleLabel);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 40)));
        // Buttons 
        playButton = createMenuButton("PLAY", new Color(100, 150, 100)); // Tombol play biru
        themeButton = createMenuButton("SELECT THEME", new Color(70, 130, 180)); // Tombol tema hijau
        exitButton = createMenuButton("EXIT", new Color(180, 70, 70)); // Tombol exit merah

        // Menambahkan action listener untuk tombol play
        playButton.addActionListener(e -> {
            removeAll(); // Hapus menu
            startGame(); // Mulai permainan
        });

        // Menambahkan action listener untuk tombol tema
        themeButton.addActionListener(e -> showThemeSelection());
        // Menambahkan action listener untuk tombol exit
        exitButton.addActionListener(e -> System.exit(0));

        menuPanel.add(playButton);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 40))); // Spacer lebih besar
        
        menuPanel.add(themeButton);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 40))); // Spacer lebih besar
        
        menuPanel.add(exitButton);
        
        add(menuPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
}
    /**
     * Method pembantu untuk membuat tombol menu
     * @param text Teks yang ditampilkan pada tombol
     * @param color Warna dasar tombol
     * @return JButton yang sudah dikonfigurasi
     */
    private JButton createMenuButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Courier", Font.BOLD, 24));
        button.setBackground(color); // Warna background
        button.setForeground(Color.WHITE); // Warna teks putih
        button.setFocusPainted(false); // Hilangkan border focus
        // Set border dengan kombinasi line border dan empty border
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color.darker(), 2),
            BorderFactory.createEmptyBorder(10, 30, 10, 30)
        ));
        button.setAlignmentX(CENTER_ALIGNMENT); // Posisi tengah
        button.setMaximumSize(new Dimension(250, 60)); // Ukuran maksimal
       // Menambahkan efek hover pada tombol
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.brighter()); // Warna lebih terang saat hover
                button.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Ubah kursor jadi tangan
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color); // Kembalikan warna asli
                button.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); 
            }
        });
        return button;
    }


    //  Method untuk menampilkan menu pemilihan tema
    private void showThemeSelection() {
        removeAll();  // Hapus semua komponen
        
         // Panel untuk tema
        JPanel themePanel = new JPanel();
        themePanel.setLayout(new BoxLayout(themePanel, BoxLayout.Y_AXIS)); // Layout vertikal
        themePanel.setBackground(panelColor); // Warna background
        themePanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0)); // Padding
        themePanel.setAlignmentX(CENTER_ALIGNMENT); // Posisi tengah

        JLabel titleLabel = new JLabel("SELECT THEME");
        titleLabel.setFont(new Font("Courier", Font.BOLD, 36));
        titleLabel.setForeground(new Color(70, 130, 180));
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        themeButtons = new JButton[3];
        themeButtons[0] = createThemeButton("DEFAULT", new Color(240, 240, 240));
        themeButtons[1] = createThemeButton("DARK", new Color(60, 60, 60));
        themeButtons[2] = createThemeButton("NATURE", new Color(200, 230, 200));

        
        JButton backButton = createMenuButton("BACK", new Color(150, 150, 150));
        backButton.addActionListener(e -> {
            removeAll();
            createStartMenu();
        });

         // Menambahkan komponen ke panel tema
        themePanel.add(titleLabel);
        for (JButton button : themeButtons) {
            themePanel.add(button);
            themePanel.add(Box.createRigidArea(new Dimension(0, 15)));
        }
        themePanel.add(Box.createRigidArea(new Dimension(0, 30)));
        themePanel.add(backButton);

        add(themePanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

     /**
     * Method pembantu untuk membuat tombol tema
     * @param text Nama tema
     * @param bg Warna background tombol
     * @return JButton yang sudah dikonfigurasi
     */
    private JButton createThemeButton(String text, Color bg) {
        JButton button = new JButton(text);
        button.setFont(new Font("Courier", Font.BOLD, 20));
        button.setBackground(bg);
        button.setForeground(textColor);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(bg.darker(), 2),
            BorderFactory.createEmptyBorder(10, 50, 10, 50)
        ));
        button.setAlignmentX(CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(300, 50));
        
        // Jika tema gelap, ubah warna teks jadi putih
        if (text.equals("DARK")) {
            button.setForeground(Color.WHITE);
        }
        
        button.addActionListener(e -> {
            if (text.equals("DEFAULT")) changeTheme(1);
            else if (text.equals("DARK")) changeTheme(2);
            else if (text.equals("NATURE")) changeTheme(3);
            removeAll();
            createStartMenu();
        });
        
        // Efek hover pada tombol
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
        
        return button;
    }

    /**
     * Method untuk memulai permainan
     */
    private void startGame() {
        isRunning = true; // Set status permainan berjalan
        score = 0;
        lives = 3;
        level = 1;
        applesEaten = 0;
        specialApplesEaten = 0;
        snake = new Snake(125, 125, snakeColor);
        apple = new Apple(appleColor);
        direction = 'R';
        gameTimer.setDelay(150);
        gameTimer.start();
        requestFocus();
    }

     /**
     * Method untuk menggambar komponen permainan
     * @param g Graphics context untuk menggambar
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        if (!isRunning) return;

        // Draw game area with border
        g2d.setColor(panelColor);
        g2d.fillRect(50, 50, 500, 500); // Area permainan 500x500 di posisi (50,50)
        g2d.setColor(borderColor);
        g2d.setStroke(new BasicStroke(3)); // Ketebalan border 3px
        g2d.drawRoundRect(50, 50, 500, 500, 10, 10); // Border dengan sudut melengkung

        // Gambar grid (samar)
        g2d.setColor(new Color(230, 230, 230));
        g2d.setStroke(new BasicStroke(1)); 
        // Gambar garis vertikal dan horizontal setiap 25px
        for (int i = 0; i <= 500; i += 25) {
            g2d.drawLine(50 + i, 50, 50 + i, 550);
            g2d.drawLine(50, 50 + i, 550, 50 + i);
        }

        // Gambar elemen permainan
        snake.draw(g2d);
        apple.draw(g2d);

        /// Gambar panel UI di bagian bawah
        g2d.setColor(panelColor);
        g2d.fillRect(0, 560, getWidth(), 90);
        g2d.setColor(borderColor);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(0, 560, getWidth(), 560);

        // Gambar info permainan
        g2d.setFont(new Font("Courier", Font.BOLD, 16)); // Font info
        
        // Info di sisi kiri
        g2d.setColor(textColor);
        g2d.drawString("Score: " + score, 60, 585);
        g2d.drawString("Highscore: " + ScoreManager.getHighScore(), 60, 610);
        g2d.drawString("Level: " + level, 60, 635);
        
        // Info di sisi kanan
        String multiplierText = "Multiplier: x" + multiplier;
        int multiplierWidth = g2d.getFontMetrics().stringWidth(multiplierText);
        g2d.drawString(multiplierText, getWidth() - multiplierWidth - 60, 585);
        
        // Jika speed boost aktif, tampilkan indikator
        if (isSpeedBoosted) {
            String speedText = "SPEED BOOST!";
            int speedWidth = g2d.getFontMetrics().stringWidth(speedText);
            g2d.setColor(new Color(70, 130, 180));
            g2d.drawString(speedText, getWidth() - speedWidth - 60, 610);
        }
        
        // Jika invincible aktif, tampilkan indikator
        if (isInvincible) {
            String invincibleText = "INVINCIBLE!";
            int invincibleWidth = g2d.getFontMetrics().stringWidth(invincibleText);
            g2d.setColor(new Color(220, 50, 50));
            g2d.drawString(invincibleText, getWidth() - invincibleWidth - 60, 635);
        }

        drawHearts(g2d); // Gambar indikator nyawa

        if (isPaused) drawPauseScreen(g2d); // Jika dijeda, gambar layar pause
    
        // Tampilkan notifikasi level up jika aktif
        if (!levelUpText.isEmpty()) {
            long elapsed = System.currentTimeMillis() - levelUpStartTime;
            if (elapsed < 2500) { // Tampilkan selama 2.5 detik
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
                g2d.setColor(Color.GREEN);
                g2d.setFont(new Font("Courier", Font.BOLD, 25));
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(levelUpText);
                g2d.drawString(levelUpText, (getWidth() - textWidth) / 2, getHeight() / 4);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            } else {
                levelUpText = ""; // Reset setelah selesai
            }
        }

    }    /**
     * Method untuk menggambar indikator nyawa
     * @param g Graphics context
     */
    private void drawHearts(Graphics g) {
        int heartSize = 25; // Ukuran icon nyawa
        // Hitung posisi awal agar icon nyawa ditengah
        int startX = getWidth() / 2 - (lives * (heartSize + 5)) / 2;
        int y = 580; // Posisi y tetap
        
        for (int i = 0; i < lives; i++) {
            drawHeart(g, startX + i * (heartSize + 5), y, heartSize);
        }
    }

    /**
     * Method untuk menggambar satu hati
     * @param g Graphics context
     * @param x Posisi x
     * @param y Posisi y
     * @param size Ukuran icon nyawa
     */
    private void drawHeart(Graphics g, int x, int y, int size) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Titik-titik untuk membentuk polygon  icon nyawa
        int[] xPoints = {x, x+size/4, x+size/2, x+size-size/4, x+size, x+size-size/4, x+size/2, x+size/4};
        int[] yPoints = {y+size/3, y, y-size/3, y, y+size/3, y+size/2, y+size, y+size/2};
        
        // Gambar icon nyawa dengan warna merah
        g2d.setColor(new Color(220, 50, 50));  // Warna merah
        g2d.fillPolygon(xPoints, yPoints, 8); // Isi icon nyawa
        g2d.setColor(new Color(180, 40, 40));  // Warna merah lebih gelap
        g2d.drawPolygon(xPoints, yPoints, 8);  // Garis tepi icon nyawa
    }

    /**
     * Method untuk menggambar layar pause
     * @param g Graphics context
     */
    private void drawPauseScreen(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        
        // Semi-transparent overlay
        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.fillRoundRect(50, 50, 500, 500, 10, 10);
        
        // Pause text
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Courier", Font.BOLD, 48));
        
        String pauseText = "PAUSED";
        int textWidth = g2d.getFontMetrics().stringWidth(pauseText);
        g2d.drawString(pauseText, 300 - textWidth/2, 300);
        
        g2d.setFont(new Font("Courier", Font.PLAIN, 18));
        String continueText = "Press ESC to continue";
        int continueWidth = g2d.getFontMetrics().stringWidth(continueText);
        g2d.drawString(continueText, 300 - continueWidth/2, 340);
    }

     /**
     * Method yang dipanggil oleh timer untuk update game state
     * @param e ActionEvent dari timer
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        // Jika game tidak berjalan, dijeda, atau sedang respawn, abaikan
        if (!isRunning || isPaused || isRespawning) return;

        // Movement logic
        int dx = 0, dy = 0;
        switch (direction) {
            case 'U' -> dy = -25; //Ataas 
            case 'D' -> dy = 25; // Bawah
            case 'L' -> dx = -25; // Kiri
            case 'R' -> dx = 25; // Kanan
        }

        snake.move(dx, dy); // Gerakkan ular

        // Cek jika ular memakan apel
        if (snake.getX() == apple.getX() && snake.getY() == apple.getY()) {
            handleAppleEaten();
        }

        // Deteksi tabrakan (dengan tubuh sendiri atau keluar arena)
        if ((snake.isColliding() || isOutOfBounds()) && !isInvincible) {
            handleCollision();
        }

        repaint();
    }

      /**
     * Method untuk menangani ketika apel dimakan
     */
    private void handleAppleEaten() {
        applesEaten++;
        
        int points = 10;
        int growthAmount = 1;
        switch (apple.getType()) {
            case 1: // Apel emas
                points = 50; // Poin lebih banyak
                multiplier = 2; // Multiplier 2x
                growthAmount = 3;
                snake.setEffectColor(new Color(255, 215, 0));
                effectTimer.stop();
                effectTimer.setInitialDelay(10000);
                effectTimer.start();
                specialApplesEaten++;
                break;
            case 2:  // Apel biru (speed boost)
                isSpeedBoosted = true;
                growthAmount = 1;
                gameTimer.setDelay(Math.max(50, gameTimer.getDelay() - 20));
                snake.setEffectColor(new Color(70, 130, 180));
                effectTimer.stop();
                effectTimer.setInitialDelay(8000);
                effectTimer.start();
                specialApplesEaten++; // Tambah counter apel spesial
                break;
            case 3:  // Apel hijau (bonus)
                isInvincible = true; // Aktifkan invincible
                growthAmount = 2;
                lives = Math.min(3, lives + 1); // Tambah nyawa (maks 3)
                snake.setEffectColor(new Color(50, 180, 50)); // Efek warna hijau
                effectTimer.stop();
                effectTimer.setInitialDelay(5000); // Efek selama 5 detik
                effectTimer.start();
                specialApplesEaten++; // Tambah counter apel spesial
                break;
            
            default: // Normal apple (apel merah)
            growthAmount = 1; // Tambah 1 segmen
            break;
        }
        
         // Tambahkan panjang ular sesuai jenis apel
    for (int i = 0; i < growthAmount; i++) {
        snake.grow();
        }
        
        score += points * multiplier; // Tambah skor (dikalikan multiplier jika ada)
        apple.spawn(); // Buat apel baru di posisi random
        
         // Naik level setiap 5 apel
        if (applesEaten % 5 == 0) {
            levelUp();
        }
    }

    /**
     * Method untuk meningkatkan level
     */
    private void levelUp() {
    level++; // Naik level
    gameTimer.setDelay(Math.max(50, gameTimer.getDelay() - 10)); // Naikkan kecepatan

    // Cek dan simpan skor tertinggi per level
    if (score > levelHighScores.getOrDefault(level, 0)) {
        levelHighScores.put(level, score);
    }

    // Tampilkan tulisan level up (pakai waktu sistem)
    levelUpText = "Level Up! Now at Level " + level;
    levelUpStartTime = System.currentTimeMillis();
    }


    /**
     * Method untuk mengecek apakah ular keluar arena
     * @return true jika keluar arena, false jika tidak
     */
    private boolean isOutOfBounds() {
        return snake.getX() < 50 || snake.getY() < 50 || 
               snake.getX() >= 530 || snake.getY() >= 530;
    }

    private void handleCollision() {
        gameTimer.stop(); // Hentikan permainan sementara
        lives--; // Kurangi nyawa
        
        if (lives > 0) {
            // Jika masih ada nyawa tersisa
            isRespawning = true;
            // Tampilkan pesan
            JOptionPane.showMessageDialog(this, 
                "Crash! " + lives + " lives left", 
                "Life Lost", 
                JOptionPane.WARNING_MESSAGE);
            
            snake = new Snake(125, 125, snakeColor); // respawn dengan panjang sebelumnya
            direction = 'R';
            apple.spawn(); // Buat apel baru
            respawnTimer.start(); // Mulai timer respawn
        } else {
            
            gameOver(); // Panggil method game over
        }
    }

     //Method untuk menangani game over
    private void gameOver() {
        isRunning = false;
        
        // Simpan highscore jika skor saat ini lebih tinggi
        int highscore = ScoreManager.getHighScore();
        if (score > highscore) {
            ScoreManager.saveHighScore(score);
        }
        
        // Format statistik game over
        String stats = String.format(
            "Game Over!\n\nFinal Score: %d\nLevel Reached: %d\nApples Eaten: %d\nSpecial Apples: %d",
            score, level, applesEaten, specialApplesEaten
        );
        // Tampilkan dialog game over dengan pilihan
        int option = JOptionPane.showOptionDialog(this, 
            stats, 
            "Game Over", 
            JOptionPane.YES_NO_OPTION, 
            JOptionPane.INFORMATION_MESSAGE, 
            null, 
            new String[]{"Play Again", "Main Menu"}, 
            "Play Again");
        
        if (option == 0) {
            startGame();
        } else {
            removeAll();
            createStartMenu();
        }
    }

     /**
     * Method untuk mengubah tema permainan
     * @param themeId ID tema (1=default, 2=dark, 3=nature)
     */
    private void changeTheme(int themeId) {
        switch (themeId) {
            case 1 -> { // Default
                bgColor = new Color(240, 240, 240);
                snakeColor = new Color(50, 120, 50);
                appleColor = new Color(220, 50, 50);
                textColor = new Color(60, 60, 60);
                panelColor = new Color(250, 250, 250);
                borderColor = new Color(200, 200, 200);
            }
            case 2 -> { // Dark
                bgColor = new Color(60, 60, 60);
                snakeColor = new Color(100, 180, 100);
                appleColor = new Color(220, 80, 80);
                textColor = Color.WHITE;
                panelColor = new Color(80, 80, 80);
                borderColor = new Color(120, 120, 120);
            }
            case 3 -> { // Nature
                bgColor = new Color(230, 240, 230);
                snakeColor = new Color(80, 140, 60);
                appleColor = new Color(200, 60, 60);
                textColor = new Color(50, 70, 50);
                panelColor = new Color(240, 250, 240);
                borderColor = new Color(180, 200, 180);
            }
        }
        // Terapkan warna baru
        setBackground(bgColor);
        if (snake != null) snake.setColor(snakeColor);
        if (apple != null) apple.setColor(appleColor);
    }

     // Kelas untuk menangani input keyboard
    private class KeyHandler extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (!isRunning) return;

            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT -> { if (direction != 'R') direction = 'L'; }
                case KeyEvent.VK_RIGHT -> { if (direction != 'L') direction = 'R'; }
                case KeyEvent.VK_UP -> { if (direction != 'D') direction = 'U'; }
                case KeyEvent.VK_DOWN -> { if (direction != 'U') direction = 'D'; }
                case KeyEvent.VK_ESCAPE -> { // Tombol ESC untuk pause/lanjut
                    isPaused = !isPaused; 
                    if (isPaused) {
                        gameTimer.stop(); // Jeda game
                    } else {
                        gameTimer.start(); // Lanjutkan game
                    }
                }
                case KeyEvent.VK_SPACE -> { // Tombol spasi untuk mulai game
                    if (!isRunning) startGame();
                }
            }
        }
    }
}