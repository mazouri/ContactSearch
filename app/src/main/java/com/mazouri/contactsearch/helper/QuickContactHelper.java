package com.mazouri.contactsearch.helper;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.net.Uri;
import android.provider.ContactsContract;
import android.widget.QuickContactBadge;

import java.lang.reflect.Field;

/**
 * Created by wangdongdong on 17-4-12.
 */

public class QuickContactHelper {

    private static final String[] PHOTO_ID_PROJECTION = new String[] {
            ContactsContract.Contacts.PHOTO_ID
    };

    private static final String[] PHOTO_BITMAP_PROJECTION = new String[] {
            ContactsContract.CommonDataKinds.Photo.PHOTO
    };

    public static void addThumbnail(Context context, QuickContactBadge badge, String phoneNumber, boolean useCircleBitmap) {
        final Integer thumbnailId = fetchThumbnailId(context, phoneNumber);
        if (thumbnailId != null) {
            final Bitmap thumbnail = fetchThumbnail(context, thumbnailId);
            if (thumbnail != null) {
                if (useCircleBitmap) {
                    badge.setImageBitmap(frameBitmapInCircle(thumbnail));
                } else {
                    badge.setImageBitmap(thumbnail);
                }
            }
        }

    }

    private static Integer fetchThumbnailId(Context context, String phoneNumber) {

        final Uri uri = Uri.withAppendedPath(ContactsContract.CommonDataKinds.Phone.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        final Cursor cursor = context.getContentResolver().query(uri, PHOTO_ID_PROJECTION, null, null, ContactsContract.Contacts.DISPLAY_NAME + " ASC");

        try {
            Integer thumbnailId = null;
            if (cursor.moveToFirst()) {
                thumbnailId = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_ID));
            }
            return thumbnailId;
        }
        finally {
            cursor.close();
        }

    }

    private static Bitmap fetchThumbnail(Context context, int thumbnailId) {

        final Uri uri = ContentUris.withAppendedId(ContactsContract.Data.CONTENT_URI, thumbnailId);
        final Cursor cursor = context.getContentResolver().query(uri, PHOTO_BITMAP_PROJECTION, null, null, null);

        try {
            Bitmap thumbnail = null;
            if (cursor.moveToFirst()) {
                final byte[] thumbnailBytes = cursor.getBlob(0);
                if (thumbnailBytes != null) {
                    thumbnail = BitmapFactory.decodeByteArray(thumbnailBytes, 0, thumbnailBytes.length);
                }
            }
            return thumbnail;
        }
        finally {
            cursor.close();
        }
    }

    public static Bitmap frameBitmapInCircle(Bitmap input) {
        if (input == null) {
            return null;
        }

        // Crop the image if not squared.
        int inputWidth = input.getWidth();
        int inputHeight = input.getHeight();
        int targetX, targetY, targetSize;
        if (inputWidth >= inputHeight) {
            targetX = inputWidth / 2 - inputHeight / 2;
            targetY = 0;
            targetSize = inputHeight;
        } else {
            targetX = 0;
            targetY = inputHeight / 2 - inputWidth / 2;
            targetSize = inputWidth;
        }

        // Create an output bitmap and a canvas to draw on it.
        Bitmap output = Bitmap.createBitmap(targetSize, targetSize, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        // Create a black paint to draw the mask.
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);

        // Draw a circle.
        canvas.drawCircle(targetSize / 2, targetSize / 2, targetSize / 2, paint);

        // Replace the black parts of the mask with the input image.
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(input, targetX /* left */, targetY /* top */, paint);

        return output;
    }

    /**
     * 去掉三角
     */
    public static void removeOverlay(QuickContactBadge badge) {
        try {
            Field f = badge.getClass().getDeclaredField("mOverlay");
            f.setAccessible(true);
            f.set(badge,null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
