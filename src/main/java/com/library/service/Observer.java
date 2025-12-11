package com.library.service;

import com.library.model.UserAccount;

public interface Observer {
    void notify(UserAccount user, String message);
}
