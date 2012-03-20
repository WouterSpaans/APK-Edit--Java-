import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JComboBox;
import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Rectangle;
import javax.swing.border.EmptyBorder;
import javax.swing.JPanel;
import java.awt.Toolkit;


public class EditLanguage {

	JFrame Languages;
	private JTable table;
	private String tempPath = null;
	private static JComboBox comboBox;
	DefaultTableModel model = new DefaultTableModel(
			new Object[][] {
			},
				new String[] {
					"Label", "Value"
				}
			) {
				boolean[] columnEditables = new boolean[] {
					false, true
				};
				public boolean isCellEditable(int row, int column) {
					return columnEditables[column];
				}
			};
	/**
	 * Launch the application.
	 */
//	public static void main(String TempPath) {
//		EventQueue.invokeLater(new Runnable() {
//			public void run() {
//				try {
//					EditLanguage window = new EditLanguage();
//					window.frame.setVisible(true);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		});
//	}

	/**
	 * Create the application.
	 */
	public EditLanguage(String TempPath) {
		tempPath = TempPath;
		initialize();
		
		// Add available languages...
		
	}
	
	/**
     * Convert a string based locale into a Locale Object.
     * Assumes the string has form "{language}_{country}_{variant}".
     * Examples: "en", "de_DE", "_GB", "en_US_WIN", "de__POSIX", "fr_MAC"
     *  
     * @param localeString The String
     * @return the Locale
     */
    public static Locale getLocaleFromString(String localeString)
    {
        if (localeString == null)
        {
            return null;
        }
        localeString = localeString.trim();
        if (localeString.toLowerCase().equals("default"))
        {
            return Locale.getDefault();
        }

        // Extract language
        int languageIndex = localeString.indexOf('-');
        String language = null;
        if (languageIndex == -1)
        {
            // No further "_" so is "{language}" only
            return new Locale(localeString, "");
        }
        else
        {
            language = localeString.substring(0, languageIndex);
        }

        // Extract country
        int countryIndex = localeString.indexOf('-', languageIndex + 1);
        String country = null;
        if (countryIndex == -1)
        {
            // No further "_" so is "{language}_{country}"
            country = localeString.substring(languageIndex+1);
            
            // remove r if it is there
            if (country.startsWith("r"))
            {
            	country = country.substring(1);                
            }
            return new Locale(language, country);
        }
        else
        {
            // Assume all remaining is the variant so is "{language}_{country}_{variant}"
            country = localeString.substring(languageIndex+1, countryIndex);
            String variant = localeString.substring(countryIndex+1);
            return new Locale(language, country, variant);
        }
    }
	
    private static List<Locale> locales = new ArrayList<Locale>();
    private JPanel panel;
	public void ReadLanguages() throws XPathExpressionException, SAXException, IOException, ParserConfigurationException
	{
		
		// Read all directories in tempPath/res/values...
		String pathToScan = XmlParsing.ConvertSeperatorToOsSpecific(tempPath + "/res");
		System.out.println("pathToScan = " + pathToScan);
		
		File myDirectory = new File(pathToScan);

		File[] allFiles = myDirectory.listFiles();
		for (File file : allFiles)
		{
			if (file.isDirectory())
			{
				String directoryName = file.getName();
				// check if it starts with a values
				if (directoryName.startsWith("values"))
				{
					System.out.println("found a values folder:  = " + directoryName);
					
					// skip default folder
					if (directoryName.length() != 6)
					{
						String locale = directoryName.substring(7);
						System.out.println("stipping values: " + locale);
						
						Locale.getISOCountries();
						Locale l = getLocaleFromString(locale);
						
						if (new File(XmlParsing.ConvertSeperatorToOsSpecific(pathToScan + "/" + directoryName + "/strings.xml")).exists())
						{
							if (l != null){	
								System.out.println("found locale: " + l.getISO3Language() + l.getISO3Language());
								locales.add(l);
							}
						}
						// add to dropdown
						

					}
					else
					{
						// Add defualt language to localoes	
						Locale l = getLocaleFromString("en-US");
						
						locales.add(l);
						System.out.println("found locale: " + l.getLanguage() + l.getCountry());
						
					}
					
				}
			}
		}
		
		
		// fill dropdown
		for (Locale l : locales)
		{
			comboBox.addItem(l.getDisplayName());			
		}
		
		Locale selectedLocale = locales.get(comboBox.getSelectedIndex());
		
		
		// fill table with xml
		//Get Map in Set interface to get key and value
        
		Iterator<?> it = XmlParsing.getValuesXml(tempPath, selectedLocale).entrySet().iterator();
		while(it.hasNext())
		{
			// key=value separator this by Map.Entry to get key and value
            Map.Entry m =(Map.Entry)it.next();

            // getKey is used to get key of Map
            String key=(String)m.getKey();

            // getValue is used to get value of key in Map
            String value=(String)m.getValue();

            model.addRow(new Object[]{key, value});

            System.out.println("Key :"+key+"  Value :"+value);

		}
	}
	
