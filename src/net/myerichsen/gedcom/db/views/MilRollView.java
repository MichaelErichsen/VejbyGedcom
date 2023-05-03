package net.myerichsen.gedcom.db.views;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationAdapter;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import net.myerichsen.gedcom.db.models.MilRollModel;
import net.myerichsen.gedcom.util.Fonkod;

/**
 * Input application for military rolls
 *
 * @author Michael Erichsen
 * @version 3. maj 2023
 *
 */
// TODO Move properties to main file
// TODO Find possible GEDCOM ID's
// TODO Validate input before saving to Derby
// TODO Create military roll search view
// TODO Merge military roll relocations into relocation view
// TODO Find a way to merge entries from following years
// TODO Button to store URI property
public class MilRollView {
	private static final String PROPERTIES_PATH = System.getProperty("user.home") + "/MilRollView.properties";
	private static final String AMT = "Kronborg";
	private static final String AAR = "1792";
	private static final String HOVEDRULLE = "Hovedrulle";
	private static final String LAEGDNR = "6";
	private static final String SOGN = "Vejby";
	private static final String DBPATH = System.getProperty("user.home") + "/laegd";
	private static final String DBSCHEMA = "LAEGD";
	private static final String DASH_DATE = "\\d*-\\d{2}-\\d{4}";
	private static final String EIGHT_DIGITS = "\\d{8}";
	private static final String FOUR_DIGITS = "\\d{4}";
	private static final String US_DASH_DATE = "\\d{4}-\\d{2}-\\d*";
	private static final String URI = "https://www.sa.dk/ao-soegesider/da/billedviser?epid=16481031#17028,656192";

	/**
	 * Launch the application.
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			final MilRollView window = new MilRollView();
			window.open();
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	protected Shell shlLgdsrulleindtastning;
	private Text textDbpath;
	private Text textDbschema;
	private Text textAmt;
	private Text textAar;
	private Combo comboRulletype;
	private Text textLaegdNr;
	private Text textSogn;
	private Composite compositeData;
	private Text textLitra;
	private Text textGlLoebenr;
	private Text textNyLoebenr;
	private Text textFader;
	private Text textSoen;
	private Text textFoedested;
	private Text textAlder;
	private Text textStoerrelseitommer;
	private Text textOphold;
	private Text textAnmaerkninger;
	private Text textFoedt;
	private Text textGedcomid;
	private Composite compositeRulleButtons;
	private Button btnGem;
	private Button btnRyd;
	private Button btnHent;
	private Properties props;
	private Composite compositeButtons;
	private Button btnGemIndtastning;
	private Fonkod fk;
	private Composite compositeBrowser;
	private Text textUri;
	private Combo messageComboBox;
	private Button btnSgEfterGedcom;

	/**
	 * Clear roll fields
	 */
	protected void clearRollFields() {
		textDbpath.setText("");
		textDbschema.setText("");
		textAmt.setText("");
		textAar.setText("");
		comboRulletype.setText("");
		textLaegdNr.setText("");
		textSogn.setText("");
	}

