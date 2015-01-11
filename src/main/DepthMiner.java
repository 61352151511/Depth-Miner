package main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import main.data.Data;
import main.data.SaveData;
import main.keyconfig.KeyConfig;
import main.keyconfig.KeyEnum;
import main.world.Position;

public class DepthMiner extends Canvas implements Runnable {
	private static final long serialVersionUID = -8518293955116639411L;
	public static final int WIDTH = 800;
	public static final int HEIGHT = 600;
	private static ArrayList<String> ImagePaths = new ArrayList<>();
	private static HashMap<String, Image> Images = new HashMap<String, Image>();
	private static boolean[] arrowKeys = new boolean[] {false, false, false, false};
	private static boolean inventoryOpen;
	private static boolean resetPrompt = false;
	private static int[] mousePosTracker = new int[] {0, 0};
	
	private Thread thread;
	private boolean running = false;
	private BufferedImage img;
	private int fps;
	
	public DepthMiner() {
		Dimension size = new Dimension(WIDTH, HEIGHT);
		setPreferredSize(size);
		setMinimumSize(size);
		setMaximumSize(size);
		img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
	}
	
	private void start() {
		if (running) return;
		running = true;
		thread = new Thread(this);
		thread.start();
	}
	
	@SuppressWarnings("unused") private void stop() {
		if (!running) return;
		running = false;
		try { thread.join(); }
		catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	@Override public void run() {
		int frames = 0;
		double unprocessedSeconds = 0;
		long previousTime = System.nanoTime();
		double secondsPerTick = 1 / 60.0;
		int tickCount = 0;
		boolean ticked = false;
		
		while (running) {
			long currentTime = System.nanoTime();
			long passedTime = currentTime - previousTime;
			previousTime = currentTime;
			unprocessedSeconds += passedTime / 1000000000.0;
			
			while (unprocessedSeconds > secondsPerTick) {
				tick();
				unprocessedSeconds -= secondsPerTick;
				ticked = true;
				tickCount ++;
				if (tickCount % 60 == 0) {
					fps = frames;
					previousTime += 1000;
					frames = 0;
				}
			}
			if (ticked) {
				render();
				frames ++;
			}
			render();
			frames ++;
		}
	}
	
	private void tick() {
		Data.getRobot().update(arrowKeys, inventoryOpen);
	}
	
	private void render() {
		BufferStrategy bufferStrategy = this.getBufferStrategy();
		if (bufferStrategy == null) {
			createBufferStrategy(3);
			return;
		}
		
		Graphics g = bufferStrategy.getDrawGraphics();
		g.drawImage(img, 0, 0, WIDTH + 10, HEIGHT + 10, null);
		
		/* BACKGROUND */
		int depth = Data.getDepth();
		DrawingUtilities.attemptImageDraw(g, getImage("Background"), 0, 0, WIDTH + 10, HEIGHT + 10, null);
		
		/* ROBOT ANCHORING */
		if (Data.getRobot().isAnchored()) {
			int Y = Data.getRobot().getBlockY();
			boolean B25 = false;
			if (Data.getRobot().getY() % 50 > 25) {
				B25 = true;
				Y ++;
			}
			if (Y > -1 && Data.getRobot().getY() > - 30) {
				int X = Data.getRobot().getBlockX();
				while (true) {
					g.fillRect(X * 50, ((B25 ? Y - 1 : Y) * 50) - depth + (Data.getRobot().getY() % 50) + 20, 50, 10);
					X --;
					if (Data.blockNull(new Position(X, Y))) break;
					if (!Data.isBlockMined(new Position(X, Y))) break;
				}
				X = Data.getRobot().getBlockX();
				while (true) {
					g.fillRect(X * 50, ((B25 ? Y - 1 : Y) * 50) - depth + (Data.getRobot().getY() % 50) + 20, 50, 10);
					X ++;
					if (Data.blockNull(new Position(X, Y))) break;
					if (!Data.isBlockMined(new Position(X, Y))) break;
				}
			} else {
				g.fillRect(0, (Y * 50) - depth + (Data.getRobot().getY() % 50) + 20, WIDTH + 10, 10);
			}
		}
		
		/* ROBOT */
		boolean flame = arrowKeys[2];
		if (inventoryOpen || Data.getRobot().isAnchored()) flame = false;
		DrawingUtilities.attemptImageDraw(g, getImage("Robot" + (flame ? "_flame" : "")), Data.getRobot().getX(), Data.getRobot().getY() - depth, 50, 50, null);
		
		/* SCREEN MOVING */
		if (Data.getRobot().getY() - depth > HEIGHT - 200) depth ++;
		if (Data.getRobot().getY() - depth < HEIGHT - 400 && depth > -200) depth --;
		
		/* BLOCKS */
		int extra = ((depth + 200) / 50);
		if (extra >= 5) {
			SaveData.unloadChunk(extra - 5);
		}
		SaveData.unloadChunk(extra + (HEIGHT / 50) + 1);
		int var = extra > 0 ? 0 - extra : 0;
		if (var < -4) var = -4;
		for (int y = var; y < (HEIGHT / 50) + 1; y ++) {
			if (!SaveData.chunkLoaded(y + extra)) {
				SaveData.loadChunk(y + extra);
			}
			for (int x = 0; x < (WIDTH / 50) + 1; x ++) {
				if (!Data.blockNull(new Position(x, y + extra))) {
					if (!Data.isBlockMined(new Position(x, y + extra))) {
						DrawingUtilities.attemptImageDraw(g, getImage(Data.getBlock(new Position(x, y + extra)).getType()), x * 50, ((y + extra) * 50) - depth, 50, 50, null);
					}
				}
			}
		}
		
		/* INVENTORY */
		if (inventoryOpen) {
			int xSize = WIDTH / 2;
			int ySize = HEIGHT / 2;
			int xStart = (WIDTH / 2) - (xSize / 2);
			int yStart = (HEIGHT / 2) - (ySize / 2);
			g.setColor(new Color(0, 0, 0, 200));
			g.fillRect(xStart, yStart, xSize, ySize);
			int yBlocks = yStart + 10;
			g.setColor(new Color(255, 255, 255, 255));
			g.setFont(new Font(g.getFont().getFontName(), Font.PLAIN, 14));
			String[] BlockTypes = new String[] {"Grass", "Dirt", "Stone", "Coal", "Iron", "Gold", "Diamond"};
			for (String blocktype : BlockTypes) {
				if (Data.getMinedBlocks().containsKey(blocktype)) {
					DrawingUtilities.attemptImageDraw(g, getImage(blocktype), xStart + 10, yBlocks, 32, 32, null);
					g.drawString(blocktype + " (" + Data.getMinedBlocks().get(blocktype) + ")", xStart + 52, yBlocks + 16 + (g.getFontMetrics().getHeight() / 2));
					yBlocks += 42;
				}
			}
		}
		
		/* RESET CONFIRMATION */
		
		if (resetPrompt) {
			g.setFont(new Font(g.getFont().getFontName(), Font.PLAIN, 14));
			g.setColor(new Color(0, 0, 0, 200));
			String Confirm = "Press Y to confirm reset.";
			String Cancel = "Press N to cancel.";
			int xSize = g.getFontMetrics().stringWidth(Confirm);
			int ySize = g.getFontMetrics().getHeight() * 3;
			int xStart = (WIDTH / 2) - (xSize / 2);
			int yStart = (HEIGHT / 2) - (ySize / 2);
			xSize = xSize > g.getFontMetrics().stringWidth(Cancel) ? xSize : g.getFontMetrics().stringWidth("Press any other key to cancel.");
			xSize += 10;
			g.fillRect(xStart, yStart, xSize, ySize);
			g.setColor(new Color(255, 255, 255, 255));
			g.drawString(Confirm, xStart + 5, yStart + g.getFontMetrics().getHeight());
			g.drawString(Cancel, xStart + 5, yStart + (g.getFontMetrics().getHeight() * 2));
		}
		
		/* FPS COMES LAST */
		g.setFont(new Font(g.getFont().getFontName(), Font.BOLD, 18));
		g.setColor(Color.YELLOW);
		g.drawString(fps + "FPS", 5, g.getFontMetrics().getHeight());
		
		/* UPDATE DEPTH */
		Data.setDepth(depth);

		g.dispose();
		bufferStrategy.show();
	}
	
	private static Image getImage(String s) {
		if (Images.containsKey("/textures/" + s + ".png")) {
			return Images.get("/textures/" + s + ".png");
		}
		return null;
	}
	
	private static void loadImagePaths() {
		ImagePaths.add("/textures/Background.png");
		ImagePaths.add("/textures/Grass.png");
		ImagePaths.add("/textures/Dirt.png");
		ImagePaths.add("/textures/Stone.png");
		ImagePaths.add("/textures/Coal.png");
		ImagePaths.add("/textures/Iron.png");
		ImagePaths.add("/textures/Gold.png");
		ImagePaths.add("/textures/Diamond.png");
		ImagePaths.add("/textures/Robot.png");
		ImagePaths.add("/textures/Robot_flame.png");
	}
	
	private static void addImage(String s, Image img) {
		Images.put(s, img);
	}
	
	public static void loadImages() {
		for (String s : ImagePaths) {
			try { addImage(s, ImageIO.read(DepthMiner.class.getResource(s))); }
			catch (IOException e) {}
			catch (IllegalArgumentException e) { System.out.println("Missing texture: " + s); }
		}
	}
	
	public static int getFrameWidth() { return WIDTH; }
	public static int getFrameHeight() { return HEIGHT; }
	
	public static void main(String[] args) {
		loadImagePaths();
		loadImages();
		Data.load();
		DepthMiner test = new DepthMiner();
		test.addMouseListener(new MouseListener() {
			@Override public void mouseClicked(MouseEvent e) {
				if (!inventoryOpen) {
					Position robotLoc = Data.getRobot().getPosition();
					if (Math.abs((robotLoc.getX() / 50) - (e.getX() / 50)) <= 1 && Math.abs(robotLoc.getY() / 50 - (e.getY() + Data.getDepth()) / 50) <= 1) {
						Data.mineBlock(new Position(e.getX() / 50, (e.getY() + Data.getDepth()) / 50));
					}
				}
			}
			@Override public void mouseEntered(MouseEvent e) {}
			@Override public void mouseExited(MouseEvent e) {}
			@Override public void mousePressed(MouseEvent e) {}
			@Override public void mouseReleased(MouseEvent e) {
				if (KeyConfig.getTouchMode()) {
					arrowKeys[0] = false;
					arrowKeys[1] = false;
					arrowKeys[2] = false;
					arrowKeys[3] = false;
				}
			}
		});
		test.addMouseMotionListener(new MouseMotionListener() {
			@Override public void mouseDragged(MouseEvent e) {
				int x = e.getX();
				int y = e.getY();
				if (KeyConfig.getTouchMode()) {
					if (x < mousePosTracker[0]) arrowKeys[0] = true;
					if (x > mousePosTracker[0]) arrowKeys[1] = true;
					if (x == mousePosTracker[0]) {
						arrowKeys[0] = false;
						arrowKeys[1] = false;
					}
					if (y < mousePosTracker[1]) arrowKeys[2] = true;
					if (y > mousePosTracker[1]) arrowKeys[3] = true;
					if (y == mousePosTracker[1]) {
						arrowKeys[2] = false;
						arrowKeys[3] = false;
					}
				}
				mousePosTracker[0] = e.getX();
				mousePosTracker[1] = e.getY();
			}
			@Override public void mouseMoved(MouseEvent e) {
				mousePosTracker[0] = e.getX();
				mousePosTracker[1] = e.getY();
			}
		});
		test.addKeyListener(new KeyListener() {
			@Override public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_T) KeyConfig.setTouchMode(!KeyConfig.getTouchMode());
				if (!KeyConfig.getTouchMode()) {
					if (e.getKeyCode() == KeyConfig.getKey(KeyEnum.LEFT)) arrowKeys[0] = true;
					if (e.getKeyCode() == KeyConfig.getKey(KeyEnum.RIGHT)) arrowKeys[1] = true;
					if (e.getKeyCode() == KeyConfig.getKey(KeyEnum.UP)) arrowKeys[2] = true;
					if (e.getKeyCode() == KeyConfig.getKey(KeyEnum.DOWN)) arrowKeys[3] = true;
				}
				if (e.getKeyCode() == KeyConfig.getKey(KeyEnum.ANCHOR) && !inventoryOpen) Data.getRobot().toggleAnchor();
				if (e.getKeyCode() == KeyConfig.getKey(KeyEnum.INVENTORY)) inventoryOpen = !inventoryOpen;
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					if (inventoryOpen) {
						inventoryOpen = false;
						return;
					}
				}
				if (e.getKeyCode() == KeyConfig.getKey(KeyEnum.SAVE) && e.isControlDown()) {
					SaveData.saveLoadedChunks();
					Data.save();
				}
				if (e.getKeyCode() == KeyConfig.getKey(KeyEnum.RESET) && e.isControlDown()) {
					resetPrompt = true;
				}
				if (resetPrompt) {
					if (e.getKeyCode() == KeyConfig.getKey(KeyEnum.RESET_CONFIRM)) {
						resetPrompt = false;
						inventoryOpen = false;
						Data.reset();
					} else if (e.getKeyCode() == KeyConfig.getKey(KeyEnum.RESET_CANCEL)) {
						resetPrompt = false;
					}
				}
			}
			@Override public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyConfig.getKey(KeyEnum.LEFT)) arrowKeys[0] = false;
				if (e.getKeyCode() == KeyConfig.getKey(KeyEnum.RIGHT)) arrowKeys[1] = false;
				if (e.getKeyCode() == KeyConfig.getKey(KeyEnum.UP)) arrowKeys[2] = false;
				if (e.getKeyCode() == KeyConfig.getKey(KeyEnum.DOWN)) arrowKeys[3] = false;
			}
			@Override public void keyTyped(KeyEvent e) {}
		});
		final JFrame frame = new JFrame();
		frame.add(test);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Depth Miner");
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		frame.addFocusListener(new FocusListener() {
			@Override public void focusGained(FocusEvent e) {}
			@Override public void focusLost(FocusEvent e) {
				for (int i = 0; i < 4; i ++) arrowKeys[i] = false;
				SaveData.saveLoadedChunks();
				Data.save();
			}
		});
		frame.addWindowListener(new WindowListener() {
			@Override public void windowActivated(WindowEvent e) {}
			@Override public void windowClosed(WindowEvent e) {}
			@Override public void windowClosing(WindowEvent e) {
				SaveData.saveLoadedChunks();
				Data.save();
			}
			@Override public void windowDeactivated(WindowEvent e) {}
			@Override public void windowDeiconified(WindowEvent e) {}
			@Override public void windowIconified(WindowEvent e) {}
			@Override public void windowOpened(WindowEvent e) {}
		});
		
		test.start();
	}

	public static int getChunkSize() {
		return WIDTH / 50;
	}
}