	private void changeLanguage() {
		// TODO Auto-generated method stub
Locale selectedLocale = locales.get(comboBox.getSelectedIndex());
		
		
		// fill table with xml
		//Get Map in Set interface to get key and value
        
		Iterator<?> it = null;
		try {
			it = XmlParsing.getValuesXml(tempPath, selectedLocale).entrySet().iterator();
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
		
		while (model.getRowCount()>0){
			model.removeRow(0);
			}
//		 for(int i=1; i<model.getRowCount(); i++){
//			 model.removeRow(i);
//             //System.out.println("Count is: " + i);
//        }

		
		
		while(it.hasNext())
		{
			// key=value separator this by Map.Entry to get key and value
            Map.Entry m =(Map.Entry)it.next();

            // getKey is used to get key of Map
            String key=(String)m.getKey();

            // getValue is used to get value of key in Map
            String value=(String)m.getValue();
            model.addRow(new Object[]{key, value});

            System.out.println("Key :"+key+"  Value :"+value);

		}
	}
	
	/**
	 * Initialize the contents of the frame.
	 */
	
	public static Locale getSelectedLocale()
	{
		return locales.get(comboBox.getSelectedIndex());
	}
	private void initialize() {
		Languages = new JFrame();
		Languages.setTitle("Languages");
		Languages.setIconImage(Toolkit.getDefaultToolkit().getImage(APKEdit.class.getResource("icon.png")));
		BorderLayout borderLayout = (BorderLayout) Languages.getContentPane().getLayout();
		Languages.setBounds(100, 100, 450, 300);
		Languages.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		
		panel = new JPanel();
		panel.setBorder(new EmptyBorder(10, 10, 10, 10));
		Languages.getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(new BorderLayout(0, 0));
		
		comboBox = new JComboBox();
		panel.add(comboBox, BorderLayout.NORTH);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBorder(new EmptyBorder(10, 0, 0, 0));
		panel.add(scrollPane, BorderLayout.CENTER);
		
	

		table = new JTable();
		table.setModel(model);
		table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
               int selrow = table.getSelectedRow();
               int selcol = table.getSelectedColumn();

                if(e.getClickCount() == 1) {
                	table.editCellAt(selrow, selcol );

//                    if(selrow == 0) {
//                        lblcell.setText("");
//                    }
                }
            }}
);
		table.getModel().addTableModelListener(new TableModelListener() {

		     

			@Override
			public void tableChanged(TableModelEvent arg0) {
				// TODO Auto-generated method stub
				System.out.println("Edited row: " + arg0.getFirstRow());
				if (arg0.getType() != arg0.DELETE)
				{
					System.out.println("Edited label: " + model.getValueAt(arg0.getFirstRow(), 0) );
					System.out.println("Edited value: " + model.getValueAt(arg0.getFirstRow(), 1) );
					try {
						XmlParsing.saveSettingsXml(tempPath, getSelectedLocale(), (String)model.getValueAt(arg0.getFirstRow(), 0), (String)model.getValueAt(arg0.getFirstRow(), 1));
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
		    });
		
				table.getColumnModel().getColumn(0).setPreferredWidth(150);
				table.getColumnModel().getColumn(0).setMaxWidth(150);
				scrollPane.setViewportView(table);
		comboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				changeLanguage();
			}

			
		});
				
	}

}
