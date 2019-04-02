package org.wf.game.flappybird;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class FalappyBirdGame {
    public static void main(String[] args) {
        // 定义画框
        JFrame jf = new JFrame("bird_game");
        jf.setSize(432, 674);
        jf.setAlwaysOnTop(false);
        jf.setLocationRelativeTo(null);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setResizable(false);
        Sky sky = new Sky();
        jf.add(sky);
        // 显示画框
        jf.setVisible(true);
        sky.action();
    }
}

// 天空类
class Sky extends JPanel {
    private static final long serialVersionUID = 1L;
    BufferedImage bgBufferImage; // 背景图片
    Ground ground = new Ground(); // 地面
    Column column = new Column(350); // 钢管
    Column column2 = new Column(600); // 钢管
    static Bird bird = new Bird(); // 小鸟
    int score = 0; // 游戏得分
    BufferedImage startBufferImage; // 开始准备界面
    boolean isStrat; // 是否开始游戏
    BufferedImage overBufferImage; // 游戏结束界面
    boolean isOver; // 游戏是否结束

    public Sky() {
        super();
        // 读取图片
        File bgImage = new File("images/bg.png");
        File starImage = new File("images/start.png");
        File overImage = new File("images/gameover.png");
        try {
            bgBufferImage = ImageIO.read(bgImage);
            startBufferImage = ImageIO.read(starImage);
            overBufferImage = ImageIO.read(overImage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 绘制界面方法
    @Override
    public void paint(Graphics graphics) {
        // 画背景
        graphics.drawImage(bgBufferImage, 0, 0, null);
        // 获取新的画笔对象
        Graphics2D gg = (Graphics2D) graphics;
        gg.rotate(-bird.ratation, bird.bird_x, bird.bird_y);
        // 画小鸟
        graphics.drawImage(bird.biBufferImage, bird.bird_x - bird.bird_width
                / 2, bird.bird_y - bird.bird_height / 2, null);
        gg.rotate(bird.ratation, bird.bird_x, bird.bird_y);
        // 画钢管
        graphics.drawImage(column.coBufferImage, column.column_x - column.width
                / 2, column.column_y - column.height / 2, null);
        graphics.drawImage(column2.coBufferImage, column2.column_x
                        - column2.width / 2, column2.column_y - column2.height / 2,
                null);
        // 画地面
        graphics.drawImage(ground.grBufferImage, ground.ground_x,
                ground.ground_y, null);
        // 画文字
        graphics.setColor(Color.BLUE);
        graphics.setFont(new Font("楷体", Font.ITALIC, 30));
        graphics.drawString("分数:" + score, 100, 600);
        // 画开始准备图片
        if (!isStrat && !isOver) {
            graphics.drawImage(startBufferImage, 0, 0, null);
        }
        // 画结束界面
        if (isOver) {
            graphics.drawImage(overBufferImage, 0, 0, null);
        }
    }

    // 游戏启动逻辑
    public void action() {
        // 添加鼠标监听器
        MouseAdapter adapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // System.out.println("点击了鼠标");
                /*
                 * 若游戏结束重新开始游戏,游戏恢复初始状态
                 * 若未结束:鸟飞起来
                 */
                if (isOver) {
                    bird = new Bird();
                    ground = new Ground();
                    column = new Column(350);
                    column2 = new Column(600);
                    score = 0;
                    isOver = false;
                    isStrat = false;
                } else {
                    bird.refly();
                    isStrat = true;
                }
            }
        };
        this.addMouseListener(adapter);
        // 添加键盘监听器
        KeyAdapter keyAdapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                char charA = e.getKeyChar();
                if (charA == 'w') {
                    if (bird.bird_y > 20) {
                        bird.bird_y -= 20;
                    }
                } else if (charA == 's') {
                    if (bird.bird_y < 465) {
                        bird.bird_y += 20;
                    }
                } else if (charA == 'a') {
                    if (bird.bird_x > 20) {
                        bird.bird_x -= 20;
                    }
                } else if (charA == 'd') {
                    if (bird.bird_x < 395) {
                        bird.bird_x += 20;
                    }
                } else if (charA == ' ') {
                    /*
                     * 若游戏结束重新开始游戏,游戏恢复初始状态
                     * 若未结束:鸟飞起来
                     */
                    if (isOver) {
                        bird = new Bird();
                        ground = new Ground();
                        column = new Column(350);
                        column2 = new Column(600);
                        score = 0;
                        isOver = false;
                        isStrat = false;
                    } else {
                        bird.refly();
                        isStrat = true;
                    }
                }
                super.keyPressed(e);
            }
        };
        this.addKeyListener(keyAdapter);
        this.requestFocus();

