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
import java.util.List;
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

import net.myerichsen.gedcom.db.models.IndividualModel;
import net.myerichsen.gedcom.db.models.MilRollModel;
import net.myerichsen.gedcom.db.models.MilrollListModel;
import net.myerichsen.gedcom.util.Fonkod;

/**
 * Input application for military roll entries
 *
 * @author Michael Erichsen
 * @version 6. maj 2023
 *
 */

// TODO Merge military roll relocations into relocation view
// TODO Find a way to merge entries from following years

public class MilRollView {
	private static final String DASH_DATE = "\\d*-\\d*-\\d{4}";
	private static final String EIGHT_DIGITS = "\\d{8}";
	private static final String FOUR_DIGITS = "\\d{4}";
	private static final String US_DASH_DATE = "\\d{4}-\\d*-\\d*";

	/**
	 * Launch the application.
	 *
	 * @param args
	 * @wbp.parser.entryPoint
	 */
	public static void main(Properties props) {
		try {
			final MilRollView window = new MilRollView();
			window.open();
			window.setProperties(props);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	protected Shell shlLgdsrulleindtastning;
	private Text textAmt;
	private Text textAar;
	private Text textLitra;
	private Text textRulletype;
	private Text textLaegdNr;
	private Text textSogn;
	private Text textLaegdId;
	private Composite compositeData;
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
	private Button btnTilbage;
	private Button btnFrem;
	private Properties props;
	private Composite compositeButtons;
	private Button btnGemIndtastning;
	private Fonkod fk;
	private Composite compositeBrowser;
	private Text textUri;
	private Combo messageComboBox;
	private Button btnSgEfterGedcom;
	private Button btnGemUri;

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
		compositeRulle.setLayout(new GridLayout(16, false));

		final Label lblAmt = new Label(compositeRulle, SWT.NONE);
		lblAmt.setText("Amt");

		textAmt = new Text(compositeRulle, SWT.BORDER);
		textAmt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textAmt.setText(props.getProperty("amt"));

		final Label lblAar = new Label(compositeRulle, SWT.NONE);
		lblAar.setText("År");

		textAar = new Text(compositeRulle, SWT.BORDER);
		textAar.setEditable(false);
		textAar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		textAar.setText(props.getProperty("aar"));

		final Label lblLitra = new Label(compositeRulle, SWT.NONE);
		lblLitra.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblLitra.setText("Litra");

		textLitra = new Text(compositeRulle, SWT.BORDER);
		textLitra.setEditable(false);
		textLitra.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		textLitra.setText(props.getProperty("litra"));

		textRulletype = new Text(compositeRulle, SWT.NONE);
		textRulletype.setEditable(false);
		textRulletype.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textRulletype.setText(props.getProperty("rulletype"));

		final Label lblLgd = new Label(compositeRulle, SWT.NONE);
		lblLgd.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblLgd.setText("L\u00E6gd");

		textLaegdNr = new Text(compositeRulle, SWT.BORDER);
		textLaegdNr.setEditable(false);
		textLaegdNr.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		textLaegdNr.setText(props.getProperty("laegdnr"));

		textSogn = new Text(compositeRulle, SWT.BORDER);
		textSogn.setEnabled(false);
		textSogn.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textSogn.setText(props.getProperty("sogn"));

		final Label lblId = new Label(compositeRulle, SWT.NONE);
		lblId.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblId.setText("ID");

		textLaegdId = new Text(compositeRulle, SWT.BORDER);
		textLaegdId.setEditable(false);
		textLaegdId.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		textLaegdId.setText(props.getProperty("laegdid"));

		compositeRulleButtons = new Composite(compositeRulle, SWT.NONE);
		compositeRulleButtons.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		compositeRulleButtons.setLayout(new RowLayout(SWT.HORIZONTAL));

		btnFrem = new Button(compositeRulleButtons, SWT.NONE);
		btnFrem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					getNextMilRoll(Integer.parseInt(textLaegdId.getText()));
				} catch (final NumberFormatException | SQLException e1) {
					setMessage(e1.getMessage());
//					e1.printStackTrace();
				}
			}
		});
		btnFrem.setText("Frem");

		btnTilbage = new Button(compositeRulleButtons, SWT.NONE);
		btnTilbage.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					getPrevMilRoll(Integer.parseInt(textLaegdId.getText()));
				} catch (final NumberFormatException | SQLException e1) {
					setMessage(e1.getMessage());
//					e1.printStackTrace();
				}
			}
		});
		btnTilbage.setText("Tilbage");

		btnGem = new Button(compositeRulleButtons, SWT.NONE);
		btnGem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				storeProperties();
			}
		});
		btnGem.setText("Gem");
		new Label(compositeRulle, SWT.NONE);
		new Label(compositeRulle, SWT.NONE);

		compositeBrowser = new Composite(shlLgdsrulleindtastning, SWT.BORDER);
		compositeBrowser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		compositeBrowser.setLayout(new GridLayout(3, false));

		final Label lblUri = new Label(compositeBrowser, SWT.NONE);
		lblUri.setText("L\u00E6gdsrulle URI");

		textUri = new Text(compositeBrowser, SWT.BORDER);
		textUri.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textUri.setText(props.getProperty("uri"));

		btnGemUri = new Button(compositeBrowser, SWT.NONE);
		btnGemUri.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				props.setProperty("uri", textUri.getText());
				storeProperties();
				setMessage("Indstillinger er gemt i " + Constants.PROPERTIES_PATH);
			}
		});
		btnGemUri.setText("Gem URI");

		final Browser browser = new Browser(compositeBrowser, SWT.NONE);
		browser.addLocationListener(new LocationAdapter() {
			@Override
			public void changed(LocationEvent event) {
				final String location = event.location;

				if (location.contains("billedviser")) {
					textUri.setText(location);
				}
			}
		});
		browser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
		browser.setUrl(textUri.getText());

		compositeData = new Composite(shlLgdsrulleindtastning, SWT.BORDER);
		compositeData.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		compositeData.setLayout(new GridLayout(6, false));

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
				try {
					searchForGedcomID();
				} catch (final Exception e1) {
					setErrorMessage(e1.getMessage());
//					e1.printStackTrace();
				}
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
		new Label(compositeData, SWT.NONE);
		new Label(compositeData, SWT.NONE);

		messageComboBox = new Combo(shlLgdsrulleindtastning, SWT.READ_ONLY);
		messageComboBox.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

	}

	/**
	 * @param laegdId
	 * @throws SQLException
	 *
	 */
	protected void getNextMilRoll(int laegdId) throws SQLException {
		final MilrollListModel m = MilrollListModel.selectNext(props.getProperty("milrollPath"),
				props.getProperty("milrollSchema"), laegdId);

		if (m.getLaegdId() > 0) {
			textAmt.setText(m.getAmt());
			textAar.setText(Integer.toString(m.getAar()));
			textLitra.setText(m.getLitra());
			textRulletype.setText(m.getRulleType());
			textLaegdNr.setText(Integer.toString(m.getLaegdNr()));
			textSogn.setText(m.getSogn());
			textLaegdId.setText(Integer.toString(m.getLaegdId()));
		} else
			setMessage("Ingen højere i dette amt");
	}

	/**
	 * @param laegdId
	 * @throws SQLException
	 *
	 */
	protected void getPrevMilRoll(int laegdId) throws SQLException {
		final MilrollListModel m = MilrollListModel.selectPrev(props.getProperty("milrollPath"),
				props.getProperty("milrollSchema"), laegdId);

		if (m.getLaegdId() > 0) {
			textAmt.setText(m.getAmt());
			textAar.setText(Integer.toString(m.getAar()));
			textLitra.setText(m.getLitra());
			textRulletype.setText(m.getRulleType());
			textLaegdNr.setText(Integer.toString(m.getLaegdNr()));
			textSogn.setText(m.getSogn());
			textLaegdId.setText(Integer.toString(m.getLaegdId()));
		} else
			setMessage("Ingen lavere i dette amt");

	}

	/**
	 * Get properties from file or create them
	 */
	private void getProperties() {
		props = new Properties();

		try {
			final InputStream input = new FileInputStream(Constants.PROPERTIES_PATH);
			props.load(input);
		} catch (final Exception e) {

			props.setProperty("amt", Constants.AMT);
			props.setProperty("aar", Constants.AAR);
			props.setProperty("litra", Constants.LITRA);
			props.setProperty("rulletype", Constants.HOVEDRULLE);
			props.setProperty("laegdnr", Constants.LAEGDNR);
			props.setProperty("sogn", Constants.SOGN);
			props.setProperty("milrollPath", Constants.MILROLLDB_PATH);
			props.setProperty("milrollSchema", Constants.MILROLLDB_SCHEMA);
			props.setProperty("laegdid", Constants.LAEGDID);
			props.setProperty("uri", Constants.MILROLL_URI);

			storeProperties();
			System.out.println("Egenskaber gemt i " + Constants.PROPERTIES_PATH);
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
	protected void saveToDb() throws SQLException {
		final MilRollModel lm = new MilRollModel();

		try {
			lm.setGlLoebeNr(Integer.parseInt(textGlLoebenr.getText()));
		} catch (final NumberFormatException e2) {
		}

		try {
			lm.setLoebeNr(Integer.parseInt(textNyLoebenr.getText()));
		} catch (final Exception e) {
			setErrorMessage("Indtast venligst løbenr.");
			textNyLoebenr.setFocus();
		}

		try {
			lm.setFader(textFader.getText());
		} catch (final Exception e2) {
			setErrorMessage("Indtast venligst fader");
			textFader.setFocus();
		}

		try {
			lm.setSoen(textSoen.getText());
		} catch (final Exception e1) {
			setErrorMessage("Indtast venligst søn");
			textSoen.setFocus();
		}

		lm.setFoedeSted(textFoedested.getText());

		try {
			lm.setAlder(Integer.parseInt(textAlder.getText()));
		} catch (final Exception e) {
			lm.setAlder(0);
		}

		try {
			lm.setStoerrelseITommer(new BigDecimal(textStoerrelseitommer.getText()));
		} catch (final Exception e) {
			lm.setStoerrelseITommer(new BigDecimal(0));
		}

		lm.setOphold(textOphold.getText());
		lm.setAnmaerkninger(textAnmaerkninger.getText());

		try {
			lm.setFoedt(string2Date(textFoedt.getText()));
		} catch (final ParseException e1) {
		}

		lm.setGedcomId(textGedcomid.getText());

		try {
			lm.setFaderFon(fk.generateKey(lm.getFader()));
		} catch (final Exception e1) {
			lm.setFaderFon("");
		}

		try {
			lm.setLaegdId(Integer.parseInt(textLaegdId.getText()));
		} catch (final Exception e) {
			lm.setLaegdId(0);
		}

		lm.setNavn(constructName(lm.getFader(), lm.getSoen()));

		try {
			lm.setSoenFon(fk.generateKey(lm.getNavn()));
		} catch (final Exception e) {
			lm.setSoenFon("");
		}

		final String result = lm.insert(props);
		setMessage(result);

	}

	/**
	 * Search for id for phonetic name of son and age or birth. Present result in a
	 * popup with selection possibility
	 *
	 * @throws Exception
	 */
	protected void searchForGedcomID() throws Exception {
		final String constructName = constructName(textFader.getText(), textSoen.getText());
		final String phonName = fk.generateKey(constructName);

		final int alder = Integer.parseInt(textAlder.getText());
		final int aar = Integer.parseInt(textAar.getText());
		final int birthYearInt = aar - alder;
		final Date birthDate = Date.valueOf(Integer.toString(birthYearInt) + "-01-01");

		final List<String> ls = IndividualModel.getDataFromPhonName(props.getProperty("vejbyPath"),
				props.getProperty("vejbySchema"), phonName, birthDate);

		final String[] sa = new String[ls.size()];
		for (int i = 0; i < ls.size(); i++) {
			sa[i] = ls.get(i);
		}

		final MilrollDialog mrd = new MilrollDialog(shlLgdsrulleindtastning);
		mrd.setInput(sa);
		mrd.open();

		textGedcomid.setText(mrd.getSelectedIndividual());
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
		final int lastItem = Integer.parseInt(props.getProperty("msgLogLen"));

		if (messageComboBox.getItemCount() > lastItem) {
			messageComboBox.remove(lastItem);
		}
	}

	/**
	 * @param props
	 */
	public void setProperties(Properties props) {
		this.props = props;
	}

	/**
	 * Store properties in file
	 */
	protected void storeProperties() {
		try {
			final OutputStream output = new FileOutputStream(Constants.PROPERTIES_PATH);

			props.setProperty("amt", textAmt.getText());
			props.setProperty("aar", textAar.getText());
			props.setProperty("rulletype", textRulletype.getText());
			props.setProperty("laegdnr", textLaegdNr.getText());
			props.setProperty("sogn", textSoen.getText());
			props.setProperty("laegdid", textLaegdId.getText());
			props.setProperty("uri", textUri.getText());

			props.store(output, "Archive searcher properties");
		} catch (final Exception e2) {
			setErrorMessage("Kan ikke gemme egenskaber i " + Constants.PROPERTIES_PATH);
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
