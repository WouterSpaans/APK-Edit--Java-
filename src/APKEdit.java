import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.UIManager;

import javax.swing.JTabbedPane;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.JLabel;
import javax.swing.JTextField;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.Cursor;
import brut.androlib.AndrolibException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;
import java.awt.Component;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.factories.FormFactory;
import javax.swing.ImageIcon;
import java.awt.Insets;
import javax.swing.SwingConstants;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.CaretEvent;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;

public class APKEdit {

	private JFrame frmApkEdit;
	private static String apkPath = null;
	private static String apkBackupPath = null;	
	private static String tempPath = null;
	//private static String manifestXmlFile = null;

	XmlParsing.AndroidManifest AndroidManifest;

	static EditLanguage windowEditLanguages;
	
	private JTextField tfName = new JTextField();
	private	JButton pbLoading = new JButton("");
	private JButton btnIconHDPI = new JButton("");
	private JButton btnIconMDPI = new JButton("");
	private JButton btnIconLDPI = new JButton("");
	private JButton btnLanguages = new JButton("Languages");
	private JButton btnBrowse = new JButton("Browse");
	private JButton btnRevert = new JButton("Revert");
	private JButton btnOK = new JButton("OK");
	private JButton btnCancel = new JButton("Cancel");
	private JButton btnApply = new JButton("Apply");

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {

		// Set the look and feel to that of the system
		try { UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() ); } catch ( Exception e ) { System.err.println( e ); }

		// Get tempPath
		tempPath = getTempPath();  

		// Get apkFilePath
		apkPath = getFileName(args);

		
		// Get AndroidManifset.xml Path
		//manifestXmlFile = getXmlManifestFile(tempPath);

		// Exit application if we don't have a valid file to work with
		if (apkPath == null) exitApplication();

		apkBackupPath = apkPath + ".bck";
		
		// Default generated code...
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					APKEdit window = new APKEdit();					
					window.frmApkEdit.setVisible(true);
					
					windowEditLanguages = new EditLanguage(tempPath);
					windowEditLanguages.Languages.setVisible(false);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	//	private static String getXmlManifestFile(String TempPath) {
	//		return TempPath + System.getProperty("file.separator") + "AndroidManifest.xml";
	//	}

	private static String getTempPath() {
		// Get random temp folder
		java.util.Random f = new java.util.Random();
		return System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + "APKEdit" + f.nextInt();
	}

	private static String getFileName(String[] args) {
		if (args.length == 0)
		{
			// Ask user - AWT Implementation
			Frame f = new Frame();
			FileDialog fd1 = new FileDialog(f, "Select a Android Package (.apk) File to Open", FileDialog.LOAD);
			fd1.setFile("*.apk");
			fd1.setFilenameFilter(new FilenameFilter(){ @Override public boolean accept(File dir, String name) { return (name.endsWith(".apk"));}});
			fd1.setVisible(true);

			String apkDirectory = fd1.getDirectory();
			String apkFile = fd1.getFile();

			f.dispose();

			return 	(apkDirectory != null && apkFile != null) ? 
					apkDirectory + System.getProperty("file.separator") + apkFile :
						null;
		}
		else			
		{
			return args[0];	
		}
	}
	
	private static String getIconName() {
		
		// Ask user - AWT Implementation
		Frame f = new Frame();
		FileDialog fd1 = new FileDialog(f, "Select a icon file to Open", FileDialog.LOAD);
		fd1.setFile("*.png");
		fd1.setFilenameFilter(new FilenameFilter(){ @Override public boolean accept(File dir, String name) { return (name.endsWith(".png"));}});
		fd1.setVisible(true);

		String pngDirectory = fd1.getDirectory();
		String pngFile = fd1.getFile();

		f.dispose();

		return 	(pngDirectory != null && pngFile != null) ? 
				pngDirectory + System.getProperty("file.separator") + pngFile :
					null;

	}

