package com.shopby.dhakkan.listener;

import com.shopby.dhakkan.model.UserModel;

/**
 * Created by Ashiq on 5/30/16.
 */
public interface RegistrationListener {
     void onValidationComplete(UserModel userModel);
}
