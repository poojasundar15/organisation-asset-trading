package Client.gui;

import Client.*;
import Server.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;



/**
 * Point of entry for the GUI of the program
 */
public class AssetTradingGUI extends JFrame implements ActionListener {

    private JTextField loginField;
    private JPasswordField passwordField;

    private JPanel mainContent;
    private JPanel buyPanel;
    private JPanel sellPanel;
    private JPanel createPanel;
    private JPanel accountPanel;
    private JPanel assetsPanel;
    private JPanel myListingsPanel;
    private JPanel notificationsPanel;

    // Top menu components
    private JPanel topMenuContainer;
    private JPanel topMenuRight;
    private JPanel topMenuLeft;

    // Side menu components
    private JPanel sideMenuTop;
    private JPanel sideMenuBottom;

    private CardLayout cardLayout = new CardLayout();

    private int tableTradeID;

    // The unit the logged in user belongs to
    private Units unit;
    private User userLoggedIn;
    // Instances of the database wrappers
    private UnitDBSource udb;
    private UserDBSource usdb;
    private TradeDBSource tdb;
    private AssetDBSource adb;
    private PurchasesDBSource pdb;
    private  HistoryDBSource hdb;
    TradeLogic implementTrade;
    // Server connection
    private static final ServerAPI server =new ServerAPI();



    public AssetTradingGUI(User user) {

        super("Asset Trading");

        // Create connection to database and instantiate database wrappers
        this.udb = new UnitDBSource();
        this.tdb = new TradeDBSource();
        this.adb = new AssetDBSource();
        this.usdb = new UserDBSource();
        this.pdb = new PurchasesDBSource();
        this.hdb = new HistoryDBSource();
        this.unit = udb.getUnit(user.getUnit());
        this.userLoggedIn = user;
        implementTrade = new TradeLogic(udb, usdb, tdb, adb, pdb, hdb);

        // Setup of main frame
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(1000, 700));
        setLayout(new BorderLayout(0, 0));

        // Main content in center
        mainContent = new JPanel();
        mainContent.setLayout(cardLayout);
        add(mainContent, BorderLayout.CENTER);

        // Setup Panels
        addTopMenu();
        addSideMenu();
        setupBuyPanel();
        setupSellPanel();
        setupAccountPanel();
        setupAssetsPanel();
        setupMyListingsPanel();
        setupCreatePanel();

        // Display assets panel on startup
        cardLayout.show(mainContent, "4");

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void refreshGUI() {
        topMenuRight.setVisible(false);
        topMenuContainer.setVisible(false);
        topMenuLeft.setVisible(false);
        unit = udb.getUnit(userLoggedIn.getUnit());
        addTopMenu();
        setupSellPanel();
        setupAssetsPanel();
        setupMyListingsPanel();
        setupAccountPanel();
    }

    public static void main(String[] args) throws UserException, TradesException, AssetsException {
        new LoginGUI(server);
    }

