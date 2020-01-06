package Main;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;
import com.fazecast.jSerialComm.*;

public class Main {
	private static final String VERSION = "1.1.0.0";
	public static boolean debug = false;
	public static boolean logging = false;
	public static boolean log_ready = false;
	public static String branch = "master";
	public static String projectUri = "https://raw.githubusercontent.com/lukasaldersley/POLO_LUEFTER_STEUERUNG/";
	public static String downloadTargetUri = "https://github.com/lukasaldersley/POLO_LUEFTER_STEUERUNG/raw/";
	private static boolean fromUpdate;
	private static FileWriter logWriter;
	private static File logFile;
	private static File oldFile;
	private static URL source;
	private static ReadableByteChannel readableByteChannelFromSource;
	private static FileOutputStream fileOutputStream;
	private static BufferedReader br;
	private static String PORTNAME = "USB-SERIAL CH340";
	private static JFrame frame;
	private static boolean supposedToRun;
	private static boolean NEEDS_WRITE;

	/**
	 * Checks whether an Update is available
	 * 
	 * @return true if an update is available, false if no update is available
	 */
	public static boolean checkIfUpdateAvailable() {
		try {
			source = new URL(projectUri + branch + "/ready/VERSION");

			logln(projectUri + branch + "/ready/VERSION");
			br = new BufferedReader(new InputStreamReader(source.openStream()));
			String RemoteVersion = br.readLine().trim();
			// in String.split muss ein '.' escaped werden
			// http://www.java-examples.com/java-string-split-example
			// 06.09.2017 3:46 Uhr
			// (codezeilen 31-41 im beispiel auf der website)
			String[] remote = RemoteVersion.split("\\.");
			String[] local = VERSION.split("\\.");
			for (int i = 0; i < 4; i++) {

				logln(local[i] + " | " + remote[i]);
				if (Integer.parseInt(remote[i]) > Integer.parseInt(local[i])) {
					return true;
				} else if (Integer.parseInt(local[i]) == Integer.parseInt(remote[i])) {
					;
				} else {
					return false;
				}
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * After the Programm has checked whether an update from github is available it
	 * performs this update
	 */
	public static void updateJar() {
		logln("\n\nUPDATING\n\n");
		try {
			// quelle wie oben bei der config datei
			oldFile = new File("PoloLuefterSteuerung.jar");
			oldFile.renameTo(new File("PoloLuefterSteuerung_old.jar"));
			// oldFile.delete();
			// fileUrl:
			// "https://raw.githubusercontent.com/lukasaldersley/Raumklima/master/PublicVersion/VERSION"
			source = new URL(downloadTargetUri + branch + "/ready/PoloLuefterSteuerung.jar");
			readableByteChannelFromSource = Channels.newChannel(source.openStream());
			fileOutputStream = new FileOutputStream("PoloLuefterSteuerung.jar");
			fileOutputStream.getChannel().transferFrom(readableByteChannelFromSource, 0, Long.MAX_VALUE);
			fileOutputStream.close();
			String parameters = "";
			if (debug) {
				parameters += " -d";
			}
			if (logging) {
				parameters += " -l";
			}
			oldFile = new File("PoloLuefterSteuerung_old.jar");
			oldFile.delete();
			Runtime.getRuntime().exec("java -jar PoloLuefterSteuerung.jar" + parameters);
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
			logln(e);
			oldFile = new File("PoloLuefterSteuerung.jar");
			if (!oldFile.exists()) {
				oldFile = new File("PoloLuefterSteuerung_old.jar");
				oldFile.renameTo(new File("PoloLuefterSteuerung.jar"));
			}
		}
	}

	public static void log(Object msg) {// anstatt von System.out.print() wird log() verwendet; folgende methoden analog
		if (debug) {
			System.out.print(msg);
		}
		try {
			if (logging) {
				if (log_ready) {// falls logdatei schon vorhanden => reinschreiben
					logWriter.write(String.valueOf(msg));
					logWriter.flush();
				} else {// sonst erst ertellen und dann reinschreiben
					logFile = new File("PoloLuefterSteuerungLog_"
							+ new SimpleDateFormat("dd.MM.yyyy_HH,mm").format(new Date()) + ".txt");
					logWriter = new FileWriter(logFile);
					log_ready = true;
					logWriter.write(String.valueOf(msg));
					logWriter.flush();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void logln(Object msg) {
		if (debug) {
			System.out.println(msg);
		}
		try {
			if (logging) {
				if (log_ready) {
					logWriter.write(String.valueOf(msg) + System.lineSeparator());
					logWriter.flush();
				} else {
					logFile = new File(
							"RaumklimaLog_" + new SimpleDateFormat("dd.MM.yyyy_HH,mm").format(new Date()) + ".txt");
					logWriter = new FileWriter(logFile);
					log_ready = true;
					logWriter.write(String.valueOf(msg) + System.lineSeparator());
					logWriter.flush();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void logln() {
		if (debug) {
			System.out.println();
		}
		try {
			if (logging) {
				if (log_ready) {
					logWriter.write(System.lineSeparator());
					logWriter.flush();
				} else {
					logFile = new File(
							"RaumklimaLog_" + new SimpleDateFormat("dd.MM.yyyy_HH,mm").format(new Date()) + ".txt");
					logWriter = new FileWriter(logFile);
					log_ready = true;
					logWriter.write(System.lineSeparator());
					logWriter.flush();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static int factorial(int in) {// irgendwie fï¿½r die cmd arguments
		int res = 1;
		for (int i = in; i > 0; i--) {
			res *= i;
		}
		return res;
	}

	public static String permutation(String str) { // irgendwie fï¿½r die cmd arguments
		String x = permutation("", str, "");
		return x.substring(0, x.length() - 1);
	}

	private static String permutation(String prefix, String str, String returnValue) {// irgendwie fï¿½r die cmd
		// arguments
		int n = str.length();
		if (n == 0) {
			System.out.println(prefix);
			returnValue += prefix + "|";
		} else {
			for (int i = 0; i < n; i++)
				returnValue = permutation(prefix + str.charAt(i), str.substring(0, i) + str.substring(i + 1, n),
						returnValue);
		}
		return returnValue;
	}

	public static boolean testPerm(String testAgainst, String permutator) {// irgendwie fï¿½r die cmd arguments
		System.out.println(factorial(permutator.length()));
		String[] testCases = permutation(permutator).split("|");
		for (String T : testCases) {
			if (testAgainst.equalsIgnoreCase(T) || testAgainst.equalsIgnoreCase("/" + T)
					|| testAgainst.equalsIgnoreCase("-" + T)) {
				return true;
			}
		}
		return false;
	}

	public static boolean tp(String args, String t) {// irgendwie fï¿½r die cmd arguments
		return (t.length() == 1) ? eq(args, t) : testPerm(args, t);
	}

	public static boolean eq(String a, String b) {// irgendwie fï¿½r die cmd arguments
		return (a.equalsIgnoreCase(b) || a.equalsIgnoreCase("-" + b) || a.equalsIgnoreCase("/" + b)
				|| a.equalsIgnoreCase("--" + b));
	}


	public static void main(String[] args) {// Startet das Programm (ggf mnit debug/logging)
		for (int i = 0; i < args.length; i++) {
			if (eq(args[i], "log") || tp(args[i], "l")) {
				logging = true;
				log("");
				System.out.println("Dateiname fï¿½r die Aufzeichnung: " + logFile.getName());
			} else if (eq(args[i], "debug") || tp(args[i], "d")) {
				debug = true;
				logln("Debugmodus aktiviert");
			} else if (tp(args[i], "u")) {
				fromUpdate = true;
			} else if (tp(args[i], "du")) {
				fromUpdate = true;
				debug = true;
				logln("Debugmodus aktiviert");
			} else if (tp(args[i], "lu")) {
				fromUpdate = true;
				logging = true;
				log("");
				System.out.println("Dateiname fï¿½r die Aufzeichnung: " + logFile.getName());
			} else if (tp(args[i], "dlu")) {
				debug = true;
				logln("Debugmodus aktiviert");
				logging = true;
				log("");
				System.out.println("Dateiname fï¿½r die Aufzeichnung: " + logFile.getName());
			} else if (tp(args[i], "dl")) {
				debug = true;
				logln("Debugmodus aktiviert");
				logging = true;
				log("");
				System.out.println("Dateiname fï¿½r die Aufzeichnung: " + logFile.getName());
			} else if (tp(args[i], "?") || eq(args[i], "help") || tp(args[i], "h")) {
				System.out.println(
						"\nBefehlszeilenparameter\n\n\"d\" oder \"debug\"\t\tDebugmodus\n\"l\" oder \"log\"\t\t\tAusgabe in Datei abspeichern\n"
								+ "\tSie dï¿½rden anstatt -dl natï¿½rlich auch -d -l schreiben.\n");
				System.exit(0);
			}
		}

		frame = new JFrame("Polo Lueftersteuerung");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel initPanel = new JPanel();
		initPanel.setLayout(new BoxLayout(initPanel, BoxLayout.Y_AXIS));
		JLabel initLabel = new JLabel("Initialisiere Verbindung. Bitte warten.");
		initLabel.setFont(new Font("Arial Bold", Font.BOLD, 30));
		initPanel.add(initLabel);
		frame.add(initPanel);
		// frame.setBounds(500, 500, 200, 200);
		frame.setPreferredSize(new Dimension(600, 300));
		// frame.setMaximumSize(new Dimension(800,600));
		frame.setMinimumSize(new Dimension(600, 300));
		frame.setLocationRelativeTo(null);
		frame.validate();
		frame.setVisible(true);
		frame.revalidate();

		JPanel workingPanel = new JPanel();
		workingPanel.setLayout(new BoxLayout(workingPanel, BoxLayout.Y_AXIS));

		JPanel upper = new JPanel();
		upper.setLayout(new BoxLayout(upper, BoxLayout.X_AXIS));
		upper.setMaximumSize(new Dimension(frame.getWidth(), 30));

		JPanel millisPanel = new JPanel();
		JLabel millisLabel = new JLabel("millisekunden: ");
		JTextField millisField = new JTextField("----------", 10);
		millisField.setEditable(false);
		millisField.setFocusable(false);

		JPanel minsPanel = new JPanel();
		JLabel minsLabel = new JLabel("Minuten: ");
		JTextField minsField = new JTextField("-----", 5);
		minsField.setEditable(false);
		minsField.setFocusable(false);

		JPanel secsPanel = new JPanel();
		JLabel secsLabel = new JLabel("Sekunden: ");
		JTextField secsField = new JTextField("--.--", 5);
		secsField.setEditable(false);
		secsField.setFocusable(false);

		millisPanel.add(millisLabel);
		millisPanel.add(millisField);

		minsPanel.add(minsLabel);
		minsPanel.add(minsField);

		secsPanel.add(secsLabel);
		secsPanel.add(secsField);

		JPanel lower = new JPanel();
		lower.setLayout(new BoxLayout(lower, BoxLayout.X_AXIS));
		lower.setMaximumSize(new Dimension(frame.getWidth(), 30));

		upper.add(new JLabel("IST: "));
		upper.add(millisPanel);
		upper.add(minsPanel);
		upper.add(secsPanel);

		lower.add(new JLabel("SOLL: "));

		JPanel setMinsPanel = new JPanel();
		JLabel setMinsLabel = new JLabel("Minuten: ");
		JTextField setMinsField = new JTextField("-----", 5);
		setMinsPanel.add(setMinsLabel);
		setMinsPanel.add(setMinsField);

		JPanel setSecsPanel = new JPanel();
		JLabel setSecsLabel = new JLabel("Sekunden: ");
		JTextField setSecsField = new JTextField("--.--", 5);
		setSecsPanel.add(setSecsLabel);
		setSecsPanel.add(setSecsField);

		lower.add(setMinsPanel);
		lower.add(setSecsPanel);

		JButton button = new JButton("SET");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				logln("request write");
				setMinsField.setEditable(false);
				setMinsField.setFocusable(false);
				setSecsField.setEditable(false);
				setSecsField.setFocusable(false);
				NEEDS_WRITE = true;
				button.setEnabled(false);
				button.setText("WAIT");
				logln("disabled inputs and button");
			}
		});
		lower.add(button);

		workingPanel.add(upper);
		workingPanel.add(lower);

		logln("Java Version: " + System.getProperty("java.version"));
		logln("Software Version: "+VERSION);
		logln(Charset.defaultCharset());
		// Check for Software updates
		if (!fromUpdate) {
			if (checkIfUpdateAvailable()) {
				updateJar();
			}
		}

		SerialPort[] ports = SerialPort.getCommPorts();
		for (SerialPort port : ports) {
			String name = port.getDescriptivePortName();
			if (name.contains("Bluetooth")) {
				logln("skipped bluetooth serial port");
				continue;
			}
			if (name.contains(PORTNAME)) {
				logln("Trying Port: " + name);
				try {
					port.setBaudRate(115200);
					boolean procOpen = port.openPort();
					logln("openPort(): " + procOpen);
					boolean sanityOpen = port.isOpen();
					logln("isOpen(): " + sanityOpen);
					if (!(procOpen && sanityOpen)) {
						logln("Maybe this isn't the right device, the device is in use or I don't have the permissions");
						continue;
					}
					// serialScanner = new Scanner(new InputStreamReader(port.getInputStream()));
					// serialWriter = new BufferedWriter(new
					// OutputStreamWriter(port.getOutputStream()));
					String in = null;
					Thread.sleep(2000);
					supposedToRun = true;
					while (supposedToRun) {
						if (NEEDS_WRITE) {
							int mins;
							double secs;
							try {
								mins = Integer.parseInt(setMinsField.getText());
							} catch (NumberFormatException ex) {
								ex.printStackTrace();
								mins = 0;
								setMinsField.setText("0");
							}
							try {
							 secs = Double.parseDouble(setSecsField.getText());
							}catch(NumberFormatException ex) {
								ex.printStackTrace();
								secs=0;
								setSecsField.setText("0.0");
							}
							long total=((60 * 1000L * mins) + (long) (1000L * secs));
							if(total<1000) {
								
								logln("discarding write request as time would be set to 0");
								setMinsField.setEditable(true);
								setMinsField.setFocusable(true);
								setSecsField.setEditable(true);
								setSecsField.setFocusable(true);
								NEEDS_WRITE=false;
								button.setEnabled(true);
								button.setText("SET");
								logln("re-enabled inputs and button");
								JOptionPane.showMessageDialog(frame, "Die Eingegebene Zeit darf nicht unter 1 Sekunde sein. Übertragung wurde abgebrochen. Bitte einen anderen Wert eingeben.", "FEHLER - SENDEN ABGEBROCHEN", JOptionPane.ERROR_MESSAGE);
								continue;
							}
							String toWrite = "SET_TIME " + total + "\n";
							// serialWriter.write(toWrite);
							// serialWriter.flush();
							byte[] bytes = toWrite.getBytes();
							port.writeBytes(bytes, bytes.length);
							logln("sent message to controller: " + toWrite);
							setMinsField.setEditable(true);
							setMinsField.setFocusable(true);
							setSecsField.setEditable(true);
							setSecsField.setFocusable(true);
							NEEDS_WRITE = false;
							button.setEnabled(true);
							button.setText("UNCONFIRMED");
							logln("re-enabled inputs and button");
						}
						if (port.bytesAvailable() < 10) {
							Thread.sleep(1000);
							continue;
						} else {
							in = "";
							boolean done = false;
							while (!done) {
								int available = port.bytesAvailable();
								logln("there are " + available + " bytes available to be read");
								byte[] rxBytes = new byte[available];
								logln("actually read " + port.readBytes(rxBytes, available) + " bytes");
								in += new String(rxBytes);
								Thread.sleep(100);
								if (port.bytesAvailable() == 0) {
									done = true;
								} else {
									logln("there's more; attempting to read the rest");
								}
							}
						}
						/*
						 * try { in = serialScanner.nextLine(); } catch (NoSuchElementException ex) {
						 * log("."); Thread.sleep(2000); continue; }
						 */
						logln("RX: " + in+" </RX>");
						in = in.replace("\r", "");
						for (String part : in.split("\n")) {
							if (part.contains("To set the delay, type 'SET_TIME x', where x is the number of milliseconds to wait")) {
								logln("correct device is found");
								frame.remove(initPanel);
								frame.add(workingPanel);
								frame.revalidate();
								byte[] versionQuery="VERSION".getBytes();
								port.writeBytes(versionQuery, versionQuery.length);
								while(port.bytesAvailable()<7) {
									Thread.sleep(200);
								}
								byte[] version=new byte[port.bytesAvailable()];
								logln("recieved "+port.readBytes(version, version.length)+" bytes while querying for the Firmware Version");
								logln("Firmware Version: "+new String(version));
								/*for(byte b:version) {
									logln((int)(b));
								}*/
							} else if (part.startsWith("DELAY: ")) {
								part = part.replace("DELAY: ", "");
								part = part.replace("(", "");
								part = part.replace("Minutes, ", "");
								part = part.replace(" Seconds )", "");
								logln(part);
								String[] arr = part.split(" ");
								millisField.setText(arr[0]);
								minsField.setText(arr[1]);
								secsField.setText(arr[2]);
							} else if (part.startsWith("WARNING - delay is under")) {
								logln("recieves small time warning; just accepting this for now");
								byte[] txArr = "YES\n".getBytes();
								port.writeBytes(txArr, txArr.length);
							}
							else if(part.contains("Minutes")&&part.contains("Seconds")) {
								String old=part;
								part=part.replace("Minutes, ", "");
								part=part.replace(" Seconds", "");
								logln(part);
								String[] arr=part.split(" ");
								minsField.setText(arr[0]);
								secsField.setText(arr[1]);
								try {
									millisField.setText(String.valueOf((long)(Integer.parseInt(arr[0])*60+Double.parseDouble(arr[1]))*1000L));
								}
								catch(NumberFormatException ex) {
									ex.printStackTrace();
									JOptionPane.showMessageDialog(frame, "Entweder der Controller hat Unsinn geantwortet,\r\noder das parsen der Antwort ging schief. Hier ist die empfangene Nachricht:\r\n"+old, "PARSE ERROR", JOptionPane.WARNING_MESSAGE);
								}
							}
							else if(part.contains("SET INITIAL DELAY AS:")) {
								;
							}
							else {
								if(part.contains("DONE")) {
									button.setText("SET");
								}
								JOptionPane.showMessageDialog(frame, "Sonstige Nachricht: \r\n"+part,"INFORMATION",JOptionPane.INFORMATION_MESSAGE);
							}
						}
					}
				} catch (Exception ex) {
					logln("crashed program");
					ex.printStackTrace();
				}
			}
		}
		frame.remove(initPanel);
		JPanel errPanel = new JPanel();
		JLabel noConnWarning = new JLabel("Es konnte KEINE Verbindung hergestellt werden.");
		JLabel noConnWarning1 = new JLabel("Ist der Controller richtig angeschlossen?");
		JLabel noConnWarning2 = new JLabel("Verwendet kein anderes Programm die seriellen Ports?");
		noConnWarning.setFont(new Font("Arial Bold", Font.BOLD, 25));
		noConnWarning1.setFont(new Font("Arial Bold", Font.BOLD, 20));
		noConnWarning2.setFont(new Font("Arial Bold", Font.BOLD, 20));
		errPanel.add(noConnWarning);
		errPanel.add(noConnWarning1);
		errPanel.add(noConnWarning2);
		frame.add(errPanel);
		frame.revalidate();
		logln("giving up - can't open any ports");
	}
}