	/**
	 * Construct a patronymicon name for the son
	 *
	 * @param fader
	 * @param soen
	 * @return
	 */
	private String constructName(String fader, String soen) {
		final String[] nameParts = fader.split(" ");
		return soen + " " + nameParts[0] + "sen";
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shlLgdsrulleindtastning = new Shell();
		shlLgdsrulleindtastning.setSize(1041, 543);
		shlLgdsrulleindtastning.setText("L\u00E6gdsrulleindtastning");
		shlLgdsrulleindtastning.setLayout(new GridLayout(1, false));

		final Composite compositeRulle = new Composite(shlLgdsrulleindtastning, SWT.BORDER);
		compositeRulle.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		compositeRulle.setLayout(new GridLayout(6, false));

		final Label lblDatabase = new Label(compositeRulle, SWT.NONE);
		lblDatabase.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblDatabase.setText("Database");

		textDbpath = new Text(compositeRulle, SWT.BORDER);
		textDbpath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textDbpath.setText(props.getProperty("dbpath"));

		final Label lblSchema = new Label(compositeRulle, SWT.NONE);
		lblSchema.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblSchema.setText("Schema");

		textDbschema = new Text(compositeRulle, SWT.BORDER);
		textDbschema.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textDbschema.setText(props.getProperty("dbschema"));

		final Label lblAmt = new Label(compositeRulle, SWT.NONE);
		lblAmt.setText("Amt");

		textAmt = new Text(compositeRulle, SWT.BORDER);
		textAmt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textAmt.setText(props.getProperty("amt"));

		final Label lblr = new Label(compositeRulle, SWT.NONE);
		lblr.setText("\u00C5r");

		textAar = new Text(compositeRulle, SWT.BORDER);
		textAar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textAar.setText(props.getProperty("aar"));

		final Label lblRulletype = new Label(compositeRulle, SWT.NONE);
		lblRulletype.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblRulletype.setText("Rulletype");

		comboRulletype = new Combo(compositeRulle, SWT.NONE);
		comboRulletype.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		final String[] rulleTyper = { "Hovedrulle", "Tilgangsrulle" };
		comboRulletype.setItems(rulleTyper);
		comboRulletype.setText(props.getProperty("rulletype"));

		final Label lblLgdNr = new Label(compositeRulle, SWT.NONE);
		lblLgdNr.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblLgdNr.setText("L\u00E6gd nr");

		textLaegdNr = new Text(compositeRulle, SWT.BORDER);
		textLaegdNr.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textLaegdNr.setText(props.getProperty("laegdnr"));

		final Label lblSogn = new Label(compositeRulle, SWT.NONE);
		lblSogn.setText("Sogn");

		textSogn = new Text(compositeRulle, SWT.BORDER);
		textSogn.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textSogn.setText(props.getProperty("sogn"));

		compositeRulleButtons = new Composite(compositeRulle, SWT.NONE);
		compositeRulleButtons.setLayout(new RowLayout(SWT.HORIZONTAL));
		compositeRulleButtons.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));

		btnGem = new Button(compositeRulleButtons, SWT.NONE);
		btnGem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				storeProperties();
			}
		});
		btnGem.setText("Gem");

		btnHent = new Button(compositeRulleButtons, SWT.NONE);
		btnHent.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getProperties();
				textDbpath.setText(props.getProperty("dbpath"));
				textDbschema.setText(props.getProperty("dbschema"));
				textAmt.setText(props.getProperty("amt"));
				textAar.setText(props.getProperty("aar"));
				comboRulletype.setText(props.getProperty("rulletype"));
				textLaegdNr.setText(props.getProperty("laegdnr"));
				textSogn.setText(props.getProperty("sogn"));
			}
		});
		btnHent.setText("Hent");

		btnRyd = new Button(compositeRulleButtons, SWT.NONE);
		btnRyd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				clearRollFields();
			}
		});
		btnRyd.setText("Ryd");
		new Label(compositeRulle, SWT.NONE);
		new Label(compositeRulle, SWT.NONE);

		compositeBrowser = new Composite(shlLgdsrulleindtastning, SWT.BORDER);
		compositeBrowser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		compositeBrowser.setLayout(new GridLayout(2, false));

		final Label lblUri = new Label(compositeBrowser, SWT.NONE);
		lblUri.setText("URI");

		textUri = new Text(compositeBrowser, SWT.BORDER);
		textUri.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textUri.setText(props.getProperty("uri"));

		final Browser browser = new Browser(compositeBrowser, SWT.NONE);
		browser.addLocationListener(new LocationAdapter() {
			@Override
			public void changed(LocationEvent event) {
				final String location = event.location;

				if (location.contains("billedviser")) {
					textUri.setText(location);
					props.setProperty("uri", location);
					setMessage("URI ændret til " + location);
				}
			}
		});
		browser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		browser.setUrl(textUri.getText());

		compositeData = new Composite(shlLgdsrulleindtastning, SWT.BORDER);
		compositeData.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		compositeData.setLayout(new GridLayout(6, false));

		final Label lblLitra = new Label(compositeData, SWT.NONE);
		lblLitra.setText("Litra");

		textLitra = new Text(compositeData, SWT.BORDER);
		textLitra.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		final Label lblGlLbenr = new Label(compositeData, SWT.NONE);
		lblGlLbenr.setBounds(0, 0, 55, 15);
		lblGlLbenr.setText("Gl. l\u00F8benr");

		textGlLoebenr = new Text(compositeData, SWT.BORDER);
		textGlLoebenr.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		final Label lblNyLbenr = new Label(compositeData, SWT.NONE);
		lblNyLbenr.setBounds(0, 0, 55, 15);
		lblNyLbenr.setText("Nyt l\u00F8benr");

		compositeButtons = new Composite(shlLgdsrulleindtastning, SWT.NONE);
		compositeButtons.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		compositeButtons.setLayout(new RowLayout(SWT.HORIZONTAL));

		btnSgEfterGedcom = new Button(compositeButtons, SWT.NONE);
		btnSgEfterGedcom.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				searchForGedcomID();
			}
		});
		btnSgEfterGedcom.setText("S\u00F8g efter GEDCOM ID");

		btnGemIndtastning = new Button(compositeButtons, SWT.NONE);
		btnGemIndtastning.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					saveToDb();
				} catch (final Exception e1) {
					setErrorMessage(e1.getMessage());
				}
			}
		});
		btnGemIndtastning.setText("Gem indtastning");

		textNyLoebenr = new Text(compositeData, SWT.BORDER);
		textNyLoebenr.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		final Label lblFader = new Label(compositeData, SWT.NONE);
		lblFader.setText("Fader");

		textFader = new Text(compositeData, SWT.BORDER);
		textFader.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		final Label lblSn = new Label(compositeData, SWT.NONE);
		lblSn.setText("S\u00F8n");

		textSoen = new Text(compositeData, SWT.BORDER);
		textSoen.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		final Label lblFdested = new Label(compositeData, SWT.NONE);
		lblFdested.setText("F\u00F8dested");

		textFoedested = new Text(compositeData, SWT.BORDER);
		textFoedested.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		final Label lblAlder = new Label(compositeData, SWT.NONE);
		lblAlder.setText("Alder");

		textAlder = new Text(compositeData, SWT.BORDER);
		textAlder.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		final Label lblStrITommer = new Label(compositeData, SWT.NONE);
		lblStrITommer.setText("Str. i tommer");

		textStoerrelseitommer = new Text(compositeData, SWT.BORDER);
		textStoerrelseitommer.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		final Label lblOphold = new Label(compositeData, SWT.NONE);
		lblOphold.setText("Ophold");

		textOphold = new Text(compositeData, SWT.BORDER);
		textOphold.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		final Label lblAnmrkninger = new Label(compositeData, SWT.NONE);
		lblAnmrkninger.setText("Anm\u00E6rkninger");

		textAnmaerkninger = new Text(compositeData, SWT.BORDER);
		textAnmaerkninger.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		final Label lblFdt = new Label(compositeData, SWT.NONE);
		lblFdt.setText("F\u00F8dt");

		textFoedt = new Text(compositeData, SWT.BORDER);
		textFoedt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		final Label lblGedcomId = new Label(compositeData, SWT.NONE);
		lblGedcomId.setText("GEDCOM ID");

		textGedcomid = new Text(compositeData, SWT.BORDER);
		textGedcomid.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		messageComboBox = new Combo(shlLgdsrulleindtastning, SWT.READ_ONLY);
		messageComboBox.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

	}

	/**
	 * Get properties from file or create them
	 */
	private void getProperties() {
		props = new Properties();

		try {
			final InputStream input = new FileInputStream(PROPERTIES_PATH);
			props.load(input);
		} catch (final Exception e) {

			props.setProperty("amt", AMT);
			props.setProperty("aar", AAR);
			props.setProperty("rulletype", HOVEDRULLE);
			props.setProperty("laegdnr", LAEGDNR);
			props.setProperty("sogn", SOGN);
			props.setProperty("dbpath", DBPATH);
			props.setProperty("dbschema", DBSCHEMA);
			props.setProperty("uri", URI);

			storeProperties();
			setMessage("Egenskaber gemt i " + PROPERTIES_PATH);
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		final Display display = Display.getDefault();
		fk = new Fonkod();
		getProperties();
		createContents();
		shlLgdsrulleindtastning.open();
		shlLgdsrulleindtastning.layout();
		while (!shlLgdsrulleindtastning.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Save to Derby database
	 *
	 * @throws SQLException
	 *
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	protected void saveToDb() throws SQLException {
		final MilRollModel lm = new MilRollModel();

		lm.setAar(textAar.getText());
		try {
			lm.setAlder(Integer.parseInt(textAlder.getText()));
		} catch (final Exception e) {
			lm.setAlder(0);
		}
		lm.setAmt(textAmt.getText());
		lm.setAnmaerkninger(textAnmaerkninger.getText());
		lm.setDbPath(textDbpath.getText());
		lm.setDbSchema(textDbschema.getText());

		lm.setFader(textFader.getText());
		try {
			lm.setFaderFon(fk.generateKey(lm.getFader()));
		} catch (final Exception e1) {
			lm.setFaderFon("");
		}
		lm.setFoedeSted(textFoedested.getText());
		try {
			lm.setFoedt(string2Date(textFoedt.getText()));
		} catch (final ParseException e1) {
			lm.setFoedt(new Date(1, 1, 1001));
		}
		lm.setGedcomId(textGedcomid.getText());
		try {
			lm.setGlLoebeNr(Integer.parseInt(textGlLoebenr.getText()));
		} catch (final Exception e) {
			lm.setGlLoebeNr(0);
		}
		try {
			lm.setLaegdNr(Integer.parseInt(textLaegdNr.getText()));
		} catch (final Exception e) {
			lm.setLaegdNr(0);
		}
		lm.setLitra(textLitra.getText());
		try {
			lm.setNyLoebeNr(Integer.parseInt(textNyLoebenr.getText()));
		} catch (final Exception e) {
			lm.setNyLoebeNr(0);
		}
		lm.setOphold(textOphold.getText());
		lm.setRulleType(comboRulletype.getText());
		lm.setSoen(textSoen.getText());
		lm.setSogn(textSogn.getText());
		try {
			lm.setStoerrelseITommer(new BigDecimal(textStoerrelseitommer.getText()));
		} catch (final Exception e) {
			lm.setStoerrelseITommer(new BigDecimal(0));
		}

		lm.setNavn(constructName(lm.getFader(), lm.getSoen()));
		try {
			lm.setSoenFon(fk.generateKey(lm.getNavn()));
		} catch (final Exception e) {
			lm.setSoenFon("");
		}

		final String result = lm.saveToDb();
		setMessage(result);

	}

	/**
	 * Search for id for phonetic name of son and age or birth. Present result in a
	 * popup with selection possibility
	 */
	protected void searchForGedcomID() {
		// TODO Auto-generated method stub

	}

	/**
	 * Set the message in the message combo box
	 *
	 * @param string
	 *
	 */
	public void setErrorMessage(String string) {
		setMessage(string);
		messageComboBox.setBackground(new Color(255, 0, 0, 255));
	}

	/**
	 * Set the message in the message combo box
	 *
	 * @param string
	 *
	 */
	public void setMessage(String string) {

		final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
		final LocalTime localTime = LocalTime.now();
		messageComboBox.add(dtf.format(localTime) + " " + string, 0);
		messageComboBox.select(0);
		messageComboBox.setBackground(new Color(255, 255, 255, 255));
//		final int lastItem = Integer.parseInt(props.getProperty("msgLogLen"));

		if (messageComboBox.getItemCount() > 20) {
			messageComboBox.remove(20);
		}
	}

	/**
	 * Store properties in file
	 */
	protected void storeProperties() {
		try {
			final OutputStream output = new FileOutputStream(PROPERTIES_PATH);
			props.store(output, "MilRollView properties");
		} catch (final Exception e2) {
			setErrorMessage("Kan ikke gemme egenskaber i " + PROPERTIES_PATH);
			e2.printStackTrace();
		}
	}

	private Date string2Date(String string) throws ParseException {
		SimpleDateFormat sdf;
		java.util.Date utilDate;
		if (string.length() > 0) {
			Pattern pattern = Pattern.compile(EIGHT_DIGITS);
			Matcher matcher = pattern.matcher(string);

			if (matcher.find()) {
				sdf = new SimpleDateFormat("yyyymmdd");
				utilDate = sdf.parse(string);
				return new Date(utilDate.getTime());
			}

			pattern = Pattern.compile(DASH_DATE);
			matcher = pattern.matcher(string);

			if (matcher.find()) {
				sdf = new SimpleDateFormat("dd-mm-yyyy");
				utilDate = sdf.parse(string);
				return new Date(utilDate.getTime());
			}

			pattern = Pattern.compile(US_DASH_DATE);
			matcher = pattern.matcher(string);

			if (matcher.find()) {
				sdf = new SimpleDateFormat("yyyy-mm-dd");
				utilDate = sdf.parse(string);
				return new Date(utilDate.getTime());
			}

			pattern = Pattern.compile(FOUR_DIGITS);
			matcher = pattern.matcher(string);

			if (matcher.find()) {
				sdf = new SimpleDateFormat("yyyy");
				utilDate = sdf.parse(string);
				return new Date(utilDate.getTime());
			}
		}
		return null;
	}
}
