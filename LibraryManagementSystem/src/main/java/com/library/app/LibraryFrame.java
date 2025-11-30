package com.library.app;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import com.library.service.*;
import com.library.model.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

// Main dashboard for the library system, handling Admin and User UI views.

public class LibraryFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    private final LibraryService libraryService;
    private final UserAccount currentUser;
    private final JPanel leftPanel, rightPanel;
    
    // UI components for the search panel to allow persistence
    private JComboBox<String> typeCombo;
    private JTextField keywordField;
    private JScrollPane tableScrollPane; 

    public LibraryFrame(LibraryService service, UserAccount user){
        super("Library Dashboard - " + user.getRole() + ": " + user.getEmail());
        this.libraryService = service;
        this.currentUser = user;

        setSize(1000,600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        /* Background setup
        JLabel bgLabel = new JLabel(new ImageIcon("src/main/java/com/library/app/library.jpg"));
        bgLabel.setBounds(0,0,1000,600); 
        add(bgLabel);
*/
        // Left Navigation Panel
        leftPanel = new JPanel(); 
        leftPanel.setLayout(new BoxLayout(leftPanel,BoxLayout.Y_AXIS));
        leftPanel.setOpaque(false); 
        leftPanel.setBounds(30,50,200,480); 
       // bgLabel.add(leftPanel);

        // Right Content Panel
        rightPanel = new JPanel(); 
        rightPanel.setLayout(null);
        rightPanel.setOpaque(false); 
        rightPanel.setBounds(250,50,700,480); 
       // bgLabel.add(rightPanel);

        // Buttons Configuration 
        if("Admin".equalsIgnoreCase(currentUser.getRole())){
            // Admin buttons: Ordered as requested. Removed user-specific functions.
            addNavButton("Add Book", () -> addItemPanel("Book"));
            addNavButton("Add CD", () -> addItemPanel("CD"));
            addNavButton("Search Items", this::setupSearchPanelGUI);
            addNavButton("View Active Loans", this::showActiveLoansAdminPanel); 
            addNavButton("Unregister User", this::unregisterUserPanel);
            
            // Default view for Admin
            showActiveLoansAdminPanel(); 
        } else { 
            // User buttons: Pay Fine integrated into View My Loans.
            addNavButton("Search Items", this::setupSearchPanelGUI);
            addNavButton("Borrow Item", this::borrowItemPanel);
            addNavButton("Return Item", this::returnItemPanel);
            addNavButton("View My Loans", this::showUserLoansPanel); 
            
            // Default view for User
            setupSearchPanelGUI();
        }

        addNavButton("Logout", this::logout);
    }

    private void addNavButton(String text, Runnable action){
        JButton btn = new JButton(text);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(180,40));
        btn.setFont(new Font("Arial", Font.BOLD,16));
        btn.setBackground(new Color(65,105,225));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseEntered(MouseEvent e){ 
            	btn.setBackground(new Color(100,149,237)); 
            }
                   
            @Override
            public void mouseExited(MouseEvent e){ 
            	btn.setBackground(new Color(65,105,225)); 
            }
        });
        btn.addActionListener(e -> action.run());
        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(btn);
    }

    // RIGHT PANEL IMPLEMENTATIONS
    // Add Book/CD (For the admin)
    private void addItemPanel(String type){
        rightPanel.removeAll();
        rightPanel.setLayout(null); 
        JPanel panel = new JPanel(new GridLayout(4,2,10,10));
        panel.setBounds(0,0,680,400); panel.setOpaque(false);

        JTextField titleField = new JTextField(), authorOrArtistField = new JTextField(), isbnField = new JTextField();
        panel.add(new JLabel("Title:")); panel.add(titleField);
        panel.add(new JLabel(type.equals("Book")?"Author:":"Artist:")); panel.add(authorOrArtistField);
        panel.add(new JLabel("ISBN:")); panel.add(isbnField);
        JButton addBtn = new JButton("Add " + type); panel.add(new JLabel()); panel.add(addBtn);

        addBtn.addActionListener(e -> {
            if(titleField.getText().trim().isEmpty() || authorOrArtistField.getText().trim().isEmpty() || isbnField.getText().trim().isEmpty()){
                JOptionPane.showMessageDialog(this,"All fields required!"); return;
            }
            if(type.equals("Book")){
                libraryService.addBook(new Book(titleField.getText(), authorOrArtistField.getText(), isbnField.getText()));
            } else {
                libraryService.addBook(new CD(titleField.getText(), authorOrArtistField.getText(), isbnField.getText()));
            }
            JOptionPane.showMessageDialog(this,type+" added successfully!");
        });

        rightPanel.add(panel); 
        rightPanel.revalidate(); rightPanel.repaint();
    }

    // Search Items GUI (Admin/User) - Setup the static layout (Search + Table)
    private void setupSearchPanelGUI(){
        rightPanel.removeAll();
        rightPanel.setLayout(null);
        
        // 1. Search Controls (Static)
        JLabel typeLabel = new JLabel("Select Type:"); typeLabel.setBounds(10,10,80,25);
        typeCombo = new JComboBox<>(new String[]{"Book","CD"}); typeCombo.setBounds(100,10,120,25);
        JLabel keywordLabel = new JLabel("Keyword:"); keywordLabel.setBounds(240,10,80,25);
        keywordField = new JTextField(); keywordField.setBounds(320,10,150,25);
        JButton searchBtn = new JButton("Search"); searchBtn.setBounds(480,10,100,25);

        // 2. Table Area (Static position)
        DefaultTableModel initialModel = new DefaultTableModel(new Object[][]{}, new String[]{"Type","Title","Author/Artist","ISBN"});
        JTable initialTable = new JTable(initialModel);
        
        tableScrollPane = new JScrollPane(initialTable); 
        tableScrollPane.setBounds(10, 50, 680, 400); 

        rightPanel.add(typeLabel); rightPanel.add(typeCombo); 
        rightPanel.add(keywordLabel); rightPanel.add(keywordField); 
        rightPanel.add(searchBtn);
        rightPanel.add(tableScrollPane); 

        searchBtn.addActionListener(e -> {
            String type = typeCombo.getSelectedItem().toString();
            String keyword = keywordField.getText().trim();
            updateSearchResults(type, keyword);
        });
        
        // Load initial results
        updateSearchResults("Book", "");
        
        rightPanel.revalidate(); rightPanel.repaint();
    }
    
    // Search Items Logic (Admin/User) - Update table content only
    private void updateSearchResults(String type, String keyword){
        List<Book> all = libraryService.getBooks();
        List<Book> filtered = all.stream()
            .filter(b -> b.getType().equals(type) &&
                    (b.getTitle().toLowerCase().contains(keyword.toLowerCase()) ||
                     b.getIsbn().toLowerCase().contains(keyword.toLowerCase()) ||
                     (type.equals("Book")? b.getAuthor().toLowerCase().contains(keyword.toLowerCase()) : (b instanceof CD? ((CD)b).getArtist().toLowerCase().contains(keyword.toLowerCase()) : false))
                    ))
            .toList();

        String[] columns = {"Type","Title","Author/Artist","ISBN"};
        Object[][] data = new Object[filtered.size()][4];
        for(int i=0;i<filtered.size();i++){
            Book b = filtered.get(i);
            data[i][0] = b.getType();
            data[i][1] = b.getTitle();
            data[i][2] = b.getType().equals("Book")? b.getAuthor() : (b instanceof CD? ((CD)b).getArtist() : "");
            data[i][3] = b.getIsbn();
        }

        JTable table = new JTable(data,columns){
             private static final long serialVersionUID = 1L;

			 @Override
             public boolean isCellEditable(int row, int column) {
                 return false; // Prevent table editing
             }
        };
        table.getTableHeader().setReorderingAllowed(false);
        
        tableScrollPane.setViewportView(table); 
        tableScrollPane.revalidate();
        tableScrollPane.repaint();
    }

    // Borrow Item (User)
    private void borrowItemPanel(){
        rightPanel.removeAll();
        rightPanel.setLayout(null);
        JPanel panel = new JPanel(null); panel.setBounds(0,0,680,450); panel.setOpaque(false);
        JLabel label = new JLabel("Enter ISBN to Borrow:"); label.setBounds(10,10,200,25);
        JTextField field = new JTextField(); field.setBounds(200,10,150,25);
        JButton borrow = new JButton("Borrow"); borrow.setBounds(370,10,100,25);
        panel.add(label); panel.add(field); panel.add(borrow);

        borrow.addActionListener(e -> {
            Book b = libraryService.findBookByIsbn(field.getText().trim());
            if(b==null){ JOptionPane.showMessageDialog(this,"Item not found!"); return; }
            boolean ok = libraryService.borrowBook(currentUser,b);
            JOptionPane.showMessageDialog(this, ok?"Item borrowed!":"Cannot borrow (restriction/already borrowed).");
        });

        rightPanel.add(panel); rightPanel.revalidate(); rightPanel.repaint();
    }

    // Return Item (User)
    private void returnItemPanel(){
        rightPanel.removeAll();
        rightPanel.setLayout(null);
        JPanel panel = new JPanel(null); panel.setBounds(0,0,680,450); panel.setOpaque(false);
        JLabel label = new JLabel("Enter ISBN to Return:"); label.setBounds(10,10,200,25);
        JTextField field = new JTextField(); field.setBounds(200,10,150,25);
        JButton ret = new JButton("Return"); ret.setBounds(370,10,100,25);
        panel.add(label); panel.add(field); panel.add(ret);

        ret.addActionListener(e -> {
            Book b = libraryService.findBookByIsbn(field.getText().trim());
            if(b==null){ JOptionPane.showMessageDialog(this,"Item not found!"); return; }
            
            // returnBook handles fine calculation and internal email notification
            boolean ok = libraryService.returnBook(currentUser,b); 
            JOptionPane.showMessageDialog(this, ok?"Item returned!":"Cannot return (not borrowed by you).");
        });

        rightPanel.add(panel); rightPanel.revalidate(); rightPanel.repaint();
    }
    
    // View Active Loans (Admin) - Replaces Borrow/Return/Pay Fine for Admin
    private void showActiveLoansAdminPanel(){
        rightPanel.removeAll();
        rightPanel.setLayout(new BorderLayout());

        JLabel title = new JLabel("All Active Loans", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        rightPanel.add(title, BorderLayout.NORTH);

        String[] columns={"Item Type","Title","User Name","User ID","Borrow Date","Due Date", "Overdue Days"};
        List<Loan> activeLoans = libraryService.getAllActiveLoans();
        Object[][] data = new Object[activeLoans.size()][7];
        LocalDate today = LocalDate.now();

        for(int i=0;i<activeLoans.size();i++){
            Loan l = activeLoans.get(i);
            long overdueDays = l.getDueDate().isBefore(today) ? ChronoUnit.DAYS.between(l.getDueDate(), today) : 0;
            
            data[i][0] = l.getItem().getType();
            data[i][1] = l.getItem().getTitle();
            data[i][2] = l.getUser().getName();
            data[i][3] = l.getUser().getId();
            data[i][4] = l.getBorrowDate();
            data[i][5] = l.getDueDate();
            data[i][6] = overdueDays > 0 ? overdueDays + " (Overdue)" : "0";
        }

        JTable table = new JTable(data,columns){
             private static final long serialVersionUID = 1L;

			 @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table.getTableHeader().setReorderingAllowed(false);

        JScrollPane scroll = new JScrollPane(table); 
        rightPanel.add(scroll, BorderLayout.CENTER);

        rightPanel.revalidate(); rightPanel.repaint();
    }


    // View Loans (User) - Includes conditional Pay Fine form (as requested)
    private void showUserLoansPanel(){
        rightPanel.removeAll();
        rightPanel.setLayout(new BorderLayout());

        JLabel title = new JLabel("My Loan History", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        rightPanel.add(title, BorderLayout.NORTH);

        String[] columns={"Type","Title","Borrow Date","Due Date","Returned"};
        List<Loan> loans = libraryService.getUserLoans(currentUser);
        Object[][] data = new Object[loans.size()][5];

        for(int i=0;i<loans.size();i++){
            Loan l = loans.get(i);
            data[i][0] = l.getItem().getType();
            data[i][1] = l.getItem().getTitle();
            data[i][2] = l.getBorrowDate();
            data[i][3] = l.getDueDate();
            data[i][4] = l.isReturned()?"Yes":"No";
        }

        JTable table = new JTable(data,columns){
             private static final long serialVersionUID = 1L;

			 @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table.getTableHeader().setReorderingAllowed(false);
        JScrollPane scroll = new JScrollPane(table); 
        rightPanel.add(scroll, BorderLayout.CENTER);
        
        // --- Pay Fine Area (Conditional display) ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        bottomPanel.setBackground(new Color(240, 248, 255));
        
        double fineBalance = currentUser.getFineBalance();
        
        if(fineBalance > 0){
            JLabel fineLabel = new JLabel(String.format("Pending Fine Balance: %.2f NIS", fineBalance));
            fineLabel.setForeground(Color.RED.darker());
            fineLabel.setFont(new Font("Arial", Font.BOLD, 16));
            
            JTextField payAmountField = new JTextField(String.format("%.2f", fineBalance), 10);
            JButton payBtn = new JButton("Pay Fine");
            
            payBtn.addActionListener(e -> {
                try {
                    double amount = Double.parseDouble(payAmountField.getText().trim());
                    if (libraryService.payFine(currentUser, amount)) {
                        JOptionPane.showMessageDialog(this, String.format("Paid %.2f NIS. New balance: %.2f NIS", amount, currentUser.getFineBalance()));
                        // Reload panel to update balance/form visibility
                        showUserLoansPanel(); 
                    } else {
                        JOptionPane.showMessageDialog(this, "Invalid payment amount or exceeding current balance.");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid amount.");
                }
            });
            
            bottomPanel.add(fineLabel);
            bottomPanel.add(new JLabel("Amount to Pay:"));
            bottomPanel.add(payAmountField);
            bottomPanel.add(payBtn);
        } else {
            JLabel noFineLabel = new JLabel("You have no pending fines.");
            bottomPanel.add(noFineLabel);
        }

        rightPanel.add(bottomPanel, BorderLayout.SOUTH);

        rightPanel.revalidate(); rightPanel.repaint();
    }

    // Unregister User (Admin)
    private void unregisterUserPanel(){
        rightPanel.removeAll();
        rightPanel.setLayout(null);
        JPanel panel = new JPanel(null); panel.setBounds(0,0,680,450); panel.setOpaque(false);
        JLabel label = new JLabel("User ID to Unregister (Regular User Only):"); label.setBounds(10,10,300,25);
        JTextField field = new JTextField(); field.setBounds(310,10,150,25);
        JButton btn = new JButton("Unregister"); btn.setBounds(470,10,120,25);
        panel.add(label); panel.add(field); panel.add(btn);

        btn.addActionListener(e -> {
            boolean ok = libraryService.unregisterUser(currentUser, field.getText().trim());
            if(ok){
                JOptionPane.showMessageDialog(this, "User unregistered successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Cannot unregister user: Check ID, role, active loans, or pending fines.");
            }
        });

        rightPanel.add(panel); rightPanel.revalidate(); rightPanel.repaint();
    }

    private void logout(){
        dispose();
        new LoginScreen(libraryService).setVisible(true);
    }
}