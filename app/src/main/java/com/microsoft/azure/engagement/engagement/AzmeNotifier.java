// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package com.microsoft.azure.engagement.engagement;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.BigPictureStyle;
import android.support.v4.app.NotificationCompat.BigTextStyle;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.microsoft.azure.engagement.R;
import com.microsoft.azure.engagement.reach.EngagementAbstractAnnouncement;
import com.microsoft.azure.engagement.reach.EngagementDefaultNotifier;
import com.microsoft.azure.engagement.reach.EngagementReachInteractiveContent;
import com.microsoft.azure.engagement.reach.v11.EngagementNotificationUtilsV11;

public final class AzmeNotifier
        extends EngagementDefaultNotifier {

    private static final String IN_APP_POP_UP_CATEGORY = "POP-UP";
    private static final String INTERSTITIAL_CATEGORY = "INTERSTITIAL";

    public AzmeNotifier(Context context) {
        super(context);
    }

    @Override
    protected int getOverlayLayoutId(String category) {
        return R.layout.engagement_notification_overlay;
    }

    @Override
    public Integer getOverlayViewId(String category) {
        return R.id.engagement_notification_overlay;
    }

    @Override
    public Integer getInAppAreaId(String category) {
        return R.id.engagement_notification_area;
    }

    @Override
    public Boolean handleNotification(EngagementReachInteractiveContent content)
            throws RuntimeException {
        return super.handleNotification(content);
    }

    @Override
    protected void prepareInAppArea(final EngagementReachInteractiveContent content, final View notifAreaView)
            throws RuntimeException {
        // Checks whether it is an in-app notifications with POP-UP category.
        if (content.isSystemNotification() == false && AzmeNotifier.IN_APP_POP_UP_CATEGORY.equals(content.getCategory()) == true) {
            // We create an in-app notifications with POP-UP category in order to display a dialog.

            // Gets the context from the view in order to display a dialog
            final Context context = notifAreaView.getContext();
            final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
            dialogBuilder.setTitle(content.getNotificationTitle());
            dialogBuilder.setMessage(content.getNotificationMessage());
            dialogBuilder.setCancelable(false);
            // Sets the positive action to perform the Engagement 'open notification' action.
            dialogBuilder.setPositiveButton(R.string.positive_button, new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    content.actionNotification(context, true);
                }
            });
            if (content.isNotificationCloseable() == true) {
                // Sets the negative action to perform the Engagement 'exit/cancel' action.
                dialogBuilder.setNegativeButton(R.string.negative_button, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        content.exitNotification(context);
                    }
                });
            }
            notifAreaView.setVisibility(View.GONE);
            dialogBuilder.show();
        }
        // Checks whether it is an in-app notifications with INTERSTITIAL category.
        else if (content.isSystemNotification() == false && AzmeNotifier.INTERSTITIAL_CATEGORY.equals(content.getCategory()) == true) {
            // We display directly the notification content
            notifAreaView.setVisibility(View.GONE);
            content.actionNotification(mContext, true);
        } else {
            // We show the default in-app area.
            super.prepareInAppArea(content, notifAreaView);
        }
    }

    @Override
    protected boolean onNotificationPrepared(Notification notification, EngagementReachInteractiveContent content)
            throws RuntimeException {

        if (content.isSystemNotification() == true) {
            // Read http://developer.android.com/guide/topics/ui/notifiers/notifications.html
            final NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mContext);

            // "Large Icon" : the left icon
            if (content.getNotificationImage() != null) {
                notificationBuilder.setLargeIcon(content.getNotificationImage());
            }
            // "Small Icon": the small icon on the bottom right
            notificationBuilder.setSmallIcon(R.drawable.ic_notification_default);

            // "Content Title": the legacy notification title, i.e. the text on the top
            notificationBuilder.setContentTitle(content.getNotificationTitle());
            // "Content Text": the legacy notification text, i.e. the text on the bottom
            notificationBuilder.setContentText(content.getNotificationMessage());

            // The ticker text
            notificationBuilder.setTicker(notification.tickerText);

            // The notification settings
            notificationBuilder.setDefaults(notification.defaults);
            if (content.isNotificationCloseable() == false) {
                notificationBuilder.setAutoCancel(false);
            }
            notificationBuilder.setContent(notification.contentView);
            notificationBuilder.setContentIntent(notification.contentIntent);
            notificationBuilder.setDeleteIntent(notification.deleteIntent);
            notificationBuilder.setWhen(notification.when);

            if (content.getNotificationBigText() != null) {
                final BigTextStyle bigTextStyle = new BigTextStyle();
                bigTextStyle.setBigContentTitle(content.getNotificationTitle());
                bigTextStyle.setSummaryText(content.getNotificationMessage());
                bigTextStyle.bigText(content.getNotificationBigText());
                notificationBuilder.setStyle(bigTextStyle);
            } else if (content.getNotificationBigPicture() != null) {
                final BigPictureStyle bigPictureStyle = new BigPictureStyle();
                final Bitmap bitmap = EngagementNotificationUtilsV11.getBigPicture(this.mContext, content.getDownloadId().longValue());
                bigPictureStyle.bigPicture(bitmap);
                bigPictureStyle.setSummaryText(content.getNotificationMessage());
                notificationBuilder.setStyle(bigPictureStyle);
            }

            // Retrieves the actionURL from the content
            final String actionURL;
            if (content instanceof EngagementAbstractAnnouncement == true) {
                actionURL = ((EngagementAbstractAnnouncement) content).getActionURL();
            } else {
                // We are receiving a poll notification
                actionURL = null;
            }

            if (actionURL != null) {
                final Uri actionUri = Uri.parse(actionURL);

                // Retrieves the specify parameters from the actionURL
                final boolean displayShareButton = actionUri.getBooleanQueryParameter("displayShareButton", false);
                final boolean displayFeedbackButton = actionUri.getBooleanQueryParameter("displayFeedbackButton", false);

                if (displayFeedbackButton == true) {
                    final Intent feedbackIntent = new Intent();
                    feedbackIntent.setAction(Intent.ACTION_VIEW);
                    final Uri data = Uri.parse("mailto:");
                    feedbackIntent.putExtra(Intent.EXTRA_SUBJECT, mContext.getString(R.string.notification_feedback_email_subject));
                    feedbackIntent.setData(data);
                    final PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, feedbackIntent, 0);

                    notificationBuilder.addAction(R.drawable.ic_out_of_app_send_feedback, mContext.getString(R.string.notification_feedback_button_title), pendingIntent);
                }

                if (displayShareButton == true) {
                    final Intent sharingIntent = new Intent();
                    sharingIntent.setAction(Intent.ACTION_SEND);
                    sharingIntent.putExtra(Intent.EXTRA_TEXT, mContext.getString(R.string.notification_share_message));
                    sharingIntent.setType("text/plain");
                    final PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, sharingIntent, 0);

                    notificationBuilder.addAction(R.drawable.ic_out_of_app_share, mContext.getString(R.string.notification_share_button_title), pendingIntent);
                }

            }

      /* Dismiss option can be managed only after build */
            final Notification finalNotification = notificationBuilder.build();
            if (content.isNotificationCloseable() == false) {
                finalNotification.flags |= Notification.FLAG_NO_CLEAR;
            }

      /* Notify here instead of super class */
            final NotificationManager manager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(getNotificationId(content), finalNotification); // notice the call to get the right identifier

      /* Return false, we notify ourselves */
            return false;
        } else {
            return super.onNotificationPrepared(notification, content);
        }
    }
}