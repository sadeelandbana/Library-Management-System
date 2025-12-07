package com.library.app;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import com.library.service.LibraryService;
import com.library.model.UserAccount;

public class LoginScreen extends JFrame {
    private static final long serialVersionUID = 1L;
    private LibraryService libraryService;
    private JTextField emailField;
    private JPasswordField passwordField;

    public LoginScreen(LibraryService service){
        super("Library Login");
        this.libraryService = service;

        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Colors & Fonts
        Color backgroundColor = new Color(240,248,255);
        Color panelColor = new Color(224,235,255);
        Color textColor = new Color(25,25,112);
        Font titleFont = new Font("Arial", Font.BOLD, 26);
        Font labelFont = new Font("Arial", Font.BOLD, 14);
        Font fieldFont = new Font("Arial", Font.PLAIN, 16);
        Font buttonFont = new Font("Arial", Font.BOLD, 16);

        JPanel mainPanel = new JPanel(new BorderLayout(20,20));
        mainPanel.setBackground(backgroundColor);
        mainPanel.setBorder(new EmptyBorder(30,40,30,40));

        JLabel titleLabel = new JLabel("User Login", SwingConstants.CENTER);
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(textColor);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // ---------------- FORM PANEL ----------------
        JPanel formPanel = new JPanel(new GridLayout(0, 1, 15, 15));
        formPanel.setBackground(panelColor);
        formPanel.setBorder(new CompoundBorder(
                new LineBorder(new Color(173,216,230), 1),
                new EmptyBorder(25,25,25,25)
        ));

        // EMAIL ROW
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(labelFont);
        emailLabel.setForeground(textColor);

        JPanel emailRow = new JPanel(new BorderLayout(10, 0));
        emailRow.setBackground(panelColor);
        JLabel emailIcon = new JLabel(new ImageIcon("icons/email.png"));  // غيّر المسار
        emailField = new JTextField();
        emailField.setFont(fieldFont);
        emailField.setPreferredSize(new Dimension(250, 40));
        emailRow.add(emailIcon, BorderLayout.WEST);
        emailRow.add(emailField, BorderLayout.CENTER);

        // PASSWORD ROW
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(labelFont);
        passLabel.setForeground(textColor);

        JPanel passRow = new JPanel(new BorderLayout(10, 0));
        passRow.setBackground(panelColor);
        JLabel passIcon = new JLabel(new ImageIcon("icons/password.png")); // غيّر المسار
        passwordField = new JPasswordField();
        passwordField.setFont(fieldFont);
        passwordField.setPreferredSize(new Dimension(250, 40));
        passRow.add(passIcon, BorderLayout.WEST);
        passRow.add(passwordField, BorderLayout.CENTER);

        // إضافة الحقول للوحة
        formPanel.add(emailLabel);
        formPanel.add(emailRow);
        formPanel.add(passLabel);
        formPanel.add(passRow);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // ---------------- BUTTON PANEL ----------------
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10,10));
        buttonPanel.setBackground(backgroundColor);

        JButton backBtn = new JButton("← Back");
        JButton loginBtn = new JButton("Login");
        JButton registerBtn = new JButton("Register Now");

        // Button styling unified
        styleButton(loginBtn);
        styleButton(registerBtn);
        styleButton(backBtn);

        buttonPanel.add(backBtn);
        buttonPanel.add(loginBtn);
        buttonPanel.add(registerBtn);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Actions
        loginBtn.addActionListener(e -> handleLogin());
        registerBtn.addActionListener(e -> {
            dispose();
            new RegistrationScreen(libraryService).setVisible(true);
        });
        backBtn.addActionListener(e -> {
            dispose();
            new WelcomeScreen(libraryService).setVisible(true);
        });

        setContentPane(mainPanel);
        setVisible(true);
    }

    // ---------------- BUTTON STYLE ----------------
    private void styleButton(JButton btn){
        btn.setFont(new Font("Arial", Font.BOLD, 15));
        btn.setBackground(new Color(52, 152, 219));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(new LineBorder(new Color(41, 128, 185), 2));
        btn.setPreferredSize(new Dimension(140, 40));
    }

    private void handleLogin(){
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if(email.isEmpty() || password.isEmpty()){
            JOptionPane.showMessageDialog(this,"Fill all fields!");
            return;
        }

        UserAccount user = libraryService.login(email, password);
        if(user != null){
            JOptionPane.showMessageDialog(this, "Login successful as " + user.getRole());
            dispose();
            new LibraryFrame(libraryService, user).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Invalid email or password!");
        }
    }
}
