package com.upc.appcentroidiomas.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.upc.appcentroidiomas.data.model.LoggedInUser;

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
public class LoginRepository {

    private static volatile LoginRepository instance;
    private Context context;

    private LoginDataSource dataSource;

    // If user credentials will be cached in local storage, it is recommended it be encrypted
    // @see https://developer.android.com/training/articles/keystore
    private LoggedInUser user = null;
    private SharedPreferences Loginprefs;

    // private constructor : singleton access
    private LoginRepository(LoginDataSource dataSource, Context context)  {
        this.dataSource = dataSource;
        this.context = context;

        Loginprefs = context.getSharedPreferences("logindetail", 0);
    }

    public static LoginRepository getInstance(LoginDataSource dataSource, Context context) {
        if (instance == null) {
            instance = new LoginRepository(dataSource, context);
            //activity = new MainActivity();
        }
        return instance;
    }

    public boolean isLoggedIn() {
        //return user != null;
        //Loginprefs = activity.getApplicationContext().getSharedPreferences("logindetail", 0);

        String userLoginStatus = Loginprefs.getString("userLoginStatus", null);
        if (userLoginStatus == null){
            return false;
        }
        return userLoginStatus.toString().equals("yes");
    }

    public void logout() {
        user = null;
        //dataSource.logout();
        //prefs = getSharedPreferences("logindetail", 0);
        SharedPreferences.Editor edit = Loginprefs.edit();
        edit.clear();
        edit.apply();
    }

    private void setLoggedInUser(LoggedInUser user) {
        this.user = user;

        //prefs = getSharedPreferences("logindetail", 0);
        SharedPreferences.Editor edit = Loginprefs.edit();
        edit.putString("userId", Integer.toString(user.getUserId()));
        edit.putString("displayName", user.getDisplayName());
        edit.putString("userLoginStatus", "yes");
        edit.apply();
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }

    public LoggedInUser getLoggedUser() {
        String userId = Loginprefs.getString("userId", null);
        String displayName = Loginprefs.getString("displayName", null);

        if (userId == null){
            this.user = new LoggedInUser(0, displayName);
        }else{
            this.user = new LoggedInUser(Integer.parseInt(userId), displayName);
        }

        return this.user;
    }

    public Result<LoggedInUser> login(String username, String password) {
        // handle login
        Result<LoggedInUser> result = dataSource.login(username, password);
        if (result instanceof Result.Success) {
            setLoggedInUser(((Result.Success<LoggedInUser>) result).getData());
        }
        return result;
    }

    public void forceLogin(LoggedInUser loggedInUser) {
        setLoggedInUser(loggedInUser);
    }
}