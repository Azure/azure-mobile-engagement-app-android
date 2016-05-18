// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package com.microsoft.azure.engagement.fragment;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.BigPictureStyle;
import android.support.v4.app.NotificationCompat.BigTextStyle;
import android.support.v4.app.NotificationCompat.Style;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AlertDialog.Builder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.microsoft.azure.engagement.HowToSendNotificationActivity;
import com.microsoft.azure.engagement.HowToSendNotificationActivity.NotificationType;
import com.microsoft.azure.engagement.MainActivity.NavigationProvider;
import com.microsoft.azure.engagement.R;
import com.microsoft.azure.engagement.ReboundActivity;
import com.microsoft.azure.engagement.engagement.AzmeTracker;

public final class OutOfAppPushNotificationsFragment
        extends Fragment
        implements OnClickListener, NavigationProvider {

    private static int NOTIFICATION_ID = 0;

    // all notification styles
    public enum NotificationStyle {
        normal, bigImage, bigText
    }

    private AlertDialog alertDialog;
    private View notificationOnlyButton;
    private View announcementButton;
    private View footer;
    private View iconTitleMessageButton;
    private View iconTitleMessageBigTextButton;
    private View iconTitleMessageBigImageButton;
    private boolean isForAnnouncement;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_out_of_app_push_notifications, container, false);

        notificationOnlyButton = view.findViewById(R.id.notificationOnlyButton);
        announcementButton = view.findViewById(R.id.announcementButton);
        footer = view.findViewById(R.id.footer);
        notificationOnlyButton.setOnClickListener(this);
        announcementButton.setOnClickListener(this);
        footer.setOnClickListener(this);

        AzmeTracker.startActivity(getActivity(), "notification_out_of_app");

        return view;
    }

    @Override
    public void onClick(View view) {
        if (view == notificationOnlyButton) {
            computeDialogDisplay(false);
        } else if (view == announcementButton) {
            computeDialogDisplay(true);
        } else if (view == iconTitleMessageButton) {
            final String actionUrl = isForAnnouncement == true ? getString(R.string.deeplink_rebound) : getString(R.string.deeplink_recent_product_updates);
            displayNotification(getActivity(), NotificationStyle.normal, actionUrl);
            alertDialog.dismiss();

            AzmeTracker.sendEvent(getActivity(), isForAnnouncement == true ? "announcement_icon_title_message" : "notification_only_icon_title_message");
        } else if (view == iconTitleMessageBigTextButton) {
            final String actionUrl = isForAnnouncement == true ? getString(R.string.deeplink_rebound) : getString(R.string.deeplink_recent_product_updates);
            displayNotification(getActivity(), NotificationStyle.bigText, actionUrl);
            alertDialog.dismiss();

            AzmeTracker.sendEvent(getActivity(), isForAnnouncement == true ? "announcement_icon_title_message_big_text" : "notification_only_icon_title_message_big_text");
        } else if (view == iconTitleMessageBigImageButton) {
            final String actionUrl = isForAnnouncement == true ? getString(R.string.deeplink_rebound) : getString(R.string.deeplink_recent_product_updates);
            displayNotification(getActivity(), NotificationStyle.bigImage, actionUrl);
            alertDialog.dismiss();

            AzmeTracker.sendEvent(getActivity(), isForAnnouncement == true ? "announcement_icon_title_message_big_image" : "notification_only_icon_title_message_big_image");
        } else if (view == footer) {
            final Intent intent = new Intent(getActivity(), HowToSendNotificationActivity.class);
            intent.putExtra(HowToSendNotificationActivity.NOTIFICATION_TYPE_EXTRA, NotificationType.outOfApp);
            startActivity(intent);
        }
    }

    /**
     * Methods that computes and displays the dialog content.
     *
     * @param isForAnnouncement boolean
     */
    private final void computeDialogDisplay(boolean isForAnnouncement) {
        final Builder builder = new Builder(getContext());
        builder.setTitle(R.string.out_of_app_push_notifications_dialog_title);
        final View dialogContainerView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_out_of_app_push_notifications, (ViewGroup) getView().getParent(), false);
        iconTitleMessageButton = dialogContainerView.findViewById(R.id.iconTitleMessageButton);
        iconTitleMessageBigTextButton = dialogContainerView.findViewById(R.id.iconTitleMessageBigTextButton);
        iconTitleMessageBigImageButton = dialogContainerView.findViewById(R.id.iconTitleMessageBigImageButton);

        this.isForAnnouncement = isForAnnouncement;
        iconTitleMessageButton.setOnClickListener(this);
        iconTitleMessageBigTextButton.setOnClickListener(this);
        iconTitleMessageBigImageButton.setOnClickListener(this);

        builder.setView(dialogContainerView);
        this.alertDialog = builder.create();
        this.alertDialog.show();

        AzmeTracker.sendEvent(getActivity(), isForAnnouncement ? "display_out_of_app_announcement" : "display_out_of_app_notification_only");
    }


    /**
     * Method that displays an out-of-app notification.
     *
     * @param context           The context
     * @param notificationStyle The notificationStyle
     * @param actionUrl         The actionUrl
     */
    private final void displayNotification(Context context, NotificationStyle notificationStyle, String actionUrl) {
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        final Resources resources = context.getResources();
        builder.setSmallIcon(R.drawable.ic_notification_default);
        builder.setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher));

        builder.setContentTitle(context.getString(R.string.out_of_app_push_notification_content_title));
        builder.setContentText(context.getString(R.string.out_of_app_push_notification_content_message));
        final Intent notificationIntent = new Intent(Intent.ACTION_VIEW);
        notificationIntent.putExtra(ReboundActivity.ACTION_URL_EXTRA, getString(R.string.deeplink_recent_product_updates));

        notificationIntent.setData(Uri.parse(actionUrl));
        final PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        final Style style;

        switch (notificationStyle) {
            case normal:
                style = null;
                builder.setContentTitle(context.getString(R.string.out_of_app_push_notification_content_title));
                builder.setContentText(context.getString(R.string.out_of_app_push_notification_content_message));
                break;
            case bigText:
                final BigTextStyle bigTextStyle = new BigTextStyle();
                bigTextStyle.setBigContentTitle(context.getString(R.string.out_of_app_push_notification_content_title));
                bigTextStyle.setSummaryText(context.getString(R.string.out_of_app_push_notification_content_message));
                bigTextStyle.bigText(context.getString(R.string.out_of_app_push_notification_content_big_text));
                builder.setContentTitle(context.getString(R.string.out_of_app_push_notification_content_title));
                builder.setContentText(context.getString(R.string.out_of_app_push_notification_content_message));
                style = bigTextStyle;
                break;
            case bigImage:
                final BigPictureStyle bigPictureStyle = new BigPictureStyle();
                bigPictureStyle.setBigContentTitle(context.getString(R.string.out_of_app_push_notification_content_title));
                bigPictureStyle.setSummaryText(context.getString(R.string.out_of_app_push_notification_content_message));
                bigPictureStyle.bigPicture(BitmapFactory.decodeResource(resources, R.drawable.ic_notification_big_image));
                bigPictureStyle.bigLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher));
                builder.setContentTitle(context.getString(R.string.out_of_app_push_notification_content_title));
                builder.setContentText(context.getString(R.string.out_of_app_push_notification_content_message));
                style = bigPictureStyle;
                break;
            default:
                style = null;
                break;
        }

        builder.setStyle(style);
        builder.setContentIntent(pendingIntent);

        // Share action
        final Intent sharingIntent = new Intent();
        sharingIntent.setAction(Intent.ACTION_SEND);
        sharingIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.notification_share_message));
        sharingIntent.setType("text/plain");
        final PendingIntent actionShareIntent = PendingIntent.getActivity(getActivity(), 0, sharingIntent, 0);
        builder.addAction(R.drawable.ic_out_of_app_share, getString(R.string.notification_share_button_title), actionShareIntent);

        // Feedback action
        final Intent feedbackIntent = new Intent();
        feedbackIntent.setAction(Intent.ACTION_VIEW);
        final Uri data = Uri.parse("mailto:");
        feedbackIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.notification_feedback_email_subject));
        feedbackIntent.setData(data);
        final PendingIntent actionFeedbackIntent = PendingIntent.getActivity(getActivity(), 0, feedbackIntent, 0);
        builder.addAction(R.drawable.ic_out_of_app_send_feedback, getString(R.string.notification_feedback_button_title), actionFeedbackIntent);

        final Notification notification = builder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        final NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(OutOfAppPushNotificationsFragment.NOTIFICATION_ID++, notification);

    }

    @Override
    public int getMenuIdentifier() {
        return R.id.menu_out_of_app;
    }

    @Override
    public int getTitleIdentifier() {
        return R.string.menu_out_of_app_title;
    }
}