        while (true) {
            // 判断游戏是否开始
            if (isStrat && !isOver) {
                ground.move();
                column.move();
                column2.move();
                bird.change();
                bird.move_go();
            }
            // 判断撞击障碍
            if (bird.bird_x - bird.bird_width / 2 == column.column_x
                    + column.width / 2
                    || bird.bird_x - bird.bird_width / 2 == column2.column_x
                    + column2.width / 2) {
                score++;
            }
            if (bird.hit(ground) || bird.hit(column) || bird.hit(column2)) {
                isStrat = false;
                isOver = true;
            }
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            repaint();
        }
    }
}

// 地面类
class Ground {
    int ground_x, ground_y; // 地面的坐标
    BufferedImage grBufferImage; // 地面图片

    public Ground() {
        super();
        File grImage = new File("images/ground.png");
        try {
            grBufferImage = ImageIO.read(grImage);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ground_y = 500;
    }

    // 地面动画方法
    public void move() {
        ground_x--;
        if (ground_x < -110) {
            ground_x = 0;
        }
    }
}

// 钢管类
class Column {
    int column_x, column_y; // 钢管的中心坐标
    int width, height; // 宽度高度
    int gap = 140; // 钢管的空隙
    Random random = new Random();
    ; // 随机坐标
    BufferedImage coBufferImage; // 钢管图片

    public Column(int x) {
        super();
        File coImage = new File("images/column.png");
        try {
            coBufferImage = ImageIO.read(coImage);
        } catch (IOException e) {
            e.printStackTrace();
        }
        column_x = x;
        column_y = random.nextInt(180) + 150;
        width = coBufferImage.getWidth();
        height = coBufferImage.getHeight();
    }

    // 钢管动画方法
    public void move() {
        column_x--;
        if (column_x < -width / 2) {
            column_y = random.nextInt(180) + 150;
            column_x = 432 + width / 2;
        }
    }
}

// 鸟类
class Bird {
    int bird_x = 60, bird_y = 300; // 鸟的中心点坐标
    int bird_width, bird_height; // 鸟的宽度,高度
    double speed = 20; // 速度
    double g = 4; // 加速度
    double s; // 运动距离
    double t = 0.3; // 运动时间
    BufferedImage biBufferImage; // 鸟图片
    BufferedImage[] images = new BufferedImage[8];
    int bird_icon = 0;

    public Bird() {
        super();
        for (int i = 0; i < images.length; i++) {
            File biImage = new File("images/" + i + ".png");
            try {
                images[i] = ImageIO.read(biImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        biBufferImage = images[0];
        bird_width = biBufferImage.getWidth();
        bird_height = biBufferImage.getHeight();
    }

    // 小鸟展翅动画方法
    int index = 0;

    public void change() {
        index++;
        biBufferImage = images[index / 3 % 8];
    }

    // 小鸟移动的方法
    double ratation; // 倾斜角度

    public void move_go() {
        double v0 = speed;
        s = v0 * t - 0.5 * g * t * t;
        double vt = v0 - g * t;
        speed = vt;
        bird_y = bird_y - (int) s;
        ratation = s / 16;
        if (bird_y <= bird_height / 2) {
            bird_y = bird_height / 2;
        }
    }

    // 重新飞翔
    public void refly() {
        speed = 20;
    }

    // 撞击地面
    public boolean hit(Ground ground) {
        return bird_y + bird_height / 2 >= ground.ground_y;
    }

    // 撞击钢管
    public boolean hit(Column column) {
        int left_x = column.column_x - column.width / 2 - bird_width / 2;
        int right_x = column.column_x + column.width / 2 + bird_width / 2;
        int top_y = column.column_y - column.gap / 2 + bird_height / 2 - 5;
        int down_y = column.column_y + column.gap / 2 - bird_height / 2 + 5;
        if (bird_x > left_x && bird_x < right_x) {
            if (bird_y > top_y && bird_y < down_y) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }
}