	private static void decompileAPK(String ApkPath, String TempPath) {
		String[] argsApkTool = {"d", "-f", ApkPath, TempPath};
		try {
			brut.apktool.Main.main(argsApkTool);
		} catch (AndrolibException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void exitApplication() {
		// remove temp path
		deleteDir(new File(tempPath));
		System.exit(0);
	}

	// Deletes all files and subdirectories under dir.
	// Returns true if all deletions were successful.
	// If a deletion fails, the method stops attempting to delete and returns false.
	public static boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i=0; i<children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}

		// The directory is now empty so delete it
		return dir.delete();
	} 
	public static boolean deleteFile(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i=0; i<children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}

		// The directory is now empty so delete it
		return dir.delete();
	} 
	

	/**
	 * Create the application.
	 */
	public APKEdit() {
		initialize();

		decompileApk();
	}

	private void decompileApk() {
		SwingWorker worker = new SwingWorker() {
			@Override
			protected Object doInBackground() throws Exception {

				// disableUI
				tfName.setEnabled(false);
				pbLoading.setVisible(true);
				btnIconHDPI.setEnabled(false);
				btnIconMDPI.setEnabled(false);
				btnIconLDPI.setEnabled(false);
				btnLanguages.setEnabled(false);
				btnBrowse.setEnabled(false);
				btnRevert.setEnabled(false);
				btnOK.setEnabled(false);
				btnCancel.setEnabled(false);
				btnApply.setEnabled(false);

				// Decompile APK file...
				decompileAPK(apkPath, tempPath);

				// Parse Manifest XML
				AndroidManifest = XmlParsing.parseManifestXml(tempPath);

				return null;
			}


			@Override
			protected void done() {
				
				// Read all languages into language window!
				try {
					windowEditLanguages.ReadLanguages();
				} catch (XPathExpressionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SAXException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ParserConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				tfName.setEnabled(true);
				pbLoading.setVisible(false);
				
				// Enable UI
				tfName.setEnabled(true);
				tfName.setText(AndroidManifest.ApplicationName);

				if (AndroidManifest.PathIconHDPI != null)
				{
					btnIconHDPI.setEnabled(true);
					btnIconHDPI.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
					btnIconHDPI.setIcon(getIcon(AndroidManifest.PathIconHDPI, 76, 76));
					
				}
				
				if (AndroidManifest.PathIconMDPI != null)
				{
					btnIconMDPI.setEnabled(true);
					btnIconMDPI.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
					btnIconMDPI.setIcon(getIcon(AndroidManifest.PathIconMDPI, 52, 52));
				}
				
				if (AndroidManifest.PathIconLDPI != null)
				{
					btnIconLDPI.setEnabled(true);
					btnIconLDPI.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
					btnIconLDPI.setIcon(getIcon(AndroidManifest.PathIconLDPI, 36, 36));
				}

				
				btnLanguages.setEnabled(true);
				btnBrowse.setEnabled(true);
				btnRevert.setEnabled(new File(apkBackupPath).exists());
				btnOK.setEnabled(true);
				btnCancel.setEnabled(true);
				btnApply.setEnabled(true);

			}
		};

		worker.execute();
	}
	
	private void compileAPK(String TempPath, String ApkPath)
	{
		String[] argsApkTool = {"b", TempPath};
		try {
			brut.apktool.Main.main(argsApkTool);
		} catch (AndrolibException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		// copy compiled apk to ApkPath
	}
	private void compileApk() {
		SwingWorker worker = new SwingWorker() {
			@Override
			protected Object doInBackground() throws Exception {

				// disableUI
				tfName.setEnabled(false);
				pbLoading.setVisible(true);
				btnIconHDPI.setEnabled(false);
				btnIconMDPI.setEnabled(false);
				btnIconLDPI.setEnabled(false);
				btnLanguages.setEnabled(false);
				btnBrowse.setEnabled(false);
				btnRevert.setEnabled(false);
				btnOK.setEnabled(false);
				btnCancel.setEnabled(false);
				btnApply.setEnabled(false);

				// Decompile APK file...
				compileAPK(tempPath, apkPath);

				// Parse Manifest XML
				// AndroidManifest = XmlParsing.parseManifestXml(tempPath);

				return null;
			}


			@Override
			protected void done() {
				
				// Read all languages into language window!
				try {
					windowEditLanguages.ReadLanguages();
				} catch (XPathExpressionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SAXException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ParserConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				tfName.setEnabled(true);
				pbLoading.setVisible(false);
				
				// Enable UI
				tfName.setEnabled(true);
				tfName.setText(AndroidManifest.ApplicationName);

				if (AndroidManifest.PathIconHDPI != null)
				{
					btnIconHDPI.setEnabled(true);
					btnIconHDPI.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
					btnIconHDPI.setIcon(getIcon(AndroidManifest.PathIconHDPI, 76, 76));
					
				}
				
				if (AndroidManifest.PathIconMDPI != null)
				{
					btnIconMDPI.setEnabled(true);
					btnIconMDPI.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
					btnIconMDPI.setIcon(getIcon(AndroidManifest.PathIconMDPI, 52, 52));
				}
				
				if (AndroidManifest.PathIconLDPI != null)
				{
					btnIconLDPI.setEnabled(true);
					btnIconLDPI.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
					btnIconLDPI.setIcon(getIcon(AndroidManifest.PathIconLDPI, 36, 36));
				}

				
				btnLanguages.setEnabled(true);
				btnBrowse.setEnabled(true);
				btnRevert.setEnabled(new File(apkBackupPath).exists());
				btnOK.setEnabled(true);
				btnCancel.setEnabled(true);
				btnApply.setEnabled(true);

			}
		};

		worker.execute();
	}

	private void browseClicked() throws IOException {
		// TODO Auto-generated method stub
		Desktop desktop = Desktop.getDesktop();
		desktop.open(new File(tempPath));
		//Desktop.Action.OPEN();
	}
	
	private void nameChanged() {
		// TODO Auto-generated method stub
		// Save name to xml file
		
		if (AndroidManifest.ContainsApplicationName)
		{
			AndroidManifest.ApplicationName = tfName.getText();
			System.out.println("Save to AndroidManifest.xml " + tfName.getText());	
			try {
				XmlParsing.saveManifestXml(tempPath, AndroidManifest);
			} catch (XPathExpressionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TransformerFactoryConfigurationError e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TransformerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
		{
			System.out.println("Save to Settings.xml " + tfName.getText());	
			try {
				XmlParsing.saveSettingsXml(tempPath, new Locale("en-US"), AndroidManifest.ApplicationNameAttribute, tfName.getText());
			} catch (XPathExpressionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TransformerFactoryConfigurationError e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TransformerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
	}
	
	private void openLanguages() {
		// TODO Auto-generated method stub
		windowEditLanguages.Languages.setVisible(true);
		
	}
	
	private void btnIconHDPIClicked() {
		String newIconPath = getIconName();
		if (newIconPath != null)
		{
			copyfile(newIconPath, AndroidManifest.PathIconHDPI);
			btnIconHDPI.setIcon(getIcon(AndroidManifest.PathIconHDPI, 76, 76));
		}
	}
	private void btnIconMDPIClicked() {
		String newIconPath = getIconName();
		if (newIconPath != null)
		{
			copyfile(newIconPath, AndroidManifest.PathIconHDPI);
			btnIconHDPI.setIcon(getIcon(AndroidManifest.PathIconMDPI, 52, 52));
		}
	}
	private void btnIconLDPIClicked() {
		String newIconPath = getIconName();
		if (newIconPath != null)
		{
			copyfile(newIconPath, AndroidManifest.PathIconHDPI);
			btnIconHDPI.setIcon(getIcon(AndroidManifest.PathIconLDPI, 36, 36));
		}
	}
	
	private static ImageIcon getIcon(String Path, int Width, int Height)
	{
		ImageIcon icon = new ImageIcon(Path);
		icon.getImage().flush();
		icon = new ImageIcon(Path);		
		Image img = icon.getImage();  
		Image newimg = img.getScaledInstance(76, 76,  java.awt.Image.SCALE_SMOOTH);  
		return new ImageIcon(newimg);  
	}
	
	private void btnRevertClicked()
	{
		// copy backup file to original
		copyfile(apkBackupPath, apkPath); 
		
		// remove backup file
		new File(apkBackupPath).delete();
		
		btnRevert.setEnabled(false);
		
		// Re- decompile
		decompileApk();
	}
	
	private void btnOKClicked() {
		// TODO Auto-generated method stub
		
		// Make backup if needed
		if (!new File(apkBackupPath).exists())
			copyfile(apkPath, apkBackupPath); 
		
		// Compile
		// ...
		compileApk();
		
		// Close app
		exitApplication();
		
	}
	private void btnCancelClicked() {
		// TODO Auto-generated method stub
		
		// Close app
		exitApplication();
		
	}
	private void btnApplyClicked() {
		// TODO Auto-generated method stub
		
		// Make backup if needed
		if (!new File(apkBackupPath).exists())
			copyfile(apkPath, apkBackupPath); 
		
		// Compile
		// ...
		compileApk();
		
		// Enable revert link		
		btnRevert.setEnabled(true);
	}
	
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmApkEdit = new JFrame();
		frmApkEdit.setTitle("APK Edit");
		frmApkEdit.addWindowListener(new WindowAdapter(){ @Override public void windowClosing(WindowEvent e) { exitApplication(); }});
		frmApkEdit.setIconImage(Toolkit.getDefaultToolkit().getImage(APKEdit.class.getResource("icon.png")));
		frmApkEdit.setBounds(100, 100, 377, 515);
		frmApkEdit.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmApkEdit.getContentPane().setLayout(new BorderLayout(0, 0));

		// TOP
		JPanel pnlNorth = new JPanel();
		FlowLayout flowLayout_2 = (FlowLayout) pnlNorth.getLayout();
		flowLayout_2.setVgap(3);
		flowLayout_2.setHgap(3);
		frmApkEdit.getContentPane().add(pnlNorth, BorderLayout.NORTH);

		// LEFT
		JPanel pnlWest = new JPanel();
		FlowLayout flowLayout = (FlowLayout) pnlWest.getLayout();
		flowLayout.setVgap(3);
		flowLayout.setHgap(3);
		frmApkEdit.getContentPane().add(pnlWest, BorderLayout.WEST);

		// CENTER
		JTabbedPane pnlCenter = new JTabbedPane(JTabbedPane.TOP);
		frmApkEdit.getContentPane().add(pnlCenter, BorderLayout.CENTER);

		JPanel pnlGeneral = new JPanel();
		pnlGeneral.setOpaque(false);
		pnlCenter.addTab("General", null, pnlGeneral, null);
		pnlGeneral.setLayout(new BorderLayout(0, 0));

		JPanel pnlGeneralTop = new JPanel();
		pnlGeneral.add(pnlGeneralTop, BorderLayout.NORTH);
		pnlGeneralTop.setOpaque(false);
		pnlGeneralTop.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("76px"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("52px"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("36px"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC,},
				new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("top:76px"),}));

		JLabel lblName = new JLabel("Name:");
		pnlGeneralTop.add(lblName, "2, 2, right, default");
		KeyListener keyListener = new KeyListener() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			@Override
			public void keyReleased(KeyEvent arg0) {
				// TODO Auto-generated method stub
				nameChanged();
			}
			@Override
			public void keyTyped(KeyEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		    };
	    tfName.addKeyListener(keyListener);
		


		pnlGeneralTop.add(tfName, "4, 2, 7, 1, fill, default");
		tfName.setColumns(10);

		JLabel lblIcons = new JLabel("Icons:");
		pnlGeneralTop.add(lblIcons, "2, 4");
		btnIconHDPI.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				btnIconHDPIClicked();
			}

			
		});
		btnIconHDPI.setContentAreaFilled(false);
		btnIconHDPI.setHorizontalTextPosition(SwingConstants.CENTER);

		btnIconHDPI.setPreferredSize(new Dimension(76, 76));
		pnlGeneralTop.add(btnIconHDPI, "4, 4");
		btnIconMDPI.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				btnIconMDPIClicked();
			}
		});
		btnIconMDPI.setContentAreaFilled(false);

		btnIconMDPI.setPreferredSize(new Dimension(52, 52));
		pnlGeneralTop.add(btnIconMDPI, "6, 4");
		btnIconLDPI.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				btnIconLDPIClicked();
			}
		});
		btnIconLDPI.setContentAreaFilled(false);

		btnIconLDPI.setPreferredSize(new Dimension(36, 36));
		pnlGeneralTop.add(btnIconLDPI, "8, 4");

		JPanel pnlGeneralCenter = new JPanel();
		pnlGeneralCenter.setOpaque(false);
		pnlGeneral.add(pnlGeneralCenter, BorderLayout.CENTER);
		pnlGeneralCenter.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("70px"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC,},
				new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("70px"),
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_ROWSPEC,}));

		pbLoading.setHorizontalTextPosition(SwingConstants.CENTER);
		pbLoading.setContentAreaFilled(false);
		pbLoading.setBorderPainted(false);
		pbLoading.setMargin(new Insets(0, 0, 0, 0));
		pbLoading.setIcon(new ImageIcon(APKEdit.class.getResource("LoadingAnim.gif")));
		pbLoading.setPreferredSize(new Dimension(70, 70));
		pnlGeneralCenter.add(pbLoading, "4, 4");

		JPanel pnlGeneralBottom = new JPanel();
		pnlGeneralBottom.setOpaque(false);
		pnlGeneral.add(pnlGeneralBottom, BorderLayout.SOUTH);
		pnlGeneralBottom.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				ColumnSpec.decode("4dlu:grow"),
				FormFactory.DEFAULT_COLSPEC,
				ColumnSpec.decode("4dlu:grow"),
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,},
				new RowSpec[] {
				RowSpec.decode("25px"),
				FormFactory.RELATED_GAP_ROWSPEC,}));
		btnLanguages.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				openLanguages();
			}

			
		});

		btnLanguages.setBorder(null);
		btnLanguages.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btnLanguages.setContentAreaFilled(false);
		btnLanguages.setForeground(new Color(0, 102, 204));
		pnlGeneralBottom.add(btnLanguages, "2, 1");
		btnBrowse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// Open browser window!
				try {
					browseClicked();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			
		});

		btnBrowse.setBorder(null);
		btnBrowse.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btnBrowse.setContentAreaFilled(false);
		btnBrowse.setForeground(new Color(0, 102, 204));
		pnlGeneralBottom.add(btnBrowse, "4, 1");
		btnRevert.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				btnRevertClicked();
			}
		});

		btnRevert.setBorder(null);
		btnRevert.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btnRevert.setContentAreaFilled(false);
		btnRevert.setForeground(new Color(0, 102, 204));
		pnlGeneralBottom.add(btnRevert, "6, 1");

		JPanel pnlAbout = new JPanel();
		pnlAbout.setOpaque(false);
		pnlCenter.addTab("About", null, pnlAbout, null);

		// RIGHT
		JPanel pnlEast = new JPanel();
		FlowLayout flowLayout_1 = (FlowLayout) pnlEast.getLayout();
		flowLayout_1.setVgap(3);
		flowLayout_1.setHgap(3);
		frmApkEdit.getContentPane().add(pnlEast, BorderLayout.EAST);

		// BOTTOM
		JPanel pnlSouth = new JPanel();
		frmApkEdit.getContentPane().add(pnlSouth, BorderLayout.SOUTH);
		pnlSouth.setLayout(new FlowLayout(FlowLayout.RIGHT, 8, 7));
		btnOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				btnOKClicked();
			}			
		});

		btnOK.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		btnOK.setPreferredSize(new Dimension(82, 25));
		pnlSouth.add(btnOK);
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				btnCancelClicked();
			}
		});
		btnCancel.setPreferredSize(new Dimension(82, 25));

		pnlSouth.add(btnCancel);
		btnApply.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				btnApplyClicked();
			}
		});

		btnApply.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		btnApply.setPreferredSize(new Dimension(82, 25));
		pnlSouth.add(btnApply);

	}
	private static void copyfile(String srFile, String dtFile){
		try{
			File f1 = new File(srFile);
			File f2 = new File(dtFile);
			InputStream in = new FileInputStream(f1);

			//For Append the file.
			//  OutputStream out = new FileOutputStream(f2,true);

			//For Overwrite the file.
			OutputStream out = new FileOutputStream(f2);

			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0){
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
			System.out.println("File copied.");
		}
		catch(FileNotFoundException ex){
			System.out.println(ex.getMessage() + " in the specified directory.");
			System.exit(0);
		}
		catch(IOException e){
			System.out.println(e.getMessage());  
		}
	}

}
