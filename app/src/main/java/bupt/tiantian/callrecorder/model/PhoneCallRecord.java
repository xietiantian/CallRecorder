package bupt.tiantian.callrecorder.model;

import android.Manifest;
import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;

import bupt.tiantian.callrecorder.database.CallLog;

import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Info about a phone call recording
 */
public class PhoneCallRecord {

    // Cache of Contact Pictures to minimize image memory use...
    private static Map<String, Drawable> synchronizedMap = Collections.synchronizedMap(new HashMap<String, Drawable>());
    private String name;
    private String contactId;
    CallLog phoneCall;

    public PhoneCallRecord(CallLog phoneCall) {
        this.phoneCall = phoneCall;
    }

    public void setImage(Drawable photo) {
        synchronizedMap.put(phoneCall.getPhoneNumber(), photo);
    }

    /**
     * Get the Contact image from the cache...
     *
     * @return NULL if there isn't an Image in the cache
     */
    public Drawable getImage() {
        Drawable drawable = synchronizedMap.get(phoneCall.getPhoneNumber());
        return drawable;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public String getContactId() {
        return contactId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        if (null == name) {
            return phoneCall.getPhoneNumber();
        }
        return name;
    }

    public CallLog getPhoneCall() {
        return phoneCall;
    }

    public void resolveContactInfo(Context context) {
        String name = null;
        String contactId = null;
        InputStream input = null;
        String phoneNumber = phoneCall.getPhoneNumber();
        if (null == phoneNumber || phoneNumber.isEmpty()) return;
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED)
            return;

        try {
            // define the columns the query should return
            String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID};
            // encode the phone number and build the filter URI
            Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
            // query time
            Cursor cursor = context.getContentResolver().query(contactUri, projection, null, null, null);

            if (cursor.moveToFirst()) {
                // Get values from contacts database:
                contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup._ID));
                setContactId(contactId);
                name = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
                setName(name);

                // Already in the cache?
                if (null == getImage()) { // no...
                    // Get photo of contactId as input stream:
                    Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.parseLong(contactId));
                    input = ContactsContract.Contacts.openContactPhotoInputStream(context.getContentResolver(), uri);
                    if (null != input) {
                        BitmapDrawable drawable = new BitmapDrawable(context.getResources(), input);
                        setImage(drawable);
                    }
                }
            }
        } catch (Exception e) {

        }
    }
}
