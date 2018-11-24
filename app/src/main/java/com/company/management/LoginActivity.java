package com.company.management;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.company.management.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via phone/password.
 */

/**
 * this page just do tasks as follows:
 * 1. input account & password
 * 2. check out account & password
 * 3. save the login status
 * 4. change to basic functional display page.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {
    //Id to identity READ_CONTACTS permission request.
    private static final int REQUEST_READ_CONTACTS = 0;
    //A dummy authentication store containing known user names and passwords.
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };

    private GetVerifyTask mVerifyTask = null;

    // UI references.
    private EditText mPhoneView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mCardFormView;
    private View mResetView;
    private View mLoginView;
    private EditText RPhoneView;
    private EditText RPasswordView;
    private EditText RPassAgainView;
    private EditText RVerifyView;

    private Button SignInButton;
    private Button LoginButton;
    private Button VerifyCodeBtton;
    private TextView mForget;
    private TextView mRegister;

    private MyApp myApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        myApp = (MyApp)getApplication();
        mPhoneView = (EditText) findViewById(R.id.phone);
        mPasswordView = (EditText) findViewById(R.id.password);
        SignInButton = (Button) findViewById(R.id.sign_in_button);
        mCardFormView = findViewById(R.id.card_form);
        mProgressView = findViewById(R.id.login_progress);
        mResetView = findViewById(R.id.reset_form);
        mLoginView = findViewById(R.id.login_form);
        mForget = (TextView) findViewById(R.id.forget);
        mRegister = (TextView) findViewById(R.id.register);
        RPhoneView = (EditText) findViewById(R.id.Rphone);
        RPasswordView = (EditText) findViewById(R.id.Rpassword);
        RPassAgainView = (EditText) findViewById(R.id.Rpassword_again);
        RVerifyView = (EditText) findViewById(R.id.Rverification);
        LoginButton = (Button) findViewById(R.id.Rlogin_button);
        VerifyCodeBtton = (Button) findViewById(R.id.verification_button);

        //sign in button
        SignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        //Verification code
        VerifyCodeBtton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                getVerifyCode();
                Toast.makeText(LoginActivity.this,"验证码已发送",Toast.LENGTH_LONG).show();
            }
        });

        //Login in
        LoginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptReset();
            }
        });

        //press enter
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        //foget password
        mForget.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showReset(true);
            }
        });

        //register user
        mRegister.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mPhoneView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {

        // Reset errors.
        mPhoneView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String account = mPhoneView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;
        /* TODO: 此处后端有数据之后需要删除注释
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }
        if (TextUtils.isEmpty(account)) {
            mPhoneView.setError(getString(R.string.error_field_required));
            focusView = mPhoneView;
            cancel = true;
        } else if (!isPhoneValid(account)) {
            mPhoneView.setError(getString(R.string.error_invalid_phone));
            focusView = mPhoneView;
            cancel = true;
        }
        */
        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            Login(account, password);
        }
    }

    private void attemptReset() {
        // Reset errors.
        RPhoneView.setError(null);
        RPasswordView.setError(null);
        RPassAgainView.setError(null);

        // Store values at the time of the login attempt.
        String phone = RPhoneView.getText().toString();
        String password = RPasswordView.getText().toString();
        String passAgain = RPassAgainView.getText().toString();
        String verifycode = RVerifyView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        /*
        if (TextUtils.isEmpty(verifycode)) {
            RVerifyView.setError(getString(R.string.error_field_required));
            focusView = RVerifyView;
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
        if(!password.equals(passAgain)) {
            RPassAgainView.setError(getString(R.string.error_different_password));
            focusView = RPassAgainView;
            cancel = true;
        }
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            RPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = RPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(phone)) {
            RPhoneView.setError(getString(R.string.error_field_required));
            focusView = RPhoneView;
            cancel = true;
        } else if (!isPhoneValid(phone)) {
            RPhoneView.setError(getString(R.string.error_invalid_phone));
            focusView = RPhoneView;
            cancel = true;
        }
        */
        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            Reset(phone, password, verifycode);
        }
    }

    private void getVerifyCode() {
        if(mVerifyTask != null) {
            return;
        }
        RPhoneView.setError(null);
        String phone = RPhoneView.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            RPhoneView.setError(getString(R.string.error_field_required));
            RPhoneView.requestFocus();
        } else if (!isPhoneValid(phone)) {
            RPhoneView.setError(getString(R.string.error_invalid_phone));
            RPhoneView.requestFocus();
        } else {
            mVerifyTask = new GetVerifyTask(phone);
            mVerifyTask.execute((Void) null);
        }
    }

    private boolean isPhoneValid(String phone) {
        return phone.length() == 11;
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 5;
    }


    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mCardFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mCardFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mCardFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mCardFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void showReset(boolean show) {
        if(show) {
            mResetView.setVisibility(View.VISIBLE);
            mLoginView.setVisibility(View.GONE);
        } else {
            mResetView.setVisibility(View.GONE);
            mLoginView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }
        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        //mPhoneView.setAdapter(adapter);
    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };
        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    public void Login(String username, String password){
        /**
         * 1. 成功登录之后保存用户登录状态
         */
        UserWR userWR = new UserWR();
        userWR.saveUserLogin(username, true, getApplicationContext());
        /**
         * 2. 跳转到功能页
         */
        Intent intent = new Intent(LoginActivity.this,BottomNavigation.class);
        startActivity(intent);
    }
    public void Reset(String phone, String password, String verifycode){
        Intent intent = new Intent();
        intent.setClass(LoginActivity.this,BottomNavigation.class);
        startActivity(intent);
    }

    public class GetVerifyTask extends AsyncTask<Void, Void, Boolean> {
        private final String mPhone;

        GetVerifyTask(String phone) {
            mPhone=phone;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                JSONObject json = new JSONObject();
                json.put("account",mPhone);
                JSONObject obj = Conn.doJsonPost("/users/registerVerifycode",json);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            mVerifyTask = null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            mVerifyTask = null;
        }
    }
}

