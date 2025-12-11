package com.library.model;

//Represents a user in the system (Admin or regular user)

public class UserAccount {
    private String id;
    private String name;
    private String email;
    private String phone;
    private String password;
    private String role; // "Admin" or "User"
    private double fineBalance = 0.0;

    public UserAccount(String id, String name, String email, String phone, String password, String role){
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.role = role;
    }

    public String getId(){ 
    	return id;
    	}
    
    public String getName(){ 
    	return name;
    	}
    
    public String getEmail(){ 
    	return email;
    	}
    
    public String getPhone(){ 
    	return phone;
    	}
    
    public String getPassword(){ 
    	return password;
    	}
    
    public String getRole(){ 
    	return role;
    	}
    
    public double getFineBalance(){ 
    	return fineBalance;
    	}
    
    public void addFine(double amount){ 
    	fineBalance += amount;
    	}
}
