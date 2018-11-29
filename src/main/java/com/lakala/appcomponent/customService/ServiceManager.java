package com.lakala.appcomponent.customService;

import android.app.Activity;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;

public class ServiceManager {
    /**
     * 加载html界面
     *
     * @param activity 活动
     * @param loadUrl  界面url
     */
    public static void loadHtml(Activity activity, Builder builder, String loadUrl) {
        Intent intent = new Intent(activity, WebViewActivity.class);
        intent.putExtra("data", builder);
        intent.putExtra(WebViewActivity.URL_KEY, loadUrl);
        activity.startActivity(intent);
    }

    public static class Builder implements Parcelable {
        public String title;
        public int titleColor;
        public int titleSize;
        public int icon;
        public int toolBarColor;
        public int toolBarVisible;

        public Builder() {
        }

        protected Builder(Parcel in) {
            title = in.readString();
            titleColor = in.readInt();
            titleSize = in.readInt();
            icon = in.readInt();
            toolBarColor = in.readInt();
            toolBarVisible = in.readInt();
        }

        public static final Creator<Builder> CREATOR = new Creator<Builder>() {
            @Override
            public Builder createFromParcel(Parcel in) {
                return new Builder(in);
            }

            @Override
            public Builder[] newArray(int size) {
                return new Builder[size];
            }
        };

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder titleColor(int titleColor) {
            this.titleColor = titleColor;
            return this;
        }

        public Builder titleSize(int titleSize) {
            this.titleSize = titleSize;
            return this;
        }

        public Builder icon(int icon) {
            this.icon = icon;
            return this;
        }

        public Builder toolBarColor(int toolBarColor) {
            this.toolBarColor = toolBarColor;
            return this;
        }

        public Builder toolBarVisible(int toolBarVisible) {
            this.toolBarVisible = toolBarVisible;
            return this;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(title);
            dest.writeInt(titleColor);
            dest.writeInt(titleSize);
            dest.writeInt(icon);
            dest.writeInt(toolBarColor);
            dest.writeInt(toolBarVisible);
        }
    }

}