    public void addTopMenu() {
        // Contains all components
        topMenuContainer = new JPanel(new BorderLayout());
        topMenuContainer.setBackground(Utility.PRIMARYBLUE);

        topMenuRight = new JPanel(new FlowLayout(FlowLayout.RIGHT,10,15));
        topMenuRight.setBackground(Utility.PRIMARYBLUE);

        topMenuLeft = new JPanel();
        topMenuLeft.setBorder(new EmptyBorder(0, 20, 0, 0));
        topMenuLeft.setBackground(Utility.PRIMARYBLUE);

        add(topMenuContainer, BorderLayout.NORTH);
        topMenuContainer.add(topMenuLeft, BorderLayout.WEST);
        topMenuContainer.add(topMenuRight, BorderLayout.EAST);

        // Display username in top left corner
        JLabel userLabel = new JLabel(userLoggedIn.getUsername());
        userLabel.setPreferredSize(new Dimension(60, 50));
        userLabel.setForeground(Color.WHITE);
        topMenuLeft.add(userLabel);

        ImageIcon icon = new ImageIcon(this.getClass().getResource("images/bell.png"));

        if(userLoggedIn.getAdminStatus() == true) {
            // Buttons in top menu (Will come back and refactor)
            for (int i = 0; i < 5; i++) {
                String btnName = "";
                switch(i) {
                    case 0:
                        btnName = "Buy";
                        icon = new ImageIcon(this.getClass().getResource("images/buy.png"));
                        break;
                    case 1:
                        btnName = "Sell";
                        icon = new ImageIcon(this.getClass().getResource("images/sell.png"));
                        break;
                    case 2:
                        btnName = "Account";
                        icon = new ImageIcon(this.getClass().getResource("images/account.png"));
                        break;
                    case 3:
                        btnName = "Create";
                        icon = new ImageIcon(this.getClass().getResource("images/plus.png"));
                        break;
                    case 4:
                        btnName = "Modify";
                        icon = new ImageIcon(this.getClass().getResource("images/modify.png"));
                        break;
                }
                JButton btn = new JButton(btnName, icon);
                btn.setBorderPainted(false);
                btn.setBackground(Utility.PRIMARYBLUE);
                btn.setForeground(Color.WHITE);
                btn.setPreferredSize(new Dimension(130, 30));
                btn.addActionListener(this);
                topMenuRight.add(btn);
            }
        } else {
            for (int i = 0; i < 3; i++) {
                String btnName = "";
                switch (i) {
                    case 0:
                        btnName = "Buy";
                        icon = new ImageIcon(this.getClass().getResource("images/buy.png"));
                        break;
                    case 1:
                        btnName = "Sell";
                        icon = new ImageIcon(this.getClass().getResource("images/sell.png"));
                        break;
                    case 2:
                        btnName = "Account";
                        icon = new ImageIcon(this.getClass().getResource("images/account.png"));
                        break;
                }
                JButton btn = new JButton(btnName, icon);
                btn.setBorderPainted(false);
                btn.setBackground(Utility.PRIMARYBLUE);
                btn.setForeground(Color.WHITE);
                btn.setPreferredSize(new Dimension(120, 30));
                btn.addActionListener(this);
                topMenuRight.add(btn);
            }
        }

        JLabel creditsLabel =
                new JLabel("Credits: [" + unit.getCredits() + "]");
        creditsLabel.setPreferredSize(new Dimension(100, 30));
        creditsLabel.setForeground(Color.WHITE);
        topMenuRight.add(creditsLabel);
    }

    public void addSideMenu() {

        JPanel leftMenuPanel = new JPanel();
        leftMenuPanel.setPreferredSize(new Dimension(140, 0));
        leftMenuPanel.setLayout(new BorderLayout());
        leftMenuPanel.setBackground(Utility.DARKGREY);
        add(leftMenuPanel, BorderLayout.WEST);
        sideMenuTop = new JPanel();
        sideMenuBottom = new JPanel();
        sideMenuTop.setBackground(Utility.DARKGREY);
        sideMenuBottom.setBackground(Utility.DARKGREY);
        leftMenuPanel.add(sideMenuTop);
        leftMenuPanel.add(sideMenuBottom, BorderLayout.SOUTH);

        ImageIcon icon = new ImageIcon(this.getClass().getResource("images/bell.png"));

        for (int i = 0; i < 4; i++) {
            String btnName = "";
            switch(i) {
                case 0:
                    btnName = "Notifications";
                    break;
                case 1:
                    btnName = "My Listings";
                    icon = new ImageIcon(this.getClass().getResource("images/mylistings.png"));
                    break;
                case 2:
                    btnName = "View Assets";
                    icon = new ImageIcon(this.getClass().getResource("images/viewassets.png"));
                    break;
                case 3:
                    btnName = "Logout";
                    icon = new ImageIcon(this.getClass().getResource("images/logout.png"));
                    break;

            }
            JButton btn = new JButton(btnName, icon);
            btn.setBorderPainted(false);
            btn.setBackground(Utility.DARKGREY);
            btn.setForeground(Color.WHITE);
            btn.setPreferredSize(new Dimension(125, 40));
            btn.addActionListener(this);
            if(btnName.equals("Logout")) {
                sideMenuBottom.add(btn, BorderLayout.SOUTH);
            } else  {
                sideMenuTop.add(btn);
            }
        }
    }

