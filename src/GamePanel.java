import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener{
    
    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 25;
    // size of elements in screen - 25px
    static final int GAME_UNITS = (SCREEN_WIDTH*SCREEN_HEIGHT)/UNIT_SIZE;
    static final int DELAY = 75; 
    // delay controls how fast the game moves
    final int x[] = new int[GAME_UNITS];
    final int y[] = new int[GAME_UNITS];
    // arrays hold coordinates for all body parts
    // of the snake including the head
    int bodyParts = 6;
    // amt of body parts for snake
    int applesEaten = 0;
    int appleX = 0;
    int appleY = 0;
    char direction = 'R';
    // start direction = R = right
    boolean running = false;
    Timer timer;
    Random random;

    GamePanel(){
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        startGame();
    }

    public void startGame(){
        newApple();
        // spawn a new apple
        running = true;
        // game is now running
        timer = new Timer(DELAY, this);
        // dictates how fast game is running
        timer.start();
    }

    public void paintComponent(Graphics gfx){
        super.paintComponent(gfx);
        draw(gfx);
    }

    public void draw(Graphics gfx){
        if(running){
            // OPTIONAL - create grid to visualize what's happening
            for(int i = 0; i < SCREEN_HEIGHT/UNIT_SIZE; i++){
                gfx.drawLine(i*UNIT_SIZE, 0, i*UNIT_SIZE, SCREEN_HEIGHT);
                // vertical lines
                gfx.drawLine(0, i*UNIT_SIZE, SCREEN_WIDTH, i*UNIT_SIZE);
                // horizontal lines
                // each square is the size of a UNIT_SIZE
            }

            gfx.setColor(Color.red);
            gfx.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

            for(int i = 0; i < bodyParts; i++){
                if(i == 0){
                    // in this case, head of snake
                    gfx.setColor(Color.green);
                    gfx.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }
                else{
                    // else, body of snake
                    gfx.setColor(new Color(45, 180, 0));
                    // OPTIONAL - random colors
                    // gfx.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
                    gfx.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }
            }
            // display score
            gfx.setColor(Color.red);
            gfx.setFont(new Font("Ink Free", Font.BOLD, 40));
            FontMetrics metrics = getFontMetrics(gfx.getFont());
            gfx.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: " + applesEaten))/2, gfx.getFont().getSize());
        }
        else{
            gameOver(gfx);
        }
    }

    public void newApple(){
        appleX = random.nextInt((int)(SCREEN_WIDTH/UNIT_SIZE))*UNIT_SIZE;
        // get x coordinate of apple randomly
        appleY = random.nextInt((int)(SCREEN_HEIGHT/UNIT_SIZE))*UNIT_SIZE;
        // get y coordinate of apple randomly

    }

    public void move(){
        for(int i = bodyParts; i > 0; i--){
            // shift body parts of snake around
            x[i] = x[i-1];
            y[i] = y[i-1];
        }

        switch (direction) {
            case 'U':
                y[0] = y[0] - UNIT_SIZE;
                break;
            case 'D':
                y[0] = y[0] + UNIT_SIZE;
                break;
            case 'L':
                x[0] = x[0] - UNIT_SIZE;
                break;
            case 'R':
                x[0] = x[0] + UNIT_SIZE;
                break;
            
            default:
                break;
        }
    }

    public void checkApple(){
        if((x[0] == appleX) && (y[0] == appleY)){
            bodyParts++;
            applesEaten++;
            newApple();
        }
    }

    public void checkCollisions(){
        // check if snake head collides with body
        for(int i = bodyParts; i > 0; i--){
            if((x[0] == x[i]) && (y[0] == y[i])){
                running = false;
                // game over, no longer running bc snake collided with itself
            }
        }

        // check if head touches left border
        if(x[0] < 0){
            running = false;
        }
        // check if head touches right border
        if(x[0] > SCREEN_WIDTH){
            running = false;
        }
        // check if head touches top border
        if(y[0] < 0){
            running = false;
        }
        // check if head touches bottom border
        if(y[0] > SCREEN_HEIGHT){
            running = false;
        }

        // stop the timer if game over
        if(!running){
            timer.stop();
        }
    }

    public void gameOver(Graphics gfx){
        // display score
        gfx.setColor(Color.red);
        gfx.setFont(new Font("Ink Free", Font.BOLD, 40));
        FontMetrics metricsScore = getFontMetrics(gfx.getFont());
        gfx.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metricsScore.stringWidth("Score: " + applesEaten))/2, gfx.getFont().getSize());

        //display Game Over
        gfx.setColor(Color.red);
        gfx.setFont(new Font("Ink Free", Font.BOLD, 75));
        FontMetrics metricsGameOver = getFontMetrics(gfx.getFont());
        gfx.drawString("Game Over", (SCREEN_WIDTH - metricsGameOver.stringWidth("Game Over"))/2, SCREEN_HEIGHT/2);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(running){
            // if game is running...
            move();
            checkApple();
            // did we collide with apple?
            checkCollisions();
        }
        // if no longer running...
        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter{
        @Override
        public void keyPressed(KeyEvent e){
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if(direction != 'R'){
                        direction = 'L';
                    }
                    break;

                case KeyEvent.VK_RIGHT:
                    if(direction != 'L'){
                        direction = 'R';
                    }
                    break;

                case KeyEvent.VK_UP:
                    if(direction != 'D'){
                        direction = 'U';
                    }
                    break;

                case KeyEvent.VK_DOWN:
                    if(direction != 'U'){
                        direction = 'D';
                    }
                    break;
            }
        }
    }
}
