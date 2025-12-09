package com.library.app;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;
import com.library.service.*;
import com.library.model.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class LibraryFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    private final LibraryService libraryService;
    private final UserAccount currentUser;
    private final JPanel leftPanel, rightPanel;

    // Unified Color Palette
    private final Color backgroundColor = new Color(245, 243, 240); // #F5F3F0 - Vanilla Mist
    private final Color panelColor = new Color(218, 210, 205);     // #DAD2CD - Mushroom Beige
    private final Color textColor = new Color(56, 43, 38);        // #382B26 - Dark Roast (for H1 Titles & Primary Buttons)
    private final Color secondaryTextColor = new Color(156, 126, 101); // #9C7E65 - Soft Leather (Secondary Titles/Borders)
    private final Color buttonPrimaryBgColor = new Color(56, 43, 38); // #382B26 - Dark Roast (Primary Buttons)
    private final Color buttonPrimaryHoverColor = new Color(80, 60, 50); // Lighter Dark Roast
    private final Color buttonSecondaryBgColor = new Color(111, 86, 65); // #6F5641 - Mudstone (Secondary Buttons)
    private final Color buttonSecondaryHoverColor = new Color(140, 110, 90); // Lighter Mudstone
    private final Color borderColor = new Color(156, 126, 101);    // #9C7E65 - Soft Leather (for rounded borders where needed)

    // Unified Font Scheme
    private final Font titleFont = new Font("Arial", Font.BOLD, 24);
    private final Font subtitleFont = new Font("Arial", Font.ITALIC, 20);
    private final Font headingFont = new Font("Arial", Font.BOLD, 20);
    private final Font paragraphFont = new Font("Arial", Font.PLAIN, 16);
    private final Font labelFont = new Font("Arial", Font.BOLD, 14);
    private final Font fieldFont = new Font("Arial", Font.PLAIN, 14);
    private final Font buttonFont = new Font("Arial", Font.BOLD, 14);

    // UI components for the search panel
    private JComboBox<String> typeCombo;
    private JTextField keywordField;
    private JScrollPane tableScrollPane;

    public LibraryFrame(LibraryService service, UserAccount user) {
        super("Library Dashboard - " + user.getRole() + ": " + user.getEmail());
        this.libraryService = service;
        this.currentUser = user;

        setSize(1000, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel bgLabel = new JLabel(new ImageIcon("src/main/java/com/library/app/library.jpg"));
        bgLabel.setBounds(0, 0, 1000, 600);
        add(bgLabel);

        leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setOpaque(false);
        leftPanel.setBounds(30, 50, 200, 480);
        bgLabel.add(leftPanel);

        rightPanel = new JPanel();
        rightPanel.setLayout(null);
        rightPanel.setOpaque(false);
        rightPanel.setBounds(250, 50, 700, 480);
        bgLabel.add(rightPanel);

        if ("Admin".equalsIgnoreCase(currentUser.getRole())) {
            addNavButton("Add Book", () -> addItemPanel("Book"));
            addNavButton("Add CD", () -> addItemPanel("CD"));
            addNavButton("Search Items", this::setupSearchPanelGUI);
            addNavButton("View Active Loans", this::showActiveLoansAdminPanel);
            addNavButton("Unregister User", this::unregisterUserPanel);
            showActiveLoansAdminPanel();
        } else {
            addNavButton("Search Items", this::setupSearchPanelGUI);
            addNavButton("Borrow Item", this::borrowItemPanel);
            addNavButton("Return Item", this::returnItemPanel);
            addNavButton("View My Loans", this::showUserLoansPanel);
            setupSearchPanelGUI();
        }

        addNavButton("Logout", this::logout);
    }

    private void addNavButton(String text, Runnable action) {
        JButton btn = new JButton(text);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(180, 40));
        btn.setFont(buttonFont);
        btn.setBackground(buttonPrimaryBgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new RoundedBorder(buttonPrimaryBgColor, 1, 15)); // Apply rounded border to buttons
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(buttonPrimaryHoverColor); }
            public void mouseExited(MouseEvent e) { btn.setBackground(buttonPrimaryBgColor); }
        });
        btn.addActionListener(e -> action.run());
        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(btn);
    }

    // ================= ADD BOOK / CD =================
 // ================= ADD BOOK / CD =================
    private void addItemPanel(String type) {
        rightPanel.removeAll();
        rightPanel.setLayout(null);

        // Title label
        JLabel header = new JLabel("Add " + type, SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 22));
        header.setForeground(textColor);
        header.setBounds(0, 0, 680, 40);
        rightPanel.add(header);

        // Panel with better spacing
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setBounds(80, 70, 520, 260);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.anchor = GridBagConstraints.WEST;

        // Bigger, clearer labels
        Font labelF = new Font("Arial", Font.BOLD, 18);

        JLabel titleLbl = new JLabel("Title:");
        titleLbl.setFont(labelF);
        titleLbl.setForeground(textColor);

        JLabel authorLbl = new JLabel(type.equals("Book") ? "Author:" : "Artist:");
        authorLbl.setFont(labelF);
        authorLbl.setForeground(textColor);

        JLabel isbnLbl = new JLabel("ISBN:");
        isbnLbl.setFont(labelF);
        isbnLbl.setForeground(textColor);

        // Text fields resized + shaped
        JTextField titleField = new JTextField();
        titleField.setFont(fieldFont);
        titleField.setPreferredSize(new Dimension(260, 36));
        titleField.setBorder(new RoundedBorder(borderColor, 1, 12));

        JTextField authorField = new JTextField();
        authorField.setFont(fieldFont);
        authorField.setPreferredSize(new Dimension(260, 36));
        authorField.setBorder(new RoundedBorder(borderColor, 1, 12));

        JTextField isbnField = new JTextField();
        isbnField.setFont(fieldFont);
        isbnField.setPreferredSize(new Dimension(260, 36));
        isbnField.setBorder(new RoundedBorder(borderColor, 1, 12));

        // Add rows in perfect alignment
        gbc.gridx = 0; gbc.gridy = 0; panel.add(titleLbl, gbc);
        gbc.gridx = 1;              panel.add(titleField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; panel.add(authorLbl, gbc);
        gbc.gridx = 1;              panel.add(authorField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; panel.add(isbnLbl, gbc);
        gbc.gridx = 1;              panel.add(isbnField, gbc);

        // Add button
        JButton addBtn = new JButton("Add " + type);
        addBtn.setFont(buttonFont);
        addBtn.setBackground(buttonPrimaryBgColor);
        addBtn.setForeground(Color.WHITE);
        addBtn.setFocusPainted(false);
        addBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addBtn.setBorder(new RoundedBorder(buttonPrimaryBgColor, 1, 15));
        addBtn.setPreferredSize(new Dimension(200, 40));

        addBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { addBtn.setBackground(buttonPrimaryHoverColor); }
            public void mouseExited(MouseEvent e) { addBtn.setBackground(buttonPrimaryBgColor); }
        });

        // Button placement
        gbc.gridx = 1; gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(addBtn, gbc);

        // Action for add
        addBtn.addActionListener(e -> {
            if (titleField.getText().trim().isEmpty() ||
                authorField.getText().trim().isEmpty() ||
                isbnField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields required!");
                return;
            }

            if (type.equals("Book")) {
                libraryService.addBook(new Book(titleField.getText(), authorField.getText(), isbnField.getText()));
            } else {
                libraryService.addBook(new CD(titleField.getText(), authorField.getText(), isbnField.getText()));
            }
            JOptionPane.showMessageDialog(this, type + " added successfully!");
        });

        rightPanel.add(panel);
        rightPanel.revalidate();
        rightPanel.repaint();
    }

    // ================= SEARCH PANEL =================
 
    private void setupSearchPanelGUI() {
        rightPanel.removeAll();
        rightPanel.setLayout(null);

        JLabel typeLabel = new JLabel("Select Type:");
        typeLabel.setBounds(10, 10, 120, 30);
        typeLabel.setFont(labelFont);
        typeLabel.setForeground(textColor);

        typeCombo = new JComboBox<>(new String[]{"Book", "CD"});
        typeCombo.setBounds(130, 10, 150, 32);
        
        typeCombo.setFont(fieldFont);
        typeCombo.setBorder(new RoundedBorder(borderColor, 1, 10));

    
        typeCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {

                JLabel label = (JLabel) super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);

                label.setFont(fieldFont);
                label.setHorizontalAlignment(SwingConstants.LEFT);
                label.setPreferredSize(new Dimension(140, 28));

                return label;
            }
        });

        JLabel keywordLabel = new JLabel("Keyword:");
        keywordLabel.setBounds(300, 10, 100, 30);
        keywordLabel.setFont(labelFont);
        keywordLabel.setForeground(textColor);

        keywordField = new JTextField();
        keywordField.setFont(fieldFont);
        keywordField.setBounds(380, 10, 180, 32);
        keywordField.setBorder(new RoundedBorder(borderColor, 1, 10));

       
        JButton searchBtn = new JButton("Search");
        searchBtn.setBounds(570, 10, 110, 32);
        searchBtn.setFont(buttonFont);
        searchBtn.setBackground(buttonPrimaryBgColor);
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setFocusPainted(false);
        searchBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        searchBtn.setBorder(new RoundedBorder(buttonPrimaryBgColor, 1, 12));

        searchBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { searchBtn.setBackground(buttonPrimaryHoverColor); }
            public void mouseExited(MouseEvent e)  { searchBtn.setBackground(buttonPrimaryBgColor); }
        });

        DefaultTableModel emptyModel =
                new DefaultTableModel(new Object[][]{}, new String[]{"Type", "Title", "Author/Artist", "ISBN"});
        JTable emptyTable = new JTable(emptyModel);
        emptyTable.setFont(paragraphFont);
        emptyTable.getTableHeader().setFont(labelFont);

        tableScrollPane = new JScrollPane(emptyTable);
        tableScrollPane.setBounds(10, 60, 680, 380);

       
        rightPanel.add(typeLabel);
        rightPanel.add(typeCombo);
        rightPanel.add(keywordLabel);
        rightPanel.add(keywordField);
        rightPanel.add(searchBtn);
        rightPanel.add(tableScrollPane);

        searchBtn.addActionListener(e -> {
            String type = typeCombo.getSelectedItem().toString();
            String keyword = keywordField.getText().trim();
            updateSearchResults(type, keyword);
        });

        rightPanel.revalidate();
        rightPanel.repaint();
    }

    private void updateSearchResults(String type, String keyword) {
        List<Book> all = libraryService.getBooks();
        List<Book> filtered = all.stream()
                .filter(b -> b.getType().equals(type) &&
                        (b.getTitle().toLowerCase().contains(keyword.toLowerCase()) ||
                                b.getIsbn().toLowerCase().contains(keyword.toLowerCase()) ||
                                (type.equals("Book")
                                        ? b.getAuthor().toLowerCase().contains(keyword.toLowerCase())
                                        : (b instanceof CD ? ((CD) b).getArtist().toLowerCase().contains(keyword.toLowerCase()) : false))
                ))
                .toList();

        String[] columns = {"Type", "Title", "Author/Artist", "ISBN"};
        Object[][] data = new Object[filtered.size()][4];

        for (int i = 0; i < filtered.size(); i++) {
            Book b = filtered.get(i);
            data[i][0] = b.getType();
            data[i][1] = b.getTitle();
            data[i][2] = b.getType().equals("Book") ? b.getAuthor() :
                    (b instanceof CD ? ((CD) b).getArtist() : "");
            data[i][3] = b.getIsbn();
        }

        JTable table = new JTable(data, columns) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table.setFont(paragraphFont);
        table.getTableHeader().setFont(labelFont);

        tableScrollPane.setViewportView(table);
        tableScrollPane.revalidate();
        tableScrollPane.repaint();
    }

    // ================= BORROW =================
    private void borrowItemPanel() {
        rightPanel.removeAll();
        rightPanel.setLayout(null);

        JPanel panel = new JPanel(null);
        panel.setBounds(0, 0, 680, 450);
        panel.setOpaque(false);

        JLabel label = new JLabel("Enter ISBN to Borrow:");
        label.setBounds(10, 10, 200, 25); label.setFont(labelFont); label.setForeground(textColor);
        JTextField field = new JTextField();
        field.setBounds(200, 10, 150, 25); field.setFont(fieldFont); field.setBorder(new RoundedBorder(borderColor, 1, 10));
        JButton borrow = new JButton("Borrow");
        borrow.setBounds(370, 10, 100, 25);
        borrow.setFont(buttonFont);
        borrow.setBackground(buttonPrimaryBgColor);
        borrow.setForeground(Color.WHITE);
        borrow.setFocusPainted(false);
        borrow.setCursor(new Cursor(Cursor.HAND_CURSOR));
        borrow.setBorder(new RoundedBorder(buttonPrimaryBgColor, 1, 15));
        borrow.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { borrow.setBackground(buttonPrimaryHoverColor); }
            public void mouseExited(MouseEvent e) { borrow.setBackground(buttonPrimaryBgColor); }
        });

        panel.add(label);
        panel.add(field);
        panel.add(borrow);

        borrow.addActionListener(e -> {
            Book b = libraryService.findBookByIsbn(field.getText().trim());
            if (b == null) {
                JOptionPane.showMessageDialog(this, "Item not found!");
                return;
            }
            boolean ok = libraryService.borrowBook(currentUser, b);
            JOptionPane.showMessageDialog(this, ok ? "Item borrowed!" : "Cannot borrow.");
        });

        rightPanel.add(panel);
        rightPanel.revalidate();
        rightPanel.repaint();
    }

    // ================= RETURN =================
    private void returnItemPanel() {
        rightPanel.removeAll();
        rightPanel.setLayout(null);

        JPanel panel = new JPanel(null);
        panel.setBounds(0, 0, 680, 450);
        panel.setOpaque(false);

        JLabel label = new JLabel("Enter ISBN to Return:");
        label.setBounds(10, 10, 200, 25); label.setFont(labelFont); label.setForeground(textColor);
        JTextField field = new JTextField();
        field.setBounds(200, 10, 150, 25); field.setFont(fieldFont); field.setBorder(new RoundedBorder(borderColor, 1, 10));
        JButton ret = new JButton("Return");
        ret.setBounds(370, 10, 100, 25);
        ret.setFont(buttonFont);
        ret.setBackground(buttonPrimaryBgColor);
        ret.setForeground(Color.WHITE);
        ret.setFocusPainted(false);
        ret.setCursor(new Cursor(Cursor.HAND_CURSOR));
        ret.setBorder(new RoundedBorder(buttonPrimaryBgColor, 1, 15));
        ret.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { ret.setBackground(buttonPrimaryHoverColor); }
            public void mouseExited(MouseEvent e) { ret.setBackground(buttonPrimaryBgColor); }
        });

        panel.add(label);
        panel.add(field);
        panel.add(ret);

        ret.addActionListener(e -> {
            Book b = libraryService.findBookByIsbn(field.getText().trim());
            if (b == null) {
                JOptionPane.showMessageDialog(this, "Item not found!");
                return;
            }

            boolean ok = libraryService.returnBook(currentUser, b);
            JOptionPane.showMessageDialog(this, ok ? "Item returned!" : "Cannot return.");
        });

        rightPanel.add(panel);
        rightPanel.revalidate();
        rightPanel.repaint();
    }

    // ================ ACTIVE LOANS (ADMIN) ================
    private void showActiveLoansAdminPanel() {

        rightPanel.removeAll();
        rightPanel.setLayout(new BorderLayout());

        JLabel title = new JLabel("All Active Loans", SwingConstants.CENTER);
        title.setFont(headingFont);
        title.setForeground(textColor);
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        rightPanel.add(title, BorderLayout.NORTH);

        String[] columns = {
                "Item Type", "Title", "User Name", "User ID", "Borrow Date",
                "Due Date", "Overdue Days"
        };

        List<Loan> activeLoans = libraryService.getAllActiveLoans();
        Object[][] data = new Object[activeLoans.size()][7];
        LocalDate today = LocalDate.now();

        for (int i = 0; i < activeLoans.size(); i++) {
            Loan l = activeLoans.get(i);
            long overdueDays = l.getDueDate().isBefore(today)
                    ? ChronoUnit.DAYS.between(l.getDueDate(), today)
                    : 0;

            data[i][0] = l.getItem().getType();
            data[i][1] = l.getItem().getTitle();
            data[i][2] = l.getUser().getName();
            data[i][3] = l.getUser().getId();
            data[i][4] = l.getBorrowDate();
            data[i][5] = l.getDueDate();
            data[i][6] = overdueDays > 0 ? overdueDays + " (Overdue)" : "0";
        }

        JTable table = new JTable(data, columns) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table.getTableHeader().setReorderingAllowed(false);
        table.setFont(paragraphFont);
        table.getTableHeader().setFont(labelFont);
        table.setForeground(textColor);
        table.setBackground(panelColor);
        table.getTableHeader().setBackground(panelColor.darker());
        table.getTableHeader().setForeground(textColor);
        table.setBorder(new RoundedBorder(borderColor, 1, 10)); // Rounded border for table

        rightPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        rightPanel.revalidate();
        rightPanel.repaint();
    }

    // ================= USER LOANS =================
    private void showUserLoansPanel() {
        rightPanel.removeAll();
        rightPanel.setLayout(new BorderLayout());

        JLabel title = new JLabel("My Loan History", SwingConstants.CENTER);
        title.setFont(headingFont);
        title.setForeground(textColor);
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        rightPanel.add(title, BorderLayout.NORTH);

        String[] columns = {"Type", "Title", "Borrow Date", "Due Date", "Returned"};
        List<Loan> loans = libraryService.getUserLoans(currentUser);
        Object[][] data = new Object[loans.size()][5];

        for (int i = 0; i < loans.size(); i++) {
            Loan l = loans.get(i);
            data[i][0] = l.getItem().getType();
            data[i][1] = l.getItem().getTitle();
            data[i][2] = l.getBorrowDate();
            data[i][3] = l.getDueDate();
            data[i][4] = l.isReturned() ? "Yes" : "No";
        }

        JTable table = new JTable(data, columns) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table.getTableHeader().setReorderingAllowed(false);
        table.setFont(paragraphFont);
        table.getTableHeader().setFont(labelFont);
        table.setForeground(textColor);
        table.setBackground(panelColor);
        table.getTableHeader().setBackground(panelColor.darker());
        table.getTableHeader().setForeground(textColor);
        table.setBorder(new RoundedBorder(borderColor, 1, 10)); // Rounded border for table
        rightPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout());
        double fineBalance = currentUser.getFineBalance();

        if (fineBalance > 0) {
            JLabel fineLabel = new JLabel("Pending Fine: " + fineBalance + " NIS");
            fineLabel.setFont(labelFont); fineLabel.setForeground(textColor);
            JTextField payField = new JTextField(String.valueOf(fineBalance), 10);
            payField.setFont(fieldFont);
            payField.setBorder(new RoundedBorder(borderColor, 1, 10));
            JButton payBtn = new JButton("Pay Fine");
            payBtn.setFont(buttonFont);
            payBtn.setBackground(buttonPrimaryBgColor);
            payBtn.setForeground(Color.WHITE);
            payBtn.setFocusPainted(false);
            payBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            payBtn.setBorder(new RoundedBorder(buttonPrimaryBgColor, 1, 15));
            payBtn.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { payBtn.setBackground(buttonPrimaryHoverColor); }
                public void mouseExited(MouseEvent e) { payBtn.setBackground(buttonPrimaryBgColor); }
            });

            bottomPanel.add(fineLabel);
            bottomPanel.add(payField);
            bottomPanel.add(payBtn);
        } else {
            JLabel noFineLabel = new JLabel("No pending fines.");
            noFineLabel.setFont(labelFont); noFineLabel.setForeground(textColor);
            bottomPanel.add(noFineLabel);
        }

        rightPanel.add(bottomPanel, BorderLayout.SOUTH);

        rightPanel.revalidate();
        rightPanel.repaint();
    }

    // ================= UNREGISTER =================
    private void unregisterUserPanel() {
        rightPanel.removeAll();
        rightPanel.setLayout(null);

        JPanel panel = new JPanel(null);
        panel.setOpaque(false);
        panel.setBounds(0, 0, 680, 450);

        JLabel label = new JLabel("User ID to Unregister:");
        label.setBounds(10, 10, 250, 25); label.setFont(labelFont); label.setForeground(textColor);
        JTextField field = new JTextField();
        field.setBounds(260, 10, 150, 25); field.setFont(fieldFont); field.setBorder(new RoundedBorder(borderColor, 1, 10));
        JButton btn = new JButton("Unregister");
        btn.setBounds(430, 10, 120, 25);
        btn.setFont(buttonFont);
        btn.setBackground(buttonPrimaryBgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new RoundedBorder(buttonPrimaryBgColor, 1, 15));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(buttonPrimaryHoverColor); }
            public void mouseExited(MouseEvent e) { btn.setBackground(buttonPrimaryBgColor); }
        });

        panel.add(label);
        panel.add(field);
        panel.add(btn);

        btn.addActionListener(e -> {
            boolean ok = libraryService.unregisterUser(currentUser, field.getText().trim());
            JOptionPane.showMessageDialog(this, ok ? "User unregistered!" : "Cannot unregister user.");
        });

        rightPanel.add(panel);
        rightPanel.revalidate();
        rightPanel.repaint();
    }

    private void logout() {
        dispose();
        new LoginScreen(libraryService).setVisible(true);
    }

    // Custom Rounded Border Class
    class RoundedBorder extends AbstractBorder {
        private Color color;
        private int thickness;
        private int radius;
        private Insets insets;

        RoundedBorder(Color color, int thickness, int radius) {
            this.color = color;
            this.thickness = thickness;
            this.radius = radius;
            this.insets = new Insets(radius, radius, radius, radius);
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(thickness));
            g2.drawRoundRect(x, y, width - thickness, height - thickness, radius, radius);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return insets;
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = insets.top = insets.right = insets.bottom = radius;
            return insets;
        }
    }
}