    public void setupBuyPanel() {

        buyPanel = new JPanel(new BorderLayout(0, 0));
        buyPanel.setBorder(BorderFactory.createTitledBorder("Buy"));

        setSize(1000, 700);
        JLabel label1 , label2, label3 , label4, label5;
        JSpinner s2, s3;

        JCheckBox terms;
        JButton submit;
        JLabel msg;

        label1 = new JLabel("Asset Type");
        label1.setBounds(30, 50, 100, 20);

        //TODO: Need to change this so a units own assets don't appear
        //TODO: I.e., a unit doesn't need to buy the assets it makes
        JComboBox assetTypeList = new JComboBox(adb.getAssetNames());
        assetTypeList.setBounds(140 , 50, 150 , 20);

        label2 = new JLabel("Amount");
        label2.setBounds(30 , 80, 100 , 20);

        s2 = new JSpinner();
        s2.setBounds(140 , 80, 150 , 20);

        label3 = new JLabel("Cost Per Unit");
        label3.setBounds(30 , 110, 100 , 20);

        s3 = new JSpinner();
        s3.setBounds(140 , 110, 150 , 20);

        terms = new JCheckBox("Please accept that the details you have entered are correct");
        terms.setBounds(27 , 143, 400 , 20);

        submit = new JButton("Submit");
        submit.setBounds(30 , 180, 100 , 20);

        msg = new JLabel("");
        msg.setBounds(140 , 230, 100 , 20);

        submit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String type = (String) assetTypeList.getSelectedItem();
                String amount = s2.getValue() + "";
                String price = s3.getValue() + "";
                boolean boxSelected = terms.isSelected();

                // Checks if the amount and price values are positive and non-zero
                if(Integer.parseInt(amount) > 0 && Integer.parseInt(price) > 0) {

                    // If the checkbox is selected
                    if(boxSelected) {
                        Trades newTrade;
                        try {
                            newTrade = new Trades(Trades.TradeType.buy, userLoggedIn.getUsername(),
                                    adb.getAsset(type).getAssetID(), Integer.parseInt(amount), Integer.parseInt(price));
                            implementTrade.setTrade(newTrade);
                            JOptionPane.showMessageDialog(null, "Buy order was placed!",
                                    "Successful", JOptionPane.INFORMATION_MESSAGE);
                            refreshGUI();
                        } catch (TradesException tradesException) {
                            JOptionPane.showMessageDialog(null, tradesException.getMessage()
                                    , "Error", JOptionPane.ERROR_MESSAGE);
                            tradesException.printStackTrace();
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "You have not accepted " +
                                        "the terms. Please select the checkbox.", "Checkbox Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid entry: Please enter a positive "
                                    + "non-zero number for both Amount and Cost", "Checkbox Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }

        });

        // accountPanel.add();
        buyPanel.add(label1);
        buyPanel.add(label2);
        buyPanel.add(s2);
        buyPanel.add(label3);
        buyPanel.add(s3);
        buyPanel.add(assetTypeList);
        buyPanel.add(terms);
        buyPanel.add(submit);
        buyPanel.add(msg);

        mainContent.add(buyPanel, "1");

    }

    public void setupSellPanel() {

        JPanel sellPanelContainer = new JPanel(new GridLayout(1, 2));
        JPanel leftPanel = new JPanel(new BorderLayout(0, 0));
        JPanel rightPanel = new JPanel(new BorderLayout(0, 0));
        rightPanel.setBorder(new EmptyBorder(0,0,30,30));

        sellPanelContainer.add(leftPanel);
        sellPanelContainer.add(rightPanel);
        sellPanelContainer.setBorder(BorderFactory.createTitledBorder("Sell"));

        JLabel label1 , label2, label3 , label4, label5;
        JSpinner s2, s3;

        JCheckBox terms;
        JButton submit;
        JLabel msg;

        label1 = new JLabel("Asset Type");
        label1.setBounds(30, 50, 100, 20);

        JComboBox assetOwnedList =
                new JComboBox(adb.getAssetNamesByUnit(unit.getUnitID()));
        assetOwnedList.setBounds(140 , 50, 150 , 20);

        label2 = new JLabel("Amount");
        label2.setBounds(30 , 80, 100 , 20);

        s2 = new JSpinner();
        s2.setBounds(140 , 80, 150 , 20);

        label3 = new JLabel("Cost Per Unit");
        label3.setBounds(30 , 110, 100 , 20);

        s3 = new JSpinner();
        s3.setBounds(140 , 110, 150 , 20);

        terms = new JCheckBox("Please confirm that the details you have entered are correct");
        terms.setBounds(27 , 143, 400 , 20);

        submit = new JButton("Submit");
        submit.setBounds(30 , 180, 100 , 20);

        msg = new JLabel("");
        msg.setBounds(140 , 230, 100 , 20);

        submit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String type = (String) assetOwnedList.getSelectedItem();
                String amount = s2.getValue() + "";
                String price = s3.getValue() + "";
                boolean boxSelected = terms.isSelected();

                // Checks if the amount and price values are positive and non-zero
                if(Integer.parseInt(amount) > 0 && Integer.parseInt(price) > 0) {

                    // If the checkbox is selected
                    if(boxSelected) {
                        try {
                            Trades newTrade;
                            newTrade = new Trades(Trades.TradeType.sell, userLoggedIn.getUsername(),
                                    adb.getAsset(type).getAssetID(), Integer.parseInt(amount), Integer.parseInt(price));
                            implementTrade.setTrade(newTrade);
                            refreshGUI();
                            cardLayout.show(mainContent, "2");
                            JOptionPane.showMessageDialog(null, "Sell order was placed!",
                                    "Successful", JOptionPane.INFORMATION_MESSAGE);
                        } catch (TradesException tradesException) {
                            JOptionPane.showMessageDialog(null, "You do not have enough assets " +
                                            "to create this listing", "Assets Error",
                                    JOptionPane.ERROR_MESSAGE);
                            tradesException.printStackTrace();
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "You have not accepted " +
                                        "the terms. Please select the checkbox.", "Checkbox Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid entry: Please enter a positive " +
                                    "non-zero number for both Amount and Cost", "Checkbox Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }

        });

        // Row data in the table
        Object tableData[] = new Object[2];

        UserAssetsTable userAssetsTable = new UserAssetsTable(rightPanel, unit, userLoggedIn, adb);

        leftPanel.add(label1);
        leftPanel.add(label2);
        leftPanel.add(s2);
        leftPanel.add(label3);
        leftPanel.add(s3);
        leftPanel.add(assetOwnedList);
        leftPanel.add(terms);
        leftPanel.add(submit);
        leftPanel.add(msg);

        mainContent.add(sellPanelContainer, "2");
    }

    public void setupAccountPanel() {
        accountPanel = new JPanel(new BorderLayout(0, 0));
        accountPanel.setBorder(BorderFactory.createTitledBorder("Account"));

        setSize(1000, 700);
        JLabel label1 , label2, label3 , label4, label5, label6, label7;
        JTextField t1, t2, t3, t4, t5;
        JPasswordField userPassword, newPasswordInput, confirmPasswordInput;
        //JComboBox day, month, year;
        //JRadioButton AccUser, AccAdmin;
        //JTextArea ta1;
        String[] messageStrings = {"User", "Admin"};

        String[] unitNames = udb.getUnitNames();

        //JLabel lbltext = new JLabel();
        JCheckBox terms;
        JButton submit, changePassword;
        JLabel msg;


        label1 = new JLabel("Username");
        label1.setBounds(30, 50, 100, 20);

        t1 = new JTextField();
        t1.setBounds(140 , 50, 100 , 20);

        label2 = new JLabel("Password");
        label2.setBounds(30 , 80, 100 , 20);

        userPassword = new JPasswordField();
        userPassword.setBounds(140 , 80, 100 , 20);

        label4 = new JLabel("Unit");
        label4.setBounds(30 , 110, 100 , 20);

        JComboBox units = new JComboBox(unitNames);
        units.setBounds(140 , 110, 100 , 20);

        label5 = new JLabel("Access Level");
        label5.setBounds(30 , 140, 100 , 20);

        label6 = new JLabel("New Password");
        label6.setBounds(30 , 340, 100 , 20);

        newPasswordInput = new JPasswordField();
        newPasswordInput.setBounds(160 , 340, 100 , 20);

        label7 = new JLabel("Confirm Password");
        label7.setBounds(30 , 370, 130 , 20);

        confirmPasswordInput = new JPasswordField();
        confirmPasswordInput.setBounds(160 , 370, 100 , 20);

        changePassword = new JButton("Change");
        changePassword.setBounds(30 , 400, 100 , 20);

        submit = new JButton("Submit");
        submit.setBounds(30 , 200, 100 , 20);

        JComboBox cmbMessageList = new JComboBox(messageStrings);
        cmbMessageList.setBounds(140 , 140 , 100, 20);

        terms = new JCheckBox("Please accept that the " +
                "details you have entered are correct");
        terms.setBounds(30 , 170, 400 , 20);


        submit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = t1.getText();
                String password = userPassword.getText();
                String unit = units.getSelectedItem().toString().trim();
                String userType = cmbMessageList.getSelectedItem().toString();
                boolean boxSelected = terms.isSelected();
                boolean admin = userType.trim() == messageStrings[1];

                if (admin && !unit.equals("IT Administration")) {
                    JOptionPane.showMessageDialog(null, "Only IT Admin staff can be admins!",
                            "Invalid Unit For Admin", JOptionPane.ERROR_MESSAGE);
                } else {
                    if (boxSelected) {
                        try {
                            // Initialise new user object
                            User newUser = new User(null, null, null, username, password,
                                    admin, udb.getUnit(unit).getUnitID());
                            // Check username doesn't exist already
                            if (usdb.checkUsername(newUser.getUsername())) {
                                throw new UserException("Username already exists!");
                            } else {
                                usdb.addUser(newUser);
                                JOptionPane.showMessageDialog(null, username +
                                        " was added to the " + unit + " organisational unit!",
                                        "User added", JOptionPane.INFORMATION_MESSAGE);
                            }
                        } catch (UserException userException) {
                            userException.printStackTrace();
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "The terms box was not selected",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        changePassword.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String newPassword = newPasswordInput.getText();
                String confirmPassword = confirmPasswordInput.getText();
                if (newPassword.length() != 0 && confirmPassword.length() != 0) {
                    if (newPassword.equals(confirmPassword)) {
                        JOptionPane.showMessageDialog(null, "Password has been changed",
                                "Successful", JOptionPane.INFORMATION_MESSAGE);
                        userLoggedIn.setPassword(User.hashPassword(newPassword));
                        usdb.update(userLoggedIn);
                    } else {
                        JOptionPane.showMessageDialog(null, "Passwords don't match!",
                                "Invalid", JOptionPane.ERROR_MESSAGE);
                    }

                } else {
                    JOptionPane.showMessageDialog(null, "Password fields cannot be empty",
                            "Invalid", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        msg = new JLabel("");
        msg.setBounds(140 , 230, 100 , 20);


        if(userLoggedIn.getAdminStatus() == false) {
            label6.setBounds(30, 50, 130, 20);
            accountPanel.add(label6);
            label7.setBounds(30, 80, 130, 20);
            accountPanel.add(label7);
            newPasswordInput.setBounds(160 , 50, 100 , 20);
            accountPanel.add(newPasswordInput);
            accountPanel.add(confirmPasswordInput);
            confirmPasswordInput.setBounds(160, 80, 100, 20);
            accountPanel.add(changePassword);
            changePassword.setBounds(30 , 110, 100 , 20);
        } else {
            // accountPanel.add();
            accountPanel.add(label1);
            accountPanel.add(t1);
            accountPanel.add(label2);
            accountPanel.add(userPassword);
            accountPanel.add(label4);
            accountPanel.add(units);
            accountPanel.add(label5);
            accountPanel.add(label6);
            accountPanel.add(label7);
            accountPanel.add(newPasswordInput);
            accountPanel.add(confirmPasswordInput);
            accountPanel.add(changePassword);
            //accountPanel.add(AccUser);
            //accountPanel.add(AccAdmin);
            accountPanel.add(cmbMessageList);
            accountPanel.add(terms);
            accountPanel.add(submit);
        }
        accountPanel.add(msg);


        // Change Password

        mainContent.add(accountPanel, "3");
    }

    public void setupAssetsPanel() {
        assetsPanel = new JPanel(new BorderLayout(0, 0));
        assetsPanel.setBorder(BorderFactory.createTitledBorder("Assets"));
        mainContent.add(assetsPanel, "4");
        AssetsTable table = new AssetsTable(assetsPanel, tdb);
    }

    public void setupMyListingsPanel() {
        myListingsPanel = new JPanel(new BorderLayout());
        myListingsPanel.setBorder(BorderFactory.createTitledBorder("My Listings"));
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT,10,15));
        JPanel gridPanel = new JPanel(new GridLayout(1, 2, 10, 20));
        JButton cancelOrderBtn = new JButton("Cancel Order");
        topPanel.add(cancelOrderBtn);

        MyListingsTableBuy buyTable = new MyListingsTableBuy(gridPanel, unit, userLoggedIn, tdb);
        MyListingsTableSell sellTable = new MyListingsTableSell(gridPanel, unit, userLoggedIn, tdb);

        JTable buyTableClick = buyTable.getBuyTable();
        JTable sellTableClick = sellTable.getSellTable();

        buyTableClick.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                JTable table = (JTable)e.getSource();
                int row = table.rowAtPoint(e.getPoint());

                if(row != -1) {
                    tableTradeID = Integer.parseInt(table.getModel().getValueAt(row, 0) + "");
                }
            }
        });

        sellTableClick.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                JTable table = (JTable)e.getSource();
                int row = table.rowAtPoint(e.getPoint());

                if(row != -1) {
                    tableTradeID = Integer.parseInt(table.getModel().getValueAt(row, 0) + "");
                }
            }
        });

        cancelOrderBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get trades object
                Trades tradeToCancel = tdb.getTrade(tableTradeID);
                // Pass to cancel trade method
                implementTrade.cancelTrade(tradeToCancel);
                refreshGUI();
                cardLayout.show(mainContent, "5");
                JOptionPane.showMessageDialog(null, "Order was successfully cancelled",
                        "Order Cancelled", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        myListingsPanel.add(topPanel, BorderLayout.NORTH);
        myListingsPanel.add(gridPanel);

        mainContent.add(myListingsPanel, "5");
    }

    public void setupCreatePanel() {
        JPanel createPanelContainer = new JPanel(new GridLayout(1, 2));
        JPanel leftPanel = new JPanel(new BorderLayout(0, 0));
        JPanel rightPanel = new JPanel(new BorderLayout(0, 0));
        rightPanel.setBorder(new EmptyBorder(0,0,30,30));

        createPanelContainer.add(leftPanel);
        createPanelContainer.add(rightPanel);
        createPanelContainer.setBorder(BorderFactory.createTitledBorder("Create"));

        setSize(1000, 700);
        JLabel label1 , label2;
        JSpinner s1;
        JTextField t1;
        JCheckBox terms;
        JButton submit;
        JLabel msg;

        label1 = new JLabel("Organisational Unit Name");
        label1.setBounds(30, 50, 150, 20);

        t1 = new JTextField();
        t1.setBounds(220 , 50, 150 , 20);

        label2 = new JLabel("Credits");
        label2.setBounds(30 , 80, 100 , 20);

        s1 = new JSpinner();
        s1.setBounds(220 , 80, 150 , 20);


        terms = new JCheckBox("Please confirm that the " +
                "details you have entered are correct");
        terms.setBounds(30 , 200, 400 , 20);


        submit = new JButton("Submit");
        submit.setBounds(30 , 230, 100 , 20);

        msg = new JLabel("");
        msg.setBounds(140 , 180, 100 , 20);

        // I'm just passing in an arbitrary number of rows to fill out the table
        // My reasoning is admins can add bespoke assets when creating a class, rather than pre-filling from a set
        // list
        int numRows = 30;
        OrganisationAssetsTable organisationAssetsTable = new OrganisationAssetsTable(rightPanel, numRows);
        organisationAssetsTable.getAssetsTable().putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

        submit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Units newUnit;
                String unitName = t1.getText();
                String credits = s1.getValue() + "";
                boolean boxSelected = terms.isSelected();


                String[] unitNames = udb.getUnitNames();

                if(boxSelected) {
                    // Check credits are >= 0, if not set to 0 by default
                    int intCredits = Math.max(Integer.parseInt(credits), 0);

                    newUnit = new Units(unitName, intCredits);
                    // Check if unit already exists
                    if(!Arrays.asList(unitNames).contains(unitName)) {
                        udb.add(newUnit);
                        // Get the unit ID generated by the database
                        int newUnitId = udb.getUnit(newUnit.getUnitName()).getUnitID();

                        // Add unit assets
                        // Loops through table until it comes to a blank asset name cell
                        int row = 0;
                        int col = 0;
                        String assetName = organisationAssetsTable.getAssetsTable().getValueAt(row, col).toString();
                        int assetQty;
                        while (assetName.trim().length() != 0) {
                            // If the user has entered a non-numerical value, default to 0
                            try{
                                assetQty = Integer.parseInt(organisationAssetsTable.getAssetsTable()
                                        .getValueAt(row, (col + 1)).toString());
                            } catch (NumberFormatException err) {
                                assetQty = 0;
                            }

                            // Check if asset qty >= 0, if not set to 0 as default
                            assetQty = Math.max(assetQty, 0);

                            Assets newAsset = new Assets(assetName, assetQty, newUnitId);
                            adb.add(newAsset);
                            row++;
                            assetName = organisationAssetsTable.getAssetsTable().getValueAt(row, col).toString();
                        }
                        JOptionPane.showMessageDialog(null,
                                unitName + " has been added as a new organisation"  , "Successful",
                                JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, "An organisation with the same name " +
                                        "has already been created.", "Organisation Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                    } else {
                    JOptionPane.showMessageDialog(null, "You have not accepted " +
                                    "the terms. Please select the checkbox.", "Checkbox Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }

        });

        leftPanel.add(label1);
        leftPanel.add(label2);
        leftPanel.add(t1);
        leftPanel.add(s1);
        leftPanel.add(terms);
        leftPanel.add(submit);
        leftPanel.add(msg);

        mainContent.add(createPanelContainer, "6");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String btnSrcTxt = e.getActionCommand();

        if (btnSrcTxt.equals("Buy")) {
            cardLayout.show(mainContent, "1");
        } else if (btnSrcTxt.equals("Sell")) {
            cardLayout.show(mainContent, "2");
        } else if (btnSrcTxt.equals("Account")) {
            refreshGUI();
            cardLayout.show(mainContent, "3");
        } else if (btnSrcTxt.equals("View Assets")) {
            refreshGUI();
            cardLayout.show(mainContent, "4");
        } else if (btnSrcTxt.equals("Logout")) {
            setVisible(false);
            new LoginGUI(server);
        } else if (btnSrcTxt.equals("My Listings")) {
            refreshGUI();
            cardLayout.show(mainContent, "5");
        } else if (btnSrcTxt.equals("Create")) {
            refreshGUI();
            cardLayout.show(mainContent, "6");
        }

    }
}

