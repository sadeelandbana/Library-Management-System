package com.library.app;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import com.library.service.LibraryService;
import com.library.model.UserAccount;

public class RegistrationScreen extends JFrame {

    private LibraryService libraryService;
    private JTextField idField,nameField,emailField,phoneField,adminCodeField;
    private JPasswordField passwordField;
    private JComboBox<String> roleComboBox;

    // Unified Color Palette
    private final Color backgroundColor = new Color(247, 241, 236); // #F7F1EC - Vanilla Mist
    private final Color panelColor = new Color(216, 195, 176);     // #D8C3B0 - Pale Biscuit
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

    public RegistrationScreen(LibraryService service){
        super("Register"); this.libraryService = service;
        setSize(650,600); setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(20,20));
        mainPanel.setBackground(backgroundColor); mainPanel.setBorder(new EmptyBorder(30,40,30,40));
        JLabel titleLabel = new JLabel("Create New Account",SwingConstants.CENTER);
        titleLabel.setFont(titleFont); titleLabel.setForeground(textColor); mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(0,2,15,15));
        formPanel.setBackground(panelColor); formPanel.setBorder(new CompoundBorder(new RoundedBorder(borderColor, 1, 15),new EmptyBorder(25,25,25,25)));
        formPanel.add(new JLabel("Account Type:"){{setForeground(textColor); setFont(labelFont);}});
     // Create ComboBox with larger size
        roleComboBox = new JComboBox<>(new String[]{"User", "Admin"});
        roleComboBox.setFont(fieldFont);
        roleComboBox.setPreferredSize(new Dimension(240, 35));  // ← العرض الجديد

        // Wrap ComboBox inside a panel to respect preferred size
        JPanel comboWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        comboWrapper.setOpaque(false);
        comboWrapper.add(roleComboBox);

        // Add wrapper instead of comboBox directly:
        formPanel.add(comboWrapper);
        formPanel.add(new JLabel("User ID:"){{setForeground(textColor); setFont(labelFont);}});
        idField = new JTextField(); idField.setFont(fieldFont); idField.setBorder(new RoundedBorder(borderColor, 1, 10)); formPanel.add(idField);
        formPanel.add(new JLabel("Full Name:"){{setForeground(textColor); setFont(labelFont);}});
        nameField = new JTextField(); nameField.setFont(fieldFont); nameField.setBorder(new RoundedBorder(borderColor, 1, 10)); formPanel.add(nameField);
        formPanel.add(new JLabel("Email:"){{setForeground(textColor); setFont(labelFont);}});
        emailField = new JTextField(); emailField.setFont(fieldFont); emailField.setBorder(new RoundedBorder(borderColor, 1, 10)); formPanel.add(emailField);
        formPanel.add(new JLabel("Phone:"){{setForeground(textColor); setFont(labelFont);}});
        phoneField = new JTextField(); phoneField.setFont(fieldFont); phoneField.setBorder(new RoundedBorder(borderColor, 1, 10)); formPanel.add(phoneField);
        formPanel.add(new JLabel("Password:"){{setForeground(textColor); setFont(labelFont);}});
        passwordField = new JPasswordField(); passwordField.setFont(fieldFont); passwordField.setBorder(new RoundedBorder(borderColor, 1, 10)); formPanel.add(passwordField);
        formPanel.add(new JLabel("Admin Code:"){{setForeground(textColor); setFont(labelFont);}});
        adminCodeField = new JTextField(); adminCodeField.setFont(fieldFont); adminCodeField.setBorder(new RoundedBorder(borderColor, 1, 10)); formPanel.add(adminCodeField);
        adminCodeField.setVisible(false); formPanel.getComponent(12).setVisible(false);

        roleComboBox.addActionListener(e -> {
            boolean isAdmin = roleComboBox.getSelectedItem().equals("Admin");
            adminCodeField.setVisible(isAdmin); formPanel.getComponent(12).setVisible(isAdmin);
        });

        mainPanel.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER,20,10));
        buttonPanel.setBackground(backgroundColor);
        JButton backBtn = createStyledButton("← Back", buttonSecondaryBgColor, buttonSecondaryHoverColor, textColor, buttonFont);
        JButton registerBtn = createStyledButton("Register", buttonPrimaryBgColor, buttonPrimaryHoverColor, Color.WHITE, buttonFont);
        buttonPanel.add(backBtn); buttonPanel.add(registerBtn);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        backBtn.addActionListener(e -> { dispose(); new LoginScreen(libraryService).setVisible(true); });
        registerBtn.addActionListener(e -> handleRegistration());

        setContentPane(mainPanel); setVisible(true);
    }

    private JButton createStyledButton(String text, Color bg, Color hover, Color fg, Font font){
        JButton btn = new JButton(text);
        btn.setFont(font); btn.setBackground(bg); btn.setForeground(fg);
        btn.setFocusPainted(false); btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e){ btn.setBackground(hover); }
            public void mouseExited(MouseEvent e){ btn.setBackground(bg); }
        });
        return btn;
    }

    private void handleRegistration(){
        String role = (String)roleComboBox.getSelectedItem();
        String id = idField.getText().trim();
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String adminCode = adminCodeField.getText().trim();

        if(id.isEmpty() || name.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()){
            JOptionPane.showMessageDialog(this,"Fill all fields!"); return;
        }
        if(!name.matches("[a-zA-Z\\s]+")){ JOptionPane.showMessageDialog(this,"Name invalid!"); return; }
        if(!email.contains("@")){ JOptionPane.showMessageDialog(this,"Email invalid!"); return; }
        if(!phone.matches("\\d+")){ JOptionPane.showMessageDialog(this,"Phone invalid!"); return; }
        if("Admin".equals(role) && !adminCode.equals("ADM2025")){ JOptionPane.showMessageDialog(this,"Invalid admin code!"); return; }

        UserAccount newUser = new UserAccount(id,name,email,phone,password,role);
        boolean success = libraryService.registerUser(newUser);
        if(!success){ JOptionPane.showMessageDialog(this,"Email or ID exists!"); return; }

        JOptionPane.showMessageDialog(this,"Registration successful!");
        dispose(); new LoginScreen(libraryService).setVisible(true);
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
