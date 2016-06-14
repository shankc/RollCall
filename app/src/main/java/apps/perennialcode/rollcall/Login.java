package apps.perennialcode.rollcall;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.LinkedHashMap;

import apps.perennialcode.rollcall.Tools.config;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
public class Login extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener  {

public static LinkedHashMap<String,String > UserData;
    GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 9001;
    private String  Email;
    Button Try_Again;
    Boolean isConnected;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        UserData = new LinkedHashMap<>();

        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
         isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setScopes(gso.getScopeArray());
        Try_Again= (Button) findViewById(R.id.tryagain_button);
   Try_Again.setVisibility(View.GONE);
        findViewById(R.id.sign_in_button).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(isConnected) {


            switch (v.getId()) {
                case R.id.sign_in_button:
                    signIn();
                    break;
            }
        }
        else
        {
            Toast.makeText(Login.this,"Please Check Network Connection ",Toast.LENGTH_SHORT).show();
        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            handleSignInResult(result);
        }
    }
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d("Login", "handleSignInResult:" + result.isSuccess()+" "+result);

        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            final GoogleSignInAccount acct = result.getSignInAccount();
            Email = acct.getEmail();
          {
                new GetUserDetails() {
                    @Override
                    protected void onPostExecute(Boolean result) {
                        super.onPostExecute(result);
                         {
                            if (result) {
                                Intent intent = new Intent(Login.this, Main2Activity.class);
                                intent.putExtra("Email", acct.getEmail());
                                startActivity(intent);
                            } else {

                                Toast.makeText(Login.this, "You Are Not Authorized! or there was an error!", Toast.LENGTH_LONG).show();
                                Try_Again.setVisibility(View.VISIBLE);
                                Try_Again.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        signIn();
                                    }
                                });
                                //signIn();
                            }
                        }

                    }

                }.execute();

                updateUI(true);
            }


        }
                else{
                    // Signed out, show unauthenticated UI.

                    updateUI(false);
                }


    }
    private void updateUI(boolean signedIn) {
        if (signedIn) {
            findViewById(R.id.sign_in_button).setVisibility(View.GONE);

        } else {


            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);

        }
    }
@Override
    protected void onStart(){
    super.onStart();
    OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
    if (opr.isDone()) {
        // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
        // and the GoogleSignInResult will be available instantly.
        Log.d("Login", "Got cached sign-in");
        GoogleSignInResult result = opr.get();
        handleSignInResult(result);
    } else {
        // If the user has not previously signed in on this device or the sign-in has expired,
        // this asynchronous branch will attempt to sign in the user silently.  Cross-device
        // single sign-on will occur in this branch.

        opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
            @Override
            public void onResult(GoogleSignInResult googleSignInResult) {

                Log.d("Login","the result "+googleSignInResult);
                handleSignInResult(googleSignInResult);
            }
        });
    }

}
    class GetUserDetails extends AsyncTask<Void,Void,Boolean>{

        @Override
        protected Boolean doInBackground(Void... params) {
            config c = new config();
            OkHttpClient client = new OkHttpClient();
            String URL = c.getURL()+"/api/login/getuserdetails?EmailId="+Email;
            Request request = new Request.Builder().url(URL).build();
            SharedPreferences datastore= getSharedPreferences("DataStorage", 0);
            SharedPreferences.Editor editor=datastore.edit();
            Response response =null;
            try{
                response = client.newCall(request).execute();
                String content = response.body().string();
                Log.d("DailyActivity"," the content is "+content);
                JSONObject object= new JSONObject(content);
                Log.d("DailyActivity", "the JSON array is " + object);
                 DataStorage data = new DataStorage();
                boolean role;

                editor.putString("SuperId", Integer.toString(object.getInt("SuperId")));
                editor.putString("OrgName",object.getString("OrgName"));
                editor.putString("RoleId",Integer.toString(object.getInt("RoleId")));
                editor.putString("UserName",object.getString("UserName"));
                editor.putString("EmailId", object.getString("EmailId"));
                editor.putString("RegistrationId", Integer.toString(object.getInt("RegistrationId")));
                editor.commit();
                if(object.getInt("RoleId")==0){
                    role= false;
                }
                else
                {
                    role= true;
                }

                return role;

            }
            catch(IOException | JSONException e){
                e.printStackTrace();
            }

return false;
        }
    }

}
