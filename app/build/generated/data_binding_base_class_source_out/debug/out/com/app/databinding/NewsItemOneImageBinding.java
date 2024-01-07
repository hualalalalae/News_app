// Generated by view binder compiler. Do not edit!
package com.app.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.app.R;
import com.makeramen.roundedimageview.RoundedImageView;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class NewsItemOneImageBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final TextView newsDesc;

  @NonNull
  public final RoundedImageView newsImage;

  @NonNull
  public final TextView newsTitle;

  private NewsItemOneImageBinding(@NonNull LinearLayout rootView, @NonNull TextView newsDesc,
      @NonNull RoundedImageView newsImage, @NonNull TextView newsTitle) {
    this.rootView = rootView;
    this.newsDesc = newsDesc;
    this.newsImage = newsImage;
    this.newsTitle = newsTitle;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static NewsItemOneImageBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static NewsItemOneImageBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.news_item_one_image, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static NewsItemOneImageBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.news_desc;
      TextView newsDesc = ViewBindings.findChildViewById(rootView, id);
      if (newsDesc == null) {
        break missingId;
      }

      id = R.id.news_image;
      RoundedImageView newsImage = ViewBindings.findChildViewById(rootView, id);
      if (newsImage == null) {
        break missingId;
      }

      id = R.id.news_title;
      TextView newsTitle = ViewBindings.findChildViewById(rootView, id);
      if (newsTitle == null) {
        break missingId;
      }

      return new NewsItemOneImageBinding((LinearLayout) rootView, newsDesc, newsImage, newsTitle);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
