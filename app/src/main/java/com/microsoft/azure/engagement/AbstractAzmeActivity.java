// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package com.microsoft.azure.engagement;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.microsoft.azure.engagement.reach.EngagementAnnouncement;
import com.microsoft.azure.engagement.reach.EngagementReachAgent;

public abstract class AbstractAzmeActivity
    extends AppCompatActivity
    implements OnClickListener
{

  private View engagementNotificationOverlay;

  private View engagementNotificationArea;

  private TextView engagementNotificationTitleTextView;

  private TextView engagementNotificationMessageTextView;

  private View engagementNotificationCloseImageButton;

  /**
   * Abstract method
   *
   * @return The activity layout resource id
   */
  protected abstract int getLayoutResourceId();

  @Override
  protected void onCreate(final Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(getLayoutResourceId());

    engagementNotificationOverlay = findViewById(R.id.engagement_notification_overlay);
    engagementNotificationArea = findViewById(R.id.engagement_notification_area);
    engagementNotificationTitleTextView = (TextView) findViewById(R.id.engagement_notification_title);
    engagementNotificationMessageTextView = (TextView) findViewById(R.id.engagement_notification_message);
    engagementNotificationCloseImageButton = findViewById(R.id.engagement_notification_close);

    engagementNotificationCloseImageButton.setOnClickListener(this);
  }

  @Override
  protected void onPostCreate(Bundle savedInstanceState)
  {
    if (EngagementAgentUtils.isInDedicatedEngagementProcess(this))
    {
      return;
    }
    super.onPostCreate(savedInstanceState);
  }

  @Override
  protected void onResume()
  {
    super.onResume();
    final String activityNameOnEngagement = EngagementAgentUtils.buildEngagementActivityName(getClass());
    EngagementAgent.getInstance(this).startActivity(this, activityNameOnEngagement, null);

    final EngagementAnnouncement engagementAnnouncement = EngagementReachAgent.getInstance(this).getContent(getIntent());
    if (engagementAnnouncement != null)
    {
      engagementAnnouncement.exitContent(this);
    }
  }

  @Override
  protected void onPause()
  {
    super.onPause();
    EngagementAgent.getInstance(this).endActivity();
  }

  @Override
  protected void onStop()
  {
    super.onStop();
    EngagementAgent.getInstance(this).endActivity();
  }

  @Override
  protected void onDestroy()
  {
    super.onDestroy();
    EngagementAgent.getInstance(this).endActivity();
  }

  @Override
  public void onClick(View view)
  {
    if (view == engagementNotificationCloseImageButton)
    {
      hideNotificationOverlay();
    }
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    // Handle item selection
    switch (item.getItemId())
    {
    case android.R.id.home:
      finish();
      return true;
    default:
      return super.onOptionsItemSelected(item);
    }
  }

  /**
   * Method that shows the in-app notification area in a local mode
   *
   * @param title   The title to show
   * @param message The message to show
   * @param intent  The intent to start
   */
  public final void showInAppNotification(String title, String message, final Intent intent)
  {
    engagementNotificationTitleTextView.setText(title);
    engagementNotificationMessageTextView.setText(message);
    engagementNotificationArea.setOnClickListener(new OnClickListener()
    {
      @Override
      public void onClick(View v)
      {
        startActivity(intent);
        hideNotificationOverlay();
      }
    });
    engagementNotificationArea.setVisibility(View.VISIBLE);
    engagementNotificationOverlay.setVisibility(View.VISIBLE);
  }

  /**
   * Method that hides the notification overlay
   */
  final void hideNotificationOverlay()
  {
    if (inAppNotificationIsShown() == true)
    {
      engagementNotificationOverlay.setVisibility(View.GONE);
    }
  }

  /**
   * Method that returns true if an in-app is shown
   *
   * @return The visibility state of the notification overlay view
   */
  final boolean inAppNotificationIsShown()
  {
    return (engagementNotificationOverlay != null && engagementNotificationOverlay.getVisibility() == View.VISIBLE);
  }
}
