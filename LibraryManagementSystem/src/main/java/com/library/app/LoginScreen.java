package com.library.app;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
//import java.awt.event.*;
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

        setSize(600,500); setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); setLocationRelativeTo(null);

        Color backgroundColor = new Color(240,248,255);
        Color panelColor = new Color(224,235,255);
        Color textColor = new Color(25,25,112);
        //Color buttonBgColor = Color.WHITE;
        //Color buttonHoverColor = new Color(220,220,220);
        Font titleFont = new Font("Arial", Font.BOLD, 26);
        Font labelFont = new Font("Arial", Font.BOLD, 14);
        Font fieldFont = new Font("Arial", Font.PLAIN, 14);
        //Font buttonFont = new Font("Arial", Font.BOLD, 15);

        JPanel mainPanel = new JPanel(new BorderLayout(20,20));
        mainPanel.setBackground(backgroundColor); mainPanel.setBorder(new EmptyBorder(30,40,30,40));

        JLabel titleLabel = new JLabel("User Login",SwingConstants.CENTER);
        titleLabel.setFont(titleFont); titleLabel.setForeground(textColor);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(0,2,15,15));
        formPanel.setBackground(panelColor); formPanel.setBorder(new CompoundBorder(new LineBorder(new Color(173,216,230),1), new EmptyBorder(25,25,25,25)));
        formPanel.add(new JLabel("Email:"){/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

		{setForeground(textColor); setFont(labelFont);}});
        emailField = new JTextField(); emailField.setFont(fieldFont); formPanel.add(emailField);
        formPanel.add(new JLabel("Password:"){/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

		{setForeground(textColor); setFont(labelFont);}});
        passwordField = new JPasswordField(); passwordField.setFont(fieldFont); formPanel.add(passwordField);
        mainPanel.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10,10));
        buttonPanel.setBackground(backgroundColor);
        JButton loginBtn = new JButton("Login"); JButton registerBtn = new JButton("Register Now");
        JButton backBtn = new JButton("← Back");
        buttonPanel.add(backBtn); buttonPanel.add(loginBtn); buttonPanel.add(registerBtn);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        loginBtn.addActionListener(e -> handleLogin());
        registerBtn.addActionListener(e -> { dispose(); new RegistrationScreen(libraryService).setVisible(true); });
        backBtn.addActionListener(e -> { dispose(); new WelcomeScreen(libraryService).setVisible(true); });

        setContentPane(mainPanel);
        setVisible(true);
    }

    private void handleLogin(){
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if(email.isEmpty() || password.isEmpty()){ JOptionPane.showMessageDialog(this,"Fill all fields!"); return; }

        UserAccount user = libraryService.login(email,password);
        if(user!=null){
            JOptionPane.showMessageDialog(this,"Login successful as "+user.getRole());
            dispose();
            new LibraryFrame(libraryService,user).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this,"Invalid email or password!");
        }
    }
}
