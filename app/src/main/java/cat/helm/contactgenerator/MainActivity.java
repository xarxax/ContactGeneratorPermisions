package cat.helm.contactgenerator;

import android.Manifest;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Data;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_WRITE_CONTACTS = 123;
    @Bind(R.id.fab)
    FloatingActionButton fab;
    @Bind(R.id.toolbar) Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
    }

    @OnClick(R.id.fab) public void createContacts(View view) {
        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_CONTACTS)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_CONTACTS},
                        MY_PERMISSIONS_REQUEST_WRITE_CONTACTS);

                // MY_PERMISSIONS_REQUEST_WRITE_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }

        } else {
            generateContacts();
        }
    }

    private void generateContacts() {
        toolbar.setTitle("Loading ");
        for (int i = 2000; i < 20000; i++) {
            writePhoneContact("La la la  " + i, "+34 555 555 " + i, getApplicationContext());
            toolbar.setTitle("Loading " + i);
        }

        toolbar.setTitle("Done");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    generateContacts();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void writePhoneContact(String displayName, String number, Context cntx /*App or Activity Ctx*/)
    {
        Context contetx 	= cntx; //Application's context or Activity's context
        String strDisplayName 	=  displayName; // Name of the Person to add
        String strNumber 	=  number; //number of the person to add with the Contact

        ArrayList<ContentProviderOperation> cntProOper = new ArrayList<ContentProviderOperation>();
        int contactIndex = cntProOper.size();//ContactSize

        //Newly Inserted contact
        // A raw contact will be inserted ContactsContract.RawContacts table in contacts database.
        cntProOper.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)//Step1
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null).build());

        //Display name will be inserted in ContactsContract.Data table
        cntProOper.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)//Step2
                .withValueBackReference(Data.RAW_CONTACT_ID,contactIndex)
                .withValue(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE)
                .withValue(StructuredName.DISPLAY_NAME, strDisplayName) // Name of the contact
                .build());
        //Mobile number will be inserted in ContactsContract.Data table
        cntProOper.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)//Step 3
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,contactIndex)
                .withValue(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
                .withValue(Phone.NUMBER, strNumber) // Number to be added
                .withValue(Phone.TYPE, Phone.TYPE_MOBILE).build()); //Type like HOME, MOBILE etc
        try
        {
            // We will do batch operation to insert all above data
            //Contains the output of the app of a ContentProviderOperation.
            //It is sure to have exactly one of uri or count set
            ContentProviderResult[] contentProresult = null;
            contentProresult = contetx.getContentResolver().applyBatch(ContactsContract.AUTHORITY, cntProOper); //apply above data insertion into contacts list
        }
        catch (RemoteException exp)
        {
            //logs;
        }
        catch (OperationApplicationException exp)
        {
            //logs
        }
    }
}
