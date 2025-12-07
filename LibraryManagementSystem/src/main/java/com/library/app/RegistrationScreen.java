package com.library.app;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import com.library.service.LibraryService;
import com.library.model.UserAccount;

public class RegistrationScreen extends JFrame {

    private LibraryService libraryService;
    private JTextField idField,nameField,emailField,phoneField,adminCodeField;
    private JPasswordField passwordField;
    private JComboBox<String> roleComboBox;

    public RegistrationScreen(LibraryService service){
        super("Register"); this.libraryService = service;
        setSize(650,600); setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); setLocationRelativeTo(null);

        Color backgroundColor = new Color(240,248,255);
        Color panelColor = new Color(224,235,255);
        Color textColor = new Color(25,25,112);
        Color buttonBgColor = Color.WHITE;
        Color buttonHoverColor = new Color(220,220,220);
        Font titleFont = new Font("Arial", Font.BOLD, 24);
        Font labelFont = new Font("Arial", Font.BOLD, 14);
        Font fieldFont = new Font("Arial", Font.PLAIN, 14);
        Font buttonFont = new Font("Arial", Font.BOLD, 15);

        JPanel mainPanel = new JPanel(new BorderLayout(20,20));
        mainPanel.setBackground(backgroundColor); mainPanel.setBorder(new EmptyBorder(30,40,30,40));
        JLabel titleLabel = new JLabel("Create New Account",SwingConstants.CENTER);
        titleLabel.setFont(titleFont); titleLabel.setForeground(textColor); mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(0,2,15,15));
        formPanel.setBackground(panelColor); formPanel.setBorder(new CompoundBorder(new LineBorder(new Color(173,216,230)),new EmptyBorder(25,25,25,25)));
        formPanel.add(new JLabel("Account Type:"){{setForeground(textColor); setFont(labelFont);}});
        roleComboBox = new JComboBox<>(new String[]{"User","Admin"}); formPanel.add(roleComboBox);
        formPanel.add(new JLabel("User ID:"){{setForeground(textColor); setFont(labelFont);}});
        idField = new JTextField(); formPanel.add(idField);
        formPanel.add(new JLabel("Full Name:"){{setForeground(textColor); setFont(labelFont);}});
        nameField = new JTextField(); formPanel.add(nameField);
        formPanel.add(new JLabel("Email:"){{setForeground(textColor); setFont(labelFont);}});
        emailField = new JTextField(); formPanel.add(emailField);
        formPanel.add(new JLabel("Phone:"){{setForeground(textColor); setFont(labelFont);}});
        phoneField = new JTextField(); formPanel.add(phoneField);
        formPanel.add(new JLabel("Password:"){{setForeground(textColor); setFont(labelFont);}});
        passwordField = new JPasswordField(); formPanel.add(passwordField);
        formPanel.add(new JLabel("Admin Code:"){{setForeground(textColor); setFont(labelFont);}});
        adminCodeField = new JTextField(); formPanel.add(adminCodeField);
        adminCodeField.setVisible(false); formPanel.getComponent(12).setVisible(false);

        roleComboBox.addActionListener(e -> {
            boolean isAdmin = roleComboBox.getSelectedItem().equals("Admin");
            adminCodeField.setVisible(isAdmin); formPanel.getComponent(12).setVisible(isAdmin);
        });

        mainPanel.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER,20,10));
        buttonPanel.setBackground(backgroundColor);
        JButton backBtn = new JButton("← Back"); JButton registerBtn = new JButton("Register");
        buttonPanel.add(backBtn); buttonPanel.add(registerBtn);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        backBtn.addActionListener(e -> { dispose(); new LoginScreen(libraryService).setVisible(true); });
        registerBtn.addActionListener(e -> handleRegistration());

        setContentPane(mainPanel); setVisible(true);
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